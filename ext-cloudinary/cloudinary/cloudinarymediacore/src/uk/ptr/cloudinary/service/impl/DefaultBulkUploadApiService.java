package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.dto.BulkUploadRequestData;
import uk.ptr.cloudinary.dto.CloudinaryProductAssestData;
import uk.ptr.cloudinary.dto.MediaContainerData;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.BulkUploadApiService;
import uk.ptr.cloudinary.service.UpdateTagApiService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultBulkUploadApiService implements BulkUploadApiService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultBulkUploadApiService.class);

    @Resource
    private ProductDao productDao;

    @Resource
    private ModelService modelService;

    @Resource
    private UpdateTagApiService updateTagApiService;

    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource
    private CatalogSynchronizationService catalogSynchronizationService;

    @Resource
    private BaseSiteService baseSiteService;

    @Resource
    private UserService userService;

    @Resource
    private ImpersonationService impersonationService;

    @Override
    public void bulkAssetUpload(BulkUploadRequestData bulkUploadRequestData,String baseSiteId) {

        Set<CatalogVersionModel> catalogVersionModels = new HashSet<>();

        CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

        bulkUploadRequestData.getProductMediaAssest().stream().forEach(bulkUpload -> {

            List<ProductModel> productModels = impersonationService
                    .executeInContext(getImpersonationContext(baseSiteId), new ImpersonationService.Executor<List<ProductModel>, ImpersonationService.Nothing>()
                    {
                        @Override
                        public List<ProductModel> execute() {
                            return productDao.findProductsByCode(bulkUpload.getProductCode());
                        }
                    });


            ProductModel stagedProduct = getStagedProduct(productModels, baseSiteId);

            if (stagedProduct != null && org.apache.commons.collections.CollectionUtils.isEmpty(stagedProduct.getGalleryImages())) {
                MediaModel mediaModel = createMediaContainerAndAssociateWithProduct(stagedProduct, bulkUpload.getMediaContainers());
                if (mediaModel != null) {
                    UpdateTagOnProduct(cloudinaryConfigModel, bulkUpload, mediaModel.getCloudinaryPublicId());
                }
                catalogVersionModels.add(stagedProduct.getCatalogVersion());
            } else {
                List<String> mediaContainerCodes = stagedProduct.getGalleryImages().stream().map(MediaContainerModel::getQualifier).collect(Collectors.toList());
                List<MediaContainerData> mediaContainersData = bulkUpload.getMediaContainers().stream().collect(Collectors.toList());

                Set<MediaContainerModel> mediaContainerModels = new HashSet<>();

                mediaContainersData.stream().forEach(md -> {
                    if (md.getMediaContainerCode() != null && mediaContainerCodes.contains(md.getMediaContainerCode())) {
                        updateMasterMedia(md, stagedProduct);
                        mediaContainerModels.add(stagedProduct.getGalleryImages().get(0));
                        UpdateTagOnProduct(cloudinaryConfigModel, bulkUpload, md.getPublicId());
                        catalogVersionModels.add(stagedProduct.getCatalogVersion());
                    } else {
                        MediaContainerModel mediaContainer = createMediaContainer(md, stagedProduct);
                        mediaContainerModels.add(mediaContainer);
                        UpdateTagOnProduct(cloudinaryConfigModel, bulkUpload, md.getPublicId());
                        catalogVersionModels.add(stagedProduct.getCatalogVersion());
                    }
                });

                if(org.apache.commons.collections.CollectionUtils.isNotEmpty(stagedProduct.getGalleryImages())) {
                    mediaContainerModels.addAll(stagedProduct.getGalleryImages());
                }
                stagedProduct.setGalleryImages(new ArrayList<>(mediaContainerModels));
                modelService.save(stagedProduct);
            }
        });

        if (!CollectionUtils.isEmpty(catalogVersionModels)) {
            catalogVersionModels.forEach(c -> {
                CatalogVersionModel onlineVersion = catalogVersionService.getCatalogVersion(c.getCatalog().getId(), CloudinarymediacoreConstants.VERSION_ONLINE);
                catalogSynchronizationService.synchronizeFullyInBackground(c, onlineVersion);
            });
        }
    }

    private void UpdateTagOnProduct(CloudinaryConfigModel cloudinaryConfigModel, CloudinaryProductAssestData bulkUpload, String publicId) {
        try {
            updateTagApiService.updateTagOnAsests(publicId, bulkUpload.getProductCode(), cloudinaryConfigModel.getCloudinaryURL());
        } catch (IOException e) {
            LOG.error("Error occured during bulk upload while updating tag for product code["+bulkUpload.getProductCode()+"] and public id["+publicId+"]", e);
        }
    }

    private ProductModel getStagedProduct(List<ProductModel> productModels,String baseSiteId) {
        for (ProductModel productmodel : productModels) {
            CatalogVersionModel stagedVersion = catalogVersionService.getCatalogVersion(productmodel.getCatalogVersion().getCatalog().getId(), CloudinarymediacoreConstants.VERSION_STAGED);
            List<ProductModel> stagedProducts = impersonationService
                    .executeInContext(getImpersonationContext(baseSiteId), new ImpersonationService.Executor<List<ProductModel>, ImpersonationService.Nothing>()
                    {
                        @Override
                        public List<ProductModel> execute() {
                            return productDao.findProductsByCode(stagedVersion,productmodel.getCode());
                        }
                    });
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(stagedProducts)) {
                return stagedProducts.get(0);
            }
        }
        return null;
    }

    private void updateMasterMedia(MediaContainerData data, ProductModel product) {

        Boolean updatedMedia = false;
        MediaContainerModel mediaContainerModel = product.getGalleryImages().get(0);
        if (mediaContainerModel != null) {
            for (MediaModel m : mediaContainerModel.getMedias()) {
                if (m.getMediaFormat() == null) {
                    updateMasterMedia(data, m);
                    updatedMedia = true;
                    break;
                }
            }
        }
        if (!updatedMedia) {
            MediaModel mediaModel = createMasterMedia(data, product);

            mediaContainerModel.setMedias(Collections.singletonList(mediaModel));
            modelService.save(mediaContainerModel);
        }

    }

    private MediaContainerModel createMediaContainer(MediaContainerData md, ProductModel product) {

        MediaModel mediaModel = createMasterMedia(md, product);

        MediaContainerModel mediaContainerModel = this.modelService.create(MediaContainerModel.class);
        mediaContainerModel.setQualifier(UUID.randomUUID().toString());
        mediaContainerModel.setCatalogVersion(product.getCatalogVersion());
        mediaContainerModel.setMedias(Collections.singletonList(mediaModel));

        modelService.save(mediaContainerModel);
        return mediaContainerModel;
    }


    private MediaModel createMediaContainerAndAssociateWithProduct(ProductModel stagedProduct, List<MediaContainerData> mediaContainers) {

        MediaModel mediaModel = null;
        List<MediaContainerModel> list = new ArrayList<>();
        for (MediaContainerData data : mediaContainers) {

            mediaModel = createMasterMedia(data, stagedProduct);

            MediaContainerModel mediaContainerModel = this.modelService.create(MediaContainerModel.class);
            mediaContainerModel.setQualifier(UUID.randomUUID().toString());
            mediaContainerModel.setCatalogVersion(stagedProduct.getCatalogVersion());
            mediaContainerModel.setMedias(Collections.singletonList(mediaModel));

            modelService.save(mediaContainerModel);

            list.add(mediaContainerModel);
        }

        stagedProduct.setGalleryImages(list);
        modelService.save(stagedProduct);

        return mediaModel;
    }

    private MediaModel createMasterMedia(MediaContainerData data, ProductModel product) {
        MediaModel mediaModel = this.modelService.create(MediaModel.class);
        mediaModel.setCode(UUID.randomUUID().toString());
        mediaModel.setCatalogVersion(product.getCatalogVersion());
        updateMasterMedia(data, mediaModel);

        return mediaModel;
    }

    private MediaModel updateMasterMedia(MediaContainerData data, MediaModel media) {
        media.setCloudinaryType(data.getCloudinaryType());
        media.setCloudinaryResourceType(data.getResourceType());
        media.setCloudinaryPublicId(data.getPublicId());
        media.setCloudinaryMediaFormat(data.getCloudinaryMediaFormat());
        modelService.save(media);
        return media;
    }

    private ImpersonationContext getImpersonationContext(String baseSiteId)
    {
        final ImpersonationContext context = new ImpersonationContext();
        context.setUser(userService.getAdminUser());
        context.setSite(getBaseSite(baseSiteId));
        return context;
    }

    private BaseSiteModel getBaseSite(String baseSiteId)
    {
        return baseSiteService.getBaseSiteForUID(baseSiteId);
    }
}

