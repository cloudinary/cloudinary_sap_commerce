package uk.ptr.cloudinary.interceptors;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import uk.ptr.cloudinary.CloudinaryMasterMediaUtil;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.RemoveTagApiService;
import uk.ptr.cloudinary.service.UpdateTagApiService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CloudinaryProductValidateInterceptor implements ValidateInterceptor<ProductModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryProductValidateInterceptor.class);

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private ModelService modelService;

    @Resource
    private UpdateTagApiService updateTagApiService;

    @Resource
    private RemoveTagApiService removeTagApiService;

    @Override
    public void onValidate(ProductModel model, InterceptorContext ctx) throws InterceptorException {

        if (model instanceof ProductModel && !ctx.isNew(model) && ctx.isModified(model, ProductModel.GALLERYIMAGES)) {

            final ItemModelContextImpl itemModelCtx = (ItemModelContextImpl) model.getItemModelContext();

            final List<MediaContainerModel> oldValue = (List<MediaContainerModel>) itemModelCtx.getValueHistory().getOriginalValue(ProductModel.GALLERYIMAGES);
            final List<MediaContainerModel> currentValue = model.getGalleryImages();

            CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();
            List<MediaContainerModel> removedMediaContainer = new ArrayList<>();
            List<MediaContainerModel> addedMediaContainer = new ArrayList<>();

            if(!CollectionUtils.isEmpty(oldValue) && CollectionUtils.isEmpty(currentValue))
            {
                oldValue.stream().forEach(mc -> {
                    MediaModel masterImage = CloudinaryMasterMediaUtil.getMasterImage(mc);
                    if (masterImage != null) {
                        removeTagApiService.removeTagFromAsset(masterImage.getCloudinaryPublicId(), model.getCode(), cloudinaryConfigModel.getCloudinaryURL());
                    }
                });
            }
            else if(CollectionUtils.isEmpty(oldValue) && !CollectionUtils.isEmpty(currentValue)){
                currentValue.stream().forEach(mc -> {
                    MediaModel masterImage = CloudinaryMasterMediaUtil.getMasterImage(mc);
                    if (masterImage != null) {
                        updateTagOnProduct(cloudinaryConfigModel.getCloudinaryURL(), model.getCode(), masterImage);
                    }
                });
            }
            else if (!CollectionUtils.isEmpty(oldValue) && !CollectionUtils.isEmpty(currentValue)) {
                oldValue.stream().forEach(o -> {
                    if (!currentValue.contains(o)) {
                        removedMediaContainer.add(o);
                    }
                });
                if (!ObjectUtils.isEmpty(removedMediaContainer)) {
                    removedMediaContainer.stream().forEach(mc -> {
                        MediaModel masterImage = CloudinaryMasterMediaUtil.getMasterImage(mc);
                        if (masterImage != null) {
                            removeTagApiService.removeTagFromAsset(masterImage.getCloudinaryPublicId(), model.getCode(), cloudinaryConfigModel.getCloudinaryURL());
                        }
                    });
                }
                currentValue.stream().forEach(n -> {
                    if (!oldValue.contains(n)) {
                        addedMediaContainer.add(n);
                    }
                });
                if (!ObjectUtils.isEmpty(addedMediaContainer)) {
                    addedMediaContainer.stream().forEach(mc -> {
                        MediaModel masterImage = CloudinaryMasterMediaUtil.getMasterImage(mc);
                        if (masterImage != null) {
                            updateTagOnProduct(cloudinaryConfigModel.getCloudinaryURL(), model.getCode(), masterImage);
                        }
                    });
                }
            }

        }
    }

    private void updateTagOnProduct(String cloudinaryUrl, String productCode, MediaModel mediaModel) {
        try {
            updateTagApiService.updateTagOnAsests(mediaModel.getCloudinaryPublicId(), productCode, cloudinaryUrl,mediaModel.getCloudinaryResourceType());
        } catch (IOException e) {
            LOG.error("Error occured while updating tag for Media code  : " + mediaModel.getCode()  + "Asset public id" + mediaModel.getCloudinaryPublicId() + "productCode : " + productCode , e);
        }
    }

}
