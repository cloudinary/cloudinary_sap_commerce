/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.util.ConverterFactory;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import uk.ptr.cloudinary.queues.data.OrderStatusUpdateElementData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("deprecation")
@UnitTest
public class OrderStatusUpdateElementPopulatorTest
{
	private static final String ORDER_CODE = "orderCode";
	private static final OrderStatus ORDER_STATUS = OrderStatus.CREATED;
	private Converter<OrderModel, OrderStatusUpdateElementData> orderStatusUpdateElementConverter;
	@Mock
	private OrderModel order;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final OrderStatusUpdateElementPopulator orderStatusUpdateElementPopulator = new OrderStatusUpdateElementPopulator();
		orderStatusUpdateElementConverter = new ConverterFactory<OrderModel, OrderStatusUpdateElementData, OrderStatusUpdateElementPopulator>()
				.create(OrderStatusUpdateElementData.class, orderStatusUpdateElementPopulator);

		given(order.getCode()).willReturn(ORDER_CODE);
		given(order.getStatus()).willReturn(ORDER_STATUS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenSourceIsNull()
	{
		orderStatusUpdateElementConverter.convert(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConvertWhenTargetIsNull()
	{
		orderStatusUpdateElementConverter.convert(mock(OrderModel.class), null);
	}

	@Test
	public void testConvert()
	{
		final OrderStatusUpdateElementData result = orderStatusUpdateElementConverter.convert(order);

		Assert.assertEquals(ORDER_CODE, result.getCode());
		Assert.assertEquals(ORDER_STATUS.getCode(), result.getStatus());
	}

	@Test
	public void testConvertWithResultCreated()
	{
		final OrderStatusUpdateElementData result = new OrderStatusUpdateElementData();
		orderStatusUpdateElementConverter.convert(order, result);

		Assert.assertEquals(ORDER_CODE, result.getCode());
		Assert.assertEquals(ORDER_STATUS.getCode(), result.getStatus());
	}

}
