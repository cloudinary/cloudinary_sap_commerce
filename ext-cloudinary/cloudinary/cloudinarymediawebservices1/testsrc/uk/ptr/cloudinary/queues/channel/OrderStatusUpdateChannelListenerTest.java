/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.channel;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import uk.ptr.cloudinary.queues.data.OrderStatusUpdateElementData;
import uk.ptr.cloudinary.queues.impl.OrderStatusUpdateQueue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class OrderStatusUpdateChannelListenerTest
{
	private static final String ORDER_CODE = "orderCode";
	private static final String ANOTHER_ORDER_CODE = "anotherOrderCode";
	@Mock
	private OrderModel order;
	@Mock
	private OrderModel anotherOrder;
	@Mock
	private OrderModel duplicateOrder;
	private OrderStatusUpdateElementData orderElementData;
	private OrderStatusUpdateElementData anotherOrderElementData;
	private OrderStatusUpdateChannelListener listener;
	private OrderStatusUpdateQueue orderStatusUpdateQueue;
	@Mock
	private Converter<OrderModel, OrderStatusUpdateElementData> orderStatusUpdateElementConverter;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		orderStatusUpdateQueue = new OrderStatusUpdateQueue();
		listener = new OrderStatusUpdateChannelListener();
		listener.setOrderStatusUpdateElementConverter(orderStatusUpdateElementConverter);
		listener.setOrderStatusUpdateQueue(orderStatusUpdateQueue);

		orderElementData = new OrderStatusUpdateElementData();
		orderElementData.setCode(ORDER_CODE);

		given(order.getCode()).willReturn(ORDER_CODE);
		given(orderStatusUpdateElementConverter.convert(order)).willReturn(orderElementData);

		anotherOrderElementData = new OrderStatusUpdateElementData();
		anotherOrderElementData.setCode(ANOTHER_ORDER_CODE);

		given(anotherOrder.getCode()).willReturn(ANOTHER_ORDER_CODE);
		given(orderStatusUpdateElementConverter.convert(anotherOrder)).willReturn(anotherOrderElementData);

		final OrderStatusUpdateElementData duplicateOrderElementData = new OrderStatusUpdateElementData();
		duplicateOrderElementData.setCode(ORDER_CODE);

		given(duplicateOrder.getCode()).willReturn(ORDER_CODE);
		given(orderStatusUpdateElementConverter.convert(duplicateOrder)).willReturn(duplicateOrderElementData);

	}

	@Test
	public void testOnMessage()
	{
		listener.onMessage(order);
		final OrderStatusUpdateElementData queueElement = orderStatusUpdateQueue.getLastItem();
		Assert.assertEquals(orderElementData, queueElement);
	}

	@Test
	public void testAddingToQueue()
	{
		listener.onMessage(order);
		listener.onMessage(anotherOrder);
		final OrderStatusUpdateElementData queueElement = orderStatusUpdateQueue.getLastItem();
		Assert.assertEquals(anotherOrderElementData, queueElement);
		Assert.assertEquals(2, orderStatusUpdateQueue.getItems().size());
	}

	@Test
	public void testDuplicateElementSuccessfullyAdded()
	{
		listener.onMessage(order);
		listener.onMessage(order);
		Assert.assertEquals(2, orderStatusUpdateQueue.getItems().size());

		listener.onMessage(duplicateOrder);
		Assert.assertEquals(3, orderStatusUpdateQueue.getItems().size());

		listener.onMessage(anotherOrder);
		Assert.assertEquals(4, orderStatusUpdateQueue.getItems().size());
		listener.onMessage(duplicateOrder);
		Assert.assertEquals(5, orderStatusUpdateQueue.getItems().size());
	}

}
