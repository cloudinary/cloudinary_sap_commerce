/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.converters;


import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;


/**
 * Bidirectional converter between {@link ProductReferenceTypeEnum} and String
 */
@WsDTOMapping
public class ProductReferenceTypeEnumConverter extends BidirectionalConverter<ProductReferenceTypeEnum, String>
{
	@Override
	public ProductReferenceTypeEnum convertFrom(final String source, final Type<ProductReferenceTypeEnum> destinationType,
			final MappingContext mappingContext)
	{
		return ProductReferenceTypeEnum.valueOf(source);
	}

	@Override
	public String convertTo(final ProductReferenceTypeEnum source, final Type<String> destinationType,
			final MappingContext mappingContext)
	{
		return source.toString();
	}
}
