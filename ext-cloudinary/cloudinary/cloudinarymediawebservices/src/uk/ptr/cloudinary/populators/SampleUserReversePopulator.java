/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import uk.ptr.cloudinary.data.AddressData;
import uk.ptr.cloudinary.data.UserData;


/**
 * Reverse populator for sample user
 */
public class SampleUserReversePopulator implements Populator<UserData, UserModel>
{
	private Converter<AddressData, AddressModel> addressReverseConverter;

	/**
	 * Constructor for user reverse populator
	 *
	 * @param addressReverseConverter - address reverse converter, that converts AddressData to AddressModel
	 */
	public SampleUserReversePopulator(final Converter<AddressData, AddressModel> addressReverseConverter)
	{
		this.addressReverseConverter = addressReverseConverter;
	}

	@Override
	public void populate(final UserData userData, final UserModel userModel) throws ConversionException
	{
		Assert.notNull(userData, "Parameter source cannot be null.");
		Assert.notNull(userModel, "Parameter target cannot be null.");

		userModel.setDescription(userData.getDescription());
		userModel.setName(convertToName(userData.getFirstName(), userData.getLastName()));

		final List<AddressModel> addressModels = getAddressReverseConverter().convertAll(userData.getAddresses());
		userModel.setAddresses(addressModels);
		userModel.setDefaultShipmentAddress(convert(userData.getDefaultShippingAddress()));
		userModel.setDefaultPaymentAddress(convert(userData.getDefaultBillingAddress()));
	}

	protected String convertToName(final String firstName, final String lastName)
	{
		return StringUtils.joinWith(" ", firstName, lastName);
	}

	protected AddressModel convert(final AddressData addressData)
	{
		if (addressData != null)
		{
			return getAddressReverseConverter().convert(addressData);
		}
		return null;
	}

	protected Converter<AddressData, AddressModel> getAddressReverseConverter()
	{
		return addressReverseConverter;
	}
}
