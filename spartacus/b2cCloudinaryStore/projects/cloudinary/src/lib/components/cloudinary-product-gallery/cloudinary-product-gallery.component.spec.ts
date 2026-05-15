import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CloudinaryProductGalleryComponent } from './cloudinary-product-gallery.component';

describe('CloudinaryProductGalleryComponent', () => {
  let component: CloudinaryProductGalleryComponent;
  let fixture: ComponentFixture<CloudinaryProductGalleryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CloudinaryProductGalleryComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CloudinaryProductGalleryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
