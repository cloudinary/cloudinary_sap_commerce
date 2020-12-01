package uk.ptr.cloudinary.controllers.cms;

import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.core.model.components.CloudinaryVideoComponentModel;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ptr.cloudinary.controllers.pages.ProductPageController;
import uk.ptr.cloudinary.facades.CloudinaryConfigFacade;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Controller("CloudinaryVideoComponentController")
@RequestMapping("/view/CloudinaryVideoComponentController")
public class CloudinaryVideoComponentController extends
        AbstractCMSAddOnComponentController<CloudinaryVideoComponentModel> {

    private static final Logger LOG = Logger.getLogger(CloudinaryVideoComponentController.class);

    @Resource
    private CloudinaryConfigFacade cloudinaryConfigFacade;

    @Override
    protected void fillModel(HttpServletRequest request, Model model, CloudinaryVideoComponentModel component) {

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigFacade.getCloudinaryConfig();
        String transformationString = component.getTransformation();
        try {
            if (transformationString != null) {
                String transformation = transformationString.replaceAll("\'", "\"");
                JSONObject jsonObj = new JSONObject(transformation);
                model.addAttribute("transformation", jsonObj);
            }
        }
        catch (JSONException e){
            LOG.error("Exception occurred while formatting transformation value ", e);
        }
        if(cloudinaryConfigModel.getEnableCloudinary())
        {
            String cloudName[]= cloudinaryConfigModel.getCloudinaryURL().split("@");
            model.addAttribute("cloudName", cloudName[1]);
            model.addAttribute("cloudinaryConfig", cloudinaryConfigModel);
        }
        model.addAttribute("componentVideo", component.getCloudinaryVideo());

    }
}
