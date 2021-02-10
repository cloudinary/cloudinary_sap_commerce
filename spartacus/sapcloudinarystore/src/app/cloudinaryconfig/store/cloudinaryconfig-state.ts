
import { CloudinaryConfig } from '../../model/cloudinaryconfig.model';
import { EntityScopedLoaderState } from '../../state/utils/scoped-loader/scoped-loader.state';

export const CLOUDINARYCONFIG_FEATURE = 'cloudinaryConfig';
export const CLOUDINARYCONFIG_DETAIL_ENTITY = '[CloudinaryConfig] Detail Entity';

export interface StateWithCloudinaryConfig {
  [CLOUDINARYCONFIG_FEATURE]: CloudinaryConfigsState;
}

export interface CloudinaryConfigsState {
  details: EntityScopedLoaderState<CloudinaryConfig>;

}
