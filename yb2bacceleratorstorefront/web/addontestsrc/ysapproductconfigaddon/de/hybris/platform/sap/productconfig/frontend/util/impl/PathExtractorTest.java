/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class PathExtractorTest
{

	@Test
	public void testGetIndexFromErrorPath_GroupAndCstic()
	{
		final PathExtractor extractor = new PathExtractor("groups[1].cstics[5].value");

		Assert.assertEquals("wrong group index extracted from path", 1, extractor.getGroupIndex());
		Assert.assertEquals("wrong cstic index extracted from path", 5, extractor.getCsticsIndex());
		Assert.assertEquals("should be no subgroup", 0, extractor.getSubGroupCount());
	}

	@Test
	public void testGetIndexFromErrorPath_GroupSubgroupCstic()
	{
		final PathExtractor extractor = new PathExtractor("groups[1].subGroups[42].cstics[5].value");

		Assert.assertEquals("wrong group index extracted from path", 1, extractor.getGroupIndex());
		Assert.assertEquals("wrong cstic index extracted from path", 5, extractor.getCsticsIndex());
		Assert.assertEquals("should be a subgroup", 1, extractor.getSubGroupCount());
		Assert.assertEquals("wrong subgroup index extracted from path", 42, extractor.getSubGroupIndex(0));
	}


	@Test
	public void testGetIndexFromErrorPath_GroupSubgroupsCstic()
	{
		final PathExtractor extractor = new PathExtractor("groups[1].subGroups[0].subGroups[2].subGroups[42].cstics[5].value");

		Assert.assertEquals("wrong group index extracted from path", 1, extractor.getGroupIndex());
		Assert.assertEquals("wrong cstic index extracted from path", 5, extractor.getCsticsIndex());
		Assert.assertEquals("should be 3 subgroups", 3, extractor.getSubGroupCount());

		final int[] test = new int[]
		{ 0, 2, 42 };
		for (int i = 0; i < extractor.getSubGroupCount(); i++)
		{
			Assert.assertEquals("wrong subgroup index extracted from path", test[i], extractor.getSubGroupIndex(i));
		}
	}
}
