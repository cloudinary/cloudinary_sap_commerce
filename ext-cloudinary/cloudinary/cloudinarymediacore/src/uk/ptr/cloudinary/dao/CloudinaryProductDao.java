package uk.ptr.cloudinary.dao;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

public interface CloudinaryProductDao {

    public List<ProductModel> findAllProductsForGalleryImagesAndCatalogVersion(final CatalogVersionModel catalogVersion);

    public ProductModel getProductForMediaContainer(String pk, CatalogVersionModel catalogVersion);
}
