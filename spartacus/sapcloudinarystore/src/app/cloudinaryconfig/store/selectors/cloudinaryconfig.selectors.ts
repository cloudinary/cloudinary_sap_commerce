import { createSelector, MemoizedSelector } from '@ngrx/store';
import { CloudinaryConfig } from '../../../model/cloudinaryconfig.model';
import { StateUtils } from '@spartacus/core'
import { CloudinaryConfigsState, StateWithCloudinaryConfig } from '../cloudinaryconfig-state';
import { getCloudinaryConfigsState } from './feature.selector';

export const getCloudinaryConfigState: MemoizedSelector<
  StateWithCloudinaryConfig,
  StateUtils.EntityLoaderState<CloudinaryConfig>
> = createSelector(getCloudinaryConfigsState, (state: CloudinaryConfigsState) => state.details);

export const getSelectedCloudinaryConfigStateFactory = (
  scope = ''
): MemoizedSelector<StateWithCloudinaryConfig, StateUtils.LoaderState<CloudinaryConfig>> => {
  return createSelector(
    getCloudinaryConfigState,
    (details) =>
      StateUtils.entityLoaderStateSelector(details,'')[scope] ||
      StateUtils.initialLoaderState
  );
};

export const getSelectedCloudinaryConfigFactory = (
  scope = ''
): MemoizedSelector<StateWithCloudinaryConfig, CloudinaryConfig> => {
  return createSelector(
    getSelectedCloudinaryConfigStateFactory(scope),
    (cloudinaryConfigState) => StateUtils.loaderValueSelector(cloudinaryConfigState)
  );
};

export const getSelectedCloudinaryConfigLoadingFactory = (
  scope = ''
): MemoizedSelector<StateWithCloudinaryConfig, boolean> => {
  return createSelector(
    getSelectedCloudinaryConfigStateFactory(scope),
    (productState) => StateUtils.loaderLoadingSelector(productState)
  );
};

export const getSelectedCloudinaryConfigSuccessFactory = (
  scope = ''
): MemoizedSelector<StateWithCloudinaryConfig, boolean> => {
  return createSelector(
    getSelectedCloudinaryConfigStateFactory(scope),
    (productState) => StateUtils.loaderSuccessSelector(productState)
  );
};

export const getSelectedCloudinaryConfigErrorFactory = (
  scope = ''
): MemoizedSelector<StateWithCloudinaryConfig, boolean> => {
  return createSelector(
    getSelectedCloudinaryConfigStateFactory(scope),
    (cloudinaryConfigState) => StateUtils.loaderErrorSelector(cloudinaryConfigState)
  );
};

export const getAllCloudinaryConfigCodes: MemoizedSelector<
  StateWithCloudinaryConfig,
  string[]
> = createSelector(getCloudinaryConfigState, (details) => {
  return Object.keys(details.entities);
});
