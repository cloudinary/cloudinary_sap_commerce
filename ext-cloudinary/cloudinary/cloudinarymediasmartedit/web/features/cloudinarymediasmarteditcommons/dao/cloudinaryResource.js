/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
(function() {
    /**
     * @ngdoc overview
     * @name resourceModule
     *
     * @description
     * The resource module provides IRestClient factories.
     */
    angular
        .module('cloudinaryResourceModule', [
            'smarteditServicesModule',
            'resourceLocationsModule',
            'cmsCloudinaryResourceLocationsModule',
            'functionsModule'
        ])
        .factory('cloudinaryMediaResource', function(
            restServiceFactory,
            CLOUDINARY_MEDIA_RESOURCE_URI
        ) {
            return restServiceFactory.get(CLOUDINARY_MEDIA_RESOURCE_URI);
        });
})();
