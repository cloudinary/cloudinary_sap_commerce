/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.constants;

@SuppressWarnings({ "deprecation", "squid:CallToDeprecatedMethod" })
public class YcommercewebservicesConstants extends GeneratedYcommercewebservicesConstants
{
	public static final String MODULE_NAME = "cloudinarymediawebservices";
	public static final String MODULE_WEBROOT = ("y" + "commercewebservices").equals(MODULE_NAME) ? "rest" : MODULE_NAME;
	public static final String CONTINUE_URL = "session_continue_url";
	public static final String CONTINUE_URL_PAGE = "session_continue_url_page";
	public static final String ENUM_VALUES_SEPARATOR = ",";

	public static final String HTTP_REQUEST_PARAM_LANGUAGE = "lang";
	public static final String HTTP_REQUEST_PARAM_CURRENCY = "curr";

	public static final String ROOT_CONTEXT_PROPERTY = "commercewebservices.rootcontext";
	// <v1-api>
	public static final String V1_ROOT_CONTEXT = "/" + MODULE_WEBROOT + "/v1";
	// </v1-api>
	public static final String V2_ROOT_CONTEXT = "/" + MODULE_WEBROOT + "/v2";
	public static final String URL_SPECIAL_CHARACTERS_PROPERTY = "commercewebservices.url.special.characters";
	public static final String DEFAULT_URL_SPECIAL_CHARACTERS = "?,/";
	public static final String LOCATION = "Location";
	public static final String SLASH = "/";

	private YcommercewebservicesConstants()
	{
		//empty
	}
}
