/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.cart.hooks;

import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.hook.CommerceSaveCartRestorationMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.util.Config;

import org.apache.log4j.Logger;


/**
 * Hook to set the saveTime as null before performing the restore. By default it is enabled.
 */
public class CommerceWebServicesSaveCartRestorationHook implements CommerceSaveCartRestorationMethodHook
{
	private static final Logger LOG = Logger.getLogger(CommerceWebServicesSaveCartRestorationHook.class);


	@Override
	public void beforeRestoringCart(final CommerceCartParameter parameters) throws CommerceCartRestorationException
	{

		if (Config.getBoolean("cloudinarymediawebservices.commercesavecart.restoration.savetime.hook.enabled", true))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Setting saveTime as null on the cart to be restored");
			}
			final CartModel cartModel = parameters.getCart();
			//Convert save cart to active cart
			cartModel.setSaveTime(null);
		}
	}

	@Override
	public void afterRestoringCart(final CommerceCartParameter parameters) throws CommerceCartRestorationException
	{
		// Auto-generated method stub
	}
}
