package uk.ptr.cloudinary.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import de.hybris.platform.core.model.media.MediaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ptr.cloudinary.service.UpdateTagApiService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class DefaultUpdateTagApiService implements UpdateTagApiService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUpdateTagApiService.class);

    private static final String SAP_SKU = "sap_sku_";
    @Override
    public void updateTagOnAsests(String publicId, String productCode, String cloudinaryURL) throws IOException {
        try {
            Cloudinary cloudinary = new Cloudinary(cloudinaryURL);

            String[] publicIds = {publicId};

            Map result = cloudinary.uploader().addTag((SAP_SKU + productCode), publicIds, ObjectUtils.emptyMap());
        }
        catch (IllegalArgumentException illegalException) {
            LOG.error("Illegal Argument " + illegalException.getMessage(), illegalException);
        }
        catch (Exception e) {
            LOG.error("Exception occurred calling Upload  API " + e.getMessage() , e);
        }
    }

}
