import { ProductScope } from '@spartacus/core';
import { OccConfig } from '@spartacus/core';

export const defaultOccProductConfig: OccConfig = {
  backend: {
    occ: {
      endpoints: {
        product: {
          default:
            'products/${productCode}?fields=DEFAULT,averageRating,images(FULL),classifications,manufacturer,numberOfReviews,categories(FULL),baseOptions,baseProduct,variantOptions,variantType,sapCCProductCode,spinSetCode',
          list:
            'products/${productCode}?fields=code,name,summary,price(formattedValue),images(DEFAULT,galleryIndex),sapCCProductCode,spinSetCode',
          details:
            'products/${productCode}?fields=averageRating,stock(DEFAULT),description,availableForPickup,code,url,price(DEFAULT),numberOfReviews,manufacturer,categories(FULL),priceRange,multidimensional,configuratorType,configurable,tags,images(FULL),sapCCProductCode,spinSetCode',
          attributes: 'products/${productCode}?fields=classifications',
          variants:
            'products/${productCode}?fields=name,purchasable,baseOptions(DEFAULT),baseProduct,variantOptions(DEFAULT),variantType,sapCCProductCode,spinSetCode',
        },
        productReviews: 'products/${productCode}/reviews',
        // Uncomment this when occ gets configured
        // productReferences:
        //   'products/${productCode}/references?fields=DEFAULT,references(target(images(FULL)))&referenceType=${referenceType}',
        productReferences:
          'products/${productCode}/references?fields=DEFAULT,references(target(images(FULL)))',
        // tslint:disable:max-line-length
        productSearch:
          'products/search?fields=products(code,name,summary,price(FULL),images(DEFAULT),stock(FULL),averageRating,variantOptions),facets,breadcrumbs,pagination(DEFAULT),sorts(DEFAULT),freeTextSearch,currentQuery',
        // tslint:enable
        productSuggestions: 'products/suggestions',
      },
    },
    loadingScopes: {
      product: {
        details: {
          include: [ProductScope.LIST, ProductScope.VARIANTS],
        },
      },
    },
  },
};
