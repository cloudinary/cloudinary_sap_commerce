import { isPlatformBrowser } from '@angular/common';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { Actions, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import {
  combineLatest,
  defer,
  merge,
  Observable,
  of,
  SchedulerLike,
  using,
} from 'rxjs';
import {
  debounceTime,
  delay,
  distinctUntilChanged,
  filter,
  map,
  mapTo,
  shareReplay,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { deepMerge } from '@spartacus/core';
import { CloudinaryConfig } from '../../model/cloudinaryconfig.model';
import { LoadingScopesService } from '@spartacus/core';
import { withdrawOn } from '../../util/withdraw-on';
import { CloudinaryConfigActions } from '../store/actions/index';
import { StateWithCloudinaryConfig } from '../store/cloudinaryconfig-state';
import { CloudinaryConfigSelectors } from '../store/selectors/index';
//import { polish } from '../../util/polish';

@Injectable({
  providedIn: 'root',
})
export class CloudinaryConfigLoadingService {
  protected cloudinaryConfigs: {

  } = {};

  constructor(
    protected store: Store<StateWithCloudinaryConfig>,
    protected loadingScopes: LoadingScopesService,
    protected actions$: Actions,
    @Inject(PLATFORM_ID) protected platformId: any
  ) {}

  get(scopes: string[]): Observable<CloudinaryConfig> {
    scopes = this.loadingScopes.expand('cloudinaryConfig', scopes);

    this.initProductScopes(scopes);
    return this.cloudinaryConfigs[this.getScopesIndex(scopes)];
  }

  protected initProductScopes(scopes: string[]): void {
    if (!this.cloudinaryConfigs) {
      this.cloudinaryConfigs = {};
    }
console.log(scopes);
    for (const scope of scopes) {
      if (!this.cloudinaryConfigs[scope]) {
        this.cloudinaryConfigs[scope] = this.getProductForScope(
          scope
        );
        console.log(this.cloudinaryConfigs[scope]);
      }
    }

    // if (scopes.length > 1) {
    //   this.cloudinaryConfigs[this.getScopesIndex(scopes)] = combineLatest(
    //     scopes.map((scope) => this.cloudinaryConfigs[scope])
    //   ).pipe(
    //     polish(),
    //     map((productParts) =>
    //       productParts.every(Boolean)
    //         ? deepMerge({}, ...productParts)
    //         : undefined
    //     ),
    //     distinctUntilChanged()
    //   );
    // }
  }

  protected getScopesIndex(scopes: string[]): string {
    return scopes.join('ɵ');
  }

  /**
   * Creates observable for providing specified product data for the scope
   *
   * @param productCode
   * @param scope
   */
  protected getProductForScope(
    scope: string
  ): Observable<CloudinaryConfig> {
    const shouldLoad$ = this.store.pipe(
      select(
        CloudinaryConfigSelectors.getSelectedCloudinaryConfigStateFactory(scope)
      ),
      map(
        (productState) =>
          !productState?.loading &&
          !productState?.success &&
          !productState?.error
      ),
      distinctUntilChanged(),
      filter((x) => x)
    );

    const isLoading$ = this.store.pipe(
      select(
        CloudinaryConfigSelectors.getSelectedCloudinaryConfigLoadingFactory(scope)
      )
    );

    const productLoadLogic$ = merge(
      shouldLoad$,
      ...this.getProductReloadTriggers(scope)
    ).pipe(
      debounceTime(0),
      withLatestFrom(isLoading$),
      tap(([, isLoading]) => {
        if (!isLoading) {
          this.store.dispatch(
            new CloudinaryConfigActions.LoadCloudinaryConfig(scope)
          );
        }
      })
    );

    const productData$ = this.store.pipe(
      select(CloudinaryConfigSelectors.getSelectedCloudinaryConfigFactory(scope))
    );

    return using(
      () => productLoadLogic$.subscribe(),
      () => productData$
    ).pipe(shareReplay({ bufferSize: 1, refCount: true }));
  }

  /**
   * Returns reload triggers for product per scope
   *
   * @param productCode
   * @param scope
   */
  protected getProductReloadTriggers(
    scope: string
  ): Observable<boolean>[] {
    const triggers = [];

    // max age trigger add
    //const maxAge = this.loadingScopes.getMaxAge('cloudinaryconfig', scope);
    if (isPlatformBrowser(this.platformId)) {
      // we want to grab load product success and load product fail for this product and scope
      const loadFinish$ = this.actions$.pipe(
        filter(
          (
            action:
              | CloudinaryConfigActions.LoadCloudinaryConfigSuccess
              | CloudinaryConfigActions.LoadCloudinaryConfigFail
          ) =>
            (action.type === CloudinaryConfigActions.LOAD_CLOUDINARYCONFIG_SUCCESS ||
              action.type === CloudinaryConfigActions.LOAD_CLOUDINARYCONFIG_FAIL) &&
            action.meta.scope === scope
        )
      );

      const loadStart$ = this.actions$.pipe(
        ofType(CloudinaryConfigActions.LOAD_CLOUDINARYCONFIG),
        filter(
          (action: CloudinaryConfigActions.LoadCloudinaryConfig) =>
              action.meta.scope === scope
        )
      );

      triggers.push(this.getMaxAgeTrigger(loadStart$, loadFinish$, 25));
    }

    return triggers;
  }

  /**
   * Generic method that returns stream triggering reload by maxAge
   *
   * Could be refactored to separate service in future to use in other
   * max age reload implementations
   *
   * @param loadStart$ Stream that emits on load start
   * @param loadFinish$ Stream that emits on load finish
   * @param maxAge max age
   */
  private getMaxAgeTrigger(
    loadStart$: Observable<any>,
    loadFinish$: Observable<any>,
    maxAge: number,
    scheduler?: SchedulerLike
  ): Observable<boolean> {
    let timestamp = 0;

    const now = () => (scheduler ? scheduler.now() : Date.now());

    const timestamp$ = loadFinish$.pipe(tap(() => (timestamp = now())));

    const shouldReload$: Observable<boolean> = defer(() => {
      const age = now() - timestamp;

      const timestampRefresh$ = timestamp$.pipe(
        delay(maxAge, scheduler),
        mapTo(true),
        withdrawOn(loadStart$)
      );

      if (age > maxAge) {
        // we should emit first value immediately
        return merge(of(true), timestampRefresh$);
      } else if (age === 0) {
        // edge case, we should emit max age timeout after next load success
        // could happen with artificial schedulers
        return timestampRefresh$;
      } else {
        // we should emit first value when age will expire
        return merge(
          of(true).pipe(delay(maxAge - age, scheduler)),
          timestampRefresh$
        );
      }
    });

    return shouldReload$;
  }
}
