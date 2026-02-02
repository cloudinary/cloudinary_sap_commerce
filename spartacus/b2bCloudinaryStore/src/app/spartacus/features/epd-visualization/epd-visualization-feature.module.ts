import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from "@spartacus/core";
import { epdVisualizationTranslationChunksConfig, epdVisualizationTranslationsEn } from "@spartacus/epd-visualization/assets";
import { EPD_VISUALIZATION_FEATURE, EpdVisualizationConfig, EpdVisualizationRootModule } from "@spartacus/epd-visualization/root";

@NgModule({
  declarations: [],
  imports: [
    EpdVisualizationRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      [EPD_VISUALIZATION_FEATURE]: {
        module: () =>
          import('@spartacus/epd-visualization').then((m) => m.EpdVisualizationModule),
      },
    }
  }),
  provideConfig(<I18nConfig>{
    i18n: {
      resources: { en: epdVisualizationTranslationsEn },
      chunks: epdVisualizationTranslationChunksConfig,
    },
  }),
  provideConfig(<EpdVisualizationConfig>{
    epdVisualization: {
      ui5: {
        bootstrapUrl: "https://ui5.sap.com/1.120/resources/sap-ui-core.js"
      },

      apis: {
        baseUrl: "https://localhost:9002"
      }
    }
  })
  ]
})
export class EpdVisualizationFeatureModule { }
