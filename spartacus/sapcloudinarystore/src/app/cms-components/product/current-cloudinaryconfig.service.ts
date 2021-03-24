import { Injectable } from '@angular/core';
import {
  Product,
  ProductScope,
  ProductService,
  RoutingService,
} from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { distinctUntilChanged, filter, map, switchMap } from 'rxjs/operators';
//import { CloudinaryConfig } from 'src/app/model';
import {CloudinaryConfigService} from '../../cloudinaryconfig';
import {CloudinaryConfig} from './model';
import {CloudinaryConfigAdapter} from './adapter/CloudinaryConfigAdapter';
import { HttpClient,HttpHeaders } from "@angular/common/http";
import { AuthService } from '@spartacus/core';
import {
  InterceptorUtil,
  USE_CLIENT_TOKEN,
} from '@spartacus/core';
import {OccEndpointsService} from '@spartacus/core';

@Injectable({
  providedIn: 'root',
})
export class CurrentCloudinaryConfigService {

  constructor(
    private routingService: RoutingService,
    private cloudinaryConfigService: CloudinaryConfigService,
    private adapter:CloudinaryConfigAdapter,
    private http: HttpClient,
    private authService:AuthService,
    private occEndpoints: OccEndpointsService
  ) {}

  //protected readonly DEFAULT_PRODUCT_SCOPE = ProductScope.DETAILS;

  /**
   * Will emit current product or null, if there is no current product (i.e. we are not on PDP)
   *
   * @param scopes
   */

  list(): Observable<CloudinaryConfig> {
    const url = `${this.occEndpoints.getBaseEndpoint()}/cloudinary/configuration`;
    
    let headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    
       headers = InterceptorUtil.createHeader(USE_CLIENT_TOKEN, true, headers);
     return this.http.get(url).pipe(
      // Adapt each item in the raw data array
      map((data: any) => this.adapter.adapt(data))
    );
  }
}
