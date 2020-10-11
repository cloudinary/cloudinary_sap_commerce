/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.validator;

import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.commercewebservicescommons.dto.user.PrincipalWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.UserGroupWsDTO;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class PrincipalListDTOValidator implements Validator
{
	private UserMatchingService userMatchingService;
	private String fieldPath;
	private boolean canBeEmpty = true;

	@Override
	public boolean supports(final Class clazz)
	{
		return List.class.isAssignableFrom(clazz) || UserGroupWsDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final List<PrincipalWsDTO> list = (List<PrincipalWsDTO>) (fieldPath == null ? target : errors.getFieldValue(fieldPath));
		final String uidFieldName = fieldPath == null ? "uid" : fieldPath + ".uid";

		if (CollectionUtils.isEmpty(list))
		{
			setEmptyListError(errors);
		}
		else
		{
			validateErrorsForPrincipals(list, errors, uidFieldName);
		}
	}

	protected void validateErrorsForPrincipals(final List<PrincipalWsDTO> list, final Errors errors, final String uidFieldName)
	{
		for (final PrincipalWsDTO principal : list)
		{
			if (setUidEmptyError(principal, errors, uidFieldName) || setUserNotExistError(principal, errors))
			{
				break;
			}
		}
	}

	protected boolean setUidEmptyError(final PrincipalWsDTO principal, final Errors errors, final String uidFieldName)
	{
		if (StringUtils.isEmpty(principal.getUid()))
		{
			errors.reject("field.withName.required", new String[] { uidFieldName }, "Field {0} is required");
			return true;
		}
		else
		{
			return false;
		}
	}

	protected boolean setUserNotExistError(final PrincipalWsDTO principal, final Errors errors)
	{
		if (!getUserMatchingService().isUserExisting(principal.getUid()))
		{
			errors.reject("user.doesnt.exist", new String[] { principal.getUid() },
					"User {0} doesn''t exist or you have no privileges");
			return true;
		}
		else
		{
			return false;
		}
	}

	protected void setEmptyListError(final Errors errors)
	{
		if (!canBeEmpty)
		{
			errors.reject("field.required");
		}
	}

	public String getFieldPath()
	{
		return fieldPath;
	}

	public void setFieldPath(final String fieldPath)
	{
		this.fieldPath = fieldPath;
	}

	public boolean getCanBeEmpty()
	{
		return canBeEmpty;
	}

	public void setCanBeEmpty(final boolean canBeEmpty)
	{
		this.canBeEmpty = canBeEmpty;
	}

	protected UserMatchingService getUserMatchingService()
	{
		return userMatchingService;
	}

	@Required
	public void setUserMatchingService(final UserMatchingService userMatchingService)
	{
		this.userMatchingService = userMatchingService;
	}
}
