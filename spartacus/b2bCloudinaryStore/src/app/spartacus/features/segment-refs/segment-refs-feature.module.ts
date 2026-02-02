import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from "@spartacus/core";
import { SegmentRefsRootModule } from "@spartacus/segment-refs/root";

@NgModule({
  declarations: [],
  imports: [
    SegmentRefsRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      SegmentRefs: {
        module: () =>
          import('@spartacus/segment-refs').then((m) => m.SegmentRefsRootModule),
      },
    }
  })]
})
export class SegmentRefsFeatureModule { }
