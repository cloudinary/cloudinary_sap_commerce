/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.validator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Implementation of {@link org.springframework.validation.Validator} that validate instances of {@link AddressData}.
 * <p>
 * The {@code CountryAwareAddressValidator} does not validate all fields itself, but delegates to other Validators
 * {@link #countrySpecificAddressValidators}. {@code AddressValidator} uses the country.isocode field to select a
 * suitable validator for a specific country. If a matching validator cannot be found, {@link #commonAddressValidator}
 * is used.
 */
public class CountryAwareAddressValidator implements Validator
{
	private static final String COUNTRY_ISO = "country.isocode";
	private static final int MAX_ISOCODE_LENGTH = 2;
	private static final String FIELD_REQUIRED_AND_NOT_TOO_LONG_MESSAGE_ID = "field.requiredAndNotTooLong";
	private Validator commonAddressValidator;
	private Map<String, Validator> countrySpecificAddressValidators;

	@Override
	public boolean supports(final Class clazz)
	{
		return AddressData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final AddressData addressData = (AddressData) target;
		Assert.notNull(errors, "Errors object must not be null");

		if (addressData == null || addressData.getCountry() == null || addressData.getCountry().getIsocode() == null
				|| addressData.getCountry().getIsocode().length() > MAX_ISOCODE_LENGTH)
		{
			errors.rejectValue(COUNTRY_ISO, FIELD_REQUIRED_AND_NOT_TOO_LONG_MESSAGE_ID,
					new String[] { String.valueOf(MAX_ISOCODE_LENGTH) }, null);
			throw new WebserviceValidationException(errors);
		}

		Validator addressValidator = countrySpecificAddressValidators.get(addressData.getCountry().getIsocode());

		if (addressValidator == null)
		{
			addressValidator = commonAddressValidator;
		}
		addressValidator.validate(target, errors);
	}

	public Validator getCommonAddressValidator()
	{
		return commonAddressValidator;
	}

	@Required
	public void setCommonAddressValidator(final Validator commonAddressValidator)
	{
		this.commonAddressValidator = commonAddressValidator;
	}

	public Map<String, Validator> getCountrySpecificAddressValidators()
	{
		return countrySpecificAddressValidators;
	}

	@Required
	public void setCountrySpecificAddressValidators(final Map<String, Validator> customAddressValidators)
	{
		this.countrySpecificAddressValidators = customAddressValidators;
	}
}
