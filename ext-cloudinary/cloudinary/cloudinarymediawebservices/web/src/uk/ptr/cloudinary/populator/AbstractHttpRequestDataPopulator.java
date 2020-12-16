/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.populator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


public abstract class AbstractHttpRequestDataPopulator
{

	protected String updateStringValueFromRequest(final HttpServletRequest request, final String paramName,
			final String defaultValue)
	{
		final String requestParameterValue = getRequestParameterValue(request, paramName);
		if ("".equals(requestParameterValue))
		{
			return null;
		}
		return StringUtils.defaultIfBlank(requestParameterValue, defaultValue);
	}

	protected boolean updateBooleanValueFromRequest(final HttpServletRequest request, final String paramName,
			final boolean defaultValue)
	{
		final String booleanString = updateStringValueFromRequest(request, paramName, null);
		if (booleanString == null)
		{
			return defaultValue;
		}
		return Boolean.parseBoolean(booleanString);
	}

	protected Double updateDoubleValueFromRequest(final HttpServletRequest request, final String paramName,
			final Double defaultValue)
	{
		final String booleanString = updateStringValueFromRequest(request, paramName, null);
		if (booleanString == null)
		{
			return defaultValue;
		}
		return Double.valueOf(booleanString);
	}

	protected String getRequestParameterValue(final HttpServletRequest request, final String paramName)
	{
		return request.getParameter(paramName);
	}
}
