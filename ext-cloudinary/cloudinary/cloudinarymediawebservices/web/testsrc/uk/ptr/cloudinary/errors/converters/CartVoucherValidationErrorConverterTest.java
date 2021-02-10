/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.errors.converters;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import uk.ptr.cloudinary.validation.data.CartVoucherValidationData;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;


/**
 * Test suite for {@link CartVoucherValidationErrorConverter}
 */
@UnitTest
public class CartVoucherValidationErrorConverterTest
{
	private static final String APPLIED_VOUCHER_EXPIRED = "applied.voucher.expired";
	private static final String EXPIRED_VOUCHER_CODE = "expiredVoucherCode";
	private static final String TYPE = "cartVoucherError";
	private static final String SUBJECT_TYPE = "voucher";
	private static final String REASON_INVALID = "expired";

	@Mock
	private I18NService i18NService;
	@Mock
	private MessageSource messageSource;

	private CartVoucherValidationData validationData;

	private CartVoucherValidationErrorConverter converter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		converter = new CartVoucherValidationErrorConverter(i18NService, messageSource);

		validationData = new CartVoucherValidationData();
		validationData.setSubject(EXPIRED_VOUCHER_CODE);

		given(messageSource.getMessage(eq(APPLIED_VOUCHER_EXPIRED), any(Object[].class), anyString(), any(Locale.class)))
				.willReturn(APPLIED_VOUCHER_EXPIRED);
	}

	@Test
	public void testPopulate()
	{
		final ErrorWsDTO errorWsDTO = new ErrorWsDTO();
		converter.populate(validationData, errorWsDTO);

		Assert.assertEquals(TYPE, errorWsDTO.getType());
		Assert.assertEquals(SUBJECT_TYPE, errorWsDTO.getSubjectType());
		Assert.assertEquals(EXPIRED_VOUCHER_CODE, errorWsDTO.getSubject());
		Assert.assertEquals(REASON_INVALID, errorWsDTO.getReason());
		Assert.assertEquals(APPLIED_VOUCHER_EXPIRED, errorWsDTO.getMessage());
	}
}
