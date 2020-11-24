package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collection;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;

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

    @Override
    public Collection<ImageData> createTransformation(final ProductModel product, final Collection<ImageData> imageDatas)
    {
        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

        if(cloudinaryConfigModel!=null && BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary()))
        {
            StringBuilder categoryImageTransformation = new StringBuilder();
            StringBuilder categoryVideoTransformation = new StringBuilder();

            String globalImageTransformation = cloudinaryConfigModel.getCloudinaryGlobalImageTransformation();
            String globalVideoTransformation = cloudinaryConfigModel.getCloudinaryGlobalVideoTransformation();

            Collection<CategoryModel> categories = product.getSupercategories();
            boolean isCategoryOverride = false;

            if (CollectionUtils.isNotEmpty(categories))
            {

                isCategoryOverride = categories.stream().anyMatch(category -> BooleanUtils.isTrue(category.getIsCloudinaryOverride()));
                for (final CategoryModel category : categories)
                {
                    if (!(category instanceof ClassificationClassModel))
                    {
                        if (org.apache.commons.lang.StringUtils.isNotBlank(category.getCloudinaryImageTransformation()))
                        {
                            categoryImageTransformation.append(CloudinarymediacoreConstants.SLASH).append(category.getCloudinaryImageTransformation());
                        }
                        if (org.apache.commons.lang.StringUtils.isNotBlank(category.getCloudinaryVideoTransformation()))
                        {
                            categoryVideoTransformation.append(CloudinarymediacoreConstants.SLASH).append(category.getCloudinaryVideoTransformation());
                        }
                    }
                }

            }

            for (ImageData imageData : imageDatas)
            {
                if(imageData.getCloudinaryURL()!=null)
                {

                    StringBuilder transformationURL = new StringBuilder();

                    if (cloudinaryConfigModel != null)
                    {
                        if (com.cloudinary.utils.StringUtils.isNotBlank(cloudinaryConfigModel.getCloudinaryCname()))
                        {
                            transformationURL.append(cloudinaryConfigModel.getCloudinaryCname());
                        }
                        else
                        {
                            transformationURL.append(CloudinarymediacoreConstants.CLOUDINARY_DOMAIN_URL);
                        }
                        transformationURL.append(CloudinarymediacoreConstants.SLASH);

                        String cloudinaryConnectionURL = cloudinaryConfigModel.getCloudinaryURL();
                        int cloudNameIndex = cloudinaryConnectionURL.indexOf(CloudinarymediacoreConstants.AT);

                        //Extract and set cloudname
                        transformationURL.append(cloudinaryConnectionURL.substring(cloudNameIndex + 1, cloudinaryConnectionURL.length()));
                        transformationURL.append(CloudinarymediacoreConstants.SLASH);
                    }

                    transformationURL.append(imageData.getCloudinaryResourceType());
                    transformationURL.append(CloudinarymediacoreConstants.SLASH);
                    transformationURL.append(imageData.getCloudinaryType());
                    transformationURL.append(CloudinarymediacoreConstants.SLASH);

                    if (org.apache.commons.lang.StringUtils.isNotBlank(imageData.getCloudinaryTransformation()))
                    {
                        transformationURL.append(imageData.getCloudinaryTransformation());
                    }

                    if (org.apache.commons.lang.StringUtils.isNotBlank(cloudinaryConfigModel.getCloudinaryQuality()))
                    {
                        if (org.apache.commons.lang.StringUtils.isNotBlank(imageData.getCloudinaryTransformation()))
                        {
                            transformationURL.append(",");
                        }
                        transformationURL.append(cloudinaryConfigModel.getCloudinaryQuality());
                    }


                    if(!imageData.isCloudinaryOverride())
                    {
                        boolean isProductOverride = BooleanUtils.isTrue(product.getIsCloudinaryOverride());

                        if (isProductOverride)
                        {
                            if (org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryImageTransformation()) || org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryVideoTransformation()))
                            {
                                transformationURL.append(CloudinarymediacoreConstants.SLASH);
                                if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                {
                                    transformationURL.append(product.getCloudinaryImageTransformation());
                                }
                                else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                {
                                    transformationURL.append(product.getCloudinaryVideoTransformation());
                                }
                            }

                        }
                        else if (!isProductOverride)
                        {
                            if (isCategoryOverride)
                            {
                                if (org.apache.commons.lang.StringUtils.isNotBlank(categoryImageTransformation.toString()) || org.apache.commons.lang.StringUtils.isNotBlank(categoryVideoTransformation.toString()))
                                {
                                    if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                    {
                                        transformationURL.append(categoryImageTransformation);
                                    }
                                    else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                    {
                                        transformationURL.append(categoryVideoTransformation);
                                    }
                                }
                                if (org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryImageTransformation()) || org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryVideoTransformation()))
                                {
                                    transformationURL.append(CloudinarymediacoreConstants.SLASH);
                                    if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                    {
                                        transformationURL.append(product.getCloudinaryImageTransformation());
                                    }
                                    else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                    {
                                        transformationURL.append(product.getCloudinaryVideoTransformation());
                                    }
                                }

                            }
                            else if (!isCategoryOverride)
                            {
                                if (org.apache.commons.lang.StringUtils.isNotBlank(globalImageTransformation) || org.apache.commons.lang.StringUtils.isNotBlank(globalVideoTransformation))
                                {
                                    transformationURL.append(CloudinarymediacoreConstants.SLASH);
                                    if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                    {
                                        transformationURL.append(globalImageTransformation);
                                    }
                                    else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                    {
                                        transformationURL.append(globalVideoTransformation);
                                    }
                                }

                                if (org.apache.commons.lang.StringUtils.isNotBlank(categoryImageTransformation.toString()) || org.apache.commons.lang.StringUtils.isNotBlank(categoryVideoTransformation.toString()))
                                {
                                    if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                    {
                                        transformationURL.append(categoryImageTransformation);
                                    }
                                    else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                    {
                                        transformationURL.append(categoryVideoTransformation);
                                    }
                                }
                                if (org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryImageTransformation()) || org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryVideoTransformation()))
                                {
                                    transformationURL.append(CloudinarymediacoreConstants.SLASH);
                                    if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                    {
                                        transformationURL.append(product.getCloudinaryImageTransformation());
                                    }
                                    else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType()))
                                    {
                                        transformationURL.append(product.getCloudinaryVideoTransformation());
                                    }
                                }
                            }

                        }

                    }
                    transformationURL.append(CloudinarymediacoreConstants.SLASH);
                    transformationURL.append(imageData.getCloudinaryVersion());
                    transformationURL.append(CloudinarymediacoreConstants.SLASH);
                    transformationURL.append(imageData.getCloudinaryPublicId());
                    transformationURL.append(CloudinarymediacoreConstants.DOT);
                    transformationURL.append(imageData.getCloudinaryMediaFormat());

                    imageData.setUrl(transformationURL.toString());
                }
            }
        }

        return imageDatas;
    }
}
