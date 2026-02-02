import { NgModule } from '@angular/core';
import { CmsConfig, provideConfig } from '@spartacus/core';
import {
  SMART_EDIT_FEATURE,
  SmartEditConfig,
  SmartEditRootModule,
} from '@spartacus/smartedit/root';

@NgModule({
  declarations: [],
  imports: [SmartEditRootModule],
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        [SMART_EDIT_FEATURE]: {
          module: () =>
            import('@spartacus/smartedit').then((m) => m.SmartEditModule),
        },
      },
    }),
    provideConfig(<SmartEditConfig>{
      smartEdit: {
        allowOrigin:
          'localhost:9002, localhost:443, localhost:4200, localhost:3000',
      },
    }),
  ],
})
export class SmartEditFeatureModule {}
