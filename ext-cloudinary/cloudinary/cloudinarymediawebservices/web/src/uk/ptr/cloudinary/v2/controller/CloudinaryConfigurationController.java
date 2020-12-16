package uk.ptr.cloudinary.v2.controller;


import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ptr.cloudinary.dto.CloudinaryConfigurationWsDTO;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.CloudinaryConfigService;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/{baseSiteId}/cloudinary")
@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
@Api(tags = "Cloudinary Configuration")
public class CloudinaryConfigurationController {
    public static final String DEFAULT_FIELD_SET = "DEFAULT";

    @Resource
    private CloudinaryConfigService cloudinaryConfigService;

    @Resource
    private ConfigurationService configurationService;

    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CloudinaryConfigurationWsDTO getCloudinaryConfig(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        CloudinaryConfigModel cloudinaryConfig = cloudinaryConfigService.getCloudinaryConfigModel();
        CloudinaryConfigurationWsDTO cloudinaryConfigurationWsDTO = new CloudinaryConfigurationWsDTO();
        cloudinaryConfigurationWsDTO.setIsCloudinaryGalleryEnabled(cloudinaryConfig.getEnableCloudinaryGalleryWidget());
        if(cloudinaryConfig.getCloudinaryURL()!= null) {
            String cloudName[] = cloudinaryConfig.getCloudinaryURL().split("@");
            cloudinaryConfigurationWsDTO.setCloudName(cloudName[1]);
            String cloudinaryUrl[] = cloudinaryConfig.getCloudinaryURL().split(":");
            if(cloudinaryUrl!=null){
                String apikey[] = cloudinaryUrl[1].split("//");
                cloudinaryConfigurationWsDTO.setApiKey(apikey[1]);
            }
        }
        if(cloudinaryConfig.getCloudinaryCarouselHeight()!=null) {
            cloudinaryConfigurationWsDTO.setCloudinaryCarouselHeight(cloudinaryConfig.getCloudinaryCarouselHeight());
        }
        if(cloudinaryConfig.getCloudinaryCarouselWidth()!=null) {
            cloudinaryConfigurationWsDTO.setCloudinaryCarouselWidth(cloudinaryConfig.getCloudinaryCarouselWidth());
        }
        cloudinaryConfigurationWsDTO.setCloudinaryCarouselLocation(cloudinaryConfig.getCloudinaryCarouselLocation());
        if(cloudinaryConfig.getCloudinaryCarouselOffset()!=null) {
            cloudinaryConfigurationWsDTO.setCloudinaryCarouselOffset(cloudinaryConfig.getCloudinaryCarouselOffset());
        }
        cloudinaryConfigurationWsDTO.setCloudinaryZoomTrigger(cloudinaryConfig.getCloudinaryZoomTrigger());
        cloudinaryConfigurationWsDTO.setCloudinaryZoomType(cloudinaryConfig.getCloudinaryZoomType());
        cloudinaryConfigurationWsDTO.setTransformations(cloudinaryConfig.getTransformations());
        cloudinaryConfigurationWsDTO.setEnvironment(configurationService.getConfiguration().getString("environment", "dev"));
        return cloudinaryConfigurationWsDTO;
    }



}
