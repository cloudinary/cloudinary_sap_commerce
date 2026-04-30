package uk.ptr.cloudinary.occ.controllers;


import com.cloudinary.Cloudinary;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.ptr.cloudinary.dto.CloudinaryConfigurationWsDTO;
import uk.ptr.cloudinary.facades.CloudinaryConfigFacade;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;

import jakarta.annotation.Resource;

@Controller
@RequestMapping(value = "/{baseSiteId}/cloudinary")
@CacheControl(directive = CacheControlDirective.PRIVATE, maxAge = 120)
@Tag(name = "Cloudinary Configuration")
public class CloudinaryConfigurationController {
    public static final String DEFAULT_FIELD_SET = "DEFAULT";
    private static final String PLAYER = "player";
    private static final String SOURCE = "source";
    private static final String SOURCE_TYPE = "sourceTypes";
    private static final String PUBLIC_ID = "publicId";
    private static final String RAW_TRANSFORMATION= "raw_transformation";
    private static final String TRANSFORMATION= "transformation";
    private static final Logger LOG = Logger.getLogger(CloudinaryConfigurationController.class);

    @Resource
    private ConfigurationService configurationService;

    @Resource
    private CloudinaryConfigFacade cloudinaryConfigFacade;

    @GetMapping("/configuration")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CloudinaryConfigurationWsDTO getCloudinaryConfig(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        CloudinaryConfigModel cloudinaryConfig = cloudinaryConfigFacade.getCloudinaryConfig();
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

        populateCloudinaryVideoPlayerConfig(cloudinaryConfig, cloudinaryConfigurationWsDTO);
        return cloudinaryConfigurationWsDTO;
    }

    private void populateCloudinaryVideoPlayerConfig(CloudinaryConfigModel cloudinaryConfig,
            CloudinaryConfigurationWsDTO cloudinaryConfigurationWsDTO) {
        String transformationJson = cloudinaryConfig.getVideoPlayerTransformation();
        String transformationString = cloudinaryConfig.getCloudinaryGlobalContentVideoTransformation();
        createJsonData(transformationJson, transformationString, cloudinaryConfigurationWsDTO);
    }

    private void createJsonData(String transformationJson, String transformationString,
            CloudinaryConfigurationWsDTO cloudinaryConfigurationWsDTO) {
        try {
            preparePlayerJson(transformationJson, cloudinaryConfigurationWsDTO);

            JSONObject sourceJsonData = prepareSourceJson(transformationJson);
            appendTransformationString(sourceJsonData, transformationString);
            cloudinaryConfigurationWsDTO.setCloudinaryVideoSourceJsonString(String.valueOf(sourceJsonData));
        }
        catch (JSONException e) {
            LOG.error("Exception occurred while formatting transformation value ", e);
        }
    }

    private void appendTransformationString(JSONObject sourceJsonData, String transformationString) {
        try {
            if (StringUtils.isNotEmpty(transformationString)) {
                JSONObject jsonObject =new JSONObject();
                JSONArray jsonArray = new JSONArray();

                jsonObject.put(RAW_TRANSFORMATION, transformationString);
                jsonArray.put(jsonObject);
                sourceJsonData.put(TRANSFORMATION, jsonArray);
            }
        } catch (JSONException e) {
            LOG.error("Exception occurred while formatting transformation value ", e);
        }
    }

    private void preparePlayerJson(String transformationJson, CloudinaryConfigurationWsDTO cloudinaryConfigurationWsDTO) {
        try {
            if (transformationJson != null) {
                String transformation = transformationJson.replaceAll("\'", "\"");
                JSONObject jsonObj = new JSONObject(transformation);
                if (transformationJson.contains(PLAYER)) {
                    JSONObject playerJsonData = jsonObj.getJSONObject(PLAYER);
                    cloudinaryConfigurationWsDTO.setCloudinaryVideoPlayerJsonString(String.valueOf(playerJsonData));

                }
            }
        }
        catch (JSONException e) {
            LOG.error("Exception occurred while formatting transformation value ", e);
        }
    }

    private JSONObject prepareSourceJson(String transformationJson) {

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


}
