package uk.ptr.cloudinary.cronjob;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.model.CloudinaryMediaUploadSyncJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.mediaconversion.model.ConversionGroupModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.dao.CloudinaryMediaContainerDao;
import uk.ptr.cloudinary.dao.CloudinaryMediaDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.UploadApiService;

import javax.annotation.Resource;
import java.util.*;

public class CloudinaryMediaUploadSyncJob extends AbstractJobPerformable<CloudinaryMediaUploadSyncJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryMediaUploadSyncJob.class);

    @Resource
    private CloudinaryMediaDao cloudinaryMediaDao;

    @Resource
    private UploadApiService uploadApiService;

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private CloudinaryMediaContainerDao cloudinaryMediaContainerDao;

    @Resource
    private CatalogSynchronizationService catalogSynchronizationService;

    @Resource
    private MediaConversionService mediaConversionService;

    @Override
    public PerformResult perform(CloudinaryMediaUploadSyncJobModel cloudinaryMediaUploadSyncJobModel) {

        Collection<CatalogVersionModel> catalogVersions = cloudinaryMediaUploadSyncJobModel.getCatalogVersion();
        try {
            if (!CollectionUtils.isEmpty(catalogVersions)) {
                CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

                if (!ObjectUtils.isEmpty(cloudinaryConfigModel) && BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary())) {
                    catalogVersions.stream().filter(catalogVersion -> catalogVersion.getVersion().equalsIgnoreCase("Staged")).forEach(catalogVersion -> {
                        try {

                            List<MediaContainerModel> mediaContainerModels = cloudinaryMediaContainerDao.findMediaContainersNotSyncWithCloudinary(catalogVersion);
                            if (!CollectionUtils.isEmpty(mediaContainerModels)) {
                                mediaContainerModels.forEach(mediaContainer -> {
                                    updateMedia(cloudinaryConfigModel, mediaContainer);
                                });
                            }

                            List<MediaModel> medias = cloudinaryMediaDao.findMediaForEmptyCloudinaryUrlAndMediaContainer(catalogVersion);

                            CatalogVersionModel onlineVersion = catalogVersionService.getCatalogVersion(catalogVersion.getCatalog().getId(), CloudinarymediacoreConstants.VERSION_ONLINE);
                            catalogSynchronizationService.synchronizeFullyInBackground(catalogVersion, onlineVersion);

                        } catch (Exception e) {
                            LOG.error("Exception occurred while running job " + e.getMessage(), e);

                        }
                    });
                }
            }
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        } catch (Exception e) {
            LOG.error("Exception occurred while running job " + e.getMessage(), e);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
        }
    }

    private void updateMedia(CloudinaryConfigModel cloudinaryConfigModel, MediaContainerModel mediaContainer) {
        MediaModel mediaModel = getLargestImage(mediaContainer);
       if(mediaModel.getMediaFormat() != null) {
            MediaModel masterMedia = createMasterMedia(mediaModel);
        }
        List<MediaFormatModel> mediaFormats = new ArrayList<>();
        for (MediaModel medias : mediaContainer.getMedias()) {
            if (medias.getMediaFormat() != null)
                mediaFormats.add(medias.getMediaFormat());
        }
        updateOnDemandMedia(mediaContainer, mediaFormats);

    }

    private MediaModel createMasterMedia(MediaModel mediaModel) {

        MediaModel masterMedia = this.modelService.clone(mediaModel);
        String s[] = mediaModel.getCode().split("\\.");
        LOG.info("Model Media Code " + mediaModel.getCode());
        if (s.length == 2) {
            masterMedia.setCode(s[0] + "_" + mediaModel.getMediaFormat().getQualifier() + "\\." + s[1]);
        } else {
            masterMedia.setCode(mediaModel.getCode() + "_" + mediaModel.getMediaFormat().getQualifier());
        }
        masterMedia.setMediaFormat(null);
        modelService.save(masterMedia);
        return masterMedia;
    }

    private void updateOnDemandMedia(MediaContainerModel mediaContainerModel, List<MediaFormatModel> mediaFormatModels) {
        ConversionGroupModel conversionGroupModel;
        if (mediaContainerModel.getConversionGroup() == null) {
            conversionGroupModel = this.modelService.create(ConversionGroupModel.class);
            conversionGroupModel.setCode(UUID.randomUUID().toString());
        } else {
            conversionGroupModel = mediaContainerModel.getConversionGroup();
        }
        Set<MediaFormatModel> mediaFormatModel = new HashSet<>();
        mediaFormatModel.addAll(mediaFormatModels);

        conversionGroupModel.setSupportedMediaFormats(mediaFormatModel);
        modelService.save(conversionGroupModel);
        mediaContainerModel.setConversionGroup(conversionGroupModel);

        modelService.save(mediaContainerModel);

        mediaConversionService.convertMedias(mediaContainerModel);
    }

    private boolean isContainsConversionGroupForMediaformat(MediaModel stagedMedia) {
        if (stagedMedia.getMediaContainer().getConversionGroup() != null && stagedMedia.getMediaContainer().getConversionGroup().getSupportedMediaFormats().contains(stagedMedia.getMediaFormat()))
            return true;
        else
            return false;
    }

    private MediaModel getLargestImage(MediaContainerModel mediaContainerModel) {
        int imageSize = 0;
        MediaModel masterMedia = null;

        Collection<MediaModel> medias = mediaContainerModel.getMedias();
        for (MediaModel media : medias) {
            if (media.getMediaFormat() == null) {
                return media;
            }
            String s[] = media.getMediaFormat().getTransformation().split(",");
            int temp = Integer.valueOf(s[0].replace("w_", "")) * Integer.valueOf(s[1].replace("h_", ""));
            if (imageSize < temp) {
                imageSize = temp;
                masterMedia = media;
            }
        }
        return masterMedia;
    }

    @Override
    public boolean isAbortable() {
        return true;
    }
}

