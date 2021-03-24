package uk.ptr.cloudinary.dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.jalo.media.Media;

import java.util.List;

public interface CloudinaryMediaDao {

    public List<MediaModel> findMediaByCloudinaryUrl();

    public List<MediaModel> findMediaForEmptyCloudinaryUrlAndMediaContainer(final CatalogVersionModel catalogVersion);

    public  List<MediaModel> findMediaByCatalogVersionAndCloudinaryUrl(final CatalogVersionModel catalogVersion);
}
