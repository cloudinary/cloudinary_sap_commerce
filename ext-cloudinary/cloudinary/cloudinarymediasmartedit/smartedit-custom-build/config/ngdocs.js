/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['smartEdit', 'smartEditContainer', 'e2e', 'typescript'],
        config: function(data, conf) {
            return {
                options: {
                    dest: 'jsTarget/docs',
                    title: 'cloudinarymediasmartedit API',
                    startPage: '/#/cloudinarymediasmartedit'
                },
                smartEdit: {
                    api: true,
                    src: [
                        'web/features/cloudinarymediasmartedit/**/*.+(js|ts)',
                        'web/features/cloudinarymediasmarteditcommons/**/*.+(js|ts)'
                    ],
                    title: 'cloudinarymediasmartedit'
                },
                smartEditContainer: {
                    api: true,
                    src: [
                        'web/features/cloudinarymediasmartedit/**/*.+(js|ts)',
                        'web/features/cloudinarymediasmarteditcommons/**/*.+(js|ts)'
                    ],
                    title: 'cloudinarymediasmarteditContainer'
                },
                e2e: {
                    title: 'How-to: e2e Test Setup',
                    src: ['smartedit-custom-build/docs/e2eSetupNgdocs.js']
                },
                typescript: {
                    title: 'TypeScript',
                    src: ['smartedit-custom-build/docs/typescript.ts']
                }
            };
        }
    };
};
