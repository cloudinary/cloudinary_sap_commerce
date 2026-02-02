import { NgModule } from '@angular/core';
import { I18nConfig, provideConfig } from "@spartacus/core";
import { dpTranslationChunksConfig, dpTranslationsEn } from "@spartacus/digital-payments/assets";

@NgModule({
  declarations: [],
  imports: [
  ],
  providers: [provideConfig(<I18nConfig>{
    i18n: {
      resources: { en: dpTranslationsEn },
      chunks: dpTranslationChunksConfig,
    },
  })
  ]
})
export class DigitalPaymentsFeatureModule { }
