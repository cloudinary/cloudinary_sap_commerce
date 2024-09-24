/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.renderer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.sap.productconfig.frontend.jalo.GeneratedProductConfigurationPriceSummaryComponent;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PriceSummaryComponentRendererTest
{

	@InjectMocks
	private PriceSummaryComponentRenderer classUnderTest;

	private final AbstractCMSComponentModel component = new AbstractCMSComponentModel();

	@Mock
	private ProviderFactory providerFactory;
	@Mock
	private PricingConfigurationParameter pricingParams;
	@Mock
	private CMSComponentService cmsComponentService;
	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		classUnderTest.setProviderFactory(providerFactory);
		classUnderTest.setCmsComponentService(cmsComponentService);
		classUnderTest.setModelService(modelService);
		given(providerFactory.getPricingParameter()).willReturn(pricingParams);
		given(cmsComponentService.getReadableEditorProperties(component))
				.willReturn(Collections.singleton(GeneratedProductConfigurationPriceSummaryComponent.SHOWPRICEDETAILS));

	}

	@Test
	public void testIsNeitherBaseNorOptionPriceAvailable_doNotShowBasePriceAndSelectedOptions()
	{
		given(Boolean.valueOf(pricingParams.showBasePriceAndSelectedOptions())).willReturn(Boolean.FALSE);
		assertTrue(classUnderTest.isNeitherBaseNorOptionPriceAvailable());
	}

	@Test
	public void testIsNeitherBaseNorOptionPriceAvailable_showBasePriceAndSelectedOptions()
	{
		given(Boolean.valueOf(pricingParams.showBasePriceAndSelectedOptions())).willReturn(Boolean.TRUE);
		assertFalse(classUnderTest.isNeitherBaseNorOptionPriceAvailable());
	}

	@Test
	public void testGetVariablesToExpose_Overriden()
	{
		given(Boolean.valueOf(pricingParams.showBasePriceAndSelectedOptions())).willReturn(Boolean.FALSE);
		given(modelService.getAttributeValue(component, GeneratedProductConfigurationPriceSummaryComponent.SHOWPRICEDETAILS))
				.willReturn(Boolean.TRUE);
		final Map<String, Object> vars = classUnderTest.getVariablesToExpose(null, component);
		assertFalse(Boolean.valueOf(vars.get(GeneratedProductConfigurationPriceSummaryComponent.SHOWPRICEDETAILS).toString())
				.booleanValue());
	}

	@Test
	public void testGetVariablesToExpose_notOverriden()
	{
		given(Boolean.valueOf(pricingParams.showBasePriceAndSelectedOptions())).willReturn(Boolean.TRUE);
		given(modelService.getAttributeValue(component, GeneratedProductConfigurationPriceSummaryComponent.SHOWPRICEDETAILS))
				.willReturn(Boolean.TRUE);
		final Map<String, Object> vars = classUnderTest.getVariablesToExpose(null, component);
		assertTrue(Boolean.valueOf(vars.get(GeneratedProductConfigurationPriceSummaryComponent.SHOWPRICEDETAILS).toString())
				.booleanValue());
	}

	@Test
	public void testGetVariablesToExpose_FalseByCMS()
	{
		given(Boolean.valueOf(pricingParams.showBasePriceAndSelectedOptions())).willReturn(Boolean.TRUE);
		given(modelService.getAttributeValue(component, GeneratedProductConfigurationPriceSummaryComponent.SHOWPRICEDETAILS))
				.willReturn(Boolean.FALSE);
		final Map<String, Object> vars = classUnderTest.getVariablesToExpose(null, component);
		assertFalse(Boolean.valueOf(vars.get(GeneratedProductConfigurationPriceSummaryComponent.SHOWPRICEDETAILS).toString())
				.booleanValue());
	}
}
