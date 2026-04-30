import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  tick,
} from '@angular/core/testing';
import { of } from 'rxjs';

import { CloudinaryVideoPlayerComponent } from './cloudinary-video-player.component';
import {
  CLOUDINARY_CONFIG_SERVICE,
  CloudinaryConfigService,
} from '../../product/cloudinary-config.token';
import { CloudinaryConfig } from '../../models/cloudinary-config.model';

class MockConfigService implements CloudinaryConfigService {
  list() {
    return of({
      isCloudinaryGalleryEnabled: true,
      cloudName: 'test-cloud',
      cloudinaryGalleryConfigJsonString: '{}',
    } as CloudinaryConfig);
  }
}

describe('CloudinaryVideoPlayerComponent', () => {
  let component: CloudinaryVideoPlayerComponent;
  let fixture: ComponentFixture<CloudinaryVideoPlayerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CloudinaryVideoPlayerComponent],
      providers: [
        { provide: CLOUDINARY_CONFIG_SERVICE, useClass: MockConfigService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CloudinaryVideoPlayerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should extract publicId from CMS data on initialization', fakeAsync(() => {
    component.ngOnInit();
    tick();

    // Simulate CMS data emission
    const testData = {
      cloudinaryVideo: {
        cloudinaryPublicId: 'samples/my-test-video',
      },
    };

    // We need to trigger the data$ subscription manually in tests
    // by creating a new observable for testing
    const testDataSubject$ = of(testData);
    (component as any).data$ = testDataSubject$;

    component.ngOnInit();
    tick();

    expect(component.publicId).toEqual('samples/my-test-video');
  }));

  it('should read cloud name from config and initialize player', fakeAsync(() => {
    // ngAfterViewInit triggers subscription
    component.ngAfterViewInit();
    tick();

    expect(component.cloudName).toEqual('test-cloud');
    // we don't actually create a real video player in tests, but the
    // field should have been set and initializePlayer executed without error
    expect(component['player']).toBeDefined();
  }));
});
