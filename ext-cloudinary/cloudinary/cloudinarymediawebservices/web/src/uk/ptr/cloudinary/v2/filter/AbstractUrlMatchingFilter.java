/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.filter;

import javax.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Abstract matching filter that helps parsing urls.
 */
public abstract class AbstractUrlMatchingFilter extends OncePerRequestFilter
{

	public static final String BASE_SITES_ENDPOINT_PATH = "/basesites";

	protected boolean matchesUrl(final HttpServletRequest request, final String regexp)
	{
		final Matcher matcher = getMatcher(request, regexp);
		return matcher.find();
	}

	protected String getBaseSiteValue(final HttpServletRequest request, final String regexp)
	{
		if (BASE_SITES_ENDPOINT_PATH.equals(getPath(request)))
		{
			return null;
		}

		final Matcher matcher = getMatcher(request, regexp);
		if (matcher.find())
		{
			return matcher.group().substring(1);
		}
		return null;
	}

	protected String getValue(final HttpServletRequest request, final String regexp)
	{
		final Matcher matcher = getMatcher(request, regexp);
		if (matcher.find())
		{
			return matcher.group(1);
		}
		return null;
	}

	protected String getValue(final HttpServletRequest request, final String regexp, final String groupName)
	{
		final Matcher matcher = getMatcher(request, regexp);
		if (matcher.find())
		{
			return matcher.group(groupName);
		}
		return null;
	}

	protected Matcher getMatcher(final HttpServletRequest request, final String regexp)
	{
		final Pattern pattern = Pattern.compile(regexp);
		final String path = getPath(request);
		return pattern.matcher(path);
	}

	protected String getPath(final HttpServletRequest request)
	{
		return StringUtils.defaultString(request.getPathInfo());
	}
}
