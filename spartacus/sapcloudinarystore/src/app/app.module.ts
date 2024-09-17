import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import {
  provideHttpClient,
  withFetch,
  withInterceptorsFromDi,
} from '@angular/common/http';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { AppRoutingModule } from '@spartacus/storefront';
import { AppComponent } from './app.component';
import { SpartacusModule } from './spartacus/spartacus.module';
import { ProductImageNormalizer } from '@spartacus/core';
import { CustomProductImageNormalizer } from './spartacus/product-image.normalizer';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    StoreModule.forRoot({}),
    AppRoutingModule,
    EffectsModule.forRoot([]),
    SpartacusModule,
  ],
  providers: [
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    {
      provide: ProductImageNormalizer,
      useClass: CustomProductImageNormalizer
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}

