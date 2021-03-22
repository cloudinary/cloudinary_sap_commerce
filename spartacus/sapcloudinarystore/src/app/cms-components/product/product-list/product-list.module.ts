import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { ViewConfig } from '@spartacus/storefront';
import { ViewConfigModule } from '@spartacus/storefront';
import {
  ItemCounterModule,
  ListNavigationModule,
  MediaModule,
  SpinnerModule,
  StarRatingModule,
} from '@spartacus/storefront';
import { AddToCartModule } from '@spartacus/storefront';
import { IconModule } from '@spartacus/storefront';
import { defaultScrollConfig } from '@spartacus/storefront';
import { ProductVariantsModule } from '../product-variants/product-variants.module';
import { ProductListComponent } from './container/product-list.component'
import { ProductScrollComponent } from './container/product-scroll/product-scroll.component';
import { ProductFacetNavigationComponent } from '@spartacus/storefront';
import { ProductGridItemComponent } from './product-grid-item/product-grid-item.component';
import { ProductListItemComponent } from './product-list-item/product-list-item.component';
import { ProductViewComponent } from './product-view/product-view.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    MediaModule,
    AddToCartModule,
    ItemCounterModule,
    ListNavigationModule,
    UrlModule,
    I18nModule,
    StarRatingModule,
    IconModule,
    SpinnerModule,
    InfiniteScrollModule,
    ViewConfigModule,
    ProductVariantsModule,
    FeaturesConfigModule,
  ],
  providers: [
    provideDefaultConfig(<ViewConfig>defaultScrollConfig),
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CMSProductListComponent: {
          component: ProductListComponent,
        },
        ProductGridComponent: {
          component: ProductListComponent,
        },
        SearchResultsListComponent: {
          component: ProductListComponent,
        },
      },
    }),
  ],
  declarations: [
    ProductListComponent,
    ProductListItemComponent,
    ProductGridItemComponent,
    ProductViewComponent,
    ProductScrollComponent,
  ],
  exports: [
    ProductListComponent,
    ProductListItemComponent,
    ProductGridItemComponent,
    ProductViewComponent,
    ProductScrollComponent,
  ],
  entryComponents: [ProductListComponent],
})
export class ProductListModule {}
