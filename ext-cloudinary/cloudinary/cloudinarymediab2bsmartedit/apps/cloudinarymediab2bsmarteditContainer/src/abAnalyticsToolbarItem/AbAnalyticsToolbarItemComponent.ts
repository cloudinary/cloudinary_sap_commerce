/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import { ToolbarItemInternal, TOOLBAR_ITEM } from 'smarteditcommons';

@Component({
    selector: 'ab-analytics-toolbar-item',
    templateUrl: './AbAnalyticsToolbarItemComponent.html',
    standalone: false
})
export class AbAnalyticsToolbarItemComponent {
    constructor(@Inject(TOOLBAR_ITEM) public toolbarItem: ToolbarItemInternal) {}
}
