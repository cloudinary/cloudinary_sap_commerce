/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['test', 'dev', 'pack', 'e2e'],
        config: function(data, conf) {
            return {
                test: {
                    files: ['Gruntfile.js', 'web/features/**/*', 'jsTests/**/*'],
                    tasks: ['test'],
                    options: {
                        atBegin: true
                    }
                },
                dev: {
                    files: ['Gruntfile.js', 'web/features/**/*', 'jsTests/**/*'],
                    tasks: ['dev'],
                    options: {
                        atBegin: true
                    }
                },
                pack: {
                    files: ['Gruntfile.js', 'web/features/**/*', 'jsTests/**/*'],
                    tasks: ['package'],
                    options: {
                        atBegin: true
                    }
                },
                e2e: {
                    files: ['smartedit-custom-build/docs/**/*'],
                    tasks: ['ngdocs:e2e'],
                    options: {
                        atBegin: true
                    }
                }
            };
        }
    };
};
