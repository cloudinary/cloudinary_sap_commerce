/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.helper;

import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderHistoriesData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryListWsDTO;
import de.hybris.platform.core.enums.OrderStatus;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import static uk.ptr.cloudinary.constants.YcommercewebservicesConstants.ENUM_VALUES_SEPARATOR;


@Component
public class OrdersHelper extends AbstractHelper
{
	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	@Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,'DTO',#statuses,#currentPage,#pageSize,#sort,#fields)")
	public OrderHistoryListWsDTO searchOrderHistory(final String statuses, final int currentPage, final int pageSize,
			final String sort, final String fields)
	{
		final OrderHistoriesData orderHistoriesData = searchOrderHistory(statuses, currentPage, pageSize, sort);
		return getDataMapper().map(orderHistoriesData, OrderHistoryListWsDTO.class, fields);
	}

	@Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,'Data',#statuses,#currentPage,#pageSize,#sort)")
	public OrderHistoriesData searchOrderHistory(final String statuses, final int currentPage, final int pageSize,
			final String sort)
	{
		final PageableData pageableData = createPageableData(currentPage, pageSize, sort);

		final OrderHistoriesData orderHistoriesData;
		if (statuses != null)
		{
			final Set<OrderStatus> statusSet = extractOrderStatuses(statuses);
			orderHistoriesData = createOrderHistoriesData(
					orderFacade.getPagedOrderHistoryForStatuses(pageableData, statusSet.toArray(new OrderStatus[statusSet.size()])));
		}
		else
		{
			orderHistoriesData = createOrderHistoriesData(orderFacade.getPagedOrderHistoryForStatuses(pageableData));
		}
		return orderHistoriesData;
	}

	protected Set<OrderStatus> extractOrderStatuses(final String statuses)
	{
		final String[] statusesStrings = statuses.split(ENUM_VALUES_SEPARATOR);

		final Set<OrderStatus> statusesEnum = new HashSet<>();
		for (final String status : statusesStrings)
		{
			statusesEnum.add(OrderStatus.valueOf(status));
		}
		return statusesEnum;
	}

	protected OrderHistoriesData createOrderHistoriesData(final SearchPageData<OrderHistoryData> result)
	{
		final OrderHistoriesData orderHistoriesData = new OrderHistoriesData();

		orderHistoriesData.setOrders(result.getResults());
		orderHistoriesData.setSorts(result.getSorts());
		orderHistoriesData.setPagination(result.getPagination());

		return orderHistoriesData;
	}
}
