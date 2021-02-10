/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.errors.converters;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.converters.AbstractErrorConverter;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Converts {@link CartModificationDataList} to a list of
 * {@link de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO}.
 */
public class CartModificationDataListErrorConverter extends AbstractErrorConverter
{
	private CartModificationDataErrorConverter cartModificationDataErrorConverter;

	@Override
	public boolean supports(final Class clazz)
	{
		return CartModificationDataList.class.isAssignableFrom(clazz);
	}

	@Override
	public void populate(final Object o, final List<ErrorWsDTO> webserviceErrorList)
	{
		final CartModificationDataList cartModificationList = (CartModificationDataList) o;
		for (final CartModificationData modificationData : cartModificationList.getCartModificationList())
		{
			getCartModificationDataErrorConverter().populate(modificationData, webserviceErrorList);
		}
	}

	public CartModificationDataErrorConverter getCartModificationDataErrorConverter()
	{
		return cartModificationDataErrorConverter;
	}

	@Required
	public void setCartModificationDataErrorConverter(final CartModificationDataErrorConverter cartModificationDataErrorConverter)
	{
		this.cartModificationDataErrorConverter = cartModificationDataErrorConverter;
	}
}
