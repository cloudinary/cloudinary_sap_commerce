import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
//import { CloudinaryVideoComponentModel } from '@spartacus/storefront';
import { CmsComponentData } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import {CurrentCloudinaryConfigService} from '../../cms-components/product/current-cloudinaryconfig.service';

@Component({
  selector: 'app-cloudinary-video-component',
  templateUrl: 'cloudinary-video.component.html',
  styleUrls: ['./cloudinary-video.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CloudinaryVideoComponent implements OnInit {

  public videourl;

  componentData$: Observable<any> = this.componentData.data$;
  constructor(private componentData: CmsComponentData<any>,private currentCloudinaryConfigService: CurrentCloudinaryConfigService) { 
    this.currentCloudinaryConfigService.list().subscribe(cloudinaryConfig => {
      this.componentData$.subscribe(
        component =>{
          this.videourl = component.cloudinaryVideo.url;
      });
      this.renderVideoWidget(cloudinaryConfig);
  
            });
  }

  
  renderVideoWidget(cloudinaryConfig){
    const body_tag = document.body;
      const script_tag = document.createElement('script');
      script_tag.innerHTML = `
      var cld = cloudinary.Cloudinary.new({ cloud_name: "`+cloudinaryConfig.cloudName+`"});
      var player = cld.videoPlayer('cloudinaryVideoPlayer');
      player.source("`+this.videourl+`");
      `;
      body_tag.appendChild(script_tag);
  }

  ngOnInit(): void {
  }

}
