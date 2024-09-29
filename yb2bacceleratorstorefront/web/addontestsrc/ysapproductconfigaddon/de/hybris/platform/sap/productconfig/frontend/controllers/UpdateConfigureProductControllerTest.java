/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.sap.productconfig.facades.CPQActionType;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.FirstOrLastGroupType;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiGroupForDisplayData;
import de.hybris.platform.sap.productconfig.frontend.UiCsticStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStatusSync;
import de.hybris.platform.sap.productconfig.frontend.validator.ConflictError;
import de.hybris.platform.sap.productconfig.frontend.validator.MandatoryFieldError;
import de.hybris.platform.sap.productconfig.services.exceptions.ConfigurationNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateConfigureProductControllerTest extends AbstractProductConfigControllerTCBase
{
	@Mock
	protected BindingResult bindingResults;

	// will inject cmsPageService and pageTitleResolver, as well. For both no setter exists
	@InjectMocks
	private UpdateConfigureProductController classUnderTest;

	private UpdateDataHolder updateData;

	@Before
	public void setUp()
	{
		injectMocks(classUnderTest);

		kbKey = createKbKey();
		csticList = createCsticsList();
		configData = createConfigurationDataWithGeneralGroupOnly();
		updateData = new UpdateDataHolder();
		updateData.setUiStatus(new UiStatus());
		updateData.setConfigData(configData);
		updateData.getConfigData().setSingleLevel(true);
	}

	@Test
	public void testExecuteUpdateNoGroups()
	{
		final ConfigurationData configDataFromRequest = new ConfigurationData();
		configDataFromRequest.setGroups(null);
		updateData.setConfigData(configDataFromRequest);

		given(configFacade.getConfiguration(configDataFromRequest)).willReturn(configData);

		classUnderTest.executeUpdate(updateData);
		assertNotNull(updateData.getConfigData().getGroups());
	}

	@Test
	public void testConfigureProductForwardIsCorrect() throws Exception
	{
		initializeFirstCall();
		configData.setGroupIdToDisplay("_GEN");
		given(configFacade.getConfiguration(configData)).willReturn(configData);
		when(Boolean.valueOf(bindingResults.hasErrors())).thenReturn(Boolean.FALSE);


		final UiStatus uiStatus = uiStatusSync.extractUiStatusFromConfiguration(configData);
		given(configurationProductLinkStrategy.getConfigIdForProduct("YSAP_SIMPLE_POC")).willReturn(CONFIG_ID);

		request.setAttribute("de.hybris.platform.acceleratorcms.utils.SpringHelper.bean.requestContextData",
				new RequestContextData());
		classUnderTest.updateConfigureProduct(configData, bindingResults, model, request);

		verify(configFacade, times(1)).updateConfiguration(any(ConfigurationData.class));
	}

	@Test
	public void testConfigRemovedForwardToNewConfiguration() throws Exception
	{
		initializeFirstCall();
		configData.setGroupIdToDisplay("_GEN");

		request.setAttribute("de.hybris.platform.acceleratorcms.utils.SpringHelper.bean.requestContextData",
				new RequestContextData());
		classUnderTest.updateConfigureProduct(configData, bindingResults, model, request);

		verify(configFacade, times(0)).updateConfiguration(any(ConfigurationData.class));
	}

	@Test
	public void testUpdateConfigureWithErrors() throws Exception
	{
		initializeFirstCall();
		configData.setGroupIdToDisplay("_GEN");
		given(configFacade.getConfiguration(configData)).willReturn(configData);
		when(Boolean.valueOf(bindingResults.hasErrors())).thenReturn(Boolean.TRUE);

		final UiStatus uiStatus = uiStatusSync.extractUiStatusFromConfiguration(configData);
		given(configurationProductLinkStrategy.getConfigIdForProduct("YSAP_SIMPLE_POC")).willReturn(CONFIG_ID);

		request.setAttribute("de.hybris.platform.acceleratorcms.utils.SpringHelper.bean.requestContextData",
				new RequestContextData());
		classUnderTest.updateConfigureProduct(configData, bindingResults, model, request);

		verify(configFacade, times(1)).updateConfiguration(any(ConfigurationData.class));
	}

	@Test
	public void testHandleShowExtendedMessageFlagWithNullFocusId() throws Exception
	{
		initializeFirstCall();

		final UiStatusSync mockedUiSyncStatus = Mockito.mock(UiStatusSync.class);
		classUnderTest.setUiStatusSync(mockedUiSyncStatus);

		configData.setFocusId(null);
		classUnderTest.handleShowExtendedMessageFlag(CPQActionType.TOGGLE_EXTENDED_MESSAGE, updateData);
		verify(mockedUiSyncStatus, times(0)).toggleShowExtendedMessageOnUIStatusGroups(any(), any(), any(), any());
		reset(mockedUiSyncStatus);

		classUnderTest.setUiStatusSync(uiStatusSync);
	}

	@Test
	public void testHandleShowExtendedMessageFlagWithIncorrectToggleExtendedMessage() throws Exception
	{
		initializeFirstCall();

		final UiStatusSync mockedUiSyncStatus = Mockito.mock(UiStatusSync.class);
		classUnderTest.setUiStatusSync(mockedUiSyncStatus);

		configData.setFocusId("Test");
		classUnderTest.handleShowExtendedMessageFlag(CPQActionType.TOGGLE_EXTENDED_MESSAGE, updateData);
		verify(mockedUiSyncStatus, times(0)).toggleShowExtendedMessageOnUIStatusGroups(any(), any(), any(), any());
		reset(mockedUiSyncStatus);

		classUnderTest.setUiStatusSync(uiStatusSync);
	}

	@Test
	public void testHandleShowExtendedMessageFlagWithIncorrectlyDefinedToggleExtendedMessage() throws Exception
	{
		initializeFirstCall();

		final UiStatusSync mockedUiSyncStatus = Mockito.mock(UiStatusSync.class);
		classUnderTest.setUiStatusSync(mockedUiSyncStatus);

		configData.setFocusId(";;;");
		classUnderTest.handleShowExtendedMessageFlag(CPQActionType.TOGGLE_EXTENDED_MESSAGE, updateData);
		verify(mockedUiSyncStatus, times(0)).toggleShowExtendedMessageOnUIStatusGroups(any(), any(), any(), any());
		reset(mockedUiSyncStatus);

		classUnderTest.setUiStatusSync(uiStatusSync);
	}

	@Test
	public void testHandleShowExtendedMessageFlagWithoutMessage() throws Exception
	{
		initializeFirstCall();

		final UiStatusSync mockedUiSyncStatus = Mockito.mock(UiStatusSync.class);
		classUnderTest.setUiStatusSync(mockedUiSyncStatus);

		configData.setFocusId("id.linkLessDetails;cstic;;");
		classUnderTest.handleShowExtendedMessageFlag(CPQActionType.TOGGLE_GROUP, updateData);
		verify(mockedUiSyncStatus, times(0)).toggleShowExtendedMessageOnUIStatusGroups(any(), any(), any(), any());
		reset(mockedUiSyncStatus);

		classUnderTest.setUiStatusSync(uiStatusSync);
	}

	@Test
	public void testHandleShowExtendedMessageFlagWithDefinedMessageAtCsticLevel() throws Exception
	{
		initializeFirstCall();

		final UiStatusSync mockedUiSyncStatus = Mockito.mock(UiStatusSync.class);
		classUnderTest.setUiStatusSync(mockedUiSyncStatus);

		configData.setFocusId("id.linkLessDetails;cstic;;message");
		classUnderTest.handleShowExtendedMessageFlag(CPQActionType.TOGGLE_EXTENDED_MESSAGE, updateData);
		verify(mockedUiSyncStatus, times(1)).toggleShowExtendedMessageOnUIStatusGroups(any(), any(), any(), any());
		assertEquals("id.linkMoreDetails", updateData.getConfigData().getFocusId());
		reset(mockedUiSyncStatus);

		classUnderTest.setUiStatusSync(uiStatusSync);
	}

	@Test
	public void testHandleShowExtendedMessageFlagWithDefinedMessageAtCsticValueLevel() throws Exception
	{
		initializeFirstCall();

		final UiStatusSync mockedUiSyncStatus = Mockito.mock(UiStatusSync.class);
		classUnderTest.setUiStatusSync(mockedUiSyncStatus);

		configData.setFocusId("id.linkMoreDetails;cstic;value;message");
		classUnderTest.handleShowExtendedMessageFlag(CPQActionType.TOGGLE_EXTENDED_MESSAGE, updateData);
		verify(mockedUiSyncStatus, times(1)).toggleShowExtendedMessageOnUIStatusGroups(any(), any(), any(), any());
		assertEquals("id.linkLessDetails", updateData.getConfigData().getFocusId());
		reset(mockedUiSyncStatus);

		configData.setFocusId("conflict.id.linkMoreDetails;conflict.cstic;conflict.cstic.value;message");
		classUnderTest.handleShowExtendedMessageFlag(CPQActionType.TOGGLE_EXTENDED_MESSAGE, updateData);
		assertEquals("FocusIds do not equal: ", "conflict.id.linkLessDetails", updateData.getConfigData().getFocusId());
		verify(mockedUiSyncStatus, times(1)).toggleShowExtendedMessageOnUIStatusGroups(any(), any(), any(), any());
		reset(mockedUiSyncStatus);

		classUnderTest.setUiStatusSync(uiStatusSync);
	}

	@Test
	public void testHandleShowExtendedMessageFlagWithinConflictGroupWithoutMessage() throws Exception
	{
		initializeFirstCall();

		final UiStatusSync mockedUiSyncStatus = Mockito.mock(UiStatusSync.class);
		classUnderTest.setUiStatusSync(mockedUiSyncStatus);

		configData.setFocusId("conflict.id.linkLessDetails;conflict.cstic;;");
		classUnderTest.handleShowExtendedMessageFlag(CPQActionType.TOGGLE_EXTENDED_MESSAGE, updateData);
		verify(mockedUiSyncStatus, times(0)).toggleShowExtendedMessageOnUIStatusGroups(any(), any(), any(), any());
		reset(mockedUiSyncStatus);

		classUnderTest.setUiStatusSync(uiStatusSync);
	}

	@Test
	public void testHandleShowExtendedMessageFlagWithinConflictGroupAtCsticLevel() throws Exception
	{
		initializeFirstCall();

		final UiStatusSync mockedUiSyncStatus = Mockito.mock(UiStatusSync.class);
		classUnderTest.setUiStatusSync(mockedUiSyncStatus);

		configData.setFocusId("conflict.id.linkLessDetails;conflict.cstic;;message");
		classUnderTest.handleShowExtendedMessageFlag(CPQActionType.TOGGLE_EXTENDED_MESSAGE, updateData);
		assertEquals("FocusIds do not equal: ", "conflict.id.linkMoreDetails", updateData.getConfigData().getFocusId());
		verify(mockedUiSyncStatus, times(1)).toggleShowExtendedMessageOnUIStatusGroups(any(), any(), any(), any());
		reset(mockedUiSyncStatus);

		classUnderTest.setUiStatusSync(uiStatusSync);
	}

	@Test
	public void testHandleShowExtendedMessageFlagWithinConflictGroupAtCsticValueLevel() throws Exception
	{
		initializeFirstCall();

		final UiStatusSync mockedUiSyncStatus = Mockito.mock(UiStatusSync.class);
		classUnderTest.setUiStatusSync(mockedUiSyncStatus);


		configData.setFocusId("conflict.id.linkMoreDetails;conflict.cstic;conflict.cstic.value;message");
		classUnderTest.handleShowExtendedMessageFlag(CPQActionType.TOGGLE_EXTENDED_MESSAGE, updateData);
		assertEquals("FocusIds do not equal: ", "conflict.id.linkLessDetails", updateData.getConfigData().getFocusId());
		verify(mockedUiSyncStatus, times(1)).toggleShowExtendedMessageOnUIStatusGroups(any(), any(), any(), any());
		reset(mockedUiSyncStatus);

		classUnderTest.setUiStatusSync(uiStatusSync);
	}

	@Test
	public void testUpdateConfigurationWithRemovedDraft() throws Exception
	{
		initializeFirstCall();
		given(configurationProductLinkStrategy.getConfigIdForProduct("YSAP_SIMPLE_POC")).willReturn(CONFIG_ID);

		willThrow(new ConfigurationNotFoundException("Not found")).given(configFacade).updateConfiguration(configData);

		request.setAttribute("de.hybris.platform.acceleratorcms.utils.SpringHelper.bean.requestContextData",
				new RequestContextData());
		final ModelAndView view = classUnderTest.updateConfigureProduct(configData, bindingResults, model, request);

		verify(errorHandler, times(1)).handleErrorForAjaxRequest(request, model);
	}

	@Test
	public void testRemoveOutdatedValidationMultiLevelErrors()
	{
		updateData.getConfigData().setSingleLevel(false);
		final Map<String, FieldError> inputToRestore = new HashMap<>();
		inputToRestore.put("Group_1.CSTIC_3", new ConflictError(null, "group1.cstics.A", null, null, null));
		inputToRestore.put("Group_1.CSTIC_2", new MandatoryFieldError(null, "group1.cstics.B", null, null, null));
		inputToRestore.put("Group_1.CSTIC_1", new FieldError("Object", "group1.cstics.C", null));
		inputToRestore.put("Group_2.CSTIC_2", new FieldError("Object", "group2.cstics.C", null));

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setUserInputToRestore(inputToRestore);

		final UiGroupForDisplayData groupToDisplay = new UiGroupForDisplayData();
		groupToDisplay.setPath("Group_1");
		final UiGroupData uiGroup = new UiGroupData();
		uiGroup.setId("Group_1");

		final CsticData cstic = new CsticData();
		cstic.setKey("Group_1.CSTIC_1");
		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(cstic);
		uiGroup.setCstics(cstics);

		groupToDisplay.setGroup(uiGroup);
		configData.setGroupToDisplay(groupToDisplay);

		final UpdateDataHolder updateData = new UpdateDataHolder();
		updateData.setConfigData(configData);
		updateData.setUiStatus(uiStatus);

		final Map<String, FieldError> cleanedInputToRember = classUnderTest.removeOutdatedValidationErrors(updateData);
		assertNotNull(cleanedInputToRember);
		assertEquals(1, cleanedInputToRember.size());
	}

	@Test
	public void testRemoveOutdatedValidationErrors()
	{
		configData = createEmptyConfigData();
		configData.setGroups(createEmptyGroup());
		configData.setSingleLevel(true);

		final UpdateDataHolder updateData = new UpdateDataHolder();
		updateData.setConfigData(configData);

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setUserInputToRestore(Collections.EMPTY_MAP);
		updateData.setUiStatus(uiStatus);

		Map<String, FieldError> result = classUnderTest.removeOutdatedValidationErrors(updateData);
		assertNotNull(result);
		assertTrue(result.isEmpty());

		uiStatus.setUserInputToRestore(null);
		updateData.setUiStatus(uiStatus);

		result = classUnderTest.removeOutdatedValidationErrors(updateData);
		assertNull(result);
	}

	@Test
	public void testRemoveOutdatedValidationSingleLevelErrors()
	{
		final Map<String, FieldError> inputToRestore = new HashMap<>();
		inputToRestore.put("Group_1.CSTIC_3", new ConflictError(null, "group1.cstics.A", null, null, null));
		inputToRestore.put("Group_2.CSTIC_2", new MandatoryFieldError(null, "group1.cstics.B", null, null, null));
		inputToRestore.put("Group_2.CSTIC_1", new FieldError("Object", "group1.cstics.C", null));
		inputToRestore.put("Group_2.CSTIC_2", new FieldError("Object", "group2.cstics.F", null));
		inputToRestore.put("Group_3.CSTIC_3", new FieldError("Object", "group3.cstics.D", null));
		inputToRestore.put("Group_4.CSTIC_3", new FieldError("Object", "group4.cstics.E", null));
		inputToRestore.put("Group_0.CSTIC_3", new FieldError("Object", "group4.cstics.E", null));

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setUserInputToRestore(inputToRestore);

		configData.setSingleLevel(true);
		configData.setGroups(create4CsticGroups());
		final UpdateDataHolder updateData = new UpdateDataHolder();
		updateData.setConfigData(configData);
		updateData.setUiStatus(uiStatus);

		final Map<String, FieldError> cleanedInputToRember = classUnderTest.removeOutdatedValidationErrors(updateData);
		assertNotNull(cleanedInputToRember);
		assertEquals(4, cleanedInputToRember.size());
	}

	@Test
	public void testFindFirstGroupForCsticIdWithConflictHeader()
	{
		final String csticId = "root.WCEM_Conflict1";
		final List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData uiConflictData = createConflictGroups(csticId);
		groups.add(uiConflictData);
		configData = createEmptyConfigData();
		configData.setGroups(groups);
		final UiGroupData uiGroup = classUnderTest.findFirstGroupForCsticId(configData.getGroups(), csticId);

		assertNull(uiGroup);
	}

	@Test
	public void testFindFirstGroupForCsticIdSimple()
	{
		final String csticId = "root.WCEM_NUMERIC";
		final UiGroupData uiGroup = classUnderTest.findFirstGroupForCsticId(configData.getGroups(), csticId);

		assertNotNull(uiGroup);
		assertEquals(configData.getGroups().get(0), uiGroup);
	}

	@Test
	public void testFindFirstConflictGroupForCsticIdWithNullUiGroups()
	{
		final UiGroupData result = classUnderTest.findFirstConflictGroupForCsticId(null, "csticId");
		assertNull(result);
	}

	@Test
	public void testFindFirstConflictGroupForCsticIdWithNullUiGroup()
	{
		final List<UiGroupData> uiGroups = create4CsticGroups("simple.mock", "1");

		final UiGroupData result = classUnderTest.findFirstConflictGroupForCsticId(uiGroups, "csticId");
		assertNull(result);
	}

	@Test
	public void testFindFirstConflictGroupForCsticId()
	{
		final String csticId = "root.WCEM_Conflict2";
		final ConfigurationData configData = createEmptyConfigData();

		final List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData uiConflictData = createCsticsGroupWithConflicts(csticId);
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
		final UiGroupData group3 = createUiGroup("3", GroupStatusType.CONFLICT, true);
		group3.setCstics(groups.get(0).getSubGroups().get(1).getCstics());
		groups.add(group3);
		groups.add(createUiGroup("4", GroupStatusType.DEFAULT, true));

		final UiGroupData group5 = createUiGroup("5", GroupStatusType.CONFLICT, true);
		group5.setCstics(groups.get(0).getSubGroups().get(0).getCstics());
		groups.add(group5);

		configData.setGroups(groups);

		final UiGroupData uiGroup = classUnderTest.findFirstConflictGroupForCsticId(configData.getGroups(), csticId);

		assertNotNull(uiGroup);
		assertEquals(configData.getGroups().get(0).getSubGroups().get(1).getId(), uiGroup.getId());
	}

	@Test
	public void testNotFindFirstConflictGroupForCsticId()
	{
		final String csticId = "root.WCEM_Conflict2";
		final ConfigurationData configData = createEmptyConfigData();

		final List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData uiConflictData = createCsticsGroupWithConflicts(csticId);
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
		final UiGroupData group3 = createUiGroup("3", GroupStatusType.CONFLICT, true);
		group3.setCstics(groups.get(0).getSubGroups().get(1).getCstics());
		groups.add(group3);
		groups.add(createUiGroup("4", GroupStatusType.DEFAULT, true));

		final UiGroupData group5 = createUiGroup("5", GroupStatusType.CONFLICT, true);
		group5.setCstics(groups.get(0).getSubGroups().get(0).getCstics());
		groups.add(group5);

		configData.setGroups(groups);

		final UiGroupData uiGroup = classUnderTest.findFirstConflictGroupForCsticId(configData.getGroups(), "wrongCsticId");

		assertNull(uiGroup);
	}

	/**
	 * There are 2 conflicts with the same cstic, but only the first should be found.
	 */
	@Test
	public void testFindFirstConflictGroupForCsticId2()
	{
		final String csticId = "root.WCEM_Conflict2";
		final ConfigurationData configData = createEmptyConfigData();

		final List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData uiConflictData = createConflictGroups(csticId);
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
		final UiGroupData group3 = createUiGroup("3", GroupStatusType.CONFLICT, true);
		group3.setCstics(groups.get(0).getSubGroups().get(1).getCstics());
		groups.add(group3);
		groups.add(createUiGroup("4", GroupStatusType.DEFAULT, true));

		final UiGroupData group5 = createUiGroup("5", GroupStatusType.CONFLICT, true);
		group5.setCstics(groups.get(0).getSubGroups().get(0).getCstics());
		groups.add(group5);

		configData.setGroups(groups);

		final UiGroupData uiGroup = classUnderTest.findFirstConflictGroupForCsticId(configData.getGroups(), csticId);

		assertNotNull(uiGroup);
		assertEquals(configData.getGroups().get(0).getSubGroups().get(1).getId(), uiGroup.getId());
	}

	@Test
	public void testFindFirstGtroupForCsticIdComplexSingleLevel()
	{
		final String csticId = "subGroup.NUMERIC";

		final CsticData cstic = new CsticData();
		cstic.setKey(csticId);

		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(cstic);

		final ConfigurationData configData = createConfigurationDataMultiLevel();
		final UiGroupData uiGroupToSearch = configData.getGroups().get(1);
		uiGroupToSearch.setCstics(cstics);

		final UiGroupData uiGroup = classUnderTest.findFirstGroupForCsticId(configData.getGroups(), csticId);
		assertNotNull(uiGroup);
		assertEquals(uiGroupToSearch.getId(), uiGroup.getId());
	}

	@Test
	public void testFindFirstGroupForCsticIdWithinGroupsWhichDoesNotHaveAnyConflicts()
	{
		final List<UiGroupData> uiGroups = createCsticsGroup();
		final UiGroupData result = classUnderTest.findFirstConflictGroupForCsticId(uiGroups, "csticId");
		assertNull(result);
	}

	@Test
	public void testFindFirstGtroupForCsticIdComplexMultiLevel()
	{
		final String csticId = "subGroup.NUMERIC";

		final CsticData cstic = new CsticData();
		cstic.setKey(csticId);

		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(cstic);

		final ConfigurationData configData = createConfigurationDataMultiLevel();
		final UiGroupData uiGroupToSearch = configData.getGroups().get(4).getSubGroups().get(1);
		uiGroupToSearch.setCstics(cstics);

		final UiGroupData uiGroup = classUnderTest.findFirstGroupForCsticId(configData.getGroups(), csticId);
		assertNotNull(uiGroup);
		assertEquals(uiGroupToSearch.getId(), uiGroup.getId());
	}

	@Test
	public void testIsCsticPartOfGroup()
	{
		final String csticId = "root.WCEM_NUMERIC";
		final UiGroupData uiGroup = create4CsticGroups("root", "root").get(0);

		final boolean result = classUnderTest.isCsticPartOfGroup(uiGroup, csticId);
		assertTrue(result);
	}

	@Test
	public void testIsCsticPartOfGroupEmptyGroup()
	{
		final String csticId = "root.WCEM_NUMERIC";
		final UiGroupData uiGroup = new UiGroupData();

		final boolean result = classUnderTest.isCsticPartOfGroup(uiGroup, csticId);
		assertFalse(result);
	}

	@Test
	public void testNotIsCsticPartOfGroup()
	{
		final String csticId = "root.DOES_NOT_EXISTC";
		final UiGroupData uiGroup = create4CsticGroups("root", "root").get(0);

		final boolean result = classUnderTest.isCsticPartOfGroup(uiGroup, csticId);
		assertFalse(result);
	}

	@Test
	public void testHandleAutoExpand()
	{
		configData.setAutoExpand(false);
		classUnderTest.handleAutoExpand(updateData, configData);
		assertNull(configData.getFocusId());
		assertNull(configData.getGroupIdToDisplay());
	}

	@Test
	public void testHandleAutoExpand_noError()
	{
		configData.setAutoExpand(true);
		classUnderTest.handleAutoExpand(updateData, configData);
		assertNull(configData.getFocusId());
		assertFalse(configData.isAutoExpand());
		assertNull(configData.getGroupIdToDisplay());
	}

	@Test
	public void testHandleAutoExpand_Error()
	{
		configData.setAutoExpand(true);
		configData.getGroups().get(0).getCstics().get(3).setCsticStatus(CsticStatusType.ERROR);
		configData.getGroups().get(0).setGroupStatus(GroupStatusType.ERROR);
		classUnderTest.handleAutoExpand(updateData, configData);
		assertEquals("root.WCEM_NUMERIC", configData.getFocusId());
		assertEquals("_GEN", configData.getGroupIdToDisplay());
		assertEquals(configData.getGroups().get(0), configData.getGroupToDisplay().getGroup());
		assertTrue(configData.isAutoExpand());
	}

	@Test
	public void testHandleAutoExpand_Conflict()
	{
		configData.setAutoExpand(true);

		configData.getGroups().get(0).getCstics().get(3).setCsticStatus(CsticStatusType.CONFLICT);
		final List<CsticData> cstics = configData.getGroups().get(0).getCstics();

		final UiGroupData conflictGroup = createUiConflictGroup("CONFLICT");
		conflictGroup.getSubGroups().get(0).setGroupStatus(GroupStatusType.CONFLICT);
		conflictGroup.getSubGroups().get(0).setCstics(cstics);
		configData.getGroups().set(0, conflictGroup);

		classUnderTest.handleAutoExpand(updateData, configData);
		assertEquals("conflict.1.root.WCEM_NUMERIC", configData.getFocusId());
		assertEquals("10", configData.getGroupIdToDisplay());
		assertEquals(conflictGroup.getSubGroups().get(0), configData.getGroupToDisplay().getGroup());
		assertTrue(configData.isAutoExpand());
	}

	@Test
	public void testHandleAutoExpandForceExpandTrue()
	{
		configData = createEmptyConfigData();
		configData.setAutoExpand(true);
		configData.setForceExpand(true);

		final UiGroupForDisplayData groupToDisplay = new UiGroupForDisplayData();
		final UiGroupData group = new UiGroupData();
		group.setId("GENERAL");
		group.setCstics(Collections.EMPTY_LIST);
		groupToDisplay.setGroup(group);
		configData.setGroupToDisplay(groupToDisplay);

		classUnderTest.handleAutoExpand(updateData, configData);
		assertNull(configData.getFocusId());
	}

	@Test
	public void testHandleAutoExpandForceExpandTrueWithErrorCstic()
	{
		configData = createEmptyConfigData();
		configData.setAutoExpand(true);
		configData.setForceExpand(true);

		final UiGroupForDisplayData groupToDisplay = new UiGroupForDisplayData();
		groupToDisplay.setGroup(createGroupWith5Cstics());
		configData.setGroupToDisplay(groupToDisplay);

		classUnderTest.handleAutoExpand(updateData, configData);
		assertNotNull(configData.getFocusId());
		assertEquals("key_2", configData.getFocusId());
	}

	@Test
	public void testPreviousNextButtonNullCsticGroupList()
	{
		final ConfigurationData myConfigData = createEmptyConfigData();

		// Testing next
		// Current group is Group2
		myConfigData.setGroupIdToDisplay("1-" + KB_NAME + ".Group2");
		// Next button has been clicked
		myConfigData.setCpqAction(CPQActionType.NEXT_BTN);
		final CPQActionType action = myConfigData.getCpqAction();

		final UpdateDataHolder myUpdateData = new UpdateDataHolder();
		myUpdateData.setUiStatus(new UiStatus());
		myUpdateData.setConfigData(myConfigData);
		myConfigData.setAutoExpand(false);

		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		assertNull(myUpdateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testPreviousNextButtonNotFoundCurrentIndex()
	{
		final ConfigurationData myConfigData = createConfigurationDataWith4Groups();

		// Testing next
		// Current group is Group2
		myConfigData.setGroupIdToDisplay("1-" + KB_NAME + ".Group5");
		// Next button has been clicked
		myConfigData.setCpqAction(CPQActionType.NEXT_BTN);
		final CPQActionType action = myConfigData.getCpqAction();

		final UpdateDataHolder myUpdateData = new UpdateDataHolder();
		myUpdateData.setUiStatus(new UiStatus());
		myUpdateData.setConfigData(myConfigData);
		myConfigData.setAutoExpand(false);

		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		assertNull(myUpdateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testPreviousNextButtonClicked()
	{
		final ConfigurationData myConfigData = createConfigurationDataWith4Groups();

		// Testing next
		// Current group is Group2
		myConfigData.setGroupIdToDisplay("1-" + KB_NAME + ".Group2");
		// Next button has been clicked
		myConfigData.setCpqAction(CPQActionType.NEXT_BTN);
		CPQActionType action = myConfigData.getCpqAction();

		final UpdateDataHolder myUpdateData = new UpdateDataHolder();
		myUpdateData.setUiStatus(new UiStatus());
		myUpdateData.setConfigData(myConfigData);
		myConfigData.setAutoExpand(false);

		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		// identified group should be Group3
		final String identifiedGroupIdNext = "1-" + KB_NAME + ".Group3";
		assertEquals("Next group should be Group3 of instance 1", identifiedGroupIdNext,
				myUpdateData.getUiStatus().getGroupIdToDisplay());

		// Testing previous
		// Current group is now Group3
		myConfigData.setGroupIdToDisplay(myUpdateData.getUiStatus().getGroupIdToDisplay());
		// Previous button has been clicked
		myConfigData.setCpqAction(CPQActionType.PREV_BTN);
		action = myConfigData.getCpqAction();

		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		// identified group should be Group2
		final String identifiedGroupIdPrevious = "1-" + KB_NAME + ".Group2";
		assertEquals("Next group should be Group3 of instance 1", identifiedGroupIdPrevious,
				myUpdateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testPreviousNextButtonClickedMultiLevel()
	{
		// Remark: To understand the test-data see the comments in AbstractPorductConfigController.createConfigurationDataMultiLevel()
		final ConfigurationData myConfigData = createConfigurationDataMultiLevel();
		final UpdateDataHolder myUpdateData = new UpdateDataHolder();
		myUpdateData.setUiStatus(new UiStatus());
		myUpdateData.setConfigData(myConfigData);
		myConfigData.setAutoExpand(false);

		// Set current group to Group4 of instance 0.1.2 (6-SUBINST-0.1.2.Group4)
		myConfigData.setGroupIdToDisplay("6-SUBINST-0.1.2.Group4");
		// Test next: it should be Group1 of instance 0.2 (3-SUBINST-0.2.Group1)
		myConfigData.setCpqAction(CPQActionType.NEXT_BTN);
		CPQActionType action = myConfigData.getCpqAction();
		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		String identifiedGroupId = "3-SUBINST-0.2.Group1";
		assertEquals("Next group should be Group1 of instance 0.2 (3-SUBINST-0.2.Group1)", identifiedGroupId,
				myUpdateData.getUiStatus().getGroupIdToDisplay());

		// Set current group to Group1 of instance 0.2 (3-SUBINST-0.2.Group1)
		myConfigData.setGroupIdToDisplay(myUpdateData.getUiStatus().getGroupIdToDisplay());
		// Test previous: it should be Group4 of instance 0.1.2 (6-SUBINST-0.1.2.Group4)
		myConfigData.setCpqAction(CPQActionType.PREV_BTN);
		action = myConfigData.getCpqAction();
		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		identifiedGroupId = "6-SUBINST-0.1.2.Group4";
		assertEquals("Previous group should be Group4 of instance 0.1.2 (6-SUBINST-0.1.2.Group4)", identifiedGroupId,
				myUpdateData.getUiStatus().getGroupIdToDisplay());

		// Set current group to Group3 of instance 0.1 (2-SUBINST-0.1.Group3)
		myConfigData.setGroupIdToDisplay("2-SUBINST-0.1.Group3");
		// Test next: it should be Group4 of instance 0.1 (2-SUBINST-0.1.Group4)
		myConfigData.setCpqAction(CPQActionType.NEXT_BTN);
		action = myConfigData.getCpqAction();
		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		identifiedGroupId = "2-SUBINST-0.1.Group4";
		assertEquals("Next group should be Group4 of instance 0.1 (2-SUBINST-0.1.Group4)", identifiedGroupId,
				myUpdateData.getUiStatus().getGroupIdToDisplay());

		// Set current group to Group4 of instance 0.1 (2-SUBINST-0.1.Group4)
		myConfigData.setGroupIdToDisplay(myUpdateData.getUiStatus().getGroupIdToDisplay());
		// Test next: it should be Group1 of instance 0.1.2 (6-SUBINST-0.1.2.Group1)
		myConfigData.setCpqAction(CPQActionType.NEXT_BTN);
		action = myConfigData.getCpqAction();
		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		identifiedGroupId = "6-SUBINST-0.1.2.Group1";
		assertEquals("Next group should be Group1 of instance 0.1.2 (6-SUBINST-0.1.2.Group1)", identifiedGroupId,
				myUpdateData.getUiStatus().getGroupIdToDisplay());

		// Set current group to Group1 of instance 0.1.2 (6-SUBINST-0.1.2.Group1)
		myConfigData.setGroupIdToDisplay(myUpdateData.getUiStatus().getGroupIdToDisplay());
		// Test previous: it should be Group4 of instance 0.1 (2-SUBINST-0.1.Group4)
		myConfigData.setCpqAction(CPQActionType.PREV_BTN);
		action = myConfigData.getCpqAction();
		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		identifiedGroupId = "2-SUBINST-0.1.Group4";
		assertEquals("Previous group should be Group4 of instance 0.1 (2-SUBINST-0.1.Group4)", identifiedGroupId,
				myUpdateData.getUiStatus().getGroupIdToDisplay());

		// Should never happen: clicking next on last group (because button should be disabled)
		// In this case the groupIdToDisplay should stay the same
		// Set current group to Group4 of instance 0.2 (3-SUBINST-0.2.Group4)
		myConfigData.setGroupIdToDisplay("3-SUBINST-0.2.Group4");
		// Test next: it should stay the same group
		myConfigData.setCpqAction(CPQActionType.NEXT_BTN);
		action = myConfigData.getCpqAction();
		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		identifiedGroupId = "3-SUBINST-0.2.Group4";
		assertEquals("Clicking Next on last group: it should stay on the same group (3-SUBINST-0.2.Group4)", identifiedGroupId,
				myUpdateData.getUiStatus().getGroupIdToDisplay());

		// Should never happen: clicking previous on first group (because button should be disabled)
		// In this case the groupIdToDisplay should stay the same
		// Set current group to Group1 of root-instance 0 (1-YSAP_SIMPLE_POC.Group1)
		myConfigData.setGroupIdToDisplay("1-YSAP_SIMPLE_POC.Group1");
		// Test previous: it should stay the same group
		myConfigData.setCpqAction(CPQActionType.PREV_BTN);
		action = myConfigData.getCpqAction();
		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		identifiedGroupId = "1-YSAP_SIMPLE_POC.Group1";
		assertEquals("Clicking Previous on first group: it should stay on the same group (1-YSAP_SIMPLE_POC.Group1)",
				identifiedGroupId, myUpdateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testPreviousNextButtonClickedOnlyOneGroup()
	{
		final ConfigurationData myConfigData = createConfigurationDataWithGeneralGroupOnly();
		// Mark as ONLYONE
		myConfigData.getGroups().get(0).setFirstOrLastGroup(FirstOrLastGroupType.ONLYONE);

		final UpdateDataHolder myUpdateData = new UpdateDataHolder();
		myUpdateData.setUiStatus(new UiStatus());
		myUpdateData.setConfigData(myConfigData);
		myConfigData.setAutoExpand(false);

		// Should never happen: clicking next on only group (because button should be disabled)
		// In this case the groupIdToDisplay should stay the same

		// Set current group
		myConfigData.setGroupIdToDisplay("_GEN");
		// Test next: it should stay the same group
		myConfigData.setCpqAction(CPQActionType.NEXT_BTN);
		CPQActionType action = myConfigData.getCpqAction();
		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		final String identifiedGroupId = "_GEN";
		assertEquals("Clicking Next on only group: it should stay on the same group", identifiedGroupId,
				myUpdateData.getUiStatus().getGroupIdToDisplay());

		// Should never happen: clicking previous on only group (because button should be disabled)
		// In this case the groupIdToDisplay should stay the same
		// Test previous: it should stay the same group
		myConfigData.setCpqAction(CPQActionType.PREV_BTN);
		action = myConfigData.getCpqAction();
		classUnderTest.identifyPrevNextGroup(action, myUpdateData);
		assertEquals("Clicking Previous on only group: it should stay on the same group", identifiedGroupId,
				myUpdateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testPrepareGroupIdToDisplayWhenSolvingConflicts_NoConflictGroup_SV()
	{
		final CPQActionType action = CPQActionType.VALUE_CHANGED;

		updateData.getUiStatus().setGroupIdToDisplay("GroupId");
		classUnderTest.handleGroupIdToDisplayWhenSolvingConflicts(action, updateData);
		assertEquals("GroupId", updateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testPrepareGroupIdToDisplayWhenSolvingConflicts_ConflictGroup_ConflictResolved_SV()
	{
		final CPQActionType action = CPQActionType.VALUE_CHANGED;

		updateData.getUiStatus().setGroupIdToDisplay("CONFLICTGroupId");
		classUnderTest.handleGroupIdToDisplayWhenSolvingConflicts(action, updateData);
		assertEquals("_GEN", updateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testPrepareGroupIdToDisplayWhenSolvingConflicts_ConflictGroup_ConflictResolved_MV()
	{
		final CPQActionType action = CPQActionType.VALUE_CHANGED;

		updateData.getUiStatus().setGroupIdToDisplay("CONFLICTGroupId");
		updateData.getConfigData().setSingleLevel(false);
		final String lastNoneConflictGroupId = "lastNonConflicting";
		updateData.getUiStatus().setLastNoneConflictGroupId(lastNoneConflictGroupId);
		classUnderTest.handleGroupIdToDisplayWhenSolvingConflicts(action, updateData);
		assertEquals(lastNoneConflictGroupId, updateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testPrepareGroupIdToDisplayWhenSolvingConflicts_ConflictGroup_ConflictNotResolved_MV()
	{
		final CPQActionType action = CPQActionType.VALUE_CHANGED;
		updateData.getUiStatus().setGroupIdToDisplay("CONFLICTGroupId");
		updateData.getConfigData().setSingleLevel(false);
		final String conflictGroupId = SapproductconfigfrontendWebConstants.CONFLICT_PREFIX + "groupId";
		updateData.getConfigData().getGroups().get(0).setId(conflictGroupId);
		final String lastNoneConflictGroupId = "lastNonConflicting";
		updateData.getUiStatus().setLastNoneConflictGroupId(lastNoneConflictGroupId);
		classUnderTest.handleGroupIdToDisplayWhenSolvingConflicts(action, updateData);
		assertEquals(conflictGroupId, updateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testDonotPrepareGroupIdToDisplayWhenSolvingConflicts()
	{
		CPQActionType action = CPQActionType.MENU_NAVIGATION;
		updateData.getUiStatus().setGroupIdToDisplay("COFLICT_CART_TYPE");
		classUnderTest.handleGroupIdToDisplayWhenSolvingConflicts(action, updateData);
		assertNull("groupIdToDisplay is 'CONFLICT_CART_TYPE': ", updateData.getConfigData().getGroupIdToDisplay());

		action = CPQActionType.VALUE_CHANGED;
		updateData.getUiStatus().setGroupIdToDisplay("CONFLICT_GroupId");
		classUnderTest.handleGroupIdToDisplayWhenSolvingConflicts(action, updateData);
		assertEquals("_GEN", updateData.getUiStatus().getGroupIdToDisplay());

		action = CPQActionType.SHOW_FULL_LONG_TEXT;
		updateData.getUiStatus().setGroupIdToDisplay("WCEM_USED_SCENARIOS");
		classUnderTest.handleGroupIdToDisplayWhenSolvingConflicts(action, updateData);
		assertNull("groupIdToDisplay is 'WCEM_USED_SCENARIOS': ", updateData.getConfigData().getGroupIdToDisplay());

		action = CPQActionType.HIDE_FULL_LONG_TEXT;
		updateData.getUiStatus().setGroupIdToDisplay("CONFLICT_WCEM_RELEASE");
		classUnderTest.handleGroupIdToDisplayWhenSolvingConflicts(action, updateData);
		assertNull("groupIdToDisplay is 'CONFLICT_WCEM_RELEASE': ", updateData.getConfigData().getGroupIdToDisplay());
	}

	@Test
	public void testIsFirstErrorCurrentFocusCsticTrue()
	{
		final String focusId = "errorCstic";
		final String firstErrorCsticId = "errorCstic";
		assertTrue(classUnderTest.isFirstErrorCurrentFocusCstic(firstErrorCsticId, focusId));
	}

	@Test
	public void testIsFirstErrorCurrentFocusCsticFalse()
	{
		final String focusId = "notErrorCstic";
		final String firstErrorCsticId = "errorCstic";
		assertFalse(classUnderTest.isFirstErrorCurrentFocusCstic(firstErrorCsticId, focusId));
	}

	@Test
	public void testIsFirstErrorCurrentFocusCsticTrueConflict()
	{
		final String focusId = "errorCstic";
		final String firstErrorCsticId = "conflict.errorCstic";
		assertTrue(classUnderTest.isFirstErrorCurrentFocusCstic(firstErrorCsticId, focusId));
	}

	@Test
	public void testIsFirstErrorCurrentFocusCsticFalseConflict()
	{
		final String focusId = "notErrorCstic";
		final String firstErrorCsticId = "conflict.errorCstic";
		assertFalse(classUnderTest.isFirstErrorCurrentFocusCstic(firstErrorCsticId, focusId));
	}

	@Test
	public void testCheckAutoExpandModeOnValueChange_noErrorCstic()
	{
		configData.setAutoExpand(false);
		configData.setFocusId(CSTIC_PATH);
		classUnderTest.checkAutoExpandModeOnValueChange(configData, null);
		assertFalse(configData.isAutoExpand());
	}

	@Test
	public void testCheckAutoExpandModeOnValueChange_stayInAutoExpand()
	{
		createDataForAutoExpandTest();
		updateData.setConfigData(configData);
		configData.setFocusId("groups[0].cstics[0]");
		classUnderTest.checkAutoExpandModeOnValueChange(configData, "conflict.root.WCEM_STRING_SIMPLE");
		assertTrue(configData.isAutoExpand());
	}

	@Test
	public void testCheckAutoExpandModeOnValueChange_leaveAutoExpand()
	{
		createDataForAutoExpandTest();
		updateData.setConfigData(configData);
		configData.setFocusId("groups[0].cstics[1]");
		classUnderTest.checkAutoExpandModeOnValueChange(configData, "csticWithoutError");
		assertFalse(configData.isAutoExpand());
	}

	@Test
	public void testCheckAutoExpandMode_valueChangeAction()
	{
		createDataForAutoExpandTest();
		updateData.setConfigData(configData);
		configData.setFocusId("groups[0].cstics[0]");
		updateData.getUiStatus().setFirstErrorCsticId("csticWithError");
		classUnderTest.checkAutoExpandMode(CPQActionType.VALUE_CHANGED, updateData);

		assertNull("Focus Id should be resetted", configData.getFocusId());
		assertNull("Error Id should be resetted", updateData.getUiStatus().getFirstErrorCsticId());
	}

	@Test
	public void testCheckAutoExpandMode_retractAction()
	{
		createDataForAutoExpandTest();
		updateData.setConfigData(configData);
		configData.setFocusId("groups[0].cstics[1]");
		updateData.getUiStatus().setFirstErrorCsticId("csticWithError");
		classUnderTest.checkAutoExpandMode(CPQActionType.RETRACT_VALUE, updateData);

		assertNull("Focus Id should be resetted", configData.getFocusId());
		assertNull("Error Id should be resetted", updateData.getUiStatus().getFirstErrorCsticId());
	}

	@Test
	public void testCheckAutoExpandMode_otherActionQuitsAutoExpand()
	{
		createDataForAutoExpandTest();
		updateData.setConfigData(configData);
		configData.setFocusId("groups[0].cstics[1]");
		updateData.getUiStatus().setFirstErrorCsticId("csticWithError");
		classUnderTest.checkAutoExpandMode(CPQActionType.SHOW_FULL_LONG_TEXT, updateData);

		assertFalse(configData.isAutoExpand());
		assertNotNull("Focus Id should NOT be resetted", configData.getFocusId());
		assertNull("Error Id should be resetted", updateData.getUiStatus().getFirstErrorCsticId());
	}

	@Test
	public void testHandleToggleImageGallery_show()
	{
		updateData.getUiStatus().setHideImageGallery(true);
		classUnderTest.handleToggleImageGallery(CPQActionType.TOGGLE_IMAGE_GALLERY, updateData);
		assertFalse(updateData.getUiStatus().isHideImageGallery());
	}

	@Test
	public void testHandleToggleImageGallery_hide()
	{
		updateData.getUiStatus().setHideImageGallery(false);
		classUnderTest.handleToggleImageGallery(CPQActionType.TOGGLE_IMAGE_GALLERY, updateData);
		assertTrue(updateData.getUiStatus().isHideImageGallery());
	}

	@Test
	public void testHandleShowFullLongTextFlagWithWrongAction()
	{
		createDataForHideExpandLongTextTest();
		updateData.setConfigData(configData);
		updateData.setUiStatus(uiStatusSync.extractUiStatusFromConfiguration(configData));
		configData.setFocusId("longTextHidden");

		classUnderTest.handleShowFullLongTextFlag(CPQActionType.MENU_NAVIGATION, updateData);

		final List<UiCsticStatus> uiStatusCstics = updateData.getUiStatus().getGroups().get(0).getCstics();
		assertTrue(uiStatusCstics.get(0).getId() + ": long text NOT shown, but expected",
				uiStatusCstics.get(0).isShowFullLongText());
		assertFalse(uiStatusCstics.get(1).getId() + ": long text shown, but NOT expected",
				uiStatusCstics.get(1).isShowFullLongText());
	}

	@Test
	public void testHandleShowFullLongTextFlag_showLongText()
	{
		createDataForHideExpandLongTextTest();
		updateData.setConfigData(configData);
		updateData.setUiStatus(uiStatusSync.extractUiStatusFromConfiguration(configData));
		configData.setFocusId("longTextHidden");

		classUnderTest.handleShowFullLongTextFlag(CPQActionType.SHOW_FULL_LONG_TEXT, updateData);

		assertNull("Focus Id should be resetted", configData.getFocusId());
		final List<UiCsticStatus> uiStatusCstics = updateData.getUiStatus().getGroups().get(0).getCstics();
		assertTrue(uiStatusCstics.get(0).getId() + ": long text NOT shown, but expected",
				uiStatusCstics.get(0).isShowFullLongText());
		assertFalse(uiStatusCstics.get(1).getId() + ": long text shown, but NOT expected",
				uiStatusCstics.get(1).isShowFullLongText());
	}

	@Test
	public void testHandleShowFullLongTextFlag_hideLongText()
	{
		createDataForHideExpandLongTextTest();
		updateData.setConfigData(configData);
		updateData.setUiStatus(uiStatusSync.extractUiStatusFromConfiguration(configData));

		configData.setFocusId("longTextShown");
		classUnderTest.handleShowFullLongTextFlag(CPQActionType.HIDE_FULL_LONG_TEXT, updateData);

		assertNull("Focus Id should be resetted", configData.getFocusId());
		final List<UiCsticStatus> uiStatusCstics = updateData.getUiStatus().getGroups().get(0).getCstics();
		assertTrue(uiStatusCstics.get(1).getId() + ": long text NOT shown, but expected",
				uiStatusCstics.get(0).isShowFullLongText());
		assertFalse(uiStatusCstics.get(0).getId() + ": long text shown, but NOT expected",
				uiStatusCstics.get(1).isShowFullLongText());
	}

	@Test
	public void testHandleGroupIdToDisplayForNavigationLinksNoFocus()
	{
		classUnderTest.handleGroupIdToDisplayForNavigationLinks(CPQActionType.NAV_TO_CSTIC_IN_GROUP, updateData);
		assertNull(updateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testHandleGroupIdToDisplayForNavigationLinksFocusSetNavInGroup()
	{
		final String focus = "root.WCEM_STRING_SIMPLE";
		updateData.setConfigData(createConfigurationDataWith4Groups());
		updateData.getConfigData().setFocusId(focus);
		classUnderTest.handleGroupIdToDisplayForNavigationLinks(CPQActionType.NAV_TO_CSTIC_IN_GROUP, updateData);
		assertEquals("1-YSAP_SIMPLE_POC.Group1", updateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testHandleGroupIdToDisplayForNavigationLinksFocusSetAndActionTypeMenuNavigation()
	{
		final String focus = "root.WCEM_STRING_SIMPLE";
		updateData.setConfigData(createConfigurationDataWith4Groups());
		updateData.getConfigData().setFocusId(focus);
		classUnderTest.handleGroupIdToDisplayForNavigationLinks(CPQActionType.MENU_NAVIGATION, updateData);
		assertNull(updateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testHandleGroupIdToDisplayForNavigationLinksFocusSetGroupNotFound()
	{
		final UiGroupData conflictGroup = createCsticsGroupWithConflicts("conflictGroup");
		updateData.getConfigData().getGroups().add(conflictGroup);
		updateData.getConfigData().setFocusId("1");

		classUnderTest.handleGroupIdToDisplayForNavigationLinks(CPQActionType.NAV_TO_CSTIC_IN_CONFLICT, updateData);
		assertNull(updateData.getUiStatus().getGroupIdToDisplay());
	}

	@Test
	public void testHandleGroupIdToDisplayForNavigationLinksFocusSetNavInConflict()
	{
		final String conflictGroupID = "conflictGroup";
		final UiGroupData conflictGroup = createCsticsGroupWithConflicts(conflictGroupID);
		final ConfigurationData configDataWithConflicts = createEmptyConfigData();
		updateData.setConfigData(configDataWithConflicts);
		final List<UiGroupData> conflictsGroups = new ArrayList();
		conflictsGroups.add(conflictGroup);
		updateData.getConfigData().setGroups(conflictsGroups);
		updateData.getConfigData().setFocusId("root.WCEM_Conflict1");

		classUnderTest.handleGroupIdToDisplayForNavigationLinks(CPQActionType.NAV_TO_CSTIC_IN_CONFLICT, updateData);
		assertEquals(updateData.getConfigData().getFocusId(), "conflict.1.root.WCEM_Conflict1");
		assertEquals(updateData.getUiStatus().getGroupIdToDisplay(), "10");
	}

	@Test
	public void testIsFocusIdSetFocusIdNull()
	{
		assertFalse(classUnderTest.isFocusIdSet(null));
	}

	@Test
	public void testIsFocusIdSetFocusIdEmpty()
	{
		assertFalse(classUnderTest.isFocusIdSet(""));
	}

	@Test
	public void testIsFocusIdSetFocusId()
	{
		assertTrue(classUnderTest.isFocusIdSet("focusId"));
	}

	@Test
	public void testGetCsticKeysForExpandedSingleLevelGroups()
	{
		final List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData group = createCsticsGroupWithConflicts("simple.WCEM_Conflict2");
		group.setCstics(Collections.EMPTY_LIST);
		groups.add(group);
		final List<String> result = classUnderTest.getCsticKeysForExpandedSingleLevelGroups(groups);
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(group.getSubGroups().size(), result.size());
		assertEquals(group.getSubGroups().get(0).getCstics().get(0).getKey(), result.get(0));
	}

}
