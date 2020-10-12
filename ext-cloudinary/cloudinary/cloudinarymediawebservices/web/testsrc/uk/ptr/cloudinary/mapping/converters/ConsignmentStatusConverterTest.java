/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.converters;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;

import org.junit.Assert;
import org.junit.Test;

import ma.glasnost.orika.metadata.Type;


@UnitTest
public class ConsignmentStatusConverterTest
{
	private final ConsignmentStatusConverter converter = new ConsignmentStatusConverter();
	private final String stringStatus = ConsignmentStatus.PICKUP_COMPLETE.toString();
	private final ConsignmentStatus status = ConsignmentStatus.PICKUP_COMPLETE;

	@Test
	public void testConvertFrom()
	{
		final ConsignmentStatus result = converter.convertFrom(stringStatus, (Type<ConsignmentStatus>) null, null);
		Assert.assertEquals(status, result);
	}

	@Test
	public void testConvertTo()
	{
		final String result = converter.convertTo(status, (Type<String>) null, null);
		Assert.assertEquals(stringStatus, result);
	}
}
