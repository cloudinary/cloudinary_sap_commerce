/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['cloudinarymediasmartedit', 'cloudinarymediasmarteditcommons', 'cloudinarymediasmarteditContainer'],
        config: function(data, conf) {
            const sourcesRoot = 'web/features/';

            function generateConfigForFolder(folderName) {
                return {
                    src: [sourcesRoot + folderName + '/**/*Template.html'],
                    dest: 'jsTarget/' + sourcesRoot + folderName + '/templates.js',
                    options: {
                        standalone: true, //to declare a module as opposed to binding to an existing one
                        module: folderName + 'Templates'
                    }
                };
            }

            conf.cloudinarymediasmartedit = generateConfigForFolder('cloudinarymediasmartedit');
            conf.cloudinarymediasmarteditcommons = generateConfigForFolder('cloudinarymediasmarteditcommons');
            conf.cloudinarymediasmarteditContainer = generateConfigForFolder('cloudinarymediasmarteditContainer');

            return conf;
        }
    };
};
