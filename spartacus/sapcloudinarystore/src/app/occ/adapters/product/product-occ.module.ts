import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { provideDefaultConfig } from '@spartacus/core';
import { PRODUCT_NORMALIZER } from '@spartacus/core';
import { ProductAdapter } from '@spartacus/core';
import { PRODUCT_REFERENCES_NORMALIZER } from '@spartacus/core';
import { ProductReferencesAdapter } from '@spartacus/core';
import { ProductReviewsAdapter } from '@spartacus/core';
import { PRODUCT_SEARCH_PAGE_NORMALIZER } from '@spartacus/core';
import { ProductSearchAdapter } from '@spartacus/core';
import { OccProductReferencesListNormalizer } from '@spartacus/core';
import { OccProductSearchPageNormalizer } from '@spartacus/core';
import { ProductImageNormalizer } from '@spartacus/core';
import { OccProductReferencesAdapter } from '@spartacus/core';
import { OccProductReviewsAdapter } from '@spartacus/core';
import { OccProductSearchAdapter } from '@spartacus/core';
import { OccProductAdapter } from './occ-product.adapter';
import { ProductNameNormalizer } from '@spartacus/core';
import { defaultOccProductConfig } from './default-occ-product-config';
import '@spartacus/core';
import {ProductCloudinaryNormalizer} from './converters/product-cloudinary-normalizer';

@NgModule({
  imports: [CommonModule, HttpClientModule],
  providers: [
    provideDefaultConfig(defaultOccProductConfig),
    {
      provide: ProductAdapter,
      useClass: OccProductAdapter,
    },
    {
      provide: PRODUCT_NORMALIZER,
      useExisting: ProductImageNormalizer,
      multi: true,
    },
    {
      provide: PRODUCT_NORMALIZER,
      useExisting: ProductNameNormalizer,
      multi: true,
    },
    {
      provide: ProductReferencesAdapter,
      useClass: OccProductReferencesAdapter,
    },
    {
      provide: PRODUCT_REFERENCES_NORMALIZER,
      useExisting: OccProductReferencesListNormalizer,
      multi: true,
    },
    {
      provide: ProductSearchAdapter,
      useClass: OccProductSearchAdapter,
    },
    {
      provide: PRODUCT_SEARCH_PAGE_NORMALIZER,
      useExisting: OccProductSearchPageNormalizer,
      multi: true,
    },
    {
      provide: ProductReviewsAdapter,
      useClass: OccProductReviewsAdapter,
    },
    {
      provide: PRODUCT_NORMALIZER,
      useExisting: ProductCloudinaryNormalizer,
      multi: true,
    }
  ],
})
export class ProductOccModule {}
