import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Product } from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable, of } from 'rxjs';
import { distinctUntilChanged, filter, map, tap } from 'rxjs/operators';
import { CurrentProductService } from '../current-product.service';
import {CurrentCloudinaryConfigService} from '../current-cloudinaryconfig.service';
import {CloudinaryConfig} from '../../../model';

@Component({
  selector: 'cx-product-images',
  templateUrl: './product-images.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductImagesComponent {
  private mainMediaContainer = new BehaviorSubject(null);
  private prodCode;
  public isCloudinaryGalleryEnabled;

  private product$: Observable<
    Product
  > = this.currentProductService.getProduct().pipe(
    filter(Boolean),
    distinctUntilChanged(),
    tap((p: Product) => {
      this.mainMediaContainer.next(p.images?.PRIMARY ? p.images.PRIMARY : {});
      this.prodCode = p.code;
    })
  );

  thumbs$: Observable<any[]> = this.product$.pipe(
    map((p: Product) => this.createThumbs(p))
  );

  mainImage$ = combineLatest([this.product$, this.mainMediaContainer]).pipe(
    map(([, container]) => container)
  );

  constructor(private currentProductService: CurrentProductService,private currentCloudinaryConfigService: CurrentCloudinaryConfigService) {
    

    this.currentCloudinaryConfigService.list().subscribe(cloudinaryConfig => {
      
      this.isCloudinaryGalleryEnabled = cloudinaryConfig.isCloudinaryGalleryEnabled;
      
      this.loadGallerySourceCode()
              .then(() => {
                  this.renderProductGalleryWidget(cloudinaryConfig,this.prodCode);
          });
        
  
    });
  }

  openImage(item: any): void {
    this.mainMediaContainer.next(item);
  }

  isActive(thumbnail): Observable<boolean> {
    return this.mainMediaContainer.pipe(
      filter(Boolean),
      map((container: any) => {
        return (
          container.zoom &&
          container.zoom.url &&
          thumbnail.zoom &&
          thumbnail.zoom.url &&
          container.zoom.url === thumbnail.zoom.url
        );
      })
    );
  }

  /** find the index of the main media in the list of media */
  getActive(thumbs: any[]): Observable<number> {
    return this.mainMediaContainer.pipe(
      filter(Boolean),
      map((container: any) => {
        const current = thumbs.find(
          (t) =>
            t.media &&
            container.zoom &&
            t.media.container &&
            t.media.container.zoom &&
            t.media.container.zoom.url === container.zoom.url
        );
        return thumbs.indexOf(current);
      })
    );
  }

  /**
   * Return an array of CarouselItems for the product thumbnails.
   * In case there are less then 2 thumbs, we return null.
   */
  private createThumbs(product: Product): Observable<any>[] {
    if (
      !product.images ||
      !product.images.GALLERY ||
      product.images.GALLERY.length < 2
    ) {
      return [];
    }

    return (<any[]>product.images.GALLERY).map((c) => of({ container: c }));
  }

  //Cloudinary Product gallery component
  loadGallerySourceCode() {
    var url = "https://product-gallery.cloudinary.com/all.js"
    var pgw_script_tag = document.createElement('script');
      pgw_script_tag.src = url;
      pgw_script_tag.type = 'text/javascript';
      pgw_script_tag.charset = 'utf-8';
      document.getElementsByTagName('head')[0].appendChild(pgw_script_tag);

      return new Promise((resolve, reject) => {
          pgw_script_tag.onload = resolve;
      });
  }

  renderProductGalleryWidget(cloudinaryConfig,code) {
    console.log("product-->"+code);
      const body_tag = document.body;
      const script_tag = document.createElement('script');
      script_tag.innerHTML = `
          const myWidget = cloudinary.galleryWidget({
              "container": "#product-gallery-widget-wrapper",
              "cloudName": "`+cloudinaryConfig.cloudName+`",
              "mediaAssets": [{
                  "tag": "sap_sku_`+code+`",
                  "mediaType": "image"
              }, {
                  "tag": "sap_sku_`+code+`",
                  "mediaType": "video"
              }, {
                  "tag": "sap_sku_`+code+`",
                  "mediaType": "spin"
              }],`+cloudinaryConfig.cloudinaryGalleryConfigJsonString+`
              
          });
          myWidget.render();

      `;
      body_tag.appendChild(script_tag);
  }
}
