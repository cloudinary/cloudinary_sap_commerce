/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.filter;

import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;


/**
 * Filter that puts cart from the requested url into the session.
 */
public class CartMatchingFilter extends AbstractUrlMatchingFilter
{
	public static final String REFRESH_CART_PARAM = "refreshCart";
	private String regexp;
	private CartLoaderStrategy cartLoaderStrategy;
	private boolean cartRefreshedByDefault = true;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		if (matchesUrl(request, regexp))
		{
			final String cartId = getValue(request, regexp);
			cartLoaderStrategy.loadCart(cartId, shouldCartBeRefreshed(request));
		}

		filterChain.doFilter(request, response);
	}

	protected boolean shouldCartBeRefreshed(final HttpServletRequest request)
	{
		final String refreshParam = request.getParameter(REFRESH_CART_PARAM);
		return refreshParam == null ? isCartRefreshedByDefault() : Boolean.parseBoolean(refreshParam);
	}

	protected String getRegexp()
	{
		return regexp;
	}

	@Required
	public void setRegexp(final String regexp)
	{
		this.regexp = regexp;
	}

	public CartLoaderStrategy getCartLoaderStrategy()
	{
		return cartLoaderStrategy;
	}

	@Required
	public void setCartLoaderStrategy(final CartLoaderStrategy cartLoaderStrategy)
	{
		this.cartLoaderStrategy = cartLoaderStrategy;
	}

	public boolean isCartRefreshedByDefault()
	{
		return cartRefreshedByDefault;
	}

	public void setCartRefreshedByDefault(final boolean cartRefreshedByDefault)
	{
		this.cartRefreshedByDefault = cartRefreshedByDefault;
	}
}
