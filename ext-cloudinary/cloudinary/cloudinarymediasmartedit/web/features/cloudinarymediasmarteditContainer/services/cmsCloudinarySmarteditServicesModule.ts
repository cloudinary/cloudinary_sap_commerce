/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SeModule } from 'smarteditcommons';

import { SeCloudinaryMediaService } from '../components/genericEditor/media/services/SeCloudinaryMediaService';

/**
 * @ngdoc overview
 * @name cmsCloudinarySmarteditServicesModule
 *
 * @description
 * Module containing all the services shared within the CmsSmartEdit application.
 */
@SeModule({
    imports: ['smarteditServicesModule'],
    providers: [
      SeCloudinaryMediaService
    ]
})
export class CmsCloudinarySmarteditServicesModule {}
