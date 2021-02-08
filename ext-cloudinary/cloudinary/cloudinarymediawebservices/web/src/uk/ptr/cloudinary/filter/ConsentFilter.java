/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.filter;

import de.hybris.platform.commercefacades.consent.AnonymousConsentFacade;
import de.hybris.platform.commercefacades.consent.CustomerConsentDataStrategy;
import de.hybris.platform.commercefacades.consent.data.AnonymousConsentData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.site.BaseSiteService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import static de.hybris.platform.commercewebservicescommons.constants.CommercewebservicescommonsConstants.ANONYMOUS_CONSENT_HEADER;
import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Filter which handle consent for customers.<br/>
 * For anonymous users it reads consent cookie and based on it sets proper consent in session.<br/>
 * For customers it reads consent from database and it sets proper consent in session.
 */
public class ConsentFilter extends OncePerRequestFilter
{
	private static final Logger LOG = LoggerFactory.getLogger(ConsentFilter.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private UserFacade userFacade;
	private BaseSiteService baseSiteService;
	private AnonymousConsentFacade anonymousConsentFacade;
	private CustomerConsentDataStrategy customerConsentDataStrategy;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		handleConsents(request, response);
		filterChain.doFilter(request, response);
	}

	void handleConsents(final HttpServletRequest request, final HttpServletResponse response)
	{
		if (getBaseSiteService().getCurrentBaseSite() == null)
		{
			LOG.debug("current base site is not set, nothing to filter");
			return;
		}

		if (getUserFacade().isAnonymousUser())
		{
			final Supplier<List<AnonymousConsentData>> consentReader = () -> readConsentHeaders(request);
			final Consumer<List<AnonymousConsentData>> consentWriter = consents -> writeConsentHeaders(response, consents);

			getAnonymousConsentFacade().synchronizeAnonymousConsents(consentReader, consentWriter);
		}
		else
		{
			getCustomerConsentDataStrategy().populateCustomerConsentDataInSession();
		}
	}

	protected List<AnonymousConsentData> readConsentHeaders(final HttpServletRequest request)
	{
		final String rawHeader = request.getHeader(ANONYMOUS_CONSENT_HEADER);

		if (StringUtils.isNotEmpty(rawHeader))
		{
			try
			{
				final String headerValue = URLDecoder.decode(rawHeader, UTF_8);
				return Arrays.asList(mapper.readValue(headerValue, AnonymousConsentData[].class));
			}
			catch (final IOException e)
			{
				LOG.error("IOException occurred while reading the header", e);
			}
		}

		return Collections.emptyList();
	}

	protected void writeConsentHeaders(final HttpServletResponse response, final List<AnonymousConsentData> consents)
	{
		try
		{
			final String headerValue = mapper.writeValueAsString(consents);
			response.setHeader(ANONYMOUS_CONSENT_HEADER, URLEncoder.encode(headerValue, UTF_8));
		}
		catch (final IOException e)
		{
			LOG.error("IOException occurred while writing the header to the Servlet Response", e);
		}
	}

	protected UserFacade getUserFacade()
	{
		return userFacade;
	}

	@Required
	public void setUserFacade(final UserFacade userFacade)
	{
		this.userFacade = userFacade;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected AnonymousConsentFacade getAnonymousConsentFacade()
	{
		return anonymousConsentFacade;
	}

	@Required
	public void setAnonymousConsentFacade(final AnonymousConsentFacade anonymousConsentFacade)
	{
		this.anonymousConsentFacade = anonymousConsentFacade;
	}

	protected CustomerConsentDataStrategy getCustomerConsentDataStrategy()
	{
		return customerConsentDataStrategy;
	}

	@Required
	public void setCustomerConsentDataStrategy(final CustomerConsentDataStrategy customerConsentDataStrategy)
	{
		this.customerConsentDataStrategy = customerConsentDataStrategy;
	}
}
