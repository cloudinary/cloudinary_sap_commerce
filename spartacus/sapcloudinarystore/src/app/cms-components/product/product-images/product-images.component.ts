import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Product } from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable, of } from 'rxjs';
import { distinctUntilChanged, filter, map, tap } from 'rxjs/operators';
import { CurrentProductService } from '../current-product.service';
import {CurrentCloudinaryConfigService} from '../current-cloudinaryconfig.service';
import {CloudinaryConfig} from '../../../model';
import {Occ} from '../../../occ/occ-models/occ.models';

@Component({
  selector: 'cx-product-images',
  templateUrl: './product-images.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProductImagesComponent {
  private mainMediaContainer = new BehaviorSubject(null);
  private prodCode;
  public isCloudinaryGalleryEnabled;
  public sapCCProductCode;
  public CName;
  public spinSetCode;
  private sapProdCode = new BehaviorSubject(null);
  

  private product$: Observable<
    Product
  > = this.currentProductService.getProduct().pipe(
    filter(Boolean),
    distinctUntilChanged(),
    tap((p: Product) => {
      this.mainMediaContainer.next(p.images?.PRIMARY ? p.images.PRIMARY : {});
      this.prodCode = p.code;
      this.sapCCProductCode = p.sapCCProductCode;
      this.sapProdCode.next(p.sapCCProductCode);
      this.spinSetCode = p.spinSetCode;
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
      
      //this.CName = cloudinaryConfig.CName;
      if(this.isCloudinaryGalleryEnabled){
        this.product$.subscribe(prod => {
      this.loadGallerySourceCode()
              .then(() => {
                  this.renderProductGalleryWidget(cloudinaryConfig,prod.sapCCProductCode,prod.spinSetCode);
          });
        });
        }
      //}); 
  
    });
  }

  getProductCode():any{
    return this.product$.subscribe(p => {this.sapCCProductCode = p.sapCCProductCode;return p.sapCCProductCode});
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

   getSpinsetJson(cloudinaryConfig,spinURL,spinsetcode,media_assets){
    return fetch(spinURL)
           	.then(function(response) {
               if(200 == response.status){
                 console.log("response.status"+response.status);
                  media_assets.push({
                    tag: spinsetcode,
                    mediaType: "spin"
                  });
               }
               return media_assets;
             }).then(media_assets => {this.appendScriptToDOM(cloudinaryConfig,media_assets);});
  }




  renderProductGalleryWidget(cloudinaryConfig,prodcode,spinsetcode) {
    var media_assets = [];
      media_assets.push({
            	tag: prodcode,
              mediaType: "image"
            });
      media_assets.push({
                  	tag: prodcode,
                    mediaType: "video"
                  });

      if(spinsetcode!=null)
      {
        
         var spinURL;
         if(cloudinaryConfig.hasOwnProperty("CName")){
          spinURL = "https://"+cloudinaryConfig.CName;
          console.log(cloudinaryConfig);
         }
         else{
          spinURL = "https://res.cloudinary.com/" + cloudinaryConfig.cloudName;
         }
         spinURL = spinURL + "/image/list/"+spinsetcode+".json";
         this.getSpinsetJson(cloudinaryConfig,spinURL,spinsetcode,media_assets);
         
      }else{
        this.appendScriptToDOM(cloudinaryConfig,media_assets);
      }
  }

  appendScriptToDOM(cloudinaryConfig,media_assets){
    const body_tag = document.body;
      const script_tag = document.createElement('script');

      var galleryJson2 = JSON.parse(cloudinaryConfig.cloudinaryGalleryConfigJsonString);
      console.log("galleryJson2"+JSON.stringify(galleryJson2));

      var galleryJson1 = {
        "container": "#product-gallery-widget-wrapper",
        "cloudName": cloudinaryConfig.cloudName,
        "mediaAssets": media_assets,
        ...galleryJson2
      };
      
      if(cloudinaryConfig.hasOwnProperty("CName")){
        galleryJson1.privateCdn = true;
        galleryJson1.secureDistribution = cloudinaryConfig.CName;
      }
      console.log("cloudinaryConfig-->"+JSON.stringify(cloudinaryConfig));

      script_tag.innerHTML = `
          const myWidget = cloudinary.galleryWidget(`+JSON.stringify(galleryJson1)+`);
          
          myWidget.render();

      `;
      console.log(script_tag);
      body_tag.appendChild(script_tag);
  }

   jsonConcat(o1, o2) {
    for (var key in o2) {
     o1[key] = o2[key];
    }
    return o1;
   }
   
   


}
