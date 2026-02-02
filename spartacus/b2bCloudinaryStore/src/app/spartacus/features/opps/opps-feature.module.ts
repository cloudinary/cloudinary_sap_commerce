import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from "@spartacus/core";
import { OppsRootModule } from "@spartacus/opps/root";

@NgModule({
  declarations: [],
  imports: [
    OppsRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      Opps: {
        module: () =>
          import('@spartacus/opps').then((m) => m.OppsRootModule),
      },
    }
  })]
})
export class OppsFeatureModule { }
