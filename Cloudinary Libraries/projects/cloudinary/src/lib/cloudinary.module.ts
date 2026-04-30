import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { CustomProductImageNormalizer } from './normalizers/product-image.normalizer';
import {
  CmsConfig,
  ProductImageNormalizer,
  provideConfig,
} from '@spartacus/core';
import { CloudinaryVideoPlayerComponent } from './components/cloudinary-video-player/cloudinary-video-player.component';
import {
  CLOUDINARY_CONFIG_ADAPTER,
  HttpCloudinaryConfigAdapter,
} from './connectors/cloudinary-config.adapter';
import {
  CLOUDINARY_CONFIG_CONNECTOR,
  DefaultCloudinaryConfigConnector,
} from './connectors/cloudinary-config.connector';
import { CLOUDINARY_CONFIG_SERVICE } from './product/cloudinary-config.token';
import { CurrentCloudinaryConfigService } from './product/current-cloudinaryconfig.service';

@NgModule({
  declarations: [CloudinaryVideoPlayerComponent],
  imports: [CommonModule, HttpClientModule],
  providers: [
    {
      provide: ProductImageNormalizer,
      useClass: CustomProductImageNormalizer,
    },
    provideConfig(<CmsConfig>{
      cmsComponents: {
        CloudinaryVideoComponent: {
          component: CloudinaryVideoPlayerComponent,
        },
      },
      backend: {
        occ: {
          endpoints: {
            // default path; can be overridden by consumer app
            cloudinaryConfiguration: 'cloudinary/configuration',
          },
        },
      },
    }),
    // connectors / adapters for cloudinary configuration
    {
      provide: CLOUDINARY_CONFIG_ADAPTER,
      useClass: HttpCloudinaryConfigAdapter,
    },
    {
      provide: CLOUDINARY_CONFIG_CONNECTOR,
      useClass: DefaultCloudinaryConfigConnector,
    },
    // ensure the token resolves to the service implementation
    {
      provide: CLOUDINARY_CONFIG_SERVICE,
      useExisting: CurrentCloudinaryConfigService,
    },
  ],
  exports: [CloudinaryVideoPlayerComponent],
})
export class CloudinaryModule {}
