import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from "@spartacus/core";
import { multiDimensionalSelectorTranslationChunksConfig, multiDimensionalSelectorTranslationsEn } from "@spartacus/product-multi-dimensional/selector/assets";
import { PRODUCT_MULTI_DIMENSIONAL_SELECTOR_FEATURE, ProductMultiDimensionalSelectorRootModule } from "@spartacus/product-multi-dimensional/selector/root";

@NgModule({
  declarations: [],
  imports: [
    ProductMultiDimensionalSelectorRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      [PRODUCT_MULTI_DIMENSIONAL_SELECTOR_FEATURE]: {
        module: () =>
          import('@spartacus/product-multi-dimensional/selector').then((m) => m.ProductMultiDimensionalSelectorModule),
      },
    }
  }),
  provideConfig(<I18nConfig>{
    i18n: {
      resources: { en: multiDimensionalSelectorTranslationsEn },
      chunks: multiDimensionalSelectorTranslationChunksConfig,
    },
  })
  ]
})
export class ProductMultiDimensionalSelectorFeatureModule { }
