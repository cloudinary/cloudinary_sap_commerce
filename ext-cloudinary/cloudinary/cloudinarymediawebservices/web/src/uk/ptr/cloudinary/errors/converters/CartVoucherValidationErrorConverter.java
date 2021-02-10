/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.errors.converters;

import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import uk.ptr.cloudinary.validation.data.CartVoucherValidationData;

import java.util.Locale;

import org.springframework.context.MessageSource;


/**
 * Converts {@link CartVoucherValidationData} to a {@link de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO}.
 */
public class CartVoucherValidationErrorConverter extends AbstractConverter<CartVoucherValidationData, ErrorWsDTO>
{
	private static final String TYPE = "cartVoucherError";
	private static final String SUBJECT_TYPE = "voucher";
	private static final String REASON_INVALID = "expired";
	private static final String APPLIED_VOUCHER_EXPIRED = "applied.voucher.expired";
	private final I18NService i18NService;
	private final MessageSource messageSource;

	public CartVoucherValidationErrorConverter(final I18NService i18NService, final MessageSource messageSource)
	{
		this.i18NService = i18NService;
		this.messageSource = messageSource;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	protected MessageSource getMessageSource()
	{
		return messageSource;
	}

	@Override
	public void populate(final CartVoucherValidationData cartVoucherValidationData, final ErrorWsDTO errorWsDTO)
	{
		errorWsDTO.setType(TYPE);
		errorWsDTO.setSubjectType(SUBJECT_TYPE);
		errorWsDTO.setSubject(cartVoucherValidationData.getSubject());
		errorWsDTO.setReason(REASON_INVALID);
		final Object[] args = new Object[]{ cartVoucherValidationData.getSubject() };
		final Locale currentLocale = getI18NService().getCurrentLocale();
		errorWsDTO.setMessage(getMessageSource().getMessage(APPLIED_VOUCHER_EXPIRED, args, APPLIED_VOUCHER_EXPIRED, currentLocale));
	}
}
