import { Injectable } from '@angular/core';
import { OccConfig } from '@spartacus/core';
import { Occ } from '../../../occ-models/occ.models';
import { Converter } from '@spartacus/core';
import { Product } from '@spartacus/core';

@Injectable({ providedIn: 'root' })
export class ProductCloudinaryNormalizer implements Converter<Occ.Product, Product> {
  constructor(protected config: OccConfig) {}

  convert(source: Occ.Product, target?: Product): Product {
    if (target === undefined) {
      target = { ...(source as any) };
    }
      target.sapCCProductCode = source.sapCCProductCode;
    
    return target;
  }

  protected normalize(name: string): string {
    return name.replace(/<[^>]*>/g, '');
  }
}
