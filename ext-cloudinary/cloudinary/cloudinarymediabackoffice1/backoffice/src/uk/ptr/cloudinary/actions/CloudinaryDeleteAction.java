package uk.ptr.cloudinary.actions;


import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectDeletionException;
import de.hybris.platform.core.model.media.MediaModel;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacadeOperationResult;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectAccessException;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;
import uk.ptr.cloudinary.service.UploadApiService;




public class CloudinaryDeleteAction implements CockpitAction<Object, Object>
{
    private static final Logger LOG = LoggerFactory.getLogger(CloudinaryDeleteAction.class);
    @Resource
    public ObjectFacade objectFacade;

    @Resource
    private PermissionFacade permissionFacade;

    @Resource
    private NotificationService notificationService;

    @Resource
    private UploadApiService uploadApiService;
    
    @Resource
    private CloudinaryConfigDao cloudinaryConfigDao;

    public ActionResult<Object> perform(ActionContext<Object> ctx) {
        ObjectFacadeOperationResult result = new ObjectFacadeOperationResult();

        List<Object> ctxObjects = this.getDataAsCollection(ctx);
        try {
            CloudinaryConfigModel cloudinaryConfigModel = cloudinaryConfigDao.getCloudinaryConfigModel();

            if(cloudinaryConfigModel != null && BooleanUtils.isTrue(cloudinaryConfigModel.getEnableCloudinary())){
            for(Object ctxObject:ctxObjects){
                    if(ctxObject instanceof MediaModel){
                        MediaModel mediaModel = (MediaModel)ctxObject;
                        if(mediaModel.getCloudinaryPublicId() != null) {
                            try {
                                Map mapResponse  = uploadApiService.deleteAsset(cloudinaryConfigModel.getCloudinaryURL(), mediaModel.getCloudinaryPublicId());
                                String response = (String) mapResponse.get("result");
                                if (response.equalsIgnoreCase("ok") || response.equalsIgnoreCase("Not Found")) {
                                    result = this.objectFacade.delete(Collections.singletonList(ctxObject));
                                }
                            } catch (IOException e) {
                                LOG.debug("Cannot delete item", (Throwable) e);
                            }
                        }
                    }
            }
        }
            else {
                result = this.objectFacade.delete(ctxObjects);
            }
        }
        catch (RuntimeException ex) {
            result = this.addItemsToFailedObjects(ctxObjects, ex);
            LOG.debug("Cannot delete item", (Throwable)ex);
        }

        if (result == null) {
            return new ActionResult("error", ctxObjects);
        }
        if (result.hasError()) {
           this.showFailureNotification(ctx, (Map<Object, ObjectAccessException>) result.getFailedObjects().stream().collect(Collectors.toMap(e -> e, result::getErrorForObject)));
        }
        if (result.countSuccessfulObjects() > 0) {
            this.showSuccessNotification(ctx, ctxObjects);
        }
        return new ActionResult(result.countSuccessfulObjects() < 1 ? "error" : "success", ctxObjects);
    }

    private List<Object> getDataAsCollection(ActionContext<Object> ctx) {
        ArrayList<Object> ctxObjects = new ArrayList<Object>();
        if (ctx.getData() instanceof Collection) {
            ctxObjects.addAll((Collection)ctx.getData());
        } else {
            ctxObjects.add(ctx.getData());
        }
        return ctxObjects;
    }

    private ObjectFacadeOperationResult<Object> addItemsToFailedObjects(List<Object> ctxObjects, RuntimeException ex) {
        ObjectFacadeOperationResult result = new ObjectFacadeOperationResult();
        ctxObjects.forEach(obj -> result.addFailedObject(obj, new ObjectAccessException(ex.getMessage(), (Throwable)ex)));
        return result;
    }

    @Deprecated
    protected String getNotificationSource(ActionContext<Object> ctx) {
        return this.getNotificationService().getWidgetNotificationSource(ctx);
    }

    protected void showSuccessNotification(ActionContext<Object> ctx, List<Object> deletedObjects) {
        String notificationSource = this.getNotificationSource(ctx);
        this.getNotificationService().notifyUser(notificationSource, "RemoveObject", NotificationEvent.Level.SUCCESS, new Object[]{deletedObjects});
    }

    protected void showFailureNotification(ActionContext<Object> ctx, Map<Object, ObjectAccessException> problems) {
        String notificationSource = this.getNotificationSource(ctx);
        this.getNotificationService().notifyUser(notificationSource, "RemoveObject", NotificationEvent.Level.FAILURE, new Object[]{problems});
    }

    public boolean canPerform(ActionContext<Object> ctx) {
        if (ctx.getData() == null) {
            return false;
        }
        if (ctx.getData() instanceof Collection) {
            Collection selectedItems = (Collection)ctx.getData();
            ObjectFacadeOperationResult reloadResult = this.getObjectFacade().reload(selectedItems);
            List refreshedItems = reloadResult.getSuccessfulObjects();
            return this.isCollectionDeletable(refreshedItems);
        }
        return this.hasPermissionAndObjectIsPersisted(ctx.getData());
    }

    protected boolean isCollectionDeletable(Collection<?> collection) {
        return CollectionUtils.isNotEmpty(collection) && collection.stream().allMatch(this::hasPermissionAndObjectIsPersisted);
    }

    protected boolean hasPermissionAndObjectIsPersisted(Object object) {
        return this.permissionFacade.canRemoveInstance(object) && !this.objectFacade.isNew(object);
    }

    public boolean needsConfirmation(ActionContext<Object> ctx) {
        return true;
    }

    public String getConfirmationMessage(ActionContext<Object> ctx) {
        return ctx.getLabel("delete.confirm");
    }

    protected ObjectFacade getObjectFacade() {
        return this.objectFacade;
    }

    public void setObjectFacade(ObjectFacade objectFacade) {
        this.objectFacade = objectFacade;
    }

    protected PermissionFacade getPermissionFacade() {
        return this.permissionFacade;
    }

    public void setPermissionFacade(PermissionFacade permissionFacade) {
        this.permissionFacade = permissionFacade;
    }

    protected NotificationService getNotificationService() {
        return this.notificationService;
    }

    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
