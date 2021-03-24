import { InjectionToken, Provider } from '@angular/core';
import { ActionReducer, ActionReducerMap, MetaReducer } from '@ngrx/store';
import { CloudinaryConfig } from '../../../model/cloudinaryconfig.model';
//import { SiteContextActions } from '../../../site-context/store/actions/index';
import { SiteContextActions } from '@spartacus/core';
import { CLOUDINARYCONFIG_DETAIL_ENTITY, CloudinaryConfigsState } from '../cloudinaryconfig-state';
import { entityScopedLoaderReducer } from '../../../state/utils/scoped-loader/entity-scoped-loader.reducer';


export function getReducers(): ActionReducerMap<CloudinaryConfigsState> {
  return {
    details: entityScopedLoaderReducer<CloudinaryConfig>(CLOUDINARYCONFIG_DETAIL_ENTITY),
  };
}

export const reducerToken: InjectionToken<ActionReducerMap<
CloudinaryConfigsState
>> = new InjectionToken<ActionReducerMap<CloudinaryConfigsState>>('CloudinaryConfigReducers');

export const reducerProvider: Provider = {
  provide: reducerToken,
  useFactory: getReducers,
};

export function clearCloudinaryConfigsState(
  reducer: ActionReducer<any>
): ActionReducer<any> {
  return function (state, action) {
    if (
      action.type === SiteContextActions.CURRENCY_CHANGE ||
      action.type === SiteContextActions.LANGUAGE_CHANGE
    ) {
      state = undefined;
    }
    return reducer(state, action);
  };
}

export const metaReducers: MetaReducer<any>[] = [clearCloudinaryConfigsState];
