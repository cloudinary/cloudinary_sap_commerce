!function(e){var t={};function __webpack_require__(n){if(t[n])return t[n].exports;var o=t[n]={i:n,l:!1,exports:{}};return e[n].call(o.exports,o,o.exports,__webpack_require__),o.l=!0,o.exports}__webpack_require__.m=e,__webpack_require__.c=t,__webpack_require__.d=function(e,t,n){__webpack_require__.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:n})},__webpack_require__.r=function(e){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},__webpack_require__.t=function(e,t){if(1&t&&(e=__webpack_require__(e)),8&t)return e;if(4&t&&"object"==typeof e&&e&&e.__esModule)return e;var n=Object.create(null);if(__webpack_require__.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var o in e)__webpack_require__.d(n,o,function(t){return e[t]}.bind(null,o));return n},__webpack_require__.n=function(e){var t=e&&e.__esModule?function getDefault(){return e.default}:function getModuleExports(){return e};return __webpack_require__.d(t,"a",t),t},__webpack_require__.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},__webpack_require__.p="",__webpack_require__(__webpack_require__.s=14)}([function(e,t,n){e.exports=n(1)(0)},function(e,t){e.exports=vendor_chunk},function(e,t,n){e.exports=n(13)(1)},function(e,t,n){e.exports=n(1)(1)},function(e,t,n){e.exports=n(1)(32)},function(e,t,n){e.exports=n(1)(246)},function(e,t,n){e.exports=n(1)(244)},function(e,t,n){var o={"./services/abAnalyticsService.js":8,"./templates.js":9};function webpackContext(e){var t=webpackContextResolve(e);return n(t)}function webpackContextResolve(e){if(!n.o(o,e)){var t=new Error("Cannot find module '"+e+"'");throw t.code="MODULE_NOT_FOUND",t}return o[e]}webpackContext.keys=function webpackContextKeys(){return Object.keys(o)},webpackContext.resolve=webpackContextResolve,e.exports=webpackContext,webpackContext.id=7},function(e,t){angular.module("abAnalyticsServiceModule",[]).service("abAnalyticsService",["$q",function(e){this.getABAnalyticsForComponent=function(){return e.when({aValue:30,bValue:70})}}])},function(e,t){},function(e,t,n){var o={"./abAnalyticsToolbarItem/abAnalyticsToolbarItem.js":11,"./templates.js":12};function webpackContext(e){var t=webpackContextResolve(e);return n(t)}function webpackContextResolve(e){if(!n.o(o,e)){var t=new Error("Cannot find module '"+e+"'");throw t.code="MODULE_NOT_FOUND",t}return o[e]}webpackContext.keys=function webpackContextKeys(){return Object.keys(o)},webpackContext.resolve=webpackContextResolve,e.exports=webpackContext,webpackContext.id=10},function(e,t){angular.module("abAnalyticsToolbarItemModule",["cloudinarymediasmarteditContainerTemplates"]).component("abAnalyticsToolbarItem",{templateUrl:"abAnalyticsToolbarItemTemplate.html"})},function(e,t){angular.module("cloudinarymediasmarteditContainerTemplates",[]).run(["$templateCache",function(e){"use strict";e.put("web/features/cloudinarymediasmarteditContainer/abAnalyticsToolbarItem/abAnalyticsToolbarItemTemplate.html","<h2>AB Analytics</h2>\n<p>This is a dummy toolbar item used to demonstrate functionality.</p>\n"),e.put("web/features/cloudinarymediasmarteditContainer/abAnalyticsToolbarItem/abAnalyticsToolbarItemWrapperTemplate.html","<ab-analytics-toolbar-item></ab-analytics-toolbar-item>\n")}])},function(e,t){e.exports=smarteditcommons},function(e,t,n){"use strict";n.r(t),n.d(t,"CloudinarymediasmarteditContainer",function(){return a}),n.d(t,"CloudinarymediasmarteditContainerModule",function(){return _});var o=n(0);function importAll(e){e.keys().forEach(function(t){e(t)})}var r=n(2);!function doImport(){importAll(n(7)),importAll(n(10))}();var a=function(){function CloudinarymediasmarteditContainer(){}return CloudinarymediasmarteditContainer=o.__decorate([Object(r.SeModule)({imports:["smarteditServicesModule","abAnalyticsToolbarItemModule"],initialize:["featureService",function(e){"ngInject";e.addToolbarItem({toolbarId:"smartEditPerspectiveToolbar",key:"abAnalyticsToolbarItem",type:"HYBRID_ACTION",nameI18nKey:"ab.analytics.toolbar.item.name",priority:2,section:"left",iconClassName:"icon-message-information se-toolbar-menu-ddlb--button__icon",include:"abAnalyticsToolbarItemWrapperTemplate.html"})}]})],CloudinarymediasmarteditContainer)}(),i=n(4),u=n(5),c=n(3),l=n(6),s=function(){function DummyInterceptor(){}return DummyInterceptor.prototype.intercept=function(e,t){return console.log("DummyInterceptor - request url:",e.url),t.handle(e)},DummyInterceptor=o.__decorate([Object(c.Injectable)()],DummyInterceptor)}(),_=function(){function CloudinarymediasmarteditContainerModule(){}return CloudinarymediasmarteditContainerModule=o.__decorate([Object(r.SeEntryModule)("cloudinarymediasmarteditContainer"),Object(c.NgModule)({imports:[i.BrowserModule,u.UpgradeModule],declarations:[],entryComponents:[],providers:[{provide:l.HTTP_INTERCEPTORS,useClass:s,multi:!0}]})],CloudinarymediasmarteditContainerModule)}()}]);