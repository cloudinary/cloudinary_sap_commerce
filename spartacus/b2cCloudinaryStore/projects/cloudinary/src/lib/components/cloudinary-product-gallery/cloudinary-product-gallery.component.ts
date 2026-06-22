import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ComponentFactoryResolver,
  ElementRef,
  Inject,
  OnDestroy,
  OnInit,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';
import { Product } from '@spartacus/core';
import { combineLatest, from, Observable, of, Subscription } from 'rxjs';
import {
  catchError,
  distinctUntilChanged,
  filter,
  map,
  switchMap,
  tap,
} from 'rxjs/operators';
import { CurrentProductService } from '@spartacus/storefront';
import { ProductImagesComponent } from '@spartacus/storefront';
import {
  CLOUDINARY_CONFIG_SERVICE,
  CloudinaryConfigService,
} from '../../product/cloudinary-config.token';
import { CloudinaryConfig } from '../../models/cloudinary-config.model';

type ProductWithGalleryMeta = Product & {
  sapCCProductCode?: string;
  spinSetCode?: string;
  images?: any;
};

@Component({
  selector: 'lib-cloudinary-product-gallery',
  templateUrl: './cloudinary-product-gallery.component.html',
  styleUrls: ['./cloudinary-product-gallery.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class CloudinaryProductGalleryComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  @ViewChild('galleryContainer') galleryContainer?: ElementRef<HTMLDivElement>;
  @ViewChild('defaultContainer', { read: ViewContainerRef })
  defaultContainer!: ViewContainerRef;

  public isCloudinaryGalleryEnabled = false;
  private subscriptions = new Subscription();
  private widgetRendered = false;

  private product$!: Observable<ProductWithGalleryMeta>;

  constructor(
    private currentProductService: CurrentProductService,
    private cd: ChangeDetectorRef,
    private componentFactoryResolver: ComponentFactoryResolver,
    @Inject(CLOUDINARY_CONFIG_SERVICE)
    private currentCloudinaryConfigService: CloudinaryConfigService,
  ) {
    this.product$ = this.currentProductService
      .getProduct()
      .pipe(filter(Boolean), distinctUntilChanged());
  }

  ngOnInit(): void {
    const configAndProduct$ = combineLatest([
      this.currentCloudinaryConfigService.list(),
      this.product$,
    ]) as Observable<[CloudinaryConfig, ProductWithGalleryMeta]>;

    this.subscriptions.add(
      configAndProduct$
        .pipe(
          filter(([config, product]) => Boolean(config && product)),
          tap(([config]) => {
            const wasEnabled = this.isCloudinaryGalleryEnabled;
            this.isCloudinaryGalleryEnabled = config.isCloudinaryGalleryEnabled;
            this.cd.markForCheck();

            // Handle state changes
            if (!this.isCloudinaryGalleryEnabled && wasEnabled !== false) {
              // PGW was enabled and now disabled, render default component
              this.renderDefaultComponent();
            } else if (this.isCloudinaryGalleryEnabled && wasEnabled !== true) {
              // PGW was disabled and now enabled, clear default component
              this.clearDefaultComponent();
            }
          }),
          filter(([config]) => config.isCloudinaryGalleryEnabled),
          switchMap(([config, product]) =>
            from(this.loadGalleryScript()).pipe(
              tap(() => {
                this.renderGallery(
                  config,
                  product.sapCCProductCode,
                  product.spinSetCode,
                );
              }),
              catchError(() => of(void 0)),
            ),
          ),
        )
        .subscribe(),
    );
  }

  ngAfterViewInit(): void {
    // Render default component if PGW is initially disabled
    if (!this.isCloudinaryGalleryEnabled) {
      this.renderDefaultComponent();
    }
    this.cd.markForCheck();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  private async loadGalleryScript(): Promise<void> {
    if ((window as any).cloudinary?.galleryWidget) {
      return;
    }

    return new Promise<void>((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://product-gallery.cloudinary.com/all.js';
      script.onload = () => resolve();
      script.onerror = () =>
        reject(new Error('Failed to load Cloudinary Product Gallery script'));
      document.head.appendChild(script);
    });
  }

  private renderGallery(
    cloudinaryConfig: CloudinaryConfig,
    prodCode?: string,
    spinsetCode?: string,
  ): void {
    if (!this.galleryContainer?.nativeElement || !prodCode) {
      return;
    }

    if (this.widgetRendered) {
      this.galleryContainer.nativeElement.innerHTML = '';
    }

    const mediaAssets: any[] = [
      { tag: prodCode, mediaType: 'image' },
      { tag: prodCode, mediaType: 'video' },
    ];

    if (spinsetCode) {
      const spinBaseUrl = cloudinaryConfig.CName
        ? `https://${cloudinaryConfig.CName}`
        : `https://res.cloudinary.com/${cloudinaryConfig.cloudName}`;
      const spinUrl = `${spinBaseUrl}/image/list/${spinsetCode}.json`;
      fetch(spinUrl)
        .then((response) => {
          if (response.ok) {
            mediaAssets.push({ publicId: spinsetCode, mediaType: 'spin' });
          }
        })
        .finally(() => {
          this.createWidget(cloudinaryConfig, mediaAssets);
        });
    } else {
      this.createWidget(cloudinaryConfig, mediaAssets);
    }
  }

  private createWidget(
    cloudinaryConfig: CloudinaryConfig,
    mediaAssets: any[],
  ): void {
    if (
      typeof window === 'undefined' ||
      !this.galleryContainer?.nativeElement
    ) {
      return;
    }

    const cloudinaryGlobal = (window as any).cloudinary;
    if (!cloudinaryGlobal?.galleryWidget) {
      return;
    }

    const galleryConfig: any = {
      container: this.galleryContainer.nativeElement,
      cloudName: cloudinaryConfig.cloudName,
      mediaAssets: mediaAssets,
      ...this.parseGalleryConfig(cloudinaryConfig),
    };

    if (cloudinaryConfig.CName) {
      galleryConfig.privateCdn = true;
      galleryConfig.secureDistribution = cloudinaryConfig.CName;
    }

    const widget = cloudinaryGlobal.galleryWidget(galleryConfig);
    widget.render();
    this.widgetRendered = true;
  }

  private parseGalleryConfig(
    cloudinaryConfig: CloudinaryConfig,
  ): Record<string, unknown> {
    try {
      return JSON.parse(
        cloudinaryConfig.cloudinaryGalleryConfigJsonString || '{}',
      );
    } catch {
      return {};
    }
  }

  private renderDefaultComponent(): void {
    if (!this.defaultContainer) {
      return;
    }

    // Clear any existing content
    this.defaultContainer.clear();

    // Create the original Spartacus ProductImagesComponent
    const componentFactory =
      this.componentFactoryResolver.resolveComponentFactory(
        ProductImagesComponent,
      );
    const componentRef =
      this.defaultContainer.createComponent(componentFactory);

    // The component will automatically get the product context from the CurrentProductService
    // which is already available in the injector hierarchy
  }

  private clearDefaultComponent(): void {
    if (this.defaultContainer) {
      this.defaultContainer.clear();
    }
  }
}
