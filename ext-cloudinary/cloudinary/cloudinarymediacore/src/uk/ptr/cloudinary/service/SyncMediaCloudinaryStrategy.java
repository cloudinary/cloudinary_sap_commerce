package uk.ptr.cloudinary.service;

import de.hybris.platform.core.model.media.MediaModel;

public interface SyncMediaCloudinaryStrategy {

    public MediaModel onDemandSyncMedia(final MediaModel media) throws Exception;
}
