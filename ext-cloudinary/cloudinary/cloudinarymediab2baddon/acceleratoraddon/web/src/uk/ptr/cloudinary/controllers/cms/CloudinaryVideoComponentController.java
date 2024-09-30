package uk.ptr.cloudinary.controllers.cms;

import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONException;
import org.cloudinary.json.JSONObject;
import com.cloudinary.Cloudinary;
import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.core.model.components.CloudinaryVideoComponentModel;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
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
    private static String PUBLIC_ID = "publicId";
    private static String RAW_TRANSFORMATION= "raw_transformation";
    private static String TRANSFORMATION= "transformation";

    @Resource
    private CloudinaryConfigFacade cloudinaryConfigFacade;

    @Override
    protected void fillModel(HttpServletRequest request, Model model, CloudinaryVideoComponentModel component) {

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigFacade.getCloudinaryConfig();
        if (BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary()) && BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinaryVideoPlayer())) {

            String transformationJson = cloudinaryConfigModel.getVideoPlayerTransformation();
            String transformationString = setTransformationString(component,model, cloudinaryConfigModel);

            createJsonData(model, component, transformationJson, transformationString);

            if (cloudinaryConfigModel.getCloudinaryURL() != null) {
                Cloudinary cloudinary = new Cloudinary(cloudinaryConfigModel.getCloudinaryURL());
                cloudinary.setUserAgent(CloudinarymediacoreConstants.CLOUDINARYSAPCC, CloudinarymediacoreConstants.CLOUDINARY_VERSION + "(SAPCC" + CloudinarymediacoreConstants.SAP_VERSION + ")");

                model.addAttribute("cloudName", cloudinary.config.cloudName);
            }
            model.addAttribute("cloudinaryConfig", cloudinaryConfigModel);
            model.addAttribute("componentVideo", component.getCloudinaryVideo());
            model.addAttribute("showComponent", true);
        } else {
            model.addAttribute("showComponent", false);
        }
    }

    private void createJsonData(Model model, CloudinaryVideoComponentModel component, String transformationJson, String transformationString) {
        try {
            preparePlayerJson(transformationJson, model);

            JSONObject sourceJsonData = prepareSourceJson(transformationJson, model);
            if(component.getCloudinaryVideo() != null) {
                sourceJsonData.put(PUBLIC_ID, component.getCloudinaryVideo().getCloudinaryPublicId());
            }
            appendTransformationString(sourceJsonData, model, transformationString);
            model.addAttribute("sourceJsonData", sourceJsonData);
        }
        catch (JSONException e) {
            LOG.error("Exception occurred while formatting transformation value ", e);
        }
    }

    private void appendTransformationString(JSONObject sourceJsonData, Model model, String transformationString) {
        try {
            if (StringUtils.isNotEmpty(transformationString)) {
                JSONObject jsonObject =new JSONObject();
                JSONArray jsonArray = new JSONArray();

                jsonObject.put(RAW_TRANSFORMATION, transformationString);
                jsonArray.put(jsonObject);
                sourceJsonData.put(TRANSFORMATION, jsonArray);
                model.addAttribute("sourceJsonData", sourceJsonData);
            }
        } catch (JSONException e) {
            LOG.error("Exception occurred while formatting transformation value ", e);
        }
    }

    private void preparePlayerJson(String transformationJson, Model model) {
        try {
            if (transformationJson != null) {
                String transformation = transformationJson.replaceAll("\'", "\"");
                JSONObject jsonObj = new JSONObject(transformation);
                if (transformationJson.contains(PLAYER)) {
                    JSONObject playerJsonData = jsonObj.getJSONObject(PLAYER);
                    model.addAttribute("playerJsonData", playerJsonData);
                }
            }
        }
        catch (JSONException e) {
            LOG.error("Exception occurred while formatting transformation value ", e);
        }
    }


    private JSONObject prepareSourceJson(String transformationJson, Model model) {

        JSONObject sourceJson = new JSONObject();
        try{
            if (transformationJson != null) {
                String transformation = transformationJson.replaceAll("\'", "\"");
                JSONObject jsonObj = new JSONObject(transformation);
                if (transformationJson.contains(SOURCE)) {
                    JSONObject sourceJsonData = jsonObj.getJSONObject(SOURCE);
                    if (sourceJsonData.get(SOURCE_TYPE) != null) {
                        sourceJson.put(SOURCE_TYPE, sourceJsonData.get(SOURCE_TYPE));
                        return sourceJson;
                    }
                }
            }
        }
        catch (JSONException e) {
            LOG.error("Exception occurred while formatting transformation value ", e);
        }
        return sourceJson;
    }

    private String setTransformationString(CloudinaryVideoComponentModel component,Model model, CloudinaryConfigModel cloudinaryConfigModel) {

        if (component.getCatalogVersion().getCatalog() instanceof ContentCatalogModel) {
            return cloudinaryConfigModel.getCloudinaryGlobalContentVideoTransformation();
        } else {
            return cloudinaryConfigModel.getCloudinaryGlobalVideoTransformation();
        }
    }
}