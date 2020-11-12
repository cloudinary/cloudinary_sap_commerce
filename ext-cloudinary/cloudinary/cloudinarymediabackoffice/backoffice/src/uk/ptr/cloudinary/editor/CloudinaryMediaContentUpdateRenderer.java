package uk.ptr.cloudinary.editor;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Map;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractSection;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaComponentRenderer;

import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.response.UploadApiResponseData;
import uk.ptr.cloudinary.util.CloudinaryConfigUtils;


public class CloudinaryMediaContentUpdateRenderer extends AbstractEditorAreaComponentRenderer<AbstractSection, MediaModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryMediaContentUpdateRenderer.class);

    public static final String VERSION = "v";

    private AnnotateDataBinder binder;

    @Resource
    private ModelService modelService;

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Override
    public void render(Component component, AbstractSection abstractSection, MediaModel mediaModel, DataType dataType, WidgetInstanceManager widgetInstanceManager) {

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

        if(cloudinaryConfigModel != null && cloudinaryConfigModel.getEnableCloudinary()) {
            Textbox textbox = new Textbox();
            textbox.setVisible(false);
            Div uploadButtonDiv = new Div();
            final Button button = new Button("Upload Assets");

            uploadButtonDiv.appendChild(button);

            button.addEventListener(Events.ON_CLICK, (event) -> {
                onClickbutton(component, textbox, mediaModel, cloudinaryConfigModel.getCloudinaryCname());
            });
            component.appendChild(uploadButtonDiv);
            component.appendChild(textbox);
        }
    }

    public void onClickbutton(Component parent, Textbox textbox, MediaModel mediaModel, String cName){
        Window dialogWin = (Window) Executions.createComponents("widgets/cloudinaryuploadedit.zul", parent, null);
        binder = new AnnotateDataBinder(dialogWin);
        binder.loadAll();
        dialogWin.doModal();

        Button done = (Button)dialogWin.getFellow("done");
        done.addEventListener(Events.ON_CLICK, event -> {
            Textbox tx = (Textbox) dialogWin.getFellow(CloudinarymediacoreConstants.TEXT_FIELD);
            textbox.setValue(tx!= null ? tx.getValue() : "");
            populateMediaValues(textbox, mediaModel, cName);
        });

        dialogWin.addEventListener(Events.ON_CLOSE, event -> {
            Textbox tx = (Textbox) dialogWin.getFellow(CloudinarymediacoreConstants.TEXT_FIELD);
            textbox.setValue(tx!= null ? tx.getValue() : "");
            populateMediaValues(textbox, mediaModel, cName);
        });
    }

    private void populateMediaValues(final Textbox textbox, final MediaModel mediaModel, final String cName)
    {
        try {
            UploadApiResponseData responseData = getUploadApiResponseData(textbox);
            if(responseData != null) {
                if (StringUtils.isNotEmpty(cName))
                {   String updatedUrl = CloudinaryConfigUtils.updateMediaCloudinaryUrl(responseData.getSecure_url(), cName);
                mediaModel.setURL(updatedUrl);
                mediaModel.setCloudinaryURL(updatedUrl);
            }
                else {
                    mediaModel.setURL(responseData.getSecure_url());
                    mediaModel.setCloudinaryURL(responseData.getSecure_url());
                }
                mediaModel.setCloudinaryPublicId(responseData.getPublic_id());
                mediaModel.setCloudinaryResourceType(responseData.getResource_type());
                mediaModel.setCloudinaryType(responseData.getType());
                StringBuilder version = new StringBuilder();
                version.append(VERSION).append(responseData.getVersion());
                mediaModel.setCloudinaryVersion(version.toString());
                mediaModel.setCloudinaryMediaFormat(responseData.getFormat());
                modelService.save(mediaModel);
                modelService.refresh(mediaModel);
            }
        } catch (JsonProcessingException e) {
            LOG.error("Json parsing error save media", e);
        }
        catch (RuntimeException runtimeException) {
            LOG.error("Cannot save media", runtimeException);
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
        }
        catch (JsonProcessingException e) {
            LOG.error("Json parsing error save media", e);
        }
        return null;
    }
}