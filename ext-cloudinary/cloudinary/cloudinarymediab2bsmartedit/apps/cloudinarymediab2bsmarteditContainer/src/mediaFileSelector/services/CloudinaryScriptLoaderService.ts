// cloudinary-script-loader.service.ts

import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class CloudinaryScriptLoaderService {
    private isLoaded = false;
    private readonly scriptUrl = 'https://media-library.cloudinary.com/global/all.js';

    load(): Promise<void> {
        if (this.isLoaded) {
            return Promise.resolve();
        }

        return new Promise((resolve, reject) => {
            const script = document.createElement('script');
            script.src = this.scriptUrl;
            script.type = 'text/javascript';
            script.async = true;
            script.charset = 'utf-8';
            script.onload = () => {
                this.isLoaded = true;
                resolve();
            };
            script.onerror = (error) => {
                console.error('Cloudinary script could not be loaded.', error);
                reject(error);
            };
            document.head.appendChild(script);
        });
    }
}