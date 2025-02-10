/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
module.exports = (grunt) => {
    require('time-grunt')(grunt);
    require('./smartedit-build')(grunt).load();

    // -------------------------------------------------------------------------------------------------
    // File Generation
    // -------------------------------------------------------------------------------------------------
    grunt.registerTask('generate', ['generateTsConfig']);

    // -------------------------------------------------------------------------------------------------
    // Webpack
    // -------------------------------------------------------------------------------------------------
    grunt.registerTask('webpackDev', ['webpack:devSmartedit', 'webpack:devSmarteditContainer']);
    grunt.registerTask('webpackProd', ['webpack:prodSmartedit', 'webpack:prodSmarteditContainer']);

    // FORMATTING
    grunt.registerTask('formatCode', ['shell:prettierRun']);

    // -------------------------------------------------------------------------------------------------
    // Linting
    // -------------------------------------------------------------------------------------------------
    grunt.registerTask('checkFiles', [
        'jshint',
        'tslint',
        'checkNoForbiddenNameSpaces',
        'checkI18nKeysCompliancy',
        'checkNoFocus'
    ]);

    grunt.registerTask('compile_only', ['checkFiles', 'copy:sources', 'multiNGTemplates']);
    grunt.registerTask('compile', ['clean:target', 'compile_only']);

    grunt.registerTask('multiKarma', [
        'karma:cloudinarymediasmartedit',
        'karma:cloudinarymediasmarteditcommons',
        'karma:cloudinarymediasmarteditContainer'
    ]);

    grunt.registerTask('test_only', ['generate', 'multiKarma']);
    grunt.registerTask('test', ['generate', 'compile', 'instrumentSeInjectable', 'multiKarma']);

    grunt.registerTask('test', 'run unit tests', function() {
        let target = grunt.option('target') ? grunt.option('target').split(',') : [];
        let tasks = [];
        if (target && target.length) {
            target.forEach((t) => {
                if (t === 'inner') {
                    tasks.push('karma:cloudinarymediasmartedit');
                } else if (t === 'outer') {
                    tasks.push('karma:cloudinarymediasmarteditContainer');
                } else if (t === 'commons') {
                    tasks.push('karma:cloudinarymediasmarteditcommons');
                }
            });
        } else {
            tasks = ['multiKarma'];
        }
        if (grunt.option('browser') && !/^(inner|outer|commons)$/.test(grunt.option('target'))) {
            grunt.fail.fatal('Please set --target=outer, --target=inner or --target=commons');
        }
        grunt.task.run(['generate', 'compile', 'instrumentSeInjectable'].concat(tasks));
    });

    grunt.registerTask('coverage', 'run unit tests with coverage report', () => {
        grunt.option('coverage', true);
        grunt.task.run(['generate', 'multiKarma', 'connect:coverage']);
    });

    grunt.registerTask('concatAndPushDev', ['instrumentSeInjectable', 'webpackDev', 'copy:dev']);
    grunt.registerTask('concatAndPushProd', ['instrumentSeInjectable', 'webpackProd', 'copy:dev']);

    grunt.registerTask('dev', ['compile', 'concatAndPushDev']);

    grunt.registerTask('package_only', ['concatAndPushProd', 'ngdocs']);
    grunt.registerTask('package', ['test', 'package_only']);
    grunt.registerTask('packageSkipTests', ['generate', 'compile_only', 'package_only']);

    grunt.registerTask('e2e', ['connect:dummystorefront', 'connect:test', 'multiProtractor']);
    grunt.registerTask('e2e_max', [
        'connect:dummystorefront',
        'connect:test',
        'multiProtractorMax'
    ]);
    grunt.registerTask('e2e_dev', 'e2e local development mode', () => {
        grunt.option('keepalive_dummystorefront', true);
        grunt.option('open_browser', true);
        grunt.task.run(['connect:test', 'connect:dummystorefront']);
    });
    grunt.registerTask('e2e_debug', 'e2e local debug mode', () => {
        grunt.option('browser_debug', true);
        grunt.task.run('e2e');
    });
    grunt.registerTask('verify_only', ['e2e']);
    grunt.registerTask('verify', ['generate', 'package', 'verify_only']);
    grunt.registerTask('verify_max', ['generate', 'package', 'e2e_max']);
};
