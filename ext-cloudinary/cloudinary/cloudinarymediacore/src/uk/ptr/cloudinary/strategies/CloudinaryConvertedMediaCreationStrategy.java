package uk.ptr.cloudinary.strategies;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaIOException;

import java.io.InputStream;


public interface CloudinaryConvertedMediaCreationStrategy
{
        MediaModel createOrUpdate(MediaModel var1, MediaFormatModel var2, InputStream var3) throws MediaIOException;
}
