import { NgModule } from '@angular/core';
import { translationChunksConfig, translationsEn } from '@spartacus/assets';
import {
  FeaturesConfig,
  I18nConfig,
  OccConfig,
  provideConfig,
  SiteContextConfig,
} from '@spartacus/core';
import { defaultB2bOccConfig } from '@spartacus/setup';
import {
  defaultCmsContentProviders,
  layoutConfig,
  mediaConfig,
} from '@spartacus/storefront';

@NgModule({
  declarations: [],
  imports: [],
  providers: [
    provideConfig(layoutConfig),
    provideConfig(mediaConfig),
    ...defaultCmsContentProviders,
    provideConfig(<OccConfig>{
      backend: {
        occ: {
          baseUrl: 'https://powertools.localhost:9002/',
        },
      },
    }),
    provideConfig(<SiteContextConfig>{
      context: {
        urlParameters: ['baseSite', 'language', 'currency'],
        baseSite: ['powertools-spa'],
        currency: ['USD', 'GBP', 'EUR'],
      },
    }),
    provideConfig(<I18nConfig>{
      i18n: {
        resources: { en: translationsEn },
        chunks: translationChunksConfig,
        fallbackLang: 'en',
      },
    }),
    provideConfig(<FeaturesConfig>{
      features: {
        level: '2211.37',
      },
    }),
    provideConfig(defaultB2bOccConfig),
    provideConfig(<OccConfig>{
      backend: {
        occ: {
          baseUrl: 'https://powertools.localhost:9002',
        },
      },
    }),
    provideConfig(<SiteContextConfig>{
      context: {},
    }),
  ],
})
export class SpartacusConfigurationModule {}
