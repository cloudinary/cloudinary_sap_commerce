/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
 /*jshint esversion: 6 */
angular
    .module('seCloudinaryMediaContainerFieldModule', [
        'cloudinarymediasmarteditContainerTemplates',
        'seMediaUploadFormModule',
        'seCloudinaryMediaFormatModule',
        'seErrorsListModule',
        'seFileValidationServiceModule',
        'cmsSmarteditServicesModule',
        'seCloudinaryMediaUploadFormModule',
        'seCloudinaryMediaLibraryServiceModule'
    ])
    /**
     * @description
     * Constant containing the uri of the media containers endpoint.
     */
    .constant(
        'MEDIA_CONTAINERS_URI',
        '/cmswebservices/v1/catalogs/CURRENT_CONTEXT_CATALOG/versions/CURRENT_CONTEXT_CATALOG_VERSION/mediacontainers'
    )
    .controller('seCloudinaryMediaContainerFieldController', function(
        $http,
        $log,
        $q,
        seFileValidationService,
        seCloudinaryMediaLibraryService,
        typePermissionsRestService,
        systemEventService,
        lodash,
        loadConfigManagerService,
        LINKED_DROPDOWN,
        MEDIA_CONTAINERS_URI
    ) {
        this.$onInit = function() {
            this.advancedMediaContainerManagementEnabled = false;
            this.mediaContainerDropdownId = this.getMediaContainerDropdownId();
            this.mediaContainerDropdownField = this.getMediaContainerDropdownField();

            this.initMediaContainerFieldModel();

            this.itemTemplateUrl = 'seCloudinaryAdvancedMediaContainerDropdownItemTemplate.html';
            this.mediaContainerCreationInProgress = false;

            loadConfigManagerService.loadAsObject().then(
                function(configurations) {
                    this.advancedMediaContainerManagementEnabled =
                        configurations.advancedMediaContainerManagement || false;
                }.bind(this)
            );

            this.image = {};
            this.fileErrors = [];
            //this.isCloudinaryEnabled = true;

            typePermissionsRestService.hasAllPermissionsForTypes(this.field.containedTypes).then(
                function(permissionsResult) {
                    this.hasReadPermissionOnMediaRelatedTypes = this.field.containedTypes.every(
                        function(type) {
                            return permissionsResult[type].read;
                        }
                    );
                }.bind(this),
                function(error) {
                    $log.warn('Failed to retrieve type permissions', error);
                    this.hasReadPermissionOnMediaRelatedTypes = false;
                }.bind(this)
            );

            this.unregCreateMediaContainerFn = systemEventService.subscribe(
                this.getCreateMediaContainerEventName(),
                function(eventId, newMediaContainerQualifier) {
                    this.clearModel();
                    this.assignNewDataToModel({
                        qualifier: newMediaContainerQualifier,
                        medias: {}
                    });
                    this.mediaContainerCreationInProgress = true;
                }.bind(this)
            );

            this.unregSelectMediaContainerFn = systemEventService.subscribe(
                this.getSelectMediaContainerEventName(),
                function(eventId, selectedItem) {
                    this.mediaContainerCreationInProgress = false;
                    if (!this.mediaContainerFieldModel.mediaContainer) {
                        this.clearModel();
                    } else {
                        if (selectedItem.optionObject) {
                            this.assignNewDataToModel(selectedItem.optionObject);
                        }
                    }
                }.bind(this)
            );

            loadMediaLibraryWidget().then(
              function() {
                  seCloudinaryMediaLibraryService.getCloudinaryConfiguration().then(
                    function(data) {

                      window.ml = window.cloudinary.createMediaLibrary(
                        {
                          cloud_name: data.cloudName,
                          api_key: data.apiKey,
                          multiple: false,
                          max_files: 1,
                        },
                        {
                          insertHandler: function(data) {
                            data.assets.forEach(asset => {
                              var cloudinaryResponse = JSON.stringify(asset);

                              if (cloudinaryResponse !== "") {
                                var selectedFormat = this.image.format;
                                this.image = {
                                  code: asset.public_id + '.' + asset.format,
                                  cloudinaryMediaJson: cloudinaryResponse,
                                  format: selectedFormat
                                };
                              }
                            });
                          }.bind(this)
                        }
                      );
                    }.bind(this)
                  );
              }.bind(this)
            );
        };



        this.$onDestroy = function() {
            this.unregCreateMediaContainerFn();
            this.unregSelectMediaContainerFn();
        };

        this.fileSelected = function(files, format) {
            var previousFormat = this.image.format;
            this.resetImage();

            if (files.length === 1) {
                seFileValidationService.validate(files[0], this.fileErrors).then(
                    function() {
                        this.image = {
                            file: files[0],
                            format: format || previousFormat
                        };
                    }.bind(this)
                );
            }
        };

        this.showMediaLibrary = function(format) {
          this.resetImage();
          this.image = { format: format };
          window.ml.show();
        };

        this.resetImage = function() {
            this.fileErrors = [];
            this.image = {};
        };

        this.imageUploaded = function(uuid) {
            this.initModelIfUndefined();
            if (this.model && this.model[this.qualifier]) {
                this.model[this.qualifier].medias[this.image.format] = uuid;
            } else {
                this.model[this.qualifier] = {};
                this.model[this.qualifier].medias[this.image.format] = uuid;
            }

            this.resetImage();
        };

        this.imageDeleted = function(format) {
            if (!lodash.isNil(this.model[this.qualifier])) {
                delete this.model[this.qualifier].medias[format];
            }
        };

        this.isFormatUnderEdit = function(format) {
            return format === this.image.format;
        };

        /**
         * Configuration for media container dropdown that allows to display Create button if media container is not found.
         */
        this.configureSeDropdown = function(api) {
            var template =
                "<y-actionable-search-item data-event-id='" +
                this.getCreateMediaContainerEventName() +
                "'></y-actionable-search-item>";
            api.setResultsHeaderTemplate(template);
        };

        /**
         * Returns the name of the event that is triggered when user clicks on Create new media container button.
         */
        this.getCreateMediaContainerEventName = function() {
            return (
                'CREATE_MEDIA_CONTAINER_BUTTON_PRESSED_EVENT' +
                '_' +
                this.field.qualifier +
                '_' +
                this.qualifier
            );
        };

        /**
         * Returns the name of the event that is triggered when user selects existing media container.
         */
        this.getSelectMediaContainerEventName = function() {
            return this.mediaContainerDropdownId + LINKED_DROPDOWN;
        };

        /**
         * Returns the media container field to render media containers dropdown.
         */
        this.getMediaContainerDropdownField = function() {
            return {
                qualifier: 'mediaContainer',
                required: true,
                uri: MEDIA_CONTAINERS_URI,
                idAttribute: 'qualifier',
                editable: true,
                paged: true
            };
        };

        /**
         * Verifies whether the media container is populated.
         */
        this.isMediaContainerSelected = function() {
            return (
                !lodash.isNil(this.model[this.qualifier]) &&
                !lodash.isEmpty(this.model[this.qualifier])
            );
        };

        /**
         * Returns the id of media container dropdown
         */
        this.getMediaContainerDropdownId = function() {
            return 'mediaContainer_' + this.field.qualifier + '_' + this.qualifier;
        };

        /**
         * Verifies whether the name of the media container is readonly and can not be changed.
         */
        this.isMediaContainerNameReadOnly = function() {
            return this.isMediaContainerSelected() && !this.isMediaContainerCreation();
        };

        /**
         * Verifies whether we are currently creating a new media container.
         */
        this.isMediaContainerCreation = function() {
            return this.mediaContainerCreationInProgress === true; // TODO: DO we need this
        };

        /**
         * Removes all attributes from the model object. Allows to preserv the same reference to the object.
         */
        this.clearModel = function() {
            if (!lodash.isNil(this.model[this.qualifier])) {
                lodash.keys(this.model[this.qualifier]).forEach(
                    function(attribute) {
                        delete this.model[this.qualifier][attribute];
                    }.bind(this)
                );
            }
        };

        /**
         * Applies new data to the existing model.
         */
        this.assignNewDataToModel = function(newData) {
            this.initModelIfUndefined();
            lodash.assign(this.model[this.qualifier], newData);
        };

        /**
         * Need to initialize the model cause it has a complex structure (medias attribute inside)
         */
        this.initModelIfUndefined = function() {
            if (lodash.isNil(this.model[this.qualifier])) {
                this.model[this.qualifier] = {
                    medias: {}
                };
            }
        };

        /**
         * The mediaContainerFieldModel is independent from this.model and must be initialized based on the info from this.model.
         */
        this.initMediaContainerFieldModel = function() {
            if (!lodash.isEmpty(this.model[this.qualifier])) {
                this.mediaContainerFieldModel = {
                    mediaContainer: this.model[this.qualifier].qualifier
                };
            } else {
                this.mediaContainerFieldModel = {};
            }
        };

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
    })
    .component('seCloudinaryMediaContainerField', {
        templateUrl: 'seCloudinaryMediaContainerFieldTemplate.html',
        controller: 'seCloudinaryMediaContainerFieldController',
        controllerAs: 'ctrl',
        bindings: {
            field: '<',
            model: '<',
            editor: '<',
            qualifier: '<',
            isFieldDisabled: '<'
        }
    });
