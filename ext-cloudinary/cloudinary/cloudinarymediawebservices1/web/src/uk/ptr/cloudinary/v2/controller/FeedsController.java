/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.commercewebservicescommons.dto.queues.OrderStatusUpdateElementListWsDTO;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import uk.ptr.cloudinary.formatters.WsDateFormatter;
import uk.ptr.cloudinary.queues.data.OrderStatusUpdateElementData;
import uk.ptr.cloudinary.queues.data.OrderStatusUpdateElementDataList;
import uk.ptr.cloudinary.queues.impl.OrderStatusUpdateQueue;

import javax.annotation.Resource;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;


@Controller
@RequestMapping(value = "/{baseSiteId}/feeds")
@Api(tags = "Feeds")
public class FeedsController extends BaseController
{
	@Resource(name = "wsDateFormatter")
	private WsDateFormatter wsDateFormatter;
	@Resource(name = "orderStatusUpdateQueue")
	private OrderStatusUpdateQueue orderStatusUpdateQueue;

	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/orders/statusfeed", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getOrderStatusFeed", value = "Get a list of orders with status updates.", notes =
			"Returns the orders that have changed status. Returns only the elements from the "
					+ "current baseSite that have been updated after the provided timestamp.", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	public OrderStatusUpdateElementListWsDTO getOrderStatusFeed(
			@ApiParam(value = "Only items newer than the given parameter are retrieved. This parameter should be in ISO-8601 format (for example, 2018-01-09T16:28:45+0000).", required = true) @RequestParam final String timestamp,
			@ApiParam(value = "Base site identifier", required = true) @PathVariable final String baseSiteId,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final Date timestampDate = wsDateFormatter.toDate(timestamp);
		final List<OrderStatusUpdateElementData> orderStatusUpdateElements = orderStatusUpdateQueue.getItems(timestampDate);
		filterOrderStatusQueue(orderStatusUpdateElements, baseSiteId);
		final OrderStatusUpdateElementDataList dataList = new OrderStatusUpdateElementDataList();
		dataList.setOrderStatusUpdateElements(orderStatusUpdateElements);
		return getDataMapper().map(dataList, OrderStatusUpdateElementListWsDTO.class, fields);
	}

	protected void filterOrderStatusQueue(final List<OrderStatusUpdateElementData> orders, final String baseSiteId)
	{
		final Iterator<OrderStatusUpdateElementData> dataIterator = orders.iterator();
		while (dataIterator.hasNext())
		{
			final OrderStatusUpdateElementData orderStatusUpdateData = dataIterator.next();
			if (!baseSiteId.equals(orderStatusUpdateData.getBaseSiteId()))
			{
				dataIterator.remove();
			}
		}
	}
}
