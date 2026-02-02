/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { AbAnalyticsService } from 'cloudinarymediab2bsmarteditcommons/services/AbAnalyticsService';

describe('AbAnalyticsService', () => {
    let service: AbAnalyticsService;
    beforeEach(() => {
        service = new AbAnalyticsService();
    });

    it('should return an object defining AB analytics for a component', async () => {
        const actual = await service.getABAnalyticsForComponent(
            'ApparelUKHomepageSplashBannerComponent'
        );

        expect(actual).toEqual({
            aValue: 30,
            bValue: 70
        });
    });
});
