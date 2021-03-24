/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commercewebservicescommons.dto.search.pagedata.PaginationWsDTO;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;


/**
 * Base Controller. It defines the exception handler to be used by all controllers. Extending controllers can add or
 * overwrite the exception handler if needed.
 */
@Controller
public class BaseController
{
	protected static final String DEFAULT_PAGE_SIZE = "20";
	protected static final String DEFAULT_CURRENT_PAGE = "0";
	protected static final String BASIC_FIELD_SET = FieldSetLevelHelper.BASIC_LEVEL;
	protected static final String DEFAULT_FIELD_SET = FieldSetLevelHelper.DEFAULT_LEVEL;
	protected static final String HEADER_TOTAL_COUNT = "X-Total-Count";
	protected static final String INVALID_REQUEST_BODY_ERROR_MESSAGE = "Request body is invalid or missing";

	private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	protected static String logParam(final String paramName, final Long paramValue)
	{
		return paramName + " = " + paramValue;
	}

	protected static String logParam(final String paramName, final String paramValue)
	{
		return paramName + " = " + logValue(paramValue);
	}

	protected static String logValue(final String paramValue)
	{
		return "'" + sanitize(paramValue) + "'";
	}

	protected static String sanitize(final String input)
	{
		return YSanitizer.sanitize(input);
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler({ ModelNotFoundException.class })
	public ErrorListWsDTO handleModelNotFoundException(final Exception ex)
	{
		LOG.info("Handling Exception for this request - {} - {}", ex.getClass().getSimpleName(), sanitize(ex.getMessage()));
		LOG.debug("An exception occurred!", ex);

		return handleErrorInternal(UnknownIdentifierException.class.getSimpleName(), ex.getMessage());
	}

	protected ErrorListWsDTO handleErrorInternal(final String type, final String message)
	{
		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		final ErrorWsDTO error = new ErrorWsDTO();
		error.setType(type.replace("Exception", "Error"));
		error.setMessage(sanitize(message));
		errorListDto.setErrors(Lists.newArrayList(error));
		return errorListDto;
	}

	protected void validate(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}

	/**
	 * Adds pagination field to the 'fields' parameter
	 *
	 * @param fields
	 * @return fields with pagination
	 */
	protected String addPaginationField(final String fields)
	{
		String fieldsWithPagination = fields;

		if (StringUtils.isNotBlank(fieldsWithPagination))
		{
			fieldsWithPagination += ",";
		}
		fieldsWithPagination += "pagination";

		return fieldsWithPagination;
	}

	protected void setTotalCountHeader(final HttpServletResponse response, final PaginationWsDTO paginationDto)
	{
		if (paginationDto != null && paginationDto.getTotalResults() != null)
		{
			response.setHeader(HEADER_TOTAL_COUNT, String.valueOf(paginationDto.getTotalResults()));
		}
	}

	protected void setTotalCountHeader(final HttpServletResponse response, final PaginationData paginationDto)
	{
		if (paginationDto != null)
		{
			response.setHeader(HEADER_TOTAL_COUNT, String.valueOf(paginationDto.getTotalNumberOfResults()));
		}
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

	protected void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler({ DuplicateUidException.class })
	public ErrorListWsDTO handleDuplicateUidException(final DuplicateUidException ex)
	{
		LOG.debug("DuplicateUidException", ex);
		return handleErrorInternal("DuplicateUidException", ex.getMessage());
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler({ HttpMessageNotReadableException.class })
	public ErrorListWsDTO handleHttpMessageNotReadableException(final Exception ex)
	{
		LOG.debug(INVALID_REQUEST_BODY_ERROR_MESSAGE, ex);
		return handleErrorInternal(HttpMessageNotReadableException.class.getSimpleName(), INVALID_REQUEST_BODY_ERROR_MESSAGE);
	}
}
