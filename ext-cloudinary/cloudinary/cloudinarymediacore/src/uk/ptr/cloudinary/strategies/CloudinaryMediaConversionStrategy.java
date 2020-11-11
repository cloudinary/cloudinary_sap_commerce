package uk.ptr.cloudinary.strategies;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.mediaconversion.conversion.MediaConversionException;


public interface CloudinaryMediaConversionStrategy
{
        MediaModel convert(MediaConversionService mediaConversionService, MediaModel mediaModel, MediaFormatModel mediaFormatModel) throws MediaConversionException;
}
