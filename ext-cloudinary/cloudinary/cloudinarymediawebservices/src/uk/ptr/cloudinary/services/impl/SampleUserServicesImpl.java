/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.services.impl;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ptr.cloudinary.services.SampleUserServices;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


public class SampleUserServicesImpl implements SampleUserServices
{
	private final Map<String, UserModel> data;

	public SampleUserServicesImpl()
	{
		//user 1
		data = new HashMap<>();
		AddressModel address = new AddressModel();
		address.setStreetname("grosse strasse");
		address.setStreetnumber("5b / 79");
		address.setTown("Berlin");
		address.setContactAddress(Boolean.TRUE);

		AddressModel address1 = new AddressModel();
		address1.setStreetname("Papenmoorweg");
		address1.setStreetnumber("2");
		address1.setTown("Hamburg");

		UserModel model = new UserModel();
		model.setName("User1");
		model.setDescription("normal user");
		model.setAddresses(Arrays.asList(address, address1));
		data.put("user1", model);

		//user 2
		address = new AddressModel();
		address.setStreetname("long street");
		address.setStreetnumber("1 / 864");
		address.setTown("Small town");
		address.setBillingAddress(Boolean.TRUE);

		address1 = new AddressModel();
		address1.setStreetname("short street");
		address1.setStreetnumber("9875643");
		address1.setTown("Small town");

		model = new UserModel();
		model.setName("Second user");
		model.setDescription("not a normal user");
		model.setAddresses(Arrays.asList(address, address1));
		data.put("user2", model);
	}

	@Override
	public UserModel getUserById(final String id)
	{
		return data.get(id);
	}

	@Override
	public Collection<UserModel> getUsers()
	{
		return data.values();
	}

	@Override
	public void updateUser(final String id, final UserModel user)
	{
		validateParameterNotNullStandardMessage("user", user);
		data.put(id, user);
	}
}
