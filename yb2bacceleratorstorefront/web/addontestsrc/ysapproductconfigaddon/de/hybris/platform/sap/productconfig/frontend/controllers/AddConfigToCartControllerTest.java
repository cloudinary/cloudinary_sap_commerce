/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AddConfigToCartControllerTest extends AbstractProductConfigControllerTCBase
{

	private static final String CART_ITEM_KEY = "123";

	@InjectMocks
	private AddConfigToCartController classUnderTest;
	private OrderEntryData cartItem;

	@Mock
	private RedirectAttributes redirectAttributes;
	@Mock
	protected BindingResult bindingResults;
	@Mock
	private CartFacade cartFacade;



	@Before
	public void setUp()
	{
		super.injectMocks(classUnderTest);
		classUnderTest.setCartFacade(cartFacade);

		kbKey = createKbKey();
		csticList = createCsticsList();
		configData = createConfigurationDataWithGeneralGroupOnly();
		given(configurationProductLinkStrategy.getConfigIdForProduct(kbKey.getProductCode())).willReturn(CONFIG_ID);

		cartItem = new OrderEntryData();
		cartItem.setItemPK(CART_ITEM_KEY);
		final CartData cart = new CartData();
		cart.setEntries(Collections.singletonList(cartItem));
		given(cartFacade.getSessionCart()).willReturn(cart);
	}

	@Test
	public void testAddConfigToCartIsCorrect() throws Exception
	{
		given(configFacade.getConfiguration(configData)).willReturn(configData);
		given(configCartIntegrationFacade.addConfigurationToCart(configData)).willReturn(CART_ITEM_KEY);

		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.FALSE);

		classUnderTest.addConfigToCart(kbKey.getProductCode(), configData, bindingResults, model, redirectAttributes, request);

		verify(configFacade, times(1)).updateConfiguration(any(ConfigurationData.class));
		verify(configCartIntegrationFacade, times(1)).addConfigurationToCart(any(ConfigurationData.class));
		verify(redirectAttributes, times(1)).addFlashAttribute("addedToCart", Boolean.TRUE);
	}

	@Test
	public void testAddConfigToCartProblemWithProduct() throws Exception
	{
		given(configFacade.getConfiguration(configData)).willReturn(configData);
		given(configCartIntegrationFacade.addConfigurationToCart(configData))
				.willThrow(new CommerceCartModificationException(CART_ITEM_KEY));

		classUnderTest.addConfigToCart(kbKey.getProductCode(), configData, bindingResults, model, redirectAttributes, request);

		verify(configFacade, times(1)).updateConfiguration(any(ConfigurationData.class));
		verify(configCartIntegrationFacade, times(1)).addConfigurationToCart(any(ConfigurationData.class));

		verify(model, times(1)).addAttribute(eq(GlobalMessages.ERROR_MESSAGES_HOLDER), any(Collection.class));

	}

	@Test
	public void testAddConfigToCartFailed() throws Exception
	{
		final List<FieldError> fieldErrors = new ArrayList<>();
		final FieldError error = new FieldError("config", "cstic[0].value", "a", true, null, null, null);
		fieldErrors.add(error);

		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.TRUE);

		final String targetUrl = classUnderTest.addConfigToCart(kbKey.getProductCode(), configData, bindingResults, model,
				redirectAttributes, request);
		verify(configFacade, times(1)).updateConfiguration(any(ConfigurationData.class));
		verify(configCartIntegrationFacade, times(0)).addConfigurationToCart(any(ConfigurationData.class));
		verify(redirectAttributes, times(0)).addFlashAttribute("addedToCart", Boolean.TRUE);

		final String expectedTargetUrl = "redirect:/" + kbKey.getProductCode() + "/configuratorPage/CPQCONFIGURATOR";
		assertEquals(expectedTargetUrl, targetUrl);
	}

	@Test
	public void testAddConfigToCartTwice() throws Exception
	{
		given(configFacade.getConfiguration(configData)).willReturn(configData);
		given(configCartIntegrationFacade.addConfigurationToCart(configData)).willReturn(CART_ITEM_KEY);

		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.FALSE);

		classUnderTest.addConfigToCart(kbKey.getProductCode(), configData, bindingResults, model, redirectAttributes, request);

		verify(configFacade, times(1)).updateConfiguration(any(ConfigurationData.class));
		verify(configCartIntegrationFacade, times(1)).addConfigurationToCart(any(ConfigurationData.class));
		verify(redirectAttributes, times(1)).addFlashAttribute("addedToCart", Boolean.TRUE);

		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn("4711");

		classUnderTest.addConfigToCart(kbKey.getProductCode(), configData, bindingResults, model, redirectAttributes, request);

		verify(redirectAttributes, times(1)).addFlashAttribute("addedToCart", Boolean.FALSE);
	}

	@Test
	public void testReset() throws Exception
	{
		final UiStatus uiStatus = new UiStatus();

		final String targetUrl = classUnderTest.resetConfiguration(kbKey.getProductCode());
		verify(sessionAccessFacade, times(1)).removeUiStatusForProduct(kbKey.getProductCode());
		verify(configCartIntegrationFacade, times(1)).removeConfigurationLink(kbKey.getProductCode());

		assertEquals("redirect:/cart", targetUrl);
	}


	@Test
	public void testGetCartEntryNumber() throws BusinessException
	{
		given(configurationProductLinkStrategy.getConfigIdForProduct(PRODUCT_CODE)).willReturn(CONFIG_ID);
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn("123");

		final Integer expectedEntryNumber = Integer.valueOf(20);
		cartItem.setEntryNumber(expectedEntryNumber);
		final Integer cartEntryNumber = classUnderTest.getCartEntryNumber(PRODUCT_CODE);
		assertEquals(expectedEntryNumber, cartEntryNumber);
	}

	@Test
	public void testGetCartEntryNumber_noItem() throws BusinessException
	{

		given(configurationProductLinkStrategy.getConfigIdForProduct(PRODUCT_CODE)).willReturn(CONFIG_ID);
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn(null);


		final Integer cartEntryNumber = classUnderTest.getCartEntryNumber(PRODUCT_CODE);
		assertNull("null cart entry number expected", cartEntryNumber);
	}
}
