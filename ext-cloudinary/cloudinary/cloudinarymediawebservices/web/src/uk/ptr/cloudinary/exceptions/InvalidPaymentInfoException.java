/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.exceptions;

/**
 * Specific exception that is thrown when the given payment info could not be associated with the checkout cart.
 */
public class InvalidPaymentInfoException extends Exception
{

	private final String paymentInfoId;

	/**
	 * @param id
	 */
	public InvalidPaymentInfoException(final String id)
	{
		super("PaymentInfo [" + id + "] is invalid for the current cart");
		this.paymentInfoId = id;
	}

	/**
	 * @return the paymentInfoId
	 */
	public String getPaymentInfoId()
	{
		return paymentInfoId;
	}

}
