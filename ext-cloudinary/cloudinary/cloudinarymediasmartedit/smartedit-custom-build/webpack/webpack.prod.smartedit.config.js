/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('../../smartedit-build/config/webpack/webpack.ext.prod.smartedit.config');

const { compose } = require('../../smartedit-build/builders');

const { ySmartedit } = require('./webpack.shared.config');

module.exports = compose(ySmartedit())(base);
