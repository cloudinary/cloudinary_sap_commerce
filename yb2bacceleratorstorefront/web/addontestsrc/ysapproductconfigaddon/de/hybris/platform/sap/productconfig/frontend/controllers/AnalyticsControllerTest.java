/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticCsticData;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticCsticValueData;
import de.hybris.platform.sap.productconfig.facades.analytics.ConfigurationAnalyticsFacade;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStateHandler;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;



/**
 * Unit test for {@link AnalyticsController}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AnalyticsControllerTest
{
	private AnalyticsController classUnderTest;
	private UiStatus uiStatus;
	@Mock
	private SessionAccessFacade sessionAccessFacade;
	@Mock
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;
	@Mock
	private ConfigurationAnalyticsFacade mockedAnalyticsFacade;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy orderEntryLinkStrategy;

	@Before
	public void setUp()
	{
		classUnderTest = spy(new AnalyticsController());
		classUnderTest.setSessionAccessFacade(sessionAccessFacade);
		classUnderTest.setUiStateHandler(new UiStateHandler());
		classUnderTest.setAnalyticsFacade(mockedAnalyticsFacade);
		classUnderTest.setProductLinkStrategy(configurationProductLinkStrategy);
		classUnderTest.setAbstractOrderEntryLinkStrategy(orderEntryLinkStrategy);
		uiStatus = new UiStatus();

		willReturn("_XX_ of customer chose this option").given(classUnderTest).callLocalisation(anyString(), any());
	}

	@Test
	public void testUpdateAnalytics_uiStatusNull()
	{
		final String jsonString = classUnderTest.updateAnalytics("pCode");

		assertNotNull(jsonString);
		//try to parse the json string to make sure it's parsable
		assertNotNull(jsonToObject(jsonString));
	}

	@Test
	public void testUpdateAnalytics_configNull()
	{
		final String jsonString = classUnderTest.updateAnalytics("pCode");

		assertNotNull(jsonString);
		//try to parse the json string to make sure it's parsable
		assertNotNull(jsonToObject(jsonString));
	}


	@Test
	public void testUpdateAnalytics()
	{
		final List<AnalyticCsticData> analyticData = new ArrayList<>();

		final String jsonString = classUnderTest.updateAnalytics("pCode");

		assertNotNull(jsonString);
		//try to parse the json string to make sure it's parsable
		assertNotNull(jsonToObject(jsonString));
	}

	@Test
	public void testToJsonEmpty()
	{
		final JsonObject jsonObj = classUnderTest.toJson(Collections.emptyList()).build();
		assertTrue(jsonObj.getJsonArray(AnalyticsController.JSON_NAME_ANALYTIC_CSTIC_LIST).isEmpty());
	}

	@Test
	public void testToJson()
	{
		final List<AnalyticCsticData> analyticCsticsList = new ArrayList<>();
		createAnalyticCstic(analyticCsticsList, "uiKey1");
		createAnalyticCstic(analyticCsticsList, "uiKey2");

		final JsonObject jsonObj = classUnderTest.toJson(analyticCsticsList).build();

		assertEquals(2, jsonObj.getJsonArray(AnalyticsController.JSON_NAME_ANALYTIC_CSTIC_LIST).size());
		assertTrue(jsonObj.containsKey(AnalyticsController.JSON_NAME_POPULARITY_IN_PERCENT));
	}

	@Test
	public void testToJsonKpiObj()
	{
		final JsonObject jsonObj = classUnderTest.toJson(AnalyticsController.JSON_NAME_POPULARITY_IN_PERCENT).build();

		assertEquals(AnalyticsController.JSON_VALUE_PLACE_HOLDER, jsonObj.getString(AnalyticsController.JSON_NAME_PLACE_HOLDER));
		assertEquals("_XX_ of customer chose this option", jsonObj.getString(AnalyticsController.JSON_NAME_MESSAGE_TEMPLATE));
	}

	@Test
	public void testToJsonAnalyticCsticEmptyValues()
	{
		final AnalyticCsticData analyticCstic = createAnalyticCstic(null, "uiKey");

		final JsonObject jsonObj = classUnderTest.toJson(analyticCstic).build();

		assertEquals("uiKey", jsonObj.getString(AnalyticsController.JSON_NAME_CSTIC_UI_KEY));
		assertTrue(jsonObj.getJsonArray(AnalyticsController.JSON_NAME_VALUE_LIST).isEmpty());
	}

	@Test
	public void testToJsonAnalyticCsticWithValues()
	{
		final AnalyticCsticData analyticCstic = createAnalyticCstic(null, "uiKey");
		createAnalyticValue(analyticCstic, "value1", 60.0);
		createAnalyticValue(analyticCstic, "value2", 40.0);

		final JsonObject jsonObj = classUnderTest.toJson(analyticCstic).build();

		assertEquals("uiKey", jsonObj.getString(AnalyticsController.JSON_NAME_CSTIC_UI_KEY));
		assertEquals(2, jsonObj.getJsonArray(AnalyticsController.JSON_NAME_VALUE_LIST).size());
	}

	@Test
	public void testToJsonAnalyticCsticValue_roundUp()
	{
		final AnalyticCsticValueData analyticValue = createAnalyticValue(null, "value1", 50.5);

		final JsonObject jsonObj = classUnderTest.toJson("value1", analyticValue).build();

		assertEquals("value1", jsonObj.getString(AnalyticsController.JSON_NAME_VALUE_NAME));
		assertEquals(51, jsonObj.getJsonNumber(AnalyticsController.JSON_NAME_POPULARITY_IN_PERCENT).longValue());
	}

	@Test
	public void testToJsonAnalyticCsticValue_roundDown()
	{
		final AnalyticCsticValueData analyticValue = createAnalyticValue(null, "value1", 50.4999);

		final JsonObject jsonObj = classUnderTest.toJson("value1", analyticValue).build();

		assertEquals("value1", jsonObj.getString(AnalyticsController.JSON_NAME_VALUE_NAME));
		assertEquals(50, jsonObj.getJsonNumber(AnalyticsController.JSON_NAME_POPULARITY_IN_PERCENT).longValue());
	}

	private AnalyticCsticValueData createAnalyticValue(final AnalyticCsticData analyticCstic, final String valueName,
			final double percentage)
	{
		final AnalyticCsticValueData analyticValue = new AnalyticCsticValueData();
		analyticValue.setPopularityPercentage(percentage);
		if (analyticCstic != null)
		{
			analyticCstic.getAnalyticValues().put(valueName, analyticValue);
		}
		return analyticValue;
	}

	protected AnalyticCsticData createAnalyticCstic(final List<AnalyticCsticData> analyticCsticsList, final String csticUiKey)
	{
		final AnalyticCsticData analyticCstic = new AnalyticCsticData();
		analyticCstic.setCsticUiKey(csticUiKey);
		analyticCstic.setAnalyticValues(new HashMap<>());
		if (analyticCsticsList != null)
		{
			analyticCsticsList.add(analyticCstic);
		}
		return analyticCstic;
	}

	protected JsonObject jsonToObject(final String json)
	{
		final JsonReader jsonReader = Json.createReader(new StringReader(json));
		final JsonObject object = jsonReader.readObject();
		jsonReader.close();
		return object;
	}
}
