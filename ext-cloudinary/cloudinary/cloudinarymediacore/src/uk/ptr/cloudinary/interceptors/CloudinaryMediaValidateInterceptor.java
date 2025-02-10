package uk.ptr.cloudinary.interceptors;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import uk.ptr.cloudinary.CloudinaryMasterMediaUtil;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.dao.CloudinaryMediaContainerDao;
import uk.ptr.cloudinary.dao.CloudinaryProductDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.RemoveTagApiService;
import uk.ptr.cloudinary.service.UpdateTagApiService;

import javax.annotation.Resource;
import java.io.IOException;

public class CloudinaryMediaValidateInterceptor implements ValidateInterceptor<MediaModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryMediaValidateInterceptor.class);
    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private RemoveTagApiService removeTagApiService;

    @Resource
    private UpdateTagApiService updateTagApiService;

    @Resource
    private CloudinaryProductDao cloudinaryProductDao;

    @Resource
    private CloudinaryMediaContainerDao cloudinaryMediaContainerDao;

    @Override
    public void onValidate(MediaModel model, InterceptorContext ctx) throws InterceptorException {

        if(model instanceof MediaModel && !ctx.isNew(model) && ctx.isModified(model, MediaModel.CLOUDINARYPUBLICID)) {
            final ItemModelContextImpl itemModelCtx = (ItemModelContextImpl) model.getItemModelContext();

            if (model.getMediaContainer()!= null && model.getMediaFormat() == null && model.getCloudinaryPublicId() != null) {
                final String oldValue = (String) itemModelCtx.getValueHistory().getOriginalValue(MediaModel.CLOUDINARYPUBLICID);
                final String currentValue = model.getCloudinaryPublicId();

                ProductModel product = cloudinaryProductDao.getProductForMediaContainer(model.getMediaContainer().getPk().toString(), model.getCatalogVersion());
                CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();
                if (!ObjectUtils.isEmpty(product)) {
                    if (StringUtils.isNotEmpty(oldValue) && !oldValue.equalsIgnoreCase(currentValue)) {
                        removeTagApiService.removeTagFromAsset(oldValue, product.getCode(), cloudinaryConfigModel.getCloudinaryURL());
                    }
                    updateTagOnProduct(cloudinaryConfigModel.getCloudinaryURL(), product.getCode(), currentValue, model.getCloudinaryResourceType());
                }
            }
        }
        }

    private void updateTagOnProduct(String cloudinaryUrl, String productCode, String publicId, String cloudinaryResourceType) {
        try {
            updateTagApiService.updateTagOnAsests(publicId, productCode, cloudinaryUrl, cloudinaryResourceType);
        } catch (IOException e) {
            LOG.error("Error occured while updating tag for Media Asset public id :" + publicId + "productCode : " + productCode , e);
        }
    }
}
