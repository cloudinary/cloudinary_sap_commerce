/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOrderIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationQuoteIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationSavedCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.model.ProductConfigOverviewPageModel;
import de.hybris.platform.sap.productconfig.frontend.util.ConfigErrorHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OrderEntryOverviewControllerTest extends AbstractProductConfigControllerTCBase
{
	private static final String SOURCE_DOCUMENT_ID = "sourceDocumentId";

	private static final String QUOTE_ITEM_PK = "quoteItemPk";

	private static final String QUOTE_CODE = "QuoteCode";
	private static final int QUOTE_ENTRY_NUMBER = 0;

	private static final String ORDER_CODE = "OrderCode";
	private static final int ORDER_ENTRY_NUMBER = 0;

	private static final String CART_CODE = "1234";
	private static final int CART_ENTRY_NUMBER = 0;

	private static final int index = 1;

	private static final String NAVIGATION_END_POINT = "navigationEndPoint";

	// will inject cmsPageService and pageTitleResolver, as well. For both no setter exists
	@InjectMocks
	private OrderEntryOverviewController classUnderTest;

	@Mock
	private ConfigurationQuoteIntegrationFacade configQuoteFacade;

	@Mock
	private ConfigurationSavedCartIntegrationFacade configurationSavedCartIntegrationFacade;

	@Mock
	private ConfigurationOverviewFacade configOverviewFacade;

	@Mock
	private QuoteFacade quoteFacade;

	@Mock
	private SaveCartFacade savedCartFacade;

	@Mock
	private CartFacade cartFacade;

	@Mock
	private QuoteData quoteData;

	private List<OrderEntryData> listOrderEntryData;
	@Mock
	private OrderEntryData quoteEntryData;

	private ConfigurationOverviewData configOverviewData;
	private ConfigurationOverviewData configOverviewData2;

	private final RedirectAttributes redirectModel = null;

	@Mock
	private CommerceSaveCartResultData savedCartResultData;

	private final CartData cartData = new CartData();

	@Mock
	private ConfigurationOrderIntegrationFacade configurationOrderIntegrationFacade;

	@Mock
	private ConfigErrorHandler configErrorHandler;

	@Before
	public void setUp() throws CommerceSaveCartException
	{
		setFacades();
		injectMocks(classUnderTest);

		configOverviewData = createOverviewData("p123", "c123", "001");
		configOverviewData2 = createOverviewData("p128", null, "002");
		given(configOverviewFacade.getOverviewForConfiguration("c123", configOverviewData)).willReturn(configOverviewData);

		given(quoteFacade.getQuoteForCode(QUOTE_CODE)).willReturn(quoteData);

		listOrderEntryData = new ArrayList<>();
		listOrderEntryData.add(quoteEntryData);
		defineQuoteDataBehavior();
		createCartData();

		defineSavedCartFacadeBehavior();

		defineErrorHandlerBehavior();

	}

	private void defineSavedCartFacadeBehavior() throws CommerceSaveCartException
	{
		Mockito.when(configurationSavedCartIntegrationFacade.getConfiguration(CART_CODE, CART_ENTRY_NUMBER))
				.thenReturn(configOverviewData);
	}

	private void createCartData()
	{
		final OrderEntryData cartEntry = new OrderEntryData();
		cartEntry.setEntryNumber(Integer.valueOf(CART_ENTRY_NUMBER));
		cartData.setEntries(Arrays.asList(cartEntry));
	}

	private void defineQuoteDataBehavior()
	{
		given(quoteData.getEntries()).willReturn(listOrderEntryData);
		given(quoteEntryData.getItemPK()).willReturn(QUOTE_ITEM_PK);
		given(quoteEntryData.getEntryNumber()).willReturn(Integer.valueOf(index));
	}

	private void defineErrorHandlerBehavior()
	{
		classUnderTest.setConfigurationErrorHandler(configErrorHandler);
		given(configErrorHandler.handleErrorFromQuotes(QUOTE_CODE, QUOTE_ENTRY_NUMBER, redirectModel))
				.willReturn(NAVIGATION_END_POINT);
		given(configErrorHandler.handleErrorFromOrderHistory(ORDER_CODE, ORDER_ENTRY_NUMBER, redirectModel))
				.willReturn(NAVIGATION_END_POINT);
		given(configErrorHandler.handleErrorFromSavedCarts(CART_CODE, CART_ENTRY_NUMBER, redirectModel))
				.willReturn(NAVIGATION_END_POINT);
	}

	private ConfigurationOverviewData createOverviewData(final String productCode, final String id, final String sourceDocumentId)
	{
		final ConfigurationOverviewData data = new ConfigurationOverviewData();
		data.setProductCode(productCode);
		data.setId(id);
		data.setSourceDocumentId(sourceDocumentId);
		data.setGroups(Collections.EMPTY_LIST);
		return data;
	}

	private void setFacades()
	{
		classUnderTest.setConfigurationQuoteIntegrationFacade(configQuoteFacade);
		classUnderTest.setConfigurationOverviewFacade(configOverviewFacade);
		classUnderTest.setQuoteFacade(quoteFacade);
		classUnderTest.setConfigurationOrderIntegrationFacade(configurationOrderIntegrationFacade);
		classUnderTest.setConfigurationSavedCartIntegrationFacade(configurationSavedCartIntegrationFacade);
		classUnderTest.setSaveCartFacade(savedCartFacade);
		classUnderTest.setCartFacade(cartFacade);

	}

	@Test
	public void testEnrichOverviewData() throws CMSItemNotFoundException
	{
		final ConfigurationOverviewData result = classUnderTest.enrichOverviewData("code", configOverviewData);
		assertEquals("code", result.getSourceDocumentId());
		Mockito.verify(configOverviewFacade).getOverviewForConfiguration(configOverviewData.getId(), configOverviewData);
	}


	@Test
	public void testInitializeUIStatus()
	{
		final UiStatus uiStatus = classUnderTest.initializeUIStatusForAbstractOrder(configOverviewData);
		assertNotNull(uiStatus.getMaxGroupFilterList());
		assertNotNull(uiStatus.getCsticFilterList());
		assertTrue(uiStatus.isHideImageGallery());
	}

	@Test
	public void testGetPageId()
	{
		final String pageId = classUnderTest.getPageId();
		assertEquals(AbstractConfigurationOverviewController.CMS_OV_PAGE_ID, pageId);
	}

	@Test
	public void testGetPageType()
	{
		final String pageType = classUnderTest.getPageType();
		assertEquals(AbstractConfigurationOverviewController.CMS_OV_PAGE_TYPE, pageType);
	}

	@Test
	public void testQuoteEntryViewConfig() throws Exception
	{
		initializeFirstCall();
	}

	@Override
	protected void initializeFirstCall() throws Exception
	{
		configData = new ConfigurationData();
		configData.setGroups(Collections.emptyList());
		configData.setKbKey(new KBKeyData());
		configData.getKbKey().setProductCode("p123");

		super.initializeFirstCall();
		given(cmsPageService.getPageForId(ConfigurationOverviewController.CMS_OV_PAGE_ID))
				.willReturn(new ProductConfigOverviewPageModel());
	}

	@Test
	public void testPopulateCMSAttributes() throws CMSItemNotFoundException
	{
		given(cmsPageService.getPageForId(ConfigurationOverviewController.CMS_OV_PAGE_ID))
				.willReturn(new ProductConfigOverviewPageModel());
		classUnderTest.populateCMSAttributes(model);
		Mockito.verify(model).addAttribute(Mockito.eq(AbstractProductConfigController.CMS_PAGE_TYPE),
				Mockito.eq(ConfigurationOverviewController.CMS_OV_PAGE_TYPE));
		Mockito.verify(model).addAttribute(Mockito.eq(AbstractProductConfigController.CMS_PAGE_MODEL),
				Mockito.any(ProductConfigOverviewPageModel.class));
	}

	@Test
	public void testSetUiStatusForOverview() throws Exception
	{
		initializeFirstCall();
		final UiStatus uiStatus = new UiStatus();
		final OverviewUiData overviewUiData = new OverviewUiData();
		overviewUiData.setOverviewMode(OverviewMode.QUOTATION_OVERVIEW);
		overviewUiData.setAbstractOrderCode(QUOTE_CODE);
		overviewUiData.setAbstractOrderEntryNumber(Integer.valueOf(index));
		classUnderTest.setUiStatusForOverviewInSession(uiStatus, configData.getKbKey().getProductCode(), overviewUiData);
		Mockito.verify(sessionAccessFacade).setUiStatusForCartEntry(QUOTE_ITEM_PK, uiStatus);
	}

	@Test
	public void testGetUiStatusForOverview() throws Exception
	{
		initializeFirstCall();
		final OverviewUiData overviewUiData = new OverviewUiData();
		overviewUiData.setOverviewMode(OverviewMode.QUOTATION_OVERVIEW);
		overviewUiData.setAbstractOrderCode(QUOTE_CODE);
		overviewUiData.setAbstractOrderEntryNumber(Integer.valueOf(index));
		classUnderTest.getUiStatusForOverview(configData.getKbKey().getProductCode(), overviewUiData);
		Mockito.verify(sessionAccessFacade).getUiStatusForCartEntry(QUOTE_ITEM_PK);
	}

	@Test
	public void testInitializeOverviewUiDataForQuotationOverview()
	{
		final OverviewUiData result = classUnderTest.initializeOverviewUiData("configId", QUOTE_CODE, index, SOURCE_DOCUMENT_ID,
				OverviewMode.QUOTATION_OVERVIEW);

		assertEquals(QUOTE_CODE, result.getAbstractOrderCode());
		assertEquals(index, result.getAbstractOrderEntryNumber().intValue());
		assertEquals(SOURCE_DOCUMENT_ID, result.getSourceDocumentId());
	}

	@Test
	public void testRecordUiAccessOverview()
	{
		classUnderTest.recordUiAccessOverview(configOverviewData);
		Mockito.verify(uiTrackingRecorder, times(1)).recordUiAccessOverview(configOverviewData,
				configOverviewData.getProductCode());

	}

	@Test
	public void testRecordUiAccessVariantOverview()
	{
		classUnderTest.recordUiAccessOverview(configOverviewData2);
		Mockito.verify(uiTrackingRecorder, times(1)).recordUiAccessVariantOverview(configOverviewData2.getProductCode());

	}

	@Test
	public void testSetOverviewModeQuote()
	{
		final OverviewUiData overviewUiData = new OverviewUiData();
		classUnderTest.setOverviewMode(CONFIG_ID, OverviewMode.QUOTATION_OVERVIEW, overviewUiData);
		assertEquals(OverviewMode.QUOTATION_OVERVIEW, overviewUiData.getOverviewMode());
	}

	@Test
	public void testSetOverviewModeQuoteVariant()
	{
		final OverviewUiData overviewUiData = new OverviewUiData();
		classUnderTest.setOverviewMode(null, OverviewMode.QUOTATION_OVERVIEW, overviewUiData);
		assertEquals(OverviewMode.QUOTATION_VARIANT_OVERVIEW, overviewUiData.getOverviewMode());
	}

	@Test
	public void testSetOverviewModeOrder()
	{
		final OverviewUiData overviewUiData = new OverviewUiData();
		classUnderTest.setOverviewMode(CONFIG_ID, OverviewMode.ORDER_OVERVIEW, overviewUiData);
		assertEquals(OverviewMode.ORDER_OVERVIEW, overviewUiData.getOverviewMode());
	}

	@Test
	public void testSetOverviewModeOrderVariant()
	{
		final OverviewUiData overviewUiData = new OverviewUiData();
		classUnderTest.setOverviewMode(null, OverviewMode.ORDER_OVERVIEW, overviewUiData);
		assertEquals(OverviewMode.ORDER_VARIANT_OVERVIEW, overviewUiData.getOverviewMode());
	}

	@Test
	public void testSetOverviewModeSavedCart()
	{
		final OverviewUiData overviewUiData = new OverviewUiData();
		classUnderTest.setOverviewMode(CONFIG_ID, OverviewMode.SAVED_CART_OVERVIEW, overviewUiData);
		assertEquals(OverviewMode.SAVED_CART_OVERVIEW, overviewUiData.getOverviewMode());
	}

	@Test
	public void testSetOverviewModeSavedCartVariant()
	{
		final OverviewUiData overviewUiData = new OverviewUiData();
		classUnderTest.setOverviewMode(null, OverviewMode.SAVED_CART_OVERVIEW, overviewUiData);
		assertEquals(OverviewMode.SAVED_CART_VARIANT_OVERVIEW, overviewUiData.getOverviewMode());
	}

	@Test
	public void testConfigurationSavedCartIntegrationFacade()
	{
		assertEquals(configurationSavedCartIntegrationFacade, classUnderTest.getConfigurationSavedCartIntegrationFacade());
	}

	@Test
	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	public void testSavedCartOverview() throws Exception
	{
		initializeFirstCall();
		assertEquals(SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME,
				classUnderTest.getSavedCartOverview(CART_CODE, CART_ENTRY_NUMBER, model, redirectModel, request));
		Mockito.verify(configurationSavedCartIntegrationFacade, times(1)).getConfiguration(CART_CODE, CART_ENTRY_NUMBER);
	}

	@Test
	public void testSavedCartOverviewDataNull() throws Exception
	{
		given(configurationSavedCartIntegrationFacade.getConfiguration(CART_CODE, CART_ENTRY_NUMBER)).willReturn(null);
		assertEquals(NAVIGATION_END_POINT,
				classUnderTest.getSavedCartOverview(CART_CODE, CART_ENTRY_NUMBER, model, redirectModel, request));
	}

	@Test
	public void testGetQuotationOverviewDataNull() throws Exception
	{
		given(configQuoteFacade.getConfiguration(QUOTE_CODE, QUOTE_ENTRY_NUMBER)).willReturn(null);
		assertEquals(NAVIGATION_END_POINT,
				classUnderTest.getQuotationOverview(QUOTE_CODE, QUOTE_ENTRY_NUMBER, model, redirectModel, request));
	}


	@Test
	public void testGetOrderOverviewDataNull() throws Exception
	{
		given(configurationOrderIntegrationFacade.getConfiguration(ORDER_CODE, ORDER_ENTRY_NUMBER)).willReturn(null);
		assertEquals(NAVIGATION_END_POINT,
				classUnderTest.getOrderOverview(ORDER_CODE, ORDER_ENTRY_NUMBER, model, redirectModel, request));
	}

	@Test
	public void testGetQuotationOverviewData() throws Exception
	{
		initializeFirstCall();
		given(configQuoteFacade.getConfiguration(QUOTE_CODE, QUOTE_ENTRY_NUMBER)).willReturn(configOverviewData);
		assertEquals(SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME,
				classUnderTest.getQuotationOverview(QUOTE_CODE, QUOTE_ENTRY_NUMBER, model, redirectModel, request));
		Mockito.verify(configQuoteFacade, times(1)).getConfiguration(QUOTE_CODE, QUOTE_ENTRY_NUMBER);
	}


	@Test
	public void testGetOrderOverviewData() throws Exception
	{
		initializeFirstCall();
		given(configurationOrderIntegrationFacade.getConfiguration(ORDER_CODE, ORDER_ENTRY_NUMBER)).willReturn(configOverviewData);
		assertEquals(SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME,
				classUnderTest.getOrderOverview(ORDER_CODE, ORDER_ENTRY_NUMBER, model, redirectModel, request));
		Mockito.verify(configurationOrderIntegrationFacade, times(1)).getConfiguration(ORDER_CODE, ORDER_ENTRY_NUMBER);
	}

}
