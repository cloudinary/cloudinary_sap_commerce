package uk.ptr.cloudinary.interceptors;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import uk.ptr.cloudinary.CloudinaryMasterMediaUtil;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.dao.CloudinaryMediaContainerDao;
import uk.ptr.cloudinary.dao.CloudinaryProductDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.RemoveTagApiService;

import javax.annotation.Resource;
import java.util.List;

public class CloudinaryMediaRemoveInterceptor implements RemoveInterceptor<MediaModel> {

    @Resource
    RemoveTagApiService removeTagApiService;

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private CloudinaryProductDao cloudinaryProductDao;

    @Resource
    private CloudinaryMediaContainerDao cloudinaryMediaContainerDao;


    @Override
    public void onRemove(MediaModel mediaModel, InterceptorContext interceptorContext) throws InterceptorException {

        if (mediaModel.getMediaContainer()!= null && mediaModel.getMediaFormat() == null && mediaModel.getCloudinaryURL() != null) {
            ProductModel product = cloudinaryProductDao.getProductForMediaContainer(mediaModel.getMediaContainer().getPk().toString(), mediaModel.getCatalogVersion());
            if (!ObjectUtils.isEmpty(product)) {
                CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();
                removeTagApiService.removeTagFromAsset(mediaModel.getCloudinaryPublicId(), product.getCode(), cloudinaryConfigModel.getCloudinaryURL());
            }
        }
    }
}


