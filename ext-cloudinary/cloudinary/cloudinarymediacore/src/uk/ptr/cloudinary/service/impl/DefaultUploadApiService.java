package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.core.model.media.MediaModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.Collection;
import java.util.Map;

import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.response.UploadApiResponseData;
import uk.ptr.cloudinary.service.UploadApiService;
import uk.ptr.cloudinary.util.CloudinaryConfigUtils;

import javax.annotation.Resource;


/**
 * The type Default upload api service.
 */
public class DefaultUploadApiService implements UploadApiService
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultUploadApiService.class);

    @Resource
    ModelService modelService;

    @Resource
    MediaService mediaService;

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

            if(cloudinaryConfigModel.getMediaUploadPreset() != null)
                params.put(CloudinarymediacoreConstants.PRESETS, cloudinaryConfigModel.getMediaUploadPreset().getName());

            final Collection<File> result = mediaService.getFiles(mediaModel);

            Map map = cloudinary.uploader().upload(result.iterator().next(), params);

            final ObjectMapper mapper = new ObjectMapper();
            final UploadApiResponseData responseData = mapper.convertValue(map, UploadApiResponseData.class);

            String updatedUrl = CloudinaryConfigUtils.updateMediaCloudinaryUrl(responseData.getSecure_url(), cloudinaryConfigModel.getCloudinaryCname());
            mediaModel.setURL(updatedUrl + CloudinarymediacoreConstants.CLOUDINARY_QUERY_PARAM);
            //mediaModel.setCloudinaryURL(updatedUrl);
            mediaModel.setCloudinaryPublicId(responseData.getPublic_id());
            mediaModel.setCloudinaryResourceType(responseData.getResource_type());
            mediaModel.setCloudinaryType(responseData.getType());
            StringBuilder version = new StringBuilder();
            version.append("v").append(responseData.getVersion());
            mediaModel.setCloudinaryVersion(version.toString());
            mediaModel.setCloudinaryMediaFormat(responseData.getFormat());
            modelService.save(mediaModel);
            modelService.refresh(mediaModel);

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
