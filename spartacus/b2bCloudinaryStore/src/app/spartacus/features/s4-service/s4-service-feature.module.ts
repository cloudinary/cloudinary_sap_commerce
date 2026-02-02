import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from '@spartacus/core';
import {
  s4ServiceTranslationChunksConfig,
  s4ServiceTranslationsEn,
} from '@spartacus/s4-service/assets';
import { S4_SERVICE_FEATURE } from '@spartacus/s4-service/root';

@NgModule({
  declarations: [],
  imports: [],
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        [S4_SERVICE_FEATURE]: {
          module: () =>
            import('@spartacus/s4-service').then((m) => m.S4ServiceModule),
        },
      },
    }),
    provideConfig(<I18nConfig>{
      i18n: {
        resources: { en: s4ServiceTranslationsEn },
        chunks: s4ServiceTranslationChunksConfig,
      },
    }),
  ],
})
export class S4ServiceFeatureModule {}
