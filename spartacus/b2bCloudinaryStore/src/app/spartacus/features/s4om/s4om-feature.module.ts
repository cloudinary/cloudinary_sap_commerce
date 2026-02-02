import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from "@spartacus/core";
import { s4omTranslationChunksConfig, s4omTranslationsEn } from "@spartacus/s4om/assets";
import { S4OM_FEATURE, S4omRootModule } from "@spartacus/s4om/root";

@NgModule({
  declarations: [],
  imports: [
    S4omRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      [S4OM_FEATURE]: {
        module: () =>
          import('@spartacus/s4om').then((m) => m.S4omModule),
      },
    }
  }),
  provideConfig(<I18nConfig>{
    i18n: {
      resources: { en: s4omTranslationsEn },
      chunks: s4omTranslationChunksConfig,
    },
  })
  ]
})
export class S4omFeatureModule { }
