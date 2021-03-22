import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CloudinaryConfig } from '../../../model/cloudinaryconfig.model';
import { CloudinaryConfigAdapter } from './cloudinaryconfig.adapter';
//import { ScopedProductData } from './scoped-product-data';

@Injectable({
  providedIn: 'root',
})
export class CloudinaryConfigConnector {
  constructor(protected adapter: CloudinaryConfigAdapter) {
    console.log("in cld config adapter");
  }

  get(scope = ''): Observable<CloudinaryConfig> {
    console.log("in cld config adapter get");
    return this.adapter.load(scope);
  }

  // getMany(products: ScopedProductData[]): ScopedProductData[] {
  //   if (!this.adapter.loadMany) {
  //     return products.map((product) => ({
  //       ...product,
  //       data$: this.adapter.load(product.code, product.scope),
  //     }));
  //   }

  //   return this.adapter.loadMany(products);
  // }
}
