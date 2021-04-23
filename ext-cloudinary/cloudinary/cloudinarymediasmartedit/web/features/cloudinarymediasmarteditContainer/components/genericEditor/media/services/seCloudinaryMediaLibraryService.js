/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
angular
    .module('seCloudinaryMediaLibraryServiceModule', [])
    .factory('seCloudinaryMediaLibraryService', function($http, $q, restServiceFactory) {
      var getAccessTokenByPost = function() {
          var deferred = $q.defer();
          var postData = {
              grant_type:    'client_credentials',
              client_id:     'client-side',
              client_secret: 'secret'
          };
          $http({
              url: '/authorizationserver/oauth/token',
              method: "POST",
              data: postData
          }).then(
              function(data) {
                  deferred.resolve(data);
              },
              function(error) {
                  deferred.reject(error);
              }
          );
          return deferred.promise;
      };

      var getCloudinaryConfiguration = function() {
          var dfd = $q.defer();
          var configService = restServiceFactory.get('/rest/v2/apparel-uk/cloudinary/configuration');
          configService.get().then(
            function(data) {
                dfd.resolve(data);
            },
            function(error) {
                dfd.reject(error);
            }
          );
          return dfd.promise;
      };

      return {
          getAccessTokenByPost: getAccessTokenByPost,
          getCloudinaryConfiguration: getCloudinaryConfiguration
      };
    });
