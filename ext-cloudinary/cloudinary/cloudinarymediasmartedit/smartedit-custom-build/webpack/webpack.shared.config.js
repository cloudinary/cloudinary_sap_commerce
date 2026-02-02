/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { resolve } = require('path');

const {
    group,
    webpack: { entry, alias }
} = require('../../smartedit-build/builders');

const commonsAlias = alias(
    'cloudinarymediasmarteditcommons',
    resolve('./jsTarget/web/features/cloudinarymediasmarteditcommons')
);

const smartedit = group(
    commonsAlias,
    alias('cloudinarymediasmartedit', resolve('./jsTarget/web/features/cloudinarymediasmartedit'))
);
const smarteditContainer = group(
    commonsAlias,
    alias('cloudinarymediasmarteditcontainer', resolve('./jsTarget/web/features/cloudinarymediasmarteditContainer'))
);

module.exports = {
    ySmarteditKarma: () => group(smartedit),
    ySmarteditContainerKarma: () => group(smarteditContainer),
    ySmartedit: () =>
        group(
            smartedit,
            entry({
                cloudinarymediasmartedit: resolve('./jsTarget/web/features/cloudinarymediasmartedit/index.ts')
            })
        ),
    ySmarteditContainer: () =>
        group(
            smarteditContainer,
            entry({
                cloudinarymediasmarteditContainer: resolve(
                    './jsTarget/web/features/cloudinarymediasmarteditContainer/index.ts'
                )
            })
        )
};
