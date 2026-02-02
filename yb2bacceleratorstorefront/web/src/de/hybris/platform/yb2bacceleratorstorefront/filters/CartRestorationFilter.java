/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.filters;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yb2bacceleratorstorefront.security.cookie.CartRestoreCookieGenerator;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Filter that the restores the user's cart. This is a spring configured filter that is executed by the
 * PlatformFilterChain.
 */
public class CartRestorationFilter extends OncePerRequestFilter	
{
	private static final Logger LOG = Logger.getLogger(CartRestorationFilter.class);

	private CartRestoreCookieGenerator cartRestoreCookieGenerator;
	private CartService cartService;
	private CartFacade cartFacade;
	private BaseSiteService baseSiteService;
	private UserService userService;
	private SessionService sessionService;

	@Override
	public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws IOException, ServletException
	{
		if (getUserService().isAnonymousUser(getUserService().getCurrentUser()))
		{
			processAnonymousUser(request, response);
		}
		else
		{
			restoreCartWithNoCode();
		}

		filterChain.doFilter(request, response);
	}

	protected void restoreCartWithNoCode() {
		if ((!getCartService().hasSessionCart() && getSessionService().getAttribute(WebConstants.CART_RESTORATION) == null)
                || (getCartService().hasSessionCart() && !getBaseSiteService().getCurrentBaseSite().equals(
                        getBaseSiteService().getBaseSiteForUID(getCartService().getSessionCart().getSite().getUid()))))
        {
            getSessionService().setAttribute(WebConstants.CART_RESTORATION_SHOW_MESSAGE, Boolean.TRUE);
            try
            {
                getSessionService().setAttribute(WebConstants.CART_RESTORATION, getCartFacade().restoreSavedCart(null));
            }
            catch (final CommerceCartRestorationException e)
            {
				if (LOG.isDebugEnabled())
				{
					LOG.debug(e);
				}
                getSessionService().setAttribute(WebConstants.CART_RESTORATION, WebConstants.CART_RESTORATION_ERROR_STATUS);
            }
        }
	}

	protected void processAnonymousUser(final HttpServletRequest request, final HttpServletResponse response) {
		if (getCartService().hasSessionCart()
                && getBaseSiteService().getCurrentBaseSite().equals(
                        getBaseSiteService().getBaseSiteForUID(getCartService().getSessionCart().getSite().getUid())))
        {
            final String guid = getCartService().getSessionCart().getGuid();

            if (!StringUtils.isEmpty(guid))
            {
                getCartRestoreCookieGenerator().addCookie(response, guid);
            }
        }
        else if (request.getSession().isNew()
                || (getCartService().hasSessionCart() && !getBaseSiteService().getCurrentBaseSite().equals(
                        getBaseSiteService().getBaseSiteForUID(getCartService().getSessionCart().getSite().getUid()))))
        {
			processRestoration(request);
        }
	}

	protected void processRestoration(final HttpServletRequest request) {
		String cartGuid = null;

		if (request.getCookies() != null)
        {
            final String anonymousCartCookieName = getCartRestoreCookieGenerator().getCookieName();

            for (final Cookie cookie : request.getCookies())
            {
                if (anonymousCartCookieName != null && anonymousCartCookieName.equals(cookie.getName()))
                {
                    cartGuid = cookie.getValue();
                    break;
                }
            }
        }

		if (!StringUtils.isEmpty(cartGuid))
        {
            getSessionService().setAttribute(WebConstants.CART_RESTORATION_SHOW_MESSAGE, Boolean.TRUE);
            try
            {
                getSessionService().setAttribute(WebConstants.CART_RESTORATION, getCartFacade().restoreSavedCart(cartGuid));
            }
            catch (final CommerceCartRestorationException e)
            {
				if (LOG.isDebugEnabled())
				{
					LOG.debug(e);
				}
                getSessionService().setAttribute(WebConstants.CART_RESTORATION_ERROR_STATUS,
                        WebConstants.CART_RESTORATION_ERROR_STATUS);
            }
        }
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected CartRestoreCookieGenerator getCartRestoreCookieGenerator()
	{
		return cartRestoreCookieGenerator;
	}

	public void setCartRestoreCookieGenerator(final CartRestoreCookieGenerator cartRestoreCookieGenerator)
	{
		this.cartRestoreCookieGenerator = cartRestoreCookieGenerator;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}
}
