ACC.silentorderpost = {

    spinner: $("<img>").attr("src", ACC.config.commonResourcePath + "/images/spinner.gif"),
    useDeliveryAddress: '#useDeliveryAddress',
    useDeliveryAddressData: '#useDeliveryAddressData',
    input_id_prefix_address: 'input[id^="address\\."]',
    select_id_prefix_address: 'select[id^="address\\."]',

    bindUseDeliveryAddress: function ()
    {
        $(ACC.silentorderpost.useDeliveryAddress).on('change', function ()
        {
            if ($(ACC.silentorderpost.useDeliveryAddress).is(":checked"))
            {
                var option = {'countryIsoCode': $(ACC.silentorderpost.useDeliveryAddressData).data('countryisocode'), 'useDeliveryAddress': true};
                ACC.silentorderpost.enableAddressForm();
                ACC.silentorderpost.displayCreditCardAddressForm(option, ACC.silentorderpost.useDeliveryAddressSelected);
                ACC.silentorderpost.disableAddressForm();
            }
            else
            {
                ACC.silentorderpost.clearAddressForm();
                ACC.silentorderpost.enableAddressForm();
            }
        });

        if ($(ACC.silentorderpost.useDeliveryAddress).is(":checked"))
        {
            var options = {'countryIsoCode': $(ACC.silentorderpost.useDeliveryAddressData).data('countryisocode'), 'useDeliveryAddress': true};
            ACC.silentorderpost.enableAddressForm();
            ACC.silentorderpost.displayCreditCardAddressForm(options, ACC.silentorderpost.useDeliveryAddressSelected);
            ACC.silentorderpost.disableAddressForm();
        }
    },

    bindSubmitSilentOrderPostForm: function ()
    {
        $('.submit_silentOrderPostForm').click(function ()
        {
            ACC.common.blockFormAndShowProcessingMessage($(this));
            $('.billingAddressForm').filter(":hidden").remove();
            ACC.silentorderpost.enableAddressForm();
            $('#silentOrderPostForm').submit();
        });
    },

    bindCycleFocusEvent: function ()
    {
        $('#lastInTheForm').blur(function ()
        {
            $('#silentOrderPostForm [tabindex$="10"]').focus();
        });
    },

    isEmpty: function (obj)
    {
        if (typeof obj == 'undefined' || obj === null || obj === '') {
            return true;
        }
        return false;
    },

    disableAddressForm: function ()
    {
        $(ACC.silentorderpost.input_id_prefix_address).prop('disabled', true);
        $(ACC.silentorderpost.select_id_prefix_address).prop('disabled', true);
    },

    enableAddressForm: function ()
    {
        $(ACC.silentorderpost.input_id_prefix_address).prop('disabled', false);
        $(ACC.silentorderpost.select_id_prefix_address).prop('disabled', false);
    },

    clearAddressForm: function ()
    {
        $(ACC.silentorderpost.input_id_prefix_address).val("");
        $(ACC.silentorderpost.select_id_prefix_address).val("");
    },

    useDeliveryAddressSelected: function ()
    {
        if ($(ACC.silentorderpost.useDeliveryAddress).is(":checked"))
        {
            var countryIsoCode = $('#address\\.country').val($(ACC.silentorderpost.useDeliveryAddressData).data('countryisocode')).val();
            if(ACC.silentorderpost.isEmpty(countryIsoCode))
            {
                $(ACC.silentorderpost.useDeliveryAddress).click();
                $(ACC.silentorderpost.useDeliveryAddress).parent().hide();
            }
            else
            {
                ACC.silentorderpost.disableAddressForm();
            }
        }
        else
        {
            ACC.silentorderpost.clearAddressForm();
            ACC.silentorderpost.enableAddressForm();
        }
    },

    bindCreditCardAddressForm: function ()
    {
        $('#billingCountrySelector :input').on("change", function ()
        {
            var countrySelection = $(this).val();
            var options = {
                'countryIsoCode': countrySelection,
                'useDeliveryAddress': false
            };
            ACC.silentorderpost.displayCreditCardAddressForm(options);
        });
    },

    displayCreditCardAddressForm: function (options, callback)
    {
        $.ajax({
            url: ACC.config.encodedContextPath + '/checkout/multi/sop/billingaddressform',
            async: true,
            data: options,
            dataType: "html",
            beforeSend: function ()
            {
                $('#billingAddressForm').html(ACC.silentorderpost.spinner);
            }
        }).done(function (data)
                {
                    $("#billingAddressForm").html(data);
                    if (typeof callback == 'function')
                    {
                        callback.call();
                    }
                });
    }
};

$(document).ready(function ()
{
    ACC.silentorderpost.bindUseDeliveryAddress();
    ACC.silentorderpost.bindSubmitSilentOrderPostForm();
    ACC.silentorderpost.bindCreditCardAddressForm();

    // check the checkbox
    $("#useDeliveryAddress").click();
});
