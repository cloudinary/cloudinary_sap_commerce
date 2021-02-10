/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.validator;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import uk.ptr.cloudinary.validation.data.CartVoucherValidationData;

import java.util.List;


/**
 * Commerce web services cart voucher validator. Checks if voucher is expired.
 */
public class CartVoucherValidator
{
	private final VoucherFacade voucherFacade;

	public CartVoucherValidator(final VoucherFacade voucherFacade)
	{
		this.voucherFacade = voucherFacade;
	}

	/**
	 * Validate the vouchers by voucher code list.
	 *
	 * @param voucherCodes
	 * @return List of {@link CartVoucherValidationData}s
	 */
	public List<CartVoucherValidationData> validate(final List<String> voucherCodes)
	{
		return voucherCodes.stream()
				.filter(this::isVoucherExpired)
				.map(this::convertToCartVoucherValidateData)
				.collect(toList());
	}

	protected boolean isVoucherExpired(final String voucherCode)
	{
		return !getVoucherFacade().checkVoucherCode(voucherCode);
	}

	protected CartVoucherValidationData convertToCartVoucherValidateData(final String voucherCode)
	{
		final CartVoucherValidationData cartVoucherValidationData = new CartVoucherValidationData();
		cartVoucherValidationData.setSubject(voucherCode);
		return cartVoucherValidationData;
	}

	protected VoucherFacade getVoucherFacade()
	{
		return voucherFacade;
	}
}
