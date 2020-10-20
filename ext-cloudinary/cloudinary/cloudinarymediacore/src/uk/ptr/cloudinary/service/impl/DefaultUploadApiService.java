package uk.ptr.cloudinary.service.impl;

import java.io.IOException;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.core.model.media.MediaModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.enums.CloudinaryResourceType;
import uk.ptr.cloudinary.enums.CloudinaryType;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.response.UplaodApiResponseData;
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
    public UplaodApiResponseData uploadMedia(CloudinaryConfigModel cloudinaryConfigModel, MediaModel mediaModel) throws IllegalArgumentException, Exception {

        try {
            Cloudinary cloudinary = new Cloudinary(cloudinaryConfigModel.getCloudinaryURL());

            Map params = ObjectUtils.asMap(
                    CloudinarymediacoreConstants.PUBLIC_ID, "apparelImages",
                    CloudinarymediacoreConstants.FOLDER, cloudinaryConfigModel.getCloudinaryFolderPath(),
                    CloudinarymediacoreConstants.OVERWRITE, true,
                    CloudinarymediacoreConstants.RESOURCE_TYPE, "auto",
                    CloudinarymediacoreConstants.TAGS, "myapparel, store, newtag"
            );

            Map map = cloudinary.uploader().upload(mediaModel.getURL(), params);

            final ObjectMapper mapper = new ObjectMapper();
            final UplaodApiResponseData responseData = mapper.convertValue(map, UplaodApiResponseData.class);

            mediaModel.setCloudinaryPublicId(responseData.getPublic_id());
            mediaModel.setCloudinaryURL(responseData.getSecure_url());
            mediaModel.setCloudinaryResourceType(CloudinaryResourceType.valueOf(responseData.getResource_type()));
            mediaModel.setIsCloudinaryOverride(Boolean.valueOf(responseData.getOverwritten()));
            mediaModel.setCloudinaryType(CloudinaryType.valueOf(responseData.getType()));

            mediaModel.setCloudinaryResourceType(CloudinaryResourceType.IMAGE);
            return responseData;
        }
        catch (IllegalArgumentException illegalException) {
            LOG.error("Illegal Argument " + illegalException.getMessage());
            return null;
        }
        catch (Exception e) {
            LOG.error("Exception occurred calling Upload  API " + e.getMessage());
            return null;
        }

    }

}
