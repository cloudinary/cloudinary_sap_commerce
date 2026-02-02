/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {
    IDecoratorService,
    IFeatureService,
    IPerspectiveService,
    moduleUtils,
    SeTranslationModule,
    SeEntryModule
} from 'smarteditcommons';
import { AbAnalyticsModule } from 'cloudinarymediab2bsmarteditcommons';
import { AbAnalyticsDecoratorModule } from './abAnalyticsDecorator';

@SeEntryModule('cloudinarymediab2bsmartedit')
@NgModule({
    imports: [
        BrowserModule,
        SeTranslationModule.forChild(),
        AbAnalyticsModule,
        AbAnalyticsDecoratorModule
    ],
    providers: [
        moduleUtils.bootstrap(
            (
                decoratorService: IDecoratorService,
                featureService: IFeatureService,
                perspectiveService: IPerspectiveService
            ) => {},
            [IDecoratorService, IFeatureService, IPerspectiveService]
        )
    ]
})
export class Cloudinarymediab2bsmarteditModule {}
