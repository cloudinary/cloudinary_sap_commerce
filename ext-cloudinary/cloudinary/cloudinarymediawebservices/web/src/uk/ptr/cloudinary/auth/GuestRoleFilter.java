/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.auth;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * This filter should be used after spring security filters and it is responsible for setting current authentication as
 * guest when user decided to do the checkout as a guest. During the guest checkout the userService gets current user as
 * 'anonymous', but cartService returns dedicated user.
 */
public class GuestRoleFilter extends OncePerRequestFilter
{
	private UserService userService;

	private CartService cartService;

	private AuthenticationEventPublisher authenticationEventPublisher;

	private String guestRole;

	@Override
	protected void doFilterInternal(final HttpServletRequest httpservletrequest, final HttpServletResponse httpservletresponse,
			final FilterChain filterchain) throws ServletException, IOException
	{
		final Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

		if (userService.isAnonymousUser(userService.getCurrentUser()) && cartService.hasSessionCart())
		{
			final UserModel um = cartService.getSessionCart().getUser();
			if (um != null && CustomerModel.class.isAssignableFrom(um.getClass()))
			{
				final CustomerModel cm = (CustomerModel) um;

				if (isGuest(cm))
				{
					if (currentAuth == null)
					{
						processAuthentication(cm.getUid());
					}
					else if (!currentAuth.getClass().equals(GuestAuthenticationToken.class))
					{
						processAuthentication(cm.getUid());
					}
					else if (!cm.getUid().equals(currentAuth.getName()))
					{
						processAuthentication(cm.getUid());
					}
				}
			}
		}
		filterchain.doFilter(httpservletrequest, httpservletresponse);
	}

	protected void processAuthentication(final String uid)
	{
		final Authentication authentication = createGuestAuthentication(uid);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		authenticationEventPublisher.publishAuthenticationSuccess(authentication);
	}

	protected Authentication createGuestAuthentication(final String uid)
	{
		final Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		grantedAuthorities.add(new SimpleGrantedAuthority(this.guestRole));
		return new GuestAuthenticationToken(uid, grantedAuthorities);
	}

	protected boolean isGuest(final CustomerModel cm)
	{
		if (cm == null || cm.getType() == null)
		{
			return false;
		}
		if (cm.getType().toString().equals(CustomerType.GUEST.getCode()))
		{
			return true;
		}
		return false;
	}

	public AuthenticationEventPublisher getAuthenticationEventPublisher()
	{
		return authenticationEventPublisher;
	}

	@Required
	public void setAuthenticationEventPublisher(final AuthenticationEventPublisher authenticationEventPublisher)
	{
		this.authenticationEventPublisher = authenticationEventPublisher;
	}

	public String getGuestRole()
	{
		return guestRole;
	}

	@Required
	public void setGuestRole(final String guestRole)
	{
		this.guestRole = guestRole;
	}

	public UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

}
