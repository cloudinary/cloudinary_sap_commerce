ACC.pickupinstore = {

	_autoload: [
		"bindClickPickupInStoreButton",
		"bindPickupButton",
		"bindPickupClose",
		"bindPickupInStoreSearch"
	],

	colorbox_and_js_pickup_store_pager_prev: "#colorbox .js-pickup-store-pager-prev",
    colorbox_and_js_pickup_store_pager_next: "#colorbox .js-pickup-store-pager-next",
    js_add_pickup_cart_and_pdpPickupAddtoCartInput: ".js-add-pickup-cart #pdpPickupAddtoCartInput",
    locationForSearch: '#locationForSearch',
    atCartPage: '#atCartPage',
    colorbox_and_js_pickup_component: "#colorbox .js-pickup-component",
    colorbox: "#colorbox #",
    pdpPickupAddtoCartInput: '#pdpPickupAddtoCartInput',
    js_store_prefix: ".js-store-",
    add_to_cart_storepickup_form_and_js_store_id: "#add_to_cart_storepickup_form .js-store-id",
    hidden_xs_and_hidden_sm: 'hidden-xs hidden-sm',
    longest_selector_id: "#colorbox .js-add-to-cart-for-pickup-popup, #colorbox .js-qty-selector-minus, #colorbox .js-qty-selector-input, #colorbox .js-qty-selector-plus",

	storeId:"",

	unbindPickupPaginationResults:function ()
	{
		$(document).off("click",ACC.pickupinstore.colorbox_and_js_pickup_store_pager_prev);
		$(document).off("click",ACC.pickupinstore.colorbox_and_js_pickup_store_pager_next);
	},

	bindPickupPaginationResults:function ()
	{
		var listHeight=  $("#colorbox .js-pickup-store-list").height();
		var $listitems= $("#colorbox .js-pickup-store-list > li");
		var listItemHeight = $listitems.height();
		var displayCount = 5;
		var totalCount= $listitems.length;
		var curPos=0
		var pageEndPos = (((totalCount/displayCount)-1) * (displayCount*listItemHeight)) * -1;


		$("#colorbox .js-pickup-store-pager-item-all").text(totalCount);

		$("#colorbox .store-navigation-pager").show();



		checkPosition()

		$(document).on("click",ACC.pickupinstore.colorbox_and_js_pickup_store_pager_prev,function(e){
			e.preventDefault();
			$listitems.css("transform","translateY("+(curPos+listHeight)+"px)")
			curPos = curPos+listHeight;
			checkPosition();
		})

		$(document).on("click",ACC.pickupinstore.colorbox_and_js_pickup_store_pager_next,function(e){
			e.preventDefault();
			$listitems.css("transform","translateY("+(curPos-listHeight)+"px)")
			curPos = curPos-listHeight;
			checkPosition();
		})

		function checkPosition(){

			var curPage = Math.ceil((curPos/(displayCount*listItemHeight))*-1)+1;
			$("#colorbox .js-pickup-store-pager-item-from").text(curPage*displayCount-4);

			var tocount = (curPage*displayCount > totalCount)? totalCount :curPage*displayCount;

			if(curPage*displayCount-4 == 1){
				$(ACC.pickupinstore.colorbox_and_js_pickup_store_pager_prev).hide();
			}else{
				$(ACC.pickupinstore.colorbox_and_js_pickup_store_pager_prev).show();
			}

			if(curPage*displayCount >= totalCount){
				$(ACC.pickupinstore.colorbox_and_js_pickup_store_pager_next).hide();
			}else{
				$(ACC.pickupinstore.colorbox_and_js_pickup_store_pager_next).show();
			}


			$("#colorbox .js-pickup-store-pager-item-to").text(tocount);
		}
	},



	bindPickupInStoreQuantity:function(){
		$('.pdpPickupQtyPlus').click(function(e){
			e.preventDefault();

			var inputQty = $(ACC.pickupinstore.js_add_pickup_cart_and_pdpPickupAddtoCartInput);
			var currentVal = parseInt(inputQty.val());
			var maxVal = inputQty.data('max');

			if (!isNaN(currentVal) && currentVal < maxVal) {
				inputQty.val(currentVal + 1);
				inputQty.change();
			}
		});

		$('.pdpPickupQtyMinus').click(function(e){
			e.preventDefault();
			var inputQty = $(ACC.pickupinstore.js_add_pickup_cart_and_pdpPickupAddtoCartInput);
			var currentVal = parseInt(inputQty.val());
			var minVal = inputQty.data('min');

			if (!isNaN(currentVal) && currentVal > minVal) {
				inputQty.val(currentVal - 1);
				inputQty.change();
			}
		});

		$("body").on("keyup", ACC.pickupinstore.js_add_pickup_cart_and_pdpPickupAddtoCartInput, function(event) {
			var input = $(event.target);
			input.val(this.value.match(/[0-9]*/));
			var value = input.val();
		});
	},

	bindPickupInStoreSearch: function ()
	{
		$(document).on('click', '#pickupstore_location_search_button', function (e)
		{
			ACC.pickupinstore.locationSearchSubmit($(ACC.pickupinstore.locationForSearch).val(), $(ACC.pickupinstore.atCartPage).val(), $('#entryNumber').val(), $(this).parents('form').attr('action'));
			return false;
		});

		$(document).on('keypress', ACC.pickupinstore.locationForSearch, function (e)
		{
			if (e.keyCode === 13)
			{
				e.preventDefault();
				ACC.pickupinstore.locationSearchSubmit($(ACC.pickupinstore.locationForSearch).val(), $(ACC.pickupinstore.atCartPage).val(), $('input.entryNumber').val(), $(this).parents('form').attr('action'));
			}
		});
	},

	bindPickupHereInStoreButtonClick: function ()
	{
		$(document).on('click','.pickup_add_to_bag_instore_button', function (e){
			$(this).prev('.hiddenPickupQty').val($('#pickupQty').val());
		});

		$(document).on('click','.pickup_here_instore_button', function (e){
			$(this).prev('.hiddenPickupQty').val($('#pickupQty').val());
			ACC.colorbox.close();
		});
	},

	locationSearchSubmit: function (location, cartPage, entryNumber, actionUrl, latitude, longitude)
	{
		$(ACC.pickupinstore.longest_selector_id).attr("disabled","disabled");

		$.post({
			url: actionUrl,
			data: {locationQuery: location, cartPage: cartPage, entryNumber: entryNumber, latitude: latitude, longitude: longitude},
			dataType: "text",
			success: function (response)
			{
				ACC.pickupinstore.refreshPickupInStoreColumn(response);
			}
		});
	},

	createListItemHtml: function (data,id){

		
		var $rdioEl = $("<input>").attr("type","radio")
							.attr("name","storeNamePost")
							.attr("id","pickup-entry-" + id)
							.attr("data-id", id)
							.addClass("js-pickup-store-input")
							.val(data.displayName);
		
		var $spanElStInfo = $("<span>")
							.addClass("pickup-store-info")
							.append($("<span>").addClass("pickup-store-list-entry-name").text(data.displayName))
							.append($("<span>").addClass("pickup-store-list-entry-address").text(data.line1 + " " + data.line2))
							.append($("<span>").addClass("pickup-store-list-entry-city").text(data.town));
			
		var $spanElStAvail = $("<span>")
							.addClass("store-availability")
							.append(
									$("<span>")
									.addClass("available")
									.append(document.createTextNode(data.formattedDistance))
									.append("<br>")
									.append(data.stockPickupHtml)
							);
		
		var $lblEl = $("<label>").addClass("js-select-store-label")
						.attr("for","pickup-entry-" + id)
						.append($spanElStInfo)
						.append($spanElStAvail);
		
		return $("<li>").addClass("pickup-store-list-entry")
						.append($rdioEl)
						.append($lblEl);
	},

	refreshPickupInStoreColumn: function (data){
		data = $.parseJSON(data);
		var $storeList = $('#colorbox .js-pickup-store-list');
		$storeList.empty();
		
		$(ACC.pickupinstore.colorbox_and_js_pickup_component).data("data",data);

		for(var i = 0;i < data["data"].length;i++){
			$storeList.append(ACC.pickupinstore.createListItemHtml(data["data"][i],i));
		}

		ACC.pickupinstore.unbindPickupPaginationResults()
		ACC.pickupinstore.bindPickupPaginationResults()

		// select the first store
		var firstInput= $("#colorbox .js-pickup-store-input")[0];
		$(firstInput).click();


		$(ACC.pickupinstore.longest_selector_id).removeAttr("disabled");


	},

	bindClickPickupInStoreButton :function()
	{


		$(document).on("click",".js-pickup-in-store-button",function(e){
			e.preventDefault();
			var ele = $(this);
			var productId = "pickupModal_" + $(this).attr('id');
			var cartItemProductPostfix = '';
			var productIdNUM = $(this).attr('id');
			productIdNUM = productIdNUM.split("_");
			productIdNUM = productIdNUM[1];

			if (productId != null)
			{
				cartItemProductPostfix = '_' + productId;
			}

			var boxContent =  $("#popup_store_pickup_form > #pickupModal").clone();
			var titleHeader = $('#pickupTitle > .pickup-header').html();


			ACC.colorbox.open(titleHeader,{
				html:boxContent,
				width:"960px",
				onComplete: function(){

					$(ACC.pickupinstore.longest_selector_id).attr("disabled","disabled");


					boxContent.show();
					ACC.pickupinstore.pickupStorePager();
					var tabs = $("#colorbox .js-pickup-tabs").accessibleTabs({
						tabhead:'.tabhead',
						tabbody: '.tabbody',
						fx:'show',
						fxspeed: 0,
						currentClass: 'active',
						autoAnchor:true,
						cssClassAvailable:true
					});

					$("#colorbox #pickupModal *").each(function ()
					{
						if($(this).attr("data-id")!= undefined)
						{
							$(this).attr("id", $(this).attr("data-id"));
							$(this).removeAttr("data-id");
						}
					});

					$("#colorbox input#locationForSearch").focus();

					// set a unique id
					$("#colorbox #pickupModal").attr("id", productId);

					// insert the product image
					$(`${ACC.pickupinstore.colorbox}${productId} .thumb`).html(ele.data("imgHtml"));

					// insert the product cart details
					$(`${ACC.pickupinstore.colorbox}${productId} .js-pickup-product-price`).html(ele.data("productcart"));

					var variants=ele.data("productcartVariants");
					var variantsBox = $(`${ACC.pickupinstore.colorbox}${productId} .js-pickup-product-variants`);
					$.each(variants,function(key,value){
					    variantsBox.append($("<span>").text(value));
					});

					// insert the product name
					$(`${ACC.pickupinstore.colorbox}${productId} .js-pickup-product-info`).html(ele.data("productnameHtml"));

					// insert the form action
					$(`${ACC.pickupinstore.colorbox}${productId} form.searchPOSForm`).attr("action", ele.data("actionurl"));

					// set a unique id for the form
					$(`${ACC.pickupinstore.colorbox}${productId} form.searchPOSForm`).attr("id", "pickup_in_store_search_form_product_" + productIdNUM);

					// set the quantity, if the quantity is undefined set the quantity to the data-value defined in the jsp
					$(`${ACC.pickupinstore.colorbox}${productId} #pdpPickupAddtoCartInput`)
					    .attr("value", ($(ACC.pickupinstore.pdpPickupAddtoCartInput).val() !== undefined ? $(ACC.pickupinstore.pdpPickupAddtoCartInput).val() : ele.data("value")));
					// set the entry Number
					$(`${ACC.pickupinstore.colorbox}${productId} input#entryNumber`).attr("value", ele.data("entrynumber"));
					// set the cartPage bolean
					$(`${ACC.pickupinstore.colorbox}${productId} input#atCartPage`).attr("value", ele.data("cartpage"));

					
					if(navigator.geolocation){
						navigator.geolocation.getCurrentPosition(
							function (position){
								ACC.pickupinstore.locationSearchSubmit('', $(ACC.pickupinstore.atCartPage).val(),  ele.data("entrynumber"), ele.data("actionurl"),position.coords.latitude, position.coords.longitude);
							},
							function (error){
									console.log(`An error occurred... The error code and message are: ${error.code}/${error.message}`);
							}
						);
					}
					
					ACC.product.bindToAddToCartStorePickUpForm();

					
				}

			});

		})
	},

	pickupStorePager:function()
	{
		$(document).on("change","#colorbox .js-pickup-store-input",function(e){
			e.preventDefault();


			$("#colorbox .js-pickup-tabs li.first a").click();

			var storeData=$(ACC.pickupinstore.colorbox_and_js_pickup_component).data("data");
			storeData=storeData["data"];

			var storeId=$(this).data("id");

			var $ele = $("#colorbox .display-details");


			$.each(storeData[storeId],function(key,value){
				if(key=="url"){
					$ele.find(".js-store-image").empty();
					if(value!=""){
						$ele.find(".js-store-image").append($("<img>").attr("src", value).attr("alt", ""));
					}
				}else if(key=="productcode"){
					$ele.find(".js-store-productcode").val(value);
				}
				else if(key=="openings"){
					var $oele = $ele.find(ACC.pickupinstore.js_store_prefix + key);
					$oele.empty();
					if(value!=""){
						$.each(value,function(key2,value2){
							$oele.append($("<dt>").text(key2));
							$oele.append($("<dd>").text(value2));
						});
					}

				}
				else if(value!==""){
					$ele.find(ACC.pickupinstore.js_store_prefix + key).text(value);
				}else{
					$ele.find(ACC.pickupinstore.js_store_prefix + key).empty();
				}

			})

			$(document).one("click", "#colorbox .js-pickup-map-tab",function(){
				ACC.pickupinstore.storeId = storeData[storeId];
				ACC.global.addGoogleMapsApi("ACC.pickupinstore.drawMap");
			})

			
			

			var e=$("#colorbox .pickup-store-list-entry input:checked");


			$(ACC.pickupinstore.add_to_cart_storepickup_form_and_js_store_id).attr("id",e.attr("id"));
			$(ACC.pickupinstore.add_to_cart_storepickup_form_and_js_store_id).attr("name",e.attr("name"));
			$(ACC.pickupinstore.add_to_cart_storepickup_form_and_js_store_id).val(e.val());

			if(storeData[storeId]["stockLevel"] > 0 || storeData[storeId]["stockLevel"] == "")
			{
				var input = $("#add_to_cart_storepickup_form .js-qty-selector-input");
				input.data("max",storeData[storeId]["stockLevel"]); 
				ACC.productDetail.checkQtySelector(input, "reset");
				$("#add_to_cart_storepickup_form").show()
				
			} else{
				$("#add_to_cart_storepickup_form").hide()
			}


		})

		$(document).on("click",".js-select-store-label",function(e){
			$(ACC.pickupinstore.colorbox_and_js_pickup_component).addClass("show-store");
			$("#colorbox #cboxTitle .headline-inner").addClass(ACC.pickupinstore.hidden_xs_and_hidden_sm);
			$("#colorbox #cboxTitle .back-to-storelist").removeClass(ACC.pickupinstore.hidden_xs_and_hidden_sm);
		})

		$(document).on("click",".js-back-to-storelist",function(e){
			$(ACC.pickupinstore.colorbox_and_js_pickup_component).removeClass("show-store");
			$("#colorbox #cboxTitle .headline-inner").removeClass(ACC.pickupinstore.hidden_xs_and_hidden_sm);
			$("#colorbox #cboxTitle .back-to-storelist").addClass(ACC.pickupinstore.hidden_xs_and_hidden_sm);
		})

	},


	bindPickupButton : function(){
		$(document).on("click",".js-pickup-button",function(e){
			e.preventDefault();
			var $e = $(this).parent().nextAll(".js-inline-layer")
			$e.addClass("open")

			//$e.height($e.height())
			var h= $e.height()
			$e.removeClass("open")

			$e.animate({
				height: h
			})
		})
	},


	bindPickupClose : function(){
		$(document).on("click",".js-close-inline-layer",function(e){
			e.preventDefault();
			var $e = $(this).parents(".js-inline-layer")

			$e.animate({
				height: 0
			})
		})
	},
	
	checkIfPointOfServiceIsEmpty: function (cartEntryDeliveryModeForm)
	{
		return (!cartEntryDeliveryModeForm.find('.pointOfServiceName').text().trim().length);
	},
	
	validatePickupinStoreCartEntires: function ()
	{
		var validationErrors = false;
		$("form.cartEntryShippingModeForm").each(function ()
		{
			var formid = "#" + $(this).attr('id');
			if ($(formid + ' input[value=pickUp][checked]').length && ACC.pickupinstore.checkIfPointOfServiceIsEmpty($(this)))
			{
				$(this).addClass("shipError");
				validationErrors = true;
			}
		});

		if (validationErrors)
		{
			$('div#noStoreSelected').show().focus();
			$(window).scrollTop(0);
		}
		return validationErrors;
	},

	
	drawMap: function(){

		var storeInformation = ACC.pickupinstore.storeId;

		if($("#colorbox .js-map-canvas").length > 0)
		{			


			$("#colorbox .js-map-canvas").attr("id","pickup-map")
			var centerPoint = new google.maps.LatLng(storeInformation["storeLatitude"], storeInformation["storeLongitude"]);
			
			var mapOptions = {
				zoom: 13,
				zoomControl: true,
				panControl: true,
				streetViewControl: false,
				mapTypeId: google.maps.MapTypeId.ROADMAP,
				center: centerPoint
			}
			
			var map = new google.maps.Map(document.getElementById("pickup-map"), mapOptions);
			
			var marker = new google.maps.Marker({
				position: new google.maps.LatLng(storeInformation["storeLatitude"], storeInformation["storeLongitude"]),
				map: map,
				title: storeInformation["name"],
				icon: "https://maps.google.com/mapfiles/marker" + 'A' + ".png"
			});
			var infowindow = new google.maps.InfoWindow({
				content: ACC.common.encodeHtml(storeInformation["name"]),
				disableAutoPan: true
			});
			google.maps.event.addListener(marker, 'click', function (){
				var mapWindow = infowindow.open(map, marker, 'noopener,noreferrer');
				mapWindow.opener = null;
			});
		}
		
	}

};
