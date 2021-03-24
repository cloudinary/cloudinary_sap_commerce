import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CmsConfig, ConfigModule, provideDefaultConfig } from '@spartacus/core';
import { OutletModule } from '@spartacus/storefront';
import { CarouselModule } from '@spartacus/storefront';
import { MediaModule } from '@spartacus/storefront';
//import { CmsConfig, ConfigModule } from '@spartacus/core';
//import { OutletModule } from '@angular/core';
//import { CarouselModule } from '../../../shared/components/carousel/index';
//import { MediaModule } from '../../../shared/components/media/media.module';
import { ProductImagesComponent } from './product-images.component';
import { CloudinaryConfigAdapter } from '../../../cloudinaryconfig/connectors/cloudinaryconfig/cloudinaryconfig.adapter';
import { OccCloudinaryConfigAdapter } from 'src/app/occ/adapters/cloudinaryconfig';
import { defaultOccCloudinaryConfigConfig } from 'src/app/occ/adapters/cloudinaryconfig/default-occ-cloudinaryconfig-config';
//import { OccCloudinaryConfigAdapter } from './occ-cloudinaryconfig.adapter';
//import { defaultOccCloudinaryConfigConfig } from './default-occ-cloudinaryconfig-config';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CloudinaryClientTokenInterceptor } from '../interceptor/CloudinaryClientTokenInterceptor';


@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    MediaModule,
    OutletModule,
    CarouselModule,
    ConfigModule.withConfig({
      cmsComponents: {
        ProductImagesComponent: {
          component: ProductImagesComponent,
        },
      },
    } as CmsConfig),
  ],
  providers: [
    // provideDefaultConfig(defaultOccCloudinaryConfigConfig),
    // {
    //   provide: CloudinaryConfigAdapter,
    //   useClass: OccCloudinaryConfigAdapter,
    // }

    { provide: HTTP_INTERCEPTORS, useClass: CloudinaryClientTokenInterceptor, multi: true }
  ],
  declarations: [ProductImagesComponent],
  entryComponents: [ProductImagesComponent],
  exports: [ProductImagesComponent],
})
export class ProductImagesModule {}
