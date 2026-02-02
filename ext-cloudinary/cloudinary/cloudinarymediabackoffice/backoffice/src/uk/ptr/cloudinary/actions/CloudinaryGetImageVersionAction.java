package uk.ptr.cloudinary.actions;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryGetImageVersionAction implements CockpitAction<MediaModel, Object> {
    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryGetImageVersionAction.class);

    @Resource
    private ModelService modelService;

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Override
    public ActionResult<Object> perform(ActionContext<MediaModel> ctx) {
        MediaModel media = (MediaModel) ctx.getData();
        Cloudinary client = new Cloudinary(cloudinaryConfigDao.getCloudinaryConfigModel().getCloudinaryURL());
        Map<String, Object> params = new HashMap<>();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieving image version for media: {}", media.getCode());
        }
        try {
            params.put("resource_type", media.getCloudinaryResourceType());
            params.put("type", media.getCloudinaryType());
            ApiResponse response = client.api().resource(media.getCloudinaryPublicId(), params);
            Integer version = (Integer) response.get("version");
            media.setCloudinaryVersion(version.toString());
            modelService.save(media);
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Error retrieving image version for media: {}", media.getCode(), e);
            }
            return new ActionResult<>(ActionResult.ERROR, e.getMessage());
        }
        return new ActionResult<>(ActionResult.SUCCESS);
    }

    @Override
    public boolean canPerform(ActionContext<MediaModel> ctx) {
        MediaModel media = (MediaModel) ctx.getData();
        String mediaPublicId = media.getCloudinaryPublicId();
        return mediaPublicId != null;
    }

    @Override
    public boolean needsConfirmation(ActionContext<MediaModel> ctx) {
        MediaModel media = (MediaModel) ctx.getData();
        return media.getCatalogVersion().getVersion().equals("Online");
    }

    @Override
    public String getConfirmationMessage(ActionContext<MediaModel> ctx) {
        return "You are about to update the image version on the item in Online catalog version, are you sure?";
    }

}
