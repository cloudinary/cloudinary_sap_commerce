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
import uk.ptr.cloudinary.dao.CloudinaryProductDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.RemoveTagApiService;
import uk.ptr.cloudinary.service.UpdateTagApiService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

public class CloudinaryMediaContainerValidateInterceptor implements ValidateInterceptor<MediaContainerModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryMediaContainerValidateInterceptor.class);

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private RemoveTagApiService removeTagApiService;

    @Resource
    private UpdateTagApiService updateTagApiService;

    @Resource
    private CloudinaryProductDao cloudinaryProductDao;

    @Resource
    private ModelService modelService;

    @Override
    public void onValidate(MediaContainerModel model, InterceptorContext ctx) throws InterceptorException {

        if (model instanceof MediaContainerModel && !ctx.isNew(model) && ctx.isModified(model, MediaContainerModel.MEDIAS)) {

            final ItemModelContextImpl itemModelCtx = (ItemModelContextImpl) model.getItemModelContext();

            final Collection<MediaModel> oldValue = (Collection<MediaModel>) itemModelCtx.getValueHistory().getOriginalValue(MediaContainerModel.MEDIAS);

            final Collection<MediaModel> currentValue = model.getMedias();

            CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();
            ProductModel product = cloudinaryProductDao.getProductForMediaContainer(model.getPk().toString(), model.getCatalogVersion());

            MediaModel oldMasterMedia = !CollectionUtils.isEmpty(oldValue) ? getMasterMedia(oldValue) : null;

            MediaModel newMasterMedia = !CollectionUtils.isEmpty(currentValue) ? getMasterMedia(currentValue) : null;

            MediaModel masterMedia = model.getMaster();

            if(cloudinaryConfigModel.getEnableCloudinary() && oldMasterMedia != null && newMasterMedia == null){

                Collection<MediaModel> newMediaList =  new ArrayList<MediaModel>();

                if(!CollectionUtils.isEmpty(currentValue)) {
                    Optional<MediaModel> updatedMedia = currentValue.stream().filter(mc -> !oldMasterMedia.getCloudinaryPublicId().equals(mc.getCloudinaryPublicId())).findFirst();
                    if(updatedMedia.isPresent()){
                        masterMedia.setURL(updatedMedia.get().getURL());
                        masterMedia.setCloudinaryPublicId((updatedMedia.get().getCloudinaryPublicId()));
                        masterMedia.setCloudinaryResourceType((updatedMedia.get().getCloudinaryResourceType()));
                        masterMedia.setCloudinaryType((updatedMedia.get().getCloudinaryType()));
                        masterMedia.setCloudinaryVersion((updatedMedia.get().getCloudinaryVersion()));
                        masterMedia.setCloudinaryMediaFormat((updatedMedia.get().getCloudinaryMediaFormat()));
                        modelService.save(masterMedia);
                        modelService.refresh(masterMedia);
                    }
                    currentValue.stream().forEach(m -> {
                        m.setOriginal(masterMedia);
                        modelService.save(m);
                        newMediaList.add(m);
                    });
                }
                newMediaList.add(masterMedia);
                model.setMedias(newMediaList);
            }
            if (product != null) {
                if (oldMasterMedia != null && newMasterMedia != null && !oldMasterMedia.getCloudinaryPublicId().equalsIgnoreCase(newMasterMedia.getCloudinaryPublicId())) {
                    removeTagApiService.removeTagFromAsset(oldMasterMedia.getCloudinaryPublicId(), product.getCode(), cloudinaryConfigModel.getCloudinaryURL());
                    updateTagOnProduct(cloudinaryConfigModel.getCloudinaryURL(), product.getCode(), newMasterMedia);
                }
                else if(newMasterMedia == null && oldMasterMedia != null)
                {
                    removeTagApiService.removeTagFromAsset(oldMasterMedia.getCloudinaryPublicId(), product.getCode(), cloudinaryConfigModel.getCloudinaryURL());
                }
                else if(oldMasterMedia == null && newMasterMedia != null){
                    updateTagOnProduct(cloudinaryConfigModel.getCloudinaryURL(), product.getCode(), newMasterMedia);
                }
            }
        }
    }

    private MediaModel getMasterMedia(Collection<MediaModel> medias) {
        for (MediaModel mediaModel : medias) {
            if (mediaModel.getMediaFormat() == null && mediaModel.getCloudinaryURL() != null) {
                return mediaModel;
            }
        }
        return null;
    }

    private void updateTagOnProduct(String cloudinaryUrl, String productCode, MediaModel mediaModel) {
        try {
            updateTagApiService.updateTagOnAsests(mediaModel.getCloudinaryPublicId(), productCode, cloudinaryUrl, mediaModel.getCloudinaryResourceType());
        } catch (IOException e) {
            LOG.error("Error occured while updating tag for Media code  : " + mediaModel.getCode()  + "Asset public id" + mediaModel.getCloudinaryPublicId() + "productCode : " + productCode , e);
        }
    }

}
