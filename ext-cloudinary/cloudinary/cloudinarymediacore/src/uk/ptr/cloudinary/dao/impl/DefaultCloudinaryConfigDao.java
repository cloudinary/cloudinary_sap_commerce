package uk.ptr.cloudinary.dao.impl;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ptr.cloudinary.dao.CloudinaryConfigDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;

import java.util.Collections;

public class DefaultCloudinaryConfigDao implements CloudinaryConfigDao {

    @Autowired
    FlexibleSearchService flexibleSearchService;

    @Override
    public CloudinaryConfigModel getCloudinaryConfigModel() {

         final String query = "SELECT {" + CloudinaryConfigModel._TYPECODE + ":pk}  FROM { "
                + CloudinaryConfigModel._TYPECODE + " } ";

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);;
        searchQuery.setResultClassList(Collections.singletonList(CloudinaryConfigModel.class));

        final SearchResult searchResult = flexibleSearchService.search(searchQuery);
        if(searchResult.getResult() != null && ObjectUtils.isNotEmpty(searchResult.getResult()))
        {
            CloudinaryConfigModel cloudinaryConfigModel =  (CloudinaryConfigModel) searchResult.getResult().get(0);
            return cloudinaryConfigModel;
        }
        return null;
    }
}
