/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import uk.ptr.cloudinary.queues.data.OrderStatusUpdateElementData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


@UnitTest
public class OrderStatusUpdateQueueTest
{
	static final int MAX_CAPACITY = 2;
	static final String ORDER0_CODE = "orderElement0";
	static final String ORDER1_CODE = "orderElement1";
	static final String ORDER2_CODE = "orderElement2";
	static final String ORDER3_CODE = "orderElement3";
	static final String ORDER4_CODE = "orderElement4";
	private final Date tick = new Date();
	private List<OrderStatusUpdateElementData> resultList;
	private OrderStatusUpdateQueue orderStatusUpdateQueue;
	private OrderStatusUpdateElementData orderElement0;
	private OrderStatusUpdateElementData orderElement1;
	private OrderStatusUpdateElementData orderElement2;
	private OrderStatusUpdateElementData orderElement3;
	private OrderStatusUpdateElementData orderElement4;
	private List<OrderStatusUpdateElementData> list;

	@Before
	public void setUp() throws ParseException
	{
		orderStatusUpdateQueue = Mockito.spy(new OrderStatusUpdateQueue());

		orderElement0 = new OrderStatusUpdateElementData();
		orderElement0.setCode(ORDER0_CODE);
		// 27 hours ago
		Mockito.doReturn(Long.valueOf((new Date(tick.getTime() - 60 * 60 * 27 * 1000).getTime()))).when(orderStatusUpdateQueue)
				.getTimeKey(orderElement0);

		orderElement1 = new OrderStatusUpdateElementData();
		orderElement1.setCode(ORDER1_CODE);
		// 26 hours ago
		Mockito.doReturn(Long.valueOf((new Date(tick.getTime() - 60 * 60 * 26 * 1000).getTime()))).when(orderStatusUpdateQueue)
				.getTimeKey(orderElement1);

		orderElement2 = new OrderStatusUpdateElementData();
		orderElement2.setCode(ORDER2_CODE);
		// 25 hours ago
		Mockito.doReturn(Long.valueOf((new Date(tick.getTime() - 60 * 60 * 25 * 1000).getTime()))).when(orderStatusUpdateQueue)
				.getTimeKey(orderElement2);

		orderElement3 = new OrderStatusUpdateElementData();
		orderElement3.setCode(ORDER3_CODE);
		// 10 hours ago
		Mockito.doReturn(Long.valueOf((new Date(tick.getTime() - 60 * 60 * 10 * 1000).getTime()))).when(orderStatusUpdateQueue)
				.getTimeKey(orderElement3);

		orderElement4 = new OrderStatusUpdateElementData();
		orderElement4.setCode(ORDER4_CODE);
		// now
		Mockito.doReturn(Long.valueOf(tick.getTime())).when(orderStatusUpdateQueue).getTimeKey(orderElement4);

		list = new ArrayList<OrderStatusUpdateElementData>();
		list.add(orderElement0);
		list.add(orderElement1);
		list.add(orderElement2);
		list.add(orderElement3);
	}

	@Test
	public void testGetItems()
	{
		orderStatusUpdateQueue.addItems(list);
		Assert.assertEquals(orderStatusUpdateQueue.getItems().size(), 4);

		orderStatusUpdateQueue.addItem(orderElement4);
		Assert.assertEquals(orderStatusUpdateQueue.getItems().size(), 5);

		resultList = orderStatusUpdateQueue.getItems(new Date(tick.getTime() - 60 * 60 * 26 * 1000));
		Assert.assertEquals(resultList.size(), 4);
		Assert.assertTrue(resultList.containsAll(Arrays.asList(orderElement1, orderElement2, orderElement3, orderElement4)));
		Assert.assertEquals(resultList.get(0), orderElement1);

		resultList = orderStatusUpdateQueue.getItems(new Date(tick.getTime() - 60 * 60 * 25 * 1000));
		Assert.assertEquals(resultList.size(), 3);
		Assert.assertTrue(resultList.containsAll(Arrays.asList(orderElement2, orderElement3, orderElement4)));
	}

	@Test
	public void testMaxCapacity()
	{
		orderStatusUpdateQueue.setMaxCapacity(MAX_CAPACITY);
		orderStatusUpdateQueue.addItems(list);
		Assert.assertEquals(orderStatusUpdateQueue.getItems().size(), MAX_CAPACITY);

		orderStatusUpdateQueue.addItem(orderElement4);
		Assert.assertEquals(orderStatusUpdateQueue.getItems().size(), MAX_CAPACITY);
	}

	@Test
	public void testRemoveItems()
	{
		orderStatusUpdateQueue.addItems(list);
		orderStatusUpdateQueue.addItem(orderElement4);

		orderStatusUpdateQueue.removeItems(new Date(tick.getTime() - 60 * 60 * 11 * 1000));
		resultList = orderStatusUpdateQueue.getItems();
		Assert.assertEquals(resultList.size(), 2);
		Assert.assertFalse(resultList.containsAll(Arrays.asList(orderElement0, orderElement1, orderElement2)));

		orderStatusUpdateQueue.removeItems();
		Assert.assertEquals(orderStatusUpdateQueue.getItems().size(), 0);
	}
}
