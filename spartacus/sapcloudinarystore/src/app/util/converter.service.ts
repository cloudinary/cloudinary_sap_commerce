import { Injectable, InjectionToken, Injector } from '@angular/core';
import { Observable, OperatorFunction } from 'rxjs';
import { map } from 'rxjs/operators';

/**
 * Converter is used to convert source data model to target data model.
 * By convention, we distinguish two flows:
 *   - *Normalize* is the conversion from backend models to UI models
 *   - *Serialize* is the conversion of UI models to backend models (in case of submitting data to the backend).
 *
 * Converters can be stacked together to to apply decoupled customizations
 */
export interface Converter<S, T> {
  /**
   * Convert converts source model to target model. Can use optional target parameter,
   * used in case of stacking multiple converters (for example, to implement populator pattern).
   *
   * @param source Source data model
   * @param target Optional, partially converted target model
   */
  convert(source: S, target?: T): T;
}

@Injectable({
  providedIn: 'root',
})
export class ConverterService {
  constructor(protected injector: Injector) {}

  private converters: Map<
    InjectionToken<Converter<any, any>>,
    Converter<any, any>[]
  > = new Map();

  private getConverters<S, T>(
    injectionToken: InjectionToken<Converter<S, T>>
  ): Converter<S, T>[] {
    if (!this.converters.has(injectionToken)) {
      const converters = this.injector.get<Converter<S, T>[]>(
        injectionToken,
        []
      );
      if (!Array.isArray(converters)) {
        console.warn(
          'Converter must be multi-provided, please use "multi: true" for',
          injectionToken.toString()
        );
      }
      this.converters.set(injectionToken, converters);
    }

    return this.converters.get(injectionToken);
  }

  /**
   * Will return true if converters for specified token were provided
   */
  hasConverters<S, T>(
    injectionToken: InjectionToken<Converter<S, T>>
  ): boolean {
    const converters = this.getConverters(injectionToken);
    return Array.isArray(converters) && converters.length > 0;
  }

  /**
   * Pipeable operator to apply converter logic in a observable stream
   */
  pipeable<S, T>(
    injectionToken: InjectionToken<Converter<S, T>>
  ): OperatorFunction<S, T> {
    if (this.hasConverters(injectionToken)) {
      return map((model: S) => this.convertSource(model, injectionToken));
    } else {
      return (observable: Observable<any>) => observable as Observable<T>;
    }
  }

  /**
   * Pipeable operator to apply converter logic in a observable stream to collection of items
   */
  pipeableMany<S, T>(
    injectionToken: InjectionToken<Converter<S, T>>
  ): OperatorFunction<S[], T[]> {
    if (this.hasConverters(injectionToken)) {
      return map((model: S[]) => this.convertMany(model, injectionToken));
    } else {
      return (observable: Observable<any[]>) => observable as Observable<T[]>;
    }
  }

  /**
   * Apply converter logic specified by injection token to source data
   */
  convert<S, T>(source: S, injectionToken: InjectionToken<Converter<S, T>>): T {
    if (this.hasConverters(injectionToken)) {
      return this.convertSource(source, injectionToken);
    } else {
      return source as any;
    }
  }

  /**
   * Apply converter logic specified by injection token to a collection
   */
  convertMany<S, T>(
    sources: S[],
    injectionToken: InjectionToken<Converter<S, T>>
  ): T[] {
    if (this.hasConverters(injectionToken) && Array.isArray(sources)) {
      return sources.map((source) =>
        this.convertSource(source, injectionToken)
      );
    } else {
      return sources as any[];
    }
  }

  private convertSource<S, T>(
    source: S,
    injectionToken: InjectionToken<Converter<S, T>>
  ): T {
    return this.getConverters(injectionToken).reduce((target, converter) => {
      return converter.convert(source, target);
    }, undefined as T);
  }
}
