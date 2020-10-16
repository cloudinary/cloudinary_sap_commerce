package uk.ptr.cloudinary.services;

import com.cloudinary.api.ApiResponse;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractSection;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaComponentRenderer;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.AdminApiService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfigAdminUsageRenderer extends AbstractEditorAreaComponentRenderer<AbstractSection, CloudinaryConfigModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryConfigAdminUsageRenderer.class);

    @Resource
    private AdminApiService adminApiService;

    @Resource
    private ModelService modelService;


    public CloudinaryConfigAdminUsageRenderer() {
    }

    protected String setConnectionDetailsOnDiv(CloudinaryConfigModel cloudinaryConfigModel, Div newValueContainer, Label label, Html html, Hbox boxHeader) throws Exception {

        ApiResponse response = null;

        try {
            response = adminApiService.getCloudinaryPlanInfo(cloudinaryConfigModel);
        }
        catch (IllegalArgumentException illegalException) {
            LOG.error("Illegal Argument " + illegalException.getMessage());
            return illegalException.getMessage();
        }
        catch (Exception e) {
            LOG.error("Exception occured calling Admin Usage API " + e.getMessage());
            return e.getMessage();
        }
        if (response != null) {
            return setUsageResponseData(newValueContainer, label, html, boxHeader, response);
        }
        return Boolean.FALSE.toString();
    }

    private String setUsageResponseData(Div newValueContainer, Label label, Html html, Hbox boxHeader, ApiResponse response) {
        Map<String, Integer> storageUsage = new HashMap<>();
        Map<String, Integer> banditUsages = new HashMap<>();
        Map<String, Integer> transformationUsages = new HashMap<>();

        storageUsage = (Map<String, Integer>) response.get("bandwidth");
        banditUsages = (Map<String, Integer>) response.get("bandwidth");
        transformationUsages = (Map<String, Integer>) response.get("bandwidth");

        label.setValue(CloudinarymediacoreConstants.CONNECTED);
        label.setSclass("yw-labelstyle-z-label");
        boxHeader.appendChild(label);

        String usagesData = CloudinarymediacoreConstants.STORAGE_USUAGE + storageUsage.get("usage") + CloudinarymediacoreConstants.BANDWIDTH_USUAGE + banditUsages.get("usage") + CloudinarymediacoreConstants.TRANSFORMATION_USUAGE + transformationUsages.get("usage");

        html.setContent(usagesData);
        html.setSclass("yw-editorarea-z-html");
        newValueContainer.setSclass("yw-editorarea-z-div");
        newValueContainer.appendChild(boxHeader);
        newValueContainer.appendChild(html);
        return Boolean.TRUE.toString();
    }

    @Override
    public void render(Component component, AbstractSection abstractSectionConfiguration, CloudinaryConfigModel cloudinaryConfigModel, DataType dataType, WidgetInstanceManager widgetInstanceManager) {

        Div usageResponseDiv = new Div();
        final Div enableCloudinaryRadioDiv = new Div();
        final Div radioDiv = new Div();
        final Html nextHtml = new Html();
        final Html html = new Html();
        Hbox boxHeader = new Hbox();

        nextHtml.setContent("&nbsp;<br>");

        Label enableCloudinaryFieldName = new Label(CloudinarymediacoreConstants.ENABLE_CLOUDINARY);
        Label cloudinaryConnectionLabel = new Label();
        Label connectionErrorMessage = new Label("");

        UITools.modifySClass(enableCloudinaryFieldName, "yw-enablelabelstyle", true);
        UITools.modifySClass(cloudinaryConnectionLabel, "yw-labelstyle-z-label", true);
        UITools.modifySClass(connectionErrorMessage, "yw-error-connection-labelstyle-z-label", true);

        enableCloudinaryRadioDiv.appendChild(enableCloudinaryFieldName);

        Radio trueCheck = new Radio();
        trueCheck.setLabel("True");


        Radio falseCheck = new Radio();
        falseCheck.setLabel("False");

        if (cloudinaryConfigModel.getEnableCloudinary()) {
            trueCheck.setChecked(Boolean.TRUE);
            falseCheck.setChecked(Boolean.FALSE);
        } else {
            trueCheck.setChecked(Boolean.FALSE);
            falseCheck.setChecked(Boolean.TRUE);
        }

        trueCheck.setParent(component);
        falseCheck.setParent(component);

        radioDiv.appendChild(trueCheck);
        radioDiv.appendChild(falseCheck);
        usageResponseDiv.appendChild(enableCloudinaryRadioDiv);
        usageResponseDiv.appendChild(radioDiv);
        usageResponseDiv.appendChild(nextHtml);

        if (cloudinaryConfigModel.getEnableCloudinary()) {
            try {
                setConnectionDetailsOnDiv(cloudinaryConfigModel, usageResponseDiv, cloudinaryConnectionLabel, html, boxHeader);
            } catch (IllegalArgumentException illegalException) {
                LOG.error("Illegal Argument " + illegalException.getMessage());
            } catch (Exception e) {
                LOG.error("Exception occured calling Admin Usage API " + e.getMessage());
            }
        } else {
            cloudinaryConnectionLabel.setValue(CloudinarymediacoreConstants.NOT_CONNECTED);
        }

        trueCheck.addEventListener(Events.ON_CLICK, (event) -> {
            modelService.refresh(cloudinaryConfigModel);
            String response = setConnectionDetailsOnDiv(cloudinaryConfigModel, usageResponseDiv, cloudinaryConnectionLabel, html, boxHeader);
            if (response.equalsIgnoreCase("true")) {
                falseCheck.setChecked(Boolean.FALSE);
                cloudinaryConfigModel.setEnableCloudinary(true);
                connectionErrorMessage.setValue("");
                modelService.save(cloudinaryConfigModel);
            } else {
                falseCheck.setChecked(Boolean.TRUE);
                trueCheck.setChecked(Boolean.FALSE);
                cloudinaryConnectionLabel.setValue(CloudinarymediacoreConstants.NOT_CONNECTED);
                connectionErrorMessage.setValue(response);
                html.setContent("&nbsp;");
                boxHeader.appendChild(cloudinaryConnectionLabel);
            }
        });

        falseCheck.addEventListener(Events.ON_CLICK, (event) -> {
            modelService.refresh(cloudinaryConfigModel);
            trueCheck.setChecked(Boolean.FALSE);
            cloudinaryConnectionLabel.setValue(CloudinarymediacoreConstants.NOT_CONNECTED);
            html.setContent("");
            boxHeader.appendChild(cloudinaryConnectionLabel);
            connectionErrorMessage.setValue("");

            cloudinaryConfigModel.setEnableCloudinary(false);
            modelService.save(cloudinaryConfigModel);
        });

        usageResponseDiv.appendChild(boxHeader);
        usageResponseDiv.appendChild(html);
        usageResponseDiv.appendChild(connectionErrorMessage);
        usageResponseDiv.setParent(component);

    }

}

