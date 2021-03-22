import { InjectionToken } from '@angular/core';
import { Converter } from '../../../util/converter.service';
import { CloudinaryConfig } from '../../../model/cloudinaryconfig.model'

export const CLOUDINARYCONFIG_NORMALIZER = new InjectionToken<Converter<any, CloudinaryConfig>>(
  'CloudinaryConfigNormalizer'
);
