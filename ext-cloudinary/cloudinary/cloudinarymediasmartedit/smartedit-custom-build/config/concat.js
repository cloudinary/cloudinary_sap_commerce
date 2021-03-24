/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['styling'],
        config: function(data, baseConf) {
            baseConf.styling = {
                separator: ';',
                src: ['web/webroot/css/*.css'],
                dest: 'web/webroot/css/style.css'
            };

            return baseConf;
        }
    };
};
