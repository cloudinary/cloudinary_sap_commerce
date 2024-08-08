/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.exceptions;

/**
 * Thrown when an operation is performed that requires a session cart, which was not yet created.
 */
public class NoCheckoutCartException extends Exception
{

	/**
	 * @param message
	 */
	public NoCheckoutCartException(final String message)
	{
		super(message);
	}

}
