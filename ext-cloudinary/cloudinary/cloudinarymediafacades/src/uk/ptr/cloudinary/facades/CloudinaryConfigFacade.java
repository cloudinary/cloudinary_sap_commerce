package uk.ptr.cloudinary.facades;

import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;

public interface CloudinaryConfigFacade {

    public CloudinaryConfigModel getCloudinaryConfig();
}
