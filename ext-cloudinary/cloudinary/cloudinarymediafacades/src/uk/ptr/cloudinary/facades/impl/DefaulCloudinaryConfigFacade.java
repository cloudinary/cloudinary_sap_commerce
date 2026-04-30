package uk.ptr.cloudinary.facades.impl;

import jakarta.annotation.Resource;
import uk.ptr.cloudinary.facades.CloudinaryConfigFacade;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.CloudinaryConfigService;

public class DefaulCloudinaryConfigFacade implements CloudinaryConfigFacade {

    @Resource
    private CloudinaryConfigService cloudinaryConfigService;

    @Override
    public CloudinaryConfigModel getCloudinaryConfig() {
        return cloudinaryConfigService.getCloudinaryConfigModel();
    }
}
