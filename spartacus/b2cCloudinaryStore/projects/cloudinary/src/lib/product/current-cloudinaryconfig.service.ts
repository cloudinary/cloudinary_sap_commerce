import { Injectable, Inject } from '@angular/core';
import { Observable } from 'rxjs';
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
  constructor(
    @Inject(CLOUDINARY_CONFIG_CONNECTOR)
    private connector: CloudinaryConfigConnector,
  ) {}

  /**
   * Retrieve the Cloudinary configuration from the connector.  By
   * default the connector will use the HTTP adapter but it can be
   * replaced for testing or advanced scenarios.
   */
  list(): Observable<CloudinaryConfig> {
    return this.connector.getConfig();
  }
}
