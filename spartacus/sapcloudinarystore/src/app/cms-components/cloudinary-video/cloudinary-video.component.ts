import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
//import { CloudinaryVideoComponentModel } from '@spartacus/storefront';
import { CmsComponentData } from '@spartacus/storefront';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-cloudinary-video-component',
  templateUrl: 'cloudinary-video.component.html',
  styleUrls: ['./cloudinary-video.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CloudinaryVideoComponent implements OnInit {
  //componentData$: Observable<CloudinaryVideoComponentModel> = this.componentData.data$;
  // constructor(private componentData: CmsComponentData<CloudinaryVideoComponentModel>) { }

  ngOnInit(): void {
  }

}
