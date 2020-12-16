/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.filter;

import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


/**
 * Filter that puts user from the requested url into the session.
 */
public class UserMatchingFilter extends AbstractUrlMatchingFilter
{
	public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
	public static final String ROLE_CUSTOMERGROUP = "ROLE_CUSTOMERGROUP";
	public static final String ROLE_CUSTOMERMANAGERGROUP = "ROLE_CUSTOMERMANAGERGROUP";
	public static final String ROLE_TRUSTED_CLIENT = "ROLE_TRUSTED_CLIENT";
	private static final String CURRENT_USER = "current";
	private static final String ANONYMOUS_USER = "anonymous";
	private static final String ACTING_USER_UID = "ACTING_USER_UID";
	private static final Logger LOG = LoggerFactory.getLogger(UserMatchingFilter.class);

	private String regexp;
	private UserService userService;
	private SessionService sessionService;
	private UserMatchingService userMatchingService;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		final Authentication auth = getAuth();
		if (hasRole(ROLE_CUSTOMERGROUP, auth) || hasRole(ROLE_CUSTOMERMANAGERGROUP, auth))
		{
			getSessionService().setAttribute(ACTING_USER_UID, auth.getPrincipal());
		}

		final String userID = getValue(request, regexp);
		if (userID == null)
		{
			if (hasRole(ROLE_CUSTOMERGROUP, auth) || hasRole(ROLE_CUSTOMERMANAGERGROUP, auth))
			{
				setCurrentUser((String) auth.getPrincipal());
			}
			else
			{
				// fallback to anonymous
				setCurrentUser(userService.getAnonymousUser());
			}
		}
		else if (userID.equals(ANONYMOUS_USER) && !hasRole(ROLE_CUSTOMERGROUP, auth))
		{
			setCurrentUser(userService.getAnonymousUser());
		}
		else if (hasRole(ROLE_TRUSTED_CLIENT, auth) || hasRole(ROLE_CUSTOMERMANAGERGROUP, auth))
		{
			setCurrentUser(userID);
		}
		else if (hasRole(ROLE_CUSTOMERGROUP, auth))
		{
			setCurrentUserForCustomerGroupRole((String) auth.getPrincipal(), userID);
		}
		else
		{
			// could not match any authorized role
			throw new AccessDeniedException("Access is denied");
		}

		filterChain.doFilter(request, response);
	}

	protected boolean hasRole(final String role, final Authentication auth)
	{
		if (auth != null)
		{
			for (final GrantedAuthority ga : auth.getAuthorities())
			{
				if (ga.getAuthority().equals(role))
				{
					return true;
				}
			}
		}
		return false;
	}

	protected void setCurrentUser(final String id)
	{
		try
		{
			final UserModel user = userMatchingService.getUserByProperty(id, UserModel.class);
			setCurrentUser(user);
		}
		catch (final UnknownIdentifierException ex)
		{
			LOG.debug(ex.getMessage(), ex);
			throw ex;
		}
	}

	protected void setCurrentUser(final UserModel user)
	{
		userService.setCurrentUser(user);
	}

	protected void setCurrentUserForCustomerGroupRole(final String principal, final String userID)
	{

		if (userID.equals(CURRENT_USER))
		{
			setCurrentUser(principal);
		}
		else
		{
			setCurrentUser(
					getUserForValidProperty(principal, userID).orElseThrow(() -> new AccessDeniedException("Access is denied")));
		}
	}

	protected Optional<UserModel> getUserForValidProperty(final String principal, final String propertyValue)
	{
		try
		{
			final UserModel user = userMatchingService.getUserByProperty(propertyValue, UserModel.class);
			if (principal.equals(user.getUid()))
			{
				return Optional.of(user);
			}
		}
		catch (final UnknownIdentifierException ex)
		{
			LOG.debug(ex.getMessage(), ex);
		}
		return Optional.empty();
	}

	protected Authentication getAuth()
	{
		return SecurityContextHolder.getContext().getAuthentication();
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

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected UserMatchingService getUserMatchingService()
	{
		return userMatchingService;
	}

	@Required
	public void setUserMatchingService(final UserMatchingService userMatchingService)
	{
		this.userMatchingService = userMatchingService;
	}
}
