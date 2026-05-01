import { Injectable } from '@angular/core';
import { OccConfig, ProductImageNormalizer } from '@spartacus/core';

@Injectable({
  providedIn: 'root',
})
export class CustomProductImageNormalizer extends ProductImageNormalizer {
  constructor(occConfig: OccConfig) {
    super(occConfig);
    this['normalizeImageUrl'] = this.customNormalizeImageUrl;
  }

  protected customNormalizeImageUrl(url: string): string {
    return url.replace(/,/g, '%2C');
  }
}
