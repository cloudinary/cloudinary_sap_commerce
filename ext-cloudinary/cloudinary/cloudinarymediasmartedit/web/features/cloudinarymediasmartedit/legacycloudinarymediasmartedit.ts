/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { doImport } from './forcedImports';
doImport();
import { IFeatureService, IPerspectiveService, SeModule } from 'smarteditcommons';

/**
 * @ngdoc overview
 * @name cloudinarymediasmartedit
 * @description
 * Placeholder for documentation
 */
@SeModule({
    imports: [
        'smarteditServicesModule', // Feature API Module and Perspective API from SmartEdit Application
        'decoratorServiceModule', // Decorator API Module from SmartEdit Application
        'abAnalyticsDecoratorModule' // Decorators must be added as dependencies to be wired into SmartEdit
    ],
    initialize: (
        decoratorService: any,
        featureService: IFeatureService,
        perspectiveService: IPerspectiveService
    ) => {
        // Parameters are injected factory methods
        'ngInject';
        ////////////////////////////////////////////////////
        // Create Decorator
        ////////////////////////////////////////////////////

        // Use the decoratorService.addMappings() method to associate decorators
        // The keys may be given as strings or as regex
        decoratorService.addMappings({
            SimpleResponsiveBannerComponent: ['abAnalyticsDecorator'],
            CMSParagraphComponent: ['abAnalyticsDecorator']
        });

        // Register new decorators the the featureService
        // The key MUST be the same name as the directive
        featureService.addDecorator({
            key: 'abAnalyticsDecorator',
            nameI18nKey: 'ab.analytics.feature.name'
        });

        ////////////////////////////////////////////////////
        // Create  Perspective and assign features.
        ////////////////////////////////////////////////////
        // Group the features created above in a perspective. This will enable the features once the user selects this new perspective.
        perspectiveService.register({
            key: 'abAnalyticsPerspective',
            nameI18nKey: 'ab.analytics.perspective.name',
            descriptionI18nKey: 'ab.analytics.perspective.description',
            features: ['abAnalyticsToolbarItem', 'abAnalyticsDecorator'],
            perspectives: []
        });
    }
})
export class Cloudinarymediasmartedit {}
