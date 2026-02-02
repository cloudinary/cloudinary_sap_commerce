/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.security.impl;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.DefaultRedirectStrategy;


/**
 * A redirect strategy used in
 * {@link de.hybris.platform.acceleratorstorefrontcommons.security.StorefrontAuthenticationSuccessHandler} to handle
 * express checkout case
 */
public class DefaultCommerceRedirectStrategy extends DefaultRedirectStrategy
{

	private CheckoutFlowFacade checkoutFlowFacade;
	private String expressTargetUrl;

	@Override
	public void sendRedirect(final HttpServletRequest request, final HttpServletResponse response, final String url)
			throws IOException
	{
		String redirectUrl = url;

		if (checkoutFlowFacade.isExpressCheckoutEnabledForStore()
				&& StringUtils.isNotEmpty(request.getParameter("expressCheckoutEnabled")))
		{
			redirectUrl = getExpressTargetUrl();
		}
		super.sendRedirect(request, response, redirectUrl);
	}


	protected String getExpressTargetUrl()
	{
		return expressTargetUrl;
	}

	public void setExpressTargetUrl(final String expressTargetUrl)
	{
		this.expressTargetUrl = expressTargetUrl;
	}

	protected CheckoutFlowFacade getCheckoutFlowFacade()
	{
		return checkoutFlowFacade;
	}

	public void setCheckoutFlowFacade(final CheckoutFlowFacade checkoutFlowFacade)
	{
		this.checkoutFlowFacade = checkoutFlowFacade;
	}
}
