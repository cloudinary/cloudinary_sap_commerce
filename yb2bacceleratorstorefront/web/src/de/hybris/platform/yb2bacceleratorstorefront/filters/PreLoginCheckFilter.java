/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.filters;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.security.BruteForceAttackCounter;
import de.hybris.platform.util.Config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;


public class PreLoginCheckFilter extends GenericFilterBean
{
	private BruteForceAttackCounter bruteForceAttackCounter;

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	private String defaultErrorPage;

	private String loginProcessingUrl;

	private RequestMatcher loginRequestMatcher;

	@Override
	protected void initFilterBean() throws ServletException
	{
		this.loginRequestMatcher = new AntPathRequestMatcher(loginProcessingUrl);
		super.initFilterBean();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{

		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		if (Config.getBoolean(WebConstants.OTP_CUSTOMER_LOGIN_ENABLED, false) && this.loginRequestMatcher.matches(httpRequest))
		{
			final String username = httpRequest.getParameter(WebConstants.OTP_USER_NAME);
			final String lastOtpUserName = httpRequest.getParameter(WebConstants.LAST_OTP_USER_NAME);

			if (!StringUtils.isBlank(lastOtpUserName) && !lastOtpUserName.equals(username))
			{
				bruteForceAttackCounter.registerLoginFailure(username);
				httpRequest.getSession().setAttribute(WebConstants.SPRING_SECURITY_LAST_USERNAME, username);
				httpRequest.getSession().setAttribute(WebConstants.OTP_USERNAME_CHANGED, true);
				httpRequest.getSession().setAttribute(WebConstants.OTP_TOKEN_ID, request.getParameter(WebConstants.J_USERNAME));
				this.redirectStrategy.sendRedirect(httpRequest, httpResponse, defaultErrorPage);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	public void setBruteForceAttackCounter(final BruteForceAttackCounter bruteForceAttackCounter)
	{
		this.bruteForceAttackCounter = bruteForceAttackCounter;
	}

	public void setDefaultErrorPage(final String defaultErrorPage)
	{
		this.defaultErrorPage = defaultErrorPage;
	}

	public void setLoginProcessingUrl(final String loginProcessingUrl)
	{
		this.loginProcessingUrl = loginProcessingUrl;
	}

}
