/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.validator;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;

import java.util.Calendar;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validates instances of {@link CCPaymentInfoData}.
 */
@Component("ccPaymentInfoValidator")
public class CCPaymentInfoValidator implements Validator
{
	private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";

	@Resource(name = "paymentAddressValidator")
	private Validator paymentAddressValidator;

	@Override
	public boolean supports(final Class clazz)
	{
		return CCPaymentInfoData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		final CCPaymentInfoData ccPaymentData = (CCPaymentInfoData) target;

		if (StringUtils.isNotBlank(ccPaymentData.getStartMonth()) && StringUtils.isNotBlank(ccPaymentData.getStartYear())
				&& StringUtils.isNotBlank(ccPaymentData.getExpiryMonth()) && StringUtils.isNotBlank(ccPaymentData.getExpiryYear()))
		{
			final Calendar start = Calendar.getInstance();
			start.set(Calendar.DAY_OF_MONTH, 0);
			start.set(Calendar.MONTH, Integer.parseInt(ccPaymentData.getStartMonth()) - 1);
			start.set(Calendar.YEAR, Integer.parseInt(ccPaymentData.getStartYear()) - 1);

			final Calendar expiration = Calendar.getInstance();
			expiration.set(Calendar.DAY_OF_MONTH, 0);
			expiration.set(Calendar.MONTH, Integer.parseInt(ccPaymentData.getExpiryMonth()) - 1);
			expiration.set(Calendar.YEAR, Integer.parseInt(ccPaymentData.getExpiryYear()) - 1);

			if (start.after(expiration))
			{
				errors.rejectValue("startMonth", "payment.startDate.invalid");
			}
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountHolderName", FIELD_REQUIRED_MESSAGE_ID);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardType", FIELD_REQUIRED_MESSAGE_ID);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "cardNumber", FIELD_REQUIRED_MESSAGE_ID);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "expiryMonth", FIELD_REQUIRED_MESSAGE_ID);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "expiryYear", FIELD_REQUIRED_MESSAGE_ID);

		paymentAddressValidator.validate(ccPaymentData, errors);
	}
}
