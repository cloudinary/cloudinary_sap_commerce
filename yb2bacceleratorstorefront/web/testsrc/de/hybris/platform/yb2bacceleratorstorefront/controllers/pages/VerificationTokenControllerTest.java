/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.controllers.pages;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.forms.LoginForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.commercefacades.verificationtoken.VerificationTokenFacade;
import de.hybris.platform.commercefacades.verificationtoken.data.CreateVerificationTokenInputData;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.support.BindingAwareModelMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VerificationTokenControllerTest
{
	private final Model model = Mockito.spy(new BindingAwareModelMap());
	@InjectMocks
	private VerificationTokenController controller;

	@Mock
	private VerificationTokenFacade verificationTokenFacade;

	@Mock
	private BindingResult bindingResult;


	@Test
	public void testCreateVerificationToken()
	{
		String expectedTokenId = "tokenId";
		when(verificationTokenFacade.createVerificationToken(any(CreateVerificationTokenInputData.class))).thenReturn(
				expectedTokenId);
		final String tokenId = controller.createVerificationToken("testUser", "testPassword", model, new LoginForm(), bindingResult);

		Assert.assertEquals(tokenId, expectedTokenId);
		verify(verificationTokenFacade).createVerificationToken(any(CreateVerificationTokenInputData.class));
	}

	@Test
	public void testCreateVerificationTokenForRegistration_CXEC_45023(){
		String expectedTokenId = "tokenId";
		when(verificationTokenFacade.createVerificationToken(any(CreateVerificationTokenInputData.class))).thenReturn(
				expectedTokenId);
		final String tokenId = controller.createVerificationTokenForRegistration("testUser", model, new RegisterForm(), bindingResult);

		Assert.assertEquals(tokenId, expectedTokenId);
		verify(verificationTokenFacade).createVerificationToken(any(CreateVerificationTokenInputData.class));
	}
}
