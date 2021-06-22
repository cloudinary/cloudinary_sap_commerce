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

                StringBuilder transformation = new StringBuilder();
                StringBuilder mediaurl = new StringBuilder();
                String updatedMediaUrl;

                if (media.getCatalogVersion().getCatalog() instanceof ContentCatalogModel) {
                    updatedMediaUrl = contentMediaTransformation(cloudinaryConfig, format, media, mediaurl, transformation, cloudinary);
                } else {
                    updatedMediaUrl = productMediaTransformation(cloudinaryConfig, format, media, mediaurl, transformation, cloudinary);
                }
                mediaurl.append(CloudinarymediacoreConstants.DOT);
                mediaurl.append(media.getCloudinaryMediaFormat());
                media.setURL(updatedMediaUrl);
                modelService.save(media);
                return mediaurl.toString();
            }
        }
        return null;
    }

    private String productMediaTransformation(CloudinaryConfigModel cloudinaryConfig, MediaFormatModel format, MediaModel media, StringBuilder mediaUrl, StringBuilder transformation, Cloudinary cloudinary) {
        if (format != null) {
            Transformation globalTransformation = new Transformation();
            if (BooleanUtils.isTrue(cloudinaryConfig.getCloudinaryResponsive())) {
                transformation.append("w_auto");
                transformation.append(",");
            }
            if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(media.getCloudinaryResourceType())) {
                String imageQuality = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeImage()) ? CloudinaryMediaQuality.Q_AUTO.getCode() : cloudinaryConfig.getCloudinaryQuality().getCode();

                if (imageQuality.contains("auto_")) {
                    imageQuality = imageQuality.replace("auto_", "auto:");
                }

                transformation.append(imageQuality);
                transformation.append(",");
                String imageFormat = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeImage()) ? CloudinaryMediaFormat.F_AUTO.getCode() : cloudinaryConfig.getCloudinaryImageFormat().getCode();
                transformation.append(imageFormat);


                if (cloudinaryConfig.getCloudinaryGlobalImageTransformation() != null) {
                    transformation.append(",");
                    transformation.append(cloudinaryConfig.getCloudinaryGlobalImageTransformation());
                }

            } else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(media.getCloudinaryResourceType())) {
                String videoQuality = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeVideo()) ? CloudinaryVideoQuality.Q_AUTO.getCode() : cloudinaryConfig.getCloudinaryVideoQuality().getCode();
                if (videoQuality.contains("auto_")) {
                    videoQuality = videoQuality.replace("auto_", "auto:");
                }
                transformation.append(videoQuality);
                transformation.append(",");
                String videoFormat = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeVideo()) ? CloudinaryVideoFormat.F_AUTO.getCode() : cloudinaryConfig.getCloudinaryVideoFormat().getCode();
                transformation.append(videoFormat);

                if (cloudinaryConfig.getCloudinaryGlobalVideoTransformation() != null) {
                    transformation.append(",");
                    transformation.append(cloudinaryConfig.getCloudinaryGlobalVideoTransformation());
                }
            }

            if (StringUtils.isNotBlank(transformation.toString())) {
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
        return mediaUrl.toString();
    }

    private String contentMediaTransformation(CloudinaryConfigModel cloudinaryConfig, MediaFormatModel format, MediaModel media, StringBuilder mediaUrl, StringBuilder transformation, Cloudinary cloudinary) {

            Transformation contentGlobalTransformation = new Transformation();
            if (BooleanUtils.isTrue(cloudinaryConfig.getCloudinaryResponsive())) {
                transformation.append("w_auto");
                transformation.append(",");
            }
            if (CloudinarymediacoreConstants.IMAGE.equalsIgnoreCase(media.getCloudinaryResourceType())) {
                String contentImageQuality = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeContentImage()) ? CloudinaryMediaQuality.Q_AUTO.getCode() : cloudinaryConfig.getCloudinaryContentImageQuality().getCode();

                if (contentImageQuality.contains("auto_")) {
                    contentImageQuality = contentImageQuality.replace("auto_", "auto:");
                }

                transformation.append(contentImageQuality);
                transformation.append(",");
                String imageFormat = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeContentImage()) ? CloudinaryMediaFormat.F_AUTO.getCode() : cloudinaryConfig.getCloudinaryContentImageFormat().getCode();
                transformation.append(imageFormat);


                if (StringUtils.isNotEmpty(cloudinaryConfig.getCloudinaryContentGlobalImageTransformation())) {
                    transformation.append("/");
                    transformation.append(cloudinaryConfig.getCloudinaryContentGlobalImageTransformation());
                }

            }
            else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(media.getCloudinaryResourceType())) {
                String videoQuality = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeContentVideo()) ? CloudinaryVideoQuality.Q_AUTO.getCode() : cloudinaryConfig.getCloudinaryContentVideoQuality().getCode();
                if (videoQuality.contains("auto_")) {
                    videoQuality = videoQuality.replace("auto_", "auto:");
                }
                transformation.append(videoQuality);
                transformation.append(",");
                String videoFormat = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeContentVideo()) ? CloudinaryVideoFormat.F_AUTO.getCode() : cloudinaryConfig.getCloudinaryContentVideoFormat().getCode();
                transformation.append(videoFormat);

                if (StringUtils.isNotEmpty(cloudinaryConfig.getCloudinaryGlobalContentVideoTransformation())) {
                    transformation.append("/");
                    transformation.append(cloudinaryConfig.getCloudinaryGlobalContentVideoTransformation());
                }
            }
            if (StringUtils.isNotEmpty(transformation.toString())) {
                contentGlobalTransformation = contentGlobalTransformation.rawTransformation(transformation.toString());
                if (format != null && format.getTransformation() != null) {
                    contentGlobalTransformation = contentGlobalTransformation.chain().rawTransformation(format.getTransformation());
                    media.setCloudinaryTransformation(format.getTransformation());
                }
            }
           mediaUrl.append(cloudinary.url().resourceType(media.getCloudinaryResourceType()).transformation(contentGlobalTransformation).publicId(media.getCloudinaryPublicId()).secure(Boolean.TRUE).generate());
        return mediaUrl.toString();
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
                                    String imageQuality = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeImage()) ? CloudinaryMediaQuality.Q_AUTO.getCode() : cloudinaryConfig.getCloudinaryQuality().getCode();

                                    if (imageQuality.contains("auto_")) {
                                        imageQuality = imageQuality.replace("auto_", "auto:");
                                    }
                                    globalTransformation.append(imageQuality);
                                    globalTransformation.append(",");
                                    String imageFormat = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeImage()) ? CloudinaryMediaFormat.F_AUTO.getCode() : cloudinaryConfig.getCloudinaryImageFormat().getCode();
                                    globalTransformation.append(imageFormat);


                                } else if (CloudinarymediacoreConstants.VIDEO.equalsIgnoreCase(imageData.getCloudinaryResourceType())) {
                                    String videoQuality = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeVideo()) ? CloudinaryVideoQuality.Q_AUTO.getCode() : cloudinaryConfig.getCloudinaryVideoQuality().getCode();
                                    if (videoQuality.contains("auto_")) {
                                        videoQuality = videoQuality.replace("auto_", "auto:");
                                    }
                                    globalTransformation.append(videoQuality);
                                    globalTransformation.append(",");
                                    String videoFormat = BooleanUtils.isTrue(cloudinaryConfig.getEnableOptimizeVideo()) ? CloudinaryVideoFormat.F_AUTO.getCode() : cloudinaryConfig.getCloudinaryVideoFormat().getCode();
                                    globalTransformation.append(videoFormat);

                                }

                                if (org.apache.commons.lang3.StringUtils.isNotBlank(globalTransformation.toString())) {
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
                                imageData.setUrl(mediaurl.toString());

                            }
                        }
                    }
                }
            }
        }

        return imageDatas;
    }
}
