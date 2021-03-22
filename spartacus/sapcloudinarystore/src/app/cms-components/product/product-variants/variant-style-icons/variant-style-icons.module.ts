import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { UrlModule, I18nModule,ConfigModule,CmsConfig } from '@spartacus/core';
import { VariantStyleIconsComponent } from './variant-style-icons.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    CommonModule, 
    RouterModule, 
    UrlModule, 
    I18nModule,
    ConfigModule.withConfig({
      cmsComponents: {
        VariantStyleIconsComponent: {
          component: VariantStyleIconsComponent,
        },
      },
    } as CmsConfig),
  ],
  declarations: [VariantStyleIconsComponent],
  entryComponents: [VariantStyleIconsComponent],
  exports: [VariantStyleIconsComponent],
})
export class VariantStyleIconsModule {}
