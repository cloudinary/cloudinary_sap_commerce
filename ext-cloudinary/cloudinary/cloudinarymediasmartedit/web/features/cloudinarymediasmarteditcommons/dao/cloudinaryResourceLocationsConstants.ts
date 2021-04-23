/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:CONTEXT_CATALOG
 *
 * @description
 * Constant containing the name of the catalog uid placeholder in URLs
 */

export const CONTEXT_CATALOG = 'CURRENT_CONTEXT_CATALOG';

/**
 * @ngdoc object
 * @name resourceLocationsModule.object:CONTEXT_CATALOG_VERSION
 *
 * @description
 * Constant containing the name of the catalog version placeholder in URLs
 */

export const CONTEXT_CATALOG_VERSION = 'CURRENT_CONTEXT_CATALOG_VERSION';

/**
 * @ngdoc object
 * @name resourceLocationsModule.object:CONTEXT_SITE_ID
 *
 * @description
 * Constant containing the name of the site uid placeholder in URLs
 */

export const CONTEXT_SITE_ID = 'CURRENT_CONTEXT_SITE_ID';

/**
 * @ngdoc object
 * @name resourceLocationsModule.object:CLOUDINARYWEBSERVICES_PATH
 *
 * @description
 * Regular expression identifying CMS related URIs
 */
export const CLOUDINARYWEBSERVICES_PATH = /\/rest/;

/**
 * @ngdoc object
 * @name resourceLocationsModule.object:CLOUDINARY_MEDIA_RESOURCE_URI
 *
 * @description
 * Resource URI of the custom components REST service.
 */
export const CLOUDINARY_MEDIA_RESOURCE_URI = `/rest/v2/apparel-uk/catalogs/${CONTEXT_CATALOG}/versions/${CONTEXT_CATALOG_VERSION}/media`;
