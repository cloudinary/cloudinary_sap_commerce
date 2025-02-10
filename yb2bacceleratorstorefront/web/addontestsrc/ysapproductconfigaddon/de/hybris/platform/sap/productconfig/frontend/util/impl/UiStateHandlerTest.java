/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessage;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.sap.productconfig.facades.CPQActionType;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.FirstOrLastGroupType;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.impl.UniqueUIKeyGeneratorImpl;
import de.hybris.platform.sap.productconfig.frontend.UiCsticStatus;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.controllers.AbstractProductConfigControllerTCBase;
import de.hybris.platform.sap.productconfig.frontend.controllers.DummyModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.UniqueKeyGeneratorImpl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UiStateHandlerTest extends AbstractProductConfigControllerTCBase // inherit all the test helper methods for controller
{
	private static final String UIGROUP_ID = "1-THE_PRODUCT@_GEN";
	private static final String UIGROUP_ID_1 = "1-THE_PRODUCT@1";
	private static final String UIGROUP_ID_1_SPECIALIZED = "1-THE_NEW_PRODUCT@1";
	private static final String UNKNOWN_UIGROUP_ID = "XX";
	private static final String UIGROUP_ID_1_NO_INST_NAME = "1-@1";
	private static final String UIGROUP_ID_NO_GROUP = "1-THE_PRODUCT";
	private static final String INSTANCE_NAME = "THE_PRODUCT";
	private static final String ERROR_KEY = "root.WCEM_STRING_SIMPLE";
	private static final String FIELD_NAME_ERRONEOUS = "WCEM_STRING_SIMPLE";
	@InjectMocks
	private UiStateHandler classUnderTest;
	private UiStatus uiStatus;
	private String groupIdSub;
	private String groupIdToDisplay;
	private FieldError fieldError;
	private final String defaultMessage = "Huhu";

	@Mock
	private BindingResult bindingResult;

	@Before
	public void setUp()
	{
		csticList = createCsticsList();
		configData = createConfigurationDataWithGeneralGroupOnly();
		uiStatus = new UiStatus();
		fieldError = new FieldError("ObjectName", FIELD_NAME_ERRONEOUS, defaultMessage);
		final UniqueUIKeyGeneratorImpl uiKeyGenerator = new UniqueUIKeyGeneratorImpl();
		uiKeyGenerator.setKeyGenerator(new UniqueKeyGeneratorImpl());
		classUnderTest.setUiKeyGenerator(uiKeyGenerator);

		model = Mockito.mock(Model.class);
	}

	@Test
	public void countNumberOfUiErrorsPerGroup_2errors()
	{
		final List<CsticData> cstics = createCsticsList();
		final List<UiGroupData> uiGroups = createEmptyGroup();
		uiGroups.get(0).setCstics(cstics);
		uiGroups.get(0).setNumberErrorCstics(0);

		cstics.get(0).setCsticStatus(CsticStatusType.ERROR);
		cstics.get(1).setCsticStatus(CsticStatusType.WARNING);
		cstics.get(2).setCsticStatus(CsticStatusType.FINISHED);
		cstics.get(3).setCsticStatus(CsticStatusType.CONFLICT);

		classUnderTest.countNumberOfUiErrorsPerGroup(uiGroups);
		assertEquals("Group contains 2 Cstcis with error or warning status ", 2, uiGroups.get(0).getNumberErrorCstics());
	}

	@Test
	public void countNumberOfUiErrorsPerGroup_emptyGroup()
	{
		final UiGroupData uiGroup = new UiGroupData();
		classUnderTest.countNumberOfUiErrorsPerGroup(Collections.singletonList(uiGroup));
		//no exception
	}

	@Test
	public void countNumberOfUiErrorsPerGroup_noErrors()
	{
		final List<CsticData> cstics = createCsticsList();
		final List<UiGroupData> uiGroups = createEmptyGroup();
		uiGroups.get(0).setCstics(cstics);
		uiGroups.get(0).setNumberErrorCstics(0);

		cstics.get(0).setCsticStatus(CsticStatusType.DEFAULT);
		cstics.get(1).setCsticStatus(CsticStatusType.FINISHED);
		cstics.get(2).setCsticStatus(CsticStatusType.FINISHED);

		classUnderTest.countNumberOfUiErrorsPerGroup(uiGroups);
		assertEquals("Group cntains 0 Cstcis with error or warning status ", 0, uiGroups.get(0).getNumberErrorCstics());
	}

	@Test
	public void countNumberOfUiErrorsPerGroup_null()
	{
		classUnderTest.countNumberOfUiErrorsPerGroup(null);
		//no exception
	}

	@Test
	public void countNumberOfUiErrorsPerGroup_subGroupsSum()
	{
		final List<CsticData> cstics1 = createCsticsList();
		final List<CsticData> cstics2 = createCsticsList();
		final List<UiGroupData> uiGroups = createEmptyGroup();
		final List<UiGroupData> subGroups1 = createEmptyGroup();
		final List<UiGroupData> subGroups2 = createEmptyGroup();
		subGroups1.get(0).setCstics(cstics1);
		subGroups2.get(0).setCstics(cstics2);
		uiGroups.get(0).setSubGroups(new ArrayList(subGroups1));
		uiGroups.get(0).getSubGroups().addAll(subGroups2);

		cstics1.get(0).setCsticStatus(CsticStatusType.ERROR);
		cstics1.get(1).setCsticStatus(CsticStatusType.WARNING);
		cstics1.get(2).setCsticStatus(CsticStatusType.CONFLICT);
		cstics1.get(3).setCsticStatus(CsticStatusType.FINISHED);

		cstics2.get(0).setCsticStatus(CsticStatusType.ERROR);
		cstics2.get(1).setCsticStatus(CsticStatusType.FINISHED);
		cstics2.get(2).setCsticStatus(CsticStatusType.DEFAULT);

		classUnderTest.countNumberOfUiErrorsPerGroup(uiGroups);
		assertEquals("Group contains 2 Cstcis with error or warning status ", 3, uiGroups.get(0).getNumberErrorCstics());
	}

	@Test
	public void testExpandFirstGroupWithError_Conflict()
	{
		final UiGroupData errorGroup = configData.getGroups().get(0);
		errorGroup.setGroupStatus(GroupStatusType.CONFLICT);
		errorGroup.setCollapsed(true);
		errorGroup.setCollapsedInSpecificationTree(true);
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithError(configData.getGroups());
		assertSame(errorGroup, expandedGroup);
		assertFalse(errorGroup.isCollapsed());
		assertFalse(errorGroup.isCollapsedInSpecificationTree());
	}

	@Test
	public void testExpandFirstGroupWithError_ConflictGroupType()
	{
		final UiGroupData errorGroup = configData.getGroups().get(0);
		errorGroup.setGroupStatus(GroupStatusType.CONFLICT);
		errorGroup.setGroupType(GroupType.CONFLICT);
		errorGroup.setCollapsed(true);
		errorGroup.setCollapsedInSpecificationTree(true);
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithError(configData.getGroups());
		assertNull(expandedGroup);
		assertTrue(errorGroup.isCollapsed());
		assertTrue(errorGroup.isCollapsedInSpecificationTree());
	}

	@Test
	public void testExpandFirstGroupWithError_empty()
	{
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithError(Collections.EMPTY_LIST);
		assertNull(expandedGroup);
	}

	@Test
	public void testExpandFirstGroupWithError_ErrorInSubGroup()
	{
		final UiGroupData errorGroup = configData.getGroups().get(0);
		final UiGroupData rootGroup = createUiGroup("root", GroupStatusType.ERROR, true);
		rootGroup.setGroupType(GroupType.INSTANCE);
		rootGroup.setSubGroups(configData.getGroups());
		configData.setGroups(Collections.singletonList(rootGroup));
		errorGroup.setGroupStatus(GroupStatusType.ERROR);
		errorGroup.setCollapsed(true);
		errorGroup.setCollapsedInSpecificationTree(true);
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithError(configData.getGroups());
		assertSame(errorGroup, expandedGroup);
		assertFalse(errorGroup.isCollapsed());
		assertFalse(errorGroup.isCollapsedInSpecificationTree());
		assertFalse(rootGroup.isCollapsed());
		assertFalse(rootGroup.isCollapsedInSpecificationTree());
	}

	@Test
	public void testExpandFirstGroupWithError_noError()
	{
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithError(configData.getGroups());
		assertNull(expandedGroup);
	}

	@Test
	public void testExpandFirstGroupWithError_null()
	{
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithError(null);
		assertNull(expandedGroup);
	}

	@Test
	public void testExpandFirstGroupWithError_Warning()
	{
		final UiGroupData errorGroup = configData.getGroups().get(0);
		errorGroup.setGroupStatus(GroupStatusType.WARNING);
		errorGroup.setCollapsed(true);
		errorGroup.setCollapsedInSpecificationTree(true);
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithError(configData.getGroups());
		assertSame(errorGroup, expandedGroup);
		assertFalse(errorGroup.isCollapsed());
		assertFalse(errorGroup.isCollapsedInSpecificationTree());

	}

	@Test
	public void testExpandFirstGroupWithErrorOrConflict_Conflict()
	{
		final UiGroupData errorGroup = configData.getGroups().get(0);
		errorGroup.setGroupStatus(GroupStatusType.CONFLICT);
		errorGroup.setGroupType(GroupType.CONFLICT);
		errorGroup.setCollapsed(true);
		errorGroup.setCollapsedInSpecificationTree(true);
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithErrorOrConflict(configData.getGroups());
		assertSame(errorGroup, expandedGroup);
		assertFalse(errorGroup.isCollapsed());
		assertFalse(errorGroup.isCollapsedInSpecificationTree());
	}

	@Test
	public void testExpandFirstGroupWithErrorOrConflict_empty()
	{
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithErrorOrConflict(Collections.EMPTY_LIST);
		assertNull(expandedGroup);
	}

	@Test
	public void testExpandFirstGroupWithErrorOrConflict_ErrorInSubGroup()
	{
		final UiGroupData errorGroup = configData.getGroups().get(0);
		final UiGroupData rootGroup = createUiGroup("root", GroupStatusType.ERROR, true);
		rootGroup.setGroupType(GroupType.INSTANCE);
		rootGroup.setSubGroups(configData.getGroups());
		configData.setGroups(Collections.singletonList(rootGroup));
		errorGroup.setGroupStatus(GroupStatusType.ERROR);
		errorGroup.setCollapsed(true);
		errorGroup.setCollapsedInSpecificationTree(true);
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithErrorOrConflict(configData.getGroups());
		assertSame(errorGroup, expandedGroup);
		assertFalse(errorGroup.isCollapsed());
		assertFalse(errorGroup.isCollapsedInSpecificationTree());
		assertFalse(rootGroup.isCollapsed());
		assertFalse(rootGroup.isCollapsedInSpecificationTree());
	}

	@Test
	public void testExpandFirstGroupWithErrorOrConflict_noError()
	{
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithErrorOrConflict(configData.getGroups());
		assertNull(expandedGroup);
	}

	@Test
	public void testExpandFirstGroupWithErrorOrConflict_null()
	{
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithErrorOrConflict(null);
		assertNull(expandedGroup);
	}

	@Test
	public void testExpandFirstGroupWithErrorOrConflict_Warning()
	{
		final UiGroupData errorGroup = configData.getGroups().get(0);
		errorGroup.setGroupStatus(GroupStatusType.WARNING);
		errorGroup.setCollapsed(true);
		errorGroup.setCollapsedInSpecificationTree(true);
		final UiGroupData expandedGroup = classUnderTest.expandFirstGroupWithErrorOrConflict(configData.getGroups());
		assertSame(errorGroup, expandedGroup);
		assertFalse(errorGroup.isCollapsed());
		assertFalse(errorGroup.isCollapsedInSpecificationTree());
	}

	@Test
	public void testExpandGroupCloseOthersWithEmptyorNullList()
	{
		final UiGroupData expandedGroup = new UiGroupData();
		classUnderTest.expandGroupCloseOthers(null, expandedGroup);
		classUnderTest.expandGroupCloseOthers(Collections.EMPTY_LIST, expandedGroup);
	}

	@Test
	public void testExpandGroupCloseOthers()
	{
		final List<UiGroupData> uiGroups = new ArrayList<>();
		for (int i = 0; i < 5; i++)
		{
			final UiGroupData uiGroup = new UiGroupData();
			uiGroup.setName("Group_" + i);
			uiGroup.setCollapsed(true);

			uiGroups.add(uiGroup);
		}

		classUnderTest.expandGroupCloseOthers(uiGroups, uiGroups.get(2));

		for (int i = 0; i < 5; i++)
		{
			if (i == 2)
			{
				assertFalse(uiGroups.get(i).isCollapsed());
			}
			else
			{
				assertTrue(uiGroups.get(i).isCollapsed());
			}
		}
	}

	@Test
	public void testGetFirstCsticWithConflictInGroupWithErrorCstic()
	{
		final UiGroupData group = new UiGroupData();
		final List<CsticData> cstics = createCsticsList();
		final CsticData conflictCstic = cstics.get(0);
		conflictCstic.setCsticStatus(CsticStatusType.CONFLICT);

		group.setCstics(cstics);

		final CsticData cstic = classUnderTest.getFirstCsticWithErrorInGroup(group);

		assertNotNull(cstic);
		assertEquals(conflictCstic.getKey(), cstic.getKey());
	}

	@Test
	public void testGetFirstCsticWithErrorInGroupWithErrorCstic()
	{
		final UiGroupData group = new UiGroupData();
		final List<CsticData> cstics = createCsticsList();
		final CsticData warningCstic = cstics.get(0);
		warningCstic.setCsticStatus(CsticStatusType.WARNING);

		group.setCstics(cstics);

		final CsticData cstic = classUnderTest.getFirstCsticWithErrorInGroup(group);

		assertNotNull(cstic);
		assertEquals(warningCstic.getKey(), cstic.getKey());
	}

	@Test
	public void testGetFirstCsticWithErrorInGroupWithErrorCsticInSubgroup()
	{
		final UiGroupData group = new UiGroupData();
		final List<CsticData> cstics = createCsticsList();
		group.setCstics(cstics);

		final List<UiGroupData> subGroups = createCsticsGroup();
		final UiGroupData uiSubGroupData = subGroups.get(0);
		uiSubGroupData.setGroupStatus(GroupStatusType.WARNING);
		final CsticData warningCstic = uiSubGroupData.getCstics().get(0);
		warningCstic.setCsticStatus(CsticStatusType.ERROR);

		group.setSubGroups(subGroups);

		final CsticData cstic = classUnderTest.getFirstCsticWithErrorInGroup(group);

		assertNotNull(cstic);
		assertEquals(warningCstic.getKey(), cstic.getKey());
	}

	@Test
	public void testGetFirstCsticWithErrorInGroupWithoutErrorCstic()
	{
		final UiGroupData group = new UiGroupData();
		final List<CsticData> cstics = createCsticsList();
		group.setCstics(cstics);

		final CsticData cstic = classUnderTest.getFirstCsticWithErrorInGroup(group);

		assertNull(cstic);
	}

	@Test
	public void testGroupStatusReset()
	{
		final ConfigurationData configData = new ConfigurationData();

		final List<UiGroupData> groups = new ArrayList<>();
		groups.add(createUiGroup("1", GroupStatusType.ERROR, true));
		groups.add(createUiGroup("2", GroupStatusType.WARNING, true));
		groups.add(createUiGroup("3", GroupStatusType.FLAG, true));
		groups.add(createUiGroup("4", GroupStatusType.CONFLICT, true));
		configData.setGroups(groups);

		classUnderTest.resetGroupStatus(configData);

		for (final UiGroupData group : configData.getGroups())
		{
			assertEquals(GroupStatusType.DEFAULT, group.getGroupStatus());
		}
	}

	@Test
	public void testResetGroupStatusNoGroups()
	{
		configData.setGroups(null);
		classUnderTest.resetGroupStatus(configData);
		//run without NPE
	}

	@Test
	public void testRestoreValidationErrorsAfterUpdate_errorInSubgroup0Cstic0()
	{
		final List<UiGroupData> subGroups = createCsticsGroup();
		final String csticKey = subGroups.get(0).getCstics().get(0).getKey();
		configData.getGroups().get(0).setSubGroups(subGroups);
		final CsticData numericCstic = subGroups.get(0).getCstics().get(0);

		final FieldError error = createErrorForSubgoup0Cstic0();
		final Map<String, FieldError> userInputToRestore = new HashMap<>();
		userInputToRestore.put(csticKey, error);

		final BindingResult errors = classUnderTest.restoreValidationErrorsAfterUpdate(userInputToRestore, configData, null);

		assertEquals(1, errors.getErrorCount());
		assertEquals("groups[0].subGroups[0].cstics[0].formattedValue", errors.getFieldErrors().get(0).getField());
		assertEquals("CStic should have an error", CsticStatusType.ERROR, numericCstic.getCsticStatus());

	}

	@Test
	public void testRestoreValidationErrorsAfterUpdate_errorInSubgroup0Cstic0FromConflict()
	{
		final List<UiGroupData> subGroups = createCsticsGroup();
		final String csticKey = subGroups.get(0).getCstics().get(0).getKey();
		configData.getGroups().get(0).setSubGroups(subGroups);
		final CsticData numericCstic = subGroups.get(0).getCstics().get(0);

		final FieldError error = createErrorForSubgoup0Cstic0FromConflict();
		final Map<String, FieldError> userInputToRestore = new HashMap<>();
		userInputToRestore.put(csticKey, error);

		final BindingResult errors = classUnderTest.restoreValidationErrorsAfterUpdate(userInputToRestore, configData, null);

		assertEquals(1, errors.getErrorCount());
		assertEquals("CStic should have an error", CsticStatusType.ERROR, numericCstic.getCsticStatus());
		assertEquals("groups[0].subGroups[0].cstics[0].formattedValue", errors.getFieldErrors().get(0).getField());

	}

	@Test
	public void testRestoreValidationErrorsAfterUpdate_invisibleCstic()
	{
		final CsticData numericCstic = csticList.get(3);
		numericCstic.setVisible(false);
		final FieldError error = createErrorForCstic3();

		final Map<String, FieldError> userInputToRestore = new HashMap<>();
		userInputToRestore.put("root.WCEM_NUMERIC", error);
		final BindingResult errors = classUnderTest.restoreValidationErrorsAfterUpdate(userInputToRestore, configData, null);

		assertEquals(0, errors.getErrorCount());

	}

	@Test
	public void testRestoreValidationErrorsAfterUpdate_readOnlyCstic()
	{
		final CsticData numericCstic = csticList.get(3);
		numericCstic.setType(UiType.READ_ONLY);
		final FieldError error = createErrorForCstic3();

		final Map<String, FieldError> userInputToRestore = new HashMap<>();
		userInputToRestore.put("root.WCEM_NUMERIC", error);
		final BindingResult errors = classUnderTest.restoreValidationErrorsAfterUpdate(userInputToRestore, configData, null);

		assertEquals(0, errors.getErrorCount());
	}

	@Test
	public void testRestoreValidationErrorsAfterUpdate_readOnlyMultiImageCstic()
	{
		final CsticData numericCstic = csticList.get(3);
		numericCstic.setType(UiType.READ_ONLY_MULTI_SELECTION_IMAGE);
		final FieldError error = createErrorForCstic3();

		final Map<String, FieldError> userInputToRestore = new HashMap<>();
		userInputToRestore.put("root.WCEM_NUMERIC", error);
		final BindingResult errors = classUnderTest.restoreValidationErrorsAfterUpdate(userInputToRestore, configData, null);

		assertEquals(0, errors.getErrorCount());
	}

	@Test
	public void testRestoreValidationErrorsAfterUpdate_readOnlySingleImageCstic()
	{
		final CsticData numericCstic = csticList.get(3);
		numericCstic.setType(UiType.READ_ONLY_SINGLE_SELECTION_IMAGE);
		final FieldError error = createErrorForCstic3();

		final Map<String, FieldError> userInputToRestore = new HashMap<>();
		userInputToRestore.put("root.WCEM_NUMERIC", error);
		final BindingResult errors = classUnderTest.restoreValidationErrorsAfterUpdate(userInputToRestore, configData, null);

		assertEquals(0, errors.getErrorCount());
	}

	@Test
	public void testRestoreValidationErrorsAfterUpdate_visibleCstic()
	{
		final CsticData numericCstic = csticList.get(3);
		numericCstic.setAdditionalValue("");
		final FieldError error = createErrorForCstic3();

		final Map<String, FieldError> userInputToRestore = new HashMap<>();
		userInputToRestore.put("root.WCEM_NUMERIC", error);
		final BindingResult errors = classUnderTest.restoreValidationErrorsAfterUpdate(userInputToRestore, configData, null);

		assertEquals(1, errors.getErrorCount());
		assertEquals("groups[0].cstics[3].formattedValue", errors.getFieldErrors().get(0).getField());
		assertEquals("CStic should have an error", CsticStatusType.ERROR, numericCstic.getCsticStatus());
		assertEquals("aaa", numericCstic.getFormattedValue());
		assertEquals("", numericCstic.getAdditionalValue());
	}

	@Test
	public void testRestoreValidationErrorsAfterUpdate_visibleCstic_addInput_RADIO()
	{
		final CsticData numericCstic = csticList.get(3);
		numericCstic.setType(UiType.RADIO_BUTTON_ADDITIONAL_INPUT);
		final FieldError error = createErrorForCstic3("additionalValue");

		final Map<String, FieldError> userInputToRestore = new HashMap<>();
		userInputToRestore.put("root.WCEM_NUMERIC", error);
		final BindingResult errors = classUnderTest.restoreValidationErrorsAfterUpdate(userInputToRestore, configData, null);

		assertEquals(1, errors.getErrorCount());
		assertEquals("groups[0].cstics[3].additionalValue", errors.getFieldErrors().get(0).getField());
		assertEquals("CStic should have an error", CsticStatusType.ERROR, numericCstic.getCsticStatus());
		assertEquals(numericCstic.getLastValidValue(), numericCstic.getValue());
		assertEquals("aaa", numericCstic.getAdditionalValue());
	}

	@Test
	public void testRestoreValidationErrorsAfterUpdate_visibleCstic_addInput_DDLB()
	{
		final CsticData numericCstic = csticList.get(3);
		numericCstic.setType(UiType.DROPDOWN_ADDITIONAL_INPUT);
		final FieldError error = createErrorForCstic3("additionalValue");

		final Map<String, FieldError> userInputToRestore = new HashMap<>();
		userInputToRestore.put("root.WCEM_NUMERIC", error);
		final BindingResult errors = classUnderTest.restoreValidationErrorsAfterUpdate(userInputToRestore, configData, null);

		assertEquals(1, errors.getErrorCount());
		assertEquals("groups[0].cstics[3].additionalValue", errors.getFieldErrors().get(0).getField());
		assertEquals("CStic should have an error", CsticStatusType.ERROR, numericCstic.getCsticStatus());
		assertEquals(numericCstic.getLastValidValue(), numericCstic.getValue());
		assertEquals("aaa", numericCstic.getAdditionalValue());

	}

	@Test
	public void testHandleProductConfigMessages_empty() throws Exception
	{

		final Model model = new DummyModel();
		classUnderTest.handleProductConfigMessages(configData.getMessages(), model);
		assertTrue(model.asMap().isEmpty());
	}

	@Test
	public void testHandleProductConfigMessages_info() throws Exception
	{

		final Model model = new DummyModel();

		final ProductConfigMessageData message = new ProductConfigMessageData();
		message.setMessage("A Test message");
		message.setSeverity(ProductConfigMessageUISeverity.INFO);
		configData.setMessages(Collections.singletonList(message));

		classUnderTest.handleProductConfigMessages(configData.getMessages(), model);

		assertEquals(1, model.asMap().size());
		final List<GlobalMessage> messages = (List<GlobalMessage>) model.asMap().get(GlobalMessages.INFO_MESSAGES_HOLDER);
		assertEquals(1, messages.size());
		assertEquals("A Test message", messages.get(0).getCode());
	}

	@Test
	public void testHandleProductConfigMessages_conf() throws Exception
	{
		final Model model = new DummyModel();

		final ProductConfigMessageData message = new ProductConfigMessageData();
		message.setMessage("A Test message");
		message.setSeverity(ProductConfigMessageUISeverity.CONFIG);
		configData.setMessages(Collections.singletonList(message));

		classUnderTest.handleProductConfigMessages(configData.getMessages(), model);

		assertEquals(1, model.asMap().size());
		final List<GlobalMessage> messages = (List<GlobalMessage>) model.asMap().get(GlobalMessages.CONF_MESSAGES_HOLDER);
		assertEquals(1, messages.size());
		assertEquals("A Test message", messages.get(0).getCode());
	}

	@Test
	public void testHandleProductConfigMessages_error() throws Exception
	{
		final Model model = new DummyModel();

		final ProductConfigMessageData message = new ProductConfigMessageData();
		message.setMessage("A Test message");
		message.setSeverity(ProductConfigMessageUISeverity.ERROR);
		configData.setMessages(Collections.singletonList(message));

		classUnderTest.handleProductConfigMessages(configData.getMessages(), model);

		assertEquals(1, model.asMap().size());
		final List<GlobalMessage> messages = (List<GlobalMessage>) model.asMap().get(GlobalMessages.ERROR_MESSAGES_HOLDER);
		assertEquals(1, messages.size());
		assertEquals("A Test message", messages.get(0).getCode());
	}

	@Test
	public void testHandleConflictSolverMessageWithoutAnyConflicts() throws Exception
	{
		final Model model = new DummyModel();
		final int oldNumberOfConflicts = 0;
		uiStatus.setNumberOfConflictsToDisplay(oldNumberOfConflicts);
		classUnderTest.handleConflictSolverMessage(uiStatus, 0, model);

		assertEquals(0, model.asMap().size());
	}

	@Test
	public void testHandleConflictSolverMessage_allSolved() throws Exception
	{
		final Model model = new DummyModel();
		final int oldNumberOfConflicts = 1;
		uiStatus.setNumberOfConflictsToDisplay(oldNumberOfConflicts);

		final int newNumberOfConflicts = uiStatusSync.getNumberOfConflicts(configData);
		assertEquals(0, newNumberOfConflicts);
		classUnderTest.handleConflictSolverMessage(uiStatus, uiStatusSync.getNumberOfConflicts(configData), model);

		assertEquals(1, model.asMap().size());
		final List<GlobalMessage> messages = (List<GlobalMessage>) model.asMap().get(GlobalMessages.CONF_MESSAGES_HOLDER);
		assertEquals(1, messages.size());
	}

	@Test
	public void testHandleConflictSolverMessage_firstConflict() throws Exception
	{
		final Model model = new DummyModel();
		final int oldNumberOfConflicts = 0;
		uiStatus.setNumberOfConflictsToDisplay(oldNumberOfConflicts);

		final UiGroupData uiConflictData = createUiConflictGroupsWOCstics(new String[]
		{ "Conflict1" });
		configData.getGroups().add(uiConflictData);
		final int newNumberOfConflicts = uiStatusSync.getNumberOfConflicts(configData);
		assertEquals(1, newNumberOfConflicts);
		classUnderTest.handleConflictSolverMessage(uiStatus, uiStatusSync.getNumberOfConflicts(configData), model);

		assertEquals(1, model.asMap().size());
		final List<GlobalMessage> messages = (List<GlobalMessage>) model.asMap().get(GlobalMessages.INFO_MESSAGES_HOLDER);
		assertEquals(1, messages.size());
	}

	@Test
	public void testHandleConflictSolverMessage_notSolved() throws Exception
	{
		final Model model = new DummyModel();
		final int oldNumberOfConflicts = 1;
		uiStatus.setNumberOfConflictsToDisplay(oldNumberOfConflicts);

		final UiGroupData uiConflictData = createUiConflictGroupsWOCstics(new String[]
		{ "Conflict1" });
		configData.getGroups().add(uiConflictData);
		final int newNumberOfConflicts = uiStatusSync.getNumberOfConflicts(configData);
		assertEquals(1, newNumberOfConflicts);
		classUnderTest.handleConflictSolverMessage(uiStatus, uiStatusSync.getNumberOfConflicts(configData), model);

		assertEquals(1, model.asMap().size());
		final List<GlobalMessage> messages = (List<GlobalMessage>) model.asMap().get(GlobalMessages.INFO_MESSAGES_HOLDER);
		assertEquals(1, messages.size());
	}

	@Test
	public void testHandleConflictSolverMessage_noUiStatus() throws Exception
	{
		final UiGroupData uiConflictData = createUiConflictGroupsWOCstics(new String[]
		{ "Conflict1" });
		configData.getGroups().add(uiConflictData);
		final int newNumberOfConflicts = uiStatusSync.getNumberOfConflicts(configData);
		assertEquals(1, newNumberOfConflicts);
		classUnderTest.handleConflictSolverMessage(null, uiStatusSync.getNumberOfConflicts(configData), model);
		Mockito.verify(model).addAttribute(Mockito.eq(GlobalMessages.INFO_MESSAGES_HOLDER), Mockito.any(Collection.class));

	}

	@Test
	public void testHandleConflictSolverMessage_notSolved4Old() throws Exception
	{
		final int oldNumberOfConflicts = 4;
		uiStatus.setNumberOfConflictsToDisplay(oldNumberOfConflicts);

		final UiGroupData uiConflictData = createUiConflictGroupsWOCstics(new String[]
		{ "Conflict1" });
		configData.getGroups().add(uiConflictData);
		final int newNumberOfConflicts = uiStatusSync.getNumberOfConflicts(configData);
		assertEquals(1, newNumberOfConflicts);

		final Model model = new ExtendedModelMap();
		final Model spy = spy(model);
		classUnderTest.handleConflictSolverMessage(uiStatus, uiStatusSync.getNumberOfConflicts(configData), spy);

		Mockito.verify(spy).addAttribute(Mockito.eq(GlobalMessages.CONF_MESSAGES_HOLDER), Mockito.any(Collection.class));
		final Set<Entry<String, Object>> entries = spy.asMap().entrySet();
		assertEquals(1, entries.size());

		final Entry<String, Object> entry = entries.iterator().next();
		assertEquals(GlobalMessages.CONF_MESSAGES_HOLDER, entry.getKey());

		final List<GlobalMessage> messages = (List<GlobalMessage>) entry.getValue();

		final GlobalMessage message = messages.get(0);
		assertEquals("sapproductconfig.conflict.messages.resolved", message.getCode());
		assertFalse(message.getAttributes().isEmpty());
		assertEquals(Integer.valueOf(3), message.getAttributes().iterator().next());
	}

	@Test
	public void testHandleConflictSolverMessage_newConflict() throws Exception
	{
		final int oldNumberOfConflicts = 1;
		uiStatus.setNumberOfConflictsToDisplay(oldNumberOfConflicts);

		final UiGroupData uiConflictData = createUiConflictGroupsWOCstics(new String[]
		{ "Conflict1", "Conflict2" });
		configData.getGroups().add(uiConflictData);
		final int newNumberOfConflicts = uiStatusSync.getNumberOfConflicts(configData);
		assertEquals(2, newNumberOfConflicts);
		classUnderTest.handleConflictSolverMessage(uiStatus, uiStatusSync.getNumberOfConflicts(configData), model);

		Mockito.verify(model).addAttribute(Mockito.eq(GlobalMessages.INFO_MESSAGES_HOLDER), Mockito.any(Collection.class));

	}

	@Test
	public void testHandleConflictSolverMessage_oneSolved() throws Exception
	{
		final int oldNumberOfConflicts = 2;
		uiStatus.setNumberOfConflictsToDisplay(oldNumberOfConflicts);

		final UiGroupData uiConflictData = createUiConflictGroupsWOCstics(new String[]
		{ "Conflict1" });
		configData.getGroups().add(uiConflictData);
		final int newNumberOfConflicts = uiStatusSync.getNumberOfConflicts(configData);
		assertEquals(1, newNumberOfConflicts);
		classUnderTest.handleConflictSolverMessage(uiStatus, uiStatusSync.getNumberOfConflicts(configData), model);

		Mockito.verify(model).addAttribute(Mockito.eq(GlobalMessages.CONF_MESSAGES_HOLDER), Mockito.any(Collection.class));
	}

	@Test
	public void testGetFirstGroupWithCsticsDeepSearchWithEmptyGroups()
	{
		final ConfigurationData configData = createEmptyConfigData();

		UiGroupData result = classUnderTest.getFirstGroupWithCsticsDeepSearch(configData.getGroups());
		assertNull(result);

		configData.setGroups(new ArrayList<>());
		final List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData group = new UiGroupData();
		group.setSubGroups(Collections.EMPTY_LIST);
		groups.add(group);
		configData.setGroups(groups);

		result = classUnderTest.getFirstGroupWithCsticsDeepSearch(configData.getGroups());
		assertNull(result);
	}

	@Test
	public void testGetFirstGroupWithCsticsDeepSearch()
	{
		ConfigurationData configData = createMultiLevelConfiguration();

		UiGroupData uiGroup = classUnderTest.getFirstGroupWithCsticsDeepSearch(configData.getGroups());
		assertEquals(UIGROUP_ID, uiGroup.getId());

		configData = createMultiLevelConfiguration2();
		uiGroup = classUnderTest.getFirstGroupWithCsticsDeepSearch(configData.getGroups());
		assertEquals(UIGROUP_ID, uiGroup.getId());
	}

	@Test
	public void testCheckGroupExistence()
	{
		final ConfigurationData configData = createMultiLevelConfiguration();
		assertTrue(classUnderTest.checkGroupExistence(configData, UIGROUP_ID));

		assertFalse(classUnderTest.checkGroupExistence(configData, UNKNOWN_UIGROUP_ID));
	}

	@Test
	public void testCheckGroupExistenceAfterClassNodeSpecialization()
	{
		final ConfigurationData configData = createMultiLevelConfiguration2();
		prepareClassNodeSpecializationTestData(configData);
		assertTrue(classUnderTest.checkGroupExistence(configData, UIGROUP_ID_1));

		simulateClassNodeSpecialization(configData);
		// after class node specialization: check with old groupId should still signal group existence
		assertTrue(classUnderTest.checkGroupExistence(configData, UIGROUP_ID_1));
	}

	@Test
	public void testGetGroupIdWithoutInstanceName()
	{
		String groupIdWithoutInstName = classUnderTest.getGroupIdWithoutInstanceName(UNKNOWN_UIGROUP_ID);
		assertEquals(UNKNOWN_UIGROUP_ID, groupIdWithoutInstName);

		groupIdWithoutInstName = classUnderTest.getGroupIdWithoutInstanceName(UIGROUP_ID_1);
		assertEquals(UIGROUP_ID_1_NO_INST_NAME, groupIdWithoutInstName);

		groupIdWithoutInstName = classUnderTest.getGroupIdWithoutInstanceName(UIGROUP_ID_1_SPECIALIZED);
		assertEquals(UIGROUP_ID_1_NO_INST_NAME, groupIdWithoutInstName);

		groupIdWithoutInstName = classUnderTest.getGroupIdWithoutInstanceName(null);
		assertNull(groupIdWithoutInstName);
	}

	@Test
	public void testCompileGroupForDisplayForConfigurationWithoutGroups()
	{
		final ConfigurationData configData = createEmptyConfigData();
		final UiStatus uiStatus = new UiStatus();
		uiStatus.setGroupIdToDisplay(UNKNOWN_UIGROUP_ID);
		classUnderTest.compileGroupForDisplay(configData, uiStatus);

		assertEquals(null, configData.getGroupIdToDisplay());
		assertEquals(null, configData.getGroupToDisplay());
	}

	@Test
	public void testCompileGroupForDisplayUiStatusDisplayGroupRemoved()
	{
		final ConfigurationData configData = createMultiLevelConfiguration();
		final UiStatus uiStatus = new UiStatus();
		uiStatus.setGroupIdToDisplay(UNKNOWN_UIGROUP_ID);
		classUnderTest.compileGroupForDisplay(configData, uiStatus);

		assertEquals(UIGROUP_ID, configData.getGroupIdToDisplay());
		assertEquals(UIGROUP_ID, configData.getGroupToDisplay().getGroup().getId());
	}

	@Test
	public void testCompileGroupForDisplayClassNodeSpecialized()
	{
		final ConfigurationData configData = createMultiLevelConfiguration2();
		prepareClassNodeSpecializationTestData(configData);
		final UiStatus uiStatus = new UiStatus();
		uiStatus.setGroupIdToDisplay(UIGROUP_ID_1);
		classUnderTest.compileGroupForDisplay(configData, uiStatus);

		assertEquals(UIGROUP_ID_1, configData.getGroupIdToDisplay());
		assertEquals(UIGROUP_ID_1, configData.getGroupToDisplay().getGroup().getId());
		simulateClassNodeSpecialization(configData);
		// previous groupIdToDisplay (UIGROUP_ID_1) will return uigroup for new id UIGROUP_ID_2 because instance name is not considered
		classUnderTest.compileGroupForDisplay(configData, uiStatus);
		assertEquals(UIGROUP_ID_1_SPECIALIZED, configData.getGroupIdToDisplay());
		assertEquals(UIGROUP_ID_1_SPECIALIZED, configData.getGroupToDisplay().getGroup().getId());
	}

	@Test
	public void testCompileGroupForDisplayForNavigationInConflictGroup()
	{
		final ConfigurationData configData = createMultiLevelConfiguration();
		configData.setCpqAction(null);
		final UiStatus uiStatus = new UiStatus();
		uiStatus.setGroupIdToDisplay(UNKNOWN_UIGROUP_ID);
		classUnderTest.compileGroupForDisplay(configData, uiStatus);
		assertEquals(UIGROUP_ID, configData.getGroupIdToDisplay());
		assertEquals(UIGROUP_ID, configData.getGroupToDisplay().getGroup().getId());
		assertFalse(configData.getGroupToDisplay().getGroup().isCollapsed());

		configData.setCpqAction(CPQActionType.NEXT_BTN);
		assertEquals(UIGROUP_ID, configData.getGroupIdToDisplay());
		assertEquals(UIGROUP_ID, configData.getGroupToDisplay().getGroup().getId());
		assertFalse(configData.getGroupToDisplay().getGroup().isCollapsed());

		configData.setCpqAction(CPQActionType.NAV_TO_CSTIC_IN_CONFLICT);
		assertEquals(UIGROUP_ID, configData.getGroupIdToDisplay());
		assertEquals(UIGROUP_ID, configData.getGroupToDisplay().getGroup().getId());
		assertFalse(configData.getGroupToDisplay().getGroup().isCollapsed());

		configData.setCpqAction(CPQActionType.NAV_TO_CSTIC_IN_GROUP);
		assertEquals(UIGROUP_ID, configData.getGroupIdToDisplay());
		assertEquals(UIGROUP_ID, configData.getGroupToDisplay().getGroup().getId());
		assertFalse(configData.getGroupToDisplay().getGroup().isCollapsed());
	}

	/**
	 * Class node specialization: Change instance name in group-id of second group while keeping instance id
	 *
	 * @param configData
	 */
	private void simulateClassNodeSpecialization(final ConfigurationData configData)
	{
		final UiGroupData group = configData.getGroups().get(1);
		group.setId(UIGROUP_ID_1_SPECIALIZED);
	}

	private void prepareClassNodeSpecializationTestData(final ConfigurationData configData)
	{
		// Add cstics to first group
		final UiGroupData firstGroup = configData.getGroups().get(0);
		final List<CsticData> cstics00 = new ArrayList<>();
		final CsticData cstic000 = new CsticData();
		cstic000.setName("Cstic");
		cstics00.add(cstic000);
		firstGroup.setCstics(cstics00);
	}

	@Test
	public void testCompileGroupForDisplayEmptyGroupArray()
	{
		final List<UiGroupData> groups = new ArrayList<>();
		final String groupIdToDisplay = "A";
		final Deque<String> path = new ArrayDeque<>();
		final Deque<String> groupPath = new ArrayDeque<>();
		final UiGroupData matchingGroup = classUnderTest.compileGroupForDisplay(groups, groupIdToDisplay, path, groupPath,
				UiStateHandler.PATHELEMENT_GROUPS);
		assertNull(matchingGroup);
	}

	@Test
	public void testCompileGroupForDisplayFirstGroupEmpty()
	{
		final ConfigurationData configData = createMultiLevelConfiguration();

		classUnderTest.compileGroupForDisplay(configData, null);
		assertEquals(UIGROUP_ID, configData.getGroupIdToDisplay());
		assertEquals(UIGROUP_ID, configData.getGroupToDisplay().getGroup().getId());
	}

	@Test
	public void testCompileGroupForDisplayMultiLevel()
	{
		final List<UiGroupData> groups = createConfigurationMultilevel();

		final Deque<String> path = new ArrayDeque<>();
		final Deque<String> groupPath = new ArrayDeque<>();

		final UiGroupData matchingGroup = classUnderTest.compileGroupForDisplay(groups, groupIdSub, path, groupPath,
				UiStateHandler.PATHELEMENT_GROUPS);
		assertNotNull(matchingGroup);
		assertEquals("groups[0].subGroups[1].", classUnderTest.extractPathAsString(path));
	}

	@Test
	public void testCompileGroupForDisplayNoGroups()
	{
		final ConfigurationData configData = new ConfigurationData();
		final UiStatus uiStatus = new UiStatus();
		classUnderTest.compileGroupForDisplay(configData, uiStatus);
		assertNull(configData.getGroupToDisplay());
	}

	@Test
	public void testCompileGroupForDisplaySingleLevel()
	{
		final List<UiGroupData> groups = createConfigurationMultilevel();

		final Deque<String> path = new ArrayDeque<>();
		final Deque<String> groupPath = new ArrayDeque<>();
		final UiGroupData matchingGroup = classUnderTest.compileGroupForDisplay(groups, groupIdToDisplay, path, groupPath,
				UiStateHandler.PATHELEMENT_GROUPS);
		assertNotNull(matchingGroup);
		assertEquals("groups[0].", classUnderTest.extractPathAsString(path));
	}

	@Test
	public void testGetFirstGroupWithCsticsWithEmptyGroups()
	{
		final ConfigurationData configData = createEmptyConfigData();
		final UiGroupData result = classUnderTest.getFirstGroupWithCstics(configData.getGroups());
		assertNull(result);
	}

	@Test
	public void testGetFirstGroupWithCsticsWithEmptySubGroups()
	{
		final ConfigurationData configData = createEmptyConfigData();
		final List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData group = new UiGroupData();
		group.setSubGroups(null);
		groups.add(group);
		final UiGroupData result = classUnderTest.getFirstGroupWithCstics(configData.getGroups());
		assertNull(result);
	}

	@Test
	public void testGetFirstGroupWithCsticsWithSubGroups()
	{
		final ConfigurationData configData = createEmptyConfigData();
		final List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData group = new UiGroupData();
		final List<UiGroupData> subGroups = new ArrayList<>();
		final UiGroupData subGroup = new UiGroupData();
		final String subGroupId = "subGroup_1";
		subGroup.setId(subGroupId);
		final List<CsticData> cstics = new ArrayList<>();
		for (int i = 1; i <= 5; i++)
		{
			final CsticData cstic = new CsticData();
			cstic.setName("cstic_" + i);
			cstics.add(cstic);
		}
		subGroup.setCstics(cstics);
		subGroups.add(subGroup);
		group.setSubGroups(subGroups);
		groups.add(group);
		configData.setGroups(groups);

		final UiGroupData result = classUnderTest.getFirstGroupWithCstics(configData.getGroups());
		assertNotNull(result);
		assertEquals(subGroupId, result.getId());
	}

	@Test
	public void testGetFirstGroupWithCstics()
	{
		ConfigurationData configData = createMultiLevelConfiguration();

		UiGroupData uiGroup = classUnderTest.getFirstGroupWithCstics(configData.getGroups());
		assertEquals(UIGROUP_ID, uiGroup.getId());

		configData = createMultiLevelConfiguration2();
		uiGroup = classUnderTest.getFirstGroupWithCstics(configData.getGroups());
		assertEquals(UIGROUP_ID_1, uiGroup.getId());
	}

	protected List<UiGroupData> createConfigurationMultilevel()
	{
		groupIdSub = "C";
		final List<UiGroupData> groups = new ArrayList<>();
		groupIdToDisplay = "A";
		final UiGroupData group = new UiGroupData();
		group.setId(groupIdToDisplay);
		final List<UiGroupData> subGroups = new ArrayList<>();
		group.setSubGroups(subGroups);
		groups.add(group);

		//Add 2 sub groups
		final UiGroupData groupSub1 = new UiGroupData();
		groupSub1.setId("B");
		subGroups.add(groupSub1);
		final UiGroupData groupSub2 = new UiGroupData();
		groupSub2.setId(groupIdSub);
		subGroups.add(groupSub2);
		return groups;
	}


	public ConfigurationData createMultiLevelConfiguration()
	{
		final UiStatusSyncTest uiStatusSyncTest = new UiStatusSyncTest();
		final ConfigurationData configData = new ConfigurationData();
		final List<UiGroupData> uiGroups = new ArrayList<>();
		final List<UiGroupData> uiGroupsFlat = new ArrayList<>();

		for (int i = 0; i < 3; i++)
		{
			uiGroups.add(uiStatusSyncTest.createUiGroup(String.valueOf(i), false));
		}
		uiGroupsFlat.addAll(uiGroups);

		final UiGroupData uiGroup0 = uiGroups.get(0);
		final List<UiGroupData> uiSubGroups = new ArrayList<>();
		final UiGroupData uiGroup00 = new UiGroupData();
		uiGroup00.setId(UIGROUP_ID);
		uiSubGroups.add(uiGroup00);
		uiGroupsFlat.add(uiGroup00);

		final List<CsticData> cstics00 = new ArrayList<>();
		final CsticData cstic000 = new CsticData();
		cstic000.setName("Cstic");
		cstics00.add(cstic000);
		uiGroup00.setCstics(cstics00);

		uiGroup0.setSubGroups(uiSubGroups);
		configData.setGroups(uiGroups);
		configData.setCsticGroupsFlat(uiGroupsFlat);
		return configData;
	}

	public ConfigurationData createMultiLevelConfiguration2()
	{
		final ConfigurationData configData = createMultiLevelConfiguration();
		final List<UiGroupData> uiGroups = configData.getGroups();

		final UiGroupData uiGroup0 = uiGroups.get(0);
		final List<CsticData> csticsEmpty = new ArrayList<>();
		uiGroup0.setCstics(csticsEmpty);

		final UiGroupData uiGroup1 = uiGroups.get(1);
		uiGroup1.setId(UIGROUP_ID_1);
		final List<CsticData> cstics10 = new ArrayList<>();
		final CsticData cstic100 = new CsticData();
		cstic100.setName("Cstic1");
		cstics10.add(cstic100);
		uiGroup1.setCstics(cstics10);

		configData.setGroups(uiGroups);
		return configData;
	}


	@Test
	public void testHandleValidationErrorsBeforeUpdate_noErr()
	{
		final BindingResult bindingResult = new BeanPropertyBindingResult(configData,
				SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE);
		final Map<String, FieldError> inputToRestore = classUnderTest.handleValidationErrorsBeforeUpdate(configData, bindingResult);
		assertEquals(0, inputToRestore.size());
	}

	@Test
	public void testHandleValidationErrorsBeforeUpdate_error()
	{
		final CsticData numericCstic = csticList.get(3);
		numericCstic.setValue("aaa");
		numericCstic.setCsticStatus(CsticStatusType.ERROR);

		final BindingResult bindingResult = new BeanPropertyBindingResult(configData,
				SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE);
		final FieldError error = createErrorForCstic3();
		bindingResult.addError(error);

		final Map<String, FieldError> inputToRestore = classUnderTest.handleValidationErrorsBeforeUpdate(configData, bindingResult);

		assertEquals(1, inputToRestore.size());
		assertSame(error, inputToRestore.get("root.WCEM_NUMERIC"));
		assertEquals(numericCstic.getLastValidValue(), numericCstic.getFormattedValue());
	}

	@Test
	public void testHandleValidationErrorsBeforeUpdate_error_addInput()
	{
		final CsticData numericCstic = csticList.get(3);
		numericCstic.setType(UiType.RADIO_BUTTON_ADDITIONAL_INPUT);
		numericCstic.setAdditionalValue("aaa");
		numericCstic.setCsticStatus(CsticStatusType.ERROR);

		final BindingResult bindingResult = new BeanPropertyBindingResult(configData,
				SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE);
		final FieldError error = createErrorForCstic3();
		bindingResult.addError(error);

		final Map<String, FieldError> inputToRestore = classUnderTest.handleValidationErrorsBeforeUpdate(configData, bindingResult);

		assertEquals(1, inputToRestore.size());
		assertSame(error, inputToRestore.get("root.WCEM_NUMERIC"));
		assertEquals(numericCstic.getLastValidValue(), numericCstic.getValue());
		assertEquals("", numericCstic.getAdditionalValue());
	}

	@Test
	public void testHandleValidationErrorsBeforeUpdate_findErrorInSubgroup0Cstic0()
	{
		final List<UiGroupData> subGroups = createCsticsGroup();
		final String csticKey = subGroups.get(0).getCstics().get(0).getKey();
		configData.getGroups().get(0).setSubGroups(subGroups);
		final CsticData numericCstic = subGroups.get(0).getCstics().get(0);
		numericCstic.setValue("aaa");
		numericCstic.setCsticStatus(CsticStatusType.ERROR);

		final BindingResult bindingResult = new BeanPropertyBindingResult(configData,
				SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE);
		final FieldError error = createErrorForSubgoup0Cstic0();
		bindingResult.addError(error);

		final Map<String, FieldError> inputToRestore = classUnderTest.handleValidationErrorsBeforeUpdate(configData, bindingResult);

		assertEquals(1, inputToRestore.size());
		assertSame(error, inputToRestore.get(csticKey));
		assertEquals(numericCstic.getLastValidValue(), numericCstic.getFormattedValue());

	}




	@Test
	public void testGetUiGroupStatusWithNullUiGroupStatus()
	{
		final List<String> pricingInputs = new ArrayList<>();
		final List<UiGroupStatus> groups = null;
		classUnderTest.fillAllVisibleCsticIdsOfGroup(groups, pricingInputs);
		assertTrue(pricingInputs.isEmpty());
	}


	@Test
	public void testGetUiGroupStatusWithEmptyUiGroupStatus()
	{
		final List<String> pricingInputs = new ArrayList<>();
		final List<UiGroupStatus> groups = Collections.emptyList();
		classUnderTest.fillAllVisibleCsticIdsOfGroup(groups, pricingInputs);
		assertTrue(pricingInputs.isEmpty());
	}

	@Test
	public void testGetUiGroupStatus()
	{
		final List<String> pricingInputs = new ArrayList<>();
		final List<UiGroupStatus> groups = createListOfUiGroupStatus();
		classUnderTest.fillAllVisibleCsticIdsOfGroup(groups, pricingInputs);
		assertFalse(pricingInputs.isEmpty());
	}

	@Test
	public void testGetUiCsticStatusWithNullUiCsticStatusList()
	{
		final List<UiCsticStatus> cstics = Collections.emptyList();
		final List<String> pricingInputs = new ArrayList<>();
		classUnderTest.fillAllVisibleCsticIds(cstics, pricingInputs);
		assertTrue(pricingInputs.isEmpty());
	}

	@Test
	public void testGetUiCsticStatusWithEmptyUiCsticStatusList()
	{
		final List<UiCsticStatus> cstics = null;
		final List<String> pricingInputs = new ArrayList<>();
		classUnderTest.fillAllVisibleCsticIds(cstics, pricingInputs);
		assertTrue(pricingInputs.isEmpty());
	}

	@Test
	public void testGetUiCsticStatus()
	{
		final List<String> pricingInputs = new ArrayList<>();
		final List<UiCsticStatus> cstics = new ArrayList<>();
		createUiCsticStatus(cstics);
		createUiCsticStatus(cstics);
		classUnderTest.fillAllVisibleCsticIds(cstics, pricingInputs);
		assertNotNull(pricingInputs);
		assertFalse(pricingInputs.isEmpty());
	}

	@Test
	public void testGetConflictGroupNo()
	{
		final ConfigurationData multiLevelConfiguration = createMultiLevelConfiguration();
		final UiGroupData conflictHeaderGroup = createConflictGroups("NEW_CONFLICT");
		final UiGroupData conflictGroup = createUiGroup("CONFLICT", GroupType.CONFLICT, GroupStatusType.CONFLICT,
				FirstOrLastGroupType.FIRST, false, true);
		conflictHeaderGroup.getSubGroups().add(0, conflictGroup);
		multiLevelConfiguration.getGroups().set(0, conflictHeaderGroup);

		assertEquals(1, classUnderTest.getConflictGroupNo(multiLevelConfiguration, conflictGroup));

		final ConfigurationData singleLevelConfiguration = createConfigurationDataWithGeneralGroupOnly();
		singleLevelConfiguration.setSingleLevel(true);
		singleLevelConfiguration.getGroups().add(0, conflictHeaderGroup);
		assertEquals(1, classUnderTest.getConflictGroupNo(singleLevelConfiguration, conflictGroup));

		final UiGroupData conflictGroup2 = createUiGroup("CONFLICT", GroupType.CONFLICT, GroupStatusType.CONFLICT,
				FirstOrLastGroupType.FIRST, false, true);
		conflictHeaderGroup.getSubGroups().add(1, conflictGroup2);
		assertEquals(2, classUnderTest.getConflictGroupNo(singleLevelConfiguration, conflictGroup2));
	}

	protected void createUiCsticStatus(final List<UiCsticStatus> cstics)
	{
		for (int i = 0; i < 3; i++)
		{
			final UiCsticStatus cstic = new UiCsticStatus();
			final int id = i + 1;
			cstic.setId("cstic" + id);
			cstics.add(cstic);
		}
	}


	protected List<UiGroupStatus> createListOfUiGroupStatus()
	{
		final List<UiGroupStatus> groups = new ArrayList<>();
		for (int i = 0; i < 2; i++)
		{
			final UiGroupStatus group = createUiGroupStatus(i);

			groups.add(group);
		}
		return groups;
	}

	protected UiGroupStatus createUiGroupStatus(final int i)
	{
		final UiGroupStatus group = new UiGroupStatus();
		final String id = "group" + (i + 1);
		group.setId(id);
		group.setCollapsed(true);
		if (i % 2 == 0)
		{
			group.setCollapsed(false);
			group.setSubGroups(createSubGroups(i));
			group.setCstics(createListOfUiCsticStatus());
		}

		return group;
	}

	protected List<UiGroupStatus> createSubGroups(final int i)
	{
		final List<UiGroupStatus> subGroups = new ArrayList<>();
		final UiGroupStatus subGroup = new UiGroupStatus();
		final String subGroupId = "subGroup" + (i + 1);
		subGroup.setId(subGroupId);
		subGroup.setCstics(createListOfUiCsticStatus());
		subGroups.add(subGroup);

		return subGroups;
	}

	protected List<UiCsticStatus> createListOfUiCsticStatus()
	{
		final List<UiCsticStatus> cstics = new ArrayList<>();
		for (int j = 0; j < 3; j++)
		{
			final int csticId = j + 1;
			cstics.add(createUiCsticStatus("cstic" + csticId));
		}

		return cstics;
	}

	protected UiCsticStatus createUiCsticStatus(final String csticId)
	{
		final UiCsticStatus cstic = new UiCsticStatus();
		cstic.setId(csticId);

		return cstic;
	}

	@Test
	public void testHasNoConflicts()
	{
		assertTrue(classUnderTest.hasNoConflicts(0, 0));
	}

	@Test
	public void testHasOnlyNewConflicts()
	{
		assertFalse(classUnderTest.hasOnlyNewConflicts(0, 0));
		assertTrue(classUnderTest.hasOnlyNewConflicts(0, 3));
		assertFalse(classUnderTest.hasOnlyNewConflicts(3, 0));
	}

	@Test
	public void testHasOnlyOldConflicts()
	{
		assertFalse(classUnderTest.hasOnlyOldConflicts(0, 0));
		assertTrue(classUnderTest.hasOnlyOldConflicts(2, 0));
		assertFalse(classUnderTest.hasOnlyOldConflicts(0, 2));
	}

	@Test
	public void testGetGroupIdToDisplayAfterResolvingConflicts()
	{
		configData = createMultiLevelConfiguration();
		uiStatus = new UiStatus();
		uiStatus.setLastNoneConflictGroupId("Group without conflicts");
		final String result = classUnderTest.getGroupIdToDisplayAfterResolvingConflicts(configData, uiStatus);
		assertNotNull(result);
		assertEquals(uiStatus.getLastNoneConflictGroupId(), result);
	}

	@Test
	public void testGetGroupIdToDisplayAfterResolvingConflictsForSingleLevelProduct()
	{
		final ConfigurationData configData = createEmptyConfigData();
		configData.setSingleLevel(true);
		final List<UiGroupData> groups = createEmptyGroup();
		configData.setGroups(groups);
		uiStatus = new UiStatus();
		uiStatus.setLastNoneConflictGroupId("Group without conflicts");
		final String result = classUnderTest.getGroupIdToDisplayAfterResolvingConflicts(configData, uiStatus);
		assertNotNull(result);
		assertNotEquals(uiStatus.getLastNoneConflictGroupId(), result);
	}

	@Test
	public void testGetGroupIdToDisplayAfterResolvingConflictsForGroupStartsWithConflict()
	{
		final ConfigurationData configData = createEmptyConfigData();
		final List<UiGroupData> groups = createEmptyGroup();
		groups.get(0).setId("CONFLICT_" + groups.get(0).getId());
		configData.setGroups(groups);
		uiStatus = new UiStatus();
		uiStatus.setLastNoneConflictGroupId("Group without conflicts");
		final String result = classUnderTest.getGroupIdToDisplayAfterResolvingConflicts(configData, uiStatus);
		assertNotNull(result);
		assertNotEquals(uiStatus.getLastNoneConflictGroupId(), result);
	}

	@Test
	public void testCountNumberOfUiErrorsPerGroupWithoutANyUiGroups()
	{
		assertEquals(0, classUnderTest.countNumberOfUiErrorsPerGroup(null));
	}

	@Test
	public void testCountNumberOfUiErrorsPerGroupWithUiGroupWithoutGroupType()
	{
		final List<UiGroupData> uiGroups = new ArrayList<>();
		final UiGroupData uiGroup = createUiGroup("1", null, GroupStatusType.DEFAULT, FirstOrLastGroupType.INTERJACENT, false,
				true);
		uiGroups.add(uiGroup);
		assertEquals(0, classUnderTest.countNumberOfUiErrorsPerGroup(uiGroups));
	}

	@Test
	public void testCountNumberOfUiErrorsPerGroupWithUiGroupAndGroupType()
	{
		final List<UiGroupData> uiGroups = new ArrayList<>();
		final UiGroupData uiGroup = createUiGroup("1", GroupType.CONFLICT, GroupStatusType.DEFAULT,
				FirstOrLastGroupType.INTERJACENT, false, true);
		uiGroups.add(uiGroup);
		assertEquals(0, classUnderTest.countNumberOfUiErrorsPerGroup(uiGroups));
	}

	@Test
	public void testCountNumberOfUiErrorsPerGroupWithoutCstics()
	{
		final List<UiGroupData> uiGroups = new ArrayList<>();
		final UiGroupData uiGroup = createUiGroup("1", GroupType.CSTIC_GROUP, GroupStatusType.DEFAULT,
				FirstOrLastGroupType.INTERJACENT, false, true);
		uiGroup.setCstics(Collections.EMPTY_LIST);
		uiGroups.add(uiGroup);
		assertEquals(0, classUnderTest.countNumberOfUiErrorsPerGroup(uiGroups));
	}

	@Test
	public void testCountNumberOfUiErrorsPerGroupWithConflictUiGroupAndCstics()
	{
		final String csticId = "root.WCEM_Conflict2";
		final List<UiGroupData> uiGroups = new ArrayList<>();
		final UiGroupData uiGroup = createUiGroup("2", GroupStatusType.FINISHED, GroupType.CSTIC_GROUP, true);
		final List<CsticData> cstics = new ArrayList<>();
		for (int i = 1; i <= 5; i++)
		{
			final CsticData cstic = new CsticData();
			cstic.setName("cstic_" + i);
			cstic.setCsticStatus(CsticStatusType.FINISHED);
			cstics.add(cstic);
		}
		uiGroup.setCstics(cstics);
		uiGroups.add(uiGroup);
		assertEquals(0, classUnderTest.countNumberOfUiErrorsPerGroup(uiGroups));
	}

	@Test
	public void testCountNumberOfUiErrorsPerGroupWithConflictUiGroupAndConflictCstics()
	{
		final String csticId = "root.WCEM_Conflict2";
		final List<UiGroupData> uiGroups = new ArrayList<>();
		final UiGroupData uiGroup = createUiGroup("2", GroupStatusType.FINISHED, GroupType.CSTIC_GROUP, true);
		final List<CsticData> cstics = new ArrayList<>();
		for (int i = 1; i <= 5; i++)
		{
			final CsticData cstic = new CsticData();
			cstic.setName("cstic_" + i);
			cstic.setCsticStatus(CsticStatusType.WARNING);
			cstics.add(cstic);
		}
		uiGroup.setCstics(cstics);
		uiGroups.add(uiGroup);
		assertEquals(5, classUnderTest.countNumberOfUiErrorsPerGroup(uiGroups));
	}

	@Test
	public void testFindCollapsedErrorCsticsWithNullOrEmptyUserInputToRestore()
	{
		Map<String, FieldError> userInputToRestore = null;
		configData = createEmptyConfigData();
		Map<String, FieldError> result = classUnderTest.findCollapsedErrorCstics(userInputToRestore, configData);
		assertNotNull(result);
		assertTrue(result.isEmpty());

		userInputToRestore = Collections.EMPTY_MAP;
		result = classUnderTest.findCollapsedErrorCstics(userInputToRestore, configData);
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testRestoreValidationErrorsOnGetConfig()
	{

		final CsticData numericCstic = csticList.get(3);
		numericCstic.setType(UiType.DROPDOWN_ADDITIONAL_INPUT);
		final FieldError error = createErrorForCstic3("additionalValue");

		final Map<String, FieldError> userInputToRestore = new HashMap<>();
		userInputToRestore.put("root.WCEM_NUMERIC", error);
		final BindingResult errorsOnGetConfig = classUnderTest.restoreValidationErrorsOnGetConfig(userInputToRestore, configData,
				bindingResult);

		assertEquals(errorsOnGetConfig, bindingResult);
		Mockito.verify(bindingResult).addError(Mockito.any());
	}

	@Test
	public void testFindCollapsedErrorCsticsWithInputToRestore()
	{
		final Map<String, FieldError> userInputToRestore = new HashMap<>();
		userInputToRestore.put(ERROR_KEY, fieldError);
		createConfigWithCollapsedErrorCstic();
		final Map<String, FieldError> result = classUnderTest.findCollapsedErrorCstics(userInputToRestore, configData);
		assertNotNull(result);
		assertEquals(1, result.size());
		final FieldError fieldError = result.get(ERROR_KEY);
		assertNotNull(fieldError);
		assertEquals(FIELD_NAME_ERRONEOUS, fieldError.getField());
	}


	protected void createConfigWithCollapsedErrorCstic()
	{
		configData = createConfigDataForSimpleTest(true, true, true);
		final UiGroupData secondGroup = configData.getGroups().get(1);
		secondGroup.setCstics(csticList);
		secondGroup.getSubGroups().get(0).setCstics(csticList);
		configData.getGroups().get(2).setCstics(csticList);
	}

	@Test
	public void testHandleAutoExpandReturnsNull()
	{
		configData = createEmptyConfigData();
		configData.setAutoExpand(false);
		uiStatus = new UiStatus();
		assertNull(classUnderTest.handleAutoExpand(configData, uiStatus));

		configData = createMultiLevelConfiguration();
		configData.setAutoExpand(true);
		uiStatus = new UiStatus();
		assertNull(classUnderTest.handleAutoExpand(configData, uiStatus));
		assertFalse(configData.isAutoExpand());
		assertNull(uiStatus.getFirstErrorCsticId());
	}

	@Test
	public void testHandleAutoExpand()
	{
		configData = createMultiLevelConfiguration();
		configData.setAutoExpand(true);
		final UiGroupData errorGroup = configData.getGroups().get(0);
		errorGroup.setCstics(Collections.EMPTY_LIST);
		errorGroup.setGroupStatus(GroupStatusType.CONFLICT);
		errorGroup.setGroupType(GroupType.CONFLICT);
		errorGroup.setCollapsed(true);
		errorGroup.setCollapsedInSpecificationTree(true);
		configData.getGroups().add(errorGroup);
		uiStatus = new UiStatus();

		assertNotNull(classUnderTest.handleAutoExpand(configData, uiStatus));
	}

	@Test
	public void testHandleAutoExpandWithStartLevel()
	{
		configData = createMultiLevelConfiguration();
		configData.setAutoExpand(true);
		configData.setStartLevel(1);
		final UiGroupData errorGroup = configData.getGroups().get(0);
		errorGroup.setCstics(Collections.EMPTY_LIST);
		errorGroup.setGroupStatus(GroupStatusType.CONFLICT);
		errorGroup.setGroupType(GroupType.CONFLICT);
		errorGroup.setCollapsed(true);
		errorGroup.setCollapsedInSpecificationTree(true);
		configData.getGroups().add(errorGroup);
		uiStatus = new UiStatus();

		assertNotNull(classUnderTest.handleAutoExpand(configData, uiStatus));
		assertFalse(configData.getGroups().get(0).isCollapsed());
	}

	@Test
	public void testHandleAutoExpandWithErrorCsticInNonConflictGroup()
	{
		configData = createMultiLevelConfiguration();
		configData.setAutoExpand(true);
		configData.setStartLevel(1);
		final UiGroupData errorGroup = configData.getGroups().get(0);
		errorGroup.setCstics(Collections.EMPTY_LIST);
		errorGroup.setGroupStatus(GroupStatusType.ERROR);
		errorGroup.setGroupType(GroupType.CSTIC_GROUP);
		errorGroup.setCollapsed(true);
		errorGroup.setCollapsedInSpecificationTree(true);
		final List<CsticData> cstics = createCsticsList();
		final CsticData warningCstic = cstics.get(0);
		warningCstic.setKey("cstic_key");
		warningCstic.setCsticStatus(CsticStatusType.WARNING);
		errorGroup.setCstics(cstics);
		configData.getGroups().add(errorGroup);
		uiStatus = new UiStatus();

		assertNotNull(classUnderTest.handleAutoExpand(configData, uiStatus));
		assertEquals("cstic_key", configData.getFocusId());
	}

	@Test
	public void testHandleAutoExpandWithErrorCstic()
	{
		configData = createMultiLevelConfiguration();
		configData.setAutoExpand(true);
		configData.setStartLevel(1);
		final UiGroupData errorGroup = configData.getGroups().get(0);
		errorGroup.setCstics(Collections.EMPTY_LIST);
		errorGroup.setGroupStatus(GroupStatusType.CONFLICT);
		errorGroup.setGroupType(GroupType.CONFLICT);
		errorGroup.setCollapsed(true);
		errorGroup.setCollapsedInSpecificationTree(true);
		final List<CsticData> cstics = createCsticsList();
		final CsticData warningCstic = cstics.get(0);
		warningCstic.setKey("cstic_key");
		warningCstic.setCsticStatus(CsticStatusType.WARNING);
		errorGroup.setCstics(cstics);
		configData.getGroups().add(errorGroup);
		uiStatus = new UiStatus();

		assertNotNull(classUnderTest.handleAutoExpand(configData, uiStatus));
		assertEquals("conflict.1.cstic_key", configData.getFocusId());
	}

	@Test
	public void testResetGroupWithoutSubGroups()
	{
		final List<UiGroupData> uiGroups = createEmptyGroup();
		classUnderTest.resetGroupWithSubGroups(uiGroups);
		assertEquals(GroupStatusType.DEFAULT, uiGroups.get(0).getGroupStatus());
	}

	@Test
	public void testResetGroupWithSubGroups()
	{
		final List<UiGroupData> uiGroups = createEmptyGroup();
		final UiGroupData uiGroup = uiGroups.get(0);
		final List<UiGroupData> subGroups = new ArrayList<>();
		final UiGroupData subGroup = new UiGroupData();
		subGroups.add(subGroup);
		uiGroup.setSubGroups(subGroups);
		classUnderTest.resetGroupWithSubGroups(uiGroups);
		assertEquals(GroupStatusType.DEFAULT, uiGroups.get(0).getGroupStatus());
		assertEquals(GroupStatusType.DEFAULT, uiGroups.get(0).getSubGroups().get(0).getGroupStatus());
	}

	@Test
	public void testDetermineReplacementGroupIdWithNullOrEmptyGroupIdToDisplayUiStatus()
	{
		configData = new ConfigurationData();
		groupIdToDisplay = "groupIdToDisplay";

		String result = classUnderTest.determineReplacementGroupId(configData, groupIdToDisplay, "");
		assertEquals(groupIdToDisplay, result);

		result = classUnderTest.determineReplacementGroupId(configData, groupIdToDisplay, null);
		assertEquals(groupIdToDisplay, result);
	}

	@Test
	public void testGetCsticForFieldPath()
	{
		final String fieldPath = "groups[0].cstics[0].value";
		final CsticData result = classUnderTest.getCsticForFieldPath(configData, fieldPath);
		assertNotNull(result);
		assertEquals("WCEM_STRING_SIMPLE", result.getName());
	}

	@Test
	public void testGetCsticForFieldPathInvalidGroupIndex()
	{
		final String fieldPath = "groups[1].cstics[0].value";
		final CsticData result = classUnderTest.getCsticForFieldPath(configData, fieldPath);
		assertNotNull(result);
	}

	@Test
	public void testGetGroupIndex()
	{
		final PathExtractor extractor = new PathExtractor("groups[0].cstics[0].value");
		final int result = classUnderTest.getGroupIndex(configData, extractor);
		assertEquals(0, result);
	}

	@Test
	public void testGetGroupIndexArrayOutOfBounds()
	{
		final PathExtractor extractor = new PathExtractor("groups[22].cstics[0].value");
		final int result = classUnderTest.getGroupIndex(configData, extractor);
		assertEquals(0, result);
	}

	@Test
	public void testFillAllVisibleCsticIdsOfGroupGroupHierarchy()
	{
		final List<String> pricingInputs = new ArrayList<>();
		final List<UiGroupStatus> groups = createListOfUiGroupStatusHierarchy();
		classUnderTest.fillAllVisibleCsticIdsOfGroup(groups, pricingInputs);
		assertFalse(pricingInputs.isEmpty());
		assertTrue(pricingInputs.contains("C1"));
		assertTrue(pricingInputs.contains("C11"));
		assertTrue(pricingInputs.contains("C21"));
		assertFalse(pricingInputs.contains("C2"));
		assertFalse(pricingInputs.contains("C12"));
		assertFalse(pricingInputs.contains("C22"));
	}

	protected List<UiGroupStatus> createListOfUiGroupStatusHierarchy()
	{
		final List<UiGroupStatus> groups = new ArrayList<>();
		final UiGroupStatus g1 = createUiGroupStatusWithCstic("G1", "C1", false);
		final UiGroupStatus g2 = createUiGroupStatusWithCstic("G2", "C2", true);
		groups.add(g1);
		groups.add(g2);

		final UiGroupStatus g11 = createUiGroupStatusWithCstic("G11", "C11", false);
		final UiGroupStatus g12 = createUiGroupStatusWithCstic("G12", "C12", true);
		final List<UiGroupStatus> subgroups1 = new ArrayList<>();
		subgroups1.add(g11);
		subgroups1.add(g12);
		g1.setSubGroups(subgroups1);

		final UiGroupStatus g21 = createUiGroupStatusWithCstic("G21", "C21", false);
		final UiGroupStatus g22 = createUiGroupStatusWithCstic("G22", "C22", true);
		final List<UiGroupStatus> subgroups2 = new ArrayList<>();
		subgroups2.add(g21);
		subgroups2.add(g22);
		g2.setSubGroups(subgroups2);

		return groups;
	}

	protected UiGroupStatus createUiGroupStatusWithCstic(final String groupId, final String csticId, final boolean collapsed)
	{
		final UiGroupStatus group = new UiGroupStatus();
		group.setId(groupId);
		group.setCollapsed(collapsed);
		group.setSubGroups(new ArrayList<UiGroupStatus>());
		final List<UiCsticStatus> cstics = new ArrayList<>();
		final UiCsticStatus cstic = new UiCsticStatus();
		cstic.setId(csticId);
		cstics.add(cstic);
		group.setCstics(cstics);
		return group;
	}
}
