/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.swagger;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@ApiImplicitParams({
		@ApiImplicitParam(name = "baseSiteId", value = "Base site identifier", required = true, dataType = "String", paramType = "path"),
		@ApiImplicitParam(name = "userId", value = "User identifier", required = true, dataType = "String", paramType = "path"),
		@ApiImplicitParam(name = "accountHolderName", value = "Name on card.", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "cardType", value = "Card type. Call GET /{baseSiteId}/cardtypes beforehand to see what card types are supported.", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "expiryMonth", value = "Month of expiry date.", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "expiryYear", value = "Year of expiry date.", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "issueNumber", dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "startMonth", dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "startYear", dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "subscriptionId", dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "saved", value = "Parameter defines if the payment details should be saved for the customer and than could be reused", dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "defaultPaymentInfo", value = "Parameter defines if the payment details should be used as default for customer.", dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "billingAddress.firstName", value = "Customer's first name.", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "billingAddress.lastName", value = "Customer's last name.", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "billingAddress.titleCode", value = "Customer's title code. For a list of codes, see /{baseSiteId}/titles resource", required = false, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "billingAddress.country.isocode", value = "Country isocode. This parameter havs influence on how rest of address parameters are validated (e.g. if parameters are required: line1,line2,town,postalCode,region.isocode)", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "billingAddress.line1", value = "If this parameter is required depends on country (usually it is required).", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "billingAddress.line2", value = "Second part of address. If this parameter is required depends on country (usually it is not required)", dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "bbillingAddress.town", value = "If this parameter is required depends on country (usually it is required)", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "billingAddress.postalCode", value = "Postal code. If this parameter is required depends on country (usually it is required)", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "billingAddressregion.isocode", value = "Isocode for region. If this parameter is required depends on country.", required = false, dataType = "String", paramType = "query") })
public @interface ApiBaseSiteIdAndUserIdAndPaymentDetailsParams
{
	//empty
}
