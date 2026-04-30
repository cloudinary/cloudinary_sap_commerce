import { Inject, Injectable, InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { CloudinaryConfig } from '../models/cloudinary-config.model';
import {
  CloudinaryConfigAdapter,
  CLOUDINARY_CONFIG_ADAPTER,
} from './cloudinary-config.adapter';

/**
 * Connector interface.  Higher‑level services depend on the connector
 * rather than directly on an adapter.  This allows additional logic
 * (caching, transformation, error handling) to be inserted without
 * touching the service.
 */
export abstract class CloudinaryConfigConnector {
  /**
   * Fetch the configuration via whatever underlying mechanism is available.
   */
  abstract getConfig(): Observable<CloudinaryConfig>;
}

/**
 * Token to allow overriding the default connector implementation.
 */
export const CLOUDINARY_CONFIG_CONNECTOR =
  new InjectionToken<CloudinaryConfigConnector>('CloudinaryConfigConnector');

/**
 * Default connector which simply delegates to the adapter.
 */
@Injectable()
export class DefaultCloudinaryConfigConnector implements CloudinaryConfigConnector {
  constructor(
    @Inject(CLOUDINARY_CONFIG_ADAPTER)
    protected adapter: CloudinaryConfigAdapter,
  ) {}

  getConfig(): Observable<CloudinaryConfig> {
    return this.adapter.loadConfig();
  }
}
