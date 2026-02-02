package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.core.model.media.MediaModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.media.NoDataAvailableException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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

import jakarta.annotation.Resource;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.ptr.cloudinary.response.CloudinaryDerivedAssetData;

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
        //cloudinary.setUserAgent(CloudinarymediacoreConstants.CLOUDINARYSAPCC, CloudinarymediacoreConstants.CLOUDINARY_VERSION + "(SAPCC" + CloudinarymediacoreConstants.SAP_VERSION + ")");
        return cloudinary.uploader().destroy(publicId,ObjectUtils.asMap("invalidate", Boolean.TRUE));
    }

    @Override
    public UploadApiResponseData uploadAsset(CloudinaryConfigModel cloudinaryConfigModel, MediaModel mediaModel, String tag) throws IllegalArgumentException, Exception {
        try {
            Cloudinary cloudinary = new Cloudinary(cloudinaryConfigModel.getCloudinaryURL());
            //cloudinary.setUserAgent(CloudinarymediacoreConstants.CLOUDINARYSAPCC, CloudinarymediacoreConstants.CLOUDINARY_VERSION + "(SAPCC" + CloudinarymediacoreConstants.SAP_VERSION + ")");

            //final InputStream inputStream = mediaService.getStreamFromMedia(mediaModel);
            //byte[] bytes = IOUtils.toByteArray(inputStream);

            Map params = ObjectUtils.asMap(
                    CloudinarymediacoreConstants.PUBLIC_ID, mediaModel.getCloudinaryPublicId(),
                    CloudinarymediacoreConstants.FOLDER, cloudinaryConfigModel.getCloudinaryFolderPath(),
                    CloudinarymediacoreConstants.OVERWRITE, true,
                    CloudinarymediacoreConstants.RESOURCE_TYPE, CloudinarymediacoreConstants.AUTO,
                    CloudinarymediacoreConstants.TAGS, tag
            );

            if(cloudinaryConfigModel.getMediaUploadPreset() != null)
                params.put(CloudinarymediacoreConstants.PRESETS, cloudinaryConfigModel.getMediaUploadPreset().getName());

            File file = retrieveFile(mediaModel);

            Map map = cloudinary.uploader().upload(file, params);

            final ObjectMapper mapper = new ObjectMapper();
            final UploadApiResponseData responseData = mapper.convertValue(map, UploadApiResponseData.class);

            String updatedUrl = CloudinaryConfigUtils.updateMediaCloudinaryUrl(getSecureUrl(responseData), cloudinaryConfigModel.getCloudinaryCname());
            mediaModel.setURL(updatedUrl);
            //mediaModel.setCloudinaryURL(updatedUrl);
            mediaModel.setCloudinaryPublicId(responseData.getPublic_id());
            mediaModel.setCloudinaryResourceType(responseData.getResource_type());
            mediaModel.setCloudinaryType(responseData.getType());
            mediaModel.setCloudinaryVersion(responseData.getVersion());
            mediaModel.setCloudinaryMediaFormat(responseData.getFormat());
            mediaModel.setCloudinaryTransformation(getTransformationForDerivedMedia(responseData));
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

    @Override
    public String getTransformationForDerivedMedia(UploadApiResponseData responseData) {
        if (responseData.getDerived() != null && !responseData.getDerived().isEmpty()) {
            return responseData.getDerived().stream()
                    .findFirst()
                    .map(CloudinaryDerivedAssetData::getRaw_transformation)
                    .map(Object::toString).orElse(StringUtils.EMPTY);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void setCloudinaryTransformationOnMedia(MediaModel mediaModel, UploadApiResponseData responseData) {
        final String newTransformation = getTransformationForDerivedMedia(responseData);
        mediaModel.setCloudinaryTransformation(newTransformation);
    }


    @Override
    public String getSecureUrl(UploadApiResponseData response) {
        if (org.apache.commons.lang3.ObjectUtils.isEmpty(response)) {
            return null;
        }
        return (CollectionUtils.isNotEmpty(response.getDerived()) && StringUtils.isNotBlank(response.getDerived().get(0).getSecure_url()))
                ? response.getDerived().get(0).getSecure_url()
                : response.getSecure_url();
    }

    public File retrieveFile(MediaModel media) throws IOException
    {
        NoDataAvailableException cause = null;

        try {
            Collection<File> files = mediaService.getFiles(media);
            Iterator var5 = files.iterator();

            while(var5.hasNext()) {
                File f = (File)var5.next();
                if (f.isFile() && f.canRead()) {
                    return f;
                }
            }
        } catch (NoDataAvailableException var6) {
            cause = var6;
        }

        throw new IOException("Cannot access media '" + media + "'. Data is not locally available.", cause);
    }

}
