/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.conv;

import com.thoughtworks.xstream.converters.Converter;


/**
 * Abstract implementation of {@link RedirectableConverter} interface. Contains implementation of methods common to all
 * {@link RedirectableConverter} interface implementations.
 */
public abstract class AbstractRedirectableConverter implements RedirectableConverter
{
	private Converter targetConverter;

	@Override
	public void setTargetConverter(final Converter converter)
	{
		this.targetConverter = converter;

	}

	protected Converter getTargetConverter()
	{
		return targetConverter;
	}


}
