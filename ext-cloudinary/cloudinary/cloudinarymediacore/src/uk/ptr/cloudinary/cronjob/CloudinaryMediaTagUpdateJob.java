package uk.ptr.cloudinary.cronjob;


import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.model.CloudinaryMediaTagUpdateJobModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import uk.ptr.cloudinary.CloudinaryMasterMediaUtil;
import uk.ptr.cloudinary.constants.GeneratedCloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.dao.CloudinaryProductDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.UpdateTagApiService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class CloudinaryMediaTagUpdateJob extends AbstractJobPerformable<CloudinaryMediaTagUpdateJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryMediaTagUpdateJob.class);

    @Resource
    private CloudinaryProductDao cloudinaryProductDao;

    @Resource
    private UpdateTagApiService updateTagApiService;

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Override
    public PerformResult perform(CloudinaryMediaTagUpdateJobModel cloudinaryMediaTagUpdateJobModel) {

        Collection<CatalogVersionModel> catalogVersionModels = cloudinaryMediaTagUpdateJobModel.getCatalogVersion();

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

        if (cloudinaryConfigModel.getEnableCloudinary() && !CollectionUtils.isEmpty(catalogVersionModels)) {
            LOG.debug("**************************************************************************************");
            LOG.debug("***********************Started Cloudinary Media Tag Update Job****************************");
            LOG.debug("**************************************************************************************");

            catalogVersionModels.stream().forEach(c -> {
                List<ProductModel> products = cloudinaryProductDao.findAllProductsForGalleryImagesAndCatalogVersion(c);
                if (!CollectionUtils.isEmpty(products)) {
                    products.stream().forEach(p -> {
                        for (MediaContainerModel mediaContainerModel : p.getGalleryImages()) {
                            MediaModel masterImage = CloudinaryMasterMediaUtil.getMasterImage(mediaContainerModel);
                            try {
                                if (masterImage != null) {
                                    updateTagApiService.updateTagOnAsests(masterImage.getCloudinaryPublicId(), p.getCode(), cloudinaryConfigModel.getCloudinaryURL(),masterImage.getCloudinaryResourceType());
                                    LOG.debug("Updated Tag on Media : "+ masterImage.getCode() + " for product code is " + p.getCode() + " and  PublicId is  " + masterImage.getCloudinaryPublicId());
                                }
                            } catch (IllegalArgumentException illegalException) {
                                LOG.error("Illegal Argument " + illegalException.getMessage(), illegalException);
                            } catch (Exception e) {
                                LOG.error("Exception occurred calling Tag API for Product code :  " +  p.getCode() + " and Media Code is " + masterImage.getCode() + " and PublicId is " + masterImage.getCloudinaryPublicId(), e);
                            }
                        }
                    });
                }
            });
        }
        LOG.debug("**************************************************************************************");
        LOG.debug("***********************Finished Cloudinary Media Tag Update Job***************************");
        LOG.debug("**************************************************************************************");

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    @Override
    public boolean isAbortable() {
        return true;
    }

}

