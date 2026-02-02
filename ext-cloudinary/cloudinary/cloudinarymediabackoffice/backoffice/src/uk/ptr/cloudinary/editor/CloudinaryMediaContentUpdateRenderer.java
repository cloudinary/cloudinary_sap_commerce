package uk.ptr.cloudinary.editor;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionDefinition;
import com.hybris.cockpitng.actions.ActionResult;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;

//import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractSection;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaComponentRenderer;

import uk.ptr.cloudinary.actions.CloudinaryGetImageVersionAction;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.dao.CloudinaryProductDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.response.UploadApiResponseData;
import uk.ptr.cloudinary.service.RemoveTagApiService;
import uk.ptr.cloudinary.service.UpdateTagApiService;
import uk.ptr.cloudinary.service.UploadApiService;
import uk.ptr.cloudinary.util.CloudinaryConfigUtils;


public class CloudinaryMediaContentUpdateRenderer extends AbstractEditorAreaComponentRenderer<AbstractSection, MediaModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryMediaContentUpdateRenderer.class);

    public static final String VERSION = "v";

    //private AnnotateDataBinder binder;

    @Resource
    private ModelService modelService;

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private RemoveTagApiService removeTagApiService;

    @Resource
    private CloudinaryProductDao cloudinaryProductDao;

    @Resource
    private UpdateTagApiService updateTagApiService;

    @Resource
    private UploadApiService uploadApiService;

    @Resource
    private CloudinaryGetImageVersionAction imageVersionAction;

    @Override
    public void render(Component component, AbstractSection abstractSection, MediaModel mediaModel, DataType dataType, WidgetInstanceManager widgetInstanceManager) {

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

        if (cloudinaryConfigModel != null && cloudinaryConfigModel.getEnableCloudinary()) {
            Textbox textbox = new Textbox();
            textbox.setVisible(false);
            Div uploadButtonDiv = new Div();
            final Button button = new Button("Select Asset");

            uploadButtonDiv.appendChild(button);

            button.addEventListener(Events.ON_CLICK, (event) -> {
                onClickbutton(component, textbox, mediaModel, cloudinaryConfigModel);
            });
            component.appendChild(uploadButtonDiv);
            component.appendChild(textbox);

//            Image Version section
            ActionContext<MediaModel> ctx = new ActionContext<>(mediaModel, new ActionDefinition(), Collections.emptyMap(), Collections.emptyMap());
            if (imageVersionAction.canPerform(ctx)) {
                Button versionBtn = new Button("Update Image Version Nr");
                versionBtn.setStyle("margin-left: 15px;");
                versionBtn.addEventListener(Events.ON_CLICK, evt -> {
                    if (imageVersionAction.needsConfirmation(ctx)) {
                        String msg = imageVersionAction.getConfirmationMessage(ctx);
                        Messagebox.show(
                                msg,
                                "Confirm",
                                Messagebox.OK | Messagebox.CANCEL,
                                Messagebox.QUESTION,
                                (evt2) -> {
                                    if (Messagebox.ON_OK.equals(evt2.getName())) {
                                        imageVersionAction.perform(ctx);
                                    }
                                });
                    } else {
                        imageVersionAction.perform(ctx);
                    }
                });
                component.appendChild(versionBtn);
            }
        }
    }

    public void onClickbutton(Component parent, Textbox textbox, MediaModel mediaModel, CloudinaryConfigModel cloudinaryConfigModel) {
        Window dialogWin = (Window) Executions.createComponents("widgets/cloudinaryuploadedit.zul", parent, null);
       // binder = new AnnotateDataBinder(dialogWin);
        //binder.loadAll();
        dialogWin.doModal();

        Button done = (Button) dialogWin.getFellow("done");
        done.addEventListener(Events.ON_CLICK, event -> {
            Textbox tx = (Textbox) dialogWin.getFellow(CloudinarymediacoreConstants.TEXT_FIELD);
            textbox.setValue(tx != null ? tx.getValue() : "");
            populateMediaValues(textbox, mediaModel, cloudinaryConfigModel);
        });

        dialogWin.addEventListener(Events.ON_CLOSE, event -> {
            Textbox tx = (Textbox) dialogWin.getFellow(CloudinarymediacoreConstants.TEXT_FIELD);
            textbox.setValue(tx != null ? tx.getValue() : "");
            populateMediaValues(textbox, mediaModel, cloudinaryConfigModel);
        });
    }

    private void populateMediaValues(final Textbox textbox, final MediaModel mediaModel, final CloudinaryConfigModel cloudinaryConfigModel) {
        try {
            UploadApiResponseData responseData = getUploadApiResponseData(textbox);
            if (responseData != null) {
                ProductModel productModel = null;
                if (mediaModel.getMediaContainer() != null) {
                    productModel = cloudinaryProductDao.getProductForMediaContainer(mediaModel.getMediaContainer().getPk().toString(), mediaModel.getCatalogVersion());
                }
                if (StringUtils.isNotEmpty(cloudinaryConfigModel.getCloudinaryCname())) {
                    String updatedUrl = CloudinaryConfigUtils.updateMediaCloudinaryUrl(uploadApiService.getSecureUrl(responseData), cloudinaryConfigModel.getCloudinaryCname());
                    mediaModel.setURL(updatedUrl);
                } else {
                    mediaModel.setURL(uploadApiService.getSecureUrl(responseData));
                }
                mediaModel.setCloudinaryPublicId(responseData.getPublic_id());
                mediaModel.setCloudinaryResourceType(responseData.getResource_type());
                mediaModel.setCloudinaryType(responseData.getType());
                mediaModel.setCloudinaryVersion(responseData.getVersion());
                mediaModel.setCloudinaryMediaFormat(responseData.getFormat());
                mediaModel.setCloudinaryTransformation(uploadApiService.getTransformationForDerivedMedia(responseData));
                modelService.save(mediaModel);
                modelService.refresh(mediaModel);
                if (productModel != null && mediaModel.getMediaFormat() == null && mediaModel.getCloudinaryURL()!= null) {
                    updateTagOnProduct(cloudinaryConfigModel.getCloudinaryURL(), productModel.getCode(), mediaModel);
                }
            }
        } catch (JsonProcessingException e) {
            LOG.error("Json parsing error save media", e);
        } catch (RuntimeException runtimeException) {
            LOG.error("Cannot save media", runtimeException);
        }
    }

    private void updateTagOnProduct(String cloudinaryUrl, String productCode, MediaModel mediaModel) {
        try {
            updateTagApiService.updateTagOnAsests(mediaModel.getCloudinaryPublicId(), productCode, cloudinaryUrl,mediaModel.getCloudinaryResourceType());
        } catch (IOException e) {
            LOG.error("Error occured while updating tag for Media code  : " + mediaModel.getCode() + "Asset public id" + mediaModel.getCloudinaryPublicId() + "productCode : " + productCode, e);
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
}