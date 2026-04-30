import { NgModule } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { ProductImageNormalizer } from '@spartacus/core';
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
  ],
  providers: [
    {
      provide: ProductImageNormalizer,
      useClass: CustomProductImageNormalizer,
    }
  ]
})
export class AppModule { }
