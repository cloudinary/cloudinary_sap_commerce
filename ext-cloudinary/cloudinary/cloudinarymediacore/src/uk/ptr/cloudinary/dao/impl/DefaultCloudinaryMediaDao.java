package uk.ptr.cloudinary.dao.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.impl.DefaultMediaDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import uk.ptr.cloudinary.dao.CloudinaryMediaDao;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

public class DefaultCloudinaryMediaDao extends DefaultMediaDao implements CloudinaryMediaDao {

    @Resource
    FlexibleSearchService flexibleSearchService;

    @Override
    public List<MediaModel> findMediaByCloudinaryUrl() {

        final String query = "SELECT {" + MediaModel._TYPECODE + ":pk}  FROM { "
                + MediaModel._TYPECODE + " }  where {" +MediaModel._TYPECODE + ":cloudinaryPublicId} IS NULL" ;

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);;
        searchQuery.setResultClassList(Collections.singletonList(MediaModel.class));

        final SearchResult searchResult = flexibleSearchService.search(searchQuery);

        return searchResult.getResult();
    }

    @Override
    public List<MediaModel> findMediaForEmptyCloudinaryUrlAndMediaContainer(final CatalogVersionModel catalogVersion) {

        final String query = "SELECT {" + MediaModel._TYPECODE + ":pk}  FROM { "
                + MediaModel._TYPECODE + " }  where {" + MediaModel.CLOUDINARYPUBLICID + "} IS NULL AND {" + MediaModel.MEDIACONTAINER + "} IS NULL AND {"
                + MediaModel.CATALOGVERSION + "} = ?catalogVersion";

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);;
        searchQuery.setResultClassList(Collections.singletonList(MediaModel.class));
        searchQuery.addQueryParameter("catalogVersion", catalogVersion);

        final SearchResult searchResult = flexibleSearchService.search(searchQuery);

        return searchResult.getResult();
    }

    @Override
    public List<MediaModel> findMediaByCatalogVersionAndCloudinaryUrl(final CatalogVersionModel catalogVersion) {

        final String query = "SELECT {" + MediaModel._TYPECODE + ":pk}  FROM { "
                + MediaModel._TYPECODE + " }  where {" + MediaModel.CLOUDINARYPUBLICID + "} IS NULL AND {"
                + MediaModel.CATALOGVERSION + "} = ?catalogVersion ";

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);;
        searchQuery.setResultClassList(Collections.singletonList(MediaModel.class));
        searchQuery.addQueryParameter("catalogVersion", catalogVersion);

        final SearchResult searchResult = flexibleSearchService.search(searchQuery);

        return searchResult.getResult();
    }
}
