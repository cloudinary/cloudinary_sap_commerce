/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiGroupForDisplayData;
import de.hybris.platform.sap.productconfig.frontend.UiCsticStatus;
import de.hybris.platform.sap.productconfig.frontend.UiCsticValueStatus;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiPromoMessageStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UiStatusSyncTestHelper
{
	public static List<UiCsticStatus> createUiCsticStatusList(final List<String> csticKeys)
	{
		final List<UiCsticStatus> csticsStatus = new ArrayList<>();
		for(String csticKey: csticKeys)
		{
			csticsStatus.add(createUiCsticStatus(csticKey));
		}
		return csticsStatus;
	}

	public static UiCsticStatus createUiCsticStatus(final String csticKey)
	{
		final UiCsticStatus csticStatus = new UiCsticStatus();
		csticStatus.setId(csticKey);
		csticStatus.setShowFullLongText(true);
		return csticStatus;
	}

	public static UiGroupStatus createUiGroupStatus(final String id, final boolean collapsed)
	{
		final UiGroupStatus uiGroup = new UiGroupStatus();

		uiGroup.setId(id);
		uiGroup.setCollapsed(collapsed);
		uiGroup.setCollapsedInSpecificationTree(collapsed);

		return uiGroup;
	}

	public static UiGroupStatus createUiGroupStatusWithCstic(final String id, final boolean collapsed, final List<UiCsticStatus> csticStatusList)
	{
		final UiGroupStatus uiGroup = createUiGroupStatus(id, collapsed);
		uiGroup.setCstics(csticStatusList);
		return uiGroup;
	}

	public static UiStatus createUiStatusForSimpleTest()
	{
		final UiGroupStatus uiGroup1Status = createUiGroupStatus("1", false);
		final UiCsticStatus csticStatus = new UiCsticStatus();
		csticStatus.setId("cstic_1a");
		csticStatus.setShowFullLongText(false);
		uiGroup1Status.setCstics(Collections.singletonList(csticStatus));

		final List<UiGroupStatus> groupStatusList = new ArrayList<>();
		groupStatusList.add(uiGroup1Status);
		final UiGroupStatus uiGroup2Status = createUiGroupStatus("2", true);
		uiGroup2Status.setSubGroups(Collections.singletonList(createUiGroupStatus("2.1", true)));
		groupStatusList.add(uiGroup2Status);
		groupStatusList.add(createUiGroupStatus("3", false));

		final UiStatus status = new UiStatus();
		status.setGroups(groupStatusList);
		status.setPriceSummaryCollapsed(false);
		status.setSpecificationTreeCollapsed(true);
		return status;
	}

	public static UiStatus createUiStatus(final int numberOfGroups, final boolean showFullLongText)
	{
		final List<UiGroupStatus> groupStatusList = new ArrayList<>();
		boolean collapsed = false;
		for (int i = 0; i < numberOfGroups; i++)
		{
			final int index = i + 1;
			final UiGroupStatus uiGroup = UiStatusSyncTestHelper.createUiGroupStatus("group_" + index, collapsed);
			createCsticsForUiGroupStatus(uiGroup, showFullLongText);
			groupStatusList.add(uiGroup);
			collapsed = toggleCollapsed(collapsed);
		}

		final UiStatus oldUiSate = new UiStatus();
		oldUiSate.setGroups(groupStatusList);
		oldUiSate.setPriceSummaryCollapsed(false);
		oldUiSate.setSpecificationTreeCollapsed(true);
		return oldUiSate;
	}

	public static boolean toggleCollapsed(boolean collapsed)
	{
		return !collapsed;
	}

	public static void createCsticsForUiGroupStatus(final UiGroupStatus uiGroupStatus, final boolean showFullLongText)
	{
		final List<UiCsticStatus> csticsStatus = new ArrayList<>();
		for (int i = 0; i < 5; i++)
		{
			final int index = i + 1;
			final UiCsticStatus csticStatus = createUiCsticStatus(uiGroupStatus.getId() + "_cstic_" + index);

			csticStatus.setId(uiGroupStatus.getId() + "_cstic_" + index);
			csticStatus.setShowFullLongText(showFullLongText);
			csticsStatus.add(csticStatus);
		}
		uiGroupStatus.setCstics(csticsStatus);
	}

	public static UiStatus createUIStatusWithCstic(final String csticKey)
	{
		UiStatus uiStatus = new UiStatus();
		final List<UiGroupStatus> uiGroups = new ArrayList<>();
      List<String> csticList = new ArrayList<String>();
		csticList.add(csticKey);
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatusWithCstic("1", false, UiStatusSyncTestHelper.createUiCsticStatusList(csticList)));
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("2", true));
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("3", false));

		uiStatus.setGroups(uiGroups);
		uiStatus.setPriceSummaryCollapsed(false);
		uiStatus.setSpecificationTreeCollapsed(true);
		return uiStatus;
	}

	public static UiGroupForDisplayData createUiGroupForDisplayData(String groupId, UiGroupData groupData){
		UiGroupForDisplayData group = new UiGroupForDisplayData();
		group.setGroupIdPath(groupId);
		group.setGroup(groupData);
		return group;
	}

	public static List<CsticData> createCsticsListWithPromoMessages()
	{
		final List<CsticData> csticList = new ArrayList<>();

		for (int i = 0; i < 5; i++)
		{
			CsticData csticData = new CsticData();
			csticData.setKey("cstic_" + i);
			csticData.setShowFullLongText(false);

			if (i % 2 == 0)
			{
				csticData.setMessages(createListOfMessages());
			}

			if (i == 1)
			{
				csticData.setDomainvalues(createCsticValueList());
			}

			csticList.add(csticData);
		}

		return csticList;
	}

	public static List<UiCsticStatus> createUiCsticStatusListWithPromoMessages()
	{
		final List<UiCsticStatus> uiCsticStatusList = new ArrayList<>();

		for (int i = 0; i < 5; i++)
		{
			UiCsticStatus uiCsticStatus = new UiCsticStatus();
			uiCsticStatus.setId("cstic_" + i);
			uiCsticStatus.setShowFullLongText(i == 3);

			if (i % 2 == 0)
			{
				uiCsticStatus.setPromoMessages(createUiPromoMessageStatuses());
			}

			if (i == 1)
			{
				uiCsticStatus.setCsticValuess(createUiCsticValueStatusList());
			}

			uiCsticStatusList.add(uiCsticStatus);
		}

		return uiCsticStatusList;
	}

	public static List<UiCsticStatus> createSimpleUiCsticStatusListWithPromoMessages(String csticId)
	{
		final List<UiCsticStatus> uiCsticStatusList = new ArrayList<>();

			UiCsticStatus uiCsticStatus = new UiCsticStatus();
			uiCsticStatus.setId(csticId);
			uiCsticStatus.setShowFullLongText(true);
			uiCsticStatus.setPromoMessages(createUiPromoMessageStatuses());
			uiCsticStatus.setCsticValuess(createUiCsticValueStatusList());
			uiCsticStatusList.add(uiCsticStatus);
		return uiCsticStatusList;
	}

	public static List<ProductConfigMessageData> createListOfMessages()
	{
		List<ProductConfigMessageData> messages = new ArrayList<>();

		final ProductConfigMessageData message1 = new ProductConfigMessageData();
		message1.setMessage("ABC");
		message1.setExtendedMessage("DEF");
		message1.setShowExtendedMessage(true);
		messages.add(message1);

		final ProductConfigMessageData message2 = new ProductConfigMessageData();
		message2.setMessage("ABC");
		message2.setShowExtendedMessage(false);
		messages.add(message2);

		return messages;
	}

	public static List<CsticValueData> createCsticValueList()
	{
		final List<CsticValueData> csticValues = new ArrayList<>();

		for (int i = 0; i < 5; i++)
		{
			final CsticValueData csticValue = new CsticValueData();
			csticValue.setName("value_" + i);
			csticValues.add(csticValue);

			if (i % 2 == 0)
			{
				List<ProductConfigMessageData> messages = UiStatusSyncTestHelper.createListOfMessages();
				csticValue.setMessages(messages);
			}
		}

		return csticValues;
	}

	public static List<UiPromoMessageStatus> createUiPromoMessageStatuses()
	{
		List<UiPromoMessageStatus> statusMessages = new ArrayList<>();
		final UiPromoMessageStatus statusMessage1 = new UiPromoMessageStatus();
		statusMessage1.setShowExtendedMessage(false);
		statusMessage1.setId("ABCDEF");
		statusMessages.add(statusMessage1);

		final UiPromoMessageStatus statusMessage2 = new UiPromoMessageStatus();
		statusMessage2.setShowExtendedMessage(false);
		statusMessage2.setId("ABC");
		statusMessages.add(statusMessage2);
		return statusMessages;
	}

	public static List<UiCsticValueStatus> createUiCsticValueStatusList()
	{
		final List<UiCsticValueStatus> uiCsticValueStatuses = new ArrayList<>();

		for (int i = 0; i < 5; i++)
		{
			final UiCsticValueStatus csticValue = new UiCsticValueStatus();
			csticValue.setId("value_" + i);
			uiCsticValueStatuses.add(csticValue);

			if (i % 2 == 0)
			{
				List<UiPromoMessageStatus> messages = UiStatusSyncTestHelper.createUiPromoMessageStatuses();
				csticValue.setPromoMessages(messages);
			}
		}

		return uiCsticValueStatuses;
	}
}
