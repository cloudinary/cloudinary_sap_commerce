/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.ConflictData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.FirstOrLastGroupType;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationCartIntegrationFacadeImpl;
import de.hybris.platform.sap.productconfig.facades.impl.UniqueUIKeyGeneratorImpl;
import de.hybris.platform.sap.productconfig.facades.populator.ComparableConflictGroup;
import de.hybris.platform.sap.productconfig.facades.tracking.UiTrackingRecorder;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.breadcrumb.ProductConfigureBreadcrumbBuilder;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.util.ConfigErrorHandler;
import de.hybris.platform.sap.productconfig.frontend.util.impl.ConstantHandler;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStateHandler;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStatusSync;
import de.hybris.platform.sap.productconfig.frontend.validator.ConflictChecker;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.UniqueKeyGeneratorImpl;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.context.WebApplicationContext;


public class AbstractProductConfigControllerTCBase
{
	protected static final String KB_VERSION = "123";
	protected static final String LOG_SYS = "ABC";
	protected static final String KB_NAME = "YSAP_SIMPLE_POC";
	protected static final String SIMPLE_VALUE = "SIMPLE";
	protected static final String PRODUCT_CODE = KB_NAME;
	protected static final String CONFIG_ID = "5";
	protected static final String CSTIC_PATH = "groups[0].cstics[1].key";
	protected KBKeyData kbKey;
	protected List<CsticData> csticList;
	protected ConfigurationData configData;

	@Mock
	protected WebApplicationContext wac; // cached
	@Mock
	protected ProductModel productModel;
	@Mock
	protected ProductData productData;
	@Mock
	protected SessionService sessionService;
	@Mock
	protected Session hybriSession;
	@Mock
	protected ProductFacade productFacade;
	@Mock
	protected ProductConfigureBreadcrumbBuilder productConfigurationBreadcrumbBuilder;
	@Mock
	protected ConfigurationFacade configFacade;
	@Mock
	protected ProductService productService;
	@Mock
	protected StoreSessionFacade storeSessionFacade;
	@Mock
	protected CMSPageService cmsPageService;
	@Mock
	protected PageTitleResolver pageTitleResolver;
	@Mock
	protected MockServletContext servletContext; // cached
	@Mock
	protected Model model;
	@Mock
	protected SessionAccessFacade sessionAccessFacade;
	@Mock
	protected ConfigurationAbstractOrderEntryLinkStrategy abstractOrderEntryLinkStrategy;
	@Mock
	protected ConfigurationProductLinkStrategy configurationProductLinkStrategy;
	@Mock
	protected ConflictChecker conflictChecker;
	@Mock
	protected ConfigurationCartIntegrationFacadeImpl configCartIntegrationFacade;
	@Mock
	protected UiTrackingRecorder uiTrackingRecorder;
	@Mock
	protected ConfigErrorHandler errorHandler;
	@Mock
	protected CartFacade cartFacadeMock;

	protected MockHttpServletRequest request;
	protected UiStatusSync uiStatusSync;


	public AbstractProductConfigControllerTCBase()
	{
		super();
		uiStatusSync = new UiStatusSync();
	}

	protected void injectMocks(final AbstractProductConfigController classUnderTest)
	{
		classUnderTest.setSessionAccessFacade(sessionAccessFacade);
		classUnderTest.setAbstractOrderEntryLinkStrategy(abstractOrderEntryLinkStrategy);
		classUnderTest.setProductLinkStrategy(configurationProductLinkStrategy);
		final UiStateHandler uiStateHandler = new UiStateHandler();
		final UniqueUIKeyGeneratorImpl uiKeyGenerator = new UniqueUIKeyGeneratorImpl();
		uiKeyGenerator.setKeyGenerator(new UniqueKeyGeneratorImpl());
		uiStateHandler.setUiKeyGenerator(uiKeyGenerator);
		classUnderTest.setUiStateHandler(uiStateHandler);
		classUnderTest.setUiStatusSync(uiStatusSync);
		classUnderTest.setCartFacade(cartFacadeMock);

	}

	protected KBKeyData createKbKey()
	{
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(PRODUCT_CODE);
		return kbKey;
	}

	protected ConfigurationData createConfigurationDataWithGeneralGroupOnly()
	{
		final ConfigurationData configData = createEmptyConfigData();

		final UiGroupData csticGroup = new UiGroupData();
		csticGroup.setCstics(csticList);
		csticGroup.setId(ConstantHandler.GENERAL_GROUP_NAME);
		csticGroup.setGroupType(GroupType.CSTIC_GROUP);
		final List<UiGroupData> csticGroups = new ArrayList<>();
		csticGroups.add(csticGroup);
		configData.setGroups(csticGroups);
		configData.setCsticGroupsFlat(csticGroups);

		return configData;
	}

	protected ConfigurationData createConfigurationDataWith4Groups()
	{
		final ConfigurationData myConfigData = createEmptyConfigData();

		// 4 Cstic Groups on root-instance
		final String instanceName = KB_NAME;
		final String instId = "1";
		final List<UiGroupData> groups = create4CsticGroups(instanceName, instId);
		// Mark first as FIRST
		groups.get(0).setFirstOrLastGroup(FirstOrLastGroupType.FIRST);
		// Mark last as LAST
		groups.get(3).setFirstOrLastGroup(FirstOrLastGroupType.LAST);
		myConfigData.setGroups(groups);
		myConfigData.setCsticGroupsFlat(groups);

		return myConfigData;
	}

	protected ConfigurationData createConfigurationDataMultiLevel()
	{
		final ConfigurationData myConfigData = createEmptyConfigData();
		final List<UiGroupData> csticGroupsFlat = new ArrayList();
		myConfigData.setCsticGroupsFlat(csticGroupsFlat);

		// The hierarchy:
		//
		// 0 (4 cstic-groups)
		// |
		// +--0.1 (4 cstic-groups)
		// |  |
		// |  +--0.1.1
		// |	|
		// |  +--0.1.2 (4 cstic groups)
		// |  |
		// |  +--0.1.3
		// |
		// +--0.2 (4 cstic-groups)
		// |
		// +--0.3

		// 4 Cstic Groups on root-instance (0)
		final String instanceName = KB_NAME;
		final String instId = "1";
		final List<UiGroupData> groups = create4CsticGroups(instanceName, instId);
		csticGroupsFlat.addAll(groups);
		// Mark first as first
		groups.get(0).setFirstOrLastGroup(FirstOrLastGroupType.FIRST);

		// Sub-instances below root:
		// UIGroup for sub-instance 1 of root (0.1)
		final String instanceName01 = "SUBINST-0.1";
		final String instId01 = "2";
		final UiGroupData group01 = createUiGroup(instId01 + "-" + instanceName01, GroupType.INSTANCE, null, true);
		groups.add(group01);

		// UIGroup for sub-instance 2 of root (0.2)
		final String instanceName02 = "SUBINST-0.2";
		final String instId02 = "3";
		final UiGroupData group02 = createUiGroup(instId02 + "-" + instanceName02, GroupType.INSTANCE, null, true);
		groups.add(group02);

		// UIGroup for sub-instance 3 of root (0.3)
		final String instanceName03 = "SUBINST-0.3";
		final String instId03 = "4";
		final UiGroupData group03 = createUiGroup(instId03 + "-" + instanceName03, GroupType.INSTANCE, null, false);
		groups.add(group03);

		myConfigData.setGroups(groups);

		// 4 Cstic Groups on sub-instance 1 (0.1)
		final List<UiGroupData> groups01 = create4CsticGroups(instanceName01, instId01);
		csticGroupsFlat.addAll(groups01);
		group01.setSubGroups(groups01);

		// Sub-instances below sub-instance 1 (0.1):
		// UIGroup for sub-instance 1 of sub-instance 1 (0.1.1)
		final String instanceName011 = "SUBINST-0.1.1";
		final String instId011 = "5";
		final UiGroupData group011 = createUiGroup(instId011 + "-" + instanceName011, GroupType.INSTANCE, null, false);
		groups01.add(group011);

		// UIGroup for sub-instance 2 of sub-instance 1 (0.1.2)
		final String instanceName012 = "SUBINST-0.1.2";
		final String instId012 = "6";
		final UiGroupData group012 = createUiGroup(instId012 + "-" + instanceName012, GroupType.INSTANCE, null, true);
		groups01.add(group012);

		// UIGroup for sub-instance 3 of sub-instance 1 (0.1.3)
		final String instanceName013 = "SUBINST-0.1.3";
		final String instId013 = "7";
		final UiGroupData group013 = createUiGroup(instId013 + "-" + instanceName013, GroupType.INSTANCE, null, false);
		groups01.add(group013);

		// 4 Cstic Groups on sub-instance 2 (0.1.2)
		final List<UiGroupData> groups012 = create4CsticGroups(instanceName012, instId012);
		csticGroupsFlat.addAll(groups012);
		group012.setSubGroups(groups012);

		// 4 Cstic Groups on sub-instance 2 (0.2)
		final List<UiGroupData> groups02 = create4CsticGroups(instanceName02, instId02);
		csticGroupsFlat.addAll(groups02);
		// Mark last as last
		groups02.get(3).setFirstOrLastGroup(FirstOrLastGroupType.LAST);
		group02.setSubGroups(groups02);

		return myConfigData;
	}

	protected List<UiGroupData> create4CsticGroups()
	{
		final List<UiGroupData> groups = new ArrayList<>();

		for (int i = 0; i < 5; i++)
		{
			final UiGroupData uiGroup = new UiGroupData();
			uiGroup.setId("Group_" + i);
			uiGroup.setCollapsed(i % 2 == 0);
			final List<CsticData> cstics = new ArrayList<>();
			for (int j = 0; j < 4; j++)
			{
				final CsticData cstic = new CsticData();
				cstic.setKey("Group_" + i + ".CSTIC_" + j);
				cstics.add(cstic);
			}
			uiGroup.setCstics(cstics);

			groups.add(uiGroup);
		}
		return groups;
	}

	protected List<UiGroupData> create4CsticGroups(final String instanceName, final String instId)
	{
		final List<UiGroupData> csticGroups = new ArrayList<>();
		UiGroupData csticGroup = createUiGroup(instId + "-" + instanceName + ".Group1", GroupType.CSTIC_GROUP,
				FirstOrLastGroupType.INTERJACENT, true);
		csticGroup.setCstics(csticList);
		csticGroups.add(csticGroup);

		csticGroup = createUiGroup(instId + "-" + instanceName + ".Group2", GroupType.CSTIC_GROUP, FirstOrLastGroupType.INTERJACENT,
				true);
		csticGroup.setCstics(csticList);
		csticGroups.add(csticGroup);

		csticGroup = createUiGroup(instId + "-" + instanceName + ".Group3", GroupType.CSTIC_GROUP, FirstOrLastGroupType.INTERJACENT,
				true);
		csticGroup.setCstics(csticList);
		csticGroups.add(csticGroup);

		csticGroup = createUiGroup(instId + "-" + instanceName + ".Group4", GroupType.CSTIC_GROUP, FirstOrLastGroupType.INTERJACENT,
				true);
		csticGroup.setCstics(csticList);
		csticGroups.add(csticGroup);
		return csticGroups;
	}

	protected ConfigurationData createEmptyConfigData()
	{
		final ConfigurationData configData = new ConfigurationData();
		configData.setConfigId(CONFIG_ID);
		configData.setKbKey(kbKey);
		configData.setMessages(Collections.emptyList());
		configData.setGroups(Collections.emptyList());
		configData.setComplete(true);
		configData.setConsistent(true);
		return configData;
	}

	protected List<UiGroupData> createEmptyGroup()
	{
		final UiGroupData csticGroup = createUiGroup("1", GroupType.CSTIC_GROUP, GroupStatusType.DEFAULT,
				FirstOrLastGroupType.INTERJACENT, false, true);
		csticGroup.setCstics(csticList);
		final List<UiGroupData> csticGroups = new ArrayList<>();
		csticGroups.add(csticGroup);
		return csticGroups;
	}

	protected List<UiGroupData> createCsticsGroup()
	{
		final List<UiGroupData> groups = createEmptyGroup();

		final List<CsticData> cstics = new ArrayList<>();
		final CsticData cstic = new CsticData();
		cstic.setName("numericCstic");
		cstic.setLangdepname("Numeric:");
		cstic.setType(UiType.NUMERIC);
		cstic.setVisible(true);
		cstic.setValue("123");
		cstic.setLastValidValue("123");
		cstic.setKey("root.group.numericCstic");
		cstic.setConflicts(Collections.EMPTY_LIST);
		cstics.add(cstic);

		groups.get(0).setCstics(cstics);

		return groups;
	}

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


	public UiGroupData createUiGroup(final String id, final boolean collapsed)
	{
		return createUiGroup(id, null, collapsed);
	}

	protected UiGroupData createGroupWith5Cstics()
	{
		final UiGroupData group = new UiGroupData();
		group.setId("GENERAL");
		final List<CsticData> cstics = new ArrayList<>();
		for (int i = 1; i <= 5; i++)
		{
			final CsticData cstic = new CsticData();
			if (i % 2 == 0)
			{
				cstic.setCsticStatus(CsticStatusType.WARNING);
			}
			else
			{
				cstic.setCsticStatus(CsticStatusType.FINISHED);
			}
			cstic.setKey("key_" + i);
			cstics.add(cstic);
		}
		group.setCstics(cstics);
		return group;
	}

	protected UiGroupData createCsticsGroupWithConflicts(final String id)
	{

		final UiGroupData conflictHeaderUiGroup = createUiGroup("CONFLICT_HEADER", GroupType.CONFLICT_HEADER,
				GroupStatusType.CONFLICT, FirstOrLastGroupType.FIRST, false, true);

		final List<UiGroupData> conflictGroups = new ArrayList<UiGroupData>();

		final CsticData cstic = createCsticWithConflict("root.WCEM_Conflict1");
		final ArrayList<CsticData> cstics = new ArrayList();
		cstics.add(cstic);

		final ComparableConflictGroup conflict = createConflictGroup("10", cstics);
		conflictGroups.add(conflict);

		final CsticData cstic2 = createCsticWithConflict(id);
		final ArrayList<CsticData> cstics2 = new ArrayList();
		cstics2.add(cstic2);

		final ComparableConflictGroup conflict2 = createConflictGroup("17", cstics2);
		conflictGroups.add(conflict2);

		conflictHeaderUiGroup.setSubGroups(conflictGroups);

		return conflictHeaderUiGroup;
	}

	protected UiGroupData createConflictGroups(final String id)
	{

		final UiGroupData conflictHeaderUiGroup = createUiGroup("CONFLICT_HEADER", GroupType.CONFLICT_HEADER,
				GroupStatusType.CONFLICT, FirstOrLastGroupType.FIRST, false, true);

		final List<UiGroupData> conflictGroups = new ArrayList<UiGroupData>();

		final CsticData cstic = createCsticWithConflict("root.WCEM_Conflict1");
		final ArrayList<CsticData> cstics = new ArrayList();
		cstics.add(cstic);

		final ComparableConflictGroup conflict = createConflictGroup("10", cstics);
		conflictGroups.add(conflict);

		final CsticData cstic2 = createCsticWithConflict(id);
		final ArrayList<CsticData> cstics2 = new ArrayList();
		cstics2.add(cstic2);

		final ComparableConflictGroup conflict2 = createConflictGroup("17", cstics2);
		conflictGroups.add(conflict2);

		final CsticData cstic3 = createCsticWithConflict(id);
		final ArrayList<CsticData> cstics3 = new ArrayList();
		cstics3.add(cstic3);

		final ComparableConflictGroup conflict3 = createConflictGroup("22", cstics3);
		conflictGroups.add(conflict3);

		conflictHeaderUiGroup.setSubGroups(conflictGroups);

		return conflictHeaderUiGroup;
	}

	private ComparableConflictGroup createConflictGroup(final String id, final ArrayList<CsticData> cstics)
	{
		final ComparableConflictGroup conflict = new ComparableConflictGroup();
		conflict.setId(id);
		conflict.setDescription("Conflict" + id);
		conflict.setName("Conflict" + id);
		conflict.setGroupType(GroupType.CONFLICT);
		conflict.setConfigurable(true);
		conflict.setCollapsed(false);
		conflict.setCstics(cstics);
		return conflict;
	}

	private CsticData createCsticWithConflict(final String id)
	{
		final List<ConflictData> conflicts = new ArrayList<>();
		final ConflictData conflict1 = new ConflictData();
		conflict1.setText("Cstic with conflict");
		conflicts.add(conflict1);

		final CsticData cstic = new CsticData();
		cstic.setName("numericCstic" + id);
		cstic.setLangdepname("Numeric:" + id);
		cstic.setType(UiType.NUMERIC);
		cstic.setVisible(true);
		cstic.setValue("123");
		cstic.setLastValidValue("123");
		cstic.setKey(id);
		cstic.setLongText("This cstic has a long text");
		cstic.setShowFullLongText(false);
		cstic.setCsticStatus(CsticStatusType.CONFLICT);
		cstic.setConflicts(conflicts);
		return cstic;
	}

	protected UiGroupData createUiConflictGroupsWOCstics(final String[] conflictsIds)
	{
		final String id = "header";
		final UiGroupData conflictHeaderUiGroup = createUiGroup(id, GroupType.CONFLICT_HEADER, GroupStatusType.CONFLICT,
				FirstOrLastGroupType.FIRST, false, true);

		final List<UiGroupData> conflictGroups = new ArrayList<UiGroupData>();

		ComparableConflictGroup conflict = null;
		for (final String name : conflictsIds)
		{
			conflict = createConflictGroup(name, null);
			conflictGroups.add(conflict);
		}
		conflictHeaderUiGroup.setSubGroups(conflictGroups);


		return conflictHeaderUiGroup;
	}

	protected List<CsticData> createCsticsList()
	{
		final List<CsticData> cstics = new ArrayList<>();

		CsticData cstic = new CsticData();
		cstic.setKey("root.WCEM_STRING_SIMPLE");
		cstic.setName("WCEM_STRING_SIMPLE");
		cstic.setLangdepname("Simple String:");
		cstic.setType(UiType.STRING);
		cstic.setVisible(false);
		cstic.setValue(SIMPLE_VALUE);
		cstic.setConflicts(Collections.emptyList());

		List<CsticValueData> domainValues = new ArrayList<>();
		cstic.setDomainvalues(domainValues);
		cstics.add(cstic);

		cstic = new CsticData();
		cstic.setConflicts(Collections.emptyList());
		cstic.setKey("root.WCEM_STRING_RB");
		cstic.setName("WCEM_STRING_RB");
		cstic.setLangdepname("RADIO BUTTON String:");
		cstic.setType(UiType.RADIO_BUTTON);
		cstic.setVisible(true);
		domainValues = new ArrayList<>();
		CsticValueData value = new CsticValueData();
		value.setName("VAL1");
		value.setLangdepname("VALUE 1");
		domainValues.add(value);
		value = new CsticValueData();
		value.setName("VAL2");
		value.setLangdepname("VALUE 2");
		domainValues.add(value);
		cstic.setDomainvalues(domainValues);
		cstics.add(cstic);

		cstic = new CsticData();
		cstic.setConflicts(Collections.emptyList());
		cstic.setKey("root.WCEM_STRING_MULTI");
		cstic.setLangdepname("MULTI String:");
		cstic.setType(UiType.CHECK_BOX_LIST);
		cstic.setVisible(true);
		domainValues = new ArrayList<>();
		value = new CsticValueData();
		value.setName("VAL1");
		value.setLangdepname("VALUE 1");
		domainValues.add(value);
		value = new CsticValueData();
		value.setName("VAL2");
		value.setLangdepname("VALUE 2");
		domainValues.add(value);
		cstic.setDomainvalues(domainValues);
		cstics.add(cstic);

		cstic = new CsticData();
		cstic.setConflicts(Collections.emptyList());
		cstic.setKey("root.WCEM_NUMERIC");
		cstic.setLangdepname("Numeric:");
		cstic.setType(UiType.NUMERIC);
		cstic.setVisible(true);
		cstic.setValue("123");
		cstic.setLastValidValue("123");
		cstics.add(cstic);

		return cstics;
	}

	protected UiGroupData createUiGroup(final String id, final GroupStatusType status, final boolean collapsed)
	{
		return createUiGroup(id, GroupType.CSTIC_GROUP, status, FirstOrLastGroupType.INTERJACENT, collapsed, true);
	}

	protected UiGroupData createUiGroup(final String id, final GroupStatusType status, final GroupType groupType,
			final boolean collapsed)
	{
		return createUiGroup(id, groupType, status, FirstOrLastGroupType.INTERJACENT, collapsed, true);
	}

	protected UiGroupData createUiConflictGroup(final String id)
	{
		final UiGroupData conflictHeaderUiGroup = createUiGroup(id, GroupType.CONFLICT_HEADER, GroupStatusType.CONFLICT,
				FirstOrLastGroupType.FIRST, false, true);

		final List<UiGroupData> conflictGroups = new ArrayList<UiGroupData>();
		final ComparableConflictGroup conflict = new ComparableConflictGroup();
		conflict.setId("10");
		conflict.setDescription("Conflict10");
		conflict.setName("Conflict10");
		conflict.setGroupType(GroupType.CONFLICT);
		conflict.setConfigurable(true);
		conflict.setCollapsed(false);
		conflictGroups.add(conflict);
		conflictHeaderUiGroup.setSubGroups(conflictGroups);

		return conflictHeaderUiGroup;
	}

	protected UiGroupData createUiGroup(final String id, final GroupType groupType, final FirstOrLastGroupType firstOrLast,
			final boolean configurable)
	{
		return createUiGroup(id, groupType, GroupStatusType.DEFAULT, firstOrLast, true, configurable);
	}

	protected UiGroupData createUiGroup(final String id, final GroupType groupType, final GroupStatusType status,
			final FirstOrLastGroupType firstOrLast, final boolean collapsed, final boolean configurable)
	{
		final UiGroupData group = new UiGroupData();
		group.setId(id);
		group.setConfigurable(configurable);
		group.setGroupType(groupType);
		group.setFirstOrLastGroup(firstOrLast);
		group.setGroupStatus(status);
		group.setCollapsed(collapsed);
		return group;
	}

	protected CurrencyData createCurrencyData()
	{
		final CurrencyData data = new CurrencyData();
		data.setIsocode("EUR");
		return data;
	}

	protected UiStatus createUiStatus()
	{
		final UiStatus uiStatus = new UiStatus();
		final List<UiGroupStatus> uiGroups = new ArrayList<>();
		uiStatus.setGroups(uiGroups);
		return uiStatus;
	}

	protected List<Breadcrumb> createBreadcrumbs()
	{
		final List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(new Breadcrumb("productUlr", "test", null));
		return breadcrumbs;
	}

	protected void initializeFirstCall() throws Exception
	{
		servletContext = new MockServletContext();
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);

		request = new MockHttpServletRequest(servletContext);

		given(productFacade.getProductForCodeAndOptions(any(String.class), any(Collection.class))).willReturn(productData);
	}

	protected FieldError createErrorForCstic3()
	{
		return createErrorForCstic3("formattedValue");
	}

	protected FieldError createErrorForCstic3(final String fieldSuffix)
	{
		final FieldError error = new FieldError(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE,
				"groups[0].cstics[3]." + fieldSuffix, "aaa", false, new String[]
				{ "msg.key" }, null, "error msg");
		return error;
	}

	protected FieldError createErrorForSubgoup0Cstic0()
	{
		final FieldError error = new FieldError(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE,
				"groups[0].subGroups[0].cstics[0].formattedValue", "aaa", false, new String[]
				{ "msg.key" }, null, "error msg");
		return error;
	}

	protected FieldError createErrorForSubgoup0Cstic0FromConflict()
	{
		//errors originating from conflict groups/cstic have different path than the original cstic
		final FieldError error = new FieldError(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE,
				"groups[1].cstics[0].formattedValue", "aaa", false, new String[]
				{ "msg.key" }, null, "error msg");
		return error;
	}

	protected void createDataForAutoExpandTest()
	{
		configData = createConfigurationDataWithGeneralGroupOnly();
		configData.setAutoExpand(true);
		final CsticData csticWithError = new CsticData();
		csticWithError.setCsticStatus(CsticStatusType.ERROR);
		csticWithError.setName("csticWithError");
		csticList.add(csticWithError);
		final CsticData csticWithoutError = new CsticData();
		csticWithoutError.setCsticStatus(CsticStatusType.FINISHED);
		csticWithoutError.setName("csticWithoutError");
		csticList.add(csticWithoutError);
	}

	protected void createDataForHideExpandLongTextTest()
	{
		configData = createConfigurationDataWithGeneralGroupOnly();
		csticList.clear();
		final CsticData longTextShown = new CsticData();
		longTextShown.setName("longTextShown");
		longTextShown.setShowFullLongText(true);
		longTextShown.setKey("general.longTextShown");
		csticList.add(longTextShown);
		final CsticData longTextHidden = new CsticData();
		longTextHidden.setName("longTextHidden");
		longTextHidden.setShowFullLongText(false);
		longTextHidden.setKey("general.longTextHidden");
		csticList.add(longTextHidden);
	}

	protected void createImagesData(final ImageDataType type)
	{

		final List<ImageData> images = new ArrayList<>();
		for (int i = 1; i <= 5; i++)
		{
			final ImageData image = new ImageData();
			if (null == type)
			{
				if (i % 2 == 0)
				{
					image.setImageType(ImageDataType.GALLERY);
				}
				else
				{
					image.setImageType(ImageDataType.PRIMARY);
				}
			}
			else
			{
				image.setImageType(ImageDataType.PRIMARY);
			}
			image.setUrl("imageUrl_" + i);
			image.setFormat("format");
			final Integer galleryIndex = i;
			image.setGalleryIndex(galleryIndex);
			images.add(image);
		}
		given(productData.getImages()).willReturn(images);
	}

}
