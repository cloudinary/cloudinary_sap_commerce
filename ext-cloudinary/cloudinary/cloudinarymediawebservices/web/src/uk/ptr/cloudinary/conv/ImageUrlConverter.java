/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.conv;

import com.thoughtworks.xstream.converters.SingleValueConverter;

import uk.ptr.cloudinary.constants.YcommercewebservicesConstants;

import java.util.Optional;


public class ImageUrlConverter implements SingleValueConverter
{
	@Override
	public String toString(Object o)
	{
		return Optional.ofNullable(o) //
				.filter(String.class::isInstance) //
				.map(String.class::cast) //
				.map(this::addRootContext) //
				.orElse(null);
	}

	protected String addRootContext(final String imageUrl)
	{
		return new StringBuilder(YcommercewebservicesConstants.V1_ROOT_CONTEXT).append(imageUrl).toString();
	}

	@Override
	public Object fromString(String s)
	{
		return null;
	}

	@Override
	public boolean canConvert(Class type)
	{
		return type == String.class;
	}
}
