package uk.ptr.cloudinary.interceptors;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.model.VariantProductModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.UpdateTagApiService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class CloudinaryProductValidateInterceptor implements ValidateInterceptor<ProductModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryProductValidateInterceptor.class);

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private ModelService modelService;

    @Resource
    private UpdateTagApiService updateTagApiService;

    @Override
    public void onValidate(ProductModel model, InterceptorContext ctx) throws InterceptorException {

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

        if (model instanceof ProductModel && !ctx.isNew(model) && ctx.isModified(model, ProductModel.GALLERYIMAGES)) {
            model.getGalleryImages().stream().forEach(mc -> {
                MediaModel masterImage = getMasterImage(mc);
                if (masterImage != null) {
                    updateTagOnProduct(cloudinaryConfigModel.getCloudinaryURL(), model.getCode(), masterImage.getCloudinaryPublicId());
                }
            });
        }
    }

    private MediaModel getMasterImage(MediaContainerModel mediaContainerModel) {

        MediaModel masterMedia = null;
        Collection<MediaModel> medias = mediaContainerModel.getMedias();
        for (MediaModel mediaModel : medias) {
            if (mediaModel.getMediaFormat() == null && mediaModel.getCloudinaryURL() != null) {
                masterMedia = mediaModel;
            }
        }
        return masterMedia;
    }


    private void updateTagOnProduct(String cloudinaryUrl, String productCode, String publicId) {
        try {
            updateTagApiService.updateTagOnAsests(publicId, productCode, cloudinaryUrl);
        } catch (IOException e) {
            LOG.error("Error occured while updating tag ", e);
        }
    }
}
