/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
angular
    .module('abAnalyticsDecoratorControllerModule', ['abAnalyticsServiceModule'])
    .controller('abAnalyticsDecoratorController', function(abAnalyticsService) {
        this.title = 'AB Analytics';
        this.contentTemplate = 'abAnalyticsDecoratorContentTemplate.html';

        this.$onInit = function() {
            abAnalyticsService.getABAnalyticsForComponent(this.smarteditComponentId).then(
                function(analytics) {
                    this.abAnalytics = 'A: ' + analytics.aValue + ' B: ' + analytics.bValue;
                }.bind(this)
            );
        };
    });
