angular
   .module('cloudinaryCmsGenericEditorConfigurationServiceModule', [
     'editorFieldMappingServiceModule',
     'cloudinarymediasmarteditContainerTemplates'
   ])
   .service('cloudinaryCmsGenericEditorConfigurationService', function(editorFieldMappingService) {

     this.cloudinaryDefaultEditorFieldMappings = function() {
       editorFieldMappingService.addFieldMapping('MediaContainer', null, null, {
           template: 'cloudinaryMediaContainerTemplate.html'
       });
     };
   });
