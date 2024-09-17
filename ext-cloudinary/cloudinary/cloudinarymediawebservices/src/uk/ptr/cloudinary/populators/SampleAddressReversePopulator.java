/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;

import uk.ptr.cloudinary.data.AddressData;


/**
 * Reverse populator for sample address
 */
public class SampleAddressReversePopulator implements Populator<AddressData, AddressModel>
{
	@Override
	public void populate(final AddressData addressData, final AddressModel addressModel) throws ConversionException
	{
		Assert.notNull(addressData, "Parameter source cannot be null.");
		Assert.notNull(addressModel, "Parameter target cannot be null.");

		addressModel.setBillingAddress(addressData.isBillingAddress());
		addressModel.setContactAddress(addressData.isDefaultAddress());
		addressModel.setShippingAddress(addressData.isShippingAddress());
		addressModel.setStreetname(addressData.getStreetname());
		addressModel.setStreetnumber(addressData.getStreetnumber());
		addressModel.setTown(addressData.getTown());
	}
}
