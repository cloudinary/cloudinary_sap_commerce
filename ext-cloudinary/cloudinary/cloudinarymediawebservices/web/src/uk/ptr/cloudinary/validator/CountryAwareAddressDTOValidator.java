/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.validator;

import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Implementation of {@link org.springframework.validation.Validator} that validate instances of {@link AddressWsDTO}.
 * <p>
 * The {@code CountryAwareAddressValidator} does not validate all fields itself, but delegates to other Validators
 * {@link #countrySpecificAddressWsDTOValidators}. {@code AddressValidator} uses the country.isocode field to select a
 * suitable validator for a specific country. If a matching validator cannot be found,
 * {@link #commonAddressWsDTOValidator} is used.
 */
public class CountryAwareAddressDTOValidator implements Validator
{
	private static final String COUNTRY_ISO = "country.isocode";
	private static final int MAX_ISOCODE_LENGTH = 2;
	private static final String FIELD_REQUIRED_AND_NOT_TOO_LONG_MESSAGE_ID = "field.requiredAndNotTooLong";
	private Validator commonAddressWsDTOValidator;
	private Map<String, Validator> countrySpecificAddressWsDTOValidators;

	@Override
	public boolean supports(final Class clazz)
	{
		return AddressWsDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final AddressWsDTO address = (AddressWsDTO) target;
		Assert.notNull(errors, "Errors object must not be null");

		if (address == null || address.getCountry() == null || address.getCountry().getIsocode() == null
				|| address.getCountry().getIsocode().length() > MAX_ISOCODE_LENGTH)
		{
			errors.rejectValue(COUNTRY_ISO, FIELD_REQUIRED_AND_NOT_TOO_LONG_MESSAGE_ID,
					new String[] { String.valueOf(MAX_ISOCODE_LENGTH) }, null);
			throw new WebserviceValidationException(errors);
		}

		Validator addressValidator = countrySpecificAddressWsDTOValidators.get(address.getCountry().getIsocode());

		if (addressValidator == null)
		{
			addressValidator = commonAddressWsDTOValidator;
		}
		addressValidator.validate(target, errors);
	}

	public Validator getCommonAddressValidator()
	{
		return commonAddressWsDTOValidator;
	}

	@Required
	public void setCommonAddressValidator(final Validator commonAddressValidator)
	{
		this.commonAddressWsDTOValidator = commonAddressValidator;
	}

	public Map<String, Validator> getCountrySpecificAddressValidators()
	{
		return countrySpecificAddressWsDTOValidators;
	}

	@Required
	public void setCountrySpecificAddressValidators(final Map<String, Validator> customAddressValidators)
	{
		this.countrySpecificAddressWsDTOValidators = customAddressValidators;
	}
}
