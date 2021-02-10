/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.validator;

import de.hybris.platform.commercefacades.order.data.CartData;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Default commerce web services cart validator. Checks if cart is calculated and if needed values are filled.
 */
public class PlaceOrderCartValidator implements Validator
{
	@Override
	public boolean supports(final Class<?> clazz)
	{
		return CartData.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final CartData cart = (CartData) target;

		if (!cart.isCalculated())
		{
			errors.reject("cart.notCalculated");
		}

		if (cart.getDeliveryMode() == null)
		{
			errors.reject("cart.deliveryModeNotSet");
		}

		if (cart.getPaymentInfo() == null)
		{
			errors.reject("cart.paymentInfoNotSet");
		}
	}
}
