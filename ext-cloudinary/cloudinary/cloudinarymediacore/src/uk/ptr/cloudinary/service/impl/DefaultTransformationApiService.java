package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;

import javax.annotation.Resource;

import com.cloudinary.utils.StringUtils;

import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
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
        StringBuilder transformationURL = new StringBuilder();

        //GET CLOUDINARY CONFIG
        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

        if(cloudinaryConfigModel!=null){
            if(StringUtils.isNotBlank(cloudinaryConfigModel.getCloudinaryCname())){
                transformationURL.append(cloudinaryConfigModel.getCloudinaryCname());
            }else{
                transformationURL.append(CloudinarymediacoreConstants.CLOUDINARY_DOMAIN_URL);
            }
            transformationURL.append(CloudinarymediacoreConstants.SLASH);

            String cloudinaryConnectionURL = cloudinaryConfigModel.getCloudinaryURL();
            int cloudNameIndex = cloudinaryConnectionURL.indexOf(CloudinarymediacoreConstants.AT);

            //Extract and set cloudname
            transformationURL.append(cloudinaryConnectionURL.substring(cloudNameIndex+1,cloudinaryConnectionURL.length()));
            transformationURL.append(CloudinarymediacoreConstants.SLASH);
        }

        transformationURL.append(masterMedia.getCloudinaryResourceType());
        transformationURL.append(CloudinarymediacoreConstants.SLASH);
        transformationURL.append(masterMedia.getCloudinaryType());
        transformationURL.append(CloudinarymediacoreConstants.SLASH);
        transformationURL.append(format.getTransformation());
        transformationURL.append(CloudinarymediacoreConstants.SLASH);
        transformationURL.append(masterMedia.getCloudinaryVersion());
        transformationURL.append(CloudinarymediacoreConstants.SLASH);
        transformationURL.append(masterMedia.getCloudinaryPublicId());
        transformationURL.append(CloudinarymediacoreConstants.DOT);
        transformationURL.append(masterMedia.getCloudinaryMediaFormat());

        return transformationURL.toString();
    }
}
