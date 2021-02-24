import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { translations, translationChunksConfig } from '@spartacus/assets';
import { B2cStorefrontModule } from '@spartacus/storefront';
import { ProductImagesComponent } from './cms-components/product/product-images/product-images.component';
import {ProductImagesModule} from './cms-components/product/product-images/product-images.module';
import { CmsConfig, ConfigModule, provideDefaultConfig } from '@spartacus/core';
import {CloudinaryConfigOccModule, OccCloudinaryConfigAdapter} from './occ/adapters/cloudinaryconfig'
import { CustomOccModule } from './occ/occ.module';
import { defaultOccCloudinaryConfigConfig } from './occ/adapters/cloudinaryconfig/default-occ-cloudinaryconfig-config';
import { CloudinaryConfigAdapter } from './cloudinaryconfig';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CloudinaryClientTokenInterceptor } from './cms-components/product/interceptor/CloudinaryClientTokenInterceptor';
import {VariantStyleSelectorModule} from './cms-components/product/product-variants/variant-style-selector/variant-style-selector.module';
import {ProductVariantsModule} from './cms-components/product/product-variants/product-variants.module'
import {VariantStyleIconsModule} from './cms-components/product/product-variants/variant-style-icons/variant-style-icons.module'
import {ProductListModule} from './cms-components/product/product-list/product-list.module'
import {environment} from '../environments/environment'
import {CloudinaryVideoComponentModule} from './cms-components/cloudinary-video/cloudinary-video.module'

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    B2cStorefrontModule.withConfig({
      backend: {
        occ: {
          baseUrl: environment.occBaseUrl,
          prefix: '/rest/v2/'
        }
      },
      context: {
        currency: ['USD','GBP'],
        language: ['en'],
	urlParameters: ['baseSite', 'language', 'currency'],
        baseSite: ['electronics-spa','apparel-uk-spa'],
      },
      i18n: {
        resources: translations,
        chunks: translationChunksConfig,
        fallbackLang: 'en'
      },
      features: {
        level: '2.1'
      }
    }),
    //CustomOccModule,
    //CloudinaryConfigOccModule,
    ProductListModule,
    ProductVariantsModule,
    VariantStyleIconsModule,
    VariantStyleSelectorModule,
    ProductImagesModule,
    HttpClientModule,
    CloudinaryVideoComponentModule
  ],
  providers: [
    //{ provide: HTTP_INTERCEPTORS, useClass: CloudinaryClientTokenInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

