package uk.ptr.cloudinary.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.jalo.contents.ContentCatalog;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Resource;

import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;

import org.apache.commons.lang3.StringUtils;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.enums.CloudinaryMediaFormat;
import uk.ptr.cloudinary.enums.CloudinaryMediaQuality;
import uk.ptr.cloudinary.enums.CloudinaryVideoFormat;
import uk.ptr.cloudinary.enums.CloudinaryVideoQuality;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.TransformationApiService;


public class DefaultTransformationApiService implements TransformationApiService {

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private ModelService modelService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Override
    public String createTransformation(final MediaModel media, final MediaFormatModel format) {
        //GET CLOUDINARY CONFIG
        CloudinaryConfigModel cloudinaryConfig = cloudinaryConfigDao.getCloudinaryConfigModel();

        if (cloudinaryConfig != null && cloudinaryConfig.getCloudinaryURL() != null) {
            if (media.getCloudinaryPublicId() != null && media.getCloudinaryResourceType() != null && media.getCloudinaryType() != null) {
                Cloudinary cloudinary = new Cloudinary(cloudinaryConfig.getCloudinaryURL());
                //cloudinary.setUserAgent(CloudinarymediacoreConstants.CLOUDINARYSAPCC, CloudinarymediacoreConstants.CLOUDINARY_VERSION + "(SAPCC" + CloudinarymediacoreConstants.SAP_VERSION + ")");

                StringBuilder transformation = new StringBuilder();
                StringBuilder mediaUrl = new StringBuilder();

                if (media.getCatalogVersion().getCatalog() instanceof ContentCatalogModel) {
                    mediaUrl = contentMediaTransformation(cloudinaryConfig, format, media, mediaUrl, transformation, cloudinary);
                } else {
                    mediaUrl = productMediaTransformation(cloudinaryConfig, format, media, mediaUrl, transformation, cloudinary);
                }
                mediaUrl.append(CloudinarymediacoreConstants.DOT);
                mediaUrl.append(media.getCloudinaryMediaFormat());
                media.setURL(mediaUrl.toString());
                modelService.save(media);
                return mediaUrl.toString();
            }
        }
        return null;
    }

    private StringBuilder productMediaTransformation(CloudinaryConfigModel cloudinaryConfig, MediaFormatModel format, MediaModel media, StringBuilder mediaUrl, StringBuilder transformation, Cloudinary cloudinary) {
        if (format != null) {
            Transformation globalTransformation = new Transformation();
            if (BooleanUtils.isTrue(cloudinaryConfig.getCloudinaryResponsive())) {
                transformation.append("w_auto");
                transformation.append(",");
            }
            if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(media.getCloudinaryResourceType())) {
                if(BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeImage())){

                    String imageQuality = cloudinaryConfig.getCloudinaryQuality().getCode();

                    if (imageQuality.contains("auto_")) {
                        imageQuality = imageQuality.replace("auto_", "auto:");
                    }

                    transformation.append(imageQuality);
                    transformation.append(",");
                    String imageFormat = cloudinaryConfig.getCloudinaryImageFormat().getCode();
                    transformation.append(imageFormat);
                    transformation.append(",");
                }

                if (cloudinaryConfig.getCloudinaryGlobalImageTransformation() != null) {
                    transformation.append(cloudinaryConfig.getCloudinaryGlobalImageTransformation());
                }

            } else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(media.getCloudinaryResourceType())) {
                if(BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeVideo())){
                    String videoQuality = cloudinaryConfig.getCloudinaryVideoQuality().getCode();
                    if (videoQuality.contains("auto_")) {
                        videoQuality = videoQuality.replace("auto_", "auto:");
                    }
                    transformation.append(videoQuality);
                    transformation.append(",");
                    String videoFormat = cloudinaryConfig.getCloudinaryVideoFormat().getCode();
                    transformation.append(videoFormat);
                    transformation.append(",");
                }


                if (cloudinaryConfig.getCloudinaryGlobalVideoTransformation() != null) {
                    transformation.append(cloudinaryConfig.getCloudinaryGlobalVideoTransformation());
                }
            }

            if (StringUtils.isNotBlank(transformation.toString())) {
                if (transformation.charAt(transformation.length() - 1) == ',') {
                    transformation.setLength(transformation.length() - 1);
                }
                globalTransformation = globalTransformation.rawTransformation(transformation.toString());
                if (format != null && format.getTransformation() != null) {
                    globalTransformation = globalTransformation.chain().rawTransformation(format.getTransformation());
                    media.setCloudinaryTransformation(format.getTransformation());
                }
            }
            media.setCloudinaryTransformation(format.getTransformation());
            mediaUrl.append(cloudinary.url().resourceType(media.getCloudinaryResourceType()).transformation(globalTransformation).publicId(media.getCloudinaryPublicId()).secure(Boolean.TRUE).generate());

        } else {
            if (BooleanUtils.isTrue(cloudinaryConfig.getCloudinaryResponsive())) {
                transformation.append("w_auto");
            }
            mediaUrl.append(cloudinary.url().resourceType(media.getCloudinaryResourceType()).transformation(new Transformation().rawTransformation(transformation.toString())).publicId(media.getCloudinaryPublicId()).secure(Boolean.TRUE).generate());
        }
        return mediaUrl;
    }

    private StringBuilder contentMediaTransformation(CloudinaryConfigModel cloudinaryConfig, MediaFormatModel format, MediaModel media, StringBuilder mediaUrl, StringBuilder transformation, Cloudinary cloudinary) {

        Transformation contentGlobalTransformation = new Transformation();
         if (BooleanUtils.isTrue(cloudinaryConfig.getCloudinaryResponsive())) {
            transformation.append("w_auto");
            transformation.append(",");
        }
        if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(media.getCloudinaryResourceType())) {
            if(BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeContentImage())){
                String contentImageQuality =  cloudinaryConfig.getCloudinaryContentImageQuality().getCode();

                if (contentImageQuality.contains("auto_")) {
                    contentImageQuality = contentImageQuality.replace("auto_", "auto:");
                }

                transformation.append(contentImageQuality);
                transformation.append(",");
                String imageFormat = cloudinaryConfig.getCloudinaryContentImageFormat().getCode();
                transformation.append(imageFormat);
                transformation.append(",");
            }

            if (!BooleanUtils.isTrue(media.getIsCloudinaryOverride()) && StringUtils.isNotEmpty(cloudinaryConfig.getCloudinaryContentGlobalImageTransformation())) {
                transformation.append(cloudinaryConfig.getCloudinaryContentGlobalImageTransformation());
            }

        }
        else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(media.getCloudinaryResourceType())) {
            if(BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeContentVideo())){
                String videoQuality = cloudinaryConfig.getCloudinaryContentVideoQuality().getCode();
                if (videoQuality.contains("auto_")) {
                    videoQuality = videoQuality.replace("auto_", "auto:");
                }
                transformation.append(videoQuality);
                transformation.append(",");
                String videoFormat = cloudinaryConfig.getCloudinaryContentVideoFormat().getCode();
                transformation.append(videoFormat);
                transformation.append(",");
            }

            if (!BooleanUtils.isTrue(media.getIsCloudinaryOverride()) && StringUtils.isNotEmpty(cloudinaryConfig.getCloudinaryGlobalContentVideoTransformation())) {
                transformation.append(cloudinaryConfig.getCloudinaryGlobalContentVideoTransformation());
            }
        }
        if (StringUtils.isNotEmpty(transformation.toString())) {
            if (transformation.charAt(transformation.length() - 1) == ',') {
                transformation.setLength(transformation.length() - 1);
            }
            contentGlobalTransformation = contentGlobalTransformation.rawTransformation(transformation.toString());
            if (format != null && format.getTransformation() != null) {
                String mFormat = format.getTransformation();
                if (mFormat.contains(",h_")) {
                    // Remove the height part ",h_" if it exists
                    mFormat = mFormat.replaceAll(",h_\\d+", "");
                }
                contentGlobalTransformation = contentGlobalTransformation.chain().rawTransformation(mFormat);
                media.setCloudinaryTransformation(mFormat);
            }
        }
        mediaUrl.append(cloudinary.url().resourceType(media.getCloudinaryResourceType()).transformation(contentGlobalTransformation).publicId(media.getCloudinaryPublicId()).secure(Boolean.TRUE).generate());
        return mediaUrl;
    }

    @Override
    public Collection<ImageData> createTransformation(final ProductModel product, final Collection<ImageData> imageDatas) {
        CloudinaryConfigModel cloudinaryConfig = cloudinaryConfigDao.getCloudinaryConfigModel();

        if (cloudinaryConfig != null && BooleanUtils.isTrue(cloudinaryConfig.getEnableCloudinary())) {
            StringBuilder categoryImageTransformation = new StringBuilder();
            StringBuilder categoryVideoTransformation = new StringBuilder();

            String globalImageTransformation = cloudinaryConfig.getCloudinaryGlobalImageTransformation();
            String globalVideoTransformation = cloudinaryConfig.getCloudinaryGlobalVideoTransformation();

            Collection<CategoryModel> categories = product.getSupercategories();
            boolean isCategoryOverride = false;

            if (CollectionUtils.isNotEmpty(categories)) {
                isCategoryOverride = categories.stream().anyMatch(category -> BooleanUtils.isTrue(category.getIsCloudinaryOverride()));
                for (final CategoryModel category : categories) {
                    if (!(category instanceof ClassificationClassModel)) {
                        if (org.apache.commons.lang.StringUtils.isNotBlank(category.getCloudinaryImageTransformation())) {
                            categoryImageTransformation.append(CloudinarymediacoreConstants.SLASH).append(category.getCloudinaryImageTransformation());
                        }
                        if (org.apache.commons.lang.StringUtils.isNotBlank(category.getCloudinaryVideoTransformation())) {
                            categoryVideoTransformation.append(CloudinarymediacoreConstants.SLASH).append(category.getCloudinaryVideoTransformation());
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(imageDatas)) {
                for (ImageData imageData : imageDatas) {
                    if (imageData.getCloudinaryURL() != null) {

                        //StringBuilder transformationURL = new StringBuilder();
                        if (cloudinaryConfig != null && cloudinaryConfig.getCloudinaryURL() != null) {
                            if (imageData.getCloudinaryPublicId() != null && imageData.getCloudinaryResourceType() != null && imageData.getCloudinaryType() != null) {
                                Cloudinary cloudinary = new Cloudinary(cloudinaryConfig.getCloudinaryURL());
                                //cloudinary.setUserAgent(CloudinarymediacoreConstants.CLOUDINARYSAPCC, CloudinarymediacoreConstants.CLOUDINARY_VERSION + "(SAPCC" + CloudinarymediacoreConstants.SAP_VERSION + ")");
                                StringBuilder globalTransformation = new StringBuilder();
                                StringBuilder mediaurl = new StringBuilder();

                                Transformation transformation = new Transformation();

                                if (BooleanUtils.isTrue(cloudinaryConfig.getCloudinaryResponsive())) {
                                    globalTransformation.append("w_auto");
                                    globalTransformation.append(",");
                                } else {
                                    if (org.apache.commons.lang.StringUtils.isNotBlank(imageData.getCloudinaryTransformation())) {
                                        globalTransformation.append(imageData.getCloudinaryTransformation());
                                        globalTransformation.append(",");
                                    }
                                }

                                if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                    if(BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeImage())){
                                        String imageQuality = cloudinaryConfig.getCloudinaryQuality().getCode();

                                        if (imageQuality.contains("auto_")) {
                                            imageQuality = imageQuality.replace("auto_", "auto:");
                                        }
                                        globalTransformation.append(imageQuality);
                                        globalTransformation.append(",");
                                        String imageFormat = cloudinaryConfig.getCloudinaryImageFormat().getCode();
                                        globalTransformation.append(imageFormat);
                                    }


                                } else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                    if(BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeVideo())){
                                        String videoQuality = cloudinaryConfig.getCloudinaryVideoQuality().getCode();
                                        if (videoQuality.contains("auto_")) {
                                            videoQuality = videoQuality.replace("auto_", "auto:");
                                        }
                                        globalTransformation.append(videoQuality);
                                        globalTransformation.append(",");
                                        String videoFormat = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeVideo()) ? cloudinaryConfig.getCloudinaryVideoFormat().getCode() : CloudinaryVideoFormat.F_AUTO.getCode();
                                        globalTransformation.append(videoFormat);
                                    }

                                }

                                if (org.apache.commons.lang3.StringUtils.isNotBlank(globalTransformation.toString())) {
                                    if (globalTransformation.charAt(globalTransformation.length() - 1) == ',') {
                                        globalTransformation.setLength(globalTransformation.length() - 1);
                                    }
                                    transformation = transformation.rawTransformation(globalTransformation.toString());
                                }

                                if (!imageData.isCloudinaryOverride()) {
                                    boolean isProductOverride = BooleanUtils.isTrue(product.getIsCloudinaryOverride());
                                    if (isProductOverride) {
                                        if (org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryImageTransformation()) || org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryVideoTransformation())) {
                                            if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                                transformation = transformation.chain().rawTransformation(product.getCloudinaryImageTransformation());

                                            } else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                                transformation = transformation.chain().rawTransformation(product.getCloudinaryVideoTransformation());
                                            }
                                        }

                                    } else if (!isProductOverride) {
                                        if (isCategoryOverride) {
                                            if (org.apache.commons.lang.StringUtils.isNotBlank(categoryImageTransformation.toString()) || org.apache.commons.lang.StringUtils.isNotBlank(categoryVideoTransformation.toString())) {
                                                if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                                    transformation = transformation.chain().rawTransformation(categoryImageTransformation.toString());
                                                } else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                                    transformation = transformation.chain().rawTransformation(categoryVideoTransformation.toString());
                                                }
                                            }
                                            if (org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryImageTransformation()) || org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryVideoTransformation())) {
                                                if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                                    transformation = transformation.chain().rawTransformation(product.getCloudinaryImageTransformation());
                                                } else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                                    transformation = transformation.chain().rawTransformation(product.getCloudinaryVideoTransformation());
                                                }
                                            }

                                        } else if (!isCategoryOverride) {
                                            if (org.apache.commons.lang.StringUtils.isNotBlank(globalImageTransformation) || org.apache.commons.lang.StringUtils.isNotBlank(globalVideoTransformation)) {
                                                if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {

                                                    transformation = transformation.chain().rawTransformation(globalImageTransformation);
                                                } else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                                    transformation = transformation.chain().rawTransformation(globalVideoTransformation);
                                                }
                                            }

                                            if (org.apache.commons.lang.StringUtils.isNotBlank(categoryImageTransformation.toString()) || org.apache.commons.lang.StringUtils.isNotBlank(categoryVideoTransformation.toString())) {
                                                if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                                    transformation = transformation.chain().rawTransformation(categoryImageTransformation.toString());
                                                } else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                                    transformation = transformation.chain().rawTransformation(categoryVideoTransformation.toString());
                                                }
                                            }
                                            if (org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryImageTransformation()) || org.apache.commons.lang.StringUtils.isNotBlank(product.getCloudinaryVideoTransformation())) {
                                                if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                                    transformation = transformation.chain().rawTransformation(product.getCloudinaryImageTransformation());
                                                } else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                                    transformation = transformation.chain().rawTransformation(product.getCloudinaryImageTransformation());
                                                }
                                            }
                                        }
                                    }

                                }

                                mediaurl.append(cloudinary.url().resourceType(imageData.getCloudinaryResourceType()).transformation(transformation).secure(Boolean.TRUE).publicId(imageData.getCloudinaryPublicId()).generate());
                                mediaurl.append(CloudinarymediacoreConstants.DOT);
                                mediaurl.append(imageData.getCloudinaryMediaFormat());
                                imageData.setUrl(mediaurl.toString() + CloudinarymediacoreConstants.CLOUDINARY_QUERY_PARAM);

                            }
                        }
                    }
                }
            }
        }

        return imageDatas;
    }
}
