/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.bind.WebDataBinder;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AbstractConfigurationOverviewControllerTest extends AbstractProductConfigControllerTCBase
{
	public static final String ORDER_CODE = "ORDER_CODE";
	public static final int ORDER_ENTRY_NUMBER = 1;
	public static final String CART_ENTRY_KEY = "1";
	public static final String ITEM_PK = "1234567";

	private AbstractConfigurationOverviewController classUnderTest;
	private UiStatus uiStatus;
	private OverviewUiData overviewUiData;
	private ConfigurationOverviewData configOverviewData;
	@Mock
	private QuoteFacade mockQuoteFacade;
	@Mock
	private SaveCartFacade mockSaveCartFacade;
	@Mock
	private OrderFacade mockOrderFacade;
	@Mock
	private CommerceSaveCartResultData mockCommerceSaveCartResultData;
	@Mock
	private QuoteData mockQuoteData;
	@Mock
	private OrderData mockOrderData;
	@Mock
	private CartData mockCartData;
	@Mock
	private WebDataBinder mockBinder;

	@Before
	public void setUp() throws Exception
	{
		classUnderTest = new AbstractConfigurationOverviewController();
		classUnderTest.setCartFacade(cartFacadeMock);
		classUnderTest.setAbstractOrderEntryLinkStrategy(abstractOrderEntryLinkStrategy);
		classUnderTest.setSessionAccessFacade(sessionAccessFacade);
		classUnderTest.setOrderFacade(mockOrderFacade);
		classUnderTest.setSaveCartFacade(mockSaveCartFacade);
		classUnderTest.setQuoteFacade(mockQuoteFacade);

		overviewUiData = new OverviewUiData();
		configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setId(CONFIG_ID);
		configOverviewData.setGroups(Collections.emptyList());
		kbKey = new KBKeyData();
		kbKey.setProductCode(PRODUCT_CODE);

		uiStatus = new UiStatus();
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(uiStatus);

		given(mockQuoteFacade.getQuoteForCode(ORDER_CODE)).willReturn(mockQuoteData);
		given(mockQuoteData.getEntries()).willReturn(null);
		given(mockOrderFacade.getOrderDetailsForCodeWithoutUser(ORDER_CODE)).willReturn(mockOrderData);
		given(mockOrderData.getEntries()).willReturn(null);
		given(mockCartData.getEntries()).willReturn(null);
		given(mockSaveCartFacade.getCartForCodeAndCurrentUser(any())).willReturn(mockCommerceSaveCartResultData);
		given(mockCommerceSaveCartResultData.getSavedCartData()).willReturn(mockCartData);
	}

	@Test
	public void testGetErrorCountForUi_nonZero()
	{
		final Object errorCountForUi = classUnderTest.getErrorCountForUi(1);
		assertEquals("1", errorCountForUi.toString());
	}

	@Test
	public void testGetErrorCountForUi_zero()
	{
		final Object errorCountForUi = classUnderTest.getErrorCountForUi(0);
		assertEquals("0", errorCountForUi.toString());
	}

	@Test
	public void testNeedConfigurationDetails()
	{
		overviewUiData.setOverviewMode(OverviewMode.CONFIGURATION_OVERVIEW);
		assertTrue(classUnderTest.needConfigurationDetails(overviewUiData));
	}

	@Test
	public void testNeedConfigurationDetailsIsVariant()
	{
		overviewUiData.setOverviewMode(OverviewMode.VARIANT_OVERVIEW);
		assertFalse(classUnderTest.needConfigurationDetails(overviewUiData));
	}

	@Test
	public void testPrepareOverviewUiDataMapsIdAndCode() throws BusinessException
	{
		classUnderTest.initializeFilterListsInUiStatus(configOverviewData, uiStatus);
		classUnderTest.prepareOverviewUiData(uiStatus, overviewUiData, configOverviewData, kbKey);
		assertEquals(CONFIG_ID, overviewUiData.getConfigId());
		assertEquals(PRODUCT_CODE, overviewUiData.getProductCode());
	}


	@Test
	public void testGetQuantityUiStatusNull()
	{
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(null);
		assertEquals(1, classUnderTest.getQuantity(PRODUCT_CODE));
	}

	@Test
	public void testGetQuantity()
	{
		uiStatus.setQuantity(2);
		assertEquals(2, classUnderTest.getQuantity(PRODUCT_CODE));
	}


	@Test(expected = BusinessException.class)
	public void testSetUiStatusForOverviewInSessionQuoteNoEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.QUOTATION_OVERVIEW);
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, PRODUCT_CODE, overviewUiData);
	}

	@Test(expected = BusinessException.class)
	public void testSetUiStatusForOverviewInSessionOrderNoEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.ORDER_OVERVIEW);
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, PRODUCT_CODE, overviewUiData);
	}

	@Test(expected = BusinessException.class)
	public void testSetUiStatusForOverviewInSessionSavedCartNoEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.SAVED_CART_OVERVIEW);
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, ITEM_PK, overviewUiData);
	}

	private void fillOverviewUIData(final OverviewMode overviewMode)
	{
		overviewUiData.setAbstractOrderEntryNumber(1);
		overviewUiData.setAbstractOrderCode(ORDER_CODE);
		overviewUiData.setOverviewMode(overviewMode);
	}


	@Test
	public void testSetUiStatusForOverviewInSessionOthers() throws BusinessException
	{
		overviewUiData.setOverviewMode(OverviewMode.CONFIGURATION_OVERVIEW);
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, ITEM_PK, overviewUiData);
		verify(sessionAccessFacade).setUiStatusForCartEntry(ITEM_PK, uiStatus);
	}

	@Test
	public void testSetUiStatusForOverviewInSessionQuoteWithEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.QUOTATION_OVERVIEW);
		given(mockQuoteData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, ITEM_PK, overviewUiData);
	}

	@Test
	public void testSetUiStatusForOverviewInSessionOrderWithEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.ORDER_OVERVIEW);
		given(mockOrderData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, ITEM_PK, overviewUiData);
	}

	@Test
	public void testSetUiStatusForOverviewInSessionSavedCartWithEntries() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.SAVED_CART_OVERVIEW);
		given(mockCartData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, ITEM_PK, overviewUiData);
	}

	private List<OrderEntryData> createOrderEntries(final String itemPk, final int entryNumber)
	{

		final List<OrderEntryData> entries = new ArrayList<OrderEntryData>();
		final OrderEntryData entry = new OrderEntryData();
		entry.setItemPK(itemPk);
		entry.setEntryNumber(entryNumber);
		entries.add(entry);
		return entries;

	}

	@Test
	public void testGetUiStatusForOverviewOthers() throws BusinessException
	{
		overviewUiData.setOverviewMode(OverviewMode.CONFIGURATION_OVERVIEW);
		classUnderTest.getUiStatusForOverview(CART_ENTRY_KEY, overviewUiData);
		verify(sessionAccessFacade).getUiStatusForCartEntry(CART_ENTRY_KEY);
	}

	@Test(expected = BusinessException.class)
	public void testGetUiStatusForOverviewNoEntriesQuote() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.QUOTATION_OVERVIEW);
		classUnderTest.getUiStatusForOverview(CART_ENTRY_KEY, overviewUiData);
	}


	@Test
	public void testGetUiStatusForOverviewQuote() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.QUOTATION_OVERVIEW);
		given(mockQuoteData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));

		classUnderTest.getUiStatusForOverview(CART_ENTRY_KEY, overviewUiData);
		verify(sessionAccessFacade).getUiStatusForCartEntry(ITEM_PK);
	}

	@Test
	public void testGetUiStatusForOverviewOrder() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.ORDER_OVERVIEW);
		given(mockOrderData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));

		classUnderTest.getUiStatusForOverview(CART_ENTRY_KEY, overviewUiData);
		verify(sessionAccessFacade).getUiStatusForCartEntry(ITEM_PK);
	}

	@Test
	public void testGetUiStatusForOverviewSavedCart() throws BusinessException
	{
		fillOverviewUIData(OverviewMode.SAVED_CART_OVERVIEW);
		given(mockCartData.getEntries()).willReturn(createOrderEntries(ITEM_PK, ORDER_ENTRY_NUMBER));

		classUnderTest.getUiStatusForOverview(CART_ENTRY_KEY, overviewUiData);
		verify(sessionAccessFacade).getUiStatusForCartEntry(ITEM_PK);
	}

	@Test
	public void testSetAllowedFields()
	{
		classUnderTest.initBinderConfigOverviewUiData(mockBinder);
		verify(mockBinder).setAllowedFields(AbstractConfigurationOverviewController.ALLOWED_FIELDS_OVERVIEWUIDATA);
	}

	@Test
	public void testPrepareOverviewUiData()
	{
		productData.setCode(PRODUCT_CODE);
		productData.setBaseProduct(PRODUCT_CODE);
		final PricingData pricing = new PricingData();
		final PriceData basePrice = new PriceData();
		basePrice.setValue(new BigDecimal("100.00"));
		pricing.setBasePrice(basePrice);
		final PriceData currentTotal = new PriceData();
		currentTotal.setValue(new BigDecimal("150.00"));
		pricing.setCurrentTotal(currentTotal);
		final PriceData selectedOptions = new PriceData();
		selectedOptions.setValue(new BigDecimal("50.00"));
		pricing.setSelectedOptions(selectedOptions);
		configOverviewData.setPricing(pricing);
		final List<CharacteristicGroup> groups = createCharacteristicGroups();
		configOverviewData.setGroups(groups);

		classUnderTest.prepareOverviewUiData(overviewUiData, configOverviewData, productData);
		assertEquals(overviewUiData.getProductCode(), productData.getCode());
		assertEquals(overviewUiData.getQuantity(), classUnderTest.getQuantity(productData.getBaseProduct()));
		assertEquals(overviewUiData.getGroups().size(), configOverviewData.getGroups().size());
		assertEquals(overviewUiData.getGroups(), configOverviewData.getGroups());
		assertEquals(overviewUiData.getPricing(), configOverviewData.getPricing());
		assertEquals(overviewUiData.getPricing().getBasePrice().getValue(),
				configOverviewData.getPricing().getBasePrice().getValue());
		assertEquals(overviewUiData.getPricing().getCurrentTotal().getValue(),
				configOverviewData.getPricing().getCurrentTotal().getValue());
		assertEquals(overviewUiData.getPricing().getSelectedOptions().getValue(),
				configOverviewData.getPricing().getSelectedOptions().getValue());
	}

	protected List<CharacteristicGroup> createCharacteristicGroups()
	{
		final List<CharacteristicGroup> groups = new ArrayList<>();
		for (int i = 1; i <= 4; i++)
		{
			final CharacteristicGroup group = new CharacteristicGroup();
			group.setId("group_" + i);
			groups.add(group);
		}
		return groups;
	}
}
