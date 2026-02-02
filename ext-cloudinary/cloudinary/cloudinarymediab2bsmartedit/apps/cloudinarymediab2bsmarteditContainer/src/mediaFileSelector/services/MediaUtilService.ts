/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { FILE_VALIDATION_CONFIG, GenericEditorMediaType } from 'smarteditcommons';

/**
 * This service provides functionality for Media Util.
 */
@Injectable()
export class MediaUtilService {
    /**
     * Limitation the specified file format against custom field and its allowMediaType.
     *
     * @param fieldAllowMediaType The file format to be validated.
     * @returns string[] list of accepted file types.
     */
    public getAcceptedFileTypes(fieldAllowMediaType: GenericEditorMediaType): string[] {
        const allowMediaType = fieldAllowMediaType || GenericEditorMediaType.DEFAULT;
        switch (allowMediaType) {
            case GenericEditorMediaType.IMAGE:
                return FILE_VALIDATION_CONFIG.ACCEPTED_FILE_TYPES.IMAGE;
            case GenericEditorMediaType.VIDEO:
                return FILE_VALIDATION_CONFIG.ACCEPTED_FILE_TYPES.VIDEO;
            case GenericEditorMediaType.PDF_DOCUMENT:
                return FILE_VALIDATION_CONFIG.ACCEPTED_FILE_TYPES.PDF_DOCUMENT;
            default:
                return FILE_VALIDATION_CONFIG.ACCEPTED_FILE_TYPES.DEFAULT;
        }
    }
}
