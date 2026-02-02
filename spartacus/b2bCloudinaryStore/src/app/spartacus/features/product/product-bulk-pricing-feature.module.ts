import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from "@spartacus/core";
import { bulkPricingTranslationChunksConfig, bulkPricingTranslationsEn } from "@spartacus/product/bulk-pricing/assets";
import { BulkPricingRootModule, PRODUCT_BULK_PRICING_FEATURE } from "@spartacus/product/bulk-pricing/root";

@NgModule({
  declarations: [],
  imports: [
    BulkPricingRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      [PRODUCT_BULK_PRICING_FEATURE]: {
        module: () =>
          import('@spartacus/product/bulk-pricing').then((m) => m.BulkPricingModule),
      },
    }
  }),
  provideConfig(<I18nConfig>{
    i18n: {
      resources: { en: bulkPricingTranslationsEn },
      chunks: bulkPricingTranslationChunksConfig,
    },
  })
  ]
})
export class ProductBulkPricingFeatureModule { }
