/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
angular.module('abAnalyticsServiceModule', []).service('abAnalyticsService', function($q) {
    /**
     * Returns the AB analytics for a specific component by ID. Asynchronous and
     * promise based to mimic a REST transaction.
     * @returns {Promise} A promise that resolves to the AB analytics for the component
     */
    this.getABAnalyticsForComponent = function() {
        return $q.when({
            aValue: 30,
            bValue: 70
        });
    };
});
