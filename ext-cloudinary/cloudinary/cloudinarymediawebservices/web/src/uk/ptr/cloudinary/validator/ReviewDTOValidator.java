/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.validator;

import static de.hybris.platform.customerreview.model.CustomerReviewModel.COMMENT;
import static de.hybris.platform.customerreview.model.CustomerReviewModel.HEADLINE;
import static de.hybris.platform.customerreview.model.CustomerReviewModel.RATING;

import de.hybris.platform.commercewebservicescommons.dto.product.ReviewWsDTO;

import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class ReviewDTOValidator implements Validator
{
	private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";
	private static final double RATING_MIN = 1.0d;
	private static final double RATING_MAX = 5.0d;

	@Override
	public boolean supports(final Class clazz)
	{
		return ReviewWsDTO.class.equals(clazz);
	}

	@Override
	public void validate(final Object target, final Errors errors)
	{
		ValidationUtils.rejectIfEmpty(errors, HEADLINE, FIELD_REQUIRED_MESSAGE_ID);
		ValidationUtils.rejectIfEmpty(errors, COMMENT, FIELD_REQUIRED_MESSAGE_ID);
		validateRating(errors);
	}

	protected void validateRating(final Errors errors)
	{
		Assert.notNull(errors, "Errors object must not be null");
		final Double rating = (Double) errors.getFieldValue(RATING);

		if (rating == null)
		{
			errors.rejectValue(RATING, FIELD_REQUIRED_MESSAGE_ID);
		}
		else
		{
			if (rating.doubleValue() < RATING_MIN || rating.doubleValue() > RATING_MAX)
			{
				errors.rejectValue(RATING, "review.rating.invalid");
			}
		}
	}

}
