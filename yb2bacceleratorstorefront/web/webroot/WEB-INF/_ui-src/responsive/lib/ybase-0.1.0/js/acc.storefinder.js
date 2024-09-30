ACC.CONSTANT_STORE_FINDER = {
    store_finder: ".js-store-finder"
};

ACC.storefinder = {

	_autoload: [
		["init", $(ACC.CONSTANT_STORE_FINDER.store_finder).length !== 0],
		["bindStoreChange", $(ACC.CONSTANT_STORE_FINDER.store_finder).length !== 0],
		["bindSearch", $(ACC.CONSTANT_STORE_FINDER.store_finder).length !== 0],
		"bindPagination"
	],
	
	storeData:"",
	storeId:"",
	coords:{},
	storeSearchData:{},
	show_store: "show-store",
	js_store_finder_pager_prev: ".js-store-finder-pager-prev",
	js_store_finder_pager_next: ".js-store-finder-pager-next",
	js_store_prefix: ".js-store-",
	js_store_finder_map: ".js-store-finder-map",
	findStoresNearMe: "#findStoresNearMe",

	createListItemHtml: function (data,id){

		var $rdioEl = $("<input>").addClass("js-store-finder-input")
							.attr("type","radio")
							.attr("name","storeNamePost")
							.attr("id","store-filder-entry-" + id)
							.attr("data-id",id)
							.val(data.displayName);
				
		var $spanInfo = $("<span>").addClass("entry__info")
							.append($("<span>").addClass("entry__name").text(data.displayName))
							.append($("<span>").addClass("entry__address").text(data.line1 + ' ' + data.line2))
							.append($("<span>").addClass("entry__city").text(data.town));
							
		
		var $spanDistance = $("<span>").addClass("entry__distance")
							.append($("<span>").text(data.formattedDistance));
							
		var $label = $("<label>").addClass("js-select-store-label")
							.attr("for","store-filder-entry-" + id)
							.append($spanInfo)
							.append($spanDistance);					
		
		return $("<li>").addClass("list__entry")
					.append($rdioEl)
					.append($label)
		
	},

	refreshNavigation: function (){
		var data = ACC.storefinder.storeData
		
		var $storeList = $(".js-store-finder-navigation-list");
		$storeList.empty();
		
		if(data){
			for(var i = 0;i < data["data"].length;i++){
				$storeList.append(ACC.storefinder.createListItemHtml(data["data"][i],i));
			}
	
			// select the first store
			var firstInput= $(".js-store-finder-input")[0];
			$(firstInput).click();
		}


		var page = ACC.storefinder.storeSearchData.page;
		$(".js-store-finder-pager-item-from").text(page*10+1);

		var to = ((page*10+10)>ACC.storefinder.storeData.total)? ACC.storefinder.storeData.total : page*10+10 ;
		$(".js-store-finder-pager-item-to").text(to);
		$(".js-store-finder-pager-item-all").text(ACC.storefinder.storeData.total);
		$(ACC.CONSTANT_STORE_FINDER.store_finder).removeClass(ACC.storefinder.show_store);

	},


	bindPagination:function ()
	{


		
		$(document).on("click",".js-store-finder-details-back",function(e){
			e.preventDefault();
			
			$(ACC.CONSTANT_STORE_FINDER.store_finder).removeClass(ACC.storefinder.show_store);
			
		})
		



		$(document).on("click",ACC.storefinder.js_store_finder_pager_prev,function(e){
			e.preventDefault();
			var page = ACC.storefinder.storeSearchData.page;
			ACC.storefinder.getStoreData(page-1)
			checkStatus(page-1);
		})

		$(document).on("click",ACC.storefinder.js_store_finder_pager_next,function(e){
			e.preventDefault();
			var page = ACC.storefinder.storeSearchData.page;
			ACC.storefinder.getStoreData(page+1)
			checkStatus(page+1);
		})

		function checkStatus(page){
			if(page==0){
				$(ACC.storefinder.js_store_finder_pager_prev).attr("disabled","disabled");
			}else{
				$(ACC.storefinder.js_store_finder_pager_prev).removeAttr("disabled");
			}
			
			if(page == Math.floor(ACC.storefinder.storeData.total/10)){
				$(ACC.storefinder.js_store_finder_pager_next).attr("disabled","disabled");
			}else{
				$(ACC.storefinder.js_store_finder_pager_next).removeAttr("disabled");
			}
		}

	},


	bindStoreChange:function()
	{
		$(document).on("change",".js-store-finder-input",function(e){
			e.preventDefault();



			var storeData=ACC.storefinder.storeData["data"];

			var storeId=$(this).data("id");

			var $ele = $(".js-store-finder-details");
			


			$.each(storeData[storeId],function(key,value){
				if(key=="image"){
					$ele.find(".js-store-image").empty();
					if(value!=""){
						$ele.find(".js-store-image").append($("<img>").attr("src", value).attr("alt", ""));
					}
				}else if(key=="productcode"){
					$ele.find(".js-store-productcode").val(value);
				}
				else if(key=="openings"){
					var $oele = $ele.find(ACC.storefinder.js_store_prefix + key);
					$oele.empty();
					if(value!=""){
						$.each(value,function(key2,value2){
							$oele.append($("<dt>").text(key2));
							$oele.append($("<dd>").text(value2));
						});
						
					}

				}
				else if(key=="features"){
					var $features = $ele.find(ACC.storefinder.js_store_prefix + key);
					$features.empty();
					$.each(value,function(key2,value2){
						$features.append($("<li>").text(value2));
					});
				}
				else if(value!==""){
				    $ele.find(ACC.storefinder.js_store_prefix + key).text(value);
				} else {
				    $ele.find(ACC.storefinder.js_store_prefix + key).empty();
				}
			})


			ACC.storefinder.storeId = storeData[storeId];
			ACC.storefinder.initGoogleMap();

		})

		$(document).on("click",".js-select-store-label",function(e){
			$(ACC.CONSTANT_STORE_FINDER.store_finder).addClass(ACC.storefinder.show_store);
		})

		$(document).on("click",".js-back-to-storelist",function(e){
			$(ACC.CONSTANT_STORE_FINDER.store_finder).removeClass(ACC.storefinder.show_store);
		})

	},



	initGoogleMap:function(){

		if($(ACC.storefinder.js_store_finder_map).length > 0){
			ACC.global.addGoogleMapsApi("ACC.storefinder.loadGoogleMap");
		}
	},
 
	loadGoogleMap: function(){

		var storeInformation = ACC.storefinder.storeId;

		if($(ACC.storefinder.js_store_finder_map).length > 0)
		{			
			$(ACC.storefinder.js_store_finder_map).attr("id","store-finder-map");
			var centerPoint = new google.maps.LatLng(storeInformation["latitude"], storeInformation["longitude"]);
			
			var mapOptions = {
				zoom: 13,
				zoomControl: true,
				panControl: true,
				streetViewControl: false,
				mapTypeId: google.maps.MapTypeId.ROADMAP,
				center: centerPoint
			}
			
			var map = new google.maps.Map(document.getElementById("store-finder-map"), mapOptions);
			
			var marker = new google.maps.Marker({
				position: new google.maps.LatLng(storeInformation["latitude"], storeInformation["longitude"]),
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
		
	},


	bindSearch:function(){

		$(document).on("submit",'#storeFinderForm', function(e){
			e.preventDefault()
			var q = $(".js-store-finder-search-input").val();

			if(q.length>0){
				ACC.storefinder.getInitStoreData(q);
			}else{
				if($(".js-storefinder-alert").length<1){
					var emptySearchMessage = $(".btn-primary").data("searchEmpty")
					$(ACC.CONSTANT_STORE_FINDER.store_finder).hide();
					$("#storeFinder").before(
							$("<div>").addClass("js-storefinder-alert alert alert-danger alert-dismissable getAccAlert")
								.append($("<button>").addClass("close closeAccAlert")
										.attr("type", "button")
										.attr("data-dismiss", "alert")
										.attr("aria-hidden", "true")
										.text("x"))
								.text(emptySearchMessage));
                    $(".closeAccAlert").on("click", function () {
                        $(this).parent('.getAccAlert').remove();
                    });

                }
			}
		})


		$(ACC.CONSTANT_STORE_FINDER.store_finder).hide();
		$(document).on("click",ACC.storefinder.findStoresNearMe, function(e){
			e.preventDefault()
			ACC.storefinder.getInitStoreData(null,ACC.storefinder.coords.latitude,ACC.storefinder.coords.longitude);
		})


	},


	getStoreData: function(page){
		ACC.storefinder.storeSearchData.page = page;
		var url= $(ACC.CONSTANT_STORE_FINDER.store_finder).data("url");
		$.ajax({
			url: url,
			data: ACC.storefinder.storeSearchData,
			type: "get",
            dataType: 'json',
			success: function (response){
                ACC.storefinder.storeData = response;
				ACC.storefinder.refreshNavigation();
				if(ACC.storefinder.storeData.total < 10){
					$(ACC.storefinder.js_store_finder_pager_next).attr("disabled","disabled");
				}
			}
		});
	},

	getInitStoreData: function(q,latitude,longitude){
		$(".alert").remove();
		const data ={
			"q":"" ,
			"page":0
		}
		if(q != null){
			data.q = q;
		}

		if(latitude != null){
			data.latitude = latitude;
		}

		if(longitude != null){
			data.longitude = longitude;
		}

		ACC.storefinder.storeSearchData = data;
		ACC.storefinder.getStoreData(data.page);
		$(ACC.CONSTANT_STORE_FINDER.store_finder).show();
		$(ACC.storefinder.js_store_finder_pager_prev).attr("disabled","disabled");
		$(ACC.storefinder.js_store_finder_pager_next).removeAttr("disabled");
	},

	init:function(){
		$("#findStoresNearMe").attr("disabled","disabled");
		if(navigator.geolocation){
			navigator.geolocation.getCurrentPosition(
				function (position){
					ACC.storefinder.coords = position.coords;
					$(ACC.storefinder.findStoresNearMe).removeAttr("disabled");
				},
				function (error)
				{
					console.log(`An error occurred... The error code and message are: ${error.code}/${error.message}`);
				}
			);
		}
	}
};
