package uk.ptr.cloudinary.strategies;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;


/**
 * The interface Cloudinary conversion error log strategy.
 */
public interface CloudinaryConversionErrorLogStrategy
{
    /**
     * Report conversion success.
     *
     * @param container
     *         the container
     * @param targetFormat
     *         the target format
     */
    void reportConversionSuccess(MediaContainerModel container, MediaFormatModel targetFormat);

    /**
     * Log conversion error.
     *
     * @param container
     *         the container
     * @param targetFormat
     *         the target format
     * @param sourceMedia
     *         the source media
     * @param fault
     *         the fault
     */
    void logConversionError(MediaContainerModel container, MediaFormatModel targetFormat, MediaModel sourceMedia, Exception fault);
}
