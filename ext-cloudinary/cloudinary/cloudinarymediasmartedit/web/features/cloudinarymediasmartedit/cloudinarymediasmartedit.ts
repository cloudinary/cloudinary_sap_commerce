/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { BrowserModule } from '@angular/platform-browser';
import { UpgradeModule } from '@angular/upgrade/static';
import { NgModule } from '@angular/core';
import { SeEntryModule } from 'smarteditcommons';

@SeEntryModule('cloudinarymediasmartedit')
@NgModule({
    imports: [BrowserModule, UpgradeModule],
    declarations: [],
    entryComponents: [],
    providers: []
})
export class CloudinarymediasmarteditModule {}
