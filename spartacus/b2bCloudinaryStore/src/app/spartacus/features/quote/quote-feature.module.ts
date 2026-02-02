import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from "@spartacus/core";
import { quoteTranslationChunksConfig, quoteTranslationsEn } from "@spartacus/quote/assets";
import { QUOTE_CART_GUARD_FEATURE, QUOTE_FEATURE, QUOTE_REQUEST_FEATURE, QuoteRootModule } from "@spartacus/quote/root";

@NgModule({
  declarations: [],
  imports: [
    QuoteRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      [QUOTE_FEATURE]: {
        module: () =>
          import('@spartacus/quote').then((m) => m.QuoteModule),
      },
    }
  }),
  provideConfig(<CmsConfig>{
    featureModules: {
      [QUOTE_CART_GUARD_FEATURE]: {
        module: () =>
          import('@spartacus/quote/components/cart-guard').then((m) => m.QuoteCartGuardComponentModule),
      },
    }
  }),
  provideConfig(<CmsConfig>{
    featureModules: {
      [QUOTE_REQUEST_FEATURE]: {
        module: () =>
          import('@spartacus/quote/components/request-button').then((m) => m.QuoteRequestButtonModule),
      },
    }
  }),
  provideConfig(<I18nConfig>{
    i18n: {
      resources: { en: quoteTranslationsEn },
      chunks: quoteTranslationChunksConfig,
    },
  })
  ]
})
export class QuoteFeatureModule { }
