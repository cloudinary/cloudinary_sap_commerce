package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.mediaconversion.model.ConversionGroupModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.SyncMediaCloudinaryStrategy;
import uk.ptr.cloudinary.service.UploadApiService;

import javax.annotation.Resource;
import java.util.*;

import de.hybris.platform.mediaconversion.MediaConversionService;

public class DefaultSyncMediaCloudinaryStrategy implements SyncMediaCloudinaryStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSyncMediaCloudinaryStrategy.class);

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private UploadApiService uploadApiService;

    @Resource
    private MediaService mediaService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private CatalogSynchronizationService catalogSynchronizationService;

    @Resource
    private ModelService modelService;

    @Resource
    private MediaConversionService mediaConversionService;

    public MediaModel onDemandSyncMedia(final MediaModel mediaModel) throws Exception {

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

        MediaModel stagedMedia = getStagedMedia(mediaModel);

        if (stagedMedia != null && cloudinaryConfigModel.getEnableCloudinary()) {

            if (stagedMedia.getMediaContainer() == null) {
                uploadMediaToCloudinary(cloudinaryConfigModel, stagedMedia);
            } else if (stagedMedia.getMediaFormat() != null) {
                MediaModel largestMedia = getMasterImage(stagedMedia);
                LOG.info(largestMedia.getCode() + "  " + largestMedia.getMediaFormat());
                if (largestMedia.getMediaFormat() != null && largestMedia.getCloudinaryURL() == null) {

                    MediaModel masterMedia = createMasterMedia(largestMedia);
                    LOG.info("Uploading stage master media to cloudinary " + masterMedia.getCode());
                    uploadMediaToCloudinary(cloudinaryConfigModel, masterMedia);
                }
                if (!largestMedia.getCode().equals(stagedMedia.getCode())) {
                    LOG.info("Updating media " + stagedMedia.getCode());
                    stagedMedia = updateOnDemandMedia(stagedMedia);
                }
            }
            if (stagedMedia.getCatalogVersion().getVersion().equalsIgnoreCase("Staged")) {
                CatalogVersionModel onlineVersion = catalogVersionService.getCatalogVersion(stagedMedia.getCatalogVersion().getCatalog().getId(), CloudinarymediacoreConstants.VERSION_ONLINE);
                catalogSynchronizationService.synchronizeFully(stagedMedia.getCatalogVersion(), onlineVersion);
                LOG.info("Sync media from staged to Online " + stagedMedia.getCode());
            }
        }
        return getOnlineMedia(stagedMedia);
    }

    private MediaModel createMasterMedia(MediaModel mediaModel) {

        MediaModel masterMedia = this.modelService.clone(mediaModel);
        String s[] = mediaModel.getCode().split("\\.");
        if (s.length == 2) {
            masterMedia.setCode(s[0] + "_" + mediaModel.getMediaFormat().getQualifier() + "\\." + s[1]);
        } else {
            masterMedia.setCode(mediaModel.getCode() + "_" + mediaModel.getMediaFormat().getQualifier());
        }
        masterMedia.setMediaFormat(null);
        modelService.save(masterMedia);
        modelService.save(masterMedia);
        LOG.info("Created master media  " + masterMedia.getCode());
        return masterMedia;
    }

    private MediaModel updateOnDemandMedia(MediaModel stagedMedia) {
        if (stagedMedia.getMediaFormat() == null)
            return stagedMedia;

        MediaContainerModel mediaContainerModel = stagedMedia.getMediaContainer();
        if (mediaContainerModel.getConversionGroup() == null || !isContainsConversionGroupForMediaformat(stagedMedia)) {
            ConversionGroupModel conversionGroupModel = new ConversionGroupModel();
            conversionGroupModel.setCode(UUID.randomUUID().toString());
            Set<MediaFormatModel> mediaFormatModel = new HashSet<>();
            mediaFormatModel.add(stagedMedia.getMediaFormat());

            conversionGroupModel.setSupportedMediaFormats(mediaFormatModel);
            modelService.save(conversionGroupModel);
            modelService.refresh(conversionGroupModel);

            mediaContainerModel.setConversionGroup(conversionGroupModel);

            modelService.save(mediaContainerModel);
            modelService.refresh(mediaContainerModel);
        }
        return mediaConversionService.getOrConvert(stagedMedia.getMediaContainer(), stagedMedia.getMediaFormat());
    }

    private boolean isContainsConversionGroupForMediaformat(MediaModel stagedMedia) {
        if (stagedMedia.getMediaContainer().getConversionGroup() != null && stagedMedia.getMediaContainer().getConversionGroup().getSupportedMediaFormats().contains(stagedMedia.getMediaFormat()))
            return true;
        else
            return false;
    }

    private MediaModel getStagedMedia(MediaModel mediaModel) {
        CatalogVersionModel stagedCatalogVersion = null;
        boolean isOnlineVersion = mediaModel.getCatalogVersion().getVersion().equalsIgnoreCase("Online");
        if (isOnlineVersion) {
            stagedCatalogVersion = catalogVersionService.getCatalogVersion(mediaModel.getCatalogVersion().getCatalog().getId(), "Staged");
        } else {
            stagedCatalogVersion = mediaModel.getCatalogVersion();
        }
        return mediaService.getMedia(stagedCatalogVersion, mediaModel.getCode());
    }

    private MediaModel getOnlineMedia(MediaModel mediaModel) {

        CatalogVersionModel onlineCatalogVersion = null;
        boolean isStagedVersion = mediaModel.getCatalogVersion().getVersion().equalsIgnoreCase("Staged");
        if (isStagedVersion) {
            onlineCatalogVersion = catalogVersionService.getCatalogVersion(mediaModel.getCatalogVersion().getCatalog().getId(), "Online");
        } else {
            onlineCatalogVersion = mediaModel.getCatalogVersion();
        }
        return mediaService.getMedia(onlineCatalogVersion, mediaModel.getCode());
    }

    private MediaModel getMasterImage(MediaModel mediaModel) {
        int imageSize = 0;
        MediaModel masterMedia = null;
        Collection<MediaModel> medias = mediaModel.getMediaContainer().getMedias();
        for (MediaModel mediaModel1 : medias) {
            if (mediaModel1.getMediaFormat() == null) {
                return mediaModel1;
            }
            String s[] = mediaModel1.getMediaFormat().getTransformation().split(",");
            int temp = Integer.valueOf(s[0].replace("w_", "")) * Integer.valueOf(s[1].replace("h_", ""));
            if (imageSize < temp) {
                imageSize = temp;
                masterMedia = mediaModel1;
            }
        }
        return masterMedia;
    }

    private void uploadMediaToCloudinary(CloudinaryConfigModel cloudinaryConfigModel, MediaModel media) {
        if (media.getCloudinaryURL() == null) {
            try {
                uploadApiService.uploadAsset(cloudinaryConfigModel, media, "newAssets");
                LOG.info("Uplaoded Media " + media.getCode() + "cloudinaryUrl  " + media.getCloudinaryURL());

            } catch (IllegalArgumentException illegalException) {
                LOG.error("Illegal Argument " + illegalException.getMessage(), illegalException);
            } catch (Exception e) {
                LOG.error("Exception occurred calling Upload  API " + e.getMessage(), e);
            }
        }
    }

}
