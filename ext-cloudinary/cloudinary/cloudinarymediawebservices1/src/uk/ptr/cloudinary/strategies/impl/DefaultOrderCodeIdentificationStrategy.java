/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.strategies.impl;


import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import uk.ptr.cloudinary.strategies.OrderCodeIdentificationStrategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link uk.ptr.cloudinary.strategies.OrderCodeIdentificationStrategy}.
 */
public class DefaultOrderCodeIdentificationStrategy implements OrderCodeIdentificationStrategy
{
	private String idPattern;

	/**
	 * Checks if given string is GUID
	 *
	 * @param potentialId
	 * 		- string to check
	 * @return result
	 */
	@Override
	public boolean isID(final String potentialId)
	{
		validateParameterNotNull(potentialId, "identifier must not be null");
		if (potentialId == null || potentialId.isEmpty())
		{
			return false;
		}

		final Pattern pattern = Pattern.compile(this.idPattern);
		final Matcher matcher = pattern.matcher(potentialId);
		return matcher.find();
	}

	@Required
	public void setIdPattern(final String idPattern)
	{
		this.idPattern = idPattern;
	}
}
