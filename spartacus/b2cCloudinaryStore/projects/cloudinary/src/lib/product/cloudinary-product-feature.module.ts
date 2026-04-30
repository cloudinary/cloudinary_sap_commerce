import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductImagesModule, MediaModule } from '@spartacus/storefront';
import { ProductImagesComponent } from './cms-components/product/product-images/product-images.component';

@NgModule({
  declarations: [ProductImagesComponent],
  imports: [CommonModule, ProductImagesModule, MediaModule],
  exports: [ProductImagesComponent],
})
export class CloudinaryProductFeatureModule {}
