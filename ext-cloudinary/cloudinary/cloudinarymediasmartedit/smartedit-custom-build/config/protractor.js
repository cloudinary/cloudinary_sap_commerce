/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    const dirNames = ['cloudinarymediasmartedit', 'cloudinarymediasmarteditcommons', 'cloudinarymediasmarteditContainer'];

    function getMaxName(name) {
        return `${name}_max`;
    }

    const targets = [];
    dirNames.forEach((dirName) => {
        targets.push(dirName);
        targets.push(getMaxName(dirName));
    });

    return {
        targets,
        config: function(data, conf) {
            const lodash = require('lodash');

            function createConfigFromFolderName(config, folderName) {
                const files = {
                    options: {
                        args: {
                            specs: [process.cwd() + '/jsTests/' + folderName + '/e2e/*Test.js']
                        }
                    }
                };

                // Regular 1 instance protrator
                const run = lodash.cloneDeep(conf.run);
                lodash.merge(run, files);
                config[folderName] = run;

                // Multi instance protractorMax
                const maxrun = lodash.cloneDeep(conf.maxrun);
                lodash.merge(maxrun, files);
                config[getMaxName(folderName)] = maxrun;
            }

            dirNames.forEach((name) => {
                createConfigFromFolderName(conf, name);
            });

            return conf;
        }
    };
};
