package uk.ptr.cloudinary.facades.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.model.ConversionMediaFormatModel;
import de.hybris.platform.mediaconversion.web.facades.DefaultOnDemandConversionFacade;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Optional;
import javax.annotation.Resource;

import org.apache.log4j.Logger;

import uk.ptr.cloudinary.facades.CloudinaryOnDemandConversionFacade;


public class DefaultCloudinaryOnDemandConversionFacade extends DefaultOnDemandConversionFacade implements CloudinaryOnDemandConversionFacade
{
    private static final Logger LOG = Logger.getLogger(DefaultCloudinaryOnDemandConversionFacade.class);

    @Resource(name = "catalogVersionService")
    private CatalogVersionService catalogVersionService;

    @Override
    public String convert(PK container, String format) {
        try {
            return this.convert((MediaContainerModel)this.getModelService().get(container), format);
        } catch (ClassCastException var4) {
            throw new ModelLoadingException("PK does not refer to a MediaContainerModel.", var4);
        }
    }

    @Override
    public String retrieveURL(final String mediaQualifier, final String format) throws UnknownIdentifierException
    {
        MediaContainerModel container = getMediaContainerFromMedia(mediaQualifier);

        if (format != null && !format.isEmpty()) {
            String warning;
            try {
                MediaFormatModel formatModel = this.getMediaService().getFormat(format);
                if (formatModel instanceof MediaFormatModel) {
                    return this.retrieveURL(container, formatModel);
                }

                warning = "MediaFormat '" + format + "' is not a ConversionMediaFormat.";
            } catch (UnknownIdentifierException var5) {
                warning = var5.getMessage();
            }

            LOG.warn(warning);
            return null;
        } else {
            return this.retrieveURL(container, (MediaFormatModel)null);
        }
    }

    @Override
    public String retrieveURL(final MediaContainerModel container, final MediaFormatModel format)
    {
        //MediaContainerModel container = getMediaContainerFromMedia(mediaQualifier);

        if (format == null) {
            return this.masterUrl(container);
        } else {
            try {
                return this.buildConvertUrl(container, (MediaFormatModel)format);
            } catch (ModelNotFoundException var3) {
                return this.buildConvertUrl(container, (MediaFormatModel)format);
            }
        }
    }

    @Override
    public String convert(final MediaContainerModel container, final MediaFormatModel format)
    {
        return format == null ? this.masterUrl(container) : this.getMediaConversionService().getOrConvert(container, format).getURL();
    }

    private MediaContainerModel getMediaContainerFromMedia(final String mediaQualifier)
    {
        return (MediaContainerModel)this.getModelService().get(PK.parse(mediaQualifier));
    }

    @Override
    public String convert(final MediaContainerModel container, final String format)
    {
        if (format != null && !format.isEmpty()) {
            MediaFormatModel formatModel = this.getMediaService().getFormat(format);
            if (formatModel instanceof MediaFormatModel) {
                return this.convert(container, formatModel);
            } else {
                throw new UnknownIdentifierException("MediaFormat '" + format + "' is not a ConversionMediaFormat.");
            }
        } else {
            return this.convert(container, (MediaFormatModel)null);
        }
    }

    private String masterUrl(MediaContainerModel container) {
        MediaModel master = container.getMaster();
        if (master == null) {
            LOG.warn("No master media available for '" + container + "'.");
            return null;
        } else {
            return master.getURL();
        }
    }


    protected String buildConvertUrl(MediaContainerModel container, MediaFormatModel format) {
        StringBuilder ret = new StringBuilder();
        ret.append(this.getConfigurationService().getConfiguration().getString("cloudinarymediacore.webroot", "/cloudinarymediacore"));
        if (this.getConversionServletPath() != null && !this.getConversionServletPath().isEmpty()) {
            if (ret.charAt(ret.length() - 1) != '/' && this.getConversionServletPath().charAt(0) != '/') {
                ret.append('/');
            }

            ret.append(this.getConversionServletPath());
        }

        if (ret.charAt(ret.length() - 1) != '/') {
            ret.append('/');
        }

        ret.append(container.getPk().toString());
        ret.append('/');
        ret.append(format.getQualifier());
        appendFileExtension(ret, format);
        if (ret.charAt(0) != '/') {
            ret.insert(0, '/');
        }

        return ret.toString();
    }

    protected void appendFileExtension(StringBuilder urlBuilder, MediaFormatModel format) {
        if (format.getMediaMimeType() != null && !format.getMediaMimeType().isEmpty()) {
            String fileExtension = this.getMimeMappingStrategy().fileExtensionForMimeType(format.getMediaMimeType());
            if (fileExtension != null && !fileExtension.isEmpty()) {
                urlBuilder.append('.').append(fileExtension);
            } else {
                Logger.getLogger(this.getClass()).debug("The mime mapping strategy '" + this.getMimeMappingStrategy().getClass().getName() + "' provides no file extension for mime type '." + format.getMediaMimeType() + "'");
            }
        }

    }
}
