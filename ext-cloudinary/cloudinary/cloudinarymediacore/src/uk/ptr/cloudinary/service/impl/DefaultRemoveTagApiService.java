package uk.ptr.cloudinary.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.service.RemoveTagApiService;

import java.util.Arrays;
import java.util.Map;

public class DefaultRemoveTagApiService implements RemoveTagApiService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUpdateTagApiService.class);

    @Override
    public void removeTagFromAsset(String publicId, String productCode, String cloudinaryURL) {

        try {
            Cloudinary cloudinary = new Cloudinary(cloudinaryURL);
           // cloudinary.setUserAgent(CloudinarymediacoreConstants.CLOUDINARYSAPCC, CloudinarymediacoreConstants.CLOUDINARY_VERSION + "(SAPCC" + CloudinarymediacoreConstants.SAP_VERSION + ")");
            Map result = cloudinary.uploader().removeTag((CloudinarymediacoreConstants.SAP_SKU + productCode), new String[]{publicId} , ObjectUtils.emptyMap());
        }
        catch (IllegalArgumentException illegalException) {
            LOG.error("Illegal Argument " + illegalException.getMessage(), illegalException);
        }
        catch (Exception e) {
            LOG.error("Exception occurred calling Remove Tag  API for MediaMedia Id  : " + publicId + "productCode : " + productCode , e);
        }

    }
}
