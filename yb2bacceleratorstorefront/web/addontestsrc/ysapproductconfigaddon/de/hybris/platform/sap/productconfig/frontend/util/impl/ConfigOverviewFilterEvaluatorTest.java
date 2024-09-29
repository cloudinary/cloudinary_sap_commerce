/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.frontend.FilterData;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


@UnitTest
public class ConfigOverviewFilterEvaluatorTest
{

	@Test
	public void testNoFilterSet()
	{
		final OverviewUiData overviewData = new OverviewUiData();
		overviewData.setCsticFilterList(getFilterDataList(false));
		overviewData.setGroupFilterList(getFilterDataList(false));

		assertFalse(ConfigOverviewFilterEvaluator.hasAppliedFilters(overviewData));
	}

	@Test
	public void testGroupFilterSet()
	{
		final OverviewUiData overviewData = new OverviewUiData();
		overviewData.setCsticFilterList(getFilterDataList(false));
		overviewData.setGroupFilterList(getFilterDataList(true));

		assertTrue(ConfigOverviewFilterEvaluator.hasAppliedFilters(overviewData));
	}

	@Test
	public void testCsticFilterSet()
	{
		final OverviewUiData overviewData = new OverviewUiData();
		overviewData.setCsticFilterList(getFilterDataList(true));
		overviewData.setGroupFilterList(getFilterDataList(false));

		assertTrue(ConfigOverviewFilterEvaluator.hasAppliedFilters(overviewData));
	}

	@Test
	public void testBothFilterTypesSet()
	{
		final OverviewUiData overviewData = new OverviewUiData();
		overviewData.setCsticFilterList(getFilterDataList(true));
		overviewData.setGroupFilterList(getFilterDataList(true));

		assertTrue(ConfigOverviewFilterEvaluator.hasAppliedFilters(overviewData));
	}

	private List<FilterData> getFilterDataList(final boolean selected)
	{
		final List<FilterData> filterList = new ArrayList<>();

		filterList.add(createFilterData("A", "A", selected));
		filterList.add(createFilterData("B", "B", false));
		filterList.add(createFilterData("C", "C", selected));

		return filterList;
	}

	private FilterData createFilterData(final String description, final String key, final boolean selected)
	{
		final FilterData filterData = new FilterData();

		filterData.setDescription(description);
		filterData.setKey(key);
		filterData.setSelected(selected);

		return filterData;
	}

}
