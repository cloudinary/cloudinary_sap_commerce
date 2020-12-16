/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.strategies;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.store.BaseStoreModel;


/**
 * Strategy for retrieving base store from the base site.
 */
public interface BaseStoreForSiteSelectorStrategy
{
	BaseStoreModel getBaseStore(BaseSiteModel baseSiteModel);
}
