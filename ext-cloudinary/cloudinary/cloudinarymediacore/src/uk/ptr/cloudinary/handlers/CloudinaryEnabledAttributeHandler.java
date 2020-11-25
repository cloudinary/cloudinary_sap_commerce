package uk.ptr.cloudinary.handlers;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

import javax.annotation.Resource;

import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;


public class CloudinaryEnabledAttributeHandler extends AbstractDynamicAttributeHandler<Boolean,MediaModel>
{
    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    public Boolean get(MediaModel model) {

        //GET CLOUDINARY CONFIG
        CloudinaryConfigModel cloudinaryConfig = cloudinaryConfigDao.getCloudinaryConfigModel();
        if(cloudinaryConfig!=null){
            return cloudinaryConfig.getEnableCloudinary();
        }
        return Boolean.FALSE;
    }
}
