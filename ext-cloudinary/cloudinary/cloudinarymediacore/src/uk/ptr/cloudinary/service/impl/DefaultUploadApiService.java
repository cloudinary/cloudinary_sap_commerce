package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.core.model.media.MediaModel;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.response.UploadApiResponseData;
import uk.ptr.cloudinary.service.UploadApiService;


/**
 * The type Default upload api service.
 */
public class DefaultUploadApiService implements UploadApiService
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultUploadApiService.class);

    @Override
    public Map deleteAsset(String cloudinaryURL, String publicId) throws IOException
    {
        Cloudinary cloudinary = new Cloudinary(cloudinaryURL);

        return cloudinary.uploader().destroy(publicId,ObjectUtils.asMap("invalidate", Boolean.TRUE));
    }

    @Override
    public UploadApiResponseData uploadAsset(CloudinaryConfigModel cloudinaryConfigModel, MediaModel mediaModel, String tag) throws IllegalArgumentException, Exception {
        try {
            Cloudinary cloudinary = new Cloudinary(cloudinaryConfigModel.getCloudinaryURL());

            Map params = ObjectUtils.asMap(
                    CloudinarymediacoreConstants.PUBLIC_ID, mediaModel.getCloudinaryPublicId(),
                    CloudinarymediacoreConstants.FOLDER, cloudinaryConfigModel.getCloudinaryFolderPath(),
                    CloudinarymediacoreConstants.OVERWRITE, true,
                    CloudinarymediacoreConstants.RESOURCE_TYPE, CloudinarymediacoreConstants.AUTO,
                    CloudinarymediacoreConstants.TAGS, tag
            );

            Map map = cloudinary.uploader().upload(mediaModel.getURL(), params);

            final ObjectMapper mapper = new ObjectMapper();
            final UploadApiResponseData responseData = mapper.convertValue(map, UploadApiResponseData.class);

            mediaModel.setCloudinaryPublicId(responseData.getPublic_id());
            mediaModel.setCloudinaryURL(responseData.getSecure_url());
            mediaModel.setCloudinaryResourceType(responseData.getResource_type());
            mediaModel.setCloudinaryType(responseData.getType());

            return responseData;
        }
        catch (IllegalArgumentException illegalException) {
            LOG.error("Illegal Argument " + illegalException.getMessage(), illegalException);
        }
        catch (Exception e) {
            LOG.error("Exception occurred calling Upload  API " + e.getMessage() , e);
        }
        return null;
    }


}
