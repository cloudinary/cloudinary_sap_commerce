package uk.ptr.cloudinary.occ.controllers;


import com.cloudinary.Cloudinary;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dto.CloudinaryConfigurationWsDTO;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.CloudinaryConfigService;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/{baseSiteId}/cloudinary")
@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
@Tag(name = "Cloudinary Configuration")
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
        cloudinaryConfigurationWsDTO.setIsCloudinaryEnabled(cloudinaryConfig.getEnableCloudinary());
        if(cloudinaryConfig.getCloudinaryURL()!= null) {
            Cloudinary cloudinary = new Cloudinary(cloudinaryConfig.getCloudinaryURL());
           // cloudinary.setUserAgent(CloudinarymediacoreConstants.CLOUDINARYSAPCC, CloudinarymediacoreConstants.CLOUDINARY_VERSION + "(SAPCC" + CloudinarymediacoreConstants.SAP_VERSION + ")");

            cloudinaryConfigurationWsDTO.setCloudName(cloudinary.config.cloudName);
            cloudinaryConfigurationWsDTO.setApiKey(cloudinary.config.apiKey);
            cloudinaryConfigurationWsDTO.setCName(cloudinary.config.cname);
        }
        cloudinaryConfigurationWsDTO.setEnvironment(configurationService.getConfiguration().getString("environment", "dev"));
        cloudinaryConfigurationWsDTO.setIsResponsiveEnabled(BooleanUtils.isTrue(cloudinaryConfig.getEnableCloudinary()) && BooleanUtils.isTrue(cloudinaryConfig.getCloudinaryResponsive()));
        cloudinaryConfigurationWsDTO.setCloudinaryImageWidthLimitMin(cloudinaryConfig.getCloudinaryImageWidthLimitMin());
        cloudinaryConfigurationWsDTO.setCloudinaryImageWidthLimitMax(cloudinaryConfig.getCloudinaryImageWidthLimitMax());
        cloudinaryConfigurationWsDTO.setCloudinaryByteStep(cloudinaryConfig.getCloudinaryByteStep());
        cloudinaryConfigurationWsDTO.setCloudinaryGalleryConfigJsonString(cloudinaryConfig.getCloudinaryGalleryConfigJsonString());

        return cloudinaryConfigurationWsDTO;
    }
}
