package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.mediaconversion.model.ConversionGroupModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.SyncMediaCloudinaryStrategy;
import uk.ptr.cloudinary.service.UploadApiService;


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

        if (BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary()) && BooleanUtils.isNotTrue(
                cloudinaryConfigModel.getSkipOnDemandMediaSync()))
        {
            MediaModel mediaToUploadOnDemand = getMediaToSync(mediaModel);
            if(mediaToUploadOnDemand != null)
            {
                List<ItemModel> itemsToSync = new ArrayList<>();
                if (mediaToUploadOnDemand.getMediaContainer() == null)
                {
                    uploadMediaToCloudinary(cloudinaryConfigModel, mediaToUploadOnDemand);
                    itemsToSync.add(mediaToUploadOnDemand);
                }
                else if (mediaToUploadOnDemand.getMediaFormat() != null)
                {
                    MediaModel largestMedia = getlargestImage(mediaToUploadOnDemand);
                    LOG.info("Master Media code " + largestMedia.getCode());
                    if (largestMedia.getMediaFormat() != null && largestMedia.getCloudinaryURL() == null)
                    {

                        MediaModel masterMedia = createMasterMedia(largestMedia);

                        LOG.info("Uploading stage master media to cloudinary " + masterMedia.getCode());
                        uploadMediaToCloudinary(cloudinaryConfigModel, masterMedia);
                        itemsToSync.add(masterMedia);
                    }

                    LOG.info("Updating media " + mediaToUploadOnDemand.getCode());
                    mediaToUploadOnDemand = updateOnDemandMedia(mediaToUploadOnDemand);
                    itemsToSync.add(mediaToUploadOnDemand);

                }
                if (!(mediaToUploadOnDemand.getCatalogVersion().getCatalog() instanceof ClassificationSystemModel)) {
                    CatalogVersionModel onlineVersion = catalogVersionService.getCatalogVersion(mediaToUploadOnDemand.getCatalogVersion().getCatalog().getId(), CloudinarymediacoreConstants.VERSION_ONLINE);
                    SyncItemJobModel syncJobModel = catalogSynchronizationService.getSyncJob(mediaToUploadOnDemand.getCatalogVersion(), onlineVersion, null);
                    catalogSynchronizationService.performSynchronization(itemsToSync, syncJobModel, getSyncConfig());
                    LOG.info("Sync media from staged to Online " + mediaToUploadOnDemand.getCode());
                }
                return mediaToUploadOnDemand;
            }
        }
        return mediaModel;
    }

    protected SyncConfig getSyncConfig() {
        final SyncConfig syncConfig = new SyncConfig();
        syncConfig.setCreateSavedValues(Boolean.TRUE);
        syncConfig.setForceUpdate(Boolean.TRUE);
        syncConfig.setLogLevelDatabase(JobLogLevel.WARNING);
        syncConfig.setLogLevelFile(JobLogLevel.WARNING);
        syncConfig.setLogToFile(Boolean.TRUE);
        syncConfig.setLogToDatabase(Boolean.FALSE);
        syncConfig.setSynchronous(Boolean.FALSE);
        return syncConfig;
    }

    protected MediaModel createMasterMedia(MediaModel mediaModel) {

        MediaModel masterMedia = this.modelService.clone(mediaModel);
        String s[] = mediaModel.getCode().split("\\.");
        if (s.length == 2) {
            masterMedia.setCode(s[0] + "_" + mediaModel.getMediaFormat().getQualifier() + "\\." + s[1]);
        } else {
            masterMedia.setCode(mediaModel.getCode() + "_" + mediaModel.getMediaFormat().getQualifier());
        }
        masterMedia.setMediaFormat(null);
        modelService.save(masterMedia);
        LOG.info("Created master media  " + masterMedia.getCode());
        return masterMedia;
    }

    protected MediaModel updateOnDemandMedia(MediaModel mediaToUploadOnDemand) {
        if (mediaToUploadOnDemand.getMediaFormat() == null)
            return mediaToUploadOnDemand;

        MediaContainerModel mediaContainerModel = mediaToUploadOnDemand.getMediaContainer();

        ConversionGroupModel conversionGroupModel = null;

        if(!isContainsConversionGroupForMediaformat(mediaToUploadOnDemand))
        {
            if (mediaContainerModel.getConversionGroup() == null)
            {
                conversionGroupModel = modelService.create(ConversionGroupModel.class);
                conversionGroupModel.setCode(UUID.randomUUID().toString());
            }
            else
            {
                conversionGroupModel = mediaToUploadOnDemand.getMediaContainer().getConversionGroup();
            }
            Set<MediaFormatModel> mediaFormatModel = new HashSet<>();
            mediaFormatModel.add(mediaToUploadOnDemand.getMediaFormat());
            conversionGroupModel.setSupportedMediaFormats(mediaFormatModel);
            modelService.save(conversionGroupModel);

            mediaContainerModel.setConversionGroup(conversionGroupModel);
            modelService.save(mediaContainerModel);
        }

        return mediaConversionService.getOrConvert(mediaToUploadOnDemand.getMediaContainer(), mediaToUploadOnDemand.getMediaFormat());
    }

    protected boolean isContainsConversionGroupForMediaformat(MediaModel stagedMedia) {
        if (stagedMedia.getMediaContainer().getConversionGroup() != null && stagedMedia.getMediaContainer().getConversionGroup().getSupportedMediaFormats().contains(stagedMedia.getMediaFormat()))
            return true;
        else
            return false;
    }

    protected MediaModel getMediaToSync(MediaModel mediaModel) {

        if(!(mediaModel.getCatalogVersion().getCatalog() instanceof ClassificationSystemModel)){
            CatalogVersionModel stagedCatalogVersion = null;
            boolean isOnlineVersion = mediaModel.getCatalogVersion().getVersion().equalsIgnoreCase("Online");
            if (isOnlineVersion) {
                stagedCatalogVersion = catalogVersionService.getCatalogVersion(mediaModel.getCatalogVersion().getCatalog().getId(), "Staged");
            } else {
                stagedCatalogVersion = mediaModel.getCatalogVersion();
            }
            return mediaService.getMedia(stagedCatalogVersion, mediaModel.getCode());
        }
        else {
           return mediaModel;
        }
    }

    protected MediaModel getlargestImage(MediaModel mediaModel) {
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

    protected void uploadMediaToCloudinary(CloudinaryConfigModel cloudinaryConfigModel, MediaModel media) {
        if (media.getCloudinaryURL() == null) {
            try {
                uploadApiService.uploadAsset(cloudinaryConfigModel, media, "");
                LOG.info("Uplaoded Media " + media.getCode() + "cloudinaryUrl  " + media.getCloudinaryURL());

            } catch (IllegalArgumentException illegalException) {
                LOG.error("Illegal Argument " + illegalException.getMessage(), illegalException);
            } catch (Exception e) {
                LOG.error("Exception occurred calling Upload  API " + e.getMessage(), e);
            }
        }
    }

    public CloudinaryConfigDao getCloudinaryConfigDao() {
        return cloudinaryConfigDao;
    }

    public void setCloudinaryConfigDao(CloudinaryConfigDao cloudinaryConfigDao) {
        this.cloudinaryConfigDao = cloudinaryConfigDao;
    }

    public UploadApiService getUploadApiService() {
        return uploadApiService;
    }

    public void setUploadApiService(UploadApiService uploadApiService) {
        this.uploadApiService = uploadApiService;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public CatalogVersionService getCatalogVersionService() {
        return catalogVersionService;
    }

    public void setCatalogVersionService(CatalogVersionService catalogVersionService) {
        this.catalogVersionService = catalogVersionService;
    }

    public CatalogSynchronizationService getCatalogSynchronizationService() {
        return catalogSynchronizationService;
    }

    public void setCatalogSynchronizationService(CatalogSynchronizationService catalogSynchronizationService) {
        this.catalogSynchronizationService = catalogSynchronizationService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public MediaConversionService getMediaConversionService() {
        return mediaConversionService;
    }

    public void setMediaConversionService(MediaConversionService mediaConversionService) {
        this.mediaConversionService = mediaConversionService;
    }
}
