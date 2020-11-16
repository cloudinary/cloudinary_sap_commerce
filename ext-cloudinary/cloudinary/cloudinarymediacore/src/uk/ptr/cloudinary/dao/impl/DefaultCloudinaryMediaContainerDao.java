package uk.ptr.cloudinary.dao.impl;

import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.dao.impl.DefaultMediaContainerDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import uk.ptr.cloudinary.dao.CloudinaryMediaContainerDao;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

public class DefaultCloudinaryMediaContainerDao extends DefaultMediaContainerDao implements CloudinaryMediaContainerDao {

    @Resource
    FlexibleSearchService flexibleSearchService;

    @Override
    public List<MediaContainerModel> findMediaContainerByCatalogVersion(final CatalogVersionModel catalogVersion) {
        final String query = "SELECT {" + MediaContainerModel._TYPECODE + ":pk}  FROM { "
                + MediaContainerModel._TYPECODE + " }  where {" + MediaContainerModel.CATALOGVERSION + "} = ?catalogVersion ";

        System.out.println("DB Query  " + query);

        System.out.println("PK of catalog version " + catalogVersion.getPk());

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);;
        searchQuery.setResultClassList(Collections.singletonList(MediaModel.class));
        searchQuery.addQueryParameter("catalogVersion", catalogVersion);

        final SearchResult searchResult = flexibleSearchService.search(searchQuery);

        return searchResult.getResult();
    }
}
