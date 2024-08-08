/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.validator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import uk.ptr.cloudinary.validation.data.CartVoucherValidationData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Test suite for {@link CartVoucherValidator}
 */
@UnitTest
public class CartVoucherValidatorTest
{
	private static final String VOUCHER_CODE_1 = "test_voucher_1";
	private static final String VOUCHER_CODE_2 = "test_voucher_2";

	@Mock
	private VoucherFacade voucherFacade;

	private List<String> voucherCodeList;

	private CartVoucherValidator validator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		validator = new CartVoucherValidator(voucherFacade);
		voucherCodeList = new ArrayList<>();
		voucherCodeList.add(VOUCHER_CODE_1);
		voucherCodeList.add(VOUCHER_CODE_2);
	}

	@Test
	public void testValidateWithNoVouchers()
	{
		final List<CartVoucherValidationData> validationData = validator.validate(Collections.emptyList());

		Assert.assertEquals(true, CollectionUtils.isEmpty(validationData));
	}

	@Test
	public void testValidateWithAllValidateVouchers()
	{
		given(voucherFacade.checkVoucherCode(eq(VOUCHER_CODE_1))).willReturn(true);
		given(voucherFacade.checkVoucherCode(eq(VOUCHER_CODE_2))).willReturn(true);

		final List<CartVoucherValidationData> validationData = validator.validate(voucherCodeList);

		Assert.assertEquals(true, CollectionUtils.isEmpty(validationData));
	}

	@Test
	public void testValidateWithInvalidateVouchers()
	{
		given(voucherFacade.checkVoucherCode(eq(VOUCHER_CODE_1))).willReturn(true);
		given(voucherFacade.checkVoucherCode(eq(VOUCHER_CODE_2))).willReturn(false);

		final List<CartVoucherValidationData> validationData = validator.validate(voucherCodeList);

		Assert.assertEquals(1, validationData.size());
		Assert.assertEquals(VOUCHER_CODE_2, validationData.get(0).getSubject());
	}
}
