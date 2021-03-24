import { Injectable } from '@angular/core';
import { select, Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { CloudinaryConfig } from '../../model/cloudinaryconfig.model';
import { CloudinaryConfigActions } from '../store/actions/index';
import { StateWithCloudinaryConfig } from '../store/cloudinaryconfig-state';
import { CloudinaryConfigSelectors } from '../store/selectors/index';
import { CloudinaryConfigLoadingService } from '../services/cloudinaryconfig-loading.service';
//import { CloudinaryConfigScope } from '../model/cloudinaryconfig-scope';
//import { DEFAULT_SCOPE } from '../../occ/occ-models/occ-endpoints.model';
import { DEFAULT_SCOPE } from '@spartacus/core';

@Injectable({
  providedIn: 'root',
})
export class CloudinaryConfigService {
  constructor(
    protected store: Store<StateWithCloudinaryConfig>,
    protected cloudinaryConfigLoading: CloudinaryConfigLoadingService
  ) {}

  /**
   * Returns the product observable. The product will be loaded
   * whenever there's no value observed.
   *
   * The underlying product loader ensures that the product is
   * only loaded once, even in case of parallel observers.
   *
   * You should provide product data scope you are interested in to not load all
   * the data if not needed. You can provide more than one scope.
   *
   * @param productCode Product code to load
   * @param scopes Scope or scopes of the product data
   */
  get(
    scopes: (string)[] | string = DEFAULT_SCOPE
  ): Observable<CloudinaryConfig> {
    console.log("in get() service");
    return this.cloudinaryConfigLoading.get([].concat(scopes))
      
  }

  /**
   * Returns boolean observable for product's loading state
   */
  isLoading(
    scope: string = ''
  ): Observable<boolean> {
    return this.store.pipe(
      select(
        CloudinaryConfigSelectors.getSelectedCloudinaryConfigLoadingFactory(scope)
      )
    );
  }

  /**
   * Returns boolean observable for product's load success state
   */
  isSuccess(
    scope: string = ''
  ): Observable<boolean> {
    return this.store.pipe(
      select(
        CloudinaryConfigSelectors.getSelectedCloudinaryConfigSuccessFactory(scope)
      )
    );
  }

  /**
   * Returns boolean observable for product's load error state
   */
  hasError(
    scope: string = ''
  ): Observable<boolean> {
    return this.store.pipe(
      select(
        CloudinaryConfigSelectors.getSelectedCloudinaryConfigErrorFactory(scope)
      )
    );
  }

  /**
   * Reloads the product. The product is loaded implicetly
   * whenever selected by the `get`, but in some cases an
   * explicit reload might be needed.
   */
  reload(scope: string = ''): void {
    this.store.dispatch(new CloudinaryConfigActions.LoadCloudinaryConfig(scope));
  }
}
