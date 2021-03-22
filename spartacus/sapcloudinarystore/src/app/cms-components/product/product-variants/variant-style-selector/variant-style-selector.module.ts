import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { UrlModule, I18nModule } from '@spartacus/core';
import { VariantStyleSelectorComponent } from './variant-style-selector.component';
import { RouterModule } from '@angular/router';
import { CmsConfig, ConfigModule, provideDefaultConfig } from '@spartacus/core';

@NgModule({
  imports: [
    CommonModule, 
    RouterModule, 
    UrlModule, 
    I18nModule,
    ConfigModule.withConfig({
    cmsComponents: {
      VariantStyleSelectorComponent: {
        component: VariantStyleSelectorComponent,
      },
    },
  } as CmsConfig)
],
  declarations: [VariantStyleSelectorComponent],
  entryComponents: [VariantStyleSelectorComponent],
  exports: [VariantStyleSelectorComponent],
})
export class VariantStyleSelectorModule {}
