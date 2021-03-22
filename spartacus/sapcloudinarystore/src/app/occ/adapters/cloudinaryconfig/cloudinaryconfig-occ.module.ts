import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { provideDefaultConfig } from '@spartacus/core';
// import { PRODUCT_NORMALIZER } from '../../../product/connectors/product/converters';
import { CloudinaryConfigAdapter } from '../../../cloudinaryconfig/connectors/cloudinaryconfig/cloudinaryconfig.adapter';
import { OccCloudinaryConfigAdapter } from './occ-cloudinaryconfig.adapter';
import { defaultOccCloudinaryConfigConfig } from './default-occ-cloudinaryconfig-config';
import './cloudinaryconfig-occ-config';

@NgModule({
  imports: [CommonModule, HttpClientModule],
  providers: [
    provideDefaultConfig(defaultOccCloudinaryConfigConfig),
    {
      provide: CloudinaryConfigAdapter,
      useClass: OccCloudinaryConfigAdapter,
    }
  ],
})
export class CloudinaryConfigOccModule {}
