import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from "@spartacus/core";
import { OmfRootModule } from "@spartacus/omf/root";

@NgModule({
  declarations: [],
  imports: [
    OmfRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      Omf: {
        module: () =>
          import('@spartacus/omf').then((m) => m.OmfRootModule),
      },
    }
  })
  ]
})
export class OmfFeatureModule { }
