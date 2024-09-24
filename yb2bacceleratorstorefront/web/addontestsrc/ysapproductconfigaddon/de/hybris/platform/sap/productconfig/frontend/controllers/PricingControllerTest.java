/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationPricingFacade;
import de.hybris.platform.sap.productconfig.facades.PriceDataPair;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.util.impl.JSONProviderFactory;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStateHandler;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;




/**
 * Unit test for {@link PricingController}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PricingControllerTest
{
	private static final String SAVING = "Saving";
	private static final String SAVE = "Save";
	private static final String SELECTED = "Include";
	private static final String VALUE_NAME = "csticValue";
	private static final String CSTIC_NAME = "cstic1";
	private static final String EUR = "EUR";
	private PricingController classUnderTest;
	private UiStatus uiStatus;
	@Mock
	private SessionAccessFacade sessionAccessFacade;
	@Mock
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	@Mock
	private Model mockedModel;
	@Mock
	private ConfigurationPricingFacade mockedPricingFacade;
	private PricingData priceSummary;

	@Before
	public void setUp()
	{
		classUnderTest = spy(new PricingController());
		classUnderTest.setSessionAccessFacade(sessionAccessFacade);
		classUnderTest.setUiStateHandler(new UiStateHandler());
		classUnderTest.setConfigPricingFacade(mockedPricingFacade);
		classUnderTest.setProductLinkStrategy(configurationProductLinkStrategy);
		classUnderTest.setAbstractOrderEntryLinkStrategy(configurationAbstractOrderEntryLinkStrategy);
		uiStatus = new UiStatus();

		given(sessionAccessFacade.getUiStatusForProduct("pCode")).willReturn(uiStatus);

		priceSummary = new PricingData();
		priceSummary.setBasePrice(ConfigPricing.NO_PRICE);
		priceSummary.setCurrentTotal(ConfigPricing.NO_PRICE);
		priceSummary.setCurrentTotalSavings(ConfigPricing.NO_PRICE);
		priceSummary.setSelectedOptions(ConfigPricing.NO_PRICE);
		doReturn(SELECTED).when(classUnderTest).callLocalization(PricingController.VALUE_PRICES_INCLUDED);
		doReturn(SAVE).when(classUnderTest).callLocalization(PricingController.SAVE_PREFIX);
		doReturn(SAVING).when(classUnderTest).callLocalization(PricingController.SAVING_PREFIX);
		given(configurationProductLinkStrategy.retrieveProductCode("123")).willReturn("pCode");
	}

	@Test
	public void testUpdatePricing()
	{
		given(mockedPricingFacade.getValuePrices(any(List.class), eq("123"))).willReturn(Collections.emptyList());
		given(mockedPricingFacade.getPriceSummary("123")).willReturn(priceSummary);

		final String jsonString = classUnderTest.updatePricing("123");
		assertNotNull(jsonString);
		//try to parse the json string to make sure it's parsable
		assertNotNull(jsonToObject(jsonString));
	}

	@Test
	public void testUpdatePricing_uiStatusNull()
	{
		given(sessionAccessFacade.getUiStatusForProduct("pCode")).willReturn(null);
		final String jsonString = classUnderTest.updatePricing("123");
		assertNotNull(jsonString);
		//try to parse the json string to make sure it's parsable
		assertNotNull(jsonToObject(jsonString));
	}

	@Test
	public void testUpdatePricing_productCodeNull()
	{
		given(mockedPricingFacade.getValuePrices(any(List.class), eq("123"))).willReturn(Collections.emptyList());
		given(mockedPricingFacade.getPriceSummary("123")).willReturn(priceSummary);

		given(configurationProductLinkStrategy.retrieveProductCode("123")).willReturn(null);
		given(configurationAbstractOrderEntryLinkStrategy.getCartEntryForConfigId("123")).willReturn("cartEntry");
		given(sessionAccessFacade.getUiStatusForCartEntry("cartEntry")).willReturn(uiStatus);
		final String jsonString = classUnderTest.updatePricing("123");
		assertNotNull(jsonString);
		//try to parse the json string to make sure it's parsable
		assertNotNull(jsonToObject(jsonString));
	}


	@Test
	public void testToJson()
	{
		final String jsonString = classUnderTest.toJson(priceSummary, Collections.emptyList());
		final JsonObject jsonObj = jsonToObject(jsonString);
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_BASE_PRICE_VALUE));
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_CURRENT_TOTAL_VALUE));
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_SELECTED_OPTIONS_VALUE));
		assertTrue(jsonObj.getJsonArray(PricingController.JSON_NAME_VALUE_PRICE_ARRAY).isEmpty());
	}

	@Test
	public void testPriceSummmaryToJson()
	{
		priceSummary = createPriceSummary(EUR, "800.00", "1000.99", "100.99", "200.99");
		final JsonObjectBuilder jsonBuilder = classUnderTest.toJson(priceSummary);
		final JsonObject jsonObj = jsonBuilder.build();
		assertEquals("EUR 800.00", jsonObj.getString(PricingController.JSON_NAME_BASE_PRICE_VALUE));
		assertEquals("EUR 1000.99", jsonObj.getString(PricingController.JSON_NAME_CURRENT_TOTAL_VALUE));
		assertEquals("EUR 100.99", jsonObj.getString(PricingController.JSON_NAME_CURRENT_TOTAL_SAVINGS));
		assertEquals("EUR 200.99", jsonObj.getString(PricingController.JSON_NAME_SELECTED_OPTIONS_VALUE));
		assertFalse(jsonObj.containsKey(PricingController.JSON_NAME_PRICING_ERROR));
	}

	@Test
	public void testPriceSummmaryToJson_PricingError()
	{
		priceSummary = new PricingData();
		final JsonObjectBuilder jsonBuilder = classUnderTest.toJson(priceSummary);
		final JsonObject jsonObj = jsonBuilder.build();
		assertTrue(jsonObj.getBoolean(PricingController.JSON_NAME_PRICING_ERROR));
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_BASE_PRICE_VALUE));
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_CURRENT_TOTAL_VALUE));
		assertTrue(jsonObj.containsKey(PricingController.JSON_NAME_SELECTED_OPTIONS_VALUE));
	}

	@Test
	public void testValuePricesToJson()
	{
		final PriceData price = createPriceData(EUR, "200.99");
		final List<PriceValueUpdateData> valuePrices = createValuePrices(price, true);
		final JsonArrayBuilder arrayBuilder = classUnderTest.toJson(valuePrices);
		final JsonArray jsonArray = arrayBuilder.build();
		final JsonObject jsonValue = jsonArray.getJsonObject(0);
		assertEquals(CSTIC_NAME, jsonValue.getString(PricingController.JSON_NAME_CSTIC_KEY));
		final JsonArray csticValues = jsonValue.getJsonArray(PricingController.JSON_NAME_CSTIC_VALUE_ARRAY);
		assertEquals(VALUE_NAME, csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_CSTIC_VALUE_KEY));
		assertEquals("EUR 200.99", csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_VALUE_PRICE));
	}

	@Test
	public void testObsoletePricesToJson()
	{
		final PriceData valuePrice = createPriceData(EUR, "200.99");
		final PriceData obsoletePrice = createPriceData(EUR, "300.99");
		final List<PriceValueUpdateData> valuePrices = createValuePrices(valuePrice, obsoletePrice, false);
		final JsonArrayBuilder arrayBuilder = classUnderTest.toJson(valuePrices);
		final JsonArray jsonArray = arrayBuilder.build();
		final JsonObject jsonValue = jsonArray.getJsonObject(0);
		assertEquals(CSTIC_NAME, jsonValue.getString(PricingController.JSON_NAME_CSTIC_KEY));
		final JsonArray csticValues = jsonValue.getJsonArray(PricingController.JSON_NAME_CSTIC_VALUE_ARRAY);
		assertEquals(VALUE_NAME, csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_CSTIC_VALUE_KEY));
		assertEquals("EUR 300.99", csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_VALUE_OBSOLETE_PRICE));
	}

	@Test
	public void testValuePricesToJson_Included_ForDeltaPrices()
	{
		final PriceData price = createPriceData(EUR, "0.00");
		final List<PriceValueUpdateData> valuePrices = createValuePrices(price, true);
		valuePrices.get(0).setSelectedValues(Collections.singletonList(VALUE_NAME));
		final JsonArrayBuilder arrayBuilder = classUnderTest.toJson(valuePrices);
		final JsonArray jsonArray = arrayBuilder.build();
		final JsonObject jsonValue = jsonArray.getJsonObject(0);
		assertEquals(CSTIC_NAME, jsonValue.getString(PricingController.JSON_NAME_CSTIC_KEY));
		assertEquals(Boolean.TRUE, jsonValue.getBoolean(PricingController.JSON_NAME_SHOW_DELTA_PRICES));
		final JsonArray csticValues = jsonValue.getJsonArray(PricingController.JSON_NAME_CSTIC_VALUE_ARRAY);
		assertEquals(VALUE_NAME, csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_CSTIC_VALUE_KEY));
		assertEquals(SELECTED, csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_VALUE_PRICE));
	}

	@Test
	public void testValuePricesToJson_Included_ForAbsolutePrices()
	{
		final PriceData price = createPriceData(EUR, "0.00");
		final List<PriceValueUpdateData> valuePrices = createValuePrices(price, false);
		final JsonArrayBuilder arrayBuilder = classUnderTest.toJson(valuePrices);
		final JsonArray jsonArray = arrayBuilder.build();
		final JsonObject jsonValue = jsonArray.getJsonObject(0);
		assertEquals(CSTIC_NAME, jsonValue.getString(PricingController.JSON_NAME_CSTIC_KEY));
		assertEquals(Boolean.FALSE, jsonValue.getBoolean(PricingController.JSON_NAME_SHOW_DELTA_PRICES));
		final JsonArray csticValues = jsonValue.getJsonArray(PricingController.JSON_NAME_CSTIC_VALUE_ARRAY);
		assertEquals(VALUE_NAME, csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_CSTIC_VALUE_KEY));
		assertEquals("", csticValues.getJsonObject(0).getString(PricingController.JSON_NAME_VALUE_PRICE));
	}


	@Test
	public void testValuePricesWithNoConfigPriceToJson()
	{
		final PriceData price = ConfigPricing.NO_PRICE;
		final List<PriceValueUpdateData> valuePrices = createValuePrices(price, true);
		final JsonArrayBuilder arrayBuilder = classUnderTest.toJson(valuePrices);
		final JsonArray jsonArray = arrayBuilder.build();
		assertTrue(jsonArray.isEmpty());
	}

	@Test
	public void testCsticValueArrayToJsonFalse()
	{
		final JsonArrayBuilder csticValuesArrayBuilder = JSONProviderFactory.getJSONProvider().createArrayBuilder();
		final PriceValueUpdateData valuePrice = createPriceValueUpdateData(CSTIC_NAME, VALUE_NAME, ConfigPricing.NO_PRICE, null,
				true);
		final boolean atLeastOneValuePrice = classUnderTest.addValuePriceToCsticValueArray(csticValuesArrayBuilder, valuePrice);
		assertFalse(atLeastOneValuePrice);
	}

	@Test
	public void testCsticValueArrayToJsonTrue()
	{
		final JsonArrayBuilder csticValuesArrayBuilder = JSONProviderFactory.getJSONProvider().createArrayBuilder();
		final PriceValueUpdateData valuePrice = createPriceValueUpdateData(CSTIC_NAME, VALUE_NAME, createPriceData(EUR, "200.99"),
				null, true);
		final boolean atLeastOneValuePrice = classUnderTest.addValuePriceToCsticValueArray(csticValuesArrayBuilder, valuePrice);
		assertTrue(atLeastOneValuePrice);
	}

	@Test
	public void testRetrieveObsoletePriceAsTextNull()
	{
		final String formattedPrice = classUnderTest.retrieveObsoletepriceAsText(false, null, false);
		assertTrue(formattedPrice.isEmpty());
	}

	@Test
	public void testRetrieveObsoletePriceAsText()
	{
		final PriceData obsoletePrice = createPriceData(EUR, "500.00");
		final String formattedPrice = classUnderTest.retrieveObsoletepriceAsText(false, obsoletePrice, true);
		assertEquals("EUR 500.00", formattedPrice);
	}

	@Test
	public void testRetrieveObsoletePriceAsTextForDelta()
	{
		final PriceData obsoletePrice = createPriceData(EUR, "500.00");
		final String formattedPrice = classUnderTest.retrieveObsoletepriceAsText(true, obsoletePrice, false);
		assertEquals("Save EUR 500.00", formattedPrice);
	}

	@Test
	public void testRetrieveObsoletePriceAsTextForDeltaSelected()
	{
		final PriceData obsoletePrice = createPriceData(EUR, "500.00");
		final String formattedPrice = classUnderTest.retrieveObsoletepriceAsText(true, obsoletePrice, true);
		assertEquals("Saving EUR 500.00", formattedPrice);
	}

	@Test
	public void testRetrieveValuePriceAsText()
	{
		final PriceData valuePrice = createPriceData(EUR, "500.00");
		final String formattedPrice = classUnderTest.retrieveValuePriceAsText(false, valuePrice, false);
		assertEquals("EUR 500.00", formattedPrice);
	}

	@Test
	public void testRetrieveValuePriceAsTextZeroValuePrice()
	{
		final PriceData valuePrice = createPriceData(EUR, "0.00");
		final String formattedPrice = classUnderTest.retrieveValuePriceAsText(false, valuePrice, true);
		assertEquals("", formattedPrice);
	}

	@Test
	public void testRetrieveValuePriceAsTextZeroDeltaPrice()
	{
		final PriceData valuePrice = createPriceData(EUR, "0.00");
		final String formattedPrice = classUnderTest.retrieveValuePriceAsText(true, valuePrice, true);
		assertEquals(SELECTED, formattedPrice);
	}

	@Test
	public void testRetrieveValuePriceAsTextZeroDeltaPriceNotSelected()
	{
		final PriceData valuePrice = createPriceData(EUR, "0.00");
		final String formattedPrice = classUnderTest.retrieveValuePriceAsText(true, valuePrice, false);
		assertEquals("", formattedPrice);
	}

	protected List<PriceValueUpdateData> createValuePrices(final PriceData priceValue, final PriceData obsoletePrice,
			final boolean showDeltaPrices)
	{
		final List<PriceValueUpdateData> valuePrices = new ArrayList<>();
		valuePrices.add(createPriceValueUpdateData(CSTIC_NAME, VALUE_NAME, priceValue, obsoletePrice, showDeltaPrices));

		return valuePrices;
	}

	protected List<PriceValueUpdateData> createValuePrices(final PriceData priceValue, final boolean showDeltaPrices)
	{
		final List<PriceValueUpdateData> valuePrices = new ArrayList<>();
		valuePrices.add(createPriceValueUpdateData(CSTIC_NAME, VALUE_NAME, priceValue, null, showDeltaPrices));

		return valuePrices;
	}

	protected PriceValueUpdateData createPriceValueUpdateData(final String csticKey, final String csticValueKey,
			final PriceData priceValue, final PriceData obsoletePrice, final boolean showDeltaPrices)
	{
		final PriceValueUpdateData cstic1 = new PriceValueUpdateData();
		cstic1.setCsticUiKey(csticKey);
		final Map<String, PriceDataPair> prices = new HashMap<>();
		final PriceDataPair pair = new PriceDataPair();
		pair.setPriceValue(priceValue);
		pair.setObsoletePriceValue(obsoletePrice);
		prices.put(csticValueKey, pair);
		cstic1.setPrices(prices);
		cstic1.setShowDeltaPrices(showDeltaPrices);
		cstic1.setSelectedValues(Collections.emptyList());
		return cstic1;
	}


	protected PriceData createPriceData(final String currency, final String value)
	{
		final PriceData price = new PriceData();
		price.setFormattedValue(currency + " " + value);
		price.setValue(new BigDecimal(value));
		return price;
	}

	protected PricingData createPriceSummary(final String currecncy, final String basePrice, final String currentTotal,
			final String currentSavings, final String selectedOptions)
	{
		priceSummary = new PricingData();
		priceSummary.setBasePrice(createPriceData(currecncy, basePrice));
		priceSummary.setCurrentTotal(createPriceData(currecncy, currentTotal));
		priceSummary.setCurrentTotalSavings(createPriceData(currecncy, currentSavings));
		priceSummary.setSelectedOptions(createPriceData(currecncy, selectedOptions));
		return priceSummary;
	}


	protected JsonObject jsonToObject(final String json)
	{
		final JsonReader jsonReader = Json.createReader(new StringReader(json));
		final JsonObject object = jsonReader.readObject();
		jsonReader.close();
		return object;
	}
}
