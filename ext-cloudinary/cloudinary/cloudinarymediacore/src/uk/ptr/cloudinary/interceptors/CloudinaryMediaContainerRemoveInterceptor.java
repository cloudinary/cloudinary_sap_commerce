package uk.ptr.cloudinary.interceptors;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import org.springframework.util.ObjectUtils;
import uk.ptr.cloudinary.CloudinaryMasterMediaUtil;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.dao.CloudinaryProductDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.RemoveTagApiService;

import javax.annotation.Resource;

public class CloudinaryMediaContainerRemoveInterceptor implements RemoveInterceptor {

    @Resource
    RemoveTagApiService removeTagApiService;

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private CloudinaryProductDao cloudinaryProductDao;

    @Override
    public void onRemove(Object model, InterceptorContext interceptorContext) throws InterceptorException {

        if (model instanceof MediaContainerModel) {
            final MediaContainerModel mediaContainerModel = (MediaContainerModel) model;
            MediaModel masterImage = CloudinaryMasterMediaUtil.getMasterImage(mediaContainerModel);
            if (masterImage != null) {
                ProductModel product = cloudinaryProductDao.getProductForMediaContainer(mediaContainerModel.getPk().toString(), mediaContainerModel.getCatalogVersion());
                if (!ObjectUtils.isEmpty(product)) {
                    CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();
                    removeTagApiService.removeTagFromAsset(masterImage.getCloudinaryPublicId(), product.getCode(), cloudinaryConfigModel.getCloudinaryURL());
                }
            }

        }
    }
}
