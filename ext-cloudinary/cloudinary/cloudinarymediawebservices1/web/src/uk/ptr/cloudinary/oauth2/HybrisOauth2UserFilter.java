/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.oauth2;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Filter sets current user by userService depending on current principal. <br>
 * This should happen only when there is a customer context. Anonymous credentials are also applicable, because special
 * 'anonymous' user is available for that purpose. Customer context is not available during client credential flow.
 */
public class HybrisOauth2UserFilter implements Filter
{
	private static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
	private static final String ROLE_CUSTOMERGROUP = "ROLE_CUSTOMERGROUP";
	private static final String ROLE_CUSTOMERMANAGERGROUP = "ROLE_CUSTOMERMANAGERGROUP";

	@Resource(name = "userService")
	private UserService userService;


	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException
	{
		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (containsRole(auth, ROLE_ANONYMOUS) || containsRole(auth, ROLE_CUSTOMERGROUP)
				|| containsRole(auth, ROLE_CUSTOMERMANAGERGROUP))
		{
			final UserModel userModel = userService.getUserForUID((String) auth.getPrincipal());
			userService.setCurrentUser(userModel);
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy()
	{
		// YTODO Auto-generated method stub
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException
	{
		// YTODO Auto-generated method stub
	}

	protected static boolean containsRole(final Authentication auth, final String role)
	{
		for (final GrantedAuthority ga : auth.getAuthorities())
		{
			if (ga.getAuthority().equals(role))
			{
				return true;
			}
		}
		return false;
	}
}
