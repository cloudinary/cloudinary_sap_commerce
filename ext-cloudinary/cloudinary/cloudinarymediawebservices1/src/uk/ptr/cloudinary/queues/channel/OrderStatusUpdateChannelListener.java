/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.channel;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import uk.ptr.cloudinary.queues.UpdateQueue;
import uk.ptr.cloudinary.queues.data.OrderStatusUpdateElementData;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class OrderStatusUpdateChannelListener
{
	private static final Logger LOG = Logger.getLogger(OrderStatusUpdateChannelListener.class);
	private UpdateQueue<OrderStatusUpdateElementData> orderStatusUpdateQueue;
	private Converter<OrderModel, OrderStatusUpdateElementData> orderStatusUpdateElementConverter;

	public void onMessage(final OrderModel order)
	{
		LOG.debug("OrderStatusUpdateChannelListener got new status for order with code " + order.getCode());
		final OrderStatusUpdateElementData orderStatusUpdateElementData = getOrderStatusUpdateElementConverter().convert(order);
		getOrderStatusUpdateQueue().addItem(orderStatusUpdateElementData);
	}

	public UpdateQueue<OrderStatusUpdateElementData> getOrderStatusUpdateQueue()
	{
		return orderStatusUpdateQueue;
	}

	@Required
	public void setOrderStatusUpdateQueue(final UpdateQueue<OrderStatusUpdateElementData> orderStatusUpdateQueue)
	{
		this.orderStatusUpdateQueue = orderStatusUpdateQueue;
	}

	public Converter<OrderModel, OrderStatusUpdateElementData> getOrderStatusUpdateElementConverter()
	{
		return orderStatusUpdateElementConverter;
	}

	@Required
	public void setOrderStatusUpdateElementConverter(
			final Converter<OrderModel, OrderStatusUpdateElementData> orderStatusUpdateElementConverter)
	{
		this.orderStatusUpdateElementConverter = orderStatusUpdateElementConverter;
	}

}
