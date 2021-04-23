/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

import {
  IExperience,
  IRestService,
  ISharedDataService,
  RestServiceFactory,
  SeInjectable
} from 'smarteditcommons';

export interface Media {
    code: string;
    description: string;
    altText: string;
    cloudinaryMediaJson: string;
    catalogId: string;
    catalogVersion: string;
    downloadUrl: string;
    mime: string;
    url: string;
    uuid: string;
}
/**
 * @ngdoc service
 * @name cmsCloudinarySmarteditServicesModule.seCloudinaryMediaService
 * @description
 * This service provides functionality to upload images and to fetch images by code for a specific catalog-catalog version combination.
 */
@SeInjectable()
export class SeCloudinaryMediaService {
    private mediaRestService: IRestService<Media>;
    private cloudianryMediaResourceUri: string = '/rest/v2/:siteId/catalogs/:catalogId/versions/:catalogVersion/cloudinaryMedia';

    constructor(
        private restServiceFactory: RestServiceFactory,
        private sharedDataService: ISharedDataService
    ) {
      }

    /**
     * @ngdoc method
     * @name cmsCloudinarySmarteditServicesModule.seCloudinaryMediaService.uploadMedia
     * @methodOf cmsSmarteditServicesModule.seCloudinaryMediaService
     *
     * @description
     * Uploads the media to the catalog.
     *
     * @param {Object} media The media to be uploaded
     * @param {String} media.code A unique code identifier for the media
     * @param {String} media.description A description of the media
     * @param {String} media.altText An alternate text to be shown for the media
     * @param {String} media.cloudinaryMediaJson The json string returned from Cloudinary Media Library
     *
     * @returns {Promise<object>} If request is successful, it returns a promise that resolves with the media object. If the
     * request fails, it resolves with errors from the backend.
     */
    uploadMedia(media: Media): Promise<Media> {


        return this.sharedDataService.get('experience').then((experience: IExperience) => {

          this.mediaRestService = this.restServiceFactory.get(
            this.cloudianryMediaResourceUri
            .replace(':siteId', experience.siteDescriptor.uid)
            .replace(':catalogId', experience.catalogDescriptor.catalogId)
            .replace(':catalogVersion', experience.catalogDescriptor.catalogVersion)
          );

          return this.mediaRestService.save({
              code: media.code,
              cloudinaryMediaJson: media.cloudinaryMediaJson
          });
        });
    }
}
