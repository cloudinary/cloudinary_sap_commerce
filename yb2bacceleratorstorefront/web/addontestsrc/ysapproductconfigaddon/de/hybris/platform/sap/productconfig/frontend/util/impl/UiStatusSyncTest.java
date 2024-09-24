/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.util.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.CPQActionType;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.UiCsticStatus;
import de.hybris.platform.sap.productconfig.frontend.UiCsticValueStatus;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiPromoMessageStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.controllers.AbstractProductConfigControllerTCBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UiStatusSyncTest extends AbstractProductConfigControllerTCBase
{
	@InjectMocks
	private UiStatusSync classUnderTest;
	private ConfigurationData requestData;
	private UiStatus uiStatus;
	private List<UiGroupData> uiGroups;
	private UiGroupData conflictHeader;
	private UiGroupData conflictGroup;
	private UiGroupData firstCsticGroup;
	private UiGroupData secondCsticGroup;

	@Before
	public void setup()
	{
		requestData = new ConfigurationData();
		uiStatus = new UiStatus();
		uiGroups = new ArrayList<>();
		conflictHeader = new UiGroupData();
		conflictHeader.setGroupType(GroupType.CONFLICT_HEADER);
		conflictHeader.setConfigurable(true);
		conflictGroup = new UiGroupData();
		conflictGroup.setConfigurable(true);
		conflictGroup.setGroupType(GroupType.CONFLICT);
		conflictHeader.setSubGroups(Arrays.asList(conflictGroup));
		uiGroups.add(conflictHeader);
		firstCsticGroup = new UiGroupData();
		firstCsticGroup.setGroupType(GroupType.CSTIC_GROUP);
		firstCsticGroup.setConfigurable(true);
		uiGroups.add(firstCsticGroup);
		secondCsticGroup = new UiGroupData();
		secondCsticGroup.setGroupType(GroupType.CSTIC_GROUP);
		secondCsticGroup.setConfigurable(true);
		uiGroups.add(secondCsticGroup);

	}

	@Test
	public void testStoreLastNoneConflictGroupId()
	{
		final UiStatus newUiState = new UiStatus();
		requestData.setGroupIdToDisplay("group123");
		classUnderTest.storeLastNoneConflictGroupId(newUiState, requestData);
		assertEquals("group123", newUiState.getLastNoneConflictGroupId());
	}

	@Test
	public void testStoreLastNoneConflictGroupId_conflictGroup()
	{
		uiStatus.setLastNoneConflictGroupId("group123");
		requestData.setGroupIdToDisplay("CONFLICT_group123");
		classUnderTest.storeLastNoneConflictGroupId(uiStatus, requestData);
		assertEquals("group123", uiStatus.getLastNoneConflictGroupId());
	}

	@Test
	public void tesApplyUiStausToConfiguration()
	{
		final ConfigurationData configData = createEmptyConfigData();

		List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData uiGroupData = createUiGroup("1", GroupStatusType.ERROR, true);
		final List<CsticData> cstics = new ArrayList<>();
		final CsticData cstic = new CsticData();
		cstic.setKey("ABC");
		cstic.setLongText("lorem ipsum");
		cstic.setShowFullLongText(false);
		cstics.add(cstic);
		uiGroupData.setCstics(cstics);
		groups.add(uiGroupData);

		groups.add(createUiGroup("2", GroupStatusType.FINISHED, GroupType.CSTIC_GROUP, true));
		groups.add(createUiGroup("3", GroupStatusType.FINISHED, GroupType.INSTANCE, true));
		configData.setGroups(groups);
		configData.setSingleLevel(false);
		configData.setCpqAction(CPQActionType.PREV_BTN);

		configData.setSpecificationTreeCollapsed(false);
		configData.setPriceSummaryCollapsed(true);

		uiStatus = UiStatusSyncTestHelper.createUIStatusWithCstic(cstics.get(0).getKey());
		uiStatus.setGroupIdToDisplay("3");

		classUnderTest.applyUiStatusToConfiguration(configData, uiStatus);

		groups = configData.getGroups();
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(0).isCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(1).isCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(2).isCollapsed()));

		assertEquals(Boolean.FALSE, Boolean.valueOf(configData.isPriceSummaryCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(configData.isSpecificationTreeCollapsed()));

		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(0).getCstics().get(0).isShowFullLongText()));
	}


	@Test
	public void tesApplyUiStausToConfigurationWithConflict()
	{
		final ConfigurationData configData = createEmptyConfigData();

		List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData uiConflictData = createUiConflictGroupsWOCstics(new String[]
		{ "Conflict1" });
		groups.add(uiConflictData);

		final UiGroupData uiGroupData = createUiGroup("1", GroupStatusType.ERROR, true);
		final List<CsticData> cstics = new ArrayList<>();
		final CsticData cstic = new CsticData();
		cstic.setKey("ABC");
		cstic.setLongText("lorem ipsum");
		cstic.setShowFullLongText(false);
		cstics.add(cstic);
		uiGroupData.setCstics(cstics);
		groups.add(uiGroupData);

		groups.add(createUiGroup("2", GroupStatusType.WARNING, true));
		groups.add(createUiGroup("3", GroupStatusType.DEFAULT, GroupType.CSTIC_GROUP, true));
		configData.setGroups(groups);
		configData.setCpqAction(CPQActionType.NAV_TO_CSTIC_IN_GROUP);
		configData.setSingleLevel(true);

		uiStatus = UiStatusSyncTestHelper.createUIStatusWithCstic(cstics.get(0).getKey());

		classUnderTest.applyUiStatusToConfiguration(configData, uiStatus);

		groups = configData.getGroups();
		assertEquals(GroupType.CONFLICT_HEADER, groups.get(0).getGroupType());
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(0).isCollapsed()));

		assertEquals(GroupType.CSTIC_GROUP, groups.get(1).getGroupType());
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(1).isCollapsed()));
		assertEquals(GroupStatusType.ERROR, groups.get(1).getGroupStatus());

		assertEquals(GroupType.CSTIC_GROUP, groups.get(2).getGroupType());
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(2).isCollapsed()));
		assertEquals(GroupStatusType.WARNING, groups.get(2).getGroupStatus());

		assertEquals(GroupType.CSTIC_GROUP, groups.get(3).getGroupType());
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(3).isCollapsed()));
		assertEquals(GroupStatusType.DEFAULT, groups.get(3).getGroupStatus());

		final UiGroupData uiGroupConflict = groups.get(0).getSubGroups().get(0);
		assertEquals(GroupType.CONFLICT, uiGroupConflict.getGroupType());
		assertEquals(Boolean.FALSE, Boolean.valueOf(uiGroupConflict.isCollapsed()));
	}


	@Test
	public void tesApplyUiStausToConfigurationAndCollapseGroup()
	{
		final ConfigurationData configData = createEmptyConfigData();
		List<UiGroupData> groups = new ArrayList<>();
		groups.add(createUiGroup("1", GroupStatusType.DEFAULT, GroupType.INSTANCE, true));
		groups.add(createUiGroup("1", GroupStatusType.DEFAULT, GroupType.CSTIC_GROUP, true));
		configData.setGroups(groups);
		configData.setSingleLevel(false);
		configData.setCpqAction(CPQActionType.PREV_BTN);
		configData.setSpecificationTreeCollapsed(false);
		configData.setPriceSummaryCollapsed(true);

		final List<UiGroupStatus> uiGroups = new ArrayList<>();

		final List<UiCsticStatus> csticsStatus = new ArrayList<>();
		final UiCsticStatus csticStatus = new UiCsticStatus();
		csticStatus.setShowFullLongText(true);
		csticsStatus.add(csticStatus);
		uiStatus.setGroups(uiGroups);
		uiStatus.setPriceSummaryCollapsed(false);
		uiStatus.setSpecificationTreeCollapsed(true);
		uiStatus.setGroupIdToDisplay(null);

		classUnderTest.applyUiStatusToConfiguration(configData, uiStatus);

		groups = configData.getGroups();
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(0).isCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(1).isCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(configData.isPriceSummaryCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(configData.isSpecificationTreeCollapsed()));
	}

	@Test
	public void testStatusInitialUiGroupStatus()
	{
		final ConfigurationData configData = createEmptyConfigData();

		List<UiGroupData> groups = new ArrayList<>();
		groups.add(createUiGroup("1", GroupStatusType.ERROR, true));
		groups.add(createUiGroup("2", GroupStatusType.WARNING, true));
		groups.add(createUiGroup("3", GroupStatusType.DEFAULT, true));
		configData.setGroups(groups);

		classUnderTest.setInitialStatus(configData);

		groups = configData.getGroups();
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(0).isCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(1).isCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(2).isCollapsed()));

		assertEquals(Boolean.FALSE, Boolean.valueOf(configData.isPriceSummaryCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(configData.isSpecificationTreeCollapsed()));

		assertEquals("1", configData.getGroupIdToDisplay());
	}

	@Test
	public void testStatusInitialStatus_picture()
	{
		final ConfigurationData configData = createEmptyConfigData();
		final List<UiGroupData> groups = new ArrayList<>();
		configData.setGroups(groups);
		groups.add(createUiGroup("3", GroupStatusType.DEFAULT, true));

		classUnderTest.setInitialStatus(configData);

		assertEquals(Boolean.TRUE, Boolean.valueOf(configData.isHideImageGallery()));
	}

	@Test
	public void testStatusInitialUiGroupStatus_collapsedInSpecTree()
	{
		final ConfigurationData configData = createEmptyConfigData();

		List<UiGroupData> groups = new ArrayList<>();
		groups.add(createUiGroup("1", GroupStatusType.ERROR, true));
		final List<UiGroupData> subGroups = new ArrayList<>();
		subGroups.add(createUiGroup("1.1", GroupStatusType.ERROR, true));
		subGroups.add(createUiGroup("1.2", GroupStatusType.WARNING, true));
		groups.get(0).setSubGroups(subGroups);
		groups.add(createUiGroup("2", GroupStatusType.DEFAULT, true));
		configData.setGroups(groups);

		classUnderTest.setInitialStatus(configData);

		groups = configData.getGroups();
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(0).isCollapsedInSpecificationTree()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(1).isCollapsedInSpecificationTree()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(0).getSubGroups().get(0).isCollapsedInSpecificationTree()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(0).getSubGroups().get(1).isCollapsedInSpecificationTree()));
	}

	@Test
	public void testStatusInitialUiGroupStatus_nonConfigurable()
	{
		final ConfigurationData configData = createEmptyConfigData();

		List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData csticGroup = createUiGroup("1", GroupStatusType.ERROR, true);
		csticGroup.setConfigurable(false);
		csticGroup.setCstics(createCsticsList());
		groups.add(csticGroup);
		groups.add(createUiGroup("2", GroupStatusType.WARNING, true));
		groups.add(createUiGroup("3", GroupStatusType.DEFAULT, true));
		configData.setGroups(groups);

		classUnderTest.setInitialStatus(configData);

		groups = configData.getGroups();
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(0).isCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(0).getCstics().get(0).isShowFullLongText()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(groups.get(1).isCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(groups.get(2).isCollapsed()));

		assertEquals(Boolean.FALSE, Boolean.valueOf(configData.isPriceSummaryCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(configData.isSpecificationTreeCollapsed()));
	}

	@Test
	public void testExtractUiStatusFromConfiguration_uiGroupsStatus()
	{
		final ConfigurationData configData = createEmptyConfigData();

		final List<UiGroupData> groups = new ArrayList<>();
		groups.add(createUiGroup("1", GroupStatusType.ERROR, true));
		groups.add(createUiGroup("2", GroupStatusType.WARNING, true));
		groups.add(createUiGroup("3", GroupStatusType.DEFAULT, false));
		configData.setGroups(groups);

		configData.setAutoExpand(true);
		configData.setFocusId("4");
		configData.setSpecificationTreeCollapsed(false);
		configData.setPriceSummaryCollapsed(true);

		final UiStatus uiStatus = classUnderTest.extractUiStatusFromConfiguration(configData);

		final List<UiGroupStatus> uiGroupsStatus = uiStatus.getGroups();

		assertEquals(Boolean.TRUE, Boolean.valueOf(uiGroupsStatus.get(0).isCollapsed()));
		assertEquals(Boolean.TRUE, Boolean.valueOf(uiGroupsStatus.get(1).isCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(uiGroupsStatus.get(2).isCollapsed()));

		assertEquals(Boolean.TRUE, Boolean.valueOf(uiStatus.isPriceSummaryCollapsed()));
		assertEquals(Boolean.FALSE, Boolean.valueOf(uiStatus.isSpecificationTreeCollapsed()));
		assertEquals(uiStatus.getFirstErrorCsticId(), configData.getFocusId());
	}

	@Test
	public void testExtractUiStatusFromConfiguration_uiCsticStatus()
	{
		final ConfigurationData configData = createEmptyConfigData();

		final UiGroupData uiGroup = createUiGroup("1", GroupStatusType.DEFAULT, true);
		final List<CsticData> cstics = createCsticsList();

		final CsticData cstic = cstics.get(0);
		cstic.setLongText("lorem ipsum");
		cstic.setShowFullLongText(true);
		uiGroup.setCstics(cstics);
		final List<UiGroupData> groups = new ArrayList<>();
		groups.add(uiGroup);
		configData.setGroups(groups);

		final UiStatus uiStatus = classUnderTest.extractUiStatusFromConfiguration(configData);
		final List<UiGroupStatus> uiGroupsStatus = uiStatus.getGroups();
		final List<UiCsticStatus> uiCsticStatus = uiGroupsStatus.get(0).getCstics();

		assertEquals(cstics.size(), uiCsticStatus.size());
	}

	@Test
	public void testExtractUiStatusFromConfiguration_conflictNumberStatus()
	{
		final ConfigurationData configData = createEmptyConfigData();
		List<UiGroupData> groups = createEmptyGroup();
		configData.setGroups(groups);

		UiStatus uiStatus = classUnderTest.extractUiStatusFromConfiguration(configData);
		int numberOfConflicts = uiStatus.getNumberOfConflictsToDisplay();

		assertEquals(0, numberOfConflicts);

		final String[] conflictsIds = new String[]
		{ "Conflict1", "Conflict2", "Conflict3" };
		final UiGroupData uiGroup = createUiConflictGroupsWOCstics(conflictsIds);
		final List<CsticData> cstics = createCsticsList();

		final CsticData cstic = cstics.get(0);
		cstic.setLongText("lorem ipsum");
		cstic.setShowFullLongText(true);
		uiGroup.setCstics(cstics);

		groups = new ArrayList<>();
		groups.add(uiGroup);
		configData.setGroups(groups);

		uiStatus = classUnderTest.extractUiStatusFromConfiguration(configData);
		numberOfConflicts = uiStatus.getNumberOfConflictsToDisplay();

		assertEquals(conflictsIds.length, numberOfConflicts);
	}


	@Test
	public void testUpdateUIStatusFromRequestNoChange()
	{
		final UiStatus oldUiSate = UiStatusSyncTestHelper.createUiStatusForSimpleTest();
		final ConfigurationData requestData = createConfigDataForSimpleTest(true, false, true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertNotNull("no new UI-State retruned", newUiSate);
		assertEquals("collapsed Group missing in new uiSate", 3, newUiSate.getGroups().size());
	}

	@Test
	public void testUpdateUIStatusFromRequestInconsistentGroupsNoException()
	{
		final UiStatus oldUiSate = UiStatusSyncTestHelper.createUiStatusForSimpleTest();
		oldUiSate.setGroups(Collections.EMPTY_LIST);
		final ConfigurationData requestData = createConfigDataForSimpleTest(true, false, true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertNotNull("no new UI-State retruned", newUiSate);
	}

	@Test
	public void testUpdateUIStatusFromRequestNullGroupsNoException()
	{
		final UiStatus oldUiSate = UiStatusSyncTestHelper.createUiStatusForSimpleTest();
		final ConfigurationData requestData = createConfigDataForSimpleTest(true, false, true);
		requestData.getGroups().add(new UiGroupData());

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertNotNull("no new UI-State retruned", newUiSate);
	}

	@Test
	public void testUpdateUIStatusFromRequestInconsistentCsticNoException()
	{
		final UiStatus oldUiSate = UiStatusSyncTestHelper.createUiStatusForSimpleTest();
		oldUiSate.getGroups().get(0).getCstics().get(0).setId("changed");
		final ConfigurationData requestData = createConfigDataForSimpleTest(true, false, true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertNotNull("no new UI-State retruned", newUiSate);
	}

	@Test
	public void testUpdateUIStatusFromRequestNoOldState()
	{
		final ConfigurationData requestData = createConfigDataForSimpleTest(true, true, true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, null, null);
		assertNotNull("no new UI-State retruned", newUiSate);
		assertEquals("collapsed Group missing in new uiSate", 3, newUiSate.getGroups().size());
	}

	@Test
	public void testUpdateUIStatusFromRequestBaseData()
	{
		final UiStatus oldUiSate = UiStatusSyncTestHelper.createUiStatusForSimpleTest();
		final ConfigurationData requestData = createConfigDataForSimpleTest(false, false, false);
		requestData.setPriceSummaryCollapsed(true);
		requestData.setSpecificationTreeCollapsed(false);
		requestData.setGroupIdToDisplay("group123");

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertTrue("isPriceSummaryCollapsed not mapped from old to new UI-State", newUiSate.isPriceSummaryCollapsed());
		assertFalse("isSpecificationTreeCollapsed not mapped from old to new UI-State", newUiSate.isSpecificationTreeCollapsed());
		assertEquals("id of last non conflict group not set", requestData.getGroupIdToDisplay(),
				newUiSate.getLastNoneConflictGroupId());
	}

	@Test
	public void testUpdateUIStatusFromRequestExpandCsticLongText()
	{
		final UiStatus oldUiSate = UiStatusSyncTestHelper.createUiStatusForSimpleTest();
		final ConfigurationData requestData = createConfigDataForSimpleTest(true, false, true);
		requestData.getGroups().get(0).getCstics().get(0).setShowFullLongText(true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertTrue("expand state of cstcic long text lost", newUiSate.getGroups().get(0).getCstics().get(0).isShowFullLongText());
	}

	@Test
	public void testUpdateUIStatusFromRequestGroupVisited()
	{
		final UiStatus oldUiSate = UiStatusSyncTestHelper.createUiStatusForSimpleTest();
		final ConfigurationData requestData = createConfigDataForSimpleTest(true, false, true);
		requestData.getGroups().get(0).setVisited(true);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertTrue("visited state lost", newUiSate.getGroups().get(0).isVisited());
	}

	@Test
	public void testUpdateUIStatusFromRequestGroupVisitedSticky()
	{
		final UiStatus oldUiSate = UiStatusSyncTestHelper.createUiStatusForSimpleTest();
		oldUiSate.getGroups().get(0).setVisited(true);
		final ConfigurationData requestData = createConfigDataForSimpleTest(true, false, true);
		requestData.getGroups().get(0).setVisited(false);

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		assertTrue("visited state lost", newUiSate.getGroups().get(0).isVisited());
	}

	@Test
	public void testUpdateShowFullLongTextinUIStatusGroupsTrue()
	{
		final UiStatus uiState = UiStatusSyncTestHelper.createUiStatus(5, false);
		final List<UiGroupStatus> uiStatusGroups = uiState.getGroups();

		classUnderTest.updateShowFullLongTextinUIStatusGroups("group_2_cstic_3", true, uiStatusGroups);

		final UiCsticStatus csticToCheck = uiStatusGroups.get(1).getCstics().get(2);
		assertTrue("The cstic '" + csticToCheck.getId() + "' should have 'TRUE' as a value of 'showFullLongText': ",
				csticToCheck.isShowFullLongText());
	}

	@Test
	public void testUpdateShowFullLongTextinUIStatusGroupsFalse()
	{
		final UiStatus uiState = UiStatusSyncTestHelper.createUiStatus(5, true);
		final List<UiGroupStatus> uiStatusGroups = uiState.getGroups();

		classUnderTest.updateShowFullLongTextinUIStatusGroups("group_3_cstic_1", false, uiStatusGroups);

		final UiCsticStatus csticToCheck = uiStatusGroups.get(2).getCstics().get(0);
		assertFalse("The cstic '" + csticToCheck.getId() + "' should have 'FALSE' as a value of 'showFullLongText': ",
				csticToCheck.isShowFullLongText());
	}

	@Test
	public void testReplaceNewLineForLog()
	{
		final String newString = null;
		final String result = classUnderTest.replaceNewLineForLog(newString);

		assertEquals(newString, result);
	}

	@Test
	public void testReplaceNewLineForLogStringWithoutNewLine()
	{
		final String strWithoutNewLine = "productCode";
		final String result = classUnderTest.replaceNewLineForLog(strWithoutNewLine);

		assertEquals(strWithoutNewLine, result);
	}

	@Test
	public void testReplaceNewLineForLogStringWithNewLine()
	{
		final String strWithoutNewLine = "productCode\nlog forging text";
		final String expectedString = "productCode_log forging text";

		final String result = classUnderTest.replaceNewLineForLog(strWithoutNewLine);

		assertEquals(expectedString, result);
	}

	@Test
	public void testDonotUpdateCsticUIStatusFromRequestData()
	{
		List<CsticData> requestCstics = Collections.EMPTY_LIST;
		List<UiCsticStatus> statusCstics = UiStatusSyncTestHelper.createUiCsticStatusListWithPromoMessages();

		classUnderTest.updateCsticUIStatusFromRequestData(statusCstics, requestCstics);
		assertTrue(requestCstics.isEmpty());

		requestCstics = UiStatusSyncTestHelper.createCsticsListWithPromoMessages();
		statusCstics = Collections.EMPTY_LIST;

		classUnderTest.updateCsticUIStatusFromRequestData(statusCstics, requestCstics);
		assertTrue(statusCstics.isEmpty());
	}

	@Test
	public void testUpdateCsticUIStatusFromRequestData()
	{
		final List<CsticData> requestCstics = UiStatusSyncTestHelper.createCsticsListWithPromoMessages();
		final List<UiCsticStatus> statusCstics = UiStatusSyncTestHelper.createUiCsticStatusListWithPromoMessages();

		classUnderTest.updateCsticUIStatusFromRequestData(statusCstics, requestCstics);

		for (int i = 0; i < requestCstics.size(); i++)
		{
			final CsticData csticData = requestCstics.get(i);
			final UiCsticStatus uiCsticStatus = statusCstics.get(i);

			assertEquals(csticData.getKey(), uiCsticStatus.getId());
			assertEquals(csticData.isShowFullLongText(), uiCsticStatus.isShowFullLongText());

			if (i == 1)
			{
				comparePromoMessages(csticData.getDomainvalues().get(0).getMessages(),
						uiCsticStatus.getCsticValuess().get(0).getPromoMessages());
			}
		}

		assertFalse(statusCstics.get(3).isShowFullLongText());
	}

	@Test
	public void testApplyUiStatusToCstic()
	{
		final List<CsticData> responseCstics = UiStatusSyncTestHelper.createCsticsListWithPromoMessages();
		final List<UiCsticStatus> statusCstics = UiStatusSyncTestHelper.createUiCsticStatusListWithPromoMessages();

		classUnderTest.applyUiStatusToData(statusCstics, responseCstics, //
				classUnderTest.checkCsticUiStatusMatch, //
				classUnderTest.applyUiStatusToCsticConsumer);

		for (int i = 0; i < responseCstics.size(); i++)
		{
			final CsticData csticData = responseCstics.get(i);
			final UiCsticStatus uiCsticStatus = statusCstics.get(i);

			assertEquals(csticData.getKey(), uiCsticStatus.getId());
			assertEquals(csticData.isShowFullLongText(), uiCsticStatus.isShowFullLongText());

			if (i == 1)
			{
				comparePromoMessages(csticData.getDomainvalues().get(0).getMessages(),
						uiCsticStatus.getCsticValuess().get(0).getPromoMessages());
			}
		}

		assertTrue(responseCstics.get(3).isShowFullLongText());
	}


	protected void comparePromoMessages(final List<ProductConfigMessageData> messages,
			final List<UiPromoMessageStatus> promoMessages)
	{
		assertNotNull(messages);
		assertNotNull(promoMessages);

		for (int i = 0; i < messages.size(); i++)
		{
			final ProductConfigMessageData message = messages.get(i);
			final UiPromoMessageStatus uiMessage = promoMessages.get(i);

			assertEquals(classUnderTest.getMessageId(message), uiMessage.getId());
			assertEquals(message.isShowExtendedMessage(), uiMessage.isShowExtendedMessage());
		}
	}


	@Test
	public void testExtractUiStatusFromCstic()
	{
		final List<CsticData> cstics = createCsticsList();
		cstics.get(0).setMessages(UiStatusSyncTestHelper.createListOfMessages());
		final List<UiCsticStatus> uiCsticsStatus = classUnderTest.extractUiStatusFromCstic(cstics);

		assertEquals(4, uiCsticsStatus.size());

		UiCsticStatus uiCsticStatus = uiCsticsStatus.get(1);
		assertNotNull(uiCsticStatus.getPromoMessages());
		assertEquals(0, uiCsticStatus.getPromoMessages().size());

		uiCsticStatus = uiCsticsStatus.get(0);
		assertNotNull(uiCsticStatus.getPromoMessages());
		assertEquals(2, uiCsticStatus.getPromoMessages().size());

		uiCsticStatus = uiCsticsStatus.get(1);
		assertNotNull(uiCsticStatus.getCsticValuess());
		assertEquals(2, uiCsticStatus.getCsticValuess().size());
	}

	@Test
	public void testExtractUiStatusFromCsticValue()
	{
		final List<CsticValueData> csticValues = UiStatusSyncTestHelper.createCsticValueList();
		final List<UiCsticValueStatus> uiCsticValuesStatus = classUnderTest.extractUiStatusFromCsticValue(csticValues);

		assertEquals(5, uiCsticValuesStatus.size());

		UiCsticValueStatus uiCsticValueStatus = uiCsticValuesStatus.get(1);
		assertNotNull(uiCsticValueStatus.getPromoMessages());
		assertEquals(0, uiCsticValueStatus.getPromoMessages().size());

		uiCsticValueStatus = uiCsticValuesStatus.get(2);
		assertNotNull(uiCsticValueStatus.getPromoMessages());
		assertEquals("value_2", uiCsticValueStatus.getId());
	}

	@Test
	public void testExtractUiStatusFromProductConfigMessage()
	{
		final List<ProductConfigMessageData> productConfigMessageDataList = UiStatusSyncTestHelper.createListOfMessages();
		final List<UiPromoMessageStatus> uiPromoMessageStatusList = classUnderTest
				.extractUiStatusFromMessages(productConfigMessageDataList);

		assertEquals(2, uiPromoMessageStatusList.size());

		final UiPromoMessageStatus uiMessageStatus1 = uiPromoMessageStatusList.get(0);
		assertEquals("ABCDEF", uiMessageStatus1.getId());
		assertTrue(uiMessageStatus1.isShowExtendedMessage());

		final UiPromoMessageStatus uiMessageStatus2 = uiPromoMessageStatusList.get(1);
		assertEquals("ABC", uiMessageStatus2.getId());
		assertFalse(uiMessageStatus2.isShowExtendedMessage());
	}

	@Test
	public void testUpdateUIStatusFromRequestExpandPromoMessage()
	{
		final UiStatus oldUiSate = UiStatusSyncTestHelper.createUiStatusForSimpleTest();
		oldUiSate.getGroups().get(0).getCstics().get(0).setPromoMessages(UiStatusSyncTestHelper.createUiPromoMessageStatuses());
		oldUiSate.getGroups().get(0).getCstics().get(0).setCsticValuess(UiStatusSyncTestHelper.createUiCsticValueStatusList());
		final ConfigurationData requestData = createConfigDataForSimpleTest(true, false, false);
		final CsticData cstic = requestData.getGroups().get(0).getCstics().get(0);
		cstic.setMessages(UiStatusSyncTestHelper.createListOfMessages());
		cstic.setDomainvalues(UiStatusSyncTestHelper.createCsticValueList());

		final UiStatus newUiSate = classUnderTest.updateUIStatusFromRequest(requestData, oldUiSate, null);
		// Promo Messages on Cstic level
		List<UiPromoMessageStatus> promoMessages = newUiSate.getGroups().get(0).getCstics().get(0).getPromoMessages();
		assertNotNull("Expeceted promo messages", promoMessages);
		assertEquals("Expecetd two messages, but got " + promoMessages.size(), 2, promoMessages.size());
		assertTrue("expand state of cstcic extended message lost", promoMessages.get(0).isShowExtendedMessage());
		assertFalse("expand state of cstcic extended message wrong set", promoMessages.get(1).isShowExtendedMessage());

		// Promo Messages on Domain Values level
		final List<UiCsticValueStatus> uiStatusCsticValue = newUiSate.getGroups().get(0).getCstics().get(0).getCsticValuess();
		assertNotNull(uiStatusCsticValue);
		assertEquals(5, uiStatusCsticValue.size());

		promoMessages = uiStatusCsticValue.get(0).getPromoMessages();
		assertNotNull("Expeceted promo messages", promoMessages);
		assertEquals("Expecetd two messages, but got " + promoMessages.size(), 2, promoMessages.size());
		assertTrue("expand state of cstcic extended message lost", promoMessages.get(0).isShowExtendedMessage());
		assertFalse("expand state of cstcic extended message wrong set", promoMessages.get(1).isShowExtendedMessage());
	}

	@Test
	public void testUpdateMessagesUiStatusFromRequestData()
	{
		final List<UiPromoMessageStatus> statusMessages = UiStatusSyncTestHelper.createUiPromoMessageStatuses();
		final List<ProductConfigMessageData> requestMessages = UiStatusSyncTestHelper.createListOfMessages();

		classUnderTest.updateMessagesUiStatusFromRequestData(statusMessages, Collections.emptyList());
		assertFalse(statusMessages.get(0).isShowExtendedMessage());

		classUnderTest.updateMessagesUiStatusFromRequestData(statusMessages, requestMessages);
		assertTrue(statusMessages.get(0).isShowExtendedMessage());
	}

	@Test
	public void testUpdateCsticValueUiStatusFromRequestData()
	{
		final List<UiCsticValueStatus> statusCsticValues = UiStatusSyncTestHelper.createUiCsticValueStatusList();
		final List<CsticValueData> requestCsticValues = UiStatusSyncTestHelper.createCsticValueList();

		classUnderTest.updateCsticValueUIStatusFromRequestData(statusCsticValues, null);
		assertFalse(statusCsticValues.get(0).getPromoMessages().get(0).isShowExtendedMessage());

		classUnderTest.updateCsticValueUIStatusFromRequestData(statusCsticValues, requestCsticValues);
		assertTrue(statusCsticValues.get(0).getPromoMessages().get(0).isShowExtendedMessage());
	}

	@Test
	public void testFindStatusMessage()
	{
		final List<UiPromoMessageStatus> statusMessages = UiStatusSyncTestHelper.createUiPromoMessageStatuses();

		Optional<UiPromoMessageStatus> statusMessage = classUnderTest.findStatusValue(statusMessages,
				msg -> "ABCDEF".equals(msg.getId()), 0);
		assertTrue(statusMessage.isPresent());
		assertEquals("ABCDEF", statusMessage.get().getId());

		statusMessage = classUnderTest.findStatusValue(statusMessages, msg -> "ABC".equals(msg.getId()), 0);
		assertTrue(statusMessage.isPresent());
		assertEquals("ABC", statusMessage.get().getId());

		statusMessage = classUnderTest.findStatusValue(statusMessages, msg -> "DOESNOTEXIST".equals(msg.getId()), 0);
		assertFalse(statusMessage.isPresent());

		statusMessage = classUnderTest.findStatusValue(statusMessages, msg -> "ABCDEF".equals(msg.getId()), 2);
		assertTrue(statusMessage.isPresent());
		assertEquals("ABCDEF", statusMessage.get().getId());
	}

	@Test
	public void testFindStatusCsticValue()
	{
		final List<UiCsticValueStatus> statusCsticValues = UiStatusSyncTestHelper.createUiCsticValueStatusList();

		Optional<UiCsticValueStatus> uiCsticValueStatus = classUnderTest.findStatusValue(statusCsticValues,
				value -> "value_1".equals(value.getId()), 0);
		assertTrue(uiCsticValueStatus.isPresent());
		assertEquals("value_1", uiCsticValueStatus.get().getId());

		uiCsticValueStatus = classUnderTest.findStatusValue(statusCsticValues, value -> "value_6".equals(value.getId()), 0);
		assertFalse(uiCsticValueStatus.isPresent());
	}

	@Test
	public void testUpdateNewUiStateFromOld()
	{
		final String lastNoneConflictGroupId = "noneConflictgroupId";
		final UiStatus oldUiStatus = new UiStatus();
		oldUiStatus.setLastNoneConflictGroupId(lastNoneConflictGroupId);

		final UiStatus newUiStatus = new UiStatus();

		classUnderTest.updateNewUiStateFromOld(oldUiStatus, newUiStatus);
		assertEquals(oldUiStatus.getLastNoneConflictGroupId(), newUiStatus.getLastNoneConflictGroupId());
	}


	@Override
	protected ConfigurationData createConfigDataForSimpleTest(final boolean includeG1, final boolean includeG2,
			final boolean includeG3)
	{
		final ConfigurationData requestData = createEmptyConfigData();
		final List<UiGroupData> groups = createCsticsGroup();
		if (includeG1)
		{
			groups.get(0).setId("1");
			groups.get(0).getCstics().get(0).setKey("cstic_1a");
		}
		else
		{
			groups.remove(0);
		}
		if (includeG2)
		{
			final UiGroupData uiGroup2 = createUiGroup("2", true);
			uiGroup2.setSubGroups(Collections.singletonList(createUiGroup("2.1", true)));
			groups.add(uiGroup2);
		}
		if (includeG3)
		{
			groups.add(createUiGroup("3", false));
		}
		requestData.setGroups(groups);
		requestData.setPriceSummaryCollapsed(false);
		requestData.setSpecificationTreeCollapsed(true);
		return requestData;
	}




	@Test
	public void testUiStatusGroupMatchesUiGroupNull()
	{
		final UiGroupData uiGroup = null;

		final UiGroupStatus uiStatusGroup = new UiGroupStatus();
		uiStatusGroup.setId(CONFIG_ID);

		assertFalse(classUnderTest.uiStatusGroupMatchesUiGroup(uiGroup, uiStatusGroup));
	}

	@Test
	public void testUiStatusGroupNullMatchesUiGroup()
	{
		final UiGroupData uiGroup = new UiGroupData();
		uiGroup.setId(CONFIG_ID);

		final UiGroupStatus uiStatusGroup = null;

		assertFalse(classUnderTest.uiStatusGroupMatchesUiGroup(uiGroup, uiStatusGroup));
	}

	@Test
	public void testUiStatusGroupMatchesUiGroupYes()
	{
		final UiGroupData uiGroup = new UiGroupData();
		uiGroup.setId(CONFIG_ID);

		final UiGroupStatus uiStatusGroup = new UiGroupStatus();
		uiStatusGroup.setId(CONFIG_ID);

		assertTrue(classUnderTest.uiStatusGroupMatchesUiGroup(uiGroup, uiStatusGroup));
	}

	@Test
	public void testUiStatusGroupMatchesUiGroupNo()
	{
		final UiGroupData uiGroup = new UiGroupData();
		uiGroup.setId("1");

		final UiGroupStatus uiStatusGroup = new UiGroupStatus();
		uiStatusGroup.setId(CONFIG_ID);

		assertFalse(classUnderTest.uiStatusGroupMatchesUiGroup(uiGroup, uiStatusGroup));
	}

	@Test
	public void testEmptyString()
	{
		assertFalse(classUnderTest.notNullAndNotEmpty(""));
	}

	@Test
	public void testNullString()
	{
		final String id = null;
		assertFalse(classUnderTest.notNullAndNotEmpty(id));
	}

	@Test
	public void testNotNullAndNotEmpty()
	{
		assertTrue(classUnderTest.notNullAndNotEmpty(CONFIG_ID));
	}

	@Test
	public void testHasCsticValuesFalse()
	{
		final CsticData cstic = new CsticData();
		cstic.setDomainvalues(Collections.EMPTY_LIST);
		assertFalse(classUnderTest.hasCsticValues(cstic));
	}

	@Test
	public void testHasCsticValuesTrue()
	{
		final CsticData cstic = new CsticData();
		final List<CsticValueData> domainValues = new ArrayList<>();
		final CsticValueData value = new CsticValueData();
		domainValues.add(value);
		cstic.setDomainvalues(domainValues);
		assertTrue(classUnderTest.hasCsticValues(cstic));
	}

	@Test
	public void testExpandGroupAndCollapseOther()
	{
		final String selectedGroup = "1-YSAP_SIMPLE_POC.Group3";
		configData = createConfigurationDataWith4Groups();

		classUnderTest.expandGroupAndCollapseOther(configData, selectedGroup);
		for (final UiGroupData uiGroup : configData.getGroups())
		{
			if (selectedGroup.equals(uiGroup.getId()))
			{
				assertFalse(uiGroup.isCollapsed());
			}
			else
			{
				assertTrue(uiGroup.isCollapsed());
			}
		}
	}

	@Test
	public void testIsNavigationAction()
	{
		assertTrue(classUnderTest.isNavigationAction(CPQActionType.NAV_TO_CSTIC_IN_CONFLICT));
		assertTrue(classUnderTest.isNavigationAction(CPQActionType.NAV_TO_CSTIC_IN_GROUP));
		assertTrue(classUnderTest.isNavigationAction(CPQActionType.PREV_BTN));
		assertTrue(classUnderTest.isNavigationAction(CPQActionType.NEXT_BTN));

		assertFalse(classUnderTest.isNavigationAction(CPQActionType.VALUE_CHANGED));
	}

	@Test
	public void testExpandGroupInSpecTreeAndExpandGroup()
	{
		String groupIdToDisplay = "2";
		final List<UiGroupStatus> uiGroups = new ArrayList<>();
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus(groupIdToDisplay, true));
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("3", false));

		uiStatus.setGroups(uiGroups);
		uiStatus.setGroupIdToDisplay(groupIdToDisplay);

		classUnderTest.expandGroupInSpecTreeAndExpandGroup(uiStatus);
		assertFalse(uiStatus.getGroups().get(0).isCollapsed());

		groupIdToDisplay = "4";
		uiStatus.setGroupIdToDisplay(groupIdToDisplay);
		classUnderTest.expandGroupInSpecTreeAndExpandGroup(uiStatus);
		for (final UiGroupStatus group : uiStatus.getGroups())
		{
			assertNotEquals(groupIdToDisplay, group.getId());
		}
	}

	@Test
	public void testApplyUiStatusToConfiguration()
	{
		final String selectedGroup = "1-YSAP_SIMPLE_POC.Group2";
		configData = createConfigurationDataWith4Groups();
		uiStatus.setPriceSummaryCollapsed(true);
		uiStatus.setSpecificationTreeCollapsed(true);
		uiStatus.setHideImageGallery(true);
		uiStatus.setQuantity(2L);
		final List<UiGroupStatus> uiGroups = new ArrayList<>();
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus(selectedGroup, true));
		uiStatus.setGroups(uiGroups);

		classUnderTest.applyUiStatusToConfiguration(configData, uiStatus, selectedGroup);

		assertTrue(configData.isPriceSummaryCollapsed());
		assertTrue(configData.isSpecificationTreeCollapsed());
		assertTrue(configData.isHideImageGallery());
		assertEquals(2L, configData.getQuantity());
	}

	@Test
	public void testToggleParentIfNeededWhenToggleGroupNull()
	{
		final UiGroupStatus parentGroup = new UiGroupStatus();
		final UiGroupStatus toggledGroup = null;
		assertFalse(classUnderTest.toggleParentIfNeeded(toggledGroup, parentGroup));
		assertFalse(parentGroup.isCollapsed());
	}

	@Test
	public void testToggleParentIfNeededWhenToggleGroupNotCollapsed()
	{
		final UiGroupStatus parentGroup = new UiGroupStatus();
		final UiGroupStatus toggledGroup = new UiGroupStatus();
		toggledGroup.setCollapsed(false);
		assertTrue(classUnderTest.toggleParentIfNeeded(toggledGroup, parentGroup));
		assertFalse(parentGroup.isCollapsed());
	}

	@Test
	public void testToggleParentIfNeededWhenToggleGroupCollapsed()
	{
		final UiGroupStatus parentGroup = new UiGroupStatus();
		final UiGroupStatus toggledGroup = new UiGroupStatus();
		toggledGroup.setCollapsed(true);
		assertFalse(classUnderTest.toggleParentIfNeeded(toggledGroup, parentGroup));
		assertFalse(parentGroup.isCollapsed());
	}

	@Test
	public void testToggleParentGroupInSpecTreeIfNeededWhenToggleGroupNull()
	{
		final UiGroupStatus parentGroup = new UiGroupStatus();
		parentGroup.setCollapsedInSpecificationTree(true);
		final UiGroupStatus toggledGroup = null;
		classUnderTest.toggleParentGroupInSpecTreeIfNeeded(toggledGroup, parentGroup);
		assertTrue(parentGroup.isCollapsedInSpecificationTree());
	}

	@Test
	public void testToggleParentGroupInSpecTreeIfNeededWhenToggleGroupCollapsedInSpecificationTreeFalse()
	{
		final UiGroupStatus parentGroup = new UiGroupStatus();
		parentGroup.setCollapsedInSpecificationTree(true);
		final UiGroupStatus toggledGroup = new UiGroupStatus();
		toggledGroup.setCollapsedInSpecificationTree(false);
		classUnderTest.toggleParentGroupInSpecTreeIfNeeded(toggledGroup, parentGroup);
		assertFalse(parentGroup.isCollapsedInSpecificationTree());
	}

	@Test
	public void testToggleParentGroupInSpecTreeIfNeededWhenToggleGroupCollapsedInSpecificationTreeTrue()
	{
		final UiGroupStatus parentGroup = new UiGroupStatus();
		parentGroup.setCollapsedInSpecificationTree(true);
		final UiGroupStatus toggledGroup = new UiGroupStatus();
		toggledGroup.setCollapsedInSpecificationTree(true);
		classUnderTest.toggleParentGroupInSpecTreeIfNeeded(toggledGroup, parentGroup);
		assertTrue(parentGroup.isCollapsedInSpecificationTree());
	}

	@Test
	public void testExpandGroupAndCollapseOtherGroups()
	{
		final String selectedGroup = "4";
		final List<UiGroupStatus> uiGroups = new ArrayList<>();
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("2", false));
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus(selectedGroup, false));

		classUnderTest.expandGroupAndCollapseOther(selectedGroup, uiGroups);
		assertTrue(uiGroups.get(0).isCollapsed());
		assertFalse(uiGroups.get(1).isCollapsed());
	}

	@Test
	public void testExtractUiStatusFromUiGroup()
	{
		final String groupIdToDisplay = "6";
		configData = createEmptyConfigData();
		configData.setSingleLevel(false);
		configData.setGroupIdToDisplay(groupIdToDisplay);
		final List<UiGroupStatus> uiGroupsStatus = new ArrayList<>();
		final List<UiGroupData> uiGroups = createUiGroups();

		classUnderTest.extractUiStatusFromUiGroup(uiGroups, uiGroupsStatus, configData);
		assertEquals(3, uiGroupsStatus.size());
	}

	@Test
	public void testExtractUiStatusFromUiGroupWithSingleLevelConfig()
	{
		final String groupIdToDisplay = "6";
		configData = createEmptyConfigData();
		configData.setSingleLevel(true);
		configData.setGroupIdToDisplay(groupIdToDisplay);
		final List<UiGroupStatus> uiGroupsStatus = new ArrayList<>();
		final List<UiGroupData> uiGroups = createUiGroups();

		classUnderTest.extractUiStatusFromUiGroup(uiGroups, uiGroupsStatus, configData);
		assertEquals(3, uiGroupsStatus.size());
	}

	protected List<UiGroupData> createUiGroups()
	{
		final List<UiGroupData> uiGroups = new ArrayList<>();
		final UiGroupData uiGroup1 = createUiGroup("2", GroupStatusType.FINISHED, GroupType.CSTIC_GROUP, false);
		uiGroup1.setVisited(true);
		uiGroup1.setCollapsed(true);
		uiGroups.add(uiGroup1);
		final UiGroupData uiGroup2 = createUiGroup("4", GroupStatusType.FLAG, GroupType.INSTANCE, true);
		uiGroup2.setVisited(false);
		uiGroup2.setCollapsed(false);
		uiGroups.add(uiGroup2);
		final UiGroupData uiGroup3 = createUiGroup("6", GroupStatusType.WARNING, false);
		uiGroup3.setVisited(false);
		uiGroup3.setCollapsed(true);
		uiGroups.add(uiGroup3);
		return uiGroups;
	}

	@Test
	public void testToggleGroup()
	{
		UiGroupStatus result = classUnderTest.toggleGroup("3", Collections.EMPTY_LIST, true);
		assertNull(result);

		final List<UiGroupStatus> uiGroups = new ArrayList<>();
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("1", false));
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("2", true));
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("3", false));

		result = classUnderTest.toggleGroup("3", uiGroups, false);
		assertNotNull(result);
		assertEquals("3", result.getId());
		assertTrue(result.isCollapsed());

		result = classUnderTest.toggleGroup("2", uiGroups, true);
		assertNotNull(result);
		assertEquals("2", result.getId());
		assertFalse(result.isCollapsed());

		final List<UiGroupStatus> subGroups = new ArrayList<>();
		subGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("4", false));
		subGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("5", true));
		subGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("6", false));
		uiGroups.get(1).setSubGroups(subGroups);

		result = classUnderTest.toggleGroup("5", uiGroups, false);
		assertNotNull(result);
		assertEquals("5", result.getId());
		assertFalse(result.isCollapsed());
	}

	@Test
	public void testToggleGroupInSpecTree()
	{

		UiGroupStatus result = classUnderTest.toggleGroupInSpecTree("3", Collections.EMPTY_LIST, false);
		assertNull(result);

		final List<UiGroupStatus> uiGroups = new ArrayList<>();
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("1", false));
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("2", true));
		uiGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("3", false));

		result = classUnderTest.toggleGroupInSpecTree("3", uiGroups, false);
		assertNotNull(result);
		assertEquals("3", result.getId());
		assertTrue(result.isCollapsedInSpecificationTree());

		uiGroups.get(1).setSubGroups(Collections.EMPTY_LIST);
		result = classUnderTest.toggleGroupInSpecTree("5", uiGroups, false);
		assertNull(result);

		final List<UiGroupStatus> subGroups = new ArrayList<>();
		subGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("4", false));
		subGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("5", true));
		subGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("6", false));
		uiGroups.get(1).setSubGroups(subGroups);

		result = classUnderTest.toggleGroupInSpecTree("5", uiGroups, true);
		assertNotNull(result);
		assertEquals("5", result.getId());
		assertFalse(result.isCollapsedInSpecificationTree());
	}

	@Test
	public void testGetMessgesForCsticValue()
	{
		List<UiCsticValueStatus> uiCsticValueStatuses = Collections.EMPTY_LIST;
		String csticValueKey = null;
		List<UiPromoMessageStatus> result = classUnderTest.getMessgesForCsticValue(csticValueKey, uiCsticValueStatuses);
		assertNotNull(result);
		assertTrue(result.isEmpty());

		uiCsticValueStatuses = UiStatusSyncTestHelper.createUiCsticValueStatusList();
		csticValueKey = "value_2";
		result = classUnderTest.getMessgesForCsticValue(csticValueKey, uiCsticValueStatuses);
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(2, result.size());
	}

	@Test
	public void testHandleCPQActionToggleGroup()
	{

		final String groupId = "1";
		requestData.setCpqAction(CPQActionType.TOGGLE_GROUP);
		requestData.setGroupToDisplay(UiStatusSyncTestHelper.createUiGroupForDisplayData(groupId, createUiGroup(groupId, true)));
		requestData.setGroupIdToToggle(groupId);
		uiStatus = UiStatusSyncTestHelper.createUiStatusForSimpleTest();


		classUnderTest.handleCPQAction(requestData, uiStatus, uiTrackingRecorder);
		verify(uiTrackingRecorder, times(1)).recordGroupInteraction(requestData, groupId, true);
		assertTrue(requestData.getGroupIdToToggle().isEmpty());
	}

	@Test
	public void testHandleCPQActionToggleGroupNotFornd()
	{

		final String groupId = "group_1";
		requestData.setCpqAction(CPQActionType.TOGGLE_GROUP);
		requestData.setGroupToDisplay(UiStatusSyncTestHelper.createUiGroupForDisplayData(groupId, createUiGroup(groupId, true)));
		requestData.setGroupIdToToggle(groupId);

		uiStatus = UiStatusSyncTestHelper.createUiStatusForSimpleTest();

		classUnderTest.handleCPQAction(requestData, uiStatus, uiTrackingRecorder);
		verify(uiTrackingRecorder, times(0)).recordGroupInteraction(requestData, groupId, true);
		assertTrue(requestData.getGroupIdToToggle().isEmpty());
	}


	@Test
	public void testHandleCPQActionMenuNavigationGroupNotFound()
	{

		final String groupId = "group_1";
		requestData.setCpqAction(CPQActionType.MENU_NAVIGATION);
		requestData.setGroupToDisplay(UiStatusSyncTestHelper.createUiGroupForDisplayData(groupId, createUiGroup(groupId, true)));
		requestData.setGroupIdToToggle(groupId);
		requestData.setGroupIdToToggleInSpecTree(groupId);

		uiStatus = UiStatusSyncTestHelper.createUiStatusForSimpleTest();

		classUnderTest.handleCPQAction(requestData, uiStatus, uiTrackingRecorder);
		assertTrue(requestData.getGroupIdToToggleInSpecTree().isEmpty());
		verify(uiTrackingRecorder, times(0)).recordGroupInteraction(requestData, requestData.getGroupIdToToggleInSpecTree(), false);
	}

	@Test
	public void testHandleCPQActionMenuNavigation()
	{

		final String groupId = "group_1";
		requestData.setCpqAction(CPQActionType.MENU_NAVIGATION);
		requestData.setGroupToDisplay(UiStatusSyncTestHelper.createUiGroupForDisplayData(groupId, createUiGroup(groupId, true)));
		requestData.setGroupIdToToggle(groupId);
		requestData.setGroupIdToToggleInSpecTree("1");

		uiStatus = UiStatusSyncTestHelper.createUiStatusForSimpleTest();

		classUnderTest.handleCPQAction(requestData, uiStatus, uiTrackingRecorder);
		assertTrue(requestData.getGroupIdToToggleInSpecTree().isEmpty());
		verify(uiTrackingRecorder, times(0)).recordGroupInteraction(requestData, requestData.getGroupIdToToggleInSpecTree(), false);
	}

	@Test
	public void testToggleShowExtendedMessageOnUIStatusGroupsEmptyUiStatusGroups()
	{

		final String csticKey = "cstic_1";
		final String csticValueKey = null;
		final String messageKey = "message1";

		final List<UiGroupStatus> uiStatusGroups = null;
		classUnderTest.toggleShowExtendedMessageOnUIStatusGroups(csticKey, csticValueKey, messageKey, uiStatusGroups);

	}

	@Test
	public void testToggleShowExtendedMessageOnUIStatusGroups()
	{

		final String csticKey = "cstic_1";
		final String csticValueKey = null;
		final String messageKey = "message1";

		final List<UiGroupStatus> uiStatusGroups = new ArrayList<>();
		uiStatusGroups.add(UiStatusSyncTestHelper.createUiGroupStatus("group_1", true));
		classUnderTest.toggleShowExtendedMessageOnUIStatusGroups(csticKey, csticValueKey, messageKey, uiStatusGroups);

	}

	@Test
	public void testToggleShowExtendedMessageOnStatusCsticsNotFound()
	{

		final String csticKey = "cstic1";
		final String csticValueKey = null;
		final String messageKey = "message1";

		final List<UiCsticStatus> uiStatusCstics = UiStatusSyncTestHelper.createSimpleUiCsticStatusListWithPromoMessages("cstic_1");
		assertFalse(uiStatusCstics.get(0).getPromoMessages().get(0).isShowExtendedMessage());
		assertFalse(uiStatusCstics.get(0).getPromoMessages().get(1).isShowExtendedMessage());
		classUnderTest.toggleShowExtendedMessageOnStatusCstics(csticKey, csticValueKey, messageKey, uiStatusCstics);
		assertFalse(uiStatusCstics.get(0).getPromoMessages().get(0).isShowExtendedMessage());
		assertFalse(uiStatusCstics.get(0).getPromoMessages().get(1).isShowExtendedMessage());
	}

	@Test
	public void testToggleShowExtendedMessageOnStatusCstics()
	{

		final String csticKey = "cstic_2";
		final String csticValueKey = null;
		final String messageKey = "ABC";

		final List<UiCsticStatus> uiStatusCstics = UiStatusSyncTestHelper.createUiCsticStatusListWithPromoMessages();
		assertEquals(csticKey, uiStatusCstics.get(2).getId());
		assertFalse(uiStatusCstics.get(2).getPromoMessages().get(1).isShowExtendedMessage());
		classUnderTest.toggleShowExtendedMessageOnStatusCstics(csticKey, csticValueKey, messageKey, uiStatusCstics);
		assertTrue(uiStatusCstics.get(2).getPromoMessages().get(1).isShowExtendedMessage());
	}

	@Test
	public void testToggleShowExtendedMessageOnStatusCsticsValueNotFound()
	{

		final String csticKey = "cstic_1";
		final String csticValueKey = "value_0";
		final String messageKey = "message_1";

		final List<UiCsticStatus> uiStatusCstics = UiStatusSyncTestHelper.createUiCsticStatusListWithPromoMessages();
		assertEquals(csticValueKey, uiStatusCstics.get(1).getCsticValuess().get(0).getId());
		assertFalse(uiStatusCstics.get(1).getCsticValuess().get(0).getPromoMessages().get(1).isShowExtendedMessage());
		classUnderTest.toggleShowExtendedMessageOnStatusCstics(csticKey, csticValueKey, messageKey, uiStatusCstics);
		assertFalse(uiStatusCstics.get(1).getCsticValuess().get(0).getPromoMessages().get(1).isShowExtendedMessage());
	}

	@Test
	public void testToggleShowExtendedMessageOnStatusCsticsValue()
	{

		final String csticKey = "cstic_1";
		final String csticValueKey = "value_0";
		final String messageKey = "ABC";

		final List<UiCsticStatus> uiStatusCstics = UiStatusSyncTestHelper.createUiCsticStatusListWithPromoMessages();
		assertEquals(csticValueKey, uiStatusCstics.get(1).getCsticValuess().get(0).getId());
		assertFalse(uiStatusCstics.get(1).getCsticValuess().get(0).getPromoMessages().get(1).isShowExtendedMessage());
		classUnderTest.toggleShowExtendedMessageOnStatusCstics(csticKey, csticValueKey, messageKey, uiStatusCstics);
		assertTrue(uiStatusCstics.get(1).getCsticValuess().get(0).getPromoMessages().get(1).isShowExtendedMessage());

	}

	@Test
	public void testSetInitialGroupStatus()
	{
		classUnderTest.setInitialGroupStatus(uiGroups, 0, false);
		assertFalse(conflictHeader.isCollapsed());
		assertFalse(firstCsticGroup.isCollapsed());
		assertTrue(secondCsticGroup.isCollapsed());
	}

	@Test
	public void testSetInitialGroupStatusNotConfigurable()
	{
		firstCsticGroup.setConfigurable(false);
		classUnderTest.setInitialGroupStatus(uiGroups, 0, false);
		assertTrue(firstCsticGroup.isCollapsed());
	}

	@Test
	public void testIsNonConflictGroup()
	{
		assertFalse(classUnderTest.isNonConflictGroup(conflictHeader));
		assertFalse(classUnderTest.isNonConflictGroup(conflictGroup));
		assertTrue(classUnderTest.isNonConflictGroup(firstCsticGroup));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsNonConflictGroupIllegalArgument()
	{
		classUnderTest.isNonConflictGroup(null);
	}

	@Test
	public void testSetInitialGroupStatusSubgroup()
	{
		final UiGroupData subgroup1OfFirstCsticGroup = new UiGroupData();
		subgroup1OfFirstCsticGroup.setGroupType(GroupType.CSTIC_GROUP);
		subgroup1OfFirstCsticGroup.setConfigurable(true);
		subgroup1OfFirstCsticGroup.setCollapsed(true);
		firstCsticGroup.setSubGroups(Arrays.asList(subgroup1OfFirstCsticGroup));
		classUnderTest.setInitialGroupStatus(uiGroups, 0, false);
		assertFalse(conflictHeader.isCollapsed());
		assertFalse(firstCsticGroup.isCollapsed());
		assertTrue(subgroup1OfFirstCsticGroup.isCollapsed());
		assertTrue(secondCsticGroup.isCollapsed());
	}

}





























