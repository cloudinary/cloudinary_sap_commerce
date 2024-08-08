package uk.ptr.cloudinary.renderers;

import com.cloudinary.api.ApiResponse;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractSection;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.MessageboxUtils;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.AbstractEditorAreaComponentRenderer;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spockframework.util.ObjectUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.PresetDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.model.PresetModel;
import uk.ptr.cloudinary.service.AdminApiService;
import org.zkoss.zul.Messagebox;
import uk.ptr.cloudinary.service.AnalyticsApiService;
import uk.ptr.cloudinary.service.PresetApiService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudinaryConfigAdminUsageRenderer extends AbstractEditorAreaComponentRenderer<AbstractSection, CloudinaryConfigModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryConfigAdminUsageRenderer.class);
    private static final String N_A = "N/A";
    private static final String CREDITS_USAGE = "credits_usage";
    private static final String USAGE = "usage";
    private static final String USED_PERCENT = "used_percent";
    private static final String LIMIT = "limit";
    private static final String PLAN = "plan";
    private static final String CLOSE_PARANTHESIS = ")";
    private static final String OPEN_PARANTHESIS = " (";

    @Resource
    private AdminApiService adminApiService;

    @Resource
    private PresetApiService presetApiService;

    @Resource
    private ModelService modelService;

    @Resource
    private ConfigurationService configurationService;

    @Resource
    private AnalyticsApiService analyticsApiService;

    @Resource
    private PresetDao presetDao;

    @Override
    public void render(Component component, AbstractSection abstractSectionConfiguration, CloudinaryConfigModel cloudinaryConfigModel, DataType dataType, WidgetInstanceManager widgetInstanceManager) {

        Div usageResponseDiv = new Div();
        final Div enableCloudinaryRadioDiv = new Div();
        final Div radioDiv = new Div();
        final Html nextHtml = new Html();
        final Html html = new Html();
        Hbox boxHeader = new Hbox();
        Div cloudinaryVersionDiv = new Div();

        Label cloudinaryVersion = new Label(CloudinarymediacoreConstants.VERSION);
        Label cloudinaryVersionValue = new Label();
        cloudinaryVersionValue.setValue(" :  " + CloudinarymediacoreConstants.CLOUDINARY_VERSION);

        UITools.modifySClass(cloudinaryVersion, "yw-labelstyle-z-label", true);

        cloudinaryVersionDiv.appendChild(cloudinaryVersion);
        cloudinaryVersionDiv.appendChild(cloudinaryVersionValue);

        Label enableCloudinaryFieldName = new Label(CloudinarymediacoreConstants.ENABLE_CLOUDINARY);
        Label cloudinaryConnectionLabel = new Label();
        Label connectionErrorMessage = new Label("");

        Radio trueCheck = new Radio();
        trueCheck.setLabel("True");

        Radio falseCheck = new Radio();
        falseCheck.setLabel("False");

        usageResponseDiv.appendChild(cloudinaryVersionDiv);

        setStyleAndContent(component, cloudinaryConfigModel, usageResponseDiv, enableCloudinaryRadioDiv, radioDiv, nextHtml, enableCloudinaryFieldName, cloudinaryConnectionLabel, connectionErrorMessage, trueCheck, falseCheck);

        if (BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary())) {
            try {
                setConnectionDetailsOnDiv(cloudinaryConfigModel, usageResponseDiv, cloudinaryConnectionLabel, html, boxHeader);
                //getPresetDetails(cloudinaryConfigModel);
            } catch (IllegalArgumentException illegalException) {
                LOG.error("Illegal Argument " + illegalException.getMessage());
            } catch (Exception e) {
                LOG.error("Exception occured calling Admin Usage API " + e.getMessage());
            }
        } else {
            cloudinaryConnectionLabel.setValue(CloudinarymediacoreConstants.NOT_CONNECTED);
            boxHeader.appendChild(cloudinaryConnectionLabel);
        }

        trueCheck.addEventListener(Events.ON_CLICK, (event) -> {
            Messagebox.show(getLabel("configuration.messagebox.message"),getLabel("configuration.messagebox.title"),MessageboxUtils.NO_YES_OPTION,Messagebox.QUESTION,new EventListener<Messagebox.ClickEvent>() {
                @Override
                public void onEvent(final Messagebox.ClickEvent evt) throws Exception {
                    if (Messagebox.ON_YES.equals(evt.getName())) {
                        // Code if yes clicked
                        modelService.refresh(cloudinaryConfigModel);
                        String response = setConnectionDetailsOnDiv(cloudinaryConfigModel, usageResponseDiv, cloudinaryConnectionLabel, html, boxHeader);
                        getPresetDetails(cloudinaryConfigModel);
                        if (response.equalsIgnoreCase("true")) {
                            falseCheck.setChecked(Boolean.FALSE);
                            cloudinaryConfigModel.setEnableCloudinary(true);
                            connectionErrorMessage.setValue("");
                            modelService.save(cloudinaryConfigModel);

                            LOG.info("Calling analaytic API for activating cloudinary connection");
                            LOG.info(String.valueOf(analyticsApiService.activateCloudinaryConnectionWithSAP()));
                        } else {
                            falseCheck.setChecked(Boolean.TRUE);
                            trueCheck.setChecked(Boolean.FALSE);
                            cloudinaryConnectionLabel.setValue(CloudinarymediacoreConstants.NOT_CONNECTED);
                            html.setContent("&nbsp;");
                            boxHeader.appendChild(cloudinaryConnectionLabel);
                            connectionErrorMessage.setValue(response);
                        }


                    }else{
                        trueCheck.setChecked(Boolean.FALSE);
                    }
                }
            });

        });

        falseCheck.addEventListener(Events.ON_CLICK, (event) -> {
            Messagebox.show(getLabel("configuration.messagebox.message"),getLabel("configuration.messagebox.title"),MessageboxUtils.NO_YES_OPTION,Messagebox.QUESTION,new EventListener<Messagebox.ClickEvent>()
            {
                @Override
                public void onEvent(final Messagebox.ClickEvent evt) throws Exception
                {
                    if (Messagebox.ON_YES.equals(evt.getName()))
                    {
                        modelService.refresh(cloudinaryConfigModel);
                        trueCheck.setChecked(Boolean.FALSE);
                        cloudinaryConnectionLabel.setValue(CloudinarymediacoreConstants.NOT_CONNECTED);
                        html.setContent("");
                        boxHeader.appendChild(cloudinaryConnectionLabel);
                        connectionErrorMessage.setValue("");

                        cloudinaryConfigModel.setEnableCloudinary(false);
                        modelService.save(cloudinaryConfigModel);

                        LOG.info("Calling analaytic API for deactivating cloudinary connection");
                        LOG.info(String.valueOf(analyticsApiService.deactivateCloudinaryConnectionWithSAP()));

                    }else{
                        falseCheck.setChecked(Boolean.FALSE);
                    }
                }
            });
        });

        usageResponseDiv.appendChild(boxHeader);
        usageResponseDiv.appendChild(html);
        usageResponseDiv.appendChild(connectionErrorMessage);
        usageResponseDiv.setParent(component);

    }

    private String getPresetDetails(CloudinaryConfigModel cloudinaryConfigModel) {
        Map<String, Boolean> presetResponse = null;
        String errorMsg = "";

        try {
            if(cloudinaryConfigModel != null)
            {
                presetResponse = presetApiService.getUploadPresets(cloudinaryConfigModel.getCloudinaryURL());
                populatePresetInfo(cloudinaryConfigModel, presetResponse);
            }
        }
        catch (IllegalArgumentException illegalException) {
            LOG.error("Illegal Argument " + illegalException.getMessage());
            errorMsg = CloudinarymediacoreConstants.INVALID_URL +cloudinaryConfigModel.getCloudinaryURL();
            return errorMsg;
        }
        catch (Exception e) {
            LOG.error("Exception occured calling Admin Usage API " + e.getMessage());
            errorMsg = e.getMessage();
            return errorMsg;
        }
        return Boolean.TRUE.toString();
    }

    private void populatePresetInfo(CloudinaryConfigModel cloudinaryConfigModel, Map<String, Boolean> presetResponse) {
        List<PresetModel> presetModelList = new ArrayList<>();
        if(presetResponse.size() > 0) {
            //remove previous presets
            modelService.removeAll(presetDao.find());
            for(Map.Entry<String, Boolean> entry : presetResponse.entrySet()) {
                populatePreset(cloudinaryConfigModel,presetModelList, entry);
            }
            cloudinaryConfigModel.setMediaUploadPreset(presetModelList.stream().findFirst().get());
            modelService.save(cloudinaryConfigModel);
        }
    }

    private void populatePreset(CloudinaryConfigModel cloudinaryConfigModel, List<PresetModel> presetModelList, Map.Entry<String, Boolean> entry) {
        PresetModel presetModel = modelService.create(PresetModel.class);
        presetModel.setName(entry.getKey());
        presetModelList.add(presetModel);
        modelService.save(presetModel);
    }

    private String getLabel(String key) {
        return Labels.getLabel(key);
    }

    private void setStyleAndContent(Component component, CloudinaryConfigModel cloudinaryConfigModel, Div usageResponseDiv, Div enableCloudinaryRadioDiv, Div radioDiv, Html nextHtml, Label enableCloudinaryFieldName, Label cloudinaryConnectionLabel, Label connectionErrorMessage, Radio trueCheck, Radio falseCheck) {
        nextHtml.setContent("&nbsp;<br>");

        UITools.modifySClass(enableCloudinaryFieldName, "yw-enablelabelstyle", true);
        UITools.modifySClass(cloudinaryConnectionLabel, "yw-labelstyle-z-label", true);
        UITools.modifySClass(connectionErrorMessage, "yw-error-connection-labelstyle-z-label", true);

        enableCloudinaryRadioDiv.appendChild(enableCloudinaryFieldName);

        if (BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary())) {
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
    }

    protected String setConnectionDetailsOnDiv(CloudinaryConfigModel cloudinaryConfigModel, Div newValueContainer, Label label, Html html, Hbox boxHeader) throws Exception {

        ApiResponse response = null;
        String errorMsg = "";

        try {
            if(cloudinaryConfigModel != null)
            {
                response = adminApiService.getCloudinaryPlanInfo(cloudinaryConfigModel.getCloudinaryURL());
                LOG.debug("Cloudinary Admin API Response ::"+response);
            }
        }
        catch (IllegalArgumentException illegalException) {
            LOG.error("Illegal Argument " + illegalException.getMessage());
            errorMsg = CloudinarymediacoreConstants.INVALID_URL +cloudinaryConfigModel.getCloudinaryURL();
            return errorMsg;
        }
        catch (Exception e) {
            LOG.error("Exception occured calling Admin Usage API " + e.getMessage());
            errorMsg = e.getMessage();
            return errorMsg;
        }
        if (response != null) {
            return setUsageResponseData(newValueContainer, label, html, boxHeader, response);
        }
        return Boolean.FALSE.toString();
    }

    private String setUsageResponseData(Div newValueContainer, Label label, Html html, Hbox boxHeader, ApiResponse response) {
        Map<String, Object> storageUsages = new HashMap<>();
        Map<String, Object> bandwidthUsages = new HashMap<>();
        Map<String, Object> transformationUsages = new HashMap<>();
        Map<String, Double> limit = new HashMap<>();

        storageUsages = (Map<String, Object>) response.get("storage");
        bandwidthUsages = (Map<String, Object>) response.get("bandwidth");
        transformationUsages = (Map<String, Object>) response.get("transformations");
        limit = (Map<String, Double>) response.get("credits");



        String storageUsage=null;
        if(storageUsages!=null && storageUsages.get(USAGE)!=null) {
            try {
                storageUsage = getByteConversion(Long.valueOf(storageUsages.get(USAGE).toString()));
            }catch(NumberFormatException nfex){
                LOG.error("Incorrect storage usage value :"+nfex.getMessage(),nfex);
            }
        }
        String bandwidthUsage=null;
        if(bandwidthUsages!=null && bandwidthUsages.get(USAGE)!=null) {
            try {
                bandwidthUsage = getByteConversion(Long.valueOf(bandwidthUsages.get(USAGE).toString()));
            }catch(NumberFormatException nfex){
                LOG.error("Incorrect bandwidth usage value :"+nfex.getMessage(),nfex);
            }
        }

        label.setValue(CloudinarymediacoreConstants.CONNECTED);
        label.setSclass("yw-labelstyle-z-label");
        boxHeader.appendChild(label);

        StringBuilder usagesData = new StringBuilder();
        usagesData.append(response.get(PLAN));
        usagesData.append(CloudinarymediacoreConstants.TOTAL_STORAGE_LIMIT);
        if(limit!=null) {
            usagesData.append(limit.get(LIMIT));
            usagesData.append(OPEN_PARANTHESIS);
            if(limit.get(USED_PERCENT)!=null) {
                usagesData.append(limit.get(USED_PERCENT));
                usagesData.append(CloudinarymediacoreConstants.PERCENTAGE);
            }  else if(limit.get(CREDITS_USAGE)!=null){
                usagesData.append(limit.get(CREDITS_USAGE));
                usagesData.append(CloudinarymediacoreConstants.PERCENTAGE);
            }
            else{
                usagesData.append(N_A);
            }
            usagesData.append(CLOSE_PARANTHESIS);
        }else{
            usagesData.append(N_A);
        }

        usagesData.append(CloudinarymediacoreConstants.STORAGE_USUAGE);
        if(storageUsages!=null) {
            usagesData.append(storageUsage);
            usagesData.append(OPEN_PARANTHESIS);
            if(storageUsages.get(CREDITS_USAGE)!=null) {
                usagesData.append(storageUsages.get(CREDITS_USAGE));
                usagesData.append(CloudinarymediacoreConstants.CREDITS);
            }
            else if (storageUsages.get(USED_PERCENT)!=null) {

                usagesData.append(storageUsages.get(USED_PERCENT));
                usagesData.append(CloudinarymediacoreConstants.PERCENTAGE);

            }
            else{
                usagesData.append(N_A);
            }
            usagesData.append(CLOSE_PARANTHESIS);
        }else{
            usagesData.append(N_A);
        }

        usagesData.append(CloudinarymediacoreConstants.BANDWIDTH_USUAGE);
        if(bandwidthUsages!=null) {

            usagesData.append(bandwidthUsage);
            usagesData.append(OPEN_PARANTHESIS);
            if(bandwidthUsages.get(CREDITS_USAGE)!=null) {

                usagesData.append(bandwidthUsages.get(CREDITS_USAGE));
                usagesData.append(CloudinarymediacoreConstants.CREDITS);

            }
            else if (bandwidthUsages.get(USED_PERCENT)!=null) {

                usagesData.append(bandwidthUsages.get(USED_PERCENT));
                usagesData.append(CloudinarymediacoreConstants.PERCENTAGE);

            } else{
                usagesData.append(N_A);
            }
            usagesData.append(CLOSE_PARANTHESIS);
        }else{
            usagesData.append(N_A);
        }

        usagesData.append(CloudinarymediacoreConstants.TRANSFORMATION_USUAGE);
        if(transformationUsages!=null) {

            usagesData.append(transformationUsages.get(USAGE));
            usagesData.append(OPEN_PARANTHESIS);
            if(transformationUsages.get(CREDITS_USAGE)!=null) {
                usagesData.append(transformationUsages.get(CREDITS_USAGE));
                usagesData.append(CloudinarymediacoreConstants.CREDITS);
            }
            else if (transformationUsages.get(USED_PERCENT)!=null) {

                usagesData.append(transformationUsages.get(USED_PERCENT));
                usagesData.append(CloudinarymediacoreConstants.PERCENTAGE);

            }else{
                usagesData.append(N_A);
            }
            usagesData.append(CLOSE_PARANTHESIS);
        }else{
            usagesData.append(N_A);
        }

        html.setContent(usagesData.toString());
        html.setSclass("yw-editorarea-z-html");
        newValueContainer.setSclass("yw-editorarea-z-div");
        newValueContainer.appendChild(boxHeader);
        newValueContainer.appendChild(html);
        return Boolean.TRUE.toString();
    }

    private String getByteConversion(Long bytes){
        BigDecimal usage = BigDecimal.valueOf(bytes);
        BigDecimal kilobyte = BigDecimal.valueOf(1024);
        BigDecimal megabyte = kilobyte.multiply(kilobyte);
        BigDecimal gigabyte = megabyte.multiply(kilobyte);
        BigDecimal terabyte = gigabyte.multiply(kilobyte);

        if ((usage.compareTo(BigDecimal.ZERO) != -1) && usage.compareTo(kilobyte) == -1) {
            return usage.setScale(2, RoundingMode.FLOOR) + " B";

        } else if ((usage.compareTo(kilobyte) != -1) && usage.compareTo(megabyte) == -1) {
            return usage.divide(kilobyte).setScale(2, RoundingMode.FLOOR) + " KB";

        } else if (usage.compareTo(megabyte) != -1 && usage.compareTo(gigabyte) == -1) {
            return usage.divide(megabyte).setScale(2, RoundingMode.FLOOR) + " MB";

        } else if (usage.compareTo(gigabyte) != -1 && usage.compareTo(terabyte) == -1) {
            return usage.divide(gigabyte).setScale(2, RoundingMode.FLOOR) + " GB";

        } else if (usage.compareTo(terabyte) != -1) {
            return usage.divide(terabyte).setScale(2, RoundingMode.FLOOR) + " TB";

        } else {
            return usage.setScale(2, RoundingMode.FLOOR) + " Bytes";
        }
    }

}

