import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { CloudinaryConfig } from '../models/cloudinary-config.model';
import {
  CloudinaryConfigConnector,
  CLOUDINARY_CONFIG_CONNECTOR,
} from '../connectors/cloudinary-config.connector';
import { CurrentCloudinaryConfigService } from './current-cloudinaryconfig.service';

class MockConnector implements CloudinaryConfigConnector {
  callCount = 0;
  getConfig() {
    this.callCount++;
    return of({
      isCloudinaryGalleryEnabled: true,
      cloudName: 'cached-name',
      cloudinaryGalleryConfigJsonString: '{}',
    } as CloudinaryConfig);
  }
}

describe('CurrentCloudinaryConfigService', () => {
  let service: CurrentCloudinaryConfigService;
  let connector: MockConnector;

  beforeEach(() => {
    connector = new MockConnector();
    TestBed.configureTestingModule({
      providers: [
        { provide: CLOUDINARY_CONFIG_CONNECTOR, useValue: connector },
      ],
    });
    service = TestBed.inject(CurrentCloudinaryConfigService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should cache the configuration after first load', () => {
    service.list().subscribe((cfg) => {
      expect(cfg.cloudName).toBe('cached-name');
    });
    service.list().subscribe();
    expect(connector.callCount).toBe(1);
  });
});
