/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['dev'],
        config: function(data, conf) {
            return {
                dev: {
                    files: [
                        {
                            expand: true,
                            cwd: 'web/features/styling',
                            src: '*.less',
                            dest: 'web/webroot/css/',
                            ext: '.css'
                        }
                    ]
                }
            };
        }
    };
};
