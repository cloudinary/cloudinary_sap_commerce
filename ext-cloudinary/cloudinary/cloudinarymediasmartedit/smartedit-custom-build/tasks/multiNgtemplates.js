/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function(grunt) {
    /*
     * generates angular.module('run').run(['$templateCache', function($templateCache) {}]) module
     * that contains template caches so that they become minifyable !!!
     */
    grunt.registerTask('multiNGTemplates', function() {
        var multiNGTemplatesTask = [];

        grunt.file
            .expand(
                {
                    filter: 'isDirectory'
                },
                'web/features/*'
            )
            .forEach(function(dir) {
                var folderName = dir.replace('web/features/', '');

                multiNGTemplatesTask.push(folderName);
            });

        multiNGTemplatesTask.forEach(function(folderName) {
            grunt.task.run('ngtemplates:' + folderName);
        });
    });
};
