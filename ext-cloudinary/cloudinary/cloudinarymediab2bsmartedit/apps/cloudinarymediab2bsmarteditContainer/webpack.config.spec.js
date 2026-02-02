/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { resolve } = require('path');
const base = require('smartedit-build/config/webpack/webpack.ext.karma.smartedit.config');

const {
    compose,
    webpack: { alias }
} = require('smartedit-build/builders');

module.exports = compose(alias('cloudinarymediab2bsmarteditContainer', resolve('src')))(base);
