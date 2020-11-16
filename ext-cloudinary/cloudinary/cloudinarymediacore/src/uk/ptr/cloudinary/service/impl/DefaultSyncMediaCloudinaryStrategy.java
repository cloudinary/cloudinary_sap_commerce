package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.catalog.CatalogVersionService;
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
    CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    UploadApiService uploadApiService;

    @Resource
    MediaService mediaService;

    @Resource
    CatalogVersionService catalogVersionService;

    @Resource
    CatalogSynchronizationService catalogSynchronizationService;

    @Resource
    ModelService modelService;

    @Resource
    MediaConversionService mediaConversionService;

    public MediaModel onDemandSyncMedia(final MediaModel mediaModel) throws Exception {

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

        CatalogVersionModel contentCatalogVersionStaged = catalogVersionService.getCatalogVersion(CloudinarymediacoreConstants.CONTENT_CATALOG_ID, CloudinarymediacoreConstants.VERSION_STAGED);
        CatalogVersionModel productCatalogVersionStaged = catalogVersionService.getCatalogVersion(CloudinarymediacoreConstants.PRODUCT_CATALOG_ID, CloudinarymediacoreConstants.VERSION_STAGED);
        CatalogVersionModel contentCatalogVersionOnline = catalogVersionService.getCatalogVersion(CloudinarymediacoreConstants.CONTENT_CATALOG_ID, CloudinarymediacoreConstants.VERSION_ONLINE);
        CatalogVersionModel productCatalogVersionOnline = catalogVersionService.getCatalogVersion(CloudinarymediacoreConstants.PRODUCT_CATALOG_ID, CloudinarymediacoreConstants.VERSION_ONLINE);

        MediaModel stagedMedia = getStagedMedia(mediaModel, contentCatalogVersionStaged, productCatalogVersionStaged, contentCatalogVersionOnline, productCatalogVersionOnline);

        if (stagedMedia != null && cloudinaryConfigModel.getEnableCloudinary()) {

            if (stagedMedia.getMediaContainer() == null) {
                MediaModel masterMedia = createMasterMedia(mediaModel);
                uploadMediaToCloudinary(cloudinaryConfigModel, masterMedia);
            }
            else if(stagedMedia.getMediaFormat() != null)
                {
                    MediaModel largestMedia = getMasterImage(stagedMedia);
                    if (largestMedia.getMediaFormat() != null && largestMedia.getCloudinaryURL() == null) {

                        MediaModel masterMedia = createMasterMedia(mediaModel);
                        LOG.info("Uploading stage master media to cloudinary " + stagedMedia.getCode());
                        uploadMediaToCloudinary(cloudinaryConfigModel, masterMedia);
                    }
                    if(!largestMedia.getCode().equals(stagedMedia.getCode())) {
                        stagedMedia =  updateOnDemandMedia(stagedMedia);
                }
            }
            if(stagedMedia.getCatalogVersion() == contentCatalogVersionStaged) {
                catalogSynchronizationService.synchronizeFully(contentCatalogVersionStaged, contentCatalogVersionOnline);
                LOG.info("Sync content media staged to Online " + stagedMedia.getCode());
            }
            if(stagedMedia.getCatalogVersion() == productCatalogVersionStaged) {
                catalogSynchronizationService.synchronizeFully(productCatalogVersionStaged, productCatalogVersionOnline);
                LOG.info("Sync product media staged to Online " + stagedMedia.getCode());
            }
        }
        return getOnlineMedia(stagedMedia, contentCatalogVersionStaged, productCatalogVersionStaged, contentCatalogVersionOnline, productCatalogVersionOnline);
    }

    private MediaModel createMasterMedia(MediaModel mediaModel) {
        MediaModel masterMedia = new MediaModel();
        masterMedia.setCode(mediaModel.getCode() + "_" + mediaModel.getMediaFormat().getQualifier());
        masterMedia.setMediaContainer(mediaModel.getMediaContainer());
        masterMedia.setCatalogVersion(mediaModel.getCatalogVersion());
        masterMedia.setURL(mediaModel.getURL());
        modelService.save(masterMedia);
        modelService.refresh(masterMedia);
        return masterMedia;
    }

    private MediaModel updateOnDemandMedia(MediaModel stagedMedia) {
        if(stagedMedia.getMediaFormat() == null)
          return stagedMedia;

        MediaContainerModel mediaContainerModel = stagedMedia.getMediaContainer();
        if(mediaContainerModel.getConversionGroup() == null || !isContainsConversionGroupForMediaformat(stagedMedia))
        {
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
        if(stagedMedia.getMediaContainer().getConversionGroup() != null && stagedMedia.getMediaContainer().getConversionGroup().getSupportedMediaFormats().contains(stagedMedia.getMediaFormat()))
        return true;
        else
            return false;
    }

    private MediaModel getStagedMedia(MediaModel mediaModel, CatalogVersionModel contentCatalogVersionStaged, CatalogVersionModel productCatalogVersionStaged, CatalogVersionModel contentCatalogVersionOnline, CatalogVersionModel productCatalogVersionOnline) {
        return  mediaModel.getCatalogVersion() == contentCatalogVersionOnline ?  mediaService.getMedia(contentCatalogVersionStaged, mediaModel.getCode()) :  mediaService.getMedia(productCatalogVersionStaged, mediaModel.getCode());
    }

    private MediaModel getOnlineMedia(MediaModel mediaModel, CatalogVersionModel contentCatalogVersionStaged, CatalogVersionModel productCatalogVersionStaged, CatalogVersionModel contentCatalogVersionOnline, CatalogVersionModel productCatalogVersionOnline) {
        return mediaModel.getCatalogVersion() == contentCatalogVersionStaged ? mediaService.getMedia(contentCatalogVersionOnline, mediaModel.getCode()) : mediaService.getMedia(productCatalogVersionOnline, mediaModel.getCode());
    }

    private MediaModel getMasterImage(MediaModel mediaModel){
        int imageSize = 0;
        MediaModel masterMedia = null;
        Collection<MediaModel> medias  = mediaModel.getMediaContainer().getMedias();
        for (MediaModel mediaModel1 : medias) {
            if(mediaModel1.getMediaFormat() == null)
            {
                return mediaModel1;
            }
            String s[] = mediaModel1.getMediaFormat().getTransformation().split(",");
            int temp  =  Integer.valueOf(s[0].replace("w_",""))*Integer.valueOf(s[1].replace("h_",""));
            if(imageSize < temp){
                imageSize = temp;
                masterMedia = mediaModel1;
            }
        }
        return masterMedia;
    }

    private void uploadMediaToCloudinary(CloudinaryConfigModel cloudinaryConfigModel, MediaModel media) {
        if(media.getCloudinaryURL() == null){
            try {
                uploadApiService.uploadAsset(cloudinaryConfigModel, media, "newAssets");
                LOG.info("Uplaoded Media " + media.getCode() + "cloudinaryUrl  " + media.getCloudinaryURL());

            }  catch (IllegalArgumentException illegalException) {
                LOG.error("Illegal Argument " + illegalException.getMessage(), illegalException);
            }
            catch (Exception e) {
                LOG.error("Exception occurred calling Upload  API " + e.getMessage() , e);
            }
        }
    }

}
