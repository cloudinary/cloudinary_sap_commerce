# Cloudinary Spartacus Module

This Angular library provides Cloudinary integration for SAP Commerce Cloud Spartacus applications.

## Installation

```bash
npm install @your-org/cloudinary-spartacus
```

## Usage

### 1. Import the module in your Spartacus configuration

In your `app.module.ts`, import the Cloudinary module:

```typescript
import { CloudinaryModule } from "@your-org/cloudinary-spartacus";

@NgModule({
  imports: [
    // ... other modules
    CloudinaryModule,
  ],
})
export class AppModule {}
```

This will automatically provide the custom product image normalizer that handles Cloudinary URL encoding.

### 2. (Optional) Configure Cloudinary gallery

If you want to use Cloudinary galleries, create a service that implements the Cloudinary configuration:

```typescript
import { Injectable } from "@angular/core";
import { Observable, of } from "rxjs";
import { CloudinaryConfig, CLOUDINARY_CONFIG_SERVICE, CloudinaryConfigService } from "@your-org/cloudinary-spartacus";

@Injectable()
export class MyCloudinaryConfigService implements CloudinaryConfigService {
  list(): Observable<CloudinaryConfig> {
    return of({
      isCloudinaryGalleryEnabled: true,
      cloudName: "your-cloud-name",
      cloudinaryGalleryConfigJsonString: '{"gallery": "config"}',
    });
  }
}
```

Then provide it in your module:

```typescript
import { CLOUDINARY_CONFIG_SERVICE } from "@your-org/cloudinary-spartacus";

@NgModule({
  providers: [
    {
      provide: CLOUDINARY_CONFIG_SERVICE,
      useClass: MyCloudinaryConfigService,
    },
  ],
})
export class MyModule {}
```

Create a service that implements the Cloudinary configuration. The service should have a `list()` method that returns an Observable of `CloudinaryConfig`.

```typescript
import { Injectable } from "@angular/core";
import { Observable, of } from "rxjs";
import { CloudinaryConfig } from "@your-org/cloudinary-spartacus";

@Injectable({
  providedIn: "root",
})
export class MyCloudinaryConfigService {
  list(): Observable<CloudinaryConfig> {
    return of({
      isCloudinaryGalleryEnabled: true,
      cloudName: "your-cloud-name",
      cloudinaryGalleryConfigJsonString: '{"gallery": "config"}',
    });
  }
}
```

### 3. Provide the custom normalizer

In your `app.module.ts`, provide the custom product image normalizer:

```typescript
import { ProductImageNormalizer } from "@spartacus/core";
import { CustomProductImageNormalizer } from "@your-org/cloudinary-spartacus";

@NgModule({
  providers: [
    {
      provide: ProductImageNormalizer,
      useClass: CustomProductImageNormalizer,
    },
  ],
})
export class AppModule {}
```

### 4. Configure Cloudinary settings

The module expects a `CurrentCloudinaryConfigService` to be available. Make sure to provide your implementation.

## Features

- Custom product image URL normalization (replaces commas with %2C for Cloudinary compatibility)
- Cloudinary gallery integration for product images
- Configurable Cloudinary settings via dependency injection
- Cloudinary video player component for seamless video integration

## API

### CloudinaryConfig

```typescript
interface CloudinaryConfig {
  isCloudinaryGalleryEnabled: boolean;
  CName?: string;
  cloudName?: string;
  cloudinaryGalleryConfigJsonString: string;
}
```

### Services

- `CloudinaryConfigService`: Interface for providing Cloudinary configuration
- `CLOUDINARY_CONFIG_SERVICE`: Injection token for the config service
- `CustomProductImageNormalizer`: Normalizer for product image URLs

### Components

- `CloudinaryVideoPlayerComponent`: Video player component for Cloudinary videos

#### CloudinaryVideoPlayerComponent

A component for playing videos hosted on Cloudinary.

**Selector:** `lib-cloudinary-video-player`

**Inputs:**

- `publicId: string` - The public ID of the video in Cloudinary
- `cloudName: string` - Your Cloudinary cloud name
- `width: number` - Video width (default: 640)
- `height: number` - Video height (default: 360)
- `controls: boolean` - Show video controls (default: true)
- `autoplay: boolean` - Auto-play the video (default: false)
- `muted: boolean` - Start muted (default: false)
- `loop: boolean` - Loop the video (default: false)

**Example:**

```html
<lib-cloudinary-video-player [publicId]="'my-video'" [cloudName]="'my-cloud'" [width]="800" [height]="450" [controls]="true" [autoplay]="false"> </lib-cloudinary-video-player>
```

## Building

To build the library, run:

```bash
ng build cloudinary
```

## Publishing

After building, navigate to `dist/cloudinary` and run:

```bash
npm publish
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
