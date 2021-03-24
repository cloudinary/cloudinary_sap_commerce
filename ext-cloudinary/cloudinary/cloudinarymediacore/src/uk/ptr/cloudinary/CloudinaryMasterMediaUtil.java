package uk.ptr.cloudinary;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.Collection;

public class CloudinaryMasterMediaUtil {

    public static MediaModel getMasterImage(MediaContainerModel mediaContainerModel) {

        Collection<MediaModel> medias = mediaContainerModel.getMedias();
        for (MediaModel mediaModel : medias) {
            if (mediaModel.getMediaFormat() == null && mediaModel.getCloudinaryURL() != null) {
                return mediaModel;
            }
        }
        return null;
    }
}
