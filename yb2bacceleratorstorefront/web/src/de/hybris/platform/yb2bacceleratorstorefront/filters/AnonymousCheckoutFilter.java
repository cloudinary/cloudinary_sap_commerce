/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.filters;

import de.hybris.platform.yb2bacceleratorstorefront.security.GuestCheckoutCartCleanStrategy;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;


public class AnonymousCheckoutFilter extends OncePerRequestFilter
{

	private GuestCheckoutCartCleanStrategy guestCheckoutCartCleanStrategy;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		getGuestCheckoutCartCleanStrategy().cleanGuestCart(request);
		filterChain.doFilter(request, response);
	}

	public GuestCheckoutCartCleanStrategy getGuestCheckoutCartCleanStrategy()
	{
		return guestCheckoutCartCleanStrategy;
	}

	public void setGuestCheckoutCartCleanStrategy(final GuestCheckoutCartCleanStrategy guestCheckoutCartCleanStrategy)
	{
		this.guestCheckoutCartCleanStrategy = guestCheckoutCartCleanStrategy;
	}

}
