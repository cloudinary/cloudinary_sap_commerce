/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.mappers;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressValidationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;
import uk.ptr.cloudinary.user.data.AddressDataList;
import uk.ptr.cloudinary.validation.data.AddressValidationData;

import ma.glasnost.orika.MappingContext;


public class AddressValidationDataMapper extends AbstractCustomMapper<AddressValidationData, AddressValidationWsDTO>
{
	@Override
	public void mapAtoB(final AddressValidationData a, final AddressValidationWsDTO b, final MappingContext context)
	{
		// other fields are mapped automatically

		context.beginMappingField("suggestedAddressesList", getAType(), a, "suggestedAddresses", getBType(), b);
		try
		{
			if (shouldMap(a, b, context) && a.getSuggestedAddressesList() != null)
			{
				b.setSuggestedAddresses(
						mapperFacade.mapAsList(a.getSuggestedAddressesList().getAddresses(), AddressWsDTO.class, context));
			}
		}
		finally
		{
			context.endMappingField();
		}
	}

	@Override
	public void mapBtoA(final AddressValidationWsDTO b, final AddressValidationData a, final MappingContext context)
	{
		// other fields are mapped automatically

		context.beginMappingField("suggestedAddresses", getBType(), b, "suggestedAddressesList", getAType(), a);
		try
		{
			if (shouldMap(b, a, context) && b.getSuggestedAddresses() != null)
			{
				final AddressDataList addressDataList = new AddressDataList();
				addressDataList.setAddresses(mapperFacade.mapAsList(b.getSuggestedAddresses(), AddressData.class, context));
				a.setSuggestedAddressesList(addressDataList);
			}
		}
		finally
		{
			context.endMappingField();
		}

	}
}
