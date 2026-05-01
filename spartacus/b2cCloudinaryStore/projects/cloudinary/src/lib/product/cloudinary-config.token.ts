import { InjectionToken } from '@angular/core';
import { CloudinaryConfig } from '../models/cloudinary-config.model';
import { Observable } from 'rxjs';

export interface CloudinaryConfigService {
  list(): Observable<CloudinaryConfig>;
}

export const CLOUDINARY_CONFIG_SERVICE =
  new InjectionToken<CloudinaryConfigService>('CLOUDINARY_CONFIG_SERVICE');
