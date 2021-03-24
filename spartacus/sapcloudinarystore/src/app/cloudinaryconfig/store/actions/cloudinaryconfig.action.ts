import { CloudinaryConfig } from '../../../model/cloudinaryconfig.model';
import { CLOUDINARYCONFIG_DETAIL_ENTITY } from '../cloudinaryconfig-state';
import { EntityLoaderMeta } from '../../../state/utils/entity-loader/entity-loader.action';
import { Action } from '@ngrx/store';
import { EntityScopedLoaderActions } from '../../../state/utils/scoped-loader/entity-scoped-loader.actions';

export const LOAD_CLOUDINARYCONFIG = '[CloudinaryConfig] Load Product Data';
export const LOAD_CLOUDINARYCONFIG_FAIL = '[CloudinaryConfig] Load Product Data Fail';
export const LOAD_CLOUDINARYCONFIG_SUCCESS = '[CloudinaryConfig] Load Product Data Success';

export interface CloudinaryConfigMeta extends EntityLoaderMeta {
  scope?: string;
}

export interface EntityScopedLoaderAction extends Action {
  readonly payload?: any;
  readonly meta?: CloudinaryConfigMeta;
}

export class LoadCloudinaryConfig extends EntityScopedLoaderActions.EntityScopedLoadAction {
  readonly type = LOAD_CLOUDINARYCONFIG;
  constructor(public payload: string, scope = '') {
    super(CLOUDINARYCONFIG_DETAIL_ENTITY, payload, scope);
  }
}

export class LoadCloudinaryConfigFail extends EntityScopedLoaderActions.EntityScopedFailAction {
  readonly type = LOAD_CLOUDINARYCONFIG_FAIL;
  constructor(public payload: any, scope = '') {
    super(CLOUDINARYCONFIG_DETAIL_ENTITY, scope, payload);
  }
}

export class LoadCloudinaryConfigSuccess extends EntityScopedLoaderActions.EntityScopedSuccessAction {
  readonly type = LOAD_CLOUDINARYCONFIG_SUCCESS;
  constructor(public payload: CloudinaryConfig, scope = '') {
    super(CLOUDINARYCONFIG_DETAIL_ENTITY, scope);
  }
}

// action types
export type CloudinaryConfigAction = LoadCloudinaryConfig | LoadCloudinaryConfigFail | LoadCloudinaryConfigSuccess;
