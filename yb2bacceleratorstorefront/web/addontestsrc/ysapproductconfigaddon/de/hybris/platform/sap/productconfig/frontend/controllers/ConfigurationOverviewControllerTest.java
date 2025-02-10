/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;
import de.hybris.platform.sap.productconfig.frontend.CPQOverviewActionType;
import de.hybris.platform.sap.productconfig.frontend.FilterData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.model.ProductConfigPageModel;
import de.hybris.platform.sap.productconfig.frontend.util.ConfigErrorHandler;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConfigurationOverviewControllerTest extends AbstractProductConfigControllerTCBase
{

	private static final String CART_ENTRY_KEY = "123456";

	// will inject cmsPageService and pageTitleResolver, as well. For both no setter exists
	@InjectMocks
	private ConfigurationOverviewController classUnderTest;
	private ConfigurationOverviewData configOverviewData;
	private UiStatus uiStatus;
	private OverviewUiData overviewUIData;

	@Mock
	private ConfigErrorHandler errorHandler;
	@Mock
	protected ConfigurationOverviewFacade configurationOverviewFacade;


	private final CartModificationData addedToCart = new CartModificationData();

	private final OrderEntryData abstractOrderEntry = new OrderEntryData();

	private final List<ConfigurationInfoData> configurationInfos = new ArrayList();

	private final ConfigurationInfoData configurationInfo = new ConfigurationInfoData();

	private final CartData cartData = new CartData();

	@Before
	public void setUp() throws CommerceCartModificationException
	{
		injectMocks(classUnderTest);
		classUnderTest.setConfigurationErrorHandler(errorHandler);
		classUnderTest.setConfigurationOverviewFacade(configurationOverviewFacade);

		configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setId(CONFIG_ID);
		configOverviewData.setProductCode(PRODUCT_CODE);
		configOverviewData.setGroups(Collections.emptyList());
		configOverviewData.setMessages(Collections.emptyList());

		csticList = createCsticsList();

		uiStatus = new UiStatus();

		kbKey = createKbKey();
		configData = createEmptyConfigData();
		configData.setKbKey(kbKey);

		addedToCart.setEntry(abstractOrderEntry);
		abstractOrderEntry.setConfigurationInfos(configurationInfos);
		abstractOrderEntry.setEntryNumber(Integer.valueOf(1));
		abstractOrderEntry.setItemPK(CART_ENTRY_KEY);
		abstractOrderEntry.setProduct(productData);
		configurationInfos.add(configurationInfo);
		configurationInfo.setConfigId(CONFIG_ID);
		cartData.setEntries(Arrays.asList(abstractOrderEntry));
	}

	@Test
	public void testRedirectToErrorPage() throws Exception
	{
		initializeFirstCall();
		final OverviewUiData uiData = new OverviewUiData();
		uiData.setProductCode(PRODUCT_CODE);
		classUnderTest.updateConfiguationOverview(uiData, model, request);
		Mockito.verify(errorHandler).handleErrorForAjaxRequest(request, model);
	}

	@Test
	public void testGenerateFilterDataListNoneSelected()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List<FilterData> filterDataList = classUnderTest.generateCsticFilterDataList(configOverviewData);

		assertFalse(filterDataList.isEmpty());
		for (final FilterData filterData : filterDataList)
		{
			assertFalse(filterData.isSelected());
			assertNotEquals(FilterEnum.VISIBLE.toString(), filterData.getKey());
		}
	}

	@Test
	public void testGenerateFilterDataListNull()
	{
		final ConfigurationOverviewData configOverviewData = null;
		final List<FilterData> filterDataList = classUnderTest.generateCsticFilterDataList(configOverviewData);

		assertTrue(filterDataList.isEmpty());
	}

	@Test
	public void testGenerateFilterDataListSelected()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List appliedFilters = new ArrayList<FilterEnum>();
		appliedFilters.add(FilterEnum.USER_INPUT);
		configOverviewData.setAppliedCsticFilters(appliedFilters);
		final List<FilterData> filterDataList = classUnderTest.generateCsticFilterDataList(configOverviewData);

		assertFalse(filterDataList.isEmpty());
		for (final FilterData filterData : filterDataList)
		{
			if (FilterEnum.USER_INPUT.toString().equals(filterData.getKey()))
			{
				assertTrue(filterData.isSelected());
			}
			else
			{
				assertFalse(filterData.isSelected());
			}
			assertNotEquals(FilterEnum.VISIBLE.toString(), filterData.getKey());
		}
	}

	@Test
	public void testGenerateFilterDataListIgnoreVisible()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List appliedFilters = new ArrayList<FilterEnum>();
		appliedFilters.add(FilterEnum.VISIBLE);
		configOverviewData.setAppliedCsticFilters(appliedFilters);
		final List<FilterData> filterDataList = classUnderTest.generateCsticFilterDataList(configOverviewData);

		assertFalse(filterDataList.isEmpty());
		for (final FilterData filterData : filterDataList)
		{
			assertFalse(filterData.isSelected());
			assertNotEquals(FilterEnum.VISIBLE.toString(), filterData.getKey());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	@Test(expected = NullPointerException.class)
	public void testInitializeGroupFilterDataListNull()
	{
		classUnderTest.initializeGroupFilterDataList(null);
	}

	@Test
	public void testInitializeGroupFilterDataListNotNull()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setGroups(new ArrayList<>());
		final CharacteristicGroup group = new CharacteristicGroup();
		group.setId("CPQ");
		group.setIsSelectedTopLevelGroup(true);
		configOverviewData.getGroups().add(group);
		final List<FilterData> groupFilterDataList = classUnderTest.initializeGroupFilterDataList(configOverviewData);
		assertNotNull("We expect groupFilterdataList does not equal null: ", groupFilterDataList);
		assertFalse("We expect empty groupFilterdataList: ", groupFilterDataList.isEmpty());
		assertEquals(1, groupFilterDataList.size());
		assertEquals(group.getId(), groupFilterDataList.get(0).getKey());
		assertFalse(groupFilterDataList.get(0).isSelected());
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	@Test(expected = NullPointerException.class)
	public void testComputeUiGroupFilterListNull()
	{
		classUnderTest.computeUiGroupFilterList(null);
	}

	@Test
	public void testComputeUiGroupFilterListNotNull()
	{
		final List<FilterData> maxGroupFilterList = new ArrayList<>();
		final FilterData uiFilter1 = createFilterData("WCEM_MULTI", "SAP Hardware Centre", true);
		maxGroupFilterList.add(uiFilter1);

		final List<FilterData> filterDataList = classUnderTest.computeUiGroupFilterList(maxGroupFilterList);
		assertNotNull("We expect one filterDataList: ", filterDataList);
		assertFalse("We expect not an empty list: ", filterDataList.isEmpty());
		assertEquals("We expect a list with one element in it", 1, filterDataList.size());
		assertEquals("We expect a groupId 'WCEM_SIMPLE_GROUP': ", maxGroupFilterList.get(0).getKey(),
				filterDataList.get(0).getKey());
		assertEquals("We expect a group to be selected: ", maxGroupFilterList.get(0).isSelected(),
				filterDataList.get(0).isSelected());
	}

	@Test
	public void testUpdateGroupFilterList()
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		final List<FilterData> groupFilterList = new ArrayList<>();
		final FilterData groupFilter = createFilterData("WCEM_SIMPLE", "SAP Software Centre", true);
		groupFilterList.add(groupFilter);
		overviewUIData.setGroupFilterList(groupFilterList);

		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> maxGroupFilterList = new ArrayList<>();
		final FilterData uiFilter1 = createFilterData("WCEM_MULTI", "SAP Hardware Centre", false);
		maxGroupFilterList.add(uiFilter1);

		final FilterData uiFilter2 = createFilterData("WCEM_SIMPLE", "SAP Software Centre", false);
		maxGroupFilterList.add(uiFilter2);
		uiStatus.setMaxGroupFilterList(maxGroupFilterList);

		classUnderTest.updateGroupFilterList(overviewUIData, uiStatus);

		final List<FilterData> uiFilterDataList = overviewUIData.getGroupFilterList();
		final List<FilterData> maxFilterDataList = uiStatus.getMaxGroupFilterList();

		assertNotNull(uiFilterDataList);
		assertNotNull(maxFilterDataList);

		assertEquals(1, uiFilterDataList.size());
		assertEquals(2, maxFilterDataList.size());

		assertEquals(uiFilterDataList.get(0).getKey(), maxFilterDataList.get(1).getKey());
		assertEquals(uiFilterDataList.get(0).isSelected(), maxFilterDataList.get(1).isSelected());
	}

	private FilterData createFilterData(final String key, final String description, final boolean selected)
	{
		final FilterData groupFilter = new FilterData();
		groupFilter.setKey(key);
		groupFilter.setDescription(description);
		groupFilter.setSelected(selected);
		return groupFilter;
	}

	@Test
	public void testUpdateGroupFilterListGroupFilterNull()
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		final List<FilterData> groupFilterList = null;
		overviewUIData.setGroupFilterList(groupFilterList);

		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> maxGroupFilterList = new ArrayList<>();
		final FilterData uiFilter1 = createFilterData("WCEM_MULTI", "SAP Hardware Centre", true);
		maxGroupFilterList.add(uiFilter1);

		final FilterData uiFilter2 = createFilterData("WCEM_SIMPLE", "SAP Software Centre", false);
		maxGroupFilterList.add(uiFilter2);
		uiStatus.setMaxGroupFilterList(maxGroupFilterList);

		classUnderTest.updateGroupFilterList(overviewUIData, uiStatus);

		final List<FilterData> uiFilterDataList = overviewUIData.getGroupFilterList();
		final List<FilterData> maxFilterDataList = uiStatus.getMaxGroupFilterList();

		assertNull(uiFilterDataList);
		assertNotNull(maxFilterDataList);

		assertEquals(2, maxFilterDataList.size());

		assertEquals("WCEM_MULTI", maxFilterDataList.get(0).getKey());
		assertEquals("SAP Hardware Centre", maxFilterDataList.get(0).getDescription());
		assertTrue(maxFilterDataList.get(0).isSelected());

		assertEquals("WCEM_SIMPLE", maxFilterDataList.get(1).getKey());
		assertEquals("SAP Software Centre", maxFilterDataList.get(1).getDescription());
		assertFalse(maxFilterDataList.get(1).isSelected());
	}

	@Test
	public void testUpdateAppliedFiltersNoneSelected()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List<FilterData> filterDataList = new ArrayList<FilterData>();

		final FilterData userInput = new FilterData();
		userInput.setKey(FilterEnum.USER_INPUT.toString());
		userInput.setSelected(false);
		filterDataList.add(userInput);

		final FilterData priceRelevant = new FilterData();
		priceRelevant.setKey(FilterEnum.PRICE_RELEVANT.toString());
		priceRelevant.setSelected(false);
		filterDataList.add(priceRelevant);

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setCsticFilterList(filterDataList);

		classUnderTest.updateAppliedFilters(uiStatus, configOverviewData);

		final List appliedFilters = configOverviewData.getAppliedCsticFilters();

		assertEquals(1, appliedFilters.size());
		assertEquals(FilterEnum.VISIBLE, appliedFilters.get(0));
	}

	@Test
	public void testUpdateAppliedFiltersOneSelected()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List<FilterData> filterDataList = new ArrayList<FilterData>();

		final FilterData userInput = new FilterData();
		userInput.setKey(FilterEnum.USER_INPUT.toString());
		userInput.setSelected(true);
		filterDataList.add(userInput);

		final FilterData priceRelevant = new FilterData();
		priceRelevant.setKey(FilterEnum.PRICE_RELEVANT.toString());
		priceRelevant.setSelected(false);
		filterDataList.add(priceRelevant);

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setCsticFilterList(filterDataList);

		classUnderTest.updateAppliedFilters(uiStatus, configOverviewData);

		final List appliedFilters = configOverviewData.getAppliedCsticFilters();

		assertEquals(2, appliedFilters.size());
		assertEquals(FilterEnum.VISIBLE, appliedFilters.get(0));
		assertEquals(FilterEnum.USER_INPUT, appliedFilters.get(1));
	}

	@Test
	public void testUpdateAppliedFiltersTwoSelected()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List<FilterData> filterDataList = new ArrayList<FilterData>();

		final FilterData userInput = new FilterData();
		userInput.setKey(FilterEnum.USER_INPUT.toString());
		userInput.setSelected(true);
		filterDataList.add(userInput);

		final FilterData priceRelevant = new FilterData();
		priceRelevant.setKey(FilterEnum.PRICE_RELEVANT.toString());
		priceRelevant.setSelected(true);
		filterDataList.add(priceRelevant);

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setCsticFilterList(filterDataList);

		classUnderTest.updateAppliedFilters(uiStatus, configOverviewData);

		final List appliedFilters = configOverviewData.getAppliedCsticFilters();

		assertEquals(3, appliedFilters.size());
		assertEquals(FilterEnum.VISIBLE, appliedFilters.get(0));
		assertEquals(FilterEnum.USER_INPUT, appliedFilters.get(1));
		assertEquals(FilterEnum.PRICE_RELEVANT, appliedFilters.get(2));
	}

	@Test
	public void testUpdateAppliedFiltersNull()
	{
		final ConfigurationOverviewData configOverviewData = null;
		final List<FilterData> filterDataList = new ArrayList<FilterData>();

		final FilterData userInput = new FilterData();
		userInput.setKey(FilterEnum.USER_INPUT.toString());
		userInput.setSelected(true);
		filterDataList.add(userInput);

		final FilterData priceRelevant = new FilterData();
		priceRelevant.setKey(FilterEnum.PRICE_RELEVANT.toString());
		priceRelevant.setSelected(false);
		filterDataList.add(priceRelevant);

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setCsticFilterList(filterDataList);

		classUnderTest.updateAppliedFilters(uiStatus, configOverviewData);
		assertNull(configOverviewData);
	}

	@Test
	public void testUpdateGroups()
	{
		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> maxGroupFilterList = new ArrayList<>();
		final FilterData uiFilter = createFilterData("WCEM_MULTI", "SAP Hardware Centre", true);
		maxGroupFilterList.add(uiFilter);
		uiStatus.setMaxGroupFilterList(maxGroupFilterList);

		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		classUnderTest.updateGroups(uiStatus, configOverviewData);
		assertNotNull(configOverviewData);
		assertNotNull(configOverviewData.getAppliedGroupFilters());
		assertFalse(configOverviewData.getAppliedGroupFilters().isEmpty());
	}

	@Test
	public void testUpdateGroupsEmpty()
	{
		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> maxGroupFilterList = new ArrayList<>();
		final FilterData uiFilter = createFilterData("WCEM_MULTI", "SAP Hardware Centre", false);
		maxGroupFilterList.add(uiFilter);
		uiStatus.setMaxGroupFilterList(maxGroupFilterList);

		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		classUnderTest.updateGroups(uiStatus, configOverviewData);

		assertNotNull(configOverviewData);
		assertNotNull(configOverviewData.getAppliedGroupFilters());
		assertTrue(configOverviewData.getAppliedGroupFilters().isEmpty());
	}

	@Test
	public void updateCsticFilterListOverviewDataIsNull()
	{
		final UiStatus uiStatus = new UiStatus();
		classUnderTest.updateCsticFilterList(null, uiStatus);
		assertNull(uiStatus.getCsticFilterList());
	}

	@Test
	public void updateCsticFilterListOverviewDataEmpty()
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		final List<FilterData> csticFilterList = new ArrayList<>();
		overviewUIData.setCsticFilterList(csticFilterList);
		final UiStatus uiStatus = new UiStatus();

		classUnderTest.updateCsticFilterList(overviewUIData, uiStatus);
		assertNotNull(uiStatus.getCsticFilterList());
		assertTrue(uiStatus.getCsticFilterList().isEmpty());
	}

	@Test
	public void updateCsticFilterListOverviewDataNewFilter()
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		final List<FilterData> csticFilterList = new ArrayList<>();
		final FilterData uiFilter = createFilterData("WCEM_MULTI", "SAP Hardware Centre", true);
		csticFilterList.add(uiFilter);
		overviewUIData.setCsticFilterList(csticFilterList);
		final UiStatus uiStatus = new UiStatus();

		classUnderTest.updateCsticFilterList(overviewUIData, uiStatus);
		assertNotNull(uiStatus.getCsticFilterList());
		assertFalse(uiStatus.getCsticFilterList().isEmpty());
	}

	@Test
	public void updateCsticFilterListOverviewDataChangeFilter()
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		final List<FilterData> csticFilterList = new ArrayList<>();
		final FilterData uiFilter = createFilterData(FilterEnum.USER_INPUT.toString(), "New Filter", true);
		csticFilterList.add(uiFilter);
		overviewUIData.setCsticFilterList(csticFilterList);

		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> csticFilterListNew = new ArrayList<>();
		csticFilterListNew.add(createFilterData(FilterEnum.PRICE_RELEVANT.toString(), "Old Filter", true));
		uiStatus.setCsticFilterList(csticFilterListNew);

		classUnderTest.updateCsticFilterList(overviewUIData, uiStatus);


		final List<FilterData> uiFilterDataList = overviewUIData.getCsticFilterList();
		final List<FilterData> statusFilterList = uiStatus.getCsticFilterList();

		assertNotNull(uiFilterDataList);
		assertNotNull(statusFilterList);
		assertEquals(1, uiFilterDataList.size());
		assertEquals(1, statusFilterList.size());
		assertEquals(FilterEnum.USER_INPUT.toString(), statusFilterList.get(0).getKey());
	}

	@Test
	public void testAddBreadCrumb()
	{
		given(productData.getCode()).willReturn(PRODUCT_CODE);
		final OverviewUiData overviewPageModel = Mockito.mock(OverviewUiData.class);
		classUnderTest.addBreadCrumb(model, productData, overviewPageModel);

		verify(productConfigurationBreadcrumbBuilder, times(1)).getOverviewBreadcrumbs(PRODUCT_CODE, overviewPageModel);
	}

	@Test
	public void testUpdateConfigurationOverview() throws Exception
	{
		prepareUpdateCall();

		classUnderTest.updateConfiguationOverview(overviewUIData, model, request);
		verify(configFacade).getConfiguration(any(ConfigurationData.class));
	}

	@Test
	public void getOrCreateUiStatusForCartEntry()
	{
		Mockito.when(sessionAccessFacade.getUiStatusForCartEntry(CART_ENTRY_KEY)).thenReturn(uiStatus);
		final UiStatus returnedUiStatus = classUnderTest.getOrCreateUiStatusForCartEntry(CART_ENTRY_KEY);
		assertSame(returnedUiStatus, uiStatus);
	}

	@Test
	public void getOrCreateUiStatusForCartEntryDefaults()
	{
		Mockito.when(sessionAccessFacade.getUiStatusForCartEntry(CART_ENTRY_KEY)).thenReturn(null);
		final UiStatus returnedUiStatus = classUnderTest.getOrCreateUiStatusForCartEntry(CART_ENTRY_KEY);
		assertNotNull(returnedUiStatus);
		assertTrue(returnedUiStatus.isHideImageGallery());
	}

	public void prepareUpdateCall() throws Exception
	{
		prepareGetCall();

		overviewUIData = new OverviewUiData();
		overviewUIData.setConfigId(CONFIG_ID);
		overviewUIData.setProductCode(PRODUCT_CODE);
		overviewUIData.setOverviewMode(OverviewMode.CONFIGURATION_OVERVIEW);
		classUnderTest.initializeFilterListsInUiStatus(configOverviewData, uiStatus);
		given(configFacade.getConfiguration(argThat(new ArgumentMatcher<ConfigurationData>()
		{
			@Override
			public boolean matches(final ConfigurationData arg0)
			{
				return CONFIG_ID.equals(arg0.getConfigId());
			}
		}))).willReturn(configData);
	}

	protected void prepareGetCall() throws Exception
	{
		initializeFirstCall();
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn(CART_ENTRY_KEY);
		given(configurationOverviewFacade.getOverviewForConfiguration(eq(CONFIG_ID), any())).willReturn(configOverviewData);
		given(sessionAccessFacade.getUiStatusForCartEntry(CART_ENTRY_KEY)).willReturn(uiStatus);
		final CartData cartData = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setItemPK(CART_ENTRY_KEY);
		final ProductData productData = new ProductData();
		productData.setCode(PRODUCT_CODE);
		entry.setProduct(productData);
		entry.setEntryNumber(Integer.valueOf(1));
		cartData.setEntries(Collections.singletonList(entry));
		given(cartFacadeMock.getSessionCart()).willReturn(cartData);
	}

	@Override
	protected void initializeFirstCall() throws Exception
	{
		super.initializeFirstCall();
		given(cmsPageService.getPageForId("productConfigOverview")).willReturn(new ProductConfigPageModel());
	}

	@Test
	public void testGetConfigurationOverview() throws Exception
	{
		prepareGetCall();
		given(configFacade.getConfiguration(Mockito.nullable(ConfigurationData.class))).willReturn(configData);
		assertEquals(SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME,
				classUnderTest.getConfigurationOverview(CONFIG_ID, model, request));
		verify(configFacade).getConfiguration(Mockito.nullable(ConfigurationData.class));
	}

	@Test
	public void testGetConfigurationOverviewNoCartEntry() throws Exception
	{
		prepareGetCall();
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn("");

		assertEquals(AbstractController.REDIRECT_PREFIX + AbstractController.ROOT,
				classUnderTest.getConfigurationOverview(CONFIG_ID, model, request));
		verify(configFacade, times(0)).getConfiguration(any(ConfigurationData.class));
	}

	@Test
	public void testHandleCPQActionNull() throws BusinessException
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		overviewUIData.setCpqAction(CPQOverviewActionType.TOGGLE_IMAGE_GALLERY);
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setProductCode(PRODUCT_CODE);
		final UiStatus uiStatus = new UiStatus();

		classUnderTest.handleCPQAction(overviewUIData, configOverviewData, uiStatus);
		verify(sessionAccessFacade).setUiStatusForCartEntry(Mockito.nullable(String.class), eq(uiStatus));
	}

	@Test
	public void testHandleCPQActionToggleImageGallery() throws BusinessException
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		overviewUIData.setCpqAction(CPQOverviewActionType.TOGGLE_IMAGE_GALLERY);
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setProductCode(PRODUCT_CODE);
		final UiStatus uiStatus = new UiStatus();
		uiStatus.setHideImageGallery(false);

		classUnderTest.handleCPQAction(overviewUIData, configOverviewData, uiStatus);

		assertTrue(uiStatus.isHideImageGallery());
		verify(sessionAccessFacade).setUiStatusForCartEntry(Mockito.nullable(String.class), eq(uiStatus));
	}

	@Test
	public void testHandleCPQActionAppliedFilter() throws BusinessException
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		overviewUIData.setCpqAction(CPQOverviewActionType.APPLY_FILTER);
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setProductCode(PRODUCT_CODE);
		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> maxFilterDataList = new ArrayList<FilterData>();
		uiStatus.setMaxGroupFilterList(maxFilterDataList);

		final List<FilterData> csticFilterList = new ArrayList<FilterData>();
		csticFilterList.add(createFilterData(FilterEnum.PRICE_RELEVANT.toString(), "Price relevant", true));
		overviewUIData.setCsticFilterList(csticFilterList);

		classUnderTest.handleCPQAction(overviewUIData, configOverviewData, uiStatus);

		verify(sessionAccessFacade).setUiStatusForCartEntry(Mockito.nullable(String.class), eq(uiStatus));
		final List appliedFilters = configOverviewData.getAppliedCsticFilters();
		assertEquals(2, appliedFilters.size());
		assertEquals(FilterEnum.PRICE_RELEVANT, appliedFilters.get(1));
	}

	@Test
	public void testPopulateConfigurationModel()
	{
		given(configurationOverviewFacade.getOverviewForConfiguration(eq(CONFIG_ID), any())).willReturn(configOverviewData);
		final ConfigurationOverviewData result = classUnderTest.populateConfigurationModel(null, CONFIG_ID, configOverviewData);
		assertNotNull(result);
		assertEquals(configOverviewData, result);
	}

	@Test
	public void testReReadEntry() throws Exception
	{
		final CartData cartData = new CartData();
		cartData.setEntries(Collections.singletonList(abstractOrderEntry));
		given(cartFacadeMock.getSessionCart()).willReturn(cartData);
		final OrderEntryData orderEntryData = classUnderTest.reReadEntry(addedToCart);
		assertEquals(abstractOrderEntry, orderEntryData);
	}

	@Test(expected = IllegalStateException.class)
	public void testReReadEntryNoAddedEntry() throws Exception
	{
		addedToCart.setEntry(null);
		classUnderTest.reReadEntry(addedToCart);
	}

	@Test(expected = IllegalStateException.class)
	public void testReReadEntryNumbersDoNotMatch() throws Exception
	{
		final CartData cartData = new CartData();
		cartData.setEntries(Collections.singletonList(abstractOrderEntry));
		given(cartFacadeMock.getSessionCart()).willReturn(cartData);
		final OrderEntryData wrongEntry = new OrderEntryData();
		wrongEntry.setEntryNumber(Integer.valueOf(999));
		cartData.setEntries(Arrays.asList(wrongEntry));
		classUnderTest.reReadEntry(addedToCart);
	}


}
