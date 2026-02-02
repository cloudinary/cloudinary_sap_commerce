/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
describe('abAnalyticsService', function() {
    var service;

    beforeEach(function() {
        var fixture = AngularUnitTestHelper.prepareModule('abAnalyticsServiceModule').service(
            'abAnalyticsService'
        );
        service = fixture.service;
    });

    it('should return an object defining AB analytics for a component', function() {
        // Arrange

        // Act
        var promise = service.getABAnalyticsForComponent('anyComponentId');

        // Assert
        expect(promise).toBeResolvedWithData({
            aValue: 30,
            bValue: 70
        });
    });
});
