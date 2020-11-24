package uk.ptr.cloudinary.service.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;

import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ptr.cloudinary.service.CloudinaryTaskService;
import uk.ptr.cloudinary.tasks.context.CloudinaryMediaSyncContext;


public class DefaultCloudinaryTaskService implements CloudinaryTaskService
{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCloudinaryTaskService.class);

    @Resource
    private TaskService taskService;

    @Resource
    private ModelService modelService;

    @Override
    public void createMediaSyncTask(final Set<PK> itemsToSync)
    {
        TaskModel task = new TaskModel();
        task.setRunnerBean("cloudinaryMediaSyncTaskRunner");
        task.setContext(new CloudinaryMediaSyncContext(itemsToSync));
        modelService.save(task);
        //taskService.scheduleTask(task);
        LOG.debug("Task scheduled for media sync :" + task.getContext());
    }
}
