package uk.ptr.cloudinary.tasks;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.HeaderTask;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.AbstractImpexRunnerTask;
import de.hybris.platform.catalog.daos.CatalogVersionDao;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.core.model.model.CloudinaryMediaTagUpdateJobModel;
import de.hybris.platform.jobs.GenericMaintenanceJobPerformable;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.impex.ImpExResource;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.impex.impl.StreamBasedImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import uk.ptr.cloudinary.constants.CloudinarymediacoreConstants;
import uk.ptr.cloudinary.cronjob.CloudinaryMediaTagUpdateJob;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class AbstractCloudinaryImpexRunnerTask extends AbstractImpexRunnerTask {

    private static final Logger LOG = Logger.getLogger(AbstractCloudinaryImpexRunnerTask.class);

    @Resource
    private SessionService sessionService;

    @Resource
    private ImportService importService;

    @Resource
    private CloudinaryMediaTagUpdateJob cloudinaryMediaTagUpdateJob;

    @Resource
    private ModelService modelService;

    @Resource
    private CatalogVersionDao catalogVersionDao;

    @Resource
    private CatalogSynchronizationService catalogSynchronizationService;

    @Resource
    private CronJobService cronJobService;

    @Override
    public BatchHeader execute(BatchHeader header)  throws FileNotFoundException {
        Assert.notNull(header);
        Assert.notNull(header.getEncoding());
        if (CollectionUtils.isNotEmpty(header.getTransformedFiles())) {
            final Session localSession = sessionService.createNewSession();
            try {
                for (final File file : header.getTransformedFiles()) {
                    super.processFile(file, header.getEncoding());
                }
            }
                finally
            {
                sessionService.closeSession(localSession);
                triggerUpdateTagJob();
            }
        }
        return header;
    }

    private void triggerUpdateTagJob() {

        Collection<CatalogVersionModel> catalogVersionModels = catalogVersionDao.findCatalogVersions("apparelContentCatalog", CloudinarymediacoreConstants.VERSION_ONLINE);

        List<CatalogVersionModel> onlineCatalogs = new ArrayList<>();

        for (CatalogVersionModel c : catalogVersionModels) {
            if (c.getVersion().equalsIgnoreCase(CloudinarymediacoreConstants.VERSION_ONLINE)) {
                onlineCatalogs.add(c);
            }
        }

        CloudinaryMediaTagUpdateJobModel cloudinaryMediaTagUpdateJobModel = modelService.create(CloudinaryMediaTagUpdateJobModel.class);
        cloudinaryMediaTagUpdateJobModel.setCode(UUID.randomUUID().toString());
        cloudinaryMediaTagUpdateJobModel.setActive(Boolean.TRUE);
        cloudinaryMediaTagUpdateJobModel.setJob(cronJobService.getJob("cloudinaryMediaTagUpdateJob"));
        cloudinaryMediaTagUpdateJobModel.setCatalogVersion(onlineCatalogs);
        modelService.save(cloudinaryMediaTagUpdateJobModel);

        cloudinaryMediaTagUpdateJob.perform(cloudinaryMediaTagUpdateJobModel);
    }

}



