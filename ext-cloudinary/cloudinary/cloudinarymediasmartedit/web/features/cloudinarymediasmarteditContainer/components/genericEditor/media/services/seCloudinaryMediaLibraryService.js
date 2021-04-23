/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
angular
    .module('seCloudinaryMediaLibraryServiceModule', [])
    .factory('seCloudinaryMediaLibraryService', function($http, $q, restServiceFactory) {
      var getAccessTokenByPost = function() {
          // tslint:disable-next-line:no-console
          console.log("getAccessTokenByPost is called");
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
                  // tslint:disable-next-line:no-console
                  console.log(data);
                  deferred.resolve(data);
              },
              function(error) {
                  // tslint:disable-next-line:no-console
                  console.log("getting Access Token failed.");
                  deferred.reject(error);
              }
          );
          return deferred.promise;
      };

      var getCloudinaryConfiguration = function() {
          // tslint:disable-next-line:no-console
          console.log("getCloudinaryConfiguration is called");
          var dfd = $q.defer();
          var configService = restServiceFactory.get('/rest/v2/apparel-uk/cloudinary/configuration');
          configService.get().then(
            function(data) {
                // tslint:disable-next-line:no-console
                console.log(data);
                dfd.resolve(data);
            },
            function(error) {
                dfd.reject(error);
            }
          );
          /*$http({
                  method:      "GET",
                  url:         '/rest/v2/apparel-uk/cloudinary/configuration',
                  headers:     {
                    "Content-Type": "application/json"
                  }
          }).then(
              function(data) {
                  // tslint:disable-next-line:no-console
                  console.log("Response from configuration:");
                  // tslint:disable-next-line:no-console
                  console.log(data);
                  dfd.resolve(data);
              },
              function(error) {
                  dfd.reject(error);
              }
          );*/

          return dfd.promise;
      };

      return {
          getAccessTokenByPost: getAccessTokenByPost,
          getCloudinaryConfiguration: getCloudinaryConfiguration
      };
    });
