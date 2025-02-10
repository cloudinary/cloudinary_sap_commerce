package uk.ptr.cloudinary.controllers.cms;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.core.model.components.CloudinaryGalleryComponentModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller("CloudinaryGalleryComponentController")
@RequestMapping("/view/CloudinaryGalleryComponentController")
public class CloudinaryGalleryComponentController extends
        AbstractCMSAddOnComponentController<CloudinaryGalleryComponentModel> {

    @Override
    protected void fillModel(HttpServletRequest request, Model model, CloudinaryGalleryComponentModel component) {
        model.addAttribute("getComponent", Boolean.TRUE);
    }

}
