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
          'localhost:9002, localhost:443, localhost:4200, localhost:3000, powertools.local:9002, powertools.local:4200,powertools.localhost:9002, powertools.localhost:4200',
      },
    }),
  ],
})
export class SmartEditFeatureModule {}
