/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;

import javax.json.spi.JsonProvider;

import org.junit.Test;



/**
 * Unit test for {@link JSONProviderFactory}
 */
@UnitTest
public class JSONProviderFactoryTest
{

	@Test
	public void testGetJsonProvoder()
	{
		final JsonProvider provider = JSONProviderFactory.getJSONProvider();
		assertNotNull(provider);
		assertSame(provider, JSONProviderFactory.getJSONProvider());
	}

}
