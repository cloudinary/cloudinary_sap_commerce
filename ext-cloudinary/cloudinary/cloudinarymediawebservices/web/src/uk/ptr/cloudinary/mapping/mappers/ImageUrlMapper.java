/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.mappers;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercewebservicescommons.dto.product.ImageWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;

import ma.glasnost.orika.MappingContext;


public class ImageUrlMapper extends AbstractCustomMapper<ImageData, ImageWsDTO>
{
	@Override
	public void mapAtoB(final ImageData a, final ImageWsDTO b, final MappingContext context)
	{
		// other fields are mapped automatically

		context.beginMappingField("url", getAType(), a, "url", getBType(), b);
		try
		{
			if (shouldMap(a, b, context))
			{
				b.setUrl(a.getUrl());
			}
		}
		finally
		{
			context.endMappingField();
		}
	}
}
