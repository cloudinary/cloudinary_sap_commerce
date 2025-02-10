/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantFacade;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SearchSimilarVariantsControllerTest
{

	private static final String UNKNOWN = "Unknown";
	private static final String CONFIG_ID = "ABCD";
	private static final Integer ENTRY_NUMBER = Integer.valueOf(3);
	private static final String ENTRY_KEY = "324f";
	@InjectMocks
	private SearchSimilarVariantsController classUnderTest;
	private Model model;

	@Mock
	private ConfigurationVariantFacade variantFacadeMock;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy abstractOrderLinkStrategy;
	@Mock
	private CartFacade cartFacade;

	private List<ConfigurationVariantData> variants;
	private final ConfigurationVariantData variant = new ConfigurationVariantData();
	private final CartData cartData = new CartData();
	private final List<OrderEntryData> entries = new ArrayList<>();
	private final OrderEntryData entry = new OrderEntryData();

	@Before
	public void setUp()
	{
		model = new ExtendedModelMap();
		variants = new ArrayList<ConfigurationVariantData>();
		variants.add(variant);
		cartData.setEntries(entries);
		entries.add(entry);
		entry.setEntryNumber(ENTRY_NUMBER);
		entry.setItemPK(ENTRY_KEY);
		given(variantFacadeMock.searchForSimilarVariants("config_123", "Product_123")).willReturn(variants);
		given(abstractOrderLinkStrategy.getCartEntryForDraftConfigId(CONFIG_ID)).willReturn(ENTRY_KEY);
		given(cartFacade.getSessionCart()).willReturn(cartData);
	}

	@Test
	public void testGetViewName() throws CMSItemNotFoundException
	{
		final String viewName = classUnderTest.getViewName();
		assertEquals("addon:/ysapproductconfigaddon/pages/configuration/searchVariantsForAJAXRequests", viewName);
	}

	@Test
	public void testSearchVariant() throws BusinessException
	{
		classUnderTest.searchVariant("config_123", "Product_123", model);
		final List<ConfigurationVariantData> variantResult = (List<ConfigurationVariantData>) model.asMap()
				.get(SapproductconfigfrontendWebConstants.VARIANT_SEARCH_RESULT_ATTRIBUTE);
		assertSame(variants, variantResult);
	}

	@Test
	public void testCheckForCartEntryLink() throws BusinessException
	{
		classUnderTest.checkForCartEntryLink(variants, CONFIG_ID);
		assertEquals(ENTRY_NUMBER, variants.get(0).getCartEntryNumber());
	}

	@Test
	public void testCheckForCartEntryLinkNoDraftForConfig() throws BusinessException
	{
		classUnderTest.checkForCartEntryLink(variants, UNKNOWN);
		assertNull(variants.get(0).getCartEntryNumber());
	}

	@Test(expected = IllegalStateException.class)
	public void testCheckForCartEntryLinkCartEntryNotFound() throws BusinessException
	{
		entry.setItemPK(UNKNOWN);
		classUnderTest.checkForCartEntryLink(variants, CONFIG_ID);

	}
}
