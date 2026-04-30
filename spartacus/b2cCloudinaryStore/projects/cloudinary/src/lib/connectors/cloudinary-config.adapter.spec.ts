import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { OccEndpointsService } from '@spartacus/core';
import {
  HttpCloudinaryConfigAdapter,
  CLOUDINARY_CONFIG_ADAPTER,
} from './cloudinary-config.adapter';
import { CloudinaryConfig } from '../models/cloudinary-config.model';
import { of } from 'rxjs';

class MockOccEndpointsService {
  buildUrl(key: string): string {
    return `/base/${key}`;
  }
}

describe('HttpCloudinaryConfigAdapter', () => {
  let adapter: HttpCloudinaryConfigAdapter;
  let httpMock: HttpTestingController;
  let occ: OccEndpointsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: OccEndpointsService, useClass: MockOccEndpointsService },
        {
          provide: CLOUDINARY_CONFIG_ADAPTER,
          useClass: HttpCloudinaryConfigAdapter,
        },
      ],
    });

    adapter = TestBed.inject(
      CLOUDINARY_CONFIG_ADAPTER,
    ) as HttpCloudinaryConfigAdapter;
    httpMock = TestBed.inject(HttpTestingController);
    occ = TestBed.inject(OccEndpointsService);
  });

  it('should be created', () => {
    expect(adapter).toBeTruthy();
  });

  it('should build URL via occ service', () => {
    const spy = spyOn(occ, 'buildUrl').and.callThrough();
    const dummy: CloudinaryConfig = {
      isCloudinaryGalleryEnabled: true,
      cloudinaryGalleryConfigJsonString: '{}',
    };

    adapter.loadConfig().subscribe((cfg) => {
      expect(cfg).toEqual(dummy);
    });

    expect(spy).toHaveBeenCalledWith('cloudinaryConfiguration');
    const req = httpMock.expectOne('/base/cloudinaryConfiguration');
    expect(req.request.method).toBe('GET');
    req.flush(dummy);
  });

  afterEach(() => {
    httpMock.verify();
  });
});
