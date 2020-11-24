package uk.ptr.cloudinary.dao;

import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.jalo.media.MediaContainer;

import java.util.List;

public interface CloudinaryMediaContainerDao {

    public List<MediaContainerModel> findMediaContainerByCatalogVersion(final CatalogVersionModel catalogVersionModel);

    public List<MediaContainerModel> findMediaContainers(final CatalogVersionModel catalogVersionModel);
}
