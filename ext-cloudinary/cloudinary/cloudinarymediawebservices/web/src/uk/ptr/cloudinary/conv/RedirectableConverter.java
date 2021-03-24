/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.conv;

import com.thoughtworks.xstream.converters.Converter;


/**
 * Converters implementing this interface can redirect some operations to another converter (set as a target converter).
 */
public interface RedirectableConverter extends Converter
{
	/**
	 * Sets given converter as a target of redirection.
	 *
	 * @param converter
	 * 		converter to be used instead of current converter.
	 */
	void setTargetConverter(final Converter converter);

	/**
	 * @return {@link Class} that current converter is able to convert.
	 */
	Class getConvertedClass();
}
