import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Provider } from '@angular/core';
import { CloudinaryClientTokenInterceptor } from './CloudinaryClientTokenInterceptor';

export const interceptors: Provider[] = [
  {
    provide: HTTP_INTERCEPTORS,
    useExisting: CloudinaryClientTokenInterceptor,
    multi: true,
  },
];
