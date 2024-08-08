/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;

import org.springframework.util.Assert;

import uk.ptr.cloudinary.data.AddressData;


public class SampleAddressPopulator implements Populator<AddressModel, AddressData>
{

	@Override
	public void populate(final AddressModel source, final AddressData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setBillingAddress(getBoolean(source.getBillingAddress(), false));
		target.setDefaultAddress(getBoolean(source.getContactAddress(), false));
		target.setShippingAddress(getBoolean(source.getShippingAddress(), false));
		target.setStreetname(source.getStreetname());
		target.setStreetnumber(source.getStreetnumber());
		target.setTown(source.getTown());

		target.setFormattedAddress(getFormattedAddress(source));
	}

	protected static boolean getBoolean(final Boolean val, final boolean def)
	{
		return val != null ? val.booleanValue() : def;
	}

	protected String getFormattedAddress(final AddressModel source)
	{
		return source.getTown() + " " + source.getStreetname() + " " + source.getStreetnumber();
	}
}
