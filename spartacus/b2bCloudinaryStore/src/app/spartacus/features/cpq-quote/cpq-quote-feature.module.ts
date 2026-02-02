import { NgModule } from '@angular/core';
import { I18nConfig, provideConfig } from "@spartacus/core";
import { cpqquoteTranslationChunksConfig, cpqquoteTranslationsEn } from "@spartacus/cpq-quote/assets";
import { CpqQuoteRootModule } from "@spartacus/cpq-quote/root";

@NgModule({
  declarations: [],
  imports: [
    CpqQuoteRootModule
  ],
  providers: [provideConfig(<I18nConfig>{
    i18n: {
      resources: { en: cpqquoteTranslationsEn },
      chunks: cpqquoteTranslationChunksConfig,
    },
  })
  ]
})
export class CpqQuoteFeatureModule { }
