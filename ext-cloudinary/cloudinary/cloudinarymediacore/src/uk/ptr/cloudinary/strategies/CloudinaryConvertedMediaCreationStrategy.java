package uk.ptr.cloudinary.strategies;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaIOException;

import java.io.InputStream;


/**
 * The interface Cloudinary converted media creation strategy.
 */
public interface CloudinaryConvertedMediaCreationStrategy
{
        /**
         * Create or update media model.
         *
         * @param media
         *         the var 1
         * @param format
         *         the var 2
         * @param stream
         *         the var 3
         *
         * @return the media model
         * @throws MediaIOException
         *         the media io exception
         */
        MediaModel createOrUpdate(MediaModel media, MediaFormatModel format, InputStream stream) throws MediaIOException;
}
