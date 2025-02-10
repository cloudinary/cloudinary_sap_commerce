/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractProductConfigControllerTest extends AbstractProductConfigControllerTCBase
{
	private static final String CART_ITEM_KEY = "123";

	private static final String CPQ_ITEM_PK = "cpqItemKey";

	private static final int ENTRY_NUMBER_UNKNOWN = 4;

	private AbstractProductConfigController classUnderTest;

	private CartData orderData;
	private List<OrderEntryData> orderEntryDataList;
	private OrderEntryData cpqOrderEntry;
	private OrderEntryData standardOrderEntry;

	@Before
	public void setup()
	{
		//type of classUnderTest is Abstract ->  use instance of child class in unit test
		classUnderTest = new AbstractConfigurationOverviewController();
		injectMocks(classUnderTest);
		configData = new ConfigurationData();
		configData.setKbKey(new KBKeyData());
		configData.getKbKey().setProductCode(PRODUCT_CODE);
		configData.setConfigId(CONFIG_ID);
		orderData = new CartData();


	}

	protected void prepareOrderDataTwoEntries(final String pk1, final String pk2)
	{
		cpqOrderEntry = new OrderEntryData();
		standardOrderEntry = new OrderEntryData();
		orderEntryDataList = new ArrayList();
		orderEntryDataList.add(cpqOrderEntry);
		orderEntryDataList.add(standardOrderEntry);
		orderData.setEntries(orderEntryDataList);
		cpqOrderEntry.setItemPK(pk1);
		cpqOrderEntry.setEntryNumber(Integer.valueOf(1));
		standardOrderEntry.setItemPK(pk2);
		standardOrderEntry.setEntryNumber(Integer.valueOf(2));
	}

	@Test
	public void testGetOrderEntry() throws BusinessException
	{
		prepareOrderDataTwoEntries(CPQ_ITEM_PK, null);
		final Optional<OrderEntryData> result = classUnderTest.getOrderEntry(CPQ_ITEM_PK, orderData);
		assertNotNull(result);
		assertEquals(cpqOrderEntry, result.get());
	}

	@Test
	public void testGetOrderEntryInteger() throws BusinessException
	{
		prepareOrderDataTwoEntries(CPQ_ITEM_PK, null);
		final OrderEntryData orderEntry = classUnderTest.getOrderEntry(1, orderData);

		assertEquals(cpqOrderEntry, orderEntry);
	}

	@Test(expected = BusinessException.class)
	public void testGetOrderEntryIntegerUnknownEntryNumber() throws BusinessException
	{
		prepareOrderDataTwoEntries(CPQ_ITEM_PK, null);
		classUnderTest.getOrderEntry(ENTRY_NUMBER_UNKNOWN, orderData);
	}

	@Test
	public void testGetOrderEntryNonConfigurableItems()
	{
		prepareOrderDataTwoEntries(null, null);

		try
		{
			classUnderTest.getOrderEntry(CPQ_ITEM_PK, orderData);
		}
		catch (final BusinessException e)
		{
			assertTrue(e.getCause() instanceof NoSuchElementException);
		}
	}

	protected void mockProductLinkedToCartItem()
	{
		given(configurationProductLinkStrategy.getConfigIdForProduct(PRODUCT_CODE)).willReturn(CONFIG_ID);
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn(CART_ITEM_KEY);
	}

	@Test
	public void testGetCartItemByProductCodeNoLink()
	{
		assertNull(classUnderTest.getCartItemByProductCode(PRODUCT_CODE));
	}

	@Test
	public void testGetCartItemByProductCodeIncompleteLink()
	{
		given(configurationProductLinkStrategy.getConfigIdForProduct(PRODUCT_CODE)).willReturn(CONFIG_ID);
		assertNull(classUnderTest.getCartItemByProductCode(PRODUCT_CODE));
	}

	@Test
	public void testGetCartItemByProductCode()
	{
		mockProductLinkedToCartItem();
		assertEquals(CART_ITEM_KEY, classUnderTest.getCartItemByProductCode(PRODUCT_CODE));
	}

	@Test
	public void testGetCartEntryNumber() throws BusinessException
	{
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn(CART_ITEM_KEY);
		prepareOrderDataTwoEntries(null, CART_ITEM_KEY);
		final Integer cartEntryNumber = classUnderTest.getCartEntryNumber(orderData, CONFIG_ID);
		assertEquals(2, cartEntryNumber.intValue());
	}

	@Test
	public void testGetCartEntryNumberDraft() throws BusinessException
	{
		given(abstractOrderEntryLinkStrategy.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(CART_ITEM_KEY);
		prepareOrderDataTwoEntries(null, CART_ITEM_KEY);
		final Integer cartEntryNumber = classUnderTest.getCartEntryNumber(orderData, CONFIG_ID);
		assertEquals(2, cartEntryNumber.intValue());
	}


	@Test
	public void testGetCartEntryNumberNoLink() throws BusinessException
	{
		prepareOrderDataTwoEntries(null, CART_ITEM_KEY);
		assertNull(classUnderTest.getCartEntryNumber(orderData, CONFIG_ID));
	}

	@Test
	public void testGetProductCodeForCartItem() throws BusinessException
	{
		final CartData cartData = prepareCartData("XXX", CART_ITEM_KEY, "YYY", PRODUCT_CODE);
		given(cartFacadeMock.getSessionCart()).willReturn(cartData);
		final String productCode = classUnderTest.getProductCodeForCartItem(CART_ITEM_KEY);
		assertEquals(PRODUCT_CODE, productCode);
	}

	@Test
	public void testGetProductCodeForCartItemNotExists() throws BusinessException
	{
		final CartData cartData = prepareCartData("XXX", "XXX2", "YYY", "YYY2");
		given(cartFacadeMock.getSessionCart()).willReturn(cartData);
		final String productCode = classUnderTest.getProductCodeForCartItem(CART_ITEM_KEY);
		assertNull(productCode);
	}

	@Test
	public void testSetCartEntryLinks() throws BusinessException
	{
		given(abstractOrderEntryLinkStrategy.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(CART_ITEM_KEY);
		prepareOrderDataTwoEntries(CART_ITEM_KEY, CPQ_ITEM_PK);
		classUnderTest.setCartEntryLinks(configData);
		assertTrue(configData.isLinkedToCartItem());

	}

	protected CartData prepareCartData(final String pk1, final String pk2, final String pCode1, final String pCode2)
	{
		final CartData cartData = new CartData();

		final OrderEntryData entry1 = new OrderEntryData();
		entry1.setItemPK(pk1);
		final ProductData productData1 = new ProductData();
		productData1.setCode(pCode1);
		entry1.setProduct(productData1);

		final OrderEntryData entry2 = new OrderEntryData();
		entry2.setItemPK(pk2);
		final ProductData productData2 = new ProductData();
		productData2.setCode(pCode2);
		entry2.setProduct(productData2);

		final List<OrderEntryData> entries = new ArrayList();
		entries.add(entry1);
		entries.add(entry2);
		cartData.setEntries(entries);

		return cartData;

	}

	@Test
	public void testGetUiStatusForConfigIdWithProductLink()
	{
		final UiStatus uiStatus = new UiStatus();
		given(configurationProductLinkStrategy.retrieveProductCode(CONFIG_ID)).willReturn(PRODUCT_CODE);
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(uiStatus);
		final UiStatus result = classUnderTest.getUiStatusForConfigId(CONFIG_ID);
		verify(sessionAccessFacade).getUiStatusForProduct(PRODUCT_CODE);
		assertEquals(uiStatus, result);
	}

	@Test
	public void testGetUiStatusForConfigIdNoProductLink()
	{
		final UiStatus uiStatus = new UiStatus();
		given(configurationProductLinkStrategy.retrieveProductCode(CONFIG_ID)).willReturn(null);
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn(CART_ITEM_KEY);
		given(sessionAccessFacade.getUiStatusForCartEntry(CART_ITEM_KEY)).willReturn(uiStatus);
		final UiStatus result = classUnderTest.getUiStatusForConfigId(CONFIG_ID);
		verify(sessionAccessFacade).getUiStatusForCartEntry(CART_ITEM_KEY);
		assertEquals(uiStatus, result);
	}

	@Test
	public void testGetUiStatusForConfigIdNoLink()
	{
		final UiStatus uiStatus = new UiStatus();
		given(configurationProductLinkStrategy.retrieveProductCode(CONFIG_ID)).willReturn(null);
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn(null);
		final UiStatus result = classUnderTest.getUiStatusForConfigId(CONFIG_ID);
		assertNull(result);
	}

	@Test
	public void testGetGaleryImagesNoImages()
	{
		assertEquals(Collections.emptyList(), classUnderTest.getGalleryImages(productData));
	}

	@Test
	public void testGetGaleryImages()
	{
		createImagesData(null);
		assertEquals(2, classUnderTest.getGalleryImages(productData).size());
	}

	@Test
	public void testGetGaleryImagesWithoutImages()
	{
		createImagesData(ImageDataType.PRIMARY);
		assertTrue(classUnderTest.getGalleryImages(productData).isEmpty());
	}
}
