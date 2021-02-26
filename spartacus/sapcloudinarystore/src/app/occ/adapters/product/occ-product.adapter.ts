import { Injectable } from '@angular/core';
import { ProductAdapter } from '@spartacus/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { OccEndpointsService } from '@spartacus/core';
import { ConverterService } from '@spartacus/core';
import { PRODUCT_NORMALIZER } from '@spartacus/core';
import { Product } from '@spartacus/core';
import { ScopedProductData } from '@spartacus/core';
import { ScopedDataWithUrl } from '@spartacus/core';
import { Occ } from '../../occ-models';
import { OccRequestsOptimizerService } from '@spartacus/core';

@Injectable()
export class OccProductAdapter implements ProductAdapter {
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService,
    protected requestsOptimizer: OccRequestsOptimizerService
  ) {}

  load(productCode: string, scope?: string): Observable<Product> {
    return this.http
      .get(this.getEndpoint(productCode, scope))
      .pipe(this.converter.pipeable(PRODUCT_NORMALIZER));
  }

  loadMany(products: ScopedProductData[]): ScopedProductData[] {
    const scopedDataWithUrls: ScopedDataWithUrl[] = products.map((model) => ({
      scopedData: model,
      url: this.getEndpoint(model.code, model.scope),
    }));

    return this.requestsOptimizer
      .scopedDataLoad<Occ.Product>(scopedDataWithUrls)
      .map(
        (scopedProduct) =>
          ({
            ...scopedProduct,
            data$: scopedProduct.data$.pipe(
              this.converter.pipeable(PRODUCT_NORMALIZER)
            ),
          } as ScopedProductData)
      );
  }

  protected getEndpoint(code: string, scope?: string): string {
    return this.occEndpoints.getUrl(
      'product',
      {
        productCode: code,
      },
      undefined,
      scope
    );
  }
}
