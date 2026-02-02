/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.b2bfrontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessage;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.b2bfrontend.constants.Sapproductconfigb2baddonWebConstants;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOrderIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationOrderIntegrationFacadeImpl;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CPQReorderControllerTest
{
	private static final String PRODUCT_NAME = "PRODUCT_NAME";
	private static final String ORDER_CODE = "order123";
	@InjectMocks
	private CPQReorderController classUnderTest;
	private CartModificationData cartModification;
	private RedirectAttributes redirectModel;

	@Mock
	private CheckoutFacade b2bCheckoutFacade;
	@Mock
	private CartFacade cartFacade;
	@Mock
	private ConfigurationOrderIntegrationFacade cpqOrderIntegrationFacade;

	@Before
	public void setUp()
	{
		redirectModel = new RedirectAttributesModelMap();
		cartModification = new CartModificationData();
		cartModification.setEntry(new OrderEntryData());
		cartModification.getEntry().setProduct(new ProductData());
		cartModification.getEntry().getProduct().setName(PRODUCT_NAME);
	}

	@Test
	public void testHandleCPQStatus_KB_NOT_VALID()
	{
		cartModification.setStatusCode(ConfigurationOrderIntegrationFacadeImpl.KB_NOT_VALID);
		classUnderTest.handleCPQStatus(redirectModel, cartModification);
		final List<GlobalMessage> messages = ((List<GlobalMessage>) redirectModel.getFlashAttributes().get(
				GlobalMessages.ERROR_MESSAGES_HOLDER));
		assertEquals(1, messages.size());
		assertEquals(CPQReorderController.MSG_CODE_REORDER_NOT_POSSIBLE, messages.get(0).getCode());
	}

	@Test
	public void testHandleCPQStatus_NO_STOCK()
	{
		cartModification.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
		classUnderTest.handleCPQStatus(redirectModel, cartModification);
		final List<GlobalMessage> messages = ((List<GlobalMessage>) redirectModel.getFlashAttributes().get(
				GlobalMessages.ERROR_MESSAGES_HOLDER));
		assertNull(messages);
	}

	@Test
	public void testHandleCoreStatus_KB_NOT_VALID()
	{
		cartModification.setStatusCode(ConfigurationOrderIntegrationFacadeImpl.KB_NOT_VALID);
		classUnderTest.handleCoreStatus(redirectModel, cartModification);
		final List<GlobalMessage> messages = ((List<GlobalMessage>) redirectModel.getFlashAttributes().get(
				GlobalMessages.ERROR_MESSAGES_HOLDER));
		assertNull(messages);
	}

	@Test
	public void testHandleCoreStatus_NO_STOCK()
	{
		cartModification.setStatusCode(CommerceCartModificationStatus.NO_STOCK);
		classUnderTest.handleCoreStatus(redirectModel, cartModification);
		final List<GlobalMessage> messages = ((List<GlobalMessage>) redirectModel.getFlashAttributes().get(
				GlobalMessages.ERROR_MESSAGES_HOLDER));
		assertEquals(1, messages.size());
		assertEquals(CPQReorderController.MSG_CODE_LESS_ITEMS, messages.get(0).getCode());
	}

	@Test
	public void testHandleCoreStatus_LOW_STOCK()
	{
		cartModification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
		cartModification.setQuantity(10);
		cartModification.setQuantityAdded(5);
		classUnderTest.handleCoreStatus(redirectModel, cartModification);
		final List<GlobalMessage> messages = ((List<GlobalMessage>) redirectModel.getFlashAttributes().get(
				GlobalMessages.ERROR_MESSAGES_HOLDER));
		assertEquals(1, messages.size());
		assertEquals(CPQReorderController.MSG_CODE_QUANTITY_ADJUSTED, messages.get(0).getCode());
	}

	@Test
	public void testReorder_default() throws CMSItemNotFoundException, InvalidCartException, CommerceCartModificationException,
			ParseException
	{
		given(cpqOrderIntegrationFacade.isReorderable(ORDER_CODE)).willReturn(true);
		final String view = classUnderTest.reorder(ORDER_CODE, redirectModel);
		assertEquals(Sapproductconfigb2baddonWebConstants.REDIRECT_TO_CHECKOUT, view);
	}

	@Test
	public void testReorder_cpq() throws CMSItemNotFoundException, InvalidCartException, CommerceCartModificationException,
			ParseException
	{
		given(cpqOrderIntegrationFacade.isReorderable(ORDER_CODE)).willReturn(false);
		given(cartFacade.validateCartData()).willReturn(Collections.singletonList(new CartModificationData()));
		final String view = classUnderTest.reorder(ORDER_CODE, redirectModel);
		assertEquals(Sapproductconfigb2baddonWebConstants.REDIRECT_TO_CART, view);
	}
}
