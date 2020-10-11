/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.converters;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;


/**
 * Bidirectional converter between {@link StockLevelStatus} and String
 */
@WsDTOMapping
public class StockLevelStatusConverter extends BidirectionalConverter<StockLevelStatus, String>
{
	@Override
	public String convertTo(final StockLevelStatus source, final Type<String> destinationType, final MappingContext mappingContext)
	{
		return source.toString();
	}

	@Override
	public StockLevelStatus convertFrom(final String source, final Type<StockLevelStatus> destinationType,
			final MappingContext mappingContext)
	{
		return StockLevelStatus.valueOf(source);
	}
}
