/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.populator;

import de.hybris.platform.commercewebservicescommons.dto.user.UserSignUpWsDTO;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Populates {@link UserSignUpWsDTO} instance based on http request parameters:<br>
 * <ul>
 * <li>uid</li>
 * <li>password</li>
 * <li>titleCode</li>
 * <li>firstName</li>
 * <li>lastName</li>
 * </ul>
 */
@Component("HttpRequestUserSignUpDTOPopulator")
public class HttpRequestUserSignUpDTOPopulator implements Populator<HttpServletRequest, UserSignUpWsDTO>
{
	private static final String UID = "login";
	private static final String PASSWORD = "password";
	private static final String TITLECODE = "titleCode";
	private static final String FIRSTNAME = "firstName";
	private static final String LASTNAME = "lastName";

	@Override
	public void populate(final HttpServletRequest source, final UserSignUpWsDTO target) throws ConversionException //NOSONAR
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setUid(StringUtils.defaultString(source.getParameter(UID), target.getUid()));
		target.setPassword(StringUtils.defaultString(source.getParameter(PASSWORD), target.getPassword()));
		target.setTitleCode(StringUtils.defaultString(source.getParameter(TITLECODE), target.getTitleCode()));
		target.setFirstName(StringUtils.defaultString(source.getParameter(FIRSTNAME), target.getFirstName()));
		target.setLastName(StringUtils.defaultString(source.getParameter(LASTNAME), target.getLastName()));
	}

}
