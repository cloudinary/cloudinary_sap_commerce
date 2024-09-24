/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionHandler;
import de.hybris.platform.acceleratorfacades.cart.action.exceptions.CartEntryActionException;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.integrationtests.CPQFacadeLayerTest;
import de.hybris.platform.sap.productconfig.frontend.handler.ConfigCopyCartEntryActionHandler;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for configuration copy cart entry action integration
 */
@IntegrationTest
public class ConfigCopyCartEntryActionIntegrationTest extends CPQFacadeLayerTest
{
	private CartEntryActionHandler copyActionHandler;

	@Resource(name = "sapProductConfigConfigurationCopyStrategy")
	private ConfigurationCopyStrategy configurationCopyStrategy;

	@Before
	public void setUp() throws Exception
	{
		final ConfigCopyCartEntryActionHandler cpqCopyCartActionHandler = new ConfigCopyCartEntryActionHandler();
		cpqCopyCartActionHandler.setCartFacade(cartFacade);
		cpqCopyCartActionHandler.setConfigCartFacade(cpqCartFacade);
		cpqCopyCartActionHandler.setAbstractOrderEntryLinkStrategy(cpqAbstractOrderEntryLinkStrategy);
		cpqCopyCartActionHandler.setConfigurationCopyStrategy(configurationCopyStrategy);
		copyActionHandler = cpqCopyCartActionHandler;
		prepareCPQData();

		login(USER_NAME, PASSWORD);
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
	}


	@Test
	public void testCopyAction() throws CommerceCartModificationException, CartEntryActionException
	{
		// create config and modify it
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		facadeConfigValueHelper.setCsticValue(configData, "YSAP_POC_SIMPLE_FLAG", "X", false);
		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);
		facadeConfigValueHelper.setCstic(configData, "WCEM_NUMBER_SIMPLE", "125");
		cpqFacade.updateConfiguration(configData);

		// add To cart
		cpqCartFacade.addConfigurationToCart(configData);
		List<OrderEntryData> cartEntries = cartFacade.getSessionCart().getEntries();
		assertEquals("There should be exactly one item in the cart before copy", 1, cartEntries.size());
		OrderEntryData firstEntry = cartEntries.get(0);
		OrderEntryData secondEntry = null;

		//copy
		copyActionHandler.handleAction(Collections.singletonList(Long.valueOf(firstEntry.getEntryNumber().intValue())));
		cartEntries = cartFacade.getSessionCart().getEntries();
		assertEquals("There should be exactly two items in the cart after copy", 2, cartEntries.size());

		//check
		firstEntry = cartEntries.get(0);
		secondEntry = cartEntries.get(1);
		final ConfigurationData configFirstItem = buildFromEntry(firstEntry);
		final ConfigurationData configSecondItem = buildFromEntry(secondEntry);

		assertNotEquals("Both cart items have same config ID, copy did not create new config!", configSecondItem.getConfigId(),
				configFirstItem.getConfigId());
		assertEquals("product codes were different after copy", configFirstItem.getKbKey().getProductCode(),
				configSecondItem.getKbKey().getProductCode());

		//asser config are identical
		assertEquals(configFirstItem.getCsticGroupsFlat().size(), configSecondItem.getCsticGroupsFlat().size());
		for (int grpIdx = 0; grpIdx < configFirstItem.getCsticGroupsFlat().size(); grpIdx++)
		{
			final UiGroupData firstConfigGrp = configFirstItem.getCsticGroupsFlat().get(grpIdx);
			final UiGroupData secondConfigGrp = configSecondItem.getCsticGroupsFlat().get(grpIdx);
			assertEquals(firstConfigGrp.getCstics().size(), secondConfigGrp.getCstics().size());
			for (int csticIdx = 0; csticIdx < firstConfigGrp.getCstics().size(); csticIdx++)
			{
				final CsticData firstConfigCstic = firstConfigGrp.getCstics().get(csticIdx);
				final CsticData secondConfigCstic = secondConfigGrp.getCstics().get(csticIdx);
				if (firstConfigCstic.getName().equals("WCEM_NUMBER_SIMPLE"))
				{
					assertEquals("125", secondConfigCstic.getFormattedValue());
				}
				else if (firstConfigCstic.getName().equals("YSAP_POC_SIMPLE_FLAG"))
				{
					assertFalse(secondConfigCstic.getDomainvalues().get(0).isSelected());
					assertFalse(firstConfigCstic.getDomainvalues().get(0).isSelected());
				}
				assertEquals(firstConfigCstic.getValue(), secondConfigCstic.getValue());

			}
		}
	}

	protected ConfigurationData buildFromEntry(final OrderEntryData entry)
	{
		ConfigurationData configData = new ConfigurationData();
		final String configId = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(entry.getItemPK());
		configData.setConfigId(configId);
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(entry.getProduct().getCode());
		configData.setKbKey(kbKey);
		configData = cpqFacade.getConfiguration(configData);
		return configData;
	}
}
