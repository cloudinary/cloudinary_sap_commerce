/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.http44.api.Response;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.service.PresetApiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DefaultPresetApiService implements PresetApiService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPresetApiService.class);
    public static final String PRESETS = "presets";
    public static final String NAME = "name";
    public static final String UNSIGNED = "unsigned";

    @Override
    public Map<String, Boolean> getUploadPresets(String cloudinaryURL) throws IllegalArgumentException, Exception {

        LOG.info("Fetching presets from cloudinary");

        Cloudinary cloudinary = new Cloudinary(cloudinaryURL);
        //cloudinary.setUserAgent(CloudinarymediacoreConstants.CLOUDINARYSAPCC, CloudinarymediacoreConstants.CLOUDINARY_VERSION + "(SAPCC" + CloudinarymediacoreConstants.SAP_VERSION + ")");
        Response response = (Response) cloudinary.api().uploadPresets(ObjectUtils.emptyMap());
        Map<String, Boolean> presetMap = new HashMap<>();

        Object obj = response.get(PRESETS);
        if (obj instanceof ArrayList) {
            ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>) obj;

            for (HashMap<String, Object> map : list) {
                presetMap.put((String) map.get(NAME), (Boolean) map.get(UNSIGNED));
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Presets stored in SAP");
            for (Map.Entry<String, Boolean> preset : presetMap.entrySet()) {
                LOG.debug(preset.getKey() + " , " + preset.getValue());
            }
        }

        return presetMap;
    }
}