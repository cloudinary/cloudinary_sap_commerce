import { HttpClient } from '@angular/common/http';
import { Injectable, InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { CloudinaryConfig } from '../models/cloudinary-config.model';
import { OccEndpointsService } from '@spartacus/core';

/**
 * Adapter interface used by the connector.  A consumer can replace
 * the default implementation (which simply hits `/cloudinary/configuration`)
 * by binding to the `CLOUDINARY_CONFIG_ADAPTER` token.
 */
export abstract class CloudinaryConfigAdapter {
  /**
   * Load the configuration from the back end.
   */
  abstract loadConfig(): Observable<CloudinaryConfig>;
}

/**
 * Injection token that can be used to override the adapter implementation.
 */
export const CLOUDINARY_CONFIG_ADAPTER =
  new InjectionToken<CloudinaryConfigAdapter>('CloudinaryConfigAdapter');

/**
 * Default adapter.  Routes through Spartacus' OCC endpoints service so
 * that consumers can customise the URL via the SPA's `OccConfig`.
 *
 * To use this in your application, add the following to your config:
 *
 * ```ts
 * provideConfig({
 *   backend: {
 *     occ: {
 *       endpoints: {
 *         cloudinaryConfiguration: 'cloudinary/configuration',
 *       },
 *     },
 *   },
 * });
 * ```
 */
@Injectable()
export class HttpCloudinaryConfigAdapter implements CloudinaryConfigAdapter {
  constructor(
    protected http: HttpClient,
    protected occEndpoints: OccEndpointsService,
  ) {}

  loadConfig(): Observable<CloudinaryConfig> {
    const url = this.occEndpoints.buildUrl('cloudinaryConfiguration');
    return this.http.get<CloudinaryConfig>(url);
  }
}
