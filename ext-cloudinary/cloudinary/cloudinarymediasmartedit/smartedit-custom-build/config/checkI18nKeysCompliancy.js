/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        config: function(data, baseConf) {
            return {
                /**
                 * Check keys in the code to make sure they have the correct prefix
                 */
                prefix: {
                    ignored: [],
                    expected: ['cloudinarymediasmartedit.']
                },

                /**
                 * Check to make sure all keys are in the properties file
                 */
                paths: {
                    files: ['web/features/**/*Template.html', 'web/features/**/*.js'],
                    properties: ['resources/localization/cloudinarymediasmartedit-locales_en.properties']
                }
            };
        }
    };
};
