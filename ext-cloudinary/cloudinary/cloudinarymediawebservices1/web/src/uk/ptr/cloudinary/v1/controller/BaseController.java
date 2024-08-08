/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v1.controller;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;


/**
 * Base Controller. It defines the exception handler to be used by all controllers. Extending controllers can add or
 * overwrite the exception handler if needed.
 */
@Controller("baseControllerV1")
public class BaseController
{

	protected static final String DEFAULT_PAGE_SIZE = "20";
	protected static final String DEFAULT_CURRENT_PAGE = "0";
	private static final Logger LOG = Logger.getLogger(BaseController.class);

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler(
	{ ModelNotFoundException.class })
	public ErrorListWsDTO handleModelNotFoundException(final Exception ex)
	{
		LOG.info("Handling Exception for this request - " + ex.getClass().getSimpleName() + " - " + sanitize(ex.getMessage()));
		if (LOG.isDebugEnabled())
		{
			LOG.debug(ex);
		}
		
		return handleErrorInternal(UnknownIdentifierException.class.getSimpleName(), ex.getMessage());
	}

	protected ErrorListWsDTO handleErrorInternal(final String type, final String message)
	{
		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		final ErrorWsDTO error = new ErrorWsDTO();
		error.setType(type.replace("Exception", "Error"));
		error.setMessage(message);
		errorListDto.setErrors(Lists.newArrayList(error));
		return errorListDto;
	}

	protected static String sanitize(final String input)
	{
		return YSanitizer.sanitize(input);
	}
}
