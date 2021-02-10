import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
//import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { provideDefaultConfigFactory } from '@spartacus/core';
import {
  StateConfig,
  StateTransferType,
} from '@spartacus/core';
import { CLOUDINARYCONFIG_FEATURE } from './cloudinaryconfig-state';
import { metaReducers, reducerProvider, reducerToken } from './reducers/index';

export function cloudinaryConfigStoreConfigFactory(): StateConfig {
  // if we want to reuse PRODUCT_FEATURE const in config, we have to use factory instead of plain object
  const config: StateConfig = {
    state: {
      ssrTransfer: {
        keys: { [CLOUDINARYCONFIG_FEATURE]: StateTransferType.TRANSFER_STATE },
      },
    },
  };
  return config;
}

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    StoreModule.forFeature(CLOUDINARYCONFIG_FEATURE, reducerToken, { metaReducers }),
    //EffectsModule.forFeature(effects),
  ],
  providers: [
    provideDefaultConfigFactory(cloudinaryConfigStoreConfigFactory),
    reducerProvider,
  ],
})
export class CloudinaryConfigStoreModule {}
