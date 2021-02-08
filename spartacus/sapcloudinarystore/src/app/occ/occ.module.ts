import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ModuleWithProviders, NgModule } from '@angular/core';

import {OccModule} from '@spartacus/core';
import { CloudinaryConfigOccModule } from './adapters/cloudinaryconfig';

@NgModule({
  imports: [
    OccModule,
    CloudinaryConfigOccModule
  ],
})
export class CustomOccModule extends OccModule{
  static forRoot(): ModuleWithProviders<CustomOccModule> {
    return {
      ngModule: CustomOccModule,
      providers: [
        
      ],
    };
  }
}
