package uk.ptr.cloudinary.service;

import de.hybris.platform.core.model.media.MediaModel;

import java.io.IOException;

public interface UpdateTagApiService {

    public void updateTagOnAsests(String publicId, String productCod, String cloudinaryUrl,String resourceType) throws IOException;
}
