ACC.productDetail = {

    _autoload: [
        "initPageEvents",
        "bindVariantOptions"
    ],

    js_qty_selector_input: '.js-qty-selector-input',
    js_qty_selector_and_js_qty_selector_input: '.js-qty-selector .js-qty-selector-input',


    checkQtySelector: function (self, mode) {
    	var $qtySelector = $(document).find(self).parents(".js-qty-selector");
        var input = $qtySelector.find(ACC.productDetail.js_qty_selector_input);
        var inputVal = parseInt(input.val());
        var max = input.data("max");
        var minusBtn = $qtySelector.find(".js-qty-selector-minus");
        var plusBtn = $qtySelector.find(".js-qty-selector-plus");

        $qtySelector.find(".btn").removeAttr("disabled");

        var paramsObj = {
            inputVal: inputVal,
            max: max,
            self: self,
            minusBtn: minusBtn,
            plusBtn: plusBtn
        };

        if (mode === "minus") {
            ACC.productDetail.checkQtyMinus(paramsObj);
        } else if (mode === "reset") {
            ACC.productDetail.updateQtyValue(self, 1);
        } else if (mode === "plus") {
        	ACC.productDetail.checkQtyPlus(paramsObj);
        } else if (mode === "input") {
           ACC.productDetail.checkQtyInput(paramsObj);
        } else if (mode === "focusout") {
           ACC.productDetail.checkQtyFocusout(paramsObj);
        }

    },

    checkQtyMinus: function(params) {
        if (params.inputVal !== 1) {
            ACC.productDetail.updateQtyValue(params.self, params.inputVal - 1)
            if (params.inputVal - 1 === 1) {
                params.minusBtn.attr("disabled", "disabled")
            }
        } else {
            params.minusBtn.attr("disabled", "disabled")
        }
    },

    checkQtyPlus: function(params) {
        if(params.max === "FORCE_IN_STOCK") {
            ACC.productDetail.updateQtyValue(params.self, params.inputVal + 1)
        } else if (params.inputVal <= params.max) {
           ACC.productDetail.updateQtyValue(params.self, params.inputVal + 1)
           if (params.inputVal + 1 === params.max) {
               params.plusBtn.attr("disabled", "disabled")
           }
        } else {
           params.plusBtn.attr("disabled", "disabled")
        }
    },

    checkQtyInput: function(params) {
        if (params.inputVal === 1) {
            params.minusBtn.attr("disabled", "disabled")
        } else if(params.max === "FORCE_IN_STOCK" && params.inputVal > 0) {
            ACC.productDetail.updateQtyValue(params.self, params.inputVal)
        } else if (params.inputVal === params.max) {
            params.plusBtn.attr("disabled", "disabled")
        } else if (params.inputVal < 1) {
            ACC.productDetail.updateQtyValue(params.self, 1)
            params.minusBtn.attr("disabled", "disabled")
        } else if (params.inputVal > params.max) {
            ACC.productDetail.updateQtyValue(params.self, params.max)
            params.plusBtn.attr("disabled", "disabled")
        }
    },

    checkQtyFocusout: function(params) {
        if (isNaN(params.inputVal)){
            ACC.productDetail.updateQtyValue(params.self, 1);
            params.minusBtn.attr("disabled", "disabled");
        } else if(params.inputVal >= params.max) {
            params.plusBtn.attr("disabled", "disabled");
        }
    },

    updateQtyValue: function (self, value) {
        var input = $(document).find(self).parents(".js-qty-selector").find(ACC.productDetail.js_qty_selector_input);
        var addtocartQty = $(document).find(self).parents(".addtocart-component").find("#addToCartForm").find(ACC.productDetail.js_qty_selector_input);
        var configureQty = $(document).find(self).parents(".addtocart-component").find("#configureForm").find(ACC.productDetail.js_qty_selector_input);
        input.val(value);
        addtocartQty.val(value);
        configureQty.val(value);
    },

    initPageEvents: function () {
        $(document).on("click", '.js-qty-selector .js-qty-selector-minus', function () {
            ACC.productDetail.checkQtySelector(this, "minus");
        })

        $(document).on("click", '.js-qty-selector .js-qty-selector-plus', function () {
            ACC.productDetail.checkQtySelector(this, "plus");
        })

        $(document).on("keydown", ACC.productDetail.js_qty_selector_and_js_qty_selector_input, function (e) {
            var specialRangeSet = new Set([48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105]);
            var specialValueSet = new Set([8, 46, 37, 39, 9]);
            if (($(this).val() !== " " && specialRangeSet.has(e.which)) || specialValueSet.has(e.which)) {
                // this is intentional
            }
            else if (e.which == 38) {
                ACC.productDetail.checkQtySelector(this, "plus");
            }
            else if (e.which == 40) {
                ACC.productDetail.checkQtySelector(this, "minus");
            }
            else {
                e.preventDefault();
            }
        })

        $(document).on("keyup", ACC.productDetail.js_qty_selector_and_js_qty_selector_input, function (e) {
            ACC.productDetail.checkQtySelector(this, "input");
            ACC.productDetail.updateQtyValue(this, $(this).val());

        })
        
        $(document).on("focusout", ACC.productDetail.js_qty_selector_and_js_qty_selector_input, function (e) {
            ACC.productDetail.checkQtySelector(this, "focusout");
            ACC.productDetail.updateQtyValue(this, $(this).val());
        })

        $("#Size").change(function () {
            changeOnVariantOptionSelection($("#Size option:selected"));
        });

        $("#variant").change(function () {
            changeOnVariantOptionSelection($("#variant option:selected"));
        });

        $(".selectPriority").change(function () {
            window.location.href = $(this[this.selectedIndex]).val();
        });

        function changeOnVariantOptionSelection(optionSelected) {
            window.location.href = optionSelected.attr('value');
        }
    },

    bindVariantOptions: function () {
        ACC.productDetail.bindCurrentStyle();
        ACC.productDetail.bindCurrentSize();
        ACC.productDetail.bindCurrentType();
    },

    bindCurrentStyle: function () {
        var currentStyle = $("#currentStyleValue").data("styleValue");
        var styleSpan = $(".styleName");
        if (currentStyle != null) {
            styleSpan.text(": " + currentStyle);
        }
    },

    bindCurrentSize: function () {
        var currentSize = $("#currentSizeValue").data("sizeValue");
        var sizeSpan = $(".sizeName");
        if (currentSize != null) {
            sizeSpan.text(": " + currentSize);
        }
    },

    bindCurrentType: function () {
        var currentSize = $("#currentTypeValue").data("typeValue");
        var sizeSpan = $(".typeName");
        if (currentSize != null) {
            sizeSpan.text(": " + currentSize);
        }
    }
};
