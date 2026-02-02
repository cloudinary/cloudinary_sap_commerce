/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.filters;

import de.hybris.platform.acceleratorfacades.customerlocation.CustomerLocationFacade;
import de.hybris.platform.acceleratorservices.store.data.UserLocationData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.yb2bacceleratorstorefront.security.cookie.CustomerLocationCookieGenerator;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


public class CustomerLocationRestorationFilter extends OncePerRequestFilter
{

	private CustomerLocationFacade customerLocationFacade;
	private CustomerLocationCookieGenerator customerLocationCookieGenerator;


	@Override
	public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		if (getCustomerLocationFacade().getUserLocationData() == null)
		{
			final Cookie[] cookies = request.getCookies();

			if (cookies != null)
			{
				setUserLocationDataFromCookies(cookies);
			}
		}

		filterChain.doFilter(request, response);
	}

	protected void setUserLocationDataFromCookies(final Cookie[] cookies)
	{
		for (final Cookie cookie : cookies)
		{
			if (StringUtils.equals(getCustomerLocationCookieGenerator().getCookieName(), cookie.getName()))
			{
				final UserLocationData cookieUserLocationData = decipherUserLocationData(StringUtils.remove(cookie.getValue(), "\""));
				getCustomerLocationFacade().setUserLocationData(cookieUserLocationData);
				break;
			}
		}
	}

	protected UserLocationData decipherUserLocationData(final String customerLocationString)
	{
		final UserLocationData userLocationData = new UserLocationData();
		final String searchTerm = StringUtils.substringBefore(customerLocationString,
				CustomerLocationCookieGenerator.LOCATION_SEPARATOR);
		final String latitudeAndLongitude = StringUtils.substringAfter(customerLocationString,
				CustomerLocationCookieGenerator.LOCATION_SEPARATOR);

		if (StringUtils.isNotEmpty(latitudeAndLongitude))
		{
			final GeoPoint geoPoint = new GeoPoint();
			geoPoint.setLatitude(Double.parseDouble(
					StringUtils.substringBefore(latitudeAndLongitude, CustomerLocationCookieGenerator.LATITUDE_LONGITUDE_SEPARATOR)));
			geoPoint.setLongitude(Double.parseDouble(
					StringUtils.substringAfter(latitudeAndLongitude, CustomerLocationCookieGenerator.LATITUDE_LONGITUDE_SEPARATOR)));
			userLocationData.setPoint(geoPoint);
		}

		userLocationData.setSearchTerm(searchTerm);
		return userLocationData;
	}

	protected CustomerLocationFacade getCustomerLocationFacade()
	{
		return customerLocationFacade;
	}

	public void setCustomerLocationFacade(final CustomerLocationFacade customerLocationFacade)
	{
		this.customerLocationFacade = customerLocationFacade;
	}

	protected CustomerLocationCookieGenerator getCustomerLocationCookieGenerator()
	{
		return customerLocationCookieGenerator;
	}

	public void setCustomerLocationCookieGenerator(final CustomerLocationCookieGenerator customerLocationCookieGenerator)
	{
		this.customerLocationCookieGenerator = customerLocationCookieGenerator;
	}
}
