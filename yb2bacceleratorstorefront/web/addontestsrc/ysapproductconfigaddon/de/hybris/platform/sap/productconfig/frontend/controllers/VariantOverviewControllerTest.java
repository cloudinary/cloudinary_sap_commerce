/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.model.ProductConfigOverviewPageModel;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VariantOverviewControllerTest extends AbstractProductConfigControllerTCBase
{
	private static final String BASE_PRODUCT_CODE = "BASE_PRODUCT_CODE";
	private static final int ENTRY_NUMBER = 1;
	@Mock
	ProductData productData;
	@Mock
	private ConfigurationOverviewFacade configurationOverviewFacade;
	// will inject cmsPageService and pageTitleResolver, as well. For both no setter exists
	@InjectMocks
	private VariantOverviewController classUnderTest;
	private final ConfigurationOverviewData overviewData = new ConfigurationOverviewData();

	@Before
	public void setUp() throws Exception
	{
		injectMocks(classUnderTest);
		classUnderTest.setConfigurationOverviewFacade(configurationOverviewFacade);

		initializeFirstCall();
		request.setAttribute("de.hybris.platform.acceleratorcms.utils.SpringHelper.bean.requestContextData",
				new RequestContextData());
		Mockito.when(configurationOverviewFacade.getOverviewForProductVariant(BASE_PRODUCT_CODE, null)).thenReturn(overviewData);
	}

	@Test
	public void testProductDataIsSet() throws Exception
	{
		classUnderTest.getVariantOverview(PRODUCT_CODE, model, request);
		Mockito.verify(model).addAttribute(Mockito.eq("product"), Mockito.any(ProductData.class));
	}

	@Test
	public void testPageTypeSet() throws Exception
	{
		classUnderTest.populateCMSAttributes(model);
		Mockito.verify(model).addAttribute(Mockito.eq("pageType"), Mockito.eq("productConfigOverviewPage"));
	}

	@Test
	public void testOverviewDataInitializedCorrect() throws Exception
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		given(productData.getCode()).willReturn(PRODUCT_CODE);

		final OverviewUiData overviewUiData = classUnderTest.prepareOverviewUiData(configOverviewData, productData);
		assertEquals(OverviewMode.VARIANT_OVERVIEW, overviewUiData.getOverviewMode());
	}

	@Test
	public void testPrepareOverviewData() throws Exception
	{
		final ConfigurationOverviewData configOverviewData = classUnderTest.prepareOverviewData(BASE_PRODUCT_CODE);
		assertEquals(this.overviewData, configOverviewData);
		assertEquals(BASE_PRODUCT_CODE, configOverviewData.getProductCode());
	}

	@Test
	public void testInitializeOverviewUiDataForVariantFromCartBound()
	{
		final OverviewUiData overviewUiData = classUnderTest.initializeOverviewUiDataForVariantFromCartBound(ENTRY_NUMBER);
		assertNotNull(overviewUiData);
		assertEquals(OverviewMode.VARIANT_OVERVIEW_FROM_CART_BOUND_CONFIG, overviewUiData.getOverviewMode());
		assertEquals(Integer.valueOf(ENTRY_NUMBER), overviewUiData.getCartEntryNumber());
	}

	@Test
	public void testGetVariantOverviewFromCartBoundConfig() throws CMSItemNotFoundException
	{
		assertEquals(SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME,
				classUnderTest.getVariantOverviewFromCartBoundConfig(BASE_PRODUCT_CODE, ENTRY_NUMBER, model, request));
	}

	@Test
	public void testResetUiCartConfigurationForProduct()
	{
		final UiStatus uiStatus = new UiStatus();
		given(configurationProductLinkStrategy.getConfigIdForProduct(PRODUCT_CODE)).willReturn(CONFIG_ID);
		classUnderTest.resetUiCartConfigurationForProduct(PRODUCT_CODE);

		verify(sessionAccessFacade, times(1)).removeUiStatusForProduct(PRODUCT_CODE);
		verify(configurationProductLinkStrategy, times(1)).removeConfigIdForProduct(PRODUCT_CODE);
		verify(configCartIntegrationFacade, times(1)).resetConfiguration(CONFIG_ID);
	}

	@Test
	public void testResetUiCartConfigurationForProductWithCartEntry()
	{
		final UiStatus uiStatus = new UiStatus();
		given(configurationProductLinkStrategy.getConfigIdForProduct(PRODUCT_CODE)).willReturn(CONFIG_ID);
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn("cartentry123");
		classUnderTest.resetUiCartConfigurationForProduct(PRODUCT_CODE);

		verify(sessionAccessFacade, times(1)).removeUiStatusForProduct(PRODUCT_CODE);
		verify(configurationProductLinkStrategy, times(1)).removeConfigIdForProduct(PRODUCT_CODE);
		verify(configCartIntegrationFacade, times(0)).resetConfiguration(CONFIG_ID);
	}

	@Test
	public void testResetUiCartConfigurationForProductUiStatusNull()
	{
		classUnderTest.resetUiCartConfigurationForProduct(PRODUCT_CODE);

		verify(sessionAccessFacade, times(0)).removeUiStatusForProduct(PRODUCT_CODE);
		verify(configurationProductLinkStrategy, times(0)).removeConfigIdForProduct(PRODUCT_CODE);
		verify(configCartIntegrationFacade, times(0)).resetConfiguration(CONFIG_ID);
	}

	@Test
	public void testAddBreadCrumb()
	{
		given(productData.getBaseProduct()).willReturn(BASE_PRODUCT_CODE);
		given(productData.getCode()).willReturn(PRODUCT_CODE);
		final OverviewUiData overviewPageModel = new OverviewUiData();
		classUnderTest.addBreadCrumb(model, productData, overviewPageModel);

		verify(productConfigurationBreadcrumbBuilder, times(1)).getVariantOverviewBreadcrumbs(BASE_PRODUCT_CODE, PRODUCT_CODE);
	}

	@Test
	public void testAddBreadCrumbCartBound()
	{
		given(productData.getBaseProduct()).willReturn(BASE_PRODUCT_CODE);
		given(productData.getCode()).willReturn(PRODUCT_CODE);
		final OverviewUiData overviewPageModel = new OverviewUiData();
		overviewPageModel.setCartEntryNumber(Integer.valueOf(1));
		classUnderTest.addBreadCrumb(model, productData, overviewPageModel);

		verify(productConfigurationBreadcrumbBuilder, times(1)).getVariantOverviewCartContextBreadcrumbs(BASE_PRODUCT_CODE,
				PRODUCT_CODE, Integer.valueOf(1));
	}

	@Test
	public void testAddVariantToCartCleanUp()
	{
		classUnderTest.addVariantToCartCleanUp(PRODUCT_CODE);
		verify(sessionAccessFacade, times(0)).removeUiStatusForProduct(PRODUCT_CODE);
		verify(configurationProductLinkStrategy, times(0)).removeConfigIdForProduct(PRODUCT_CODE);
		verify(configCartIntegrationFacade, times(0)).resetConfiguration(CONFIG_ID);
	}

	@Test
	public void testUpdateVariantOverview() throws CMSItemNotFoundException
	{
		final ModelAndView modelAndView = classUnderTest.updateVariantOverview(PRODUCT_CODE, model, request);
		final int length = StringUtils.length(SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME);
		assertEquals(SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME,
				StringUtils.substring(modelAndView.getViewName(), 0, length));
	}


	@Override
	protected void initializeFirstCall() throws Exception
	{
		super.initializeFirstCall();
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();

		given(configurationOverviewFacade.getOverviewForProductVariant(Mockito.eq(PRODUCT_CODE), Mockito.any()))
				.willReturn(configOverviewData);
		given(cmsPageService.getPageForId("productConfigOverview")).willReturn(new ProductConfigOverviewPageModel());
	}

}
