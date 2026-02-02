/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
describe('abAnalyticsDecoratorController', function() {
    var fixture;
    var controller;

    beforeEach(function() {
        fixture = AngularUnitTestHelper.prepareModule('abAnalyticsDecoratorControllerModule')
            .mock('abAnalyticsService', 'getABAnalyticsForComponent')
            .and.returnResolvedPromise({
                aValue: 30,
                bValue: 70
            })
            .controller('abAnalyticsDecoratorController');

        controller = fixture.controller;
        fixture.detectChanges();
    });

    it('should bind a title to the controller scope', function() {
        // Arrange

        // Act

        // Assert
        expect(controller.title).toBe('AB Analytics');
    });

    it('should bind an inner content template to the controller scope', function() {
        // Arrange

        // Act

        // Assert
        expect(controller.contentTemplate).toBe('abAnalyticsDecoratorContentTemplate.html');
    });

    it('should build a human readable AB analytics string on the controller scope', function() {
        // Arrange

        // Act
        controller.$onInit();
        fixture.detectChanges();

        // Assert
        expect(controller.abAnalytics).toBe('A: 30 B: 70');
    });
});
