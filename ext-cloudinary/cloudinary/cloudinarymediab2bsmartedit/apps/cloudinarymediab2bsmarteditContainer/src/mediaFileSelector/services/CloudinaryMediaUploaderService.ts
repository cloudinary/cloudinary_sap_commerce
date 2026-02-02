import { Injectable } from '@angular/core';
import { IRestService, RestServiceFactory, ISharedDataService } from '@smart/utils';
import {
    IExperience,
    IMediaToUpload,
    MediaToUpload,
    ICMSMedia,
    MEDIA_RESOURCE_URI
} from 'smarteditcommons';
import * as lodash from 'lodash';
import { ConfigurationObject, LoadConfigManagerService } from 'smarteditcontainer';

@Injectable()
export class CloudinaryMediaUploaderService extends IMediaToUpload {
    private readonly mediaRestService: IRestService<ICMSMedia>;

    constructor(
        private restServiceFactory: RestServiceFactory,
        private sharedDataService: ISharedDataService,
        private loadConfigManagerService: LoadConfigManagerService
    ) {
        super();
        this.mediaRestService = this.restServiceFactory.get(MEDIA_RESOURCE_URI);
    }

    public async uploadMedia(media: MediaToUpload): Promise<ICMSMedia> {
        const experience = (await this.sharedDataService.get('experience')) as IExperience;

        const cloudinaryEndpointDynamic = await this.buildCloudinaryEndpoint(experience);

        const mediaRestService = this.restServiceFactory.get<ICMSMedia>(cloudinaryEndpointDynamic);
        try {
            const jsonString = await media.file.text();
            let mediaObject: any;
            try {
                mediaObject = JSON.parse(jsonString);
            } catch (e) {
                // Not a valid JSON, fallback to super.uploadMedia
                return this.uploadLocalMedia(media);
            }
            if (mediaObject.isCloudinary) {
                const formData = new FormData();
                lodash.forEach(media, (value, key: string) => {
                    formData.append(key, value);
                });
                formData.append('cloudinaryMediaJson', mediaObject.cloudinaryMediaJson);
                return mediaRestService.save(formData as any, {
                    headers: { enctype: 'multipart/form-data', fileSize: '' + media?.file?.size }
                });
            }
        } catch (error) {
            console.error('Failed to process and upload Cloudinary media file.', error);
            // Propagate the error in a format the framework expects.
            return Promise.reject(error);
        }

        // If it's a regular file, let the original OOTB logic handle it.
        return this.uploadLocalMedia(media);
    }

    /**
     * Uploads the media to the catalog.
     *
     * @returns Promise that resolves with the media object if request is successful.
     * If the request fails, it resolves with errors from the backend.
     */
    uploadLocalMedia(media: MediaToUpload): Promise<ICMSMedia> {
        const formData = new FormData();
        lodash.forEach(media, (value, key: string) => {
            formData.append(key, value);
        });

        return this.mediaRestService.save(formData as any, {
            headers: { enctype: 'multipart/form-data', fileSize: '' + media?.file?.size }
        });
    }

    private async getOccBaseUrl(): Promise<string> {
        const conf: ConfigurationObject = await this.loadConfigManagerService.loadAsObject();
        return String(conf.cloudinaryOccBaseUrl);
    }

    private async buildCloudinaryEndpoint(experience: IExperience): Promise<string> {
        if (!experience?.siteDescriptor?.uid || !experience?.catalogDescriptor) {
            throw new Error('Invalid experience object. Cannot build Cloudinary endpoint.');
        }

        const path = `/occ/v2/${experience.siteDescriptor.uid}/catalogs/${experience.catalogDescriptor.catalogId}/versions/${experience.catalogDescriptor.catalogVersion}/cloudinaryMedia`;

        let baseUrl = '';
        if (typeof this.getOccBaseUrl === 'function') {
            try {
                baseUrl = (await this.getOccBaseUrl()) || '';
                baseUrl = baseUrl.replace(/\/+$/, ''); // Remove trailing slashes
            } catch (e) {
                console.warn('Failed to retrieve OCC base URL, falling back to relative path.', e);
            }
        }

        return `${baseUrl}${path}`;
    }
}
