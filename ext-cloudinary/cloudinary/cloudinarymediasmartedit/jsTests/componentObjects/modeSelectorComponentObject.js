/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = {
    PREVIEW_PERSPECTIVE: 'PREVIEW',
    ADVANCED_CMS_PERSPECTIVE: 'Advanced CMS',
    BASIC_CMS_PERSPECTIVE: 'Basic CMS',
    OVERLAY_SELECTOR: '#smarteditoverlay',

    select: function(perspectiveName) {
        return browser.waitUntilNoModal().then(
            function() {
                return browser
                    .switchToParent()
                    .then(
                        function() {
                            return browser
                                .findElement(by.css('.se-perspective-selector__btn'), true)
                                .getText()
                                .then(
                                    function(perspectiveSelected) {
                                        if (
                                            perspectiveSelected.toUpperCase() !==
                                            perspectiveName.toUpperCase()
                                        ) {
                                            browser.waitForWholeAppToBeReady();
                                            return browser
                                                .click(
                                                    browser.findElement(
                                                        by.css('.se-perspective-selector__btn')
                                                    ),
                                                    true
                                                )
                                                .then(
                                                    function() {
                                                        return browser
                                                            .click(
                                                                browser.findElement(
                                                                    by.cssContainingText(
                                                                        '.se-perspective__list-item',
                                                                        perspectiveName
                                                                    ),
                                                                    true
                                                                ),
                                                                'perspective ' +
                                                                    perspectiveName +
                                                                    ' is not clickable'
                                                            )
                                                            .then(
                                                                function() {
                                                                    return browser
                                                                        .waitForContainerToBeReady()
                                                                        .then(
                                                                            function() {
                                                                                return browser
                                                                                    .switchToIFrame()
                                                                                    .then(
                                                                                        function() {
                                                                                            return perspectiveName ===
                                                                                                this
                                                                                                    .PREVIEW_PERSPECTIVE
                                                                                                ? true
                                                                                                : browser.waitForVisibility(
                                                                                                      this
                                                                                                          .OVERLAY_SELECTOR
                                                                                                  );
                                                                                        }.bind(this)
                                                                                    );
                                                                            }.bind(this)
                                                                        );
                                                                }.bind(this)
                                                            );
                                                    }.bind(this)
                                                );
                                        } else {
                                            browser.waitForWholeAppToBeReady();
                                            return browser.switchToIFrame().then(
                                                function() {
                                                    return perspectiveName ===
                                                        this.PREVIEW_PERSPECTIVE
                                                        ? true
                                                        : browser.waitForVisibility(
                                                              this.OVERLAY_SELECTOR
                                                          );
                                                }.bind(this)
                                            );
                                        }
                                    }.bind(this)
                                );
                        }.bind(this)
                    )
                    .then(function() {
                        return browser.switchToParent();
                    });
            }.bind(this)
        );
    },

    selectPreviewPerspective: function() {
        return this.select(this.PREVIEW_PERSPECTIVE);
    },

    selectBasicPerspective: function() {
        return this.select(this.BASIC_CMS_PERSPECTIVE);
    },

    selectAdvancedPerspective: function() {
        return this.select(this.ADVANCED_CMS_PERSPECTIVE);
    }
};
