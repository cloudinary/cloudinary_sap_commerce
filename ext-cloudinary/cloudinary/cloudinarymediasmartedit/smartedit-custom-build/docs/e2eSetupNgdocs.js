/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc overview
 * @name Overview
 * @description
 *
 * # Overview of SmartEdit E2E testing
 *
 * This extension has been generated from cloudinarymediasmartedit, and comes with a sample e2e setup and tests that execute on
 * protractor, against a 'real' SmartEdit application.
 *
 * The following guide will explain all the different pieces of this setup.
 *
 * WIP: - lets keep this as our general list of topics until we're done then we can create the official link
 * ```
 * - Technology (protractor, jasmine)
 * - Directory structure
 * - Test Bootstrapping
 * - Test debugging and manual e2e checking
 * - Component and Page Objects
 * - Bundle component/page objects
 * - Generating storefront (html)
 * - Configuring dummy storefront (layout/delay/render strategy)
 * - Generating Smartedit (html)
 * - Running the e2e grunt task (configuring files?)
 * - e2e_dev task
 * - CORS
 *```
 *
 *
 * # Technologies
 *
 * ## Grunt
 * Like the rest of the SmartEdit build/test tools, we use Grunt to launch our e2e tests. There are different grunt
 * tasks and configurations that can be used. See the *Running e2e tests* section.
 *
 * ## Selenium+Protractor
 * When you run one of the grunt e2e tasks, SmartEdit is using the grunt-protrator-runner Grunt plugin, configured
 * through our builder config. Protractor is an AngularJs test runner that runs on top of Selenium.
 *
 * ## Jasmine
 * You can configure different test frameworks in the protractor configuration, but we use jasmine in the OOTB setup.
 *
 * ## Mocks
 * For mocking, we use jasmine spies, and HttpBackendService for mocking API calls
 *
 *
 * <hr>
 * # File/Directory Structure
 *
 * ## Tests
 * ```
 * /jsTests/e2e/<myFeature>/.*Test.js
 * ```
 *
 * ## Test Config
 * ```
 * /jsTests/e2e/<myFeature>/config.json
 * ```
 *
 * ## Component Objects
 * ```
 * /jsTests/e2e/common/componentObjects/.*ComponentObject.js
 * ```
 *
 * ## Page Objects
 * ```
 * /jsTests/e2e/common/pageObjects/.*PageObject.js
 * ```
 *
 * <hr>
 * # Generating the SmartEdit html
 *
 * // TODO ...
 *
 *
 * <hr>
 * # Generating a Storefront to test against
 *
 * // TODO ...
 *
 *
 * <hr>
 * # Bootstrapping your test JS files
 *
 * To test your feature in a running SmartEdit application, your feature needs to be added to SmartEdit somehow, or
 * even some additional test modules sometimes. To add your extension's angular modules to SmartEdit, typically you
 * would configure it through impex, pointing the *smartEditContainerLocation* and *smartEditLocation* at your served
 * js files. By default, e2e tests run against a basic SmartEdit application with no extra modules.
 *
 * To solve this problem, the test bootstrapper was created. The test bootstrapper allows you to specify, per test
 * suite, a SmartEdit configuration to use at runtime. The bootstrapper will load the files and open the SmartEdit page.
 * To add the files, first you must list the files you want for either the smartedit or smartedContainer in a
 * config.json file in your test feature directory. Then, in your test you call the *window.bootstrap* function and pass
 * the directory of the config.json file. Typically you do this in a beforeEach() and use the node defined __dirname
 * for the directory.
 *
 * The window.bootstrap function is defined in the onPrepare of the protractor conf and has 2 parameters:
 * - dir: optional directory location of a config.json test configuration
 * - done: optional done function that will be executed when the SmartEdit app is loaded.
 *
 * If no directory is passed, it will simply load SmartEdit with default configuration.
 *
 * Example of loading a smarteditContainer angular module for a test:
 *
 * <em>jsTests/e2e/x/config.json</em>
 * ```
 * {
 *   "jsFiles": [{
 *       "value": "{\"smartEditContainerLocation\":\"jsTests/e2e/x/someFile.js\"}",
 *       "key": "applications.xTest"
 *   }]
 * }
 * ```
 *
 * <em>jsTests/e2e/x/xTest.js</em>
 * ```
 * ...
 * beforeEach(function() {
 *     browser.bootstrap(__dirname);
 *
 *     // test something from someFile.js
 *     ...
 * });
 * ...
 * ```
 *
 * The most common use of bootstrapping extra JS files is for tests using customView.
 *
 * ## Custom View
 *
 * A *custom view* test is a test that executes on a special route in SmartEdit only used for testing. This is where
 * you might test a feature in the SmartEdit environment, but without a real production implementation of it. So you can
 * provide your own html page that will be ran in smarteditContainer, with all the SmartEdit angular modules and DI, but
 * you can define your own test html.
 *
 * It is always recommended to use the real running app where possible, but some use cases might only be testable in
 * a custom setup. An example of this might be where you create an angular component that has a switch (A vs B) but in
 * production only A is currently being used. So you might created a custom view with the B scenario as well.
 *
 * To create a custom view for a specific test, you need 3 pieces
 * - A custom view configuration in the bootstrapped config.json file
 * - A customView.html file to load for the test
 * - A customViewController.js file with a constant PATH_TO_CUSTOM_VIEW pointing to the html file above.
 *
 * Note the angular controller will be exposed to the html as *controller*.
 *
 * Example:
 *
 *
 * <em>jsTests/e2e/x/config.json</em>
 * ```json
 * {
 *    "jsFiles": [{
 *       "value": "{\"smartEditContainerLocation\":\"/jsTests/e2e/x/customViewController.js\"}",
 *       "key": "applications.customViewModule"
 *    }]
 * }
 * ```
 *
 *
 * <em>jsTests/e2e/x/x.html</em>
 * ```html
 * <h2>Testing some feature x</h2>
 * <x attr='controller.value' />
 * ```
 *
 *
 * <em>jsTests/e2e/x/customViewController.js</em>
 * ```js
 * angular.module('customViewModule', [])
 *     .constant('PATH_TO_CUSTOM_VIEW', 'x/x.html')
 *     .controller('customViewController', function() {
 *          this.value = 'some value for the component x in the view';
 *     });
 * angular.module('smarteditcontainer').requires.push('customViewModule');
 * ```
 *
 *
 * <em>jsTests/e2e/x/xTests.js</em>
 * ```js
 * describe('my test', function() {
 *    beforeEach(function() {
 *       browser.bootstrap(__dirname);
 *    });
 * });
 * ```
 *
 * <hr>
 * # Common configuration
 *
 * We saw how to setup test-specific configuration in the previous section on bootstrapping tests. There is also a way
 * to load a common configuration to be used for all tests. This where you would typically load your extension's
 * served js files. When the e2e-generated SmartEdit is bootstrapping, it looks for the existence of a constant call
 * *CONFIGURATION_MOCKS* in a *configurationMocksModule*. This is an array of key/value configuration that gets appended
 * to the SmartEdit configuration before bootstrapping.
 *
 * The easiest way to get this into all tests, is to add it to the generated Smartedit html.
 * To do this, add a script that points to your configuration mocks js file in the *headerContent* section of the
 * generateSmarteditIndexHtml task configuration.
 *
 * Example:
 *
 * <em>jsTests/e2e/common/configurationMocks.js</em>
 * ```js
 * angular
 *  .module('configurationMocksModule', [])
 *  .constant('CONFIGURATION_MOCKS', [{
 *      value: "{\"smartEditLocation\":\"/web/webroot/<myext>>/js/myext.js\"}",
 *      key: "applications.myext"
 *   }]);
 * ```
 *
 * <em>smartedit-custom-build/config/generateSmarteditIndexHtml.js</em>
 * ```js
 * module.exports = function() {
 *   return {
 *        targets: [ 'e2e' ],
 *        config: function(data, conf) {
 *            conf.e2e = {
 *              headerContent: '<script src="jsTests/e2e/common/configurationMocks.js"></script>'
 *              ...
 * ```
 *
 * This will generate the SmartEdit index html to include your configuraiton mocks module so that when the angular
 * application bootstraps, it will see the constant and update the configuration before bootstrapping.
 *
 *
 *
 *
 * <hr>
 * # Test debugging and manual e2e checking
 *
 * // TODO ...
 *
 *
 * <hr>
 * # Access to Component/Page Objects in Tests
 * As part of the onPrepare() function of the protractor-conf, SmartEdit loads all page objects and component objects
 * into memory under the *e2e* namespace. It also strips the tailing "PageObject.js" or "ComponentObject.js" from the
 * name.
 *
 * Example:
 *
 * <em>jsTests/e2e/pageObjects/loginPageObject.js</em>
 * ```js
 * module.exports = {
 *   goToLoginPage: () => {
 *      browser.get('.../login.html');
 *   },
 *   // ...
 * };
 * ```
 *
 * <em>jsTests/e2e/tests/loginTest.js</em>
 * ```js
 * var page = e2e.login;   // <-- Accessing jsTests/e2e/pageObjects/loginPageObject.js
 *
 * describe('loginTest', function() {
 *  beforeEach(function() {
 *      page.goToLoginPage();
 *  });
 *  // ...
 * });
 * ```
 *
 * <hr>
 * # Running e2e Tests
 *
 * You can run the e2e tests from command line with
 * ```sh
 * grunt e2e
 * ```
 *
 * This will run with standard out of the box configuration, and run as a single thread in headless mode.
 * The default setup run in a CROSS origin setup by default (by using different ports for the parent frame and the storefront frame).
 *
 * To run e2e in the browser (Chrome), run with:
 * ```sh
 * grunt e2e_debug
 * ```
 *
 * To execute the e2e tests in multi thread, run with:
 * ```sh
 * grunt e2e_max
 * ```
 *
 * To troubleshoot e2e tests, run with:
 * ```sh
 * grunt e2e_dev
 * ```
 * This will open a new Chrome window. Navigate to your test file to run your e2e test.
 *
 */
function doNothing() {}
