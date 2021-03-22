import { Injectable } from '@angular/core';
import { CloudinaryConfigAdapter } from '../../../cloudinaryconfig/connectors/cloudinaryconfig/cloudinaryconfig.adapter';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { OccEndpointsService } from '@spartacus/core';
import { ConverterService } from '../../../util/converter.service';
import { CLOUDINARYCONFIG_NORMALIZER } from '../../../cloudinaryconfig/connectors/cloudinaryconfig/converters';
import { CloudinaryConfig } from '../../../model/cloudinaryconfig.model';
//import { ScopedProductData } from '../../../cloudinaryconfig/connectors/cloudinaryconfig/scoped-cloudinaryconfig-data';
import { ScopedDataWithUrl } from '@spartacus/core';
import { Occ } from '@spartacus/core';
import { OccRequestsOptimizerService } from '@spartacus/core';

@Injectable()
export class OccCloudinaryConfigAdapter implements CloudinaryConfigAdapter {
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
    protected converter: ConverterService,
    protected requestsOptimizer: OccRequestsOptimizerService
  ) {console.log("in adapter");}

  load(scope?: string): Observable<CloudinaryConfig> {
    return this.http
      .get(this.getEndpoint(scope))
      .pipe(this.converter.pipeable(CLOUDINARYCONFIG_NORMALIZER));
  }

  // loadMany(cloudinaryconfigs: ScopedProductData[]): ScopedProductData[] {
  //   const scopedDataWithUrls: ScopedDataWithUrl[] = cloudinaryconfigs.map((model) => ({
  //     scopedData: model,
  //     url: this.getEndpoint(model.scope),
  //   }));

  //   return this.requestsOptimizer
  //     .scopedDataLoad<Occ.CloudinaryConfig>(scopedDataWithUrls)
  //     .map(
  //       (scopedcloudinaryConfig) =>
  //         ({
  //           ...scopedcloudinaryConfig,
  //           data$: scopedcloudinaryConfig.data$.pipe(
  //             this.converter.pipeable(CLOUDINARYCONFIG_NORMALIZER)
  //           ),
  //         } as ScopedProductData)
  //     );
  // }

  protected getEndpoint(code: string): string {
    console.log("endpoint->"+this.occEndpoints.getUrl(
      'cloudinaryConfig',
      undefined
    ));
    return this.occEndpoints.getUrl(
      'cloudinaryConfig',
      undefined
    );
  }
}
