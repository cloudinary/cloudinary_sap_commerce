/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.conv;

import uk.ptr.cloudinary.formatters.WsDateFormatter;

import java.util.Date;

import com.thoughtworks.xstream.converters.SingleValueConverter;


/**
 * Converter for a specific date format.
 */
public class CustomDateConverter implements SingleValueConverter
{
	private WsDateFormatter wsDateFormatter;

	public void setWsDateFormatter(final WsDateFormatter wsDateFormatter)
	{
		this.wsDateFormatter = wsDateFormatter;
	}

	@Override
	public boolean canConvert(final Class type)
	{
		return type == Date.class;
	}

	@Override
	public String toString(final Object obj)
	{
		return wsDateFormatter.toString((Date) obj);

	}

	@Override
	public Object fromString(final String str)
	{
		return wsDateFormatter.toDate(str);
	}
}
