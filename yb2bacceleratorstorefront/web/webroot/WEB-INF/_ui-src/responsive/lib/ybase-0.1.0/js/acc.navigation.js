var oDoc = document;

ACC.navigation = {

    _autoload: [
        "offcanvasNavigation",
        "myAccountNavigation",
        "orderToolsNavigation"
    ],

    js_userAccount_Links: ".js-userAccount-Links",
    js_enquire_offcanvas_navigation_and_ul_js_offcanvas_links: ".js-enquire-offcanvas-navigation ul.js-offcanvas-links",
    js_enquire_offcanvas_navigation_and_js_enquire_has_sub: ".js-enquire-offcanvas-navigation .js-enquire-has-sub",
    liOffcanvas_a: ".liOffcanvas a ",
    liOffcanvas_form: ".liOffcanvas form ",
    data_toggle: "data-toggle",
    js_secondaryNavAccount: ".js-secondaryNavAccount",
    js_secondaryNavCompany: ".js-secondaryNavCompany",
    data_trigger: "data-trigger",
    glyphicon_chevron_down: "glyphicon-chevron-down",
    glyphicon_chevron_up: "glyphicon-chevron-up",

    offcanvasNavigation: function(){

    	enquire.register("screen and (max-width:"+ ACC.common.encodeHtml(screenSmMax) +")", {

            match : function() {

                $(document).on("click",".js-enquire-offcanvas-navigation .js-enquire-has-sub .js_nav__link--drill__down",function(e){
                    e.preventDefault();
                    $(ACC.navigation.js_userAccount_Links).hide();
                    $(ACC.navigation.js_enquire_offcanvas_navigation_and_ul_js_offcanvas_links).addClass("active");
                    $(ACC.navigation.js_enquire_offcanvas_navigation_and_js_enquire_has_sub).removeClass("active");
                    $(this).parent(".js-enquire-has-sub").addClass("active");
                });


                $(document).on("click",".js-enquire-offcanvas-navigation .js-enquire-sub-close",function(e){
                    e.preventDefault();
                    $(ACC.navigation.js_userAccount_Links).show();
                    $(ACC.navigation.js_enquire_offcanvas_navigation_and_ul_js_offcanvas_links).removeClass("active");
                    $(ACC.navigation.js_enquire_offcanvas_navigation_and_js_enquire_has_sub).removeClass("active");
                });

            },

            unmatch : function() {

                $(ACC.navigation.js_userAccount_Links).show();
                $(ACC.navigation.js_enquire_offcanvas_navigation_and_ul_js_offcanvas_links).removeClass("active");
                $(ACC.navigation.js_enquire_offcanvas_navigation_and_js_enquire_has_sub).removeClass("active");

                $(document).off("click",".js-enquire-offcanvas-navigation .js-enquire-has-sub > a");
                $(document).off("click",".js-enquire-offcanvas-navigation .js-enquire-sub-close");


            }


        });

    },

    myAccountNavigation: function(){

        //copy the site logo
        $('.js-mobile-logo').html( $('.js-site-logo a').clone());

        //Add the order form img in the navigation
        $('.nav-form').append($("<span>").addClass("glyphicon glyphicon-list-alt"));


        var aAcctData = [];
        var sSignBtn = "";

        //my account items
        var oMyAccountData = $(".accNavComponent");

        //the my Account hook for the desktop
        var oMMainNavDesktop = $(".js-secondaryNavAccount > ul");

        //offcanvas menu for tablet/mobile
        var oMainNav = $(".navigation--bottom > ul.nav__links.nav__links--products");

        if(oMyAccountData){
            var aLinks = oMyAccountData.find("a");
            for(var i = 0; i < aLinks.length; i++){
                aAcctData.push({link: aLinks[i].href, text: aLinks[i].title});
            }
        }

        var navClose = $("<div>").addClass("close-nav")
		.append($("<button>").attr("type", "button")
				.addClass("js-toggle-sm-navigation btn")
				.append($("<span>").addClass("glyphicon glyphicon-remove")));

        //create mobile Sign In button
        if(ACC.navigation.createMobileSignInButton()) {
        	sSignBtn = $("<li>").addClass("auto liUserSign")
			.append($("<a>").addClass("userSign")
					.attr("href", $(ACC.navigation.liOffcanvas_a)[0].href)
					.text($(ACC.navigation.liOffcanvas_a)[0].innerHTML));
        }

        //create mobile Sign Out Button
        if(ACC.navigation.createMobileSignOutButton()) {
            var form = $(ACC.navigation.liOffcanvas_form).clone();
            var button = (form[0])[0];
            button.setAttribute("style", "padding-left:50px;color:#fff;font-weight:400;display:block;background-color:inherit");
            sSignBtn = $("<li>").addClass("auto liUserSign")
            .append(form);
        }

        //create Welcome User + expand/collapse and close button
        //This is for mobile navigation. Adding html and classes.
        var oUserInfo = $(".nav__right ul li.logged_in");
        //Check to see if user is logged in
        if(ACC.navigation.checkIfUserLoggedIn())
        {
        	var sUserBtn = $("<li>").addClass("auto")
			.append($("<div>")
					.addClass("userGroup")
					.append($("<span>")
							.addClass("glyphicon glyphicon-user myAcctUserIcon"))
							.append($("<div>")
									.addClass("userName")
									.html(oUserInfo[0].innerHTML)));

            if(aAcctData.length > 0){
            	$(sUserBtn).find(".userGroup").append($("<a>").addClass("collapsed js-nav-collapse")
        				.attr("id", "signedInUserOptionsToggle")
        				.attr(ACC.navigation.data_toggle, "collapse")
        				.attr("data-target", ".offcanvasGroup1")
        				.append($("<span>").addClass("glyphicon glyphicon-chevron-up myAcctExp")));
            }
            sUserBtn.append(navClose);

            $('.js-sticky-user-group').html(sUserBtn);

            $(ACC.navigation.js_userAccount_Links).append(sSignBtn);
            $(ACC.navigation.js_userAccount_Links).append($("<li>").addClass("auto").append($("<div>").addClass("myAccountLinksContainer js-myAccountLinksContainer")));


            //FOR DESKTOP
            var myAccountHook = $("<div>").addClass("myAccountLinksHeader js-myAccount-toggle")
			.attr(ACC.navigation.data_toggle, "collapse")
			.attr("data-parent", ".nav__right")
			.text(oMyAccountData.data("title"));

            myAccountHook.insertBefore(oMyAccountData);

            //*For toggling collapse myAccount on Desktop instead of with Bootstrap.js
            $('.myAccountLinksHeader').click(ACC.navigation.toggleCollapseMyAccount);

            //FOR MOBILE
            //create a My Account Top link for desktop - in case more components come then more parameters need to be passed from the backend
            var myAccountHook = $("<div>").addClass("sub-nav")
			.append($("<a>").attr("id", "signedInUserAccountToggle")
					.addClass("myAccountLinksHeader collapsed js-myAccount-toggle")
					.attr(ACC.navigation.data_toggle, "collapse")
					.attr("data-target", ".offcanvasGroup2")
					.text(oMyAccountData.data("title"))
					.append($("<span>").addClass("glyphicon glyphicon-chevron-down myAcctExp")));

            $('.js-myAccountLinksContainer').append(myAccountHook);

            //add UL element for nested collapsing list
            $('.js-myAccountLinksContainer').append($("<ul>")
					.attr(ACC.navigation.data_trigger ,"#signedInUserAccountToggle")
					.addClass("offcanvasGroup2 offcanvasNoBorder collapse js-nav-collapse-body subNavList js-myAccount-root sub-nav"));

            //*For toggling collapse on Mobile instead of with Bootstrap.js
            $('#signedInUserAccountToggle').click(ACC.navigation.toggleCollapseOnMobile);

            //offcanvas items
            //TODO Follow up here to see the output of the account data in the offcanvas menu
            for(var i = aAcctData.length - 1; i >= 0; i--){
                var oLink = oDoc.createElement("a");
                oLink.title = aAcctData[i].text;
                oLink.href = aAcctData[i].link;
                oLink.innerHTML = ACC.common.encodeHtml(aAcctData[i].text);

                var oListItem = oDoc.createElement("li");
                oListItem.appendChild(oLink);
                oListItem = $(oListItem);
                oListItem.addClass("auto ");
                $('.js-myAccount-root').append(oListItem);
            }

        } else if(sSignBtn) {
            var navButtons = sSignBtn.append(navClose);
            $('.js-sticky-user-group').append(navButtons);
        }

        //desktop
        for(var i = 0; i < aAcctData.length; i++){
            var oLink = oDoc.createElement("a");
            oLink.title = aAcctData[i].text;
            oLink.href = aAcctData[i].link;
            oLink.innerHTML = ACC.common.encodeHtml(aAcctData[i].text);

            var oListItem = oDoc.createElement("li");
            oListItem.appendChild(oLink);
            oListItem = $(oListItem);
            oListItem.addClass("auto col-md-4");
            oMMainNavDesktop.get(0).appendChild(oListItem.get(0));
        }

        //hide and show content areas for desktop
        $(ACC.navigation.js_secondaryNavAccount).on('shown.bs.collapse', ACC.navigation.hideOrShowNavAccountForDesktop);

        $(ACC.navigation.js_secondaryNavCompany).on('shown.bs.collapse', ACC.navigation.hideOrShowNavCompanyForDesktop);

        //change icons for up and down
        $('.js-nav-collapse-body').on('hidden.bs.collapse', ACC.navigation.hideNavCollapseBody);

        $('.js-nav-collapse-body').on('show.bs.collapse', ACC.navigation.showNavCollapseBody);

    },

    toggleCollapseMyAccount: function() {
        $(this).toggleClass('show');
        $(ACC.navigation.js_secondaryNavAccount).slideToggle(400);
        if ( $(this).hasClass('show') ) {
            $('.myCompanyLinksHeader').removeClass('show'); // hide the other one
            $(ACC.navigation.js_secondaryNavCompany).slideUp(400);
        }
        return false;
    },

    toggleCollapseOnMobile: function() {
        $(this).toggleClass('show');
        $(".offcanvasGroup2").slideToggle(400);
        if ( $(this).hasClass('show') ) {
            $(this).find('span').removeClass(ACC.navigation.glyphicon_chevron_down).addClass(ACC.navigation.glyphicon_chevron_up);
            $('#signedInCompanyToggle').removeClass('show'); // hide the other one
            $('#signedInCompanyToggle').find('span').removeClass(ACC.navigation.glyphicon_chevron_up).addClass(ACC.navigation.glyphicon_chevron_down);
            $('.offcanvasGroup3').slideUp(400);
        }
        else {
            $(this).find('span').removeClass(ACC.navigation.glyphicon_chevron_up).addClass(ACC.navigation.glyphicon_chevron_down);
        }
    },

    hideOrShowNavAccountForDesktop: function() {
        if($(ACC.navigation.js_secondaryNavCompany).hasClass('in')) {
            $('.js-myCompany-toggle').click();
        }
    },

    hideOrShowNavCompanyForDesktop: function() {
        if($(ACC.navigation.js_secondaryNavAccount).hasClass('in')){
            $('.js-myAccount-toggle').click();
        }
    },

    hideNavCollapseBody: function(event) {
            var target = $(event.target);
            var targetSpanSelector = target.attr(ACC.navigation.data_trigger) + ' > span';
            if(target.hasClass('in')) {
                $(document).find(targetSpanSelector).removeClass(ACC.navigation.glyphicon_chevron_down).addClass(ACC.navigation.glyphicon_chevron_up);
            }
            else {
                $(document).find(targetSpanSelector).removeClass(ACC.navigation.glyphicon_chevron_up).addClass(ACC.navigation.glyphicon_chevron_down);
            }
        },

    showNavCollapseBody: function(event) {
        var target = $(event.target);
        var targetSpanSelector = target.attr(ACC.navigation.data_trigger) + ' > span';
        if(target.hasClass('in')) {
            $(document).find(targetSpanSelector).removeClass(ACC.navigation.glyphicon_chevron_up).addClass(ACC.navigation.glyphicon_chevron_down);
        }
        else {
            $(document).find(targetSpanSelector).removeClass(ACC.navigation.glyphicon_chevron_down).addClass(ACC.navigation.glyphicon_chevron_up);
        }
    },

    createMobileSignInButton: function() {
        return $(ACC.navigation.liOffcanvas_a) && $(ACC.navigation.liOffcanvas_a).length > 0;
    },

    createMobileSignOutButton: function() {
        return $(ACC.navigation.liOffcanvas_form) && $(ACC.navigation.liOffcanvas_form).length > 0;
    },

    checkIfUserLoggedIn: function() {
        var oUserInfo = $(".nav__right ul li.logged_in");
        return oUserInfo && oUserInfo.length === 1;
    },

    orderToolsNavigation: function(){
        $('.js-nav-order-tools').on('click', function(e){
            $(this).toggleClass('js-nav-order-tools--active');
        });
    }
};
