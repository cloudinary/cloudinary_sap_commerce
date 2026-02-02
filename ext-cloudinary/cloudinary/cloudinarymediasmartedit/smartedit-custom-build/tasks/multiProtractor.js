/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function(grunt) {
    var lodash = require('lodash');
    var chromeDriverPattern = 'node_modules/**/chromedriver*';

    function execute(useMultipleInstances) {
        var paths = require('../paths');

        var conf = grunt.config.get('protractor');

        if (
            grunt.file.expand(
                {
                    filter: 'isFile'
                },
                chromeDriverPattern
            ).length > 0
        ) {
            var specs = global.smartedit.taskUtil.protractor.getSpecs(paths.e2eFiles);
            // start with maxrun target, defined in the base smartedit-build/config if multiInstance is set
            var baseConf = !!useMultipleInstances
                ? lodash.cloneDeep(conf.maxrun || {})
                : lodash.cloneDeep(conf.run || {});

            var options = {
                options: {
                    // default protractor conf will come from parent options
                    args: {
                        specs: specs
                    }
                }
            };
            lodash.merge(baseConf, options);

            var taskKey = 'e2especs';
            conf[taskKey] = baseConf;
            grunt.task.run('protractor:' + taskKey);

            grunt.config.set('protractor', conf);
        } else {
            grunt.log.warn(
                'protractor grunt phase was not run since no chrome driver found under ' +
                    chromeDriverPattern
            );
        }
    }

    grunt.registerTask(
        'multiProtractor',
        'Executes end to end tests for each project via protractor separately',
        function() {
            execute(false);
        }
    );

    grunt.registerTask(
        'multiProtractorMax',
        'Executes end to end tests for each project via protractor separately',
        function() {
            execute(true);
        }
    );
};
