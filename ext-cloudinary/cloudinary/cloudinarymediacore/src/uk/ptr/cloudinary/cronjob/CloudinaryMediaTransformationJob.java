package uk.ptr.cloudinary.cronjob;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.model.CloudinaryMediaTransformationJobModel;
import de.hybris.platform.core.model.model.CloudinaryMediaUploadSyncJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.mediaconversion.model.ConversionGroupModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Resource;

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


public class CloudinaryMediaTransformationJob extends AbstractJobPerformable<CloudinaryMediaTransformationJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryMediaTransformationJob.class);

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
    public PerformResult perform(CloudinaryMediaTransformationJobModel cloudinaryMediaTransformationJobModel) {

     Collection<CatalogVersionModel> catalogVersions = cloudinaryMediaTransformationJobModel.getCatalogVersions();
      try {
          if (CollectionUtils.isEmpty(catalogVersions)) {
              CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

              if (!ObjectUtils.isEmpty(cloudinaryConfigModel) && cloudinaryConfigModel.getEnableCloudinary()) {
                  catalogVersions.stream().filter(catalogVersion -> catalogVersion.getVersion().equalsIgnoreCase("Staged")).forEach(stagedVersion -> {

                      List<MediaContainerModel> mediaContainerModels = cloudinaryMediaContainerDao.findMediaContainerByCatalogVersion(stagedVersion);
                      if (!CollectionUtils.isEmpty(mediaContainerModels)) {
                          mediaContainerModels.forEach(mediaContainer -> {
                              updateMedia(mediaContainer);
                          });
                      }

                      CatalogVersionModel onlineVersion = catalogVersionService.getCatalogVersion(stagedVersion.getCatalog().getId(), CloudinarymediacoreConstants.VERSION_ONLINE);
                      catalogSynchronizationService.synchronizeFullyInBackground(stagedVersion,onlineVersion);
                  });
              }
          }
          return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
      }
      catch (Exception e) {
                LOG.error("Exception occurred while running job " + e.getMessage() , e);
                return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
            }
        }

    private void updateMedia(MediaContainerModel mediaContainer) {
        MediaModel mediaModel = getMasterImage(mediaContainer);
        if(mediaModel != null && mediaContainer.getConversionGroup()!=null)
        {
            mediaConversionService.convertMedias(mediaContainer);
        }
    }

    private MediaModel getMasterImage(MediaContainerModel mediaContainerModel){

        Collection<MediaModel> medias  = mediaContainerModel.getMedias();
        for (MediaModel media : medias) {
            if(media.getMediaFormat() == null && media.getCloudinaryURL()!=null)
            {
                return media;
            }
        }
        return null;
    }

    @Override
    public boolean isAbortable() {
        return true;
    }

}

