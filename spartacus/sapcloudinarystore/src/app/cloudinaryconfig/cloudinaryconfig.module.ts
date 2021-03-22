import { ModuleWithProviders, NgModule } from '@angular/core';
//import { PageMetaResolver } from '../cms/page/page-meta.resolver';
import { CloudinaryConfigStoreModule } from './store/cloudinaryconfig-store.module';

const pageTitleResolvers = [
  
];

@NgModule({
  imports: [CloudinaryConfigStoreModule],
})
export class ProductModule {
  static forRoot(): ModuleWithProviders<ProductModule> {
    return {
      ngModule: ProductModule,
      providers: [...pageTitleResolvers],
    };
  }
}
