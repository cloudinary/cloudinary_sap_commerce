/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = (function() {
    var componentObject = {};

    componentObject.constants = {
        // SITES
        APPAREL_SITE: 'Apparel Site UK',
        ELECTRONICS_SITE: 'Electronics',
        TOYS_SITE: 'Toys',
        ACTION_FIGURES_SITE: 'Action Figures',

        // Catalogs
        APPAREL_UK_CATALOG: 'Apparel UK Content Catalog',
        ELECTRONICS_CATALOG: 'Electronics Content Catalog',
        TOYS_CATALOG: 'Toys Content Catalog',
        ACTION_FIGURES_CATALOG: 'Action Figures Content Catalog',

        // Catalog Versions
        ACTIVE_CATALOG_VERSION: 'Online',
        STAGED_CATALOG_VERSION: 'Staged',

        // IDs
        TOYS_SITE_ID: 'toys',
        TOYS_CATALOG_ID: 'toysContentCatalog',
        ACTION_FIGURES_SITE_ID: 'action',
        ACTION_FIGURES_CATALOG_ID: 'actionFiguresContentCatalog'
    };

    componentObject.elements = {
        // Site Selector
        getSiteSelector: function() {
            return element(by.css('y-select[data-id="site"]'));
        },
        getSiteSelectorInput: function() {
            return this.getSiteSelector().element(by.css('item-printer .y-select-default-item'));
        },
        getSiteSelectorValue: function() {
            return this.getSiteSelectorInput().getText();
        },
        getSiteSelectorOptionByName: function(siteName) {
            return this.getSiteSelector().element(
                by.cssContainingText('.select2-result-single span', siteName)
            );
        },

        // Catalogs
        getCatalogsDisplayed: function() {
            return element.all(by.css('catalog-details'));
        },
        getNumberOfCatalogsDisplayed: function() {
            return this.getCatalogsDisplayed().count();
        },
        getCatalogByIndex: function(index) {
            return this.getCatalogsDisplayed().get(index);
        },
        getCatalogByName: function(catalogName) {
            var catalogByNameXPathSelector =
                "//catalog-details[.//*[contains(@class, 'se-catalog-details__header') and text()[contains(.,'" +
                catalogName +
                "')]]]";
            return element(by.xpath(catalogByNameXPathSelector));
        },
        getCatalogVersion: function(catalogName, catalogVersion) {
            var catalogVersionXPathSelector =
                "//catalog-version-details[.//*[contains(@class, 'se-catalog-version-container__name' ) and text()[contains(., '" +
                catalogVersion +
                "')]]]";

            return this.getCatalogByName(catalogName)
                .all(by.xpath(catalogVersionXPathSelector))
                .filter(function(item) {
                    return item.isDisplayed();
                })
                .first();
        },
        getCatalogContainerByName: function(catalogName) {
            return this.getCatalogByName(catalogName).element(
                by.css('.yCollapsibleContainer__group')
            );
        },
        getCatalogTitle: function(catalogName) {
            return this.getCatalogByName(catalogName).element(
                by.css('.yCollapsibleContainer__header')
            );
        },
        getCatalogThumbnail: function(catalogName) {
            return this.getCatalogByName(catalogName).element(
                by.css(
                    'catalog-versions-thumbnail-carousel .se-active-catalog-version-container__thumbnail'
                )
            );
        },
        getHomePageLink: function(catalogName, catalogVersion) {
            return this.getCatalogByName(catalogName)
                .element(by.cssContainingText('.se-catalog-version-container', catalogVersion))
                .element(by.css('home-page-link a'));
        },
        getCatalogVersionTemplateByName: function(catalogName, catalogVersion, item) {
            return this.getCatalogVersion(catalogName, catalogVersion).element(
                by.cssContainingText('div', item)
            );
        },

        // Others
        getBrowserUrl: function() {
            return browser.getCurrentUrl();
        }
    };

    componentObject.actions = {
        // Sites Selector
        openSiteSelector: function() {
            return browser.click(componentObject.elements.getSiteSelector());
        },
        selectSite: function(siteName) {
            return this.openSiteSelector().then(function() {
                return browser.click(
                    componentObject.elements.getSiteSelectorOptionByName(siteName)
                );
            });
        },

        // Catalogs
        clickOnCatalogHeader: function(catalogName) {
            return browser.click(componentObject.elements.getCatalogTitle(catalogName));
        },
        navigateToStorefrontViaThumbnail: function(catalogName) {
            return browser
                .click(componentObject.elements.getCatalogThumbnail(catalogName))
                .then(function() {
                    browser.waitForWholeAppToBeReady();
                    browser.waitForUrlToMatch(/\/storefront/);
                });
        },
        navigateToStorefrontViaHomePageLink: function(catalogName, catalogVersion) {
            return browser
                .click(componentObject.elements.getHomePageLink(catalogName, catalogVersion))
                .then(function() {
                    browser.waitForWholeAppToBeReady();
                    browser.waitForUrlToMatch(/\/storefront/);
                });
        },

        // Note: This is only meant to be used when clicking on a homePage link that don't redirect to another storefront.
        // (our tests only care about the URL being changed appropriately). Thus, it is not necessary to wait for the whole app to be ready.
        clickOnHomePageLink: function(catalogName, catalogVersion) {
            return browser
                .click(componentObject.elements.getHomePageLink(catalogName, catalogVersion))
                .then(function() {
                    browser.waitForUrlToMatch(/\/storefront/);
                });
        },
        clickOnParentCatalogHomePageLink: function(catalogName, catalogVersion) {
            return componentObject.actions.clickOnCatalogHeader(catalogName).then(function() {
                return componentObject.actions.clickOnHomePageLink(catalogName, catalogVersion);
            });
        }
    };

    componentObject.assertions = {
        // Site
        expectedSiteIsSelected: function(siteName) {
            expect(componentObject.elements.getSiteSelectorValue()).toBe(
                siteName,
                "Selected site doesn't match expected one."
            );
        },
        selectedSiteHasRightNumberOfCatalogs: function(expectedNumberOfCatalogs) {
            browser.waitUntil(function() {
                var actualNumberOfCatalogs = null;
                return componentObject.elements
                    .getNumberOfCatalogsDisplayed()
                    .then(function(_actualNumberOfCatalogs) {
                        actualNumberOfCatalogs = _actualNumberOfCatalogs;
                        return expectedNumberOfCatalogs === actualNumberOfCatalogs;
                    });
            }, 'Expected ' + expectedNumberOfCatalogs + ' catalogs for selected site., got ');
        },

        // Catalog
        catalogIsExpanded: function(catalogName) {
            expect(componentObject.utils.isCatalogExpanded(catalogName)).toBe(
                true,
                'Expected catalog to be expanded'
            );
        },
        catalogIsNotExpanded: function(catalogName) {
            expect(componentObject.utils.isCatalogExpanded(catalogName)).toBe(
                false,
                'Expected catalog to be expanded'
            );
        },
        catalogVersionContainsItem: function(catalogName, catalogVersion, item) {
            expect(
                componentObject.elements
                    .getCatalogVersionTemplateByName(catalogName, catalogVersion, item)
                    .isPresent()
            ).toBe(true, 'Expected template to be displayed in catalog version.');
        },

        // Other
        assertLandingPageIsDisplayed: function() {
            expect(componentObject.elements.getBrowserUrl()).not.toContain('/storefront');
        },
        assertStorefrontIsLoaded: function() {
            expect(componentObject.elements.getBrowserUrl()).toContain('/storefront');
        }
    };

    componentObject.utils = {
        isCatalogExpanded: function(catalogName) {
            return this.hasClass(
                componentObject.elements.getCatalogContainerByName(catalogName),
                'panel-open'
            );
        },
        hasClass: function(element, expectedClass) {
            return element.getAttribute('class').then(function(classes) {
                return classes.split(' ').indexOf(expectedClass) !== -1;
            });
        }
    };

    return componentObject;
})();
