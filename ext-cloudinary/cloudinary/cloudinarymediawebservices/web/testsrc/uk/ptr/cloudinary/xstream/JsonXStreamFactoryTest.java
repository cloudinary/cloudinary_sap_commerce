/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.xstream;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;


/**
 *
 */
@UnitTest
public class JsonXStreamFactoryTest
{
	private JsonXStreamFactory factory;

	@Before
	public void prepare() throws Exception
	{
		factory = new JsonXStreamFactory();
		factory.afterPropertiesSet();
	}

	@Test
	public void testMapperOverridenJson() throws Exception
	{
		final Object streamObject = factory.getObject();

		Assert.assertTrue(streamObject instanceof XStream);

		final XStream stream = (XStream) streamObject;

		Assert.assertEquals("testData", stream.getMapper().aliasForSystemAttribute("testData"));
		Assert.assertEquals(null, stream.getMapper().aliasForSystemAttribute("class"));
	}
}
