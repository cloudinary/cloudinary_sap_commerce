/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ReturnRequestStatusWsDTOType;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import uk.ptr.cloudinary.v2.helper.OrderReturnsHelper;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
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


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/orderReturns")
@Api(tags = "Return Requests")
public class OrderReturnsController extends BaseCommerceController
{
	private static final Logger LOG = LoggerFactory.getLogger(OrderReturnsController.class);

	@Resource(name = "orderReturnsHelper")
	private OrderReturnsHelper orderReturnsHelper;

	@Resource(name = "returnRequestEntryInputListDTOValidator")
	private Validator returnRequestEntryInputListDTOValidator;

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getReturnRequests", value = "Gets the user's return requests history", notes = "Returns order return request data associated with a specified user for a specified base store.", produces = MediaType.APPLICATION_JSON)
	@ApiBaseSiteIdAndUserIdParam
	public ReturnRequestListWsDTO getReturnRequests(
			@ApiParam(value = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "The number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Sorting method applied to the return results.") @RequestParam(required = false) final String sort,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return orderReturnsHelper.searchOrderReturnRequests(currentPage, pageSize, sort, fields);
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(value = "/{returnRequestCode}", method = RequestMethod.GET)
	@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 120)
	@ResponseBody
	@ApiOperation(nickname = "getReturnRequest", value = "Get the details of a return request.", notes = "Returns specific order return request details based on a specific return request code. The response contains detailed order return request information.", produces = MediaType.APPLICATION_JSON)
	@ApiBaseSiteIdAndUserIdParam
	public ReturnRequestWsDTO getReturnRequest(
			@ApiParam(value = "Order return request code", required = true) @PathVariable final String returnRequestCode,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return orderReturnsHelper.getOrderReturnRequest(returnRequestCode, fields);
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@ApiOperation(nickname = "updateReturnRequest", value = "Updates the order return request.", notes = "Updates the order return request. Only cancellation of the request is supported by setting the attribute status to CANCELLING. Cancellation of the return request cannot be reverted", produces = MediaType.APPLICATION_JSON)
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/{returnRequestCode}", method = RequestMethod.PATCH)
	@ApiBaseSiteIdAndUserIdParam
	public void updateReturnRequest(
			@ApiParam(value = "Order return request code", required = true) @PathVariable final String returnRequestCode,
			@ApiParam(value = "Return request modification object.", required = true) @RequestBody final ReturnRequestModificationWsDTO returnRequestModification)
	{
		if (returnRequestModification.getStatus() == ReturnRequestStatusWsDTOType.CANCELLING)
		{
			orderReturnsHelper.cancelOrderReturnRequest(returnRequestCode);
		}
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	@ApiOperation(nickname = "createReturnRequest", value = "Create an order return request.", notes = "Creates an order return request.", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ApiBaseSiteIdAndUserIdParam
	public ReturnRequestWsDTO createReturnRequest(
			@ApiParam(value = "Return request input list for the current order.", required = true) @RequestBody final ReturnRequestEntryInputListWsDTO returnRequestEntryInputList,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = FieldSetLevelHelper.DEFAULT_LEVEL) final String fields)
	{
		validate(returnRequestEntryInputList, "returnRequestEntryInputList", returnRequestEntryInputListDTOValidator);
		return orderReturnsHelper.createOrderReturnRequest(returnRequestEntryInputList, fields);
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ResponseBody
	@ExceptionHandler({ UnknownIdentifierException.class })
	public ErrorListWsDTO handleNotFoundExceptions(final Exception ex)
	{
		LOG.debug("Unknown identifier error", ex);
		return handleErrorInternal(NotFoundException.class.getSimpleName(), ex.getMessage());
	}
}
