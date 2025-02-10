/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint undef:false */
module.exports = (function() {
    var PageObject = {
        actions: {
            get: function(url) {
                browser.driver.manage().deleteAllCookies();
                return browser.get(url);
            },
            getAndWaitForWholeApp: function(url) {
                return this.get(url).then(function() {
                    return browser.waitForWholeAppToBeReady();
                });
            },
            getAndWaitForLogin: function(url) {
                return this.get(url).then(
                    function() {
                        return this.clearCookies().then(
                            function() {
                                browser.waitForAngularEnabled(false);
                                return this.waitForLoginModal();
                            }.bind(this)
                        );
                    }.bind(this)
                );
            },
            waitForLoginModal: function() {
                return browser
                    .wait(
                        protractor.ExpectedConditions.elementToBeClickable(
                            element(by.css('input[id^="username_"]'))
                        ),
                        20000,
                        'Timed out waiting for username input'
                    )
                    .then(function() {
                        return browser.waitForAngular();
                    });
            },
            setWaitForPresence: function(implicitWait) {
                return browser.driver
                    .manage()
                    .timeouts()
                    .implicitlyWait(implicitWait);
            },
            dumpAllLogsToConsole: function() {
                if (
                    global.waitForSprintDemoLogTime !== null &&
                    global.waitForSprintDemoLogTime > 0
                ) {
                    // List logs
                    var logs = browser.driver.manage().logs(),
                        logType = 'browser';
                    logs.getAvailableLogTypes().then(function(logTypes) {
                        if (logTypes.indexOf(logType) > -1) {
                            browser.driver
                                .manage()
                                .logs()
                                .get(logType)
                                .then(function(logsEntries) {
                                    var len = logsEntries.length;
                                    for (var i = 0; i < len; ++i) {
                                        var logEntry = logsEntries[i];
                                        var showLog = hasLogLevel(
                                            logEntry.level.name,
                                            global.sprintDemoLogLevels
                                        );
                                        if (showLog) {
                                            waitForSprintDemo(global.waitForSprintDemoLogTime);
                                            try {
                                                var msg = JSON.parse(logEntry.message);
                                                console.log(msg.message.text);
                                            } catch (err) {
                                                if (global.sprintDemoShowLogParsingErrors) {
                                                    console.log(
                                                        'Error parsing log:  ' + logEntry.message
                                                    );
                                                }
                                            }
                                        }
                                    }
                                }, null);
                        }
                    });
                }
            },
            clearCookies: function() {
                return browser.driver.wait(
                    function() {
                        return browser.driver
                            .manage()
                            .deleteAllCookies()
                            .then(
                                function() {
                                    return true;
                                },
                                function(err) {
                                    throw err;
                                }
                            );
                    },
                    5000,
                    'Timed out waiting for cookies to clear'
                );
            }
        },

        assertions: {},

        constants: {},

        elements: {}
    };

    return PageObject;
})();
