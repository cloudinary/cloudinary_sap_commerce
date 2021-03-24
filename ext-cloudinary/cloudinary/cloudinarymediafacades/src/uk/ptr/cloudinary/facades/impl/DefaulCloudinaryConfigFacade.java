package uk.ptr.cloudinary.facades.impl;

import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.facades.CloudinaryConfigFacade;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;

import javax.annotation.Resource;

public class DefaulCloudinaryConfigFacade implements CloudinaryConfigFacade {

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Override
    public CloudinaryConfigModel getCloudinaryConfig() {
        return cloudinaryConfigDao.getCloudinaryConfigModel();
    }
}
