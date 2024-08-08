/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.populator;

import de.hybris.platform.commercefacades.address.data.AddressVerificationErrorField;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.validation.Errors;


/**
 * Populates Errors for AddressData
 */

public class AddressDataErrorsPopulator implements Populator<AddressVerificationResult<AddressVerificationDecision>, Errors>
{

	private final Map<String, String> attributeMappingMap;

	public AddressDataErrorsPopulator(final Map<String, String> attributeMappingMap)
	{
		this.attributeMappingMap = attributeMappingMap;
	}

	@Override
	public void populate(final AddressVerificationResult<AddressVerificationDecision> source, final Errors target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		for (Map.Entry<String, AddressVerificationErrorField> entry : source.getErrors().entrySet())
		{
			target.rejectValue(
					attributeMappingMap.containsKey(entry.getKey()) ? attributeMappingMap.get(entry.getKey()) : entry.getKey(),
					entry.getValue().isInvalid() ? "field.invalid" : "field.required", new Object[] { entry.getKey() }, "");
		}
	}
}
