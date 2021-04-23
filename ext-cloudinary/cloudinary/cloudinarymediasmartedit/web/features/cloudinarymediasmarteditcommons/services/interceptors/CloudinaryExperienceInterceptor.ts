import { Injectable } from '@angular/core';
import {
  HttpUtils,
  IExperience,
  ISharedDataService,
  StringUtils,
  TypedMap
} from 'smarteditcommons';
import {
  CLOUDINARYWEBSERVICES_PATH,
  CONTEXT_CATALOG,
  CONTEXT_CATALOG_VERSION,
  CONTEXT_SITE_ID,
} from 'cloudinarymediasmarteditcommons/dao';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { from, Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Injectable()
export class CloudinaryExperienceInterceptor implements HttpInterceptor {

  constructor(
        private sharedDataService: ISharedDataService,
        private stringUtils: StringUtils,
        private httpUtils: HttpUtils
    ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        console.log('CloudinaryExperienceInterceptor - request url:', request.url);
        if (CLOUDINARYWEBSERVICES_PATH.test(request.url)) {
            return from(this.sharedDataService.get('experience')).pipe(
                switchMap((data: IExperience) => {
                    if (data) {
                        const keys: TypedMap<string> = {};
                        keys.CONTEXT_SITE_ID_WITH_COLON = data.siteDescriptor.uid;
                        keys.CONTEXT_CATALOG_VERSION_WITH_COLON =
                            data.catalogDescriptor.catalogVersion;
                        keys.CONTEXT_CATALOG_WITH_COLON = data.catalogDescriptor.catalogId;
                        keys[CONTEXT_SITE_ID] = data.siteDescriptor.uid;
                        keys[CONTEXT_CATALOG_VERSION] = data.catalogDescriptor.catalogVersion;
                        keys[CONTEXT_CATALOG] = data.catalogDescriptor.catalogId;

                        const newRequest = request.clone({
                            url: this.stringUtils.replaceAll(request.url, keys),
                            params:
                                request.params && typeof request.params === 'object'
                                    ? this.httpUtils.transformHttpParams(request.params, keys)
                                    : request.params
                        });

                        console.log('CloudinaryExperienceInterceptor - resolved request url:', newRequest.url);

                        return next.handle(newRequest);
                    }
                    return next.handle(request);
                })
            );
        } else {
            return next.handle(request);
        }
    }
}
