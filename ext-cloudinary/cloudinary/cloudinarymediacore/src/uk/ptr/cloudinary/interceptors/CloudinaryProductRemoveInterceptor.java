package uk.ptr.cloudinary.interceptors;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import org.springframework.util.CollectionUtils;
import uk.ptr.cloudinary.CloudinaryMasterMediaUtil;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.RemoveTagApiService;

import javax.annotation.Resource;
import java.util.List;

public class CloudinaryProductRemoveInterceptor implements RemoveInterceptor {

    @Resource
    RemoveTagApiService removeTagApiService;

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Override
    public void onRemove(Object model, InterceptorContext interceptorContext) throws InterceptorException {

        if (model instanceof ProductModel) {
            final ProductModel productModel = (ProductModel) model;

            List<MediaContainerModel> mediaContainerModelList = productModel.getGalleryImages();

            if (!CollectionUtils.isEmpty(mediaContainerModelList)) {
                mediaContainerModelList.stream().forEach(mc -> {
                    MediaModel masterImage = CloudinaryMasterMediaUtil.getMasterImage(mc);
                    if (masterImage != null) {
                        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();
                        removeTagApiService.removeTagFromAsset(masterImage.getCloudinaryPublicId(), productModel.getCode(), cloudinaryConfigModel.getCloudinaryURL());
                    }
                });
            }
        }
    }
}
