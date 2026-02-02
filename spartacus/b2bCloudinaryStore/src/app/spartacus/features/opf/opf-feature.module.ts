import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from "@spartacus/core";
import { OPF_BASE_FEATURE, OpfBaseRootModule, OpfConfig } from "@spartacus/opf/base/root";
import { opfCheckoutTranslationChunksConfig, opfCheckoutTranslationsEn } from "@spartacus/opf/checkout/assets";
import { OPF_CHECKOUT_FEATURE, OpfCheckoutRootModule } from "@spartacus/opf/checkout/root";
import { OPF_CTA_FEATURE, OpfCtaRootModule } from "@spartacus/opf/cta/root";
import { OPF_GLOBAL_FUNCTIONS_FEATURE, OpfGlobalFunctionsRootModule } from "@spartacus/opf/global-functions/root";
import { opfPaymentTranslationChunksConfig, opfPaymentTranslationsEn } from "@spartacus/opf/payment/assets";
import { OPF_PAYMENT_FEATURE, OpfPaymentRootModule } from "@spartacus/opf/payment/root";
import { OPF_QUICK_BUY_FEATURE, OpfQuickBuyRootModule } from "@spartacus/opf/quick-buy/root";

@NgModule({
  declarations: [],
  imports: [
    OpfQuickBuyRootModule,
    OpfGlobalFunctionsRootModule,
    OpfCtaRootModule,
    OpfBaseRootModule,
    OpfPaymentRootModule,
    OpfCheckoutRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      [OPF_QUICK_BUY_FEATURE]: {
        module: () =>
          import('@spartacus/opf/quick-buy').then((m) => m.OpfQuickBuyModule),
      },
    }
  }),
  provideConfig(<CmsConfig>{
    featureModules: {
      [OPF_GLOBAL_FUNCTIONS_FEATURE]: {
        module: () =>
          import('@spartacus/opf/global-functions').then((m) => m.OpfGlobalFunctionsModule),
      },
    }
  }),
  provideConfig(<CmsConfig>{
    featureModules: {
      [OPF_CTA_FEATURE]: {
        module: () =>
          import('@spartacus/opf/cta').then((m) => m.OpfCtaModule),
      },
    }
  }),
  provideConfig(<CmsConfig>{
    featureModules: {
      [OPF_BASE_FEATURE]: {
        module: () =>
          import('@spartacus/opf/base').then((m) => m.OpfBaseModule),
      },
    }
  }),
  provideConfig(<OpfConfig>{
    opf: {
      opfBaseUrl: "PLACEHOLDER_OPF_BASE_URL",
      commerceCloudPublicKey: "PLACEHOLDER_COMMERCE_CLOUD_PUBLIC_KEY",
    },
  }),
  provideConfig(<CmsConfig>{
    featureModules: {
      [OPF_PAYMENT_FEATURE]: {
        module: () =>
          import('@spartacus/opf/payment').then((m) => m.OpfPaymentModule),
      },
    }
  }),
  provideConfig(<I18nConfig>{
    i18n: {
      resources: { en: opfPaymentTranslationsEn },
      chunks: opfPaymentTranslationChunksConfig,
    },
  }),
  provideConfig(<CmsConfig>{
    featureModules: {
      [OPF_CHECKOUT_FEATURE]: {
        module: () =>
          import('@spartacus/opf/checkout').then((m) => m.OpfCheckoutModule),
      },
    }
  }),
  provideConfig(<I18nConfig>{
    i18n: {
      resources: { en: opfCheckoutTranslationsEn },
      chunks: opfCheckoutTranslationChunksConfig,
    },
  })
  ]
})
export class OpfFeatureModule { }
