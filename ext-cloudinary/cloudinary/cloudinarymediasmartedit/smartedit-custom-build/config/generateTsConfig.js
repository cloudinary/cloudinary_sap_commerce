/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
module.exports = function() {
    return {
        config: function(data, conf) {
            const lodash = require('lodash');

            const cloudinarymediasmarteditPaths = {
                'cloudinarymediasmartedit/*': ['web/features/cloudinarymediasmartedit/*'],
                cloudinarymediasmarteditcommons: ['web/features/cloudinarymediasmarteditcommons'],
                'cloudinarymediasmarteditcommons*': ['web/features/cloudinarymediasmarteditcommons*']
            };

            const yssmarteditmoduleContainerPaths = {
                'cloudinarymediasmarteditcontainer/*': ['web/features/cloudinarymediasmarteditContainer/*'],
                cloudinarymediasmarteditcommons: ['web/features/cloudinarymediasmarteditcommons'],
                'cloudinarymediasmarteditcommons*': ['web/features/cloudinarymediasmarteditcommons*']
            };

            const commonsLayerInclude = [
                '../../jsTarget/web/features/cloudinarymediasmarteditcommons/**/*'
            ];
            const innerLayerInclude = commonsLayerInclude.concat([
                '../../jsTarget/web/features/cloudinarymediasmartedit/**/*'
            ]);
            const outerLayerInclude = commonsLayerInclude.concat([
                '../../jsTarget/web/features/cloudinarymediasmarteditContainer/**/*'
            ]);
            const commonsLayerTestInclude = [
                '../../jsTests/tests/cloudinarymediasmarteditcommons/unit/**/*'
            ];
            const innerLayerTestInclude = commonsLayerTestInclude.concat([
                '../../jsTests/tests/cloudinarymediasmartedit/unit/features/**/*'
            ]);
            const outerLayerTestInclude = commonsLayerTestInclude.concat([
                '../../jsTests/tests/cloudinarymediasmarteditContainer/unit/features/**/*'
            ]);

            function addYsmarteditmodulePaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(cloudinarymediasmarteditPaths));
            }

            function addYsmarteditmoduleContainerPaths(conf) {
                lodash.merge(
                    conf.compilerOptions.paths,
                    lodash.cloneDeep(yssmarteditmoduleContainerPaths)
                );
            }

            // PROD
            addYsmarteditmodulePaths(conf.generateProdSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateProdSmarteditContainerTsConfig.data);
            conf.generateProdSmarteditTsConfig.data.include = innerLayerInclude;
            conf.generateProdSmarteditContainerTsConfig.data.include = outerLayerInclude;

            // DEV
            addYsmarteditmodulePaths(conf.generateDevSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateDevSmarteditContainerTsConfig.data);
            conf.generateDevSmarteditTsConfig.data.include = innerLayerInclude;
            conf.generateDevSmarteditContainerTsConfig.data.include = outerLayerInclude;

            // KARMA
            addYsmarteditmodulePaths(conf.generateKarmaSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateKarmaSmarteditContainerTsConfig.data);
            conf.generateKarmaSmarteditTsConfig.data.include = innerLayerInclude.concat(
                innerLayerTestInclude
            );
            conf.generateKarmaSmarteditContainerTsConfig.data.include = outerLayerInclude.concat(
                outerLayerTestInclude
            );

            // IDE
            addYsmarteditmodulePaths(conf.generateIDETsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateIDETsConfig.data);
            conf.generateIDETsConfig.data.include = conf.generateIDETsConfig.data.include.concat([
                '../../jsTests/tests/**/unit/**/*'
            ]);

            return conf;
        }
    };
};
