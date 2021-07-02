package uk.ptr.cloudinary.controllers.cms;

import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;
import com.cloudinary.Cloudinary;
import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.core.model.components.CloudinaryVideoComponentModel;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ptr.cloudinary.facades.CloudinaryConfigFacade;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Controller("CloudinaryVideoComponentController")
@RequestMapping("/view/CloudinaryVideoComponentController")
public class CloudinaryVideoComponentController extends
        AbstractCMSAddOnComponentController<CloudinaryVideoComponentModel> {

    private static final Logger LOG = Logger.getLogger(CloudinaryVideoComponentController.class);

    private static String PLAYER = "player";
    private static String SOURCE = "source";
    private static String SOURCE_TYPE = "sourceTypes";

    @Resource
    private CloudinaryConfigFacade cloudinaryConfigFacade;

    @Override
    protected void fillModel(HttpServletRequest request, Model model, CloudinaryVideoComponentModel component) {

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigFacade.getCloudinaryConfig();
        if (org.apache.commons.lang3.BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary()) && org.apache.commons.lang3.BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinaryVideoPlayer())) {

            String transformationJson = cloudinaryConfigModel.getVideoPlayerTransformation();
            convertToJson(model, transformationJson);

            String transformationString = setTransformationString(component, cloudinaryConfigModel);

            if (cloudinaryConfigModel.getCloudinaryURL() != null) {
                Cloudinary cloudinary = new Cloudinary(cloudinaryConfigModel.getCloudinaryURL());
                model.addAttribute("cloudName", cloudinary.config.cloudName);
            }
            model.addAttribute("cloudinaryConfig", cloudinaryConfigModel);
            model.addAttribute("componentVideo", component.getCloudinaryVideo());
            model.addAttribute("showComponent", true);
            model.addAttribute("transformationString", transformationString);
        } else {
            model.addAttribute("showComponent", false);
        }

    }

    private String setTransformationString(CloudinaryVideoComponentModel component, CloudinaryConfigModel cloudinaryConfigModel) {

        String componentTransformation = component.getTransformation();
        String globalTransformation = cloudinaryConfigModel.getCloudinaryGlobalContentVideoTransformation();

        if (BooleanUtils.isTrue(component.getIsOverridden())) {
            return componentTransformation;
        } else if (StringUtils.isNotEmpty(componentTransformation) && StringUtils.isNotEmpty(globalTransformation)) {
            return componentTransformation + '/' + globalTransformation;
        } else if (StringUtils.isNotEmpty(componentTransformation)) {
            return componentTransformation;
        } else {
            return globalTransformation;
        }
    }

    private void convertToJson(Model model, String transformationString) {
        try {
            if (transformationString != null) {
                String transformation = transformationString.replaceAll("\'", "\"");
                JSONObject jsonObj = new JSONObject(transformation);
                if (transformationString.contains(PLAYER)) {
                    JSONObject playerJsonData = jsonObj.getJSONObject(PLAYER);
                    model.addAttribute("playerJsonData", playerJsonData);
                }
                if (transformation.contains(SOURCE)) {
                    JSONObject sourceJsonData = jsonObj.getJSONObject(SOURCE);
                    if (sourceJsonData.get(SOURCE_TYPE) != null) {
                        model.addAttribute("sourceJsonData", sourceJsonData);
                    }
                }

            }
        }
        catch (JSONException e) {
            LOG.error("Exception occurred while formatting transformation value ", e);
        }
    }
}