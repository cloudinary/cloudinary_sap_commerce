/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'testhelpers';

function importAll(requireContext: any): void {
    requireContext.keys().forEach((key: string) => {
        requireContext(key);
    });
}
importAll(require.context('./features', true, /Test\.(js|ts)$/));
