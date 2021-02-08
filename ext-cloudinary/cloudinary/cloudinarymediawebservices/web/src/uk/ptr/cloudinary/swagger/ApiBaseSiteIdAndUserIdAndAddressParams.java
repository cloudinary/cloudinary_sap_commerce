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
		@ApiImplicitParam(name = "firstName", value = "Customer's first name", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "lastName", value = "Customer's last name", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "titleCode", value = "Customer's title code. Customer's title code. For a list of codes, see /{baseSiteId}/titles resource", required = false, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "country.isocode", value = "Country isocode. This parameter is required and have influence on how rest of parameters are validated (e.g. if parameters are required : line1,line2,town,postalCode,region.isocode)", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "line1", value = "First part of address. If this parameter is required depends on country (usually it is required).", required = false, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "line2", value = "Second part of address. If this parameter is required depends on country (usually it is not required)", required = false, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "town", value = "Town name. If this parameter is required depends on country (usually it is required)", required = false, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "postalCode", value = "Postal code. Isocode for region. If this parameter is required depends on country.", required = false, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "region.isocode", value = "Isocode for region. If this parameter is required depends on country.", required = false, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "defaultAddress", value = "Parameter specifies if address should be default for customer.", required = false, dataType = "String", paramType = "query") })
public @interface ApiBaseSiteIdAndUserIdAndAddressParams
{
	//empty
}
