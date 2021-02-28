import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
} from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';

import { AuthService } from '@spartacus/core';
import {
  USE_CLIENT_TOKEN,
  InterceptorUtil,
} from '@spartacus/core';
import { ClientToken } from '@spartacus/core';
import { OccEndpointsService } from '@spartacus/core';

@Injectable({ providedIn: 'root' })
export class CloudinaryClientTokenInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private occEndpoints: OccEndpointsService
  ) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    
    return this.getClientToken(request).pipe(
      take(1),
      switchMap((token: ClientToken) => {
        if (
          token && (request.url.includes('cloudinary') || request.url.includes(this.occEndpoints.getBaseEndpoint()))
        ) {
          request = request.clone({
            setHeaders: {
              Authorization: `${token.token_type} ${token.access_token}`,
            },
          });
          
        }
        
        return next.handle(request);
      })
    );
  }

  private getClientToken(request: HttpRequest<any>): Observable<ClientToken> {
    if (
      InterceptorUtil.getInterceptorParam(USE_CLIENT_TOKEN, request.headers)
    ) {
      return this.authService.refreshClientToken();
    }
    return of(null);
  }
}
