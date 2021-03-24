package uk.ptr.cloudinary.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.backoffice.wizard.MediaContentUpdateHandler;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectDeletionException;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Textbox;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.response.UploadApiResponseData;
import uk.ptr.cloudinary.util.CloudinaryConfigUtils;

import javax.annotation.Resource;
import java.util.Map;


public class CloudinaryMediaContentUpdateHandler extends MediaContentUpdateHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryMediaContentUpdateHandler.class);

    public static final String MEDIA_CONTENT_PROPERTY = "mediaContentProperty";
    public static final String MEDIA_PROPERTY = "mediaProperty";
    public static final String CLOUDINARY_MEDIA = "media";


    @Resource
    private MediaService mediaService;
    @Resource
    private ModelService modelService;
    @Resource
    private ObjectFacade objectFacade;
    @Resource
    private NotificationService notificationService;
    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    public CloudinaryMediaContentUpdateHandler() {
    }

    @Override
    public void perform(CustomType customType, FlowActionHandlerAdapter adapter, Map<String, String> map) {

        MediaModel mediaToUpdate = this.getMediaToUpdate(adapter, map);
        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

        if (map.containsKey(CLOUDINARY_MEDIA) && cloudinaryConfigModel != null && BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary())) {
            UploadApiResponseData responseData = new UploadApiResponseData();
            final Textbox textField = (Textbox) adapter.getWidgetInstanceManager().getWidgetslot().getFellow(CloudinarymediacoreConstants.TEXT_FIELD);
            try {
                responseData = getUploadApiResponseData(textField);
            } catch (JsonProcessingException e) {
                LOG.error("Json parsing error save media", e);
            }
            tryToSave(adapter, map, responseData, mediaToUpdate, cloudinaryConfigModel.getCloudinaryCname());
            saveMediaObject(adapter, map, mediaToUpdate);
            adapter.done();
        } else {
            super.perform(customType, adapter, map);
        }

    }

    private void tryToSave(FlowActionHandlerAdapter adapter, Map<String, String> map, UploadApiResponseData responseData, MediaModel mediaToUpdate, String cloudinaryCname) {

        try {
            boolean update = !this.modelService.isNew(mediaToUpdate);
            if (responseData == null) {
                if (update) {
                    this.mediaService.removeDataFromMedia(mediaToUpdate);
                }
            } else {
                if (StringUtils.isNotEmpty(cloudinaryCname)) {
                    String updatedUrl = CloudinaryConfigUtils.updateMediaCloudinaryUrl(responseData.getSecure_url(), cloudinaryCname);
                    mediaToUpdate.setURL(updatedUrl);
                    //mediaToUpdate.setCloudinaryURL(updatedUrl);
                } else {
                    mediaToUpdate.setURL(responseData.getSecure_url());
                    //mediaToUpdate.setCloudinaryURL(responseData.getSecure_url());
                }
                mediaToUpdate.setCloudinaryPublicId(responseData.getPublic_id());
                mediaToUpdate.setCloudinaryResourceType(responseData.getResource_type());
                mediaToUpdate.setCloudinaryType(responseData.getType());
                StringBuilder version = new StringBuilder();
                version.append("v").append(responseData.getVersion());
                mediaToUpdate.setCloudinaryVersion(version.toString());
                mediaToUpdate.setCloudinaryMediaFormat(responseData.getFormat());
            }
            this.objectFacade.save(mediaToUpdate);
            this.notifyAboutSuccess(mediaToUpdate);
        } catch (RuntimeException | ObjectSavingException var6) {
            LOG.error("Cannot save media", var6);
            this.rollback(adapter, map, mediaToUpdate);
            throw new ModelSavingException(var6.getMessage(), var6);
        }

    }

    private UploadApiResponseData getUploadApiResponseData(Textbox textField) throws JsonProcessingException {
        try {
            if (StringUtils.isNotEmpty(textField.getValue())) {
                final ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> response = mapper.readValue(textField.getValue(), new TypeReference<Map<String, Object>>() {
                });
                return mapper.convertValue(response, UploadApiResponseData.class);
            }
        } catch (JsonProcessingException e) {
            LOG.error("Json parsing error save media", e);
        }
        return null;
    }

    protected void saveMediaObject(FlowActionHandlerAdapter adapter, Map<String, String> map, MediaModel mediaToUpdate) {
        try {
            boolean update = !this.modelService.isNew(mediaToUpdate);
            this.objectFacade.save(mediaToUpdate);
            this.notifyAboutSuccess(mediaToUpdate);
        } catch (RuntimeException | ObjectSavingException var6) {
            LOG.error("Cannot save media", var6);
            this.rollback(adapter, map, mediaToUpdate);
            throw new ModelSavingException(var6.getMessage(), var6);
        }
    }

    @Override
    protected void rollback(FlowActionHandlerAdapter adapter, Map<String, String> params, MediaModel mediaToUpdate) {
        try {
            this.setMediaToUpdate(adapter, params, (MediaModel) this.modelService.clone(mediaToUpdate));
            this.objectFacade.delete(mediaToUpdate);
        } catch (ObjectDeletionException var5) {
            LOG.trace("Cannot remove or clone media", var5);
        }

    }

    @Override
    protected void notifyAboutSuccess(MediaModel model) {
        this.notificationService.notifyUser("notification-area", "CreateObject", Level.SUCCESS, new Object[]{model});
    }

    @Override
    protected MediaModel getMediaToUpdate(FlowActionHandlerAdapter adapter, Map<String, String> params) {
        String mediaProperty = (String) params.get(MEDIA_PROPERTY);
        if (StringUtils.isNotEmpty(mediaProperty)) {
            return (MediaModel) adapter.getWidgetInstanceManager().getModel().getValue(mediaProperty, MediaModel.class);
        } else {
            LOG.warn("Missing {} param which specifies media to update", "media");
            return null;
        }
    }



    @Override
    protected void setMediaToUpdate(FlowActionHandlerAdapter adapter, Map<String, String> params, MediaModel mediaModel) {
        String mediaProperty = (String) params.get(MEDIA_PROPERTY);
        if (StringUtils.isNotEmpty(mediaProperty)) {
            adapter.getWidgetInstanceManager().getModel().setValue(mediaProperty, mediaModel);
        } else {
            LOG.warn("Missing {} param which specifies media to update", "mediaProperty");
        }

    }
}
