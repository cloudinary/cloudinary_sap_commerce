//import { SiteContextConfig } from '../../site-context/config/site-context-config';
import { SiteContextConfig } from '@spartacus/core'
import { OccEndpoints } from '../occ-models/occ-endpoints.model';
import { LoadingScopes } from './loading-scopes-config';
import { Injectable } from '@angular/core';
//import { Config } from '../../config/config.module';
import { Config } from '@spartacus/core';

@Injectable({
  providedIn: 'root',
  useExisting: Config,
})
export abstract class OccConfig extends SiteContextConfig {
  backend?: {
    occ?: {
      baseUrl?: string;
      prefix?: string;
      /**
       * Indicates whether or not cross-site Access-Control requests should be made
       * using credentials such as cookies, authorization headers or TLS client certificates
       */
      useWithCredentials?: boolean;

      endpoints?: OccEndpoints;
      legacy?: boolean;
    };
    media?: {
      /**
       * Media URLs are typically relative, so that the host can be configured.
       * Configurable media baseURLs are useful for SEO, multi-site,
       * switching environments, etc.
       */
      baseUrl?: string;
    };
    loadingScopes?: LoadingScopes;
  };
}
