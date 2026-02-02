/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.security.cookie;

import de.hybris.platform.site.BaseSiteService;

import org.apache.commons.lang3.StringUtils;


public class CustomerLocationCookieGenerator extends EnhancedCookieGenerator
{
	public static final String LOCATION_SEPARATOR = "%7C";
	public static final String LATITUDE_LONGITUDE_SEPARATOR = "%2C";

	private BaseSiteService baseSiteService;

	@Override
	public String getCookieName()
	{
		return StringUtils.deleteWhitespace(getBaseSiteService().getCurrentBaseSite().getUid()) + "-customerLocation";
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

}
