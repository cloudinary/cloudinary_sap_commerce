package uk.ptr.cloudinary.strategies;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.mediaconversion.conversion.MediaConversionException;


/**
 * The interface Cloudinary media conversion strategy.
 */
public interface CloudinaryMediaConversionStrategy
{
        /**
         * Convert media model.
         *
         * @param mediaConversionService
         *         the media conversion service
         * @param mediaModel
         *         the media model
         * @param mediaFormatModel
         *         the media format model
         *
         * @return the media model
         * @throws MediaConversionException
         *         the media conversion exception
         */
        MediaModel convert(MediaConversionService mediaConversionService, MediaModel mediaModel, MediaFormatModel mediaFormatModel) throws MediaConversionException;
}
