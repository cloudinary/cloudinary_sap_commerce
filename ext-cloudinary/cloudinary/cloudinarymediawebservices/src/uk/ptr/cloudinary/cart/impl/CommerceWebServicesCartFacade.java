/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.cart.impl;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.core.model.order.CartModel;


/**
 * Extension of {@link DefaultCartFacade} for commercewebservices.
 */
public class CommerceWebServicesCartFacade extends DefaultCartFacade
{
	@Override
	public CartData getSessionCart()
	{
		final CartData cartData;
		final CartModel cart = getCartService().getSessionCart();
		cartData = getCartConverter().convert(cart);
		return cartData;
	}

	/**
	 * Checks if given card belongs to anonymous user.
	 *
	 * @param cartGuid
	 * 		GUID of the cart.
	 * @return <tt>true</tt> if the cart belongs to anonymous user.
	 */
	public boolean isAnonymousUserCart(final String cartGuid)
	{
		final CartModel cart = getCommerceCartService()
				.getCartForGuidAndSiteAndUser(cartGuid, getBaseSiteService().getCurrentBaseSite(),
						getUserService().getAnonymousUser());
		return cart != null;
	}

	/**
	 * Checks if given card belongs to current user.
	 *
	 * @param cartGuid
	 * 		GUID of the cart.
	 * @return <tt>true</tt> if the cart belongs to current user.
	 */
	public boolean isCurrentUserCart(final String cartGuid)
	{
		final CartModel cart = getCommerceCartService()
				.getCartForGuidAndSiteAndUser(cartGuid, getBaseSiteService().getCurrentBaseSite(), getUserService().getCurrentUser());
		return cart != null;
	}
}
