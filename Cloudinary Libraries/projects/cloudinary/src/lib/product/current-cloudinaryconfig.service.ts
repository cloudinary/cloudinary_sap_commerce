import { Injectable, Inject } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { CloudinaryConfig } from '../models/cloudinary-config.model';
import {
  CloudinaryConfigService,
  CLOUDINARY_CONFIG_SERVICE,
} from './cloudinary-config.token';
import {
  CloudinaryConfigConnector,
  CLOUDINARY_CONFIG_CONNECTOR,
} from '../connectors/cloudinary-config.connector';

@Injectable({
  providedIn: 'root',
})
export class CurrentCloudinaryConfigService implements CloudinaryConfigService {
  private config$?: Observable<CloudinaryConfig>;

  constructor(
    @Inject(CLOUDINARY_CONFIG_CONNECTOR)
    private connector: CloudinaryConfigConnector,
  ) {}

  /**
   * Retrieve the Cloudinary configuration from the connector.
   *
   * Results are memoised so that subsequent calls reuse the original
   * observable and thus perform only a single backend request per
   * application lifetime.  Consumers can still subscribe multiple times.
   */
  list(): Observable<CloudinaryConfig> {
    if (!this.config$) {
      this.config$ = this.connector.getConfig().pipe(shareReplay(1));
    }
    return this.config$;
  }
}
