package uk.ptr.cloudinary.tasks.runner;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.tasks.context.CloudinaryMediaSyncContext;


public class CloudinaryMediaSyncTaskRunner implements TaskRunner<TaskModel>
{
    private static final Logger LOG = Logger.getLogger(CloudinaryMediaSyncTaskRunner.class);

    @Resource
    private CatalogSynchronizationService catalogSynchronizationService;

    @Resource
    private ModelService modelService;

    @Resource
    private CatalogVersionService catalogVersionService;


    @Override
    public void run(final TaskService taskService, final TaskModel taskModel) throws RetryLaterException
    {
        if(taskModel.getContext() instanceof CloudinaryMediaSyncContext){
            CloudinaryMediaSyncContext context = (CloudinaryMediaSyncContext)taskModel.getContext();
            Set<PK> itemsToSync = context.getItemsToSync();
            if(CollectionUtils.isNotEmpty(itemsToSync)){
                List<ItemModel> syncItemModels = new ArrayList<>();
                for(PK itemPK:itemsToSync)
                {
                    try
                    {
                        MediaModel media = (MediaModel) modelService.get(itemPK);
                        syncItemModels.add(media);
                    }catch(ModelLoadingException ex){
                        LOG.error("Failed to sync media with PK '" + itemPK + "'.", ex);
                    }
                }

                if(CollectionUtils.isNotEmpty(syncItemModels))
                {
                    MediaModel media = (MediaModel) syncItemModels.stream().findFirst().get();
                    CatalogVersionModel onlineVersion = catalogVersionService.getCatalogVersion(media.getCatalogVersion().getCatalog().getId(), CloudinarymediacoreConstants.VERSION_ONLINE);
                    SyncItemJobModel syncJobModel = catalogSynchronizationService.getSyncJob(media.getCatalogVersion(), onlineVersion, null);
                    catalogSynchronizationService.performSynchronization(syncItemModels, syncJobModel, getSyncConfig());
                    LOG.debug("Sync media from staged to Online " + media.getCode());
                }

            }
        }
    }

    private SyncConfig getSyncConfig() {
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

    @Override
    public void handleError(final TaskService taskService, final TaskModel taskModel, final Throwable throwable)
    {
        LOG.error("Failed to sync media from '" + taskModel.getContextItem() + "'.", throwable);
    }
}
