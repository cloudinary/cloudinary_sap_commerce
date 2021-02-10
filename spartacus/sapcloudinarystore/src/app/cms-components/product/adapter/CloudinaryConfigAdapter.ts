// app/core/course.model.ts
import { Injectable } from "@angular/core";
import { Adapter } from "./adapter";
import {CloudinaryConfig} from '../model'

@Injectable({
  providedIn: "root",
})
export class CloudinaryConfigAdapter implements Adapter<CloudinaryConfig> {
  adapt(item: any): CloudinaryConfig {
    return new CloudinaryConfig(item.isCloudinaryGalleryEnabled, item.cloudName, item.cloudinaryGalleryConfigJsonString);
  }
}

