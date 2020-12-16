/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.filter;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Test suite for {@link uk.ptr.cloudinary.v2.filter.CartMatchingFilter}
 */
@UnitTest
public class CartMatchingFilterTest
{
	static final String DEFAULT_REGEXP = "^/[^/]+/users/[^/]+/carts/([^/]+)";
	static final String CURRENT_CART_ID = "current";
	static final String CART_GUID = "6d868385adf11f729b6e30acd2c44195ccd6e882";
	static final String CART_CODE = "00000001";

	private CartMatchingFilter cartMatchingFilter;
	@Mock
	private HttpServletRequest httpServletRequest;
	@Mock
	private HttpServletResponse httpServletResponse;
	@Mock
	private FilterChain filterChain;
	@Mock
	private CartLoaderStrategy cartLoaderStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		cartMatchingFilter = new CartMatchingFilter();
		cartMatchingFilter.setRegexp(DEFAULT_REGEXP);
		cartMatchingFilter.setCartLoaderStrategy(cartLoaderStrategy);
	}

	@Test
	public void testEmptyPathInfo() throws ServletException, IOException, CommerceCartRestorationException
	{
		given(httpServletRequest.getPathInfo()).willReturn(null);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(cartLoaderStrategy, never()).loadCart(anyString());
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testAnonymousNoCartInPath() throws ServletException, IOException, CommerceCartRestorationException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/addresses");

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(cartLoaderStrategy, never()).loadCart(anyString());
		verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
	}

	@Test
	public void testAmnonymousCartByGuid() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CART_GUID);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(cartLoaderStrategy, times(1)).loadCart(anyString(), anyBoolean());
		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test
	public void testAmnonymousCartByGuidLongPath() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/anonymous/carts/" + CART_GUID + "/long/path");

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(cartLoaderStrategy, times(1)).loadCart(anyString(), anyBoolean());
		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test
	public void testUserCartByCode() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/demo@customer.com/carts/" + CART_CODE);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(cartLoaderStrategy, times(1)).loadCart(anyString(), anyBoolean());
		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test
	public void testUserCartByCodeLongPath() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/demo@customer.com/carts/" + CART_CODE + "/long/path");

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(cartLoaderStrategy, times(1)).loadCart(anyString(), anyBoolean());
		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

	@Test
	public void testCurrentCart() throws ServletException, IOException
	{
		given(httpServletRequest.getPathInfo()).willReturn("/path/users/demo@customer.com/carts/" + CURRENT_CART_ID);

		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);

		verify(cartLoaderStrategy, times(1)).loadCart(anyString(), anyBoolean());
		cartMatchingFilter.doFilter(httpServletRequest, httpServletResponse, filterChain);
	}

}
