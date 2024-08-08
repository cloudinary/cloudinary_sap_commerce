/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.exceptions;

import javax.servlet.ServletException;


/**
 * Thrown when request is not supported for current configuration.
 */
public class UnsupportedRequestException extends ServletException
{
	public UnsupportedRequestException(final String message)
	{
		super(message);
	}
}
