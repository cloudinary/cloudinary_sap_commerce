import { Observable } from 'rxjs';
import { CloudinaryConfig } from '../../../model/cloudinaryconfig.model';
//import { ScopedProductData } from './scoped-cloudinaryconfig-data';

export abstract class CloudinaryConfigAdapter {
  /**
   * Abstract method used to load product's details data.
   * Product's data can be loaded from alternative sources, as long as the structure
   * converts to the `Product`.
   *
   * @param productCode The `productCode` for given product
   * @param scope The product scope to load
   */
  abstract load(scope?: string): Observable<CloudinaryConfig>;

  /**
   * Abstract method used to load data for multiple product and scopes
   * Adapter is able to optimize necessary backend calls and load
   * products in the most efficient way possible.
   *
   * @param products
   */
  //abstract loadMany?(products: ScopedProductData[]): ScopedProductData[];
}
