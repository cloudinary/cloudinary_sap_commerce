/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import uk.ptr.cloudinary.queues.data.ProductExpressUpdateElementData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Predicate;


@UnitTest
public class ProductExpressUpdateQueueTest
{
	static final int MAX_CAPACITY = 2;
	static final String PRODUCT0_CODE = "productElement0";
	static final String PRODUCT1_CODE = "productElement1";
	static final String PRODUCT2_CODE = "productElement2";
	static final String PRODUCT3_CODE = "productElement3";
	static final String PRODUCT4_CODE = "productElement4";
	private final Date tick = new Date();
	private List<ProductExpressUpdateElementData> resultList;
	private ProductExpressUpdateQueue productExpressUpdateQueue;
	private ProductExpressUpdateElementData productElement0;
	private ProductExpressUpdateElementData productElement1;
	private ProductExpressUpdateElementData productElement2;
	private ProductExpressUpdateElementData productElement3;
	private ProductExpressUpdateElementData productElement4;
	private List<ProductExpressUpdateElementData> list;

	@Before
	public void setUp() throws ParseException
	{
		productExpressUpdateQueue = Mockito.spy(new ProductExpressUpdateQueue());

		productElement0 = new ProductExpressUpdateElementData();
		productElement0.setCode(PRODUCT0_CODE);
		// 27 hours ago
		Mockito.doReturn(Long.valueOf((new Date(tick.getTime() - 60 * 60 * 27 * 1000).getTime()))).when(productExpressUpdateQueue)
				.getTimeKey(productElement0);

		productElement1 = new ProductExpressUpdateElementData();
		productElement1.setCode(PRODUCT1_CODE);
		// 26 hours ago
		Mockito.doReturn(Long.valueOf((new Date(tick.getTime() - 60 * 60 * 26 * 1000).getTime()))).when(productExpressUpdateQueue)
				.getTimeKey(productElement1);

		productElement2 = new ProductExpressUpdateElementData();
		productElement2.setCode(PRODUCT2_CODE);
		// 25 hours ago
		Mockito.doReturn(Long.valueOf((new Date(tick.getTime() - 60 * 60 * 25 * 1000).getTime()))).when(productExpressUpdateQueue)
				.getTimeKey(productElement2);

		productElement3 = new ProductExpressUpdateElementData();
		productElement3.setCode(PRODUCT3_CODE);
		// 10 hours ago
		Mockito.doReturn(Long.valueOf((new Date(tick.getTime() - 60 * 60 * 10 * 1000).getTime()))).when(productExpressUpdateQueue)
				.getTimeKey(productElement3);

		productElement4 = new ProductExpressUpdateElementData();
		productElement4.setCode(PRODUCT4_CODE);
		// now
		Mockito.doReturn(Long.valueOf(tick.getTime())).when(productExpressUpdateQueue).getTimeKey(productElement4);

		list = new ArrayList<ProductExpressUpdateElementData>();
		list.add(productElement0);
		list.add(productElement1);
		list.add(productElement2);
		list.add(productElement3);
	}

	@Test
	public void testGetItems()
	{
		productExpressUpdateQueue.addItems(list);
		Assert.assertEquals(productExpressUpdateQueue.getItems().size(), 4);

		productExpressUpdateQueue.addItem(productElement4);
		Assert.assertEquals(productExpressUpdateQueue.getItems().size(), 5);

		resultList = productExpressUpdateQueue.getItems(new Date(tick.getTime() - 60 * 60 * 26 * 1000));
		Assert.assertEquals(resultList.size(), 4);
		Assert.assertTrue(
				resultList.containsAll(Arrays.asList(productElement1, productElement2, productElement3, productElement4)));
		Assert.assertEquals(resultList.get(0), productElement1);

		resultList = productExpressUpdateQueue.getItems(new Date(tick.getTime() - 60 * 60 * 25 * 1000));
		Assert.assertEquals(resultList.size(), 3);
		Assert.assertTrue(resultList.containsAll(Arrays.asList(productElement2, productElement3, productElement4)));
	}

	@Test
	public void testMaxCapacity()
	{
		productExpressUpdateQueue.setMaxCapacity(MAX_CAPACITY);
		productExpressUpdateQueue.addItems(list);
		Assert.assertEquals(productExpressUpdateQueue.getItems().size(), MAX_CAPACITY);

		productExpressUpdateQueue.addItem(productElement4);
		Assert.assertEquals(productExpressUpdateQueue.getItems().size(), MAX_CAPACITY);
	}

	@Test
	public void testRemoveItems()
	{
		productExpressUpdateQueue.addItems(list);
		productExpressUpdateQueue.addItem(productElement4);

		productExpressUpdateQueue.removeItems(new Date(tick.getTime() - 60 * 60 * 11 * 1000));
		resultList = productExpressUpdateQueue.getItems();
		Assert.assertEquals(resultList.size(), 2);
		Assert.assertFalse(resultList.containsAll(Arrays.asList(productElement0, productElement1, productElement2)));

		productExpressUpdateQueue.removeItems();
		Assert.assertEquals(productExpressUpdateQueue.getItems().size(), 0);

		productExpressUpdateQueue.addItems(list);
		Assert.assertEquals(productExpressUpdateQueue.getItems().size(), 4);
		Assert.assertTrue(productExpressUpdateQueue.getItems().contains(productElement1));

		final Predicate<ProductExpressUpdateElementData> productElement1Predicate = new Predicate<ProductExpressUpdateElementData>()
		{
			@Override
			public boolean apply(@Nullable final ProductExpressUpdateElementData input)
			{
				if (input != null && input.getCode() != null && input.getCode().equals(PRODUCT1_CODE))
				{
					return true;
				}
				return false;
			}
		};
		productExpressUpdateQueue.removeItems(productElement1Predicate);
		Assert.assertEquals(productExpressUpdateQueue.getItems().size(), 3);
		Assert.assertFalse(productExpressUpdateQueue.getItems().contains(productElement1));
	}
}
