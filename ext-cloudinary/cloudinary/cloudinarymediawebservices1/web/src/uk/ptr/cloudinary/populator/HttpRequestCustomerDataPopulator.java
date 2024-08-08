/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.populator;

import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Populates {@link CustomerData} instance based on http request parameters:<br>
 * Does not populate address {@link AddressData} information
 * <ul>
 * <li>titleCode</li>
 * <li>firstName</li>
 * <li>lastName</li>
 * <li>language</li>
 * <li>currency</li>
 * </ul>
 */
@Component("httpRequestCustomerDataPopulator")
public class HttpRequestCustomerDataPopulator implements Populator<HttpServletRequest, CustomerData>
{

	private static final String TITLECODE = "titleCode";
	private static final String FIRSTNAME = "firstName";
	private static final String LASTNAME = "lastName";
	private static final String LANGUAGE = "language";
	private static final String CURRENCY = "currency";

	@Override
	public void populate(final HttpServletRequest source, final CustomerData target) throws ConversionException //NOSONAR
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setTitleCode(StringUtils.defaultString(source.getParameter(TITLECODE), target.getTitleCode()));
		target.setFirstName(StringUtils.defaultString(source.getParameter(FIRSTNAME), target.getFirstName()));
		target.setLastName(StringUtils.defaultString(source.getParameter(LASTNAME), target.getLastName()));

		if (source.getParameter(CURRENCY) != null)
		{
			final CurrencyData currency = new CurrencyData();
			currency.setIsocode(source.getParameter(CURRENCY));
			target.setCurrency(currency);
		}

		if (source.getParameter(LANGUAGE) != null)
		{
			final LanguageData language = new LanguageData();
			language.setIsocode(source.getParameter(LANGUAGE));
			target.setLanguage(language);
		}

	}

}
