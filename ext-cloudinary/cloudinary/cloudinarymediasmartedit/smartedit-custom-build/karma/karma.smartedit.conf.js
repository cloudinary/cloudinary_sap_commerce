/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
const lodash = require('lodash');

const base = require('../../smartedit-build/config/karma/karma.ext.smartedit.conf');
const bundlePaths = require('../../smartedit-build/bundlePaths');

const { compose, merge, add } = require('../../smartedit-build/builders');

const karma = compose(
    merge({
        junitReporter: {
            outputDir: 'jsTarget/tests/cloudinarymediasmartedit/junit/', // results will be saved as $outputDir/$browserName.xml
            outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
        },

        // list of files / patterns to load in the browser
        files: lodash.concat(
            bundlePaths.test.unit.smarteditThirdPartyJsFiles,
            bundlePaths.test.unit.commonUtilModules,
            [
                'jsTarget/web/features/cloudinarymediasmarteditcommons/**/*.js',
                'jsTarget/web/features/cloudinarymediasmartedit/**/*.js',
                'jsTarget/web/features/cloudinarymediasmartedit/templates.js',
                'jsTests/tests/cloudinarymediasmartedit/unit/specBundle.ts'
            ]
        ),

        webpack: require('../webpack/webpack.karma.smartedit.config')
    }),
    add(
        'exclude',
        ['jsTarget/web/features/cloudinarymediasmartedit/cloudinarymediasmarteditModule.ts', '**/*.d.ts', '*.d.ts'],
        true
    )
)(base);

module.exports = function(config) {
    config.set(karma);
};
