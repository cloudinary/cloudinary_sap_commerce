package uk.ptr.cloudinary.dao.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import uk.ptr.cloudinary.dao.CloudinaryProductDao;
import uk.ptr.cloudinary.model.CloudinaryConfigModel;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

public class DefaultCloudinaryProductDao implements CloudinaryProductDao {

    @Resource
    FlexibleSearchService flexibleSearchService;

    @Override
    public List<ProductModel> findAllProductsForGalleryImagesAndCatalogVersion(final CatalogVersionModel catalogVersion) {

        final String query = "SELECT {" + ProductModel.PK +"}  FROM { "
                + ProductModel._TYPECODE + " }  where {" + ProductModel.GALLERYIMAGES + "} IS NOT NULL AND {"
                + ProductModel.CATALOGVERSION + "} = ?catalogVersion";
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
        searchQuery.setResultClassList(Collections.singletonList(MediaModel.class));
        searchQuery.addQueryParameter("catalogVersion", catalogVersion);

        final SearchResult searchResult = flexibleSearchService.search(searchQuery);

        return searchResult.getResult();
    }

    @Override
    public ProductModel getProductForMediaContainer(String pk, CatalogVersionModel catalogVersion) {

        final String query = "SELECT {" + ProductModel._TYPECODE + ":pk}  FROM { "
                + ProductModel._TYPECODE + " }  where {" + ProductModel.GALLERYIMAGES + "} LIKE '%"+ pk + "%' AND {"
                + ProductModel.CATALOGVERSION + "} = ?catalogVersion";
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);;
        searchQuery.setResultClassList(Collections.singletonList(ProductModel.class));
        searchQuery.addQueryParameter("catalogVersion", catalogVersion);
        searchQuery.addQueryParameter("pk", pk);

        final SearchResult searchResult = flexibleSearchService.search(searchQuery);

        if(searchResult.getResult() != null  && searchResult.getResult().size() >0 )
        {
            ProductModel productModel =  (ProductModel) searchResult.getResult().get(0);
            return productModel;
        }
        return null;
    }
}
