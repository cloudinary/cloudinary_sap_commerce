package uk.ptr.cloudinary.tasks.context;

import de.hybris.platform.core.PK;

import java.io.Serializable;
import java.util.Set;


public class CloudinaryMediaSyncContext implements Serializable
{
    private static final long serialVersionUID = 621128208666882555L;
    private Set<PK> itemsToSync;

    public CloudinaryMediaSyncContext(Set<PK> itemsToSync){
        this.itemsToSync = itemsToSync;
    }

    public Set<PK> getItemsToSync()
    {
        return itemsToSync;
    }
}
