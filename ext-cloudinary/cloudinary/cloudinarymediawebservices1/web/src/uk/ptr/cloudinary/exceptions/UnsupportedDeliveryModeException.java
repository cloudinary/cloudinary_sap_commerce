/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.exceptions;

/**
 * Specific exception that is thrown when delivery mode is not supported for the current session cart
 */
public class UnsupportedDeliveryModeException extends Exception
{

	private final String deliveryMode;

	/**
	 * @param code
	 */
	public UnsupportedDeliveryModeException(final String code)
	{
		super("Delivery Mode [" + code + "] is not supported for the current cart");
		this.deliveryMode = code;
	}


	/**
	 * @return the deliveryMode
	 */
	public String getDeliveryMode()
	{
		return deliveryMode;
	}

}
