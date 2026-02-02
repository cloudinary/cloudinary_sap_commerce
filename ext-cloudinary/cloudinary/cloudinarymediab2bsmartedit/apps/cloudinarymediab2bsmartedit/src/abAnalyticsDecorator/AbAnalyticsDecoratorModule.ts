/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TooltipModule } from 'smarteditcommons';
import { AbAnalyticsDecoratorComponent } from './AbAnalyticsDecoratorComponent';

@NgModule({
    imports: [CommonModule, TooltipModule],
    declarations: [AbAnalyticsDecoratorComponent],
    exports: [AbAnalyticsDecoratorComponent]
})
export class AbAnalyticsDecoratorModule {}
