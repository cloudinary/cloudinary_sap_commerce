import { createFeatureSelector, MemoizedSelector } from '@ngrx/store';
import {
  CloudinaryConfigsState,
  CLOUDINARYCONFIG_FEATURE,
  StateWithCloudinaryConfig,
} from '../cloudinaryconfig-state';

export const getCloudinaryConfigsState: MemoizedSelector<
StateWithCloudinaryConfig,
CloudinaryConfigsState
> = createFeatureSelector<CloudinaryConfigsState>(CLOUDINARYCONFIG_FEATURE);
