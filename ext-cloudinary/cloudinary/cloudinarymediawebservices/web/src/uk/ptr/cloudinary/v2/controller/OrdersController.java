/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderHistoriesData;
import de.hybris.platform.commercewebservicescommons.annotation.SiteChannelRestriction;
import de.hybris.platform.commercewebservicescommons.dto.order.CancellationRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CancellationRequestEntryInputWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.PaymentAuthorizationException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelEntryData;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelRequestData;
import de.hybris.platform.ordermanagementfacades.order.OmsOrderFacade;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import uk.ptr.cloudinary.exceptions.NoCheckoutCartException;
import uk.ptr.cloudinary.strategies.OrderCodeIdentificationStrategy;
import uk.ptr.cloudinary.v2.helper.OrdersHelper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

import static java.util.stream.Collectors.toList;



/**
 * Web Service Controller for the ORDERS resource. Most methods check orders of the user. Methods require authentication
 * and are restricted to https channel.
 */


@Controller
@RequestMapping(value = "/{baseSiteId}")
@Api(tags = "Orders")
public class OrdersController extends BaseCommerceController
{
	private static final Logger LOG = LoggerFactory.getLogger(OrdersController.class);

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;
	@Resource(name = "orderCodeIdentificationStrategy")
	private OrderCodeIdentificationStrategy orderCodeIdentificationStrategy;
	@Resource(name = "cartLoaderStrategy")
	private CartLoaderStrategy cartLoaderStrategy;
	@Resource(name = "ordersHelper")
	private OrdersHelper ordersHelper;
	@Resource(name = "omsOrderFacade")
	private OmsOrderFacade omsOrderFacade;
	@Resource(name = "cancellationRequestEntryInputListDTOValidator")
	private Validator cancellationRequestEntryInputListDTOValidator;
	@Resource(name = "wsCustomerFacade")
	private CustomerFacade customerFacade;

	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/orders/{code}", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'getOrder',#code,#fields)")
	@ResponseBody
	@ApiOperation(nickname = "getOrder", value = "Get a order.", notes = "Returns details of a specific order based on the order GUID (Globally Unique Identifier) or the order CODE. The response contains detailed order information.", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	@ApiBaseSiteIdParam
	public OrderWsDTO getOrder(
			@ApiParam(value = "Order GUID (Globally Unique Identifier) or order CODE", required = true) @PathVariable final String code,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final OrderData orderData;
		if (orderCodeIdentificationStrategy.isID(code))
		{
			orderData = orderFacade.getOrderDetailsForGUID(code);
		}
		else
		{
			orderData = orderFacade.getOrderDetailsForCodeWithoutUser(code);
		}

		return getDataMapper().map(orderData, OrderWsDTO.class, fields);
	}


	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/users/{userId}/orders/{code}", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,'getOrderForUserByCode',#code,#fields)")
	@ResponseBody
	@ApiOperation(nickname = "getUserOrders", value = "Get a order.", notes = "Returns specific order details based on a specific order code. The response contains detailed order information.")
	@ApiBaseSiteIdAndUserIdParam
	public OrderWsDTO getUserOrders(
			@ApiParam(value = "Order GUID (Globally Unique Identifier) or order CODE", required = true) @PathVariable final String code,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final OrderData orderData = orderFacade.getOrderDetailsForCode(code);
		return getDataMapper().map(orderData, OrderWsDTO.class, fields);
	}



	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getUserOrderHistory", value = "Get order history for user.", notes = "Returns order history data for all orders placed by a specified user for a specified base store. The response can display the results across multiple pages, if required.")
	@ApiBaseSiteIdAndUserIdParam
	public OrderHistoryListWsDTO getUserOrderHistory(
			@ApiParam(value = "Filters only certain order statuses. For example, statuses=CANCELLED,CHECKED_VALID would only return orders with status CANCELLED or CHECKED_VALID.") @RequestParam(required = false) final String statuses,
			@ApiParam(value = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "The number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Sorting method applied to the return results.") @RequestParam(required = false) final String sort,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletResponse response)
	{
		validateStatusesEnumValue(statuses);

		final OrderHistoryListWsDTO orderHistoryList = ordersHelper
				.searchOrderHistory(statuses, currentPage, pageSize, sort, addPaginationField(fields));

		setTotalCountHeader(response, orderHistoryList.getPagination());

		return orderHistoryList;
	}


	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.HEAD)
	@ResponseBody
	@ApiOperation(nickname = "countUserOrders", value = "Get total number of orders.", notes = "In the response header, the \"x-total-count\" indicates the total number of orders placed by a specified user for a specified base store.")
	@ApiBaseSiteIdAndUserIdParam
	public void countUserOrders(
			@ApiParam(value = "Filters only certain order statuses. For example, statuses=CANCELLED,CHECKED_VALID would only return orders with status CANCELLED or CHECKED_VALID.") @RequestParam(required = false) final String statuses,
			final HttpServletResponse response)
	{
		final OrderHistoriesData orderHistoriesData = ordersHelper.searchOrderHistory(statuses, 0, 1, null);

		setTotalCountHeader(response, orderHistoriesData.getPagination());
	}


	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_CLIENT", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@SiteChannelRestriction(allowedSiteChannelsProperty = API_COMPATIBILITY_B2C_CHANNELS)
	@ApiOperation(nickname = "placeOrder", value = "Place a order.", notes = "Authorizes the cart and places the order. The response contains the new order data.")
	@ApiBaseSiteIdAndUserIdParam
	public OrderWsDTO placeOrder(
			@ApiParam(value = "Cart code for logged in user, cart GUID for guest checkout", required = true) @RequestParam final String cartId,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
			throws PaymentAuthorizationException, InvalidCartException, NoCheckoutCartException
	{
		LOG.info("placeOrder");

		cartLoaderStrategy.loadCart(cartId);

		validateCartForPlaceOrder();

		//authorize
		if (!getCheckoutFacade().authorizePayment(null))
		{
			throw new PaymentAuthorizationException();
		}

		//placeorder
		final OrderData orderData = getCheckoutFacade().placeOrder();
		return getDataMapper().map(orderData, OrderWsDTO.class, fields);
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/users/{userId}/orders/{code}/cancellation", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "doCancelOrder", value = "Cancel an order.", notes = "Cancels an order partially or completely", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ApiBaseSiteIdAndUserIdParam
	public void doCancelOrder(@ApiParam(value = "Order code", required = true) @PathVariable final String code,
			@ApiParam(value = "Cancellation request input list for the current order.", required = true) @RequestBody final CancellationRequestEntryInputListWsDTO cancellationRequestEntryInputList)
	{
		validate(cancellationRequestEntryInputList, "cancellationRequestEntryInputList",
				cancellationRequestEntryInputListDTOValidator);
		validateUserForOrder(code);

		omsOrderFacade.createRequestOrderCancel(prepareCancellationRequestData(code, cancellationRequestEntryInputList));
	}

	/**
	 * Validates if the current user has access to the order
	 *
	 * @param code
	 * 		the order code
	 * @throws NotFoundException
	 * 		if current user has no access to the order
	 */
	protected void validateUserForOrder(final String code)
	{
		try
		{
			orderFacade.getOrderDetailsForCode(code);
		}
		catch (final UnknownIdentifierException ex)
		{
			LOG.warn("Order not found for the current user in current BaseStore", ex);
			throw new NotFoundException("Resource not found");
		}
	}

	/**
	 * It prepares the {@link OrderCancelRequestData} object by
	 * taking the order code and a map of order entry and cancel quantity and sets the user
	 *
	 * @param code
	 * 		which we want to request to cancel
	 * @param cancellationRequestEntryInputList
	 * 		map of order entry and cancel quantity
	 * @return Populated {@link OrderCancelRequestData}
	 */
	protected OrderCancelRequestData prepareCancellationRequestData(final String code,
			final CancellationRequestEntryInputListWsDTO cancellationRequestEntryInputList)
	{
		final OrderCancelRequestData cancellationRequest = new OrderCancelRequestData();

		final List<OrderCancelEntryData> cancellationEntries = cancellationRequestEntryInputList.getCancellationRequestEntryInputs()
				.stream().map(this::mapToOrderCancelEntryData).collect(toList());

		cancellationRequest.setUserId(customerFacade.getCurrentCustomerUid());
		cancellationRequest.setOrderCode(code);
		cancellationRequest.setEntries(cancellationEntries);

		return cancellationRequest;
	}

	protected OrderCancelEntryData mapToOrderCancelEntryData(final CancellationRequestEntryInputWsDTO entryInput)
	{
		final OrderCancelEntryData cancelEntry = new OrderCancelEntryData();

		cancelEntry.setOrderEntryNumber(entryInput.getOrderEntryNumber());
		cancelEntry.setCancelQuantity(entryInput.getQuantity());
		cancelEntry.setCancelReason(CancelReason.CUSTOMERREQUEST);

		return cancelEntry;
	}
}
