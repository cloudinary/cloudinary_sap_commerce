package uk.ptr.cloudinary.service;

import de.hybris.platform.core.PK;

import java.util.Set;


public interface CloudinaryTaskService
{
    void createMediaSyncTask(Set<PK> itemsToSync);
}
