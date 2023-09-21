+!function(){"use strict";var o;function a(){}function t(){t.init.call(this)}function s(e){return void 0===e._maxListeners?t.defaultMaxListeners:e._maxListeners}function n(e,t,n,r){var i,o;if("function"!=typeof n)throw new TypeError('"listener" argument must be a function');return(i=e._events)?(i.newListener&&(e.emit("newListener",t,n.listener||n),i=e._events),o=i[t]):(i=e._events=new a,e._eventsCount=0),o?("function"==typeof o?o=i[t]=r?[n,o]:[o,n]:r?o.unshift(n):o.push(n),o.warned||(r=s(e))&&0<r&&o.length>r&&(o.warned=!0,(r=new Error("Possible EventEmitter memory leak detected. "+o.length+" "+t+" listeners added. Use emitter.setMaxListeners() to increase limit")).name="MaxListenersExceededWarning",r.emitter=e,r.type=t,r.count=o.length,r=r,"function"==typeof console.warn?console.warn(r):console.log(r))):(o=i[t]=n,++e._eventsCount),e}function r(e,t,n){var r=!1;function i(){e.removeListener(t,i),r||(r=!0,n.apply(e,arguments))}return i.listener=n,i}function i(e){var t=this._events;if(t){e=t[e];if("function"==typeof e)return 1;if(e)return e.length}return 0}function d(e,t){for(var n=new Array(t);t--;)n[t]=e[t];return n}a.prototype=Object.create(null),(t.EventEmitter=t).usingDomains=!1,t.prototype.domain=void 0,t.prototype._events=void 0,t.prototype._maxListeners=void 0,t.defaultMaxListeners=10,t.init=function(){this.domain=null,t.usingDomains&&(void 0).active,this._events&&this._events!==Object.getPrototypeOf(this)._events||(this._events=new a,this._eventsCount=0),this._maxListeners=this._maxListeners||void 0},t.prototype.setMaxListeners=function(e){if("number"!=typeof e||e<0||isNaN(e))throw new TypeError('"n" argument must be a positive number');return this._maxListeners=e,this},t.prototype.getMaxListeners=function(){return s(this)},t.prototype.emit=function(e){var t,n,r,i="error"===e,o=this._events;if(o)i=i&&null==o.error;else if(!i)return!1;if(s=this.domain,i){if(i=arguments[1],s)return(i=i||new Error('Uncaught, unspecified "error" event')).domainEmitter=this,i.domain=s,i.domainThrown=!1,s.emit("error",i),!1;if(i instanceof Error)throw i;var s=new Error('Uncaught, unspecified "error" event. ('+i+")");throw s.context=i,s}if(!(t=o[e]))return!1;var a,l="function"==typeof t;switch(a=arguments.length){case 1:!function(e,t,n){if(t)e.call(n);else for(var r=e.length,i=d(e,r),o=0;o<r;++o)i[o].call(n)}(t,l,this);break;case 2:!function(e,t,n,r){if(t)e.call(n,r);else for(var i=e.length,o=d(e,i),s=0;s<i;++s)o[s].call(n,r)}(t,l,this,arguments[1]);break;case 3:!function(e,t,n,r,i){if(t)e.call(n,r,i);else for(var o=e.length,s=d(e,o),a=0;a<o;++a)s[a].call(n,r,i)}(t,l,this,arguments[1],arguments[2]);break;case 4:!function(e,t,n,r,i,o){if(t)e.call(n,r,i,o);else for(var s=e.length,a=d(e,s),l=0;l<s;++l)a[l].call(n,r,i,o)}(t,l,this,arguments[1],arguments[2],arguments[3]);break;default:for(n=new Array(a-1),r=1;r<a;r++)n[r-1]=arguments[r];!function(e,t,n,r){if(t)e.apply(n,r);else for(var i=e.length,o=d(e,i),s=0;s<i;++s)o[s].apply(n,r)}(t,l,this,n)}return!0},t.prototype.addListener=function(e,t){return n(this,e,t,!1)},t.prototype.on=t.prototype.addListener,t.prototype.prependListener=function(e,t){return n(this,e,t,!0)},t.prototype.once=function(e,t){if("function"!=typeof t)throw new TypeError('"listener" argument must be a function');return this.on(e,r(this,e,t)),this},t.prototype.prependOnceListener=function(e,t){if("function"!=typeof t)throw new TypeError('"listener" argument must be a function');return this.prependListener(e,r(this,e,t)),this},t.prototype.removeListener=function(e,t){var n,r,i,o,s;if("function"!=typeof t)throw new TypeError('"listener" argument must be a function');if(!(r=this._events))return this;if(!(n=r[e]))return this;if(n===t||n.listener&&n.listener===t)0==--this._eventsCount?this._events=new a:(delete r[e],r.removeListener&&this.emit("removeListener",e,n.listener||t));else if("function"!=typeof n){for(i=-1,o=n.length;0<o--;)if(n[o]===t||n[o].listener&&n[o].listener===t){s=n[o].listener,i=o;break}if(i<0)return this;if(1===n.length){if(n[0]=void 0,0==--this._eventsCount)return this._events=new a,this;delete r[e]}else!function(e,t){for(var n=t,r=n+1,i=e.length;r<i;n+=1,r+=1)e[n]=e[r];e.pop()}(n,i);r.removeListener&&this.emit("removeListener",e,s||t)}return this},t.prototype.removeAllListeners=function(e){var t,n=this._events;if(!n)return this;if(!n.removeListener)return 0===arguments.length?(this._events=new a,this._eventsCount=0):n[e]&&(0==--this._eventsCount?this._events=new a:delete n[e]),this;if(0===arguments.length){for(var r,i=Object.keys(n),o=0;o<i.length;++o)"removeListener"!==(r=i[o])&&this.removeAllListeners(r);return this.removeAllListeners("removeListener"),this._events=new a,this._eventsCount=0,this}if("function"==typeof(t=n[e]))this.removeListener(e,t);else if(t)for(;this.removeListener(e,t[t.length-1]),t[0];);return this},t.prototype.listeners=function(e){var t=this._events,n=t&&(n=t[e])?"function"==typeof n?[n.listener||n]:function(e){for(var t=new Array(e.length),n=0;n<t.length;++n)t[n]=e[n].listener||e[n];return t}(n):[];return n},t.listenerCount=function(e,t){return"function"==typeof e.listenerCount?e.listenerCount(t):i.call(e,t)},t.prototype.listenerCount=i,t.prototype.eventNames=function(){return 0<this._eventsCount?Reflect.ownKeys(this._events):[]};var l=new Uint8Array(16);var u=/^(?:[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}|00000000-0000-0000-0000-000000000000)$/i;for(var c=[],e=0;e<256;++e)c.push((e+256).toString(16).substr(1));function p(e,t){t=1<arguments.length&&void 0!==t?t:0,e=(c[e[t+0]]+c[e[t+1]]+c[e[t+2]]+c[e[t+3]]+"-"+c[e[t+4]]+c[e[t+5]]+"-"+c[e[t+6]]+c[e[t+7]]+"-"+c[e[t+8]]+c[e[t+9]]+"-"+c[e[t+10]]+c[e[t+11]]+c[e[t+12]]+c[e[t+13]]+c[e[t+14]]+c[e[t+15]]).toLowerCase();if("string"!=typeof(t=e)||!u.test(t))throw TypeError("Stringified UUID is invalid");return e}function f(e,t,n){var r=(e=e||{}).random||(e.rng||function(){if(!o&&!(o="undefined"!=typeof crypto&&crypto.getRandomValues&&crypto.getRandomValues.bind(crypto)||"undefined"!=typeof msCrypto&&"function"==typeof msCrypto.getRandomValues&&msCrypto.getRandomValues.bind(msCrypto)))throw new Error("crypto.getRandomValues() not supported. See https://github.com/uuidjs/uuid#getrandomvalues-not-supported");return o(l)})();if(r[6]=15&r[6]|64,r[8]=63&r[8]|128,t){n=n||0;for(var i=0;i<16;++i)t[n+i]=r[i];return t}return p(r)}const h=self.console,m=Object.freeze({NONE:0,ERROR:1,WARN:2,INFO:3,LOG:4}),y=["error","warn","info","log"],v=void 0!==h&&void 0!==h.log&&void 0!==h.error&&void 0!==h.debug&&void 0!==h.warn&&"function"==typeof Function.prototype.apply;let g,w;const b=(e,t,n,r)=>h[t]?n?h[t](n):h[t]():e.log(`----------- ${n||r} ----------- `),_=e=>{let t=e.level;const n={setLevel:e=>(t=e,n),getLevel:()=>t||g};return y.forEach(t=>{n[t]=(...e)=>((e,t,n)=>{if(v){var r=y.indexOf(t),i=e.getLevel();return~r&&r+1<=i&&h[t].apply(h,n),e}})(n,t,e)}),n.groupCollapsed=e=>b(n,"groupCollapsed",e,"GROUP START"),n.group=e=>b(n,"group",e,"GROUP START"),n.groupEnd=()=>b(n,"groupEnd",null,"GROUP END"),n.debug=n.log,n};var L=(e={})=>{e.level=e.level||m.NONE;var t=e.newInstance||!w?_(e):w;return w||e.newInstance||(w=t),t};const E={init:"ML_WIDGET_INIT",show:"ML_WIDGET_SHOW",hide:"ML_WIDGET_HIDE",error:"ML_WIDGET_ERROR",insert:"ML_WIDGET_INSERT_DATA",delete:"ML_WIDGET_DELETE_DATA",identity:"ML_WIDGET_EXPOSE_IDENTITY",upload:"ML_WIDGET_ASSET_UPLOAD"},x=["cloud_name","api_key","username","timestamp","signature","integration","use_saml"],A=["access_token","redirect_url","cloud_name"],D=["remove_header"],O=["integration","inline_container","z_index","multiple","max_files","default_transformations","insert_caption","remove_header","folder","search","collection","asset","transformation","sandboxNotAllowedAttributes"],I=["allow-forms","allow-modals","allow-orientation-lock","allow-pointer-lock","allow-popups","allow-popups-to-escape-sandbox","allow-presentation","allow-same-origin","allow-scripts","allow-top-navigation","allow-top-navigation-by-user-activation"],k=()=>{},C=e=>2<e.split(".").length?e.replace("//","//console-"):e.replace("//","//console."),T=L(),M=(e,t)=>{let n=null;try{n="string"==typeof e?JSON.parse(e):e}catch(e){T.error(`[postmessage]: failed to parse data from ${t}`,e)}return n};L();var H=o=>new Promise((e,t)=>{var n=!1;const r=((r,n)=>{let i,o=Array.isArray(n.allowedOrigin)?n.allowedOrigin:[n.allowedOrigin];const{types:s}=n,a=e=>{if(!e||!e.length)throw"PostMessage - target not set!"},e=e=>{var t;~o.indexOf(e.origin)&&(i=e.origin,(t=M(e.data,e.origin))&&(n.validator&&!n.validator(t.data)||t.type&&s[t.type]&&(T.log(`[postmessage]: found matching handler for '${t.type}' event from: ${e.origin}`,t.data),s[t.type](t.data,t.type,e,n))))};return a(o),self.addEventListener("message",e,!1),{send:(e,t,n={})=>{n=n.target||i;a(n);try{T.log(`[postmessage]: posting message to: ${n}`),(r=r instanceof HTMLIFrameElement?r.contentWindow:r).postMessage(JSON.stringify({type:e,data:t}),n),r.postMessage(JSON.stringify({type:e,data:t}),C(n))}catch(e){T.error(`[postmessage]: failed to post message to target: ${n}`,e)}},close:()=>self.removeEventListener("message",e)}})(o.ifr,{validator:e=>e&&e.mlId&&e.mlId===o.mlId,allowedOrigin:[o.mlUrl.origin,C(o.mlUrl.origin)],types:{[E.insert]:o.callbacks.insertHandler,[E.delete]:o.callbacks.deleteHandler,[E.identity]:o.callbacks.identityHandler,[E.hide]:()=>{o.hideCms()},[E.error]:o.callbacks.errorHandler,[E.upload]:o.callbacks.uploadHandler}}),i=(e,t)=>{r.send(e,t,{target:o.mlUrl.origin})};o.ifr.addEventListener("load",()=>{n||(n=!0,o.iframeLoaded(),e({sendMessage:i}))}),self.addEventListener("message",e=>{e=M(e),e=M(e.data);e&&"object"==typeof e&&"consoleLoaded"===e.type&&i(E.init,o)},!1),o.ifr.addEventListener("error",function(){})});class R extends t{constructor({options:o,callbacks:s={},element:a,mlId:l}){super(),this.initBackwardCompatibilityCallbacks(s);let d,u,c=null,t=!1,n=!1,p=!!o.inline_container,e=null,f=!!o.access_token;const r=e=>{27===e.keyCode&&this.hide()},i=(()=>{let e=o.new_navigation_experience?"console-":"",t;return!0===o.dev?t="dev.cloudinary.com":!0===o.nightly?t="nightly.cloudinary.com":!0===o.staging?t="staging.cloudinary.com":!0===o.staging2?t="staging2.cloudinary.com":o.eod?t=o.eod+".cloudinary.com":(t="cloudinary.com",e=o.new_navigation_experience?"console.":""),"https://"+e+t})(),h=(e,o,t,n={})=>{const r=(()=>{const i=[];return Object.keys(n).forEach(e=>i.push(`${e}=${n[e]}`)),t.filter(e=>Boolean(o[e])).forEach(t=>{var n=o[t];if("[object Object]"===Object.prototype.toString.call(n)){let e=[];for(var r of Object.keys(n))e.push(`${r}:${encodeURIComponent(n[r])}`);i.push(`${t}=${e.join("|")}`)}else i.push(`${t}=${encodeURIComponent(n)}`)}),i})();e=i+e+"?"+r.join("&");return{origin:i,href:e}},m=e=>{window.requestAnimationFrame(()=>{u.style.padding=e.matches?"25px":"25px 0"})},y=e=>{var t=p?d:u;const n=(e=>{let t=document.body;if(p&&(t=e.inline_container,"string"==typeof t&&(t=document.querySelector(t))),!t)throw"Element Not Found ("+e.inline_container+")";return t})(e);n.appendChild(t),d.focus()},v=()=>{const e=p?d:u;n&&t?(e.style.visibility="visible",e.focus(),p||document.addEventListener("keyup",r)):(e.style.visibility="hidden",document.removeEventListener("keyup",r))},g=()=>{n=!0,v()},w=()=>{!p&&document.body&&(null===e&&(e=document.body.style.overflow),document.body.style.overflow="hidden"),t=!0,v(),this.emitShowData()},b=()=>{!p&&document.body&&null!==e&&(document.body.style.overflow=e,e=null),t=!1,v(),this.emitHideData()};p&&s.insertHandler;(()=>{const e={...o};var t=e.sandboxAttributes,n=self.location;"null"===n.origin&&(n=new URL(self.origin));var r,n=h("/console/media_library/cms",e,[...x,...D],{pmHost:`${n.protocol}//${n.host}`,new_cms:!0,ml_id:l,...(i=t,Array.isArray(i)&&-1===i.indexOf("allow-popups")?{sandbox_no_popup:!0}:{}),...o.cld_console_version?{cld_console_version:o.cld_console_version}:{}}),i=(f?h("/console/api/v1/auth/login_with_oauth_token",{...o,redirect_url:n.href},A):n).href;e.mlUrl=n,e.callbacks=s,t&&(e.sandboxNotAllowedAttributes=(t=>{let e=[];return Array.isArray(t)&&(e=I.filter(e=>-1===t.indexOf(e))),e})(t)),a&&(e=>{let t=document.createElement("button");e.style.display="none",t.setAttribute("class",o.button_class||"cloudinary-button"),t.innerHTML=o.button_caption||"Open Media Library",e.parentNode.insertBefore(t,e.previousSibling),t.addEventListener("click",e=>(w(),e&&e.preventDefault&&e.preventDefault(),e&&e.stopPropagation&&e.stopPropagation(),!1),!1)})("string"==typeof(t=a)?document.querySelector(t):t),(e=>{if(d=document.createElement("iframe"),d.setAttribute("src",e),d.setAttribute("frameborder","no"),d.setAttribute("allow","camera"),p?(d.setAttribute("width","100%"),d.setAttribute("height","100%"),d.style.border="none"):(d.setAttribute("width","100%"),d.setAttribute("height","100%"),d.style.boxShadow="0 0 50px rgba(0, 0, 0, 0.8)"),!p){if(u=document.createElement("div"),u.style.position="fixed",u.style.top="0",u.style.left="0",u.style.height="100%",u.style.width="100%",u.style.boxSizing="border-box",u.style.backgroundColor="rgba(0,0,0,0.5)",u.style.zIndex=o.z_index||99999,matchMedia){const t=window.matchMedia("(min-width: 700px)");t.addListener(m),m(t)}u.style.visibility="hidden",u.appendChild(d)}})(i),c=H({ifr:d,mlId:l,mlUrl:n,callbacks:{uploadHandler:this.emitUploadData.bind(this),errorHandler:this.emitErrorData.bind(this),identityHandler:this.emitIdentityData.bind(this),deleteHandler:this.emitDeleteData.bind(this),insertHandler:e=>{this.emitInsertData(e),p||b()}},iframeLoaded:g,hideCms:b,config:(r=e,O.reduce((e,t)=>void 0!==r[t]?{...e,[t]:r[t]}:e,{}))}),y(e)})();this.show=(t={})=>(c.then(e=>{e.sendMessage(E.show,{mlId:l,options:{...t,config:t},config:t}),w()}),this),this.hide=()=>(c.then(e=>{e.sendMessage(E.hide,{mlId:l}),b()}),this)}emitShowData(){return this.emit("show")}emitHideData(){return this.emit("hide")}emitInsertData(e){return this.emit("insert",e)}emitIdentityData(e){return this.emit("identity",e)}emitErrorData(e){return this.emit("error",e)}emitUploadData(e){return this.emit("upload",e)}emitDeleteData(e){return this.emit("delete",e)}initBackwardCompatibilityCallbacks(e){var{showHandler:t=k,hideHandler:n=k,insertHandler:r=k,identityHandler:i=k,errorHandler:e=k}=e;this.on("show",t),this.on("hide",n),this.on("insert",r),this.on("error",e),this.on("identity",i)}}(e=>{var t;-1<e.location.search.indexOf("debug=true")&&(t=m.LOG,g=t);const r=(e,t,n)=>{var r=f();return new R({options:e,callbacks:t,element:n,mlId:r})};e.cloudinary=e.cloudinary||{},e.cloudinary.openMediaLibrary=(e,t,n)=>r(e,t,n).show(e),e.cloudinary.createMediaLibrary=(e,t,n)=>r(e,t,n)})(self)}();