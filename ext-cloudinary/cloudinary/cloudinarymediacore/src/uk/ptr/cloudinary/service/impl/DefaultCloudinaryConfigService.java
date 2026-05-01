package uk.ptr.cloudinary.service.impl;

import jakarta.annotation.Resource;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.CloudinaryConfigService;


public class DefaultCloudinaryConfigService implements CloudinaryConfigService
{

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Override
    public CloudinaryConfigModel getCloudinaryConfigModel() {

         return cloudinaryConfigDao.getCloudinaryConfigModel();
    }
}
