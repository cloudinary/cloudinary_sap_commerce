import {
  AfterViewInit,
  Component,
  ElementRef,
  Inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { CmsComponentData } from '@spartacus/storefront';
import { Observable, tap } from 'rxjs';
import cloudinary from 'cloudinary-video-player';
import 'cloudinary-video-player/cld-video-player.min.css';
import {
  CLOUDINARY_CONFIG_SERVICE,
  CloudinaryConfigService,
} from '../../product/cloudinary-config.token';
import { CloudinaryConfig } from '../../models/cloudinary-config.model';

@Component({
  selector: 'lib-cloudinary-video-player',
  templateUrl: './cloudinary-video-player.component.html',
  styleUrls: ['./cloudinary-video-player.component.scss'],
  standalone: false,
})
export class CloudinaryVideoPlayerComponent implements OnInit, AfterViewInit {
  @ViewChild('playerRef') playerRef!: ElementRef;

  private player: any;
  playerConfig: any;
  sourceConfig: any;

  videoUrl: string = '';
  data$: Observable<any>;
  publicId: string = '';
  cloudName: string = '';

  constructor(
    private cms: CmsComponentData<any>,
    @Inject(CLOUDINARY_CONFIG_SERVICE)
    private cloudinaryConfigService: CloudinaryConfigService,
  ) {
    this.data$ = this.cms.data$;
  }

  ngOnInit(): void {
    // watch for data coming from the CMS and extract the public ID
    this.data$.subscribe((data) => {
      if (data?.cloudinaryVideo?.cloudinaryPublicId) {
        this.publicId = data.cloudinaryVideo.cloudinaryPublicId;
      }
    });
  }

  ngAfterViewInit() {
    // retrieve configuration first, then create player
    this.cloudinaryConfigService
      .list()
      .pipe(
        tap((cfg: CloudinaryConfig) => {
          this.cloudName = cfg.cloudName || '';
          this.sourceConfig = JSON.parse(
            cfg.cloudinaryVideoSourceJsonString || '{}',
          );
          this.playerConfig = JSON.parse(
            cfg.cloudinaryVideoPlayerJsonString || '{}',
          );
        }),
      )
      .subscribe(() => {
        this.initializePlayer();
      });
  }

  private initializePlayer() {
    this.player = cloudinary.videoPlayer(this.playerRef.nativeElement, {
      cloud_name: this.cloudName,
      secure: true,
      controls: true,
      ...this.playerConfig,
    });
    this.player.source({ publicId: this.publicId, ...this.sourceConfig });
  }
}
