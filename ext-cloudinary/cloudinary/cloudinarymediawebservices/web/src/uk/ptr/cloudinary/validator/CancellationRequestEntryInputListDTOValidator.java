/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.validator;

import de.hybris.platform.commercewebservicescommons.dto.order.CancellationRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CancellationRequestEntryInputWsDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class CancellationRequestEntryInputListDTOValidator implements Validator
{
	private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";

	@Override
	public boolean supports(final Class clazz)
	{
		return CancellationRequestEntryInputListWsDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final CancellationRequestEntryInputListWsDTO cancellationRequestEntryInputList = (CancellationRequestEntryInputListWsDTO) target;

		final List<CancellationRequestEntryInputWsDTO> cancellationRequestEntryInputs = cancellationRequestEntryInputList
				.getCancellationRequestEntryInputs();
		if (CollectionUtils.isEmpty(cancellationRequestEntryInputs))
		{
			errors.rejectValue("cancellationRequestEntryInputs", FIELD_REQUIRED_MESSAGE_ID);
		}
		else
		{
			IntStream.range(0, cancellationRequestEntryInputs.size())
					.filter(i -> Objects.isNull(cancellationRequestEntryInputs.get(i).getOrderEntryNumber())).forEach(i -> errors
					.rejectValue(String.format("cancellationRequestEntryInputs[%d].orderEntryNumber", i), FIELD_REQUIRED_MESSAGE_ID));

			IntStream.range(0, cancellationRequestEntryInputs.size())
					.filter(i -> Objects.isNull(cancellationRequestEntryInputs.get(i).getQuantity())).forEach(i -> errors
					.rejectValue(String.format("cancellationRequestEntryInputs[%d].quantity", i), FIELD_REQUIRED_MESSAGE_ID));

		}
	}
}
