/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;




@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CartConfigureProductControllerTest extends AbstractProductConfigControllerTCBase
{

	private static final String CART_ITEM_KEY = "entry123";
	private static final int ENTRY_NUMBER = 1;
	@InjectMocks
	private CartConfigureProductController classUnderTest;
	private OrderEntryData cartItem;

	@Mock
	private CartFacade cartFacade;
	private final CartData sessionCart = new CartData();

	@Before
	public void setUp()
	{
		injectMocks(classUnderTest);
		classUnderTest.setCartFacade(cartFacade);

		kbKey = createKbKey();
		csticList = createCsticsList();
		configData = createConfigurationDataWithGeneralGroupOnly();

		cartItem = new OrderEntryData();
		cartItem.setItemPK(CART_ITEM_KEY);
		cartItem.setProduct(productData);
		cartItem.setEntryNumber(ENTRY_NUMBER);
		cartItem.setQuantity(Long.valueOf(1));

		sessionCart.setEntries(Arrays.asList(cartItem));
		given(cartFacade.getSessionCart()).willReturn(sessionCart);


	}

	@Test
	public void testHandleUiStatusExisting() throws Exception
	{
		initializeFirstCall();
		final UiStatus uiStatus = new UiStatus();
		uiStatus.setHideImageGallery(true);
		uiStatus.setQuantity(42);
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn(CART_ITEM_KEY);
		given(sessionAccessFacade.getUiStatusForCartEntry(CART_ITEM_KEY)).willReturn(uiStatus);
		final UiStatus stat = classUnderTest.handleUIStatus(configData);
		assertNotNull(stat);
		assertTrue(configData.isHideImageGallery());
		assertEquals(42, configData.getQuantity());
	}

	@Test
	public void testHandleUiStatusNotExisting() throws Exception
	{

		configData.setQuantity(22);
		final UiStatus status = classUnderTest.handleUIStatus(configData);
		assertNotNull(status);
		assertTrue(status.isHideImageGallery());
		assertEquals(22, status.getQuantity());
	}

	@Test
	public void testConfigureCartEntryAfterLogout() throws CommerceCartModificationException
	{
		// this happens if a user presses logout on config screen while configuring a cart entry
		// after logout cart is empty
		given(cartFacade.getSessionCart()).willReturn(new CartData());
		final String view = classUnderTest.configureCartEntry(0, model, request);
		assertEquals(AbstractController.REDIRECT_PREFIX + AbstractController.ROOT, view);
	}

	@Test
	public void testConfigureCartEntryOnExistingDraftError() throws CommerceCartModificationException
	{
		given(configCartIntegrationFacade.configureCartItemOnExistingDraft(CART_ITEM_KEY)).willReturn(null);

		final String view = classUnderTest.configureCartEntryOnExistingDraft(ENTRY_NUMBER, model, request);
		assertEquals(AbstractController.REDIRECT_PREFIX + SapproductconfigfrontendWebConstants.CART_PREFIX + ENTRY_NUMBER
				+ SapproductconfigfrontendWebConstants.CART_CONFIG_URL, view);
	}

	@Test
	public void testConfigureCartEntryOnExistingDraft() throws Exception
	{
		initializeFirstCall();
		given(configCartIntegrationFacade.configureCartItemOnExistingDraft(CART_ITEM_KEY)).willReturn(configData);

		final String view = classUnderTest.configureCartEntryOnExistingDraft(ENTRY_NUMBER, model, request);
		assertEquals(SapproductconfigfrontendWebConstants.CONFIG_PAGE_VIEW_NAME, view);
	}

	@Test
	public void testDetermineGroupToDisplay()
	{
		final UiStatus uiStatus = new UiStatus();
		final UiGroupStatus uiGroup1 = new UiGroupStatus();
		uiGroup1.setCollapsed(false);
		uiGroup1.setId("_GEN");
		final UiGroupStatus uiGroup2 = new UiGroupStatus();
		uiGroup2.setCollapsed(true);
		final List<UiGroupStatus> uiGroups = Arrays.asList(uiGroup1, uiGroup2);
		uiGroup2.setId("GROUP_1");
		uiStatus.setGroups(uiGroups);

		final UiGroupData group1 = new UiGroupData();
		group1.setGroupType(GroupType.CSTIC_GROUP);
		group1.setGroupStatus(GroupStatusType.ERROR);
		group1.setCollapsed(true);
		group1.setId("GROUP_1");
		group1.setCstics(new ArrayList<>());

		configData.getGroups().add(group1);
		final ArgumentCaptor<UiStatus> uiStatusCaptor = ArgumentCaptor.forClass(UiStatus.class);

		classUnderTest.determineGroupToDisplay(configData, uiStatus);

		verify(sessionAccessFacade, times(1)).setUiStatusForProduct(eq(configData.getKbKey().getProductCode()),
				uiStatusCaptor.capture());

		final UiStatus newUiStatus = uiStatusCaptor.getValue();
		assertEquals("GROUP_1", newUiStatus.getGroupIdToDisplay());
		final UiGroupStatus resultUiGroupStatus = newUiStatus.getGroups().get(1);
		assertEquals("The failure group 'GROUP_1' is expected", "GROUP_1", resultUiGroupStatus.getId());
		assertFalse("It is expected, that the failure group is not collapsed", resultUiGroupStatus.isCollapsed());
	}

	@Test
	public void testDetermineGroupToDisplayWithoutErrorGroup()
	{
		final UiStatus uiStatus = new UiStatus();
		final UiGroupStatus uiGroup1 = new UiGroupStatus();
		uiGroup1.setCollapsed(false);
		uiGroup1.setId("_GEN");
		final List<UiGroupStatus> uiGroups = Arrays.asList(uiGroup1);
		uiStatus.setGroups(uiGroups);

		final ArgumentCaptor<UiStatus> uiStatusCaptor = ArgumentCaptor.forClass(UiStatus.class);

		classUnderTest.determineGroupToDisplay(configData, uiStatus);

		verify(sessionAccessFacade, times(1)).setUiStatusForProduct(eq(configData.getKbKey().getProductCode()),
				uiStatusCaptor.capture());

		final UiStatus newUiStatus = uiStatusCaptor.getValue();
		assertNull("No Error in config, so NULL is expeceted", newUiStatus.getFirstErrorCsticId());
	}

	@Test
	public void testGetCartEntry() throws CommerceCartModificationException
	{
		final OrderEntryData cartEntry = classUnderTest.getCartEntry(ENTRY_NUMBER, sessionCart);
		assertNotNull(cartEntry);
		assertEquals(Integer.valueOf(ENTRY_NUMBER), cartEntry.getEntryNumber());
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testGetCartEntryNoEntries() throws CommerceCartModificationException
	{
		sessionCart.setEntries(Collections.emptyList());
		classUnderTest.getCartEntry(ENTRY_NUMBER, sessionCart);
	}

	@Test
	public void testConfigureFromCart() throws CommerceCartModificationException
	{
		when(configCartIntegrationFacade.configureCartItem(CART_ITEM_KEY)).thenReturn(configData);
		final String result = classUnderTest.configureCartEntry(ENTRY_NUMBER, model, request);
		assertEquals(AbstractController.REDIRECT_PREFIX + SapproductconfigfrontendWebConstants.CART_PREFIX + ENTRY_NUMBER
				+ SapproductconfigfrontendWebConstants.CART_CONFIG_EXISTING_DRAFT_URL, result);
	}
}
