/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.customer.populator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Extended populator implementation for {@link de.hybris.platform.core.model.user.CustomerModel} as source and
 * {@link de.hybris.platform.commercefacades.user.data.CustomerData} as target type.
 */
public class ExtendedCustomerPopulator implements Populator<CustomerModel, CustomerData>
{
	private Converter<AddressModel, AddressData> addressConverter;

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	@Override
	public void populate(final CustomerModel source, final CustomerData target) throws ConversionException //NOSONAR
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source.getTitle() != null)
		{
			target.setTitle(source.getTitle().getName());
		}

		if (source.getDefaultPaymentAddress() != null)
		{
			target.setDefaultBillingAddress(getAddressConverter().convert(source.getDefaultPaymentAddress()));
		}
		if (source.getDefaultShipmentAddress() != null)
		{
			target.setDefaultShippingAddress(getAddressConverter().convert(source.getDefaultShipmentAddress()));
		}
	}
}
