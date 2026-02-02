/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.security;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.security.BruteForceAttackCounter;
import de.hybris.platform.util.Config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;


public class LoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler
{
	private BruteForceAttackCounter bruteForceAttackCounter;

	@Override
	public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
			final AuthenticationException exception) throws IOException, ServletException
	{
		if (Config.getBoolean(WebConstants.OTP_CUSTOMER_LOGIN_ENABLED, false))
		{
			// Register brute attacks
			bruteForceAttackCounter.registerLoginFailure(request.getParameter(WebConstants.OTP_USER_NAME));

			// Store the otpUserName in the session
			request.getSession().setAttribute(WebConstants.SPRING_SECURITY_LAST_USERNAME, request.getParameter(WebConstants.OTP_USER_NAME));
			request.getSession().setAttribute(WebConstants.OTP_TOKEN_ID, request.getParameter(WebConstants.J_USERNAME));
		}
		else
		{
			// Register brute attacks
			bruteForceAttackCounter.registerLoginFailure(request.getParameter(WebConstants.J_USERNAME));

			// Store the j_username in the session
			request.getSession().setAttribute(WebConstants.SPRING_SECURITY_LAST_USERNAME, request.getParameter(WebConstants.J_USERNAME));
		}
		super.onAuthenticationFailure(request, response, exception);
	}



	protected BruteForceAttackCounter getBruteForceAttackCounter()
	{
		return bruteForceAttackCounter;
	}

	public void setBruteForceAttackCounter(final BruteForceAttackCounter bruteForceAttackCounter)
	{
		this.bruteForceAttackCounter = bruteForceAttackCounter;
	}
}
