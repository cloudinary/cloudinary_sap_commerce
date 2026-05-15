import { NgModule } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { ConfigModule, ProductImageNormalizer } from '@spartacus/core';
import { AppRoutingModule } from '@spartacus/storefront';
import { CustomProductImageNormalizer } from './spartacus/product-image.normalizer';
import { SpartacusModule } from './spartacus/spartacus.module';
import { CloudinaryModule } from '../../projects/cloudinary/src/public-api';

@NgModule({
  imports: [
    StoreModule.forRoot({}),
    AppRoutingModule,
    EffectsModule.forRoot([]),
    SpartacusModule,
    CloudinaryModule,
    ConfigModule.withConfig({
      backend: {
        occ: {
          endpoints: {
            product: {
              details:
                'products/${productCode}?fields=averageRating,stock(DEFAULT),description,availableForPickup,code,url,price(DEFAULT),numberOfReviews,manufacturer,categories(FULL),priceRange,multidimensional,tags,images(FULL),sapCCProductCode,spinSetCode',
            },
          },
        },
      },
    }),
  ],
  providers: [
    {
      provide: ProductImageNormalizer,
      useClass: CustomProductImageNormalizer,
    },
  ],
})
export class AppModule {}
