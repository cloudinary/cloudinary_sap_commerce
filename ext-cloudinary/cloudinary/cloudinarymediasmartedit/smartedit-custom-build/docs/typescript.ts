/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * @ngdoc overview
 * @name Overview
 * @description
 *
 * # Overview of TypeScript in SmartEdit
 *
The modifications introduced with the cloudinarymediasmartedit template extension now make it possible for you to use TypeScript in your extension. But this doesn't mean that you have to switch to coding in TypeScript. You can continue to code in JavaScript. You can also start using TypeScript and keep your existing JavaScript code as is.

The existing SmartEdit framework is mostly written in JavaScript, now new features are developed in TypeScript. The SmartEdit build system can compile extensions that have both JavaScript and TypeScript in their source files.

## TypeScript documentation
For information about TypeScript, see the TypeScript documentation at https://www.typescriptlang.org

## TypeScript dependencies
All dependencies related to TypeScript support are defined in the /ysmartedimodule/package.json file (owned by the npmancillary extension). For SmartEdit, the following dependencies are particularly important:

- typescript
- tslint: TypeScript linting
- typescript-formatter: Used to format the TypeScript code
- webpack:  The module bundler
- karma-webpack: Webpack plugin for karma, which is used for unit testing.
- @types/xxx: Typing information for third-party libraries 

## TypeScript Linting:
For TypeScript code linting, the SmartEdit build system (Grunt) uses tslint; for more information, see https://palantir.github.io/tslint/.

For the linting configuration, you should be aware of the following:
- For IDE development, the tslint.json configuration file is stored at the root of the extension.
- Unless otherwise specified in a custom tslint grunt configuration file, the default "tslint.json" configuration file used when running "grunt tslint" is located in the SmartEdit bundle.
- The grunt tslint Grunt task must be successful in order for the build to be green.

## Project Configuration using the tsconfig.json File
Project configuration is done using the tsconfig.json file. For configuration, you should be aware of the following:
- For IDE development, the tsconfig.json configuration file is at the root of the extension.
- The folder smartedit-custom-build/generated contains different tsconfig configuration files. You can override any of the tsconfig files in the smartedit-custom-build/generated folder by defining your own grunt tasks. For more informations on how these files are generated, please refer to the {@link https://help.hybris.com/ SmartEdit Build documentation.}

The important properties in tsconfig.json:
- target:es5 - This means that the ECMAScript target version of the build is set to es5. Essentially, there is no impact on the end user since the target version before the introduction of TypeScript in SmartEdit was es5.
- "typeRoots" - This is the list of folders of type definitions from.
- "experimentalDecorators" - Support for annotations with TypeScript Decorators.

## Typings
The list of SmartEdit module declarations is:
- smartedit
- smarteditcontainer
- smarteditcommons

The SmartEdit declaration files are located in the SmartEdit build. There is one declaration file for each module. If your IDE is correctly configured to support TypeScript, you will be able to quickly leverage SmartEdit types in your code. Because the SmartEdit framework is not completely converted to TypeScript, you will not be able to view all the framework APIs in the declaration files. The following excerpt shows an example of how to to import an interface from the "smarteditcommons" module:

<em>cloudinarymediasmartedit/web/features/cloudinarymediasmarteditContainer/cloudinarymediasmarteditcontainermodule.ts</em>
```ts
import {IFeatureService, SeModule} from 'smarteditcommons';
@SeModule({
	imports: [
		'smarteditServicesModule',
		'abAnalyticsToolbarItemModule'
	],
	initialize: (featureService: IFeatureService) => {
		'ngInject';
		////////////////////////////////////////////////////
		// Create Toolbar Item
		////////////////////////////////////////////////////
		// Create the toolbar item as a feature.
		featureService.addToolbarItem({
			toolbarId: 'smartEditPerspectiveToolbar',
			key: 'abAnalyticsToolbarItem',
			type: 'HYBRID_ACTION',
			nameI18nKey: 'ab.analytics.toolbar.item.name',
			priority: 2,
			section: 'left',
			iconClassName: 'hyicon hyicon-info se-toolbar-menu-ddlb--button__icon',
			include: 'abAnalyticsToolbarItemWrapperTemplate.html'
		});
	}
})
export class CloudinarymediasmarteditContainer {}
```

For third-party typings, you have access to @types that are defined in node_modules/@types/package.json.
The following code excerpt shows an example of to use Angular typing in a TypeScript class:
```ts
import * as angular from 'angular';
export class ExampleService {
	constructor(
        private $log: angular.ILogService,
		private $q: angular.IQService) {}
    
	enable(): angular.IPromise<void> {
        this.$log.debug("ExampleService::enable");
		return this.$q.when();
    }
}
```

## Adding Dependency Injection Annotations
The SmartEdit build uses ng-annotate to add AngularJS dependency injection annotations. How you annotate depends on what you are injecting your annotations into:

Use the @SeInjectable() annotation in your services. This annotation will automatically add the ng-annotate @ngInject string.

```ts
import {SeInjectable} from 'smarteditcommons';
@SeInjectable()
export class ExampleService {
    constructor(
		private $q: angular.IQService,
		private $log: angular.ILogService,
		private lodash: lo.LoDashStatic,
		private siteService: SiteService) {}
}
```

## Project Bundling using Webpack

SmartEdit uses Webpack, a module bundler, to bundle the application. The TypeScript source code is transpiled into JavaScript and the existing JavaScript code (ES5) is bundled as is. The following files are the Webpack configuration for the cloudinarymediasmartedit:
- <em>cloudinarymediasmartedit/smartedit-custom-build/generated/webpack.[dev/prod/karma].smartedit.config.js</em>
- <em>cloudinarymediasmartedit/smartedit-custom-build/generated/webpack.[dev/prod/karma].smarteditContainer.config</em>

The 'cloudinarymediasmartedit' layer of cloudinarymediasmartedit is a bundle of cloudinarymediasmartedit and cloudinarymediasmarteditcommons.
The 'cloudinarymediasmarteditContainer' layer of cloudinarymediasmartedit is a bundle of cloudinarymediasmarteditContainer and cloudinarymediasmarteditcommons.

The "entry" property in these Webpack configuration files defines the application entrypoints. The entrypoints tell Webpack to create dependency graphs starting at these entrypoints.
The Webpack Karma configuration files don't include the "entry" property, because these configuration files define their own list of files to be included when running unit tests. 

For more information, see the Webpack documentation at  https://webpack.js.org/concepts/entry-points/.

## Leveraging TypeScript Development in your extension

If you want to use/inject Angular recipes (service, factory, constant, etc.) written in JavaScript (ES5) into TypeScript, set the type to 'any'.
Since there is no typing available in SmartEdit declaration files for non-migrated JavaScript code, 'any' is the recommended type in this case.

## Unit Testing

For unit testing, you can use the 'testhelpers' alias that exposes helpers.

Example of a unit test with TypeScript:

<em>ExampleServiceTest.ts</em>
```ts
import * as angular from 'angular';
import 'jasmine';
import {ExampleService} from 'myExtension';
import {domHelper, jQueryHelper, promiseHelper} from 'testhelpers';

describe('ExampleService test', () => {
    const $log = jasmine.createSpyObj('$log', ['warn']);
    const $q = promiseHelper.$q();

    let yjQuery: JQueryStatic;
    let exampleService: ExampleService;

    beforeEach(() => {
        exampleService = new ExampleService($log, $q);
        yjQuery = jQueryHelper.jQuery();
    });
    
    it('should be defined', function() {
		expect(exampleService).toBeDefined();
	});
});
```
 * 
 */
class DoNothing {}
