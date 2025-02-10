package uk.ptr.cloudinary.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import de.hybris.platform.core.model.media.MediaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.service.UpdateTagApiService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class DefaultUpdateTagApiService implements UpdateTagApiService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUpdateTagApiService.class);

    @Override
    public void updateTagOnAsests(String publicId, String productCode, String cloudinaryURL, String resourceType) throws IOException {
        try {
            Cloudinary cloudinary = new Cloudinary(cloudinaryURL);
            //cloudinary.setUserAgent(CloudinarymediacoreConstants.CLOUDINARYSAPCC, CloudinarymediacoreConstants.CLOUDINARY_VERSION + "(SAPCC" + CloudinarymediacoreConstants.SAP_VERSION + ")");

            String[] publicIds = {publicId};

            Map params = ObjectUtils.asMap(
                    CloudinarymediacoreConstants.RESOURCE_TYPE, resourceType
            );

            Map result = cloudinary.uploader().addTag((CloudinarymediacoreConstants.SAP_SKU + productCode), publicIds, params);
        }
        catch (IllegalArgumentException illegalException) {
            LOG.error("Illegal Argument " + illegalException.getMessage(), illegalException);
        }
        catch (Exception e) {
            LOG.error("Exception occurred calling Upload  API " + e.getMessage() , e);
        }
    }

}
