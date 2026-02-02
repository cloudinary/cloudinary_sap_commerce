/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = {
    STORE_FRONT_HOME_PAGE: 'storefront.html',

    // Elements
    mainLoginUsernameInput: function() {
        return element(by.name('username'));
    },
    mainLoginPasswordInput: function() {
        return element(by.name('password'));
    },
    mainLoginSubmitButton: function() {
        return element(by.name('submit'));
    },
    logoutButton: function() {
        return element(by.css('a.se-sign-out__link'));
    },
    languageSelectorDropdown: function() {
        return element(by.css('.se-login-language #uiSelectToolingLanguage'));
    },
    languageSelectorOptionByLanguage: function(language) {
        return this.languageSelectorDropdown().element(
            by.cssContainingText('.ui-select-choices-row', language)
        );
    },
    userAccountButton: function() {
        return element(by.css('[data-item-key="headerToolbar.userAccountTemplate"] button'));
    },
    // Actions
    logoutUser: function() {
        browser
            .switchToParent()
            .then(
                function() {
                    return browser.click(this.userAccountButton());
                }.bind(this)
            )
            .then(
                function() {
                    browser.waitUntil(
                        protractor.ExpectedConditions.elementToBeClickable(this.logoutButton()),
                        'Timed out waiting for logout button'
                    );
                    return browser.click(this.logoutButton());
                }.bind(this)
            );
    },

    loginAsUser: function(username, password) {
        return browser
            .waitUntil(
                protractor.ExpectedConditions.elementToBeClickable(this.mainLoginUsernameInput()),
                'Timed out waiting for username input'
            )
            .then(
                function() {
                    return this.mainLoginUsernameInput()
                        .sendKeys(username)
                        .then(
                            function() {
                                return this.mainLoginPasswordInput()
                                    .sendKeys(password)
                                    .then(
                                        function() {
                                            return browser.click(
                                                this.mainLoginSubmitButton(),
                                                'could no click on main login submit button'
                                            );
                                        }.bind(this)
                                    );
                            }.bind(this)
                        );
                }.bind(this)
            );
    },

    loginAsCmsManager: function() {
        return this.loginAsUser('cmsmanager', '1234').then(function() {
            return browser.waitForWholeAppToBeReady();
        });
    },

    loginAsAdmin: function() {
        return this.loginAsUser('admin', '1234').then(function() {
            return browser.waitForWholeAppToBeReady();
        });
    },

    loginAsCmsManagerToLandingPage: function() {
        this.loginAsUser('cmsmanager', '1234');
        browser.waitForContainerToBeReady();
    },

    toggleLanguageSelectorDropdown: function() {
        browser.click(this.languageSelectorDropdown());
    },

    waitForLanguageSelectorToBePopulated: function() {
        this.toggleLanguageSelectorDropdown();
        browser.waitToBeDisplayed(this.languageSelectorOptionByLanguage('English'));
        this.toggleLanguageSelectorDropdown();
    },

    // Assertions
    assertLanguageSelectorLanguage: function(language) {
        expect(this.languageSelectorDropdown().getText()).toBe(language);
    }
};
