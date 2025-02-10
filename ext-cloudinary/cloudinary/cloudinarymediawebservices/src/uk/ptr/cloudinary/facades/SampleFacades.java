/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.facades;

import de.hybris.platform.core.servicelayer.data.SearchPageData;

import java.util.List;

import uk.ptr.cloudinary.data.UserData;
import uk.ptr.cloudinary.dto.SampleWsDTO;
import uk.ptr.cloudinary.dto.TestMapWsDTO;


public interface SampleFacades
{
	SampleWsDTO getSampleWsDTO(final String value);

	UserData getUser(String id);

	List<UserData> getUsers();

	SearchPageData<UserData> getUsers(SearchPageData<?> params);

	void updateUser(String id, UserData user);

	TestMapWsDTO getMap();
}
