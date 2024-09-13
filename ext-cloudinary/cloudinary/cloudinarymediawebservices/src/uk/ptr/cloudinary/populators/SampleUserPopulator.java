/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import uk.ptr.cloudinary.data.AddressData;
import uk.ptr.cloudinary.data.UserData;



/**
 *
 *
 */
public class SampleUserPopulator implements Populator<UserModel, UserData>
{
	private Converter<AddressModel, AddressData> addressConverter;

	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	public Converter<AddressModel, AddressData> getAddressConverter()
	{
		return this.addressConverter;
	}

	@Override
	public void populate(final UserModel source, final UserData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setDescription(source.getDescription());
		if (source.getName() != null)
		{
			final String[] names = source.getName().split(" ", 2);
			target.setFirstName(names[0]);
			if (names.length > 1)
			{
				target.setLastName(names[1]);
			}
		}

		final List<AddressData> addresses = source.getAddresses().stream().map(a -> getAddressConverter().convert(a))
				.collect(Collectors.toList());
		target.setAddresses(addresses);

		addresses.stream().filter(a -> a.isBillingAddress()).findFirst().ifPresent(a -> target.setDefaultBillingAddress(a));
		addresses.stream().filter(a -> a.isShippingAddress()).findFirst().ifPresent(a -> target.setDefaultShippingAddress(a));
	}
}
