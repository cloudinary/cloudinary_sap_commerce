/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['all'],
        config: function(data, conf) {
            conf.all = ['web/features/**/*.js', 'jsTests/**/*.js'];

            return conf;
        }
    };
};
