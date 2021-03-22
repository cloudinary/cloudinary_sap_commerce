import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
  CmsConfig,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { ProductVariantsComponent } from './product-variants.component';
import { RouterModule } from '@angular/router';
import { VariantStyleSelectorModule } from './variant-style-selector/variant-style-selector.module';
import { VariantSizeSelectorModule } from '@spartacus/storefront';
import { VariantColorSelectorModule } from '@spartacus/storefront';
import { VariantStyleIconsModule } from './variant-style-icons/variant-style-icons.module';
import { ProductVariantGuard } from '@spartacus/storefront';
import { VariantStyleIconsComponent } from './variant-style-icons/variant-style-icons.component';
import { ConfigModule } from '@spartacus/core';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    UrlModule,
    I18nModule,
    VariantStyleSelectorModule,
    VariantSizeSelectorModule,
    VariantColorSelectorModule,
    VariantStyleIconsModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        ProductVariantSelectorComponent: {
          component: ProductVariantsComponent,
          guards: [ProductVariantGuard],
        },
      },
    }),
  ],
  declarations: [ProductVariantsComponent],
  entryComponents: [ProductVariantsComponent],
  exports: [VariantStyleIconsComponent],
})
export class ProductVariantsModule {}
