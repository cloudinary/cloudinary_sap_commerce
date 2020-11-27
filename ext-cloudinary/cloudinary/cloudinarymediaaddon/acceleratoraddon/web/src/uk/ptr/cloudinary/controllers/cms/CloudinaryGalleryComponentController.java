package uk.ptr.cloudinary.controllers.cms;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.cms.AbstractCMSComponentController;
import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.core.model.components.CloudinaryGalleryComponentModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ptr.cloudinary.constants.CloudinarymediaaddonWebConstants;
import uk.ptr.cloudinary.facades.CloudinaryConfigFacade;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller("CloudinaryGalleryComponentController")
@RequestMapping("/view/CloudinaryGalleryComponentController")
public class CloudinaryGalleryComponentController extends
        AbstractCMSAddOnComponentController<CloudinaryGalleryComponentModel> {

    @Resource
    private CloudinaryConfigFacade cloudinaryConfigFacade;

    @Override
    protected void fillModel(HttpServletRequest request, Model model, CloudinaryGalleryComponentModel component) {
        model.addAttribute("getComponent", Boolean.TRUE);
    }

}
