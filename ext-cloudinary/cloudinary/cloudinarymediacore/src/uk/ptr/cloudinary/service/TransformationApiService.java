package uk.ptr.cloudinary.service;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;


public interface TransformationApiService
{
    String createTransformation(MediaModel masterMedia,MediaFormatModel format);
}
