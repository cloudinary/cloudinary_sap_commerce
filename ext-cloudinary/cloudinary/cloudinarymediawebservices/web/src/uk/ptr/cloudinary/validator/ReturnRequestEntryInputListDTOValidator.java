/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.validator;

import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.ReturnRequestEntryInputWsDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class ReturnRequestEntryInputListDTOValidator implements Validator
{
	private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";

	@Override
	public boolean supports(final Class clazz)
	{
		return ReturnRequestEntryInputListWsDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final ReturnRequestEntryInputListWsDTO returnRequestEntryInputList = (ReturnRequestEntryInputListWsDTO) target;

		if (StringUtils.isEmpty(returnRequestEntryInputList.getOrderCode()))
		{
			errors.rejectValue("orderCode", FIELD_REQUIRED_MESSAGE_ID);
		}

		final List<ReturnRequestEntryInputWsDTO> returnRequestEntryInputs = returnRequestEntryInputList
				.getReturnRequestEntryInputs();

		if (CollectionUtils.isEmpty(returnRequestEntryInputs))
		{
			errors.rejectValue("returnRequestEntryInputs", FIELD_REQUIRED_MESSAGE_ID);
		}
		else
		{
			IntStream.range(0, returnRequestEntryInputs.size())
					.filter(i -> Objects.isNull(returnRequestEntryInputs.get(i).getOrderEntryNumber())).forEach(i -> errors
					.rejectValue(String.format("returnRequestEntryInputs[%d].orderEntryNumber", i), FIELD_REQUIRED_MESSAGE_ID));

			IntStream.range(0, returnRequestEntryInputs.size())
					.filter(i -> Objects.isNull(returnRequestEntryInputs.get(i).getQuantity())).forEach(
					i -> errors.rejectValue(String.format("returnRequestEntryInputs[%d].quantity", i), FIELD_REQUIRED_MESSAGE_ID));
		}
	}
}
