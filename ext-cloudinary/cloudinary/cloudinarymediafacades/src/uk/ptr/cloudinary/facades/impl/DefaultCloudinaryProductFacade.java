package uk.ptr.cloudinary.facades.impl;

import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.product.impl.DefaultProductFacade;
import de.hybris.platform.commercefacades.product.impl.DefaultProductVariantFacade;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import javax.annotation.Resource;
import java.util.Collection;

public class DefaultCloudinaryProductFacade extends DefaultProductFacade<ProductModel> {

    @Resource
    private Converter<ProductModel, ProductData> cloudinaryProductImagesConverter;

    @Override
    public ProductData getProductForCodeAndOptions(final String code, final Collection<ProductOption> options)
    {
        final ProductModel productModel = getProductService().getProductForCode(code);
        final ProductData productData = getProductConverter().convert(productModel);

        if (options != null)
        {
            getProductConfiguredPopulator().populate(productModel, productData, options);
        }

        return cloudinaryProductImagesConverter.convert(productModel,productData);
    }
}
