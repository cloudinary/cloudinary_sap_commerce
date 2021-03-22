package uk.ptr.cloudinary.dao;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.Collection;


public interface CloudinaryMediaConversionServiceDao
{
    Collection<MediaFormatModel> getAllMediaFormats();

    Collection<MediaModel> getConvertedMedias(MediaContainerModel var1);

    MediaModel retrieveMaster(MediaContainerModel var1);

}
