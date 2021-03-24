package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.conversion.DefaultMediaConversionService;
import de.hybris.platform.mediaconversion.conversion.MediaConversionException;
import de.hybris.platform.mediaconversion.enums.ConversionStatus;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import uk.ptr.cloudinary.dao.CloudinaryMediaConversionServiceDao;
import uk.ptr.cloudinary.strategies.CloudinaryConversionErrorLogStrategy;
import uk.ptr.cloudinary.strategies.CloudinaryMediaConversionStrategy;


public class DefaultCloudinaryMediaConversionService extends DefaultMediaConversionService
{
    private static final Logger LOG = Logger.getLogger(DefaultCloudinaryMediaConversionService.class);

    @Resource
    private CloudinaryMediaConversionServiceDao cloudinaryMediaConversionServiceDao;

    @Resource
    private  CloudinaryMediaConversionStrategy cloudinaryMediaConversionStrategy;

    @Resource
    private CloudinaryConversionErrorLogStrategy cloudinaryConversionErrorLogStrategy;


    private Collection<MediaFormatModel> getAllMediaFormats() {
        return cloudinaryMediaConversionServiceDao.getAllMediaFormats();
    }

    @Override
    public Collection<MediaModel> getConvertedMedias(MediaContainerModel container) {
        ServicesUtil.validateParameterNotNull(container, "Container must not be null.");
        return this.getModelService().isNew(container) ? this.selectDerivedMedias(container) : cloudinaryMediaConversionServiceDao.getConvertedMedias(container);
    }

    @Override
    public MediaModel getMaster(MediaContainerModel model) {
        ServicesUtil.validateParameterNotNull(model, "Container must not be null.");
        if (this.getModelService().isNew(model)) {
            return this.selectMasterFromUnsavedContainer(model);
        } else {
            try {
                return cloudinaryMediaConversionServiceDao.retrieveMaster(model);
            } catch (AmbiguousIdentifierException var2) {
                LOG.warn("Multiple medias which claim to be master detected in '" + model + "'.");
                return null;
            } catch (ModelNotFoundException var3) {
                LOG.debug("No master media found for container '" + model + "'.");
                return null;
            }
        }
    }


    @Override
    public ConversionStatus getConversionStatus(MediaContainerModel model) {
        ServicesUtil.validateParameterNotNull(model, "Container must not be null.");
        if (model.getMaster() == null) {
            return ConversionStatus.EMPTY;
        } else {
            Set<ConversionMediaFormatModel> todo = new HashSet((Collection)(model.getConversionGroup() == null ? this.getAllMediaFormats() : model.getConversionGroup().getSupportedMediaFormats()));
            if (todo.isEmpty()) {
                return ConversionStatus.CONVERTED;
            } else {
                int totalSize = todo.size();
                Collection<MediaModel> medias = this.getConvertedMedias(model);

                if(CollectionUtils.isNotEmpty(medias)) {
                    for (MediaModel media:medias) {
                        if (media.getMediaFormat() == null) {
                            LOG.warn("MediaFormat not set on media '" + media + "' in media container '" + model + "'.");
                        } else {
                            todo.remove(media.getMediaFormat());
                        }
                    }
                }

                return todo.isEmpty() ? ConversionStatus.CONVERTED : (totalSize == todo.size() ? ConversionStatus.UNCONVERTED : ConversionStatus.PARTIALLY_CONVERTED);
            }
        }
    }

    @Override
    public void convertMedias(MediaContainerModel container) {
        ServicesUtil.validateParameterNotNull(container, "Container must not be null.");
        Collection<MediaFormatModel> mediaFormats = ((Collection)(container.getConversionGroup() == null ? this.getAllMediaFormats() : container.getConversionGroup().getSupportedMediaFormats()));

        if(CollectionUtils.isNotEmpty(mediaFormats)) {
            for (MediaFormatModel format : mediaFormats) {
                try {
                    this.getOrConvert(container, format);
                } catch (ModelNotFoundException var5) {
                    LOG.error("Failed to convert media to format '" + format + "'.", var5);
                }
            }
        }

    }

    @Override
    public MediaModel getOrConvert(MediaContainerModel container, MediaFormatModel format) {
        ServicesUtil.validateParameterNotNull(container, "Container must not be null.");
        ServicesUtil.validateParameterNotNull(format, "Format must not be null.");
        ModelNotFoundException notFound = null;

        if (format instanceof MediaFormatModel) {
            try {
                return convertInTransaction(container, format);
            } catch (MediaConversionException var5) {
                throw new ModelNotFoundException("Failed to convert container '" + container.getQualifier() + " to format " + format.getQualifier() + ".", var5);
            }
        } else {
            throw notFound == null ? new ModelNotFoundException("Cannot convert to format '" + format + "'. Not a ConversionMediaFormat.") : notFound;
        }
    }

    private MediaModel convertInTransaction(final MediaContainerModel container, final MediaFormatModel format) throws MediaConversionException {
        try {
            return (MediaModel)Transaction.current().execute(new TransactionBody(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public Object execute() throws MediaConversionException {
                    Transaction.current().setTransactionIsolationLevel(2);
                    if (DefaultCloudinaryMediaConversionService.this.getLockRowInTransactionStrategy().lock((ItemModel)container)) {
                        return DefaultCloudinaryMediaConversionService.this.doConvert(container, format);
                    }
                    Object object = DefaultCloudinaryMediaConversionService.this.getModelService().getSource((Object)container);
                    synchronized (object) {
                        return DefaultCloudinaryMediaConversionService.this.doConvert(container, format);
                    }
                }
            });
        }
        catch (MediaConversionException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MediaConversionException("Transaction threw unknown exception.", e);
        }
    }


    protected MediaModel doConvert(MediaContainerModel container, MediaFormatModel format) throws MediaConversionException {
        MediaModel mediaModel;
        LOG.debug((Object)("Converting '" + container.getQualifier() + "' " + "to format '" + format.getQualifier() + "'."));
        MediaModel input = format.getInputMediaFormat() == null ? container.getMaster() : this.getOrConvert(container, (MediaFormatModel)format.getInputMediaFormat());
        if (input == null) {
            throw new MediaConversionException("Failed to retrieve input media for conversion.");
        }
        long startMillis = System.currentTimeMillis();
        try {
            MediaModel ret = this.convert(input, format);
            cloudinaryConversionErrorLogStrategy.reportConversionSuccess(container, format);
            mediaModel = ret;
        }
        catch (Throwable throwable) {
            try {
                LOG.debug((Object)("Conversion took " + (System.currentTimeMillis() - startMillis) + " ms."));
                throw throwable;
            }
            catch (MediaConversionException e) {
                cloudinaryConversionErrorLogStrategy.logConversionError(container, format, input, e);
                throw e;
            }
        }
        LOG.debug((Object)("Conversion took " + (System.currentTimeMillis() - startMillis) + " ms."));
        return mediaModel;
    }

    private MediaModel convert(MediaModel master, MediaFormatModel format) throws MediaConversionException {
        try {
            return this.cloudinaryMediaConversionStrategy.convert(this, master, format);
        }
        catch (NoSuchBeanDefinitionException e) {
            throw new MediaConversionException("Invalid conversion strategy '" + format.getTransformationStrategy() + "' defined.", e);
        }
    }

    @Override
    protected MediaModel selectMasterFromUnsavedContainer(MediaContainerModel model) {
        MediaModel master = null;
        if (model.getMedias() != null) {
            for (MediaModel media : model.getMedias()) {
                if (media.getCloudinaryURL() == null && (media.getOriginal() != null || media.getOriginalDataPK() != null)) continue;
                if (master == null) {
                    master = media;
                    continue;
                }
                LOG.debug((Object)("Multiple medias which claim to be master detected in unsaved container '" + (Object)model + "'."));
                return null;
            }
        }
        if (master == null) {
            LOG.debug((Object)("No master media found in unsaved container '" + (Object)model + "'."));
        }
        return master;
    }

}
