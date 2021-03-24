/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.filter;

import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.enums.UserTaxGroup;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Request filter that sets session attributes for Europe1 price factory.
 */
public class Europe1AttributesFilter extends OncePerRequestFilter
{
	private BaseStoreService baseStoreService;
	private SessionService sessionService;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		setUserTaxGroupAttribute();
		filterChain.doFilter(request, response);
	}

	protected void setUserTaxGroupAttribute()
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		if (currentBaseStore != null)
		{
			final UserTaxGroup taxGroup = currentBaseStore.getTaxGroup();
			if (taxGroup != null)
			{
				getSessionService().setAttribute(Europe1Constants.PARAMS.UTG, taxGroup);
			}
		}
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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
}
