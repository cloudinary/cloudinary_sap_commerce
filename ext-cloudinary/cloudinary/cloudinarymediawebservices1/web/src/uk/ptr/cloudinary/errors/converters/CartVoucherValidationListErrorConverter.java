/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.errors.converters;

import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.converters.AbstractErrorConverter;
import uk.ptr.cloudinary.validation.data.CartVoucherValidationData;
import uk.ptr.cloudinary.validation.data.CartVoucherValidationDataList;

import java.util.List;


/**
 * Converts {@link CartVoucherValidationDataList} to a list of
 * {@link de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO}.
 */
public class CartVoucherValidationListErrorConverter extends AbstractErrorConverter
{
	private final CartVoucherValidationErrorConverter cartVoucherValidationErrorConverter;

	public CartVoucherValidationListErrorConverter(final CartVoucherValidationErrorConverter converter)
	{
		this.cartVoucherValidationErrorConverter = converter;
	}

	@Override
	public boolean supports(final Class clazz)
	{
		return CartVoucherValidationDataList.class.isAssignableFrom(clazz);
	}

	@Override
	public void populate(final Object o, final List<ErrorWsDTO> webserviceErrorList)
	{
		final CartVoucherValidationDataList cartVoucherValidationDataList = (CartVoucherValidationDataList) o;
		webserviceErrorList.addAll(getCartVoucherValidationErrorConverter()
				.convertAll(cartVoucherValidationDataList.getCartVoucherValidationDataList()));
	}

	protected CartVoucherValidationErrorConverter getCartVoucherValidationErrorConverter()
	{
		return cartVoucherValidationErrorConverter;
	}
}
