/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;

import org.junit.Assert;
import org.junit.Test;

import ma.glasnost.orika.metadata.Type;


@UnitTest
public class StockLevelStatusConverterTest
{
	private final StockLevelStatusConverter converter = new StockLevelStatusConverter();
	private final String stringStatus = StockLevelStatus.INSTOCK.toString();
	private final StockLevelStatus status = StockLevelStatus.INSTOCK;

	@Test
	public void testConvertFrom()
	{
		final StockLevelStatus result = converter.convertFrom(stringStatus, (Type<StockLevelStatus>) null, null);
		Assert.assertEquals(status, result);
	}

	@Test
	public void testConvertTo()
	{
		final String result = converter.convertTo(status, (Type<String>) null, null);
		Assert.assertEquals(stringStatus, result);
	}
}
