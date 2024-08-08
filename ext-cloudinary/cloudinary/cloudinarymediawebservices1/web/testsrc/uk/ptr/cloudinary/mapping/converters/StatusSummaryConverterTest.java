/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercewebservicescommons.dto.order.StatusSummaryWsDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class StatusSummaryConverterTest
{
	private static final Integer NUMBER_OF_ISSUES = 5;
	private final List<StatusSummaryWsDTO> statusSummaryList = new ArrayList<>();
	private final Map<ProductInfoStatus, Integer> productInfoStatusMap = new HashMap<ProductInfoStatus, Integer>();
	private final StatusSummaryConverter classUnderTest = new StatusSummaryConverter();
	private final StatusSummaryWsDTO statusSummary = new StatusSummaryWsDTO();
	private final ProductInfoStatus statusError = ProductInfoStatus.ERROR;

	@Before
	public void initialize()
	{
		statusSummary.setStatus(statusError.toString());
		statusSummary.setNumberOfIssues(NUMBER_OF_ISSUES);
		statusSummaryList.add(statusSummary);
		productInfoStatusMap.put(statusError, 5);
	}

	@Test
	public void testConvertFrom()
	{
		final Map<ProductInfoStatus, Integer> productInfoMap = classUnderTest.convertFrom(statusSummaryList, null, null);
		assertEquals(productInfoStatusMap, productInfoMap);
	}

	@Test
	public void testConvertFromNull()
	{
		final Map<ProductInfoStatus, Integer> productInfoMap = classUnderTest.convertFrom(null, null, null);
		assertNotNull(productInfoMap);
		assertTrue(productInfoMap.isEmpty());
	}

	@Test
	public void testConvertTo()
	{
		final List<StatusSummaryWsDTO> statusSummaryMap = classUnderTest.convertTo(productInfoStatusMap, null, null);
		assertEquals(1, statusSummaryMap.size());
		assertEquals(NUMBER_OF_ISSUES, statusSummaryMap.get(0).getNumberOfIssues());
	}

	@Test
	public void testConvertToNull()
	{
		final List<StatusSummaryWsDTO> statusSummaryMap = classUnderTest.convertTo(null, null, null);
		assertNotNull(statusSummaryMap);
		assertTrue(statusSummaryMap.isEmpty());
	}

	@Test
	public void testConvertEntrytoWsDTO()
	{
		final StatusSummaryWsDTO statusSummaryFromMapEntry = classUnderTest
				.convertEntrytoWsDTO(productInfoStatusMap.entrySet().stream().findFirst().get());
		assertNotNull(statusSummaryFromMapEntry);
		assertEquals(NUMBER_OF_ISSUES, statusSummaryFromMapEntry.getNumberOfIssues());
		assertEquals(statusError.toString(), statusSummaryFromMapEntry.getStatus());
	}
}
