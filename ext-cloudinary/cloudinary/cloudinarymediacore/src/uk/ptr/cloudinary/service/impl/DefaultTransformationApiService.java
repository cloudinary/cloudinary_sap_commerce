package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;

import javax.annotation.Resource;

import com.cloudinary.utils.StringUtils;

import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.TransformationApiService;


public class DefaultTransformationApiService implements TransformationApiService
{
    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Override
    public String createTransformation(final MediaModel masterMedia, final MediaFormatModel format)
    {
        String originalUrl = masterMedia.getURL();
        StringBuilder transformationURL = new StringBuilder();

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();
        if(cloudinaryConfigModel!=null){
            if(StringUtils.isNotBlank(cloudinaryConfigModel.getCloudinaryCname())){
                transformationURL.append(cloudinaryConfigModel.getCloudinaryCname());
            }else{
                transformationURL.append("https://res.cloudinary.com");
            }
            transformationURL.append("/");
            //cloudinary://374658688623197:n0nPzQUL64sZcL4q6sDWbtFuOwI@portaltech-reply
            String cloudinaryConnectionURL = cloudinaryConfigModel.getCloudinaryURL();
            int cloudNameIndex = cloudinaryConnectionURL.indexOf("@");

            //Extract and set cloudname
            transformationURL.append(cloudinaryConnectionURL.substring(cloudNameIndex-1,cloudinaryConnectionURL.length()));
            transformationURL.append("/");
        }

        transformationURL.append(masterMedia.getCloudinaryResourceType());
        transformationURL.append("/");
        transformationURL.append(masterMedia.getCloudinaryType());
        transformationURL.append("/");
        transformationURL.append(format.getTransformation());
        transformationURL.append("/");
        transformationURL.append(masterMedia.getCloudinaryVersion());
        transformationURL.append("/");
        transformationURL.append(masterMedia.getCloudinaryPublicId());

        return transformationURL.toString();
    }
}
