///*
// * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
// */
// /*jshint esversion: 6 */
//angular
//    .module('seMediaLibraryWidgetModule', [])
//    .controller('seMediaLibraryWidgetController', function() {
//        this.$onInit = function() {
//            this.disabled = this.disabled || false;
//            this.customClass = this.customClass || '';
//            this.selectionMode = this.selectionMode || 'replace';
//            loadMediaLibraryWidget()
//                        .then(
//                            renderMediaLibraryWidget
//                    );
//        };
//
//
//
//        this.isReplaceMode = function() {
//            return this.selectionMode === 'replace';
//        };
//
//        //Cloudinary Product gallery component
//        function loadMediaLibraryWidget() {
//            var url = "https://media-library.cloudinary.com/global/all.js";
//            var mlwScriptTag = document.createElement('script');
//            mlwScriptTag.src = url;
//            mlwScriptTag.type = 'text/javascript';
//            mlwScriptTag.charset = 'utf-8';
//            document.getElementsByTagName('head')[0].appendChild(mlwScriptTag);
//
//            return new Promise(function(resolve) {
//                                mlwScriptTag.onload = resolve;
//                               });
//        }
//
//        function renderMediaLibraryWidget() {
////              const body_tag = document.body;
////              const script_tag = document.createElement('script');
////              script_tag.innerHTML = `
//                                window.ml = window.cloudinary.openMediaLibrary({
//                                  cloud_name: "portaltech-reply",
//                                  api_key: "374658688623197",
//                                  inline_container: '.cms-container',
////                                  integration : {
////                                                  type : "CloudinarySAPCC",//the integration identifier e.g. “SAP CC”,
////                                                  platform : "SAP Commerce Cloud 2005",//hosting app name + version e.g. SAP CC 1.2.3,
////                                                  version : "1.0.0",//integration version e.g. 1.0.0
////                                                  environment : "dev"//e.g “prod”/“stag”/“dev”
////                                                },
//                                  multiple: false,
//                                  max_files: 1,
//                                  }, {
//                                       insertHandler: function(data) {
//                                         data.assets.forEach(asset => {
//                                         alert(JSON.stringify(asset));
//
//                                         });
//                                       }
//                                     }
//                                  );
////              `;
////              body_tag.appendChild(script_tag);
//        }
//    })
//    .directive('seMediaLibraryWidget', function() {
//        return {
//            templateUrl: 'seMediaLibraryWidgetTemplate.html',
//            restrict: 'E',
//            scope: {},
//            controller: 'seMediaLibraryWidgetController',
//            controllerAs: 'ctrl',
//            bindToController: {
//                selectionMode: '<?',
//                labelI18nKey: '<',
//                acceptedFileTypes: '<',
//                customClass: '<?',
//                disabled: '<?',
//                onFileSelect: '&'
//            }
//        };
//    });

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
 /*jshint esversion: 6 */
angular
    .module('seMediaLibraryWidgetModule', ['cloudinarymediasmarteditContainerTemplates'])
    .controller('seMediaLibraryWidgetController', function() {
        this.$onInit = function() {
            this.disabled = this.disabled || false;
            this.customClass = this.customClass || '';
            this.selectionMode = this.selectionMode || 'replace';

            loadMediaLibraryWidget()
                        .then(
                            renderMediaLibraryWidget
                    );
        };

        this.isReplaceMode = function() {
            return this.selectionMode === 'replace';
        };

        //Cloudinary Product gallery component
        function loadMediaLibraryWidget() {
            var url = "https://media-library.cloudinary.com/global/all.js";
            var mlwScriptTag = document.createElement('script');
            mlwScriptTag.src = url;
            mlwScriptTag.type = 'text/javascript';
            mlwScriptTag.charset = 'utf-8';
            document.getElementsByTagName('head')[0].appendChild(mlwScriptTag);

            return new Promise(function(resolve) {
                                mlwScriptTag.onload = resolve;
                               });
        }

        function renderMediaLibraryWidget() {
//              const body_tag = document.body;
//              const script_tag = document.createElement('script');
//              script_tag.innerHTML = `
                                window.ml = window.cloudinary.createMediaLibrary({
                                  cloud_name: "portaltech-reply",
                                  api_key: "374658688623197",
                                  //inline_container: '.cms-container',
                                  button_class: 'myBtn',
                                  button_caption: 'Select Image or Video',
//                                  integration : {
//                                                  type : "CloudinarySAPCC",//the integration identifier e.g. “SAP CC”,
//                                                  platform : "SAP Commerce Cloud 2005",//hosting app name + version e.g. SAP CC 1.2.3,
//                                                  version : "1.0.0",//integration version e.g. 1.0.0
//                                                  environment : "dev"//e.g “prod”/“stag”/“dev”
//                                                },
                                  multiple: false,
                                  max_files: 1,
                                  }, {
                                       insertHandler: function(data) {
                                         data.assets.forEach(asset => {
                                           //alert(JSON.stringify(asset));
                                           //this.cloudinaryMediaJson = JSON.stringify(asset);
                                           document.getElementById("assetJson").value = JSON.stringify(asset);
                                         });
                                       }
                                     },
                                     document.getElementsByClassName("open-btn")
                                  );
//              `;
//              body_tag.appendChild(script_tag);
        }
    })
    .directive('seMediaLibraryWidget', function() {
        return {
            templateUrl: 'seMediaLibraryWidgetTemplate.html',
            //template: '<div><input type="button" id="open-btn"/><input type="hidden" id="assetJson"/></div>',

            restrict: 'E',
            scope: {},
            controller: 'seMediaLibraryWidgetController',
            controllerAs: 'ctrl',
            bindToController: {
                selectionMode: '<?',
                labelI18nKey: '<',
                acceptedFileTypes: '<',
                customClass: '<?',
                disabled: '<?',
                onFileSelect: '&',
                onCloudinaryImageSelect: '&'
            },
//            link: function($scope) {
//                            document.getElementById("assetJson").on('onChange', function(event) {
//                                $timeout(function() {
//                                    $scope.ctrl.onFileSelect({
//                                        cloudinaryMediaJson: event.target.cloudinaryMediaJson
//                                        //alert("cloudinaryMediaJson-->"+cloudinaryMediaJson);
//                                    });
//                                    var input = document.getElementById("assetJson").find('input');
//                                    input.replaceWith(input.clone(true));
//                                });
//                            });
//            }
        };
    });
