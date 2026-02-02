// cloudinary-config.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { lastValueFrom } from 'rxjs';
import { LoadConfigManagerService, ConfigurationObject } from 'smarteditcontainer';

/**
 * Defines the structure of the configuration object expected
 * from the API endpoint.
 */
export interface CloudinaryConfig {
    cloudName: string;
    apiKey: string;
    // Add any other properties returned by your configuration endpoint.
}

@Injectable({
    providedIn: 'root'
})
export class CloudinaryConfigService {
    // The API endpoint to fetch Cloudinary configuration.
    private readonly configUrl = '/occ/v2/apparel-uk/cloudinary/configuration';

    /**
     * Injects Angular's HttpClient for making API requests.
     * @param http The HttpClient instance.
     */
    constructor(
        private http: HttpClient,
        private loadConfigManagerService: LoadConfigManagerService
    ) {}

    private async getConfigUrl(): Promise<string> {
        const conf: ConfigurationObject = await this.loadConfigManagerService.loadAsObject();
        return String(conf.cloudinaryConfigUrl);
    }

    /**
     * Fetches the Cloudinary configuration using Angular's HttpClient.
     * This is now a pure Angular implementation, with no dependency on AngularJS.
     * @returns A Promise that resolves with the Cloudinary configuration.
     */
    async getConfiguration(): Promise<CloudinaryConfig> {
        // lastValueFrom converts the Observable returned by http.get() into a Promise,
        // which works seamlessly with the async/await syntax in the calling component.
        const configUrl = await this.getConfigUrl();
        return lastValueFrom(this.http.get<CloudinaryConfig>(configUrl));
    }
}
