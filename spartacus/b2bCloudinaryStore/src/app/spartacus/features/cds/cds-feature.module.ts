import { NgModule } from '@angular/core';
import { CdsConfig, CdsModule } from "@spartacus/cds";
import { cdsTranslationChunksConfig, cdsTranslationsEn } from "@spartacus/cds/assets";
import { I18nConfig, provideConfig } from "@spartacus/core";

@NgModule({
  declarations: [],
  imports: [
    CdsModule.forRoot()
  ],
  providers: [provideConfig(<I18nConfig>{
    i18n: {
      resources: { en: cdsTranslationsEn },
      chunks: cdsTranslationChunksConfig,
    },
  }),
  provideConfig(<CdsConfig>{
    cds: {
      tenant: 'TENANT_PLACEHOLDER',
      baseUrl: 'https://localhost:9002',
      endpoints: {
        strategyProducts: '/strategy/${tenant}/strategies/${strategyId}/products',
        searchIntelligence:
          '/search-intelligence/v1/sites/${cdsSiteId}/trendingSearches',
      },
      merchandising: {
        defaultCarouselViewportThreshold: 80,
      },
    },
  }),
  provideConfig(<CdsConfig>{
    cds: {
      profileTag: {
        javascriptUrl:
          'PROFILE_TAG_LOAD_URL_PLACEHOLDER',
        configUrl:
          'PROFILE_TAG_CONFIG_URL_PLACEHOLDER',
        allowInsecureCookies: true,
        sciEnabled: undefined
      },
    },
  })
  ]
})
export class CdsFeatureModule { }
