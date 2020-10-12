/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
// @ts-ignore
function importAll(requireContext: any) {
    requireContext.keys().forEach(function(key: string) {
        requireContext(key);
    });
}
importAll(require.context('./features', true, /Test\.(js|ts)$/));
importAll(
    require.context(
        '../../../../jsTarget/web/features/cloudinarymediasmarteditcommons',
        true,
        /Module\.ts$/
    )
);
importAll(
    require.context(
        '../../../../jsTarget/web/features/cloudinarymediasmarteditContainer',
        true,
        /Module\.ts$/
    )
);
