/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.filter;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import uk.ptr.cloudinary.context.ContextInformationLoader;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Filter sets currency in session context basing on request parameters:<br>
 * <ul>
 * <li><b>curr</b> - set current {@link CurrencyModel}</li>
 * </ul>
 */
public class SessionCurrencyFilter extends OncePerRequestFilter
{
	private ContextInformationLoader contextInformationLoader;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		getContextInformationLoader().setCurrencyFromRequest(request);

		filterChain.doFilter(request, response);
	}

	protected ContextInformationLoader getContextInformationLoader()
	{
		return contextInformationLoader;
	}

	@Required
	public void setContextInformationLoader(final ContextInformationLoader contextInformationLoader)
	{
		this.contextInformationLoader = contextInformationLoader;
	}

}
