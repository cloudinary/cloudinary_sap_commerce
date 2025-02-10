/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function(grunt) {
    /**
     *
     * Configuration for the karma grunt task
     *
     * In this extension, karma is triggered programmatically by the multiKarma task.
     * multiKarma executes karma once for each directory in /jsTests, and executes karma
     * with the directory name as the target.
     *
     */

    return {
        /**
         * The multiTarget-targets.
         * This should match the names of the directories in jsTests.
         */
        targets: ['cloudinarymediasmartedit', 'cloudinarymediasmarteditcommons', 'cloudinarymediasmarteditContainer'],
        config: function(data, conf) {
            const bundlePaths = require('../../smartedit-build/bundlePaths');
            const customPaths = require('../paths');
            const { coverage } = require('../../smartedit-build/builders/karma');

            /**
             * The keys of this object should match the directories and targets above.
             * The base smartedit configuration provides unitSmartedit, and unitSmarteditContainer OOTB.
             * These configurations load all the smartedit and thirparty files that you would have in the
             * real application.
             */
            const config = {
                cloudinarymediasmartedit: conf.unitSmartedit,
                cloudinarymediasmarteditContainer: conf.unitSmarteditContainer,
                cloudinarymediasmarteditcommons: {
                    options: {
                        configFile: bundlePaths.external.karma.smarteditCommons
                    }
                }
            };

            if (grunt.option('coverage')) {
                config.cloudinarymediasmartedit.options = coverage(
                    customPaths.coverage.dir,
                    customPaths.coverage.smarteditDirName
                )(config.cloudinarymediasmartedit.options);

                config.cloudinarymediasmarteditcommons.options = coverage(
                    customPaths.coverage.dir,
                    customPaths.coverage.smarteditcommonsDirName
                )(config.cloudinarymediasmarteditcommons.options);

                config.cloudinarymediasmarteditContainer.options = coverage(
                    customPaths.coverage.dir,
                    customPaths.coverage.smarteditcontainerDirName
                )(config.cloudinarymediasmarteditContainer.options);
            }

            return config;
        }
    };
};
