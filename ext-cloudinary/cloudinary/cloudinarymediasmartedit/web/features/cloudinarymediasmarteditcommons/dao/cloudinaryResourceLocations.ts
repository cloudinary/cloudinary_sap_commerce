/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { diNameUtils, SeModule } from 'smarteditcommons';
import {
    CLOUDINARY_MEDIA_RESOURCE_URI,
    CONTEXT_CATALOG,
    CONTEXT_CATALOG_VERSION,
    CONTEXT_SITE_ID
} from './cloudinaryResourceLocationsConstants';

@SeModule({
    providers: [
        diNameUtils.makeValueProvider({ CLOUDINARY_MEDIA_RESOURCE_URI }),
        diNameUtils.makeValueProvider({ CONTEXT_SITE_ID }),
        diNameUtils.makeValueProvider({ CONTEXT_CATALOG }),
        diNameUtils.makeValueProvider({ CONTEXT_CATALOG_VERSION })
    ]
})
export class CmsCloudinaryResourceLocationsModule {}
