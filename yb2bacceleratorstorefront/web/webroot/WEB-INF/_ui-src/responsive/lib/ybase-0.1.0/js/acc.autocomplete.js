ACC.autocomplete = {

	_autoload: [
		"bindSearchAutocomplete",
        "bindDisableSearch"
	],

	js_site_search_input: "#js-site-search-input",

	bindSearchAutocomplete: function ()
	{
		// extend the default autocomplete widget, to solve issue on multiple instances of the searchbox component
		$.widget( "custom.yautocomplete", $.ui.autocomplete, {
			_create:function(){
				
				// get instance specific options form the html data attr
				var option = this.element.data("options");
				// set the options to the widget
				this._setOptions({
					minLength: option.minCharactersBeforeRequest,
					displayProductImages: option.displayProductImages,
					delay: option.waitTimeBeforeRequest,
					autocompleteUrl: option.autocompleteUrl,
					source: this.source
				});
				
				// call the _super()
				$.ui.autocomplete.prototype._create.call(this);
				
			},
			options:{
				cache:{}, // init cache per instance
				focus: function (){return false;}, // prevent textfield value replacement on item focus
				select: function (event, ui){
					ui.item.value = ACC.sanitizer.sanitizeSelect(ui.item.value);
                    window.location.href = ui.item.url;
                }
			},
			_renderItem : function (ul, item){
				
				if (item.type == "autoSuggestion"){
					var renderHtml = $("<a>").attr("href", item.url)
							.append($("<div>").addClass("name").text(item.value));
					return $("<li>")
							.data("item.autocomplete", item)
							.append(renderHtml)
							.appendTo(ul);
				}
				else if (item.type == "productResult"){
				    var imageHtml = ACC.autocomplete.renderImage(item);
					var _renderHtml = $("<a>").attr("href", item.url)
							.append(imageHtml)
							.append($("<div>").addClass("name").html(ACC.sanitizer.sanitize(item.value)))
							.append($("<div>").addClass("price").text(item.price));

					return $("<li>")
							.data("item.autocomplete", item)
							.append(_renderHtml)
							.appendTo(ul);
				} else {
				    return $("<li>")
                           	.appendTo(ul);
				}
			},
			source: function (request, response)
			{
				var self=this;
				var term = request.term.toLowerCase();
				if (term in self.options.cache)
				{
					return response(self.options.cache[term]);
				}

				$.getJSON(self.options.autocompleteUrl, {term: request.term}, function (data)
				{
					var autoSearchData = [];
					if(data.suggestions != null){
						$.each(data.suggestions, function (i, obj)
						{
							autoSearchData.push({
								value: obj.term,
								url: ACC.config.encodedContextPath + "/search?text=" + encodeURIComponent(obj.term),
								type: "autoSuggestion"
							});
						});
					}
					if(data.products != null){
						$.each(data.products, function (i, obj)
						{
						    var imageUrl = ACC.autocomplete.getImageUrl(obj, self);
							autoSearchData.push({
								value: ACC.sanitizer.sanitize(obj.name),
								code: obj.code,
								desc: ACC.sanitizer.sanitize(obj.description),
								manufacturer: ACC.sanitizer.sanitize(obj.manufacturer),
								url:  ACC.config.encodedContextPath + obj.url,
								price: obj.price.formattedValue,
								type: "productResult",
								image: imageUrl // prevent errors if obj.images = null
							});
						});
					}
					self.options.cache[term] = autoSearchData;
					return response(autoSearchData);
				});
			}

		});
	
		$search = $(".js-site-search-input");
		if($search.length>0){
			$search.yautocomplete()
		}

	},

	renderImage: function(item) {
        return item.image
                            ? $("<div>").addClass("thumb")
                                    .append($("<img>").attr("src", item.image))
                            : null;
    },

    getImageUrl: function(obj, self) {
        return (obj.images != null && self.options.displayProductImages) ? obj.images[0].url : null;
    },

    bindDisableSearch: function ()
    {
        let $siteSearch = $(ACC.autocomplete.js_site_search_input);
        let updateSearchBtnState = function()
        {
            if ($siteSearch.val() != null)
            {
                 $siteSearch.val($siteSearch.val().replace(/^\s+/gm,''));
                 $('.js_search_button').prop('disabled', $siteSearch.val() == "" ? true : false);
            }
        };
        updateSearchBtnState();
        $siteSearch.on('input', updateSearchBtnState)
    }
};
