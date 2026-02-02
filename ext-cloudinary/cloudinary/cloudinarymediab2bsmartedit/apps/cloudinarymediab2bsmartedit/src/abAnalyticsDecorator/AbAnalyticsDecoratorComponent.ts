/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { AbstractDecorator } from 'smarteditcommons';
import { AbAnalyticsService } from 'cloudinarymediab2bsmarteditcommons';

@Component({
    selector: 'ab-analytics-decorator',
    templateUrl: './AbAnalyticsDecoratorComponent.html',
    standalone: false
})
export class AbAnalyticsDecoratorComponent extends AbstractDecorator implements OnInit {
    public abAnalytics: string;

    constructor(private abAnalyticsService: AbAnalyticsService) {
        super();
    }

    async ngOnInit(): Promise<void> {
        const { aValue, bValue } = await this.abAnalyticsService.getABAnalyticsForComponent(
            this.smarteditComponentId
        );
        this.abAnalytics = `A: ${aValue} B: ${bValue}`;
    }
}
