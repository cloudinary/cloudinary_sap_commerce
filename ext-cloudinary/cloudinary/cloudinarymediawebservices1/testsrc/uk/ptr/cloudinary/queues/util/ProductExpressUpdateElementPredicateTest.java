/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.util;

import de.hybris.bootstrap.annotations.UnitTest;
import uk.ptr.cloudinary.queues.data.ProductExpressUpdateElementData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductExpressUpdateElementPredicateTest
{
	private static final String PRODUCT_CODE = "productCode";
	private static final String CATALOG_VERSION = "Online";
	private static final String CATALOG_ID = "productCatalog";
	private static final String ANOTHER_PRODUCT_CODE = "anotherProductCode";
	private static final String ANOTHER_CATALOG_VERSION = "Stage";
	private static final String ANOTHER_CATALOG_ID = "anotherProductCatalog";
	private ProductExpressUpdateElementData elementData;
	private ProductExpressUpdateElementPredicate predicate;

	@Before
	public void setUp() throws Exception
	{
		elementData = new ProductExpressUpdateElementData();
		elementData.setCode(PRODUCT_CODE);
		elementData.setCatalogId(CATALOG_ID);
		elementData.setCatalogVersion(CATALOG_VERSION);

		predicate = new ProductExpressUpdateElementPredicate(elementData);
	}

	@Test
	public void testApplyWithNull()
	{
		Assert.assertFalse(predicate.apply(null));
		Assert.assertFalse((new ProductExpressUpdateElementPredicate(null)).apply(elementData));
		Assert.assertTrue((new ProductExpressUpdateElementPredicate(null)).apply(null));
	}

	@Test
	public void testApplyForIdenticalElements()
	{
		Assert.assertTrue(predicate.apply(elementData));
	}

	@Test
	public void testApplyForEqualElements()
	{
		final ProductExpressUpdateElementData equalElementData = new ProductExpressUpdateElementData();
		equalElementData.setCode(PRODUCT_CODE);
		equalElementData.setCatalogId(CATALOG_ID);
		equalElementData.setCatalogVersion(CATALOG_VERSION);

		Assert.assertTrue(predicate.apply(equalElementData));
	}

	@Test
	public void testApplyForDifferentProductCode()
	{
		final ProductExpressUpdateElementData differentProductCodeElementData = new ProductExpressUpdateElementData();
		differentProductCodeElementData.setCode(ANOTHER_PRODUCT_CODE);
		differentProductCodeElementData.setCatalogId(CATALOG_ID);
		differentProductCodeElementData.setCatalogVersion(CATALOG_VERSION);

		Assert.assertFalse(predicate.apply(differentProductCodeElementData));
	}

	@Test
	public void testApplyForDifferentCatalog()
	{
		final ProductExpressUpdateElementData differentCatalogElementData = new ProductExpressUpdateElementData();
		differentCatalogElementData.setCode(PRODUCT_CODE);
		differentCatalogElementData.setCatalogId(ANOTHER_CATALOG_ID);
		differentCatalogElementData.setCatalogVersion(CATALOG_VERSION);

		Assert.assertFalse(predicate.apply(differentCatalogElementData));
	}

	@Test
	public void testApplyForDifferentCatalogVersion()
	{
		final ProductExpressUpdateElementData differentCatalogVersionElementData = new ProductExpressUpdateElementData();
		differentCatalogVersionElementData.setCode(PRODUCT_CODE);
		differentCatalogVersionElementData.setCatalogId(CATALOG_ID);
		differentCatalogVersionElementData.setCatalogVersion(ANOTHER_CATALOG_VERSION);

		Assert.assertFalse(predicate.apply(differentCatalogVersionElementData));
	}

}
