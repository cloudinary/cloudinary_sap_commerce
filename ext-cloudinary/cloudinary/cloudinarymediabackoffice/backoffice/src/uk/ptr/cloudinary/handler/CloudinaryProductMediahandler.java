package uk.ptr.cloudinary.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectDeletionException;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;
import com.hybris.cockpitng.widgets.configurableflow.ComposedFlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
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
import uk.ptr.cloudinary.service.UpdateTagApiService;
import uk.ptr.cloudinary.util.CloudinaryConfigUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class CloudinaryProductMediahandler extends ComposedFlowActionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryProductMediahandler.class);

    public static final String CLOUDINARY_PRODUCT_MEDIA = "cloudinaryProductContentProperty";
    public static final String NEW_PRODUCT = "cloudinaryProductProperty";
    public static final String VERSION = "v";

    @Resource
    private UpdateTagApiService updateTagApiService;
    @Resource
    private ModelService modelService;
    @Resource
    private ObjectFacade objectFacade;
    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;
    @Resource
    private NotificationService notificationService;


    @Override
    public void perform(CustomType customType, FlowActionHandlerAdapter adapter, Map<String, String> map) {

        ProductModel mediaToUpdate = this.getProductMediaToUpdate(adapter, map);
        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();
        if (map.containsKey(CLOUDINARY_PRODUCT_MEDIA) && cloudinaryConfigModel != null && BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary())) {
            UploadApiResponseData responseData = new UploadApiResponseData();
            final Textbox textField = (Textbox) adapter.getWidgetInstanceManager().getWidgetslot().getFellow(CloudinarymediacoreConstants.TEXT_FIELD);
            try {
                responseData = getUploadApiResponseData(textField);
            } catch (JsonProcessingException e) {
                LOG.error("Json parsing error save media", e);
            }
            tryToSave(adapter, map, responseData, mediaToUpdate, cloudinaryConfigModel);
            saveMediaObject(adapter, map, mediaToUpdate);
            adapter.done();
        } else {
            super.perform(customType, adapter, map);
        }

    }

    private void tryToSave(FlowActionHandlerAdapter adapter, Map<String, String> map, UploadApiResponseData responseData, ProductModel productModel, CloudinaryConfigModel cloudinaryConfigModel) {
        String updatedUrl = "";
        try {
            boolean update = !this.modelService.isNew(productModel);
            if (responseData != null) {
                if (StringUtils.isNotEmpty(cloudinaryConfigModel.getCloudinaryCname())) {
                    updatedUrl = CloudinaryConfigUtils.updateMediaCloudinaryUrl(responseData.getSecure_url(), cloudinaryConfigModel.getCloudinaryCname());
                }
                MediaContainerModel mediaContainerModel = createMasterMedia(productModel, updatedUrl, responseData, cloudinaryConfigModel.getCloudinaryURL());
                productModel.setGalleryImages(Collections.singletonList(mediaContainerModel));

            }
            this.objectFacade.save(productModel);
            this.notifyAboutSuccess(productModel);
        } catch (RuntimeException | ObjectSavingException var6) {
            LOG.error("Cannot save media", var6);
            this.rollback(adapter, map, productModel);
            throw new ModelSavingException(var6.getMessage(), var6);
        }

    }

    protected void saveMediaObject(FlowActionHandlerAdapter adapter, Map<String, String> map, ProductModel mediaToUpdate) {
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

    protected ProductModel getProductMediaToUpdate(FlowActionHandlerAdapter adapter, Map<String, String> params) {
        String mediaProperty = (String) params.get(NEW_PRODUCT);
        if (StringUtils.isNotEmpty(mediaProperty)) {
            return (ProductModel) adapter.getWidgetInstanceManager().getModel().getValue(mediaProperty, ProductModel.class);
        } else {
            LOG.warn("Missing {} param which specifies media to update", "media");
            return null;
        }
    }


    protected void rollback(FlowActionHandlerAdapter adapter, Map<String, String> params, ProductModel mediaToUpdate) {
        try {
            this.setMediaToUpdate(adapter, params, (ProductModel) this.modelService.clone(mediaToUpdate));
            this.objectFacade.delete(mediaToUpdate);
        } catch (ObjectDeletionException var5) {
            LOG.trace("Cannot remove or clone media", var5);
        }

    }

    protected void setMediaToUpdate(FlowActionHandlerAdapter adapter, Map<String, String> params, ProductModel mediaModel) {
        String mediaProperty = (String) params.get(CLOUDINARY_PRODUCT_MEDIA);
        if (StringUtils.isNotEmpty(mediaProperty)) {
            adapter.getWidgetInstanceManager().getModel().setValue(mediaProperty, mediaModel);
        } else {
            LOG.warn("Missing {} param which specifies media to update", "mediaProperty");
        }

    }

    protected void notifyAboutSuccess(ProductModel model) {
        this.notificationService.notifyUser("notification-area", "CreateObject", NotificationEvent.Level.SUCCESS, new Object[]{model});
    }

    private MediaContainerModel createMasterMedia(ProductModel productModel, String updatedUrl, UploadApiResponseData responseData, String cloudinaryUrl) {
        MediaModel mediaModel = this.modelService.create(MediaModel.class);
        //mediaModel.setCloudinaryURL(StringUtils.isNotEmpty(updatedUrl) ? updatedUrl : responseData.getSecure_url());
        mediaModel.setURL(StringUtils.isNotEmpty(updatedUrl) ? updatedUrl : responseData.getSecure_url());
        mediaModel.setCode(UUID.randomUUID().toString());
        mediaModel.setCatalogVersion(productModel.getCatalogVersion());
        mediaModel.setCloudinaryPublicId(responseData.getPublic_id());
        mediaModel.setCloudinaryResourceType(responseData.getResource_type());
        mediaModel.setCloudinaryType(responseData.getType());
        StringBuilder version = new StringBuilder();
        version.append(VERSION).append(responseData.getVersion());
        mediaModel.setCloudinaryVersion(version.toString());
        mediaModel.setCloudinaryMediaFormat(responseData.getFormat());

        MediaContainerModel mediaContainerModel = this.modelService.create(MediaContainerModel.class);
        mediaContainerModel.setQualifier(UUID.randomUUID().toString());
        mediaContainerModel.setCatalogVersion(productModel.getCatalogVersion());
        mediaContainerModel.setMedias(Collections.singletonList(mediaModel));

        modelService.saveAll(mediaModel,mediaContainerModel);

        updateTagOnProduct(cloudinaryUrl, productModel.getCode(), mediaModel);
        return mediaContainerModel;
    }

    private void updateTagOnProduct(String cloudinaryUrl, String productCode, MediaModel mediaModel) {
        try {
            updateTagApiService.updateTagOnAsests(mediaModel.getCloudinaryPublicId(), productCode, cloudinaryUrl,mediaModel.getCloudinaryResourceType());
        } catch (IOException e) {
            LOG.error("Error occured while updating tag for Media code  : " + mediaModel.getCode()  + "Asset public id" + mediaModel.getCloudinaryPublicId() + "productCode : " + productCode , e);
        }
    }
}
