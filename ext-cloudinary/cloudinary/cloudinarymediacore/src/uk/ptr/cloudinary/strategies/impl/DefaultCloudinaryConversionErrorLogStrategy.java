package uk.ptr.cloudinary.strategies.impl;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.threadregistry.RegistrableThread;
import de.hybris.platform.mediaconversion.conversion.DefaultConversionErrorLogStrategy;
import de.hybris.platform.mediaconversion.model.ConversionErrorLogModel;
import de.hybris.platform.mediaconversion.util.HybrisRunnable;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Resource;

import org.apache.log4j.Logger;

import uk.ptr.cloudinary.dao.CloudinaryConversionErrorLogStrategyDao;
import uk.ptr.cloudinary.strategies.CloudinaryConversionErrorLogStrategy;


public class DefaultCloudinaryConversionErrorLogStrategy extends DefaultConversionErrorLogStrategy implements CloudinaryConversionErrorLogStrategy
{
    private static final Logger LOG = Logger.getLogger(DefaultConversionErrorLogStrategy.class);

    @Resource
    private CloudinaryConversionErrorLogStrategyDao cloudinaryConversionErrorLogStrategyDao;

    private ExecutorService executor;


    public void init() {
        if (this.executor != null) {
            throw new IllegalStateException("Executor already present.");
        } else {
            LOG.debug("Starting executor.");
            this.executor = Executors.newCachedThreadPool(new ThreadFactory() {
                private final AtomicInteger num = new AtomicInteger();

                public Thread newThread(Runnable target) {
                    return new RegistrableThread(target, "ConversionErrorLog-" + this.num.incrementAndGet());
                }
            });
        }
    }

    public void destroy() {
        if (this.executor == null) {
            LOG.warn("No executor available to shutdown.");
        } else {
            this.executor.shutdown();
            this.executor = null;
        }

    }

    @Override
    public void reportConversionSuccess(final MediaContainerModel container, final MediaFormatModel targetFormat)
    {
        if (this.getConfigurationService().getConfiguration().getBoolean("mediaconversion.removeConversionErrorLogUponSuccess", true)) {
            Iterator errorLogs = cloudinaryConversionErrorLogStrategyDao.findAllErrorLogs(container, targetFormat).iterator();

            while(errorLogs.hasNext()) {
                ConversionErrorLogModel errorLog = (ConversionErrorLogModel)errorLogs.next();

                try {
                    this.getModelService().remove(errorLog);
                } catch (ModelRemovalException ex) {
                    LOG.error("Failed to remove conversion error log entry '" + errorLog + "'.", ex);
                }
            }
        }
    }

    @Override
    public void logConversionError(MediaContainerModel container, MediaFormatModel targetFormat, MediaModel sourceMedia, Exception fault) {
        this.logConversionError(container, targetFormat, sourceMedia, this.extractErrorMessage(fault));
    }

    protected void logConversionError(final MediaContainerModel container, final MediaFormatModel targetFormat, final MediaModel sourceMedia, final String message) {
        if (this.executor == null) {
            this.error(container, targetFormat, sourceMedia, message, new IllegalStateException("No executor available."));
        } else {
            this.executor.execute(new HybrisRunnable(new Runnable() {
                public void run() {
                    DefaultCloudinaryConversionErrorLogStrategy.this.doStoreConversionError(container, targetFormat, sourceMedia, message);
                }
            }));
        }

    }

    protected void doStoreConversionError(MediaContainerModel container, MediaFormatModel targetFormat, MediaModel sourceMedia, String message) {
        try {
            ConversionErrorLogModel model = (ConversionErrorLogModel)this.getModelService().create(ConversionErrorLogModel.class);
            model.setContainer(container);
            model.setTargetMediaFormat(targetFormat);
            model.setSourceMedia(sourceMedia);
            model.setErrorMessage(message);
            this.getModelService().save(model);
        } catch (Exception var6) {
            this.error(container, targetFormat, sourceMedia, message, var6);
        }

    }

    private void error(MediaContainerModel container, MediaFormatModel targetFormat, MediaModel sourceMedia, String message, Exception exception) {
        LOG.error("Failed to store conversion error log. [container: " + container + "; targetFormat: " + targetFormat + "; sourceMedia: " + sourceMedia + "; message: " + message + "]", exception);
    }
}
