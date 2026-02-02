/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import {
    SeEntryModule,
    SeTranslationModule,
    MEDIA_FILE_SELECTOR_CUSTOM_TOKEN,
    MEDIA_UPLOAD_FIELDS_CUSTOM_TOKEN,
    MEDIA_SELECTOR_I18N_KEY_TOKEN,
    MEDIA_SELECTOR_I18N_KEY,
    IMediaToUpload
} from 'smarteditcommons';
import { AbAnalyticsToolbarItemComponent } from './abAnalyticsToolbarItem';
import { CloudinaryMediaFileSelectorComponent } from './mediaFileSelector/components/CloudinaryMediaFileSelectorComponent';
import { CloudinaryMediaUploaderService } from './mediaFileSelector/services/CloudinaryMediaUploaderService';
import { MediaUtilService } from './mediaFileSelector/services/MediaUtilService';

@SeEntryModule('cloudinarymediab2bsmarteditContainer')
@NgModule({
    imports: [BrowserModule, FormsModule, SeTranslationModule.forChild(), CommonModule],
    declarations: [AbAnalyticsToolbarItemComponent, CloudinaryMediaFileSelectorComponent],
    providers: [
        {
            provide: IMediaToUpload,
            useClass: CloudinaryMediaUploaderService
        },
        {
            provide: MEDIA_FILE_SELECTOR_CUSTOM_TOKEN,
            useValue: {
                component: CloudinaryMediaFileSelectorComponent
            }
        },
        {
            provide: MEDIA_SELECTOR_I18N_KEY_TOKEN,
            useValue: {
                ...MEDIA_SELECTOR_I18N_KEY,
                UPLOAD: 'Pick Asset' // i18n translation key
            }
        },
        MediaUtilService
    ]
})
export class Cloudinarymediab2bsmarteditContainerModule {}
