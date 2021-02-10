/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.converters;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;


/**
 * Bidirectional converter between {@link OrderStatus} and String
 */
@WsDTOMapping
public class OrderStatusConverter extends BidirectionalConverter<OrderStatus, String>
{
	@Override
	public OrderStatus convertFrom(final String source, final Type<OrderStatus> destinationType,
			final MappingContext mappingContext)
	{
		return OrderStatus.valueOf(source);
	}

	@Override
	public String convertTo(final OrderStatus source, final Type<String> destinationType, final MappingContext mappingContext)
	{
		return source.toString();
	}
}
