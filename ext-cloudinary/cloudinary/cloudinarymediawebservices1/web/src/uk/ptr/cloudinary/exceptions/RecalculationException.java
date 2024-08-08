/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.exceptions;

import de.hybris.platform.order.exceptions.CalculationException;

import javax.servlet.ServletException;


/**
 * Thrown when recalculation of cart is impossible due to any reasons.
 */
public class RecalculationException extends ServletException
{
	public RecalculationException(final CalculationException exception, final String currencyIso)
	{
		super("Cannot recalculate cart for currency: " + currencyIso, exception);
	}
}
