/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const lodash = require('lodash');
const bundlePaths = require('smartedit-build/bundlePaths');

module.exports = function (config) {
    config.set({
        basePath: '',
        frameworks: ['jasmine', '@angular-devkit/build-angular'],
        plugins: [
            require('karma-jasmine'),
            require('karma-chrome-launcher'),
            require('karma-jasmine-html-reporter'),
            require('karma-junit-reporter'),
            require('karma-coverage'),
            require('@angular-devkit/build-angular/plugins/karma')
        ],
        files: bundlePaths.test.unit.commonUtilModules,
        client: {
            jasmine: {
                // you can add configuration options for Jasmine here
                // the possible options are listed at https://jasmine.github.io/api/edge/Configuration.html
                // for example, you can disable the random execution with `random: false`
                // or set a specific seed with `seed: 4321`
            },
            clearContext: config.singleRun // leave Jasmine Spec Runner output visible in browser
        },
        jasmineHtmlReporter: {
            suppressAll: true // removes the duplicated traces
        },
        junitReporter: {
            outputDir: 'junit', // results will be saved as $outputDir/$browserName.xml
            outputFile: 'testReport.xml', // if included, results will be saved as $outputDir/$browserName/$outputFile
            suite: '' // suite will become the package name attribute in xml testsuite element
        },
        coverageReporter: {
            dir: require('path').join(__dirname, './junit'),
            subdir: './coverageReport',
            reporters: [{ type: 'html' }, { type: 'text-summary' }]
        },
        reporters: ['progress', 'junit', 'kjhtml'],
        port: 9876,
        colors: true,
        logLevel: config.LOG_INFO,
        autoWatch: true,
        browsers: ['Chrome'],
        singleRun: false,
        restartOnFileChange: true
    });
};
