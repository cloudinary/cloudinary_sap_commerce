import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfigModule } from '@spartacus/core';
import { CmsConfig } from '@spartacus/core';
import { CloudinaryVideoComponent } from './cloudinary-video.component';



@NgModule({
  declarations: [CloudinaryVideoComponent],
  imports: [
    CommonModule,
    ConfigModule.withConfig(<CmsConfig>{
      cmsComponents: {
        CloudinaryVideoComponent: {
          component: CloudinaryVideoComponent,
        },
      },
    })
  ],
  entryComponents: [CloudinaryVideoComponent],
  exports: [CloudinaryVideoComponent]
})
export class CloudinaryVideoComponentModule { }
