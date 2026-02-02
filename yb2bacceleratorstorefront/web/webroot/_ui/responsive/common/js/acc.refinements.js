ACC.CONSTANT_REFINEMENTS = {
    facet_form: '.js-facet-form'
};

ACC.refinements = {

	_autoload: [
		["bindMoreLessToggles", $(ACC.CONSTANT_REFINEMENTS.facet_form).length !== 0],
		["bindMoreStoresToggles", $(ACC.CONSTANT_REFINEMENTS.facet_form).length !== 0],
		["init", $(ACC.CONSTANT_REFINEMENTS.facet_form).length !== 0],
		["bindSearch", $(ACC.CONSTANT_REFINEMENTS.facet_form).length !== 0]
	],


	coords:{},
	storeSearchData:{},


	init:function(){
		navigator.geolocation.getCurrentPosition(
			function (position){
				ACC.refinements.coords = position.coords;
			},
			function (error)
			{
				console.log(`An error occurred... The error code and message are: ${error.code}/${error.message}`);
			}
		);

	},


	bindSearch:function() {

		$(document).on("submit",'#user_location_form', function(e) {
			e.preventDefault();
			var q = $(".js-shop-stores-facet .js-shop-store-search-input").val();
			 if(q.length > 0) {
				 ACC.refinements.getInitStoreData(q);				
			 }
		});

		$(document).on("click",'#findStoresNearMeAjax', function(e) {
			e.preventDefault();
			ACC.refinements.getInitStoreData(null,ACC.refinements.coords.latitude,ACC.refinements.coords.longitude);
		});
	},


	getInitStoreData: function(q,latitude,longitude){
		$(".alert").remove();
		var data = {
			"q":"" ,
			"page":"0"
		};

		if(q != null){
			data.q = q;
		}
		

		if(latitude != null){
			data.latitude = latitude;
		}

		if(longitude != null){
			data.longitude = longitude;
		}

		ACC.refinements.storeSearchData = data;
		ACC.refinements.getStoreData();
	},


	getStoreData: function(){
		var url= $(ACC.CONSTANT_REFINEMENTS.facet_form).data("url");
		$.ajax({
			url: url,
			data: ACC.refinements.storeSearchData,
			type: "get",
			dataType: 'text',
			success: function (response){
				window.location.reload();
			}
		});
	},
	
	bindMoreLessToggles: function() {

		$(document).on("click", ".js-shop-stores-facet .js-facet-change-link", function(e) {
			e.preventDefault();
			$(".js-shop-stores-facet .js-facet-container").hide();
			$(".js-shop-stores-facet .js-facet-form").show();
		});

		$(document).on("change", ".js-product-facet .js-facet-checkbox", function() {
			$(this).parents("form").submit();
		});
		
		$(document).on("click", ".js-product-facet .js-more-facet-values-link", function(e) {
			e.preventDefault();
			$(this).parents(".js-facet").find(".js-facet-top-values").hide();
			$(this).parents(".js-facet").find(".js-facet-list-hidden").show();

			$(this).parents(".js-facet").find(".js-more-facet-values").hide();
			$(this).parents(".js-facet").find(".js-less-facet-values").show();
		});

		$(document).on("click", ".js-product-facet .js-less-facet-values-link", function(e) {
			e.preventDefault();
			$(this).parents(".js-facet").find(".js-facet-top-values").show();
			$(this).parents(".js-facet").find(".js-facet-list-hidden").hide();

			$(this).parents(".js-facet").find(".js-more-facet-values").show();
			$(this).parents(".js-facet").find(".js-less-facet-values").hide();
		});
	},
	
	bindMoreStoresToggles: function ()
	{
		$(document).on("click", ".js-shop-stores-facet .js-more-stores-facet-values", function(e) {
			e.preventDefault();
			$(".js-shop-stores-facet ul.js-facet-list li.hidden").slice(0, 5).removeClass('hidden').first().find('.js-facet-checkbox').focus();
			if($(".js-shop-stores-facet ul.js-facet-list li.hidden").length === 0) {
				$(".js-shop-stores-facet .js-more-stores-facet-values").hide();
			}
		});
	}
};
