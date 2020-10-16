package uk.ptr.cloudinary.services;

import com.cloudinary.api.ApiResponse;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.*;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.common.ProxyRenderer;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaComponentRenderer;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaSectionRenderer;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.AdminApiService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfigAdminUsageRenderer extends AbstractEditorAreaComponentRenderer<AbstractSection, CloudinaryConfigModel> {
    //public class CloudinaryConfigAdminUsageRenderer extends AbstractEditorAreaComponentRenderer<CloudinaryConfigModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryConfigAdminUsageRenderer.class);
    private static final String NOT_CONNECTED = "Not Connected to Cloudinary ";
    private static String CONNECTED = "Connected to cloudinary ";
    private static String ENABLE_CLOUDINARY = "Enable Cloudinary ";

    @Resource
    private AdminApiService adminApiService;

    @Resource
    private ModelService modelService;


    public CloudinaryConfigAdminUsageRenderer() {
    }

    protected void setConnectionDetailsOnDiv(CloudinaryConfigModel cloudinaryConfigModel, Div newValueContainer, Label label, Html html, Hbox boxHeader) {
        try {

            ApiResponse response = adminApiService.getDataUsagesInformation();

            Map<String, Integer> storageUsage = new HashMap<>();
            Map<String, Integer> bandwithUsages = new HashMap<>();
            Map<String, Integer> transformationUsages = new HashMap<>();

            storageUsage = (Map<String, Integer>) response.get("bandwidth");
            bandwithUsages = (Map<String, Integer>) response.get("bandwidth");
            transformationUsages = (Map<String, Integer>) response.get("bandwidth");

            label.setValue(CONNECTED);
            label.setSclass("yw-labelstyle");

            boxHeader.appendChild(label);

            String usagesData = "<b>Storage Usage : </b>" + storageUsage.get("usage") + "<b> | Bandwidth Usage : </b>" + bandwithUsages.get("usage") + "<b> | Transformation Usage : </b>" + transformationUsages.get("usage");

            html.setContent(usagesData);
            html.setSclass("yw-editorarea-z-html");

            newValueContainer.setSclass("yw-editorarea-z-div");

            newValueContainer.appendChild(boxHeader);
            newValueContainer.appendChild(html);

        } catch (Exception e) {
            LOG.warn("Exception occurred on Admin Usage Api call");
        }
    }

    protected WidgetComponentRenderer<Component, CustomSection, Object> createCustomSectionRenderer(String springBean, String clazz) {
        return this.resolveCustomComponentRenderer(springBean, clazz, CustomSection.class);
    }

    @Override
    public void render(Component component, AbstractSection abstractSectionConfiguration, CloudinaryConfigModel cloudinaryConfigModel, DataType dataType, WidgetInstanceManager widgetInstanceManager) {
        {

            Div newValueContainer = new Div();
            Label cloudinaryConnectionLabel = new Label();
            Label enableCloudinaryLabel = new Label(ENABLE_CLOUDINARY);

            final Html html = new Html();
            final Hbox boxHeader = new Hbox();
            final Div radioDiv = new Div();
            final Div labelDiv = new Div();
            // final Html radioHtml = new Html();

            UITools.modifySClass(enableCloudinaryLabel, "yw-enablelabelstyle", true);
            UITools.modifySClass(cloudinaryConnectionLabel, "yw-labelstyle", true);
            //enableCloudinaryLabel.setSclass("yw-editorarea-tabbox-tabpanels-tabpanel-groupbox-attrcell-label z-label");

            //radioHtml.setSclass("yw-radioButton-z-html");
            //radioHtml.setClass();
            //radioHtml.setContent("");
            labelDiv.appendChild(enableCloudinaryLabel);
            //radioDiv.appendChild(radioHtml);

            Radio trueCheck = new Radio();
            trueCheck.setAttribute("enabled", Boolean.TRUE);
            trueCheck.setLabel("True");


            Radio falseCheck = new Radio();
            falseCheck.setAttribute("disabled", Boolean.FALSE);
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
            newValueContainer.appendChild(labelDiv);
            newValueContainer.appendChild(radioDiv);
            // newValueContainer.appendChild(radioHtml);

            if (cloudinaryConfigModel.getEnableCloudinary()) {
                setConnectionDetailsOnDiv(cloudinaryConfigModel, newValueContainer, cloudinaryConnectionLabel, html, boxHeader);
            } else {
                cloudinaryConnectionLabel.setValue(NOT_CONNECTED);
            }
            trueCheck.addEventListener(Events.ON_CLICK, (event) -> {
                falseCheck.setChecked(Boolean.FALSE);
                setConnectionDetailsOnDiv(cloudinaryConfigModel, newValueContainer, cloudinaryConnectionLabel, html, boxHeader);
                cloudinaryConfigModel.setEnableCloudinary(true);
                modelService.save(cloudinaryConfigModel);
            });

            falseCheck.addEventListener(Events.ON_CLICK, (event) -> {
                trueCheck.setChecked(Boolean.FALSE);
                cloudinaryConnectionLabel.setValue(NOT_CONNECTED);
                html.setContent("");
                boxHeader.appendChild(cloudinaryConnectionLabel);
                newValueContainer.appendChild(boxHeader);
                newValueContainer.appendChild(html);

                cloudinaryConfigModel.setEnableCloudinary(false);
                //widgetInstanceManager.getModel().setValue("enableCloudinary", Boolean.FALSE);
                modelService.save(cloudinaryConfigModel);

                //widgetInstanceManager.getModel().setValue("essentialSectionIsOpen", event.is);
            });

            newValueContainer.setParent(component);

        }


    }


}

