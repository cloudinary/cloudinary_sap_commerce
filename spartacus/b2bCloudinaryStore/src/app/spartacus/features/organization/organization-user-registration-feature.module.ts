import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from "@spartacus/core";
import { organizationUserRegistrationTranslationChunksConfig, organizationUserRegistrationTranslationsEn } from "@spartacus/organization/user-registration/assets";
import { ORGANIZATION_USER_REGISTRATION_FEATURE, OrganizationUserRegistrationRootModule } from "@spartacus/organization/user-registration/root";

@NgModule({
  declarations: [],
  imports: [
    OrganizationUserRegistrationRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      [ORGANIZATION_USER_REGISTRATION_FEATURE]: {
        module: () =>
          import('./organization-user-registration-wrapper.module').then((m) => m.OrganizationUserRegistrationWrapperModule),
      },
    }
  }),
  provideConfig(<I18nConfig>{
    i18n: {
      resources: { en: organizationUserRegistrationTranslationsEn },
      chunks: organizationUserRegistrationTranslationChunksConfig,
    },
  })
  ]
})
export class OrganizationUserRegistrationFeatureModule { }
