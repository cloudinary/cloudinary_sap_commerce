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
import { AuthService } from '@spartacus/core';import {
  InterceptorUtil,
  USE_CLIENT_TOKEN,
} from '@spartacus/core';

@Injectable({
  providedIn: 'root',
})
export class CurrentCloudinaryConfigService {
  private baseUrl = "https://localhost:9002/rest/v2/apparel-uk-spa/cloudinary/configuration";

  constructor(
    private routingService: RoutingService,
    private cloudinaryConfigService: CloudinaryConfigService,
    private adapter:CloudinaryConfigAdapter,
    private http: HttpClient,
    private authService:AuthService
  ) {}

  //protected readonly DEFAULT_PRODUCT_SCOPE = ProductScope.DETAILS;

  /**
   * Will emit current product or null, if there is no current product (i.e. we are not on PDP)
   *
   * @param scopes
   */
  // getCloudinaryConfigService(
  //   scopes?: string
  // ): Observable<CloudinaryConfig | null> {
  //   console.log("hello in service");
  //   return this.cloudinaryConfigService.get(scopes);
  // }

  list(): Observable<CloudinaryConfig> {
    const url = `${this.baseUrl}`;
    //token: Observable<any> = this.authService.getClientToken();
    let headers = new HttpHeaders({
      'Content-Type': 'application/json',
    });
    
       headers = InterceptorUtil.createHeader(USE_CLIENT_TOKEN, true, headers);
    
  //   console.log(this.http.get<CloudinaryConfig>(url).subscribe(data => {
  //     console.log("iscloudinaryenabled-->"+data.isCloudinaryGalleryEnabled);
  // }));
    //responseData: this.http.get(url);
     return this.http.get(url).pipe(
      // Adapt each item in the raw data array
      map((data: any) => this.adapter.adapt(data))
    );
  }
}
