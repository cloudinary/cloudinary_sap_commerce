/*
 * Copyright (c) 2025 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.model.ProductConfigPageModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ConfigureProductControllerTest extends AbstractProductConfigControllerTCBase
{


	@Mock
	protected BindingResult bindingResults;
	@Mock
	protected ConfigurationData configData2;
	@Mock
	protected RedirectAttributes redirectAttributes;
	@Mock
	private HttpServletRequest mockedRequest;

	// will inject cmsPageService and pageTitleResolver, as well. For both no setter exists
	@InjectMocks
	private ConfigureProductController classUnderTest;

	@Before
	public void setUp()
	{
		injectMocks(classUnderTest);

		kbKey = createKbKey();
		csticList = createCsticsList();
		configData = createConfigurationDataWithGeneralGroupOnly();
	}



	@Test
	public void testUpdateConfigureProductRedirectIsCorrect() throws Exception
	{
		initializeFirstCall();
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(new UiStatus());
		given(configFacade.getConfiguration(Mockito.nullable(KBKeyData.class))).willReturn(configData);
		request.setAttribute("de.hybris.platform.acceleratorcms.utils.SpringHelper.bean.requestContextData",
				new RequestContextData());
		final String forward = classUnderTest.configureProductGET(PRODUCT_CODE, model, request, redirectAttributes);
		assertEquals("addon:/ysapproductconfigaddon/pages/configuration/configurationPage", forward);
	}

	@Test
	public void testBreadcrumbIsSet() throws Exception
	{
		initializeFirstCall();
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(new UiStatus());
		given(configFacade.getConfiguration(Mockito.nullable(KBKeyData.class))).willReturn(configData);
		request.setAttribute("de.hybris.platform.acceleratorcms.utils.SpringHelper.bean.requestContextData",
				new RequestContextData());
		classUnderTest.configureProductGET(PRODUCT_CODE, model, request, redirectAttributes);
		Mockito.verify(model).addAttribute(Mockito.eq(WebConstants.BREADCRUMBS_KEY), Mockito.any(List.class));
	}

	@Test
	public void testProductDataIsSet() throws Exception
	{
		initializeFirstCall();
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(new UiStatus());
		given(configFacade.getConfiguration(Mockito.nullable(KBKeyData.class))).willReturn(configData);
		request.setAttribute("de.hybris.platform.acceleratorcms.utils.SpringHelper.bean.requestContextData",
				new RequestContextData());
		classUnderTest.configureProductPOST(PRODUCT_CODE, model, request, redirectAttributes);
		Mockito.verify(model).addAttribute(Mockito.eq("product"), Mockito.any(ProductData.class));
	}


	@Test
	public void testRemoveNullCStics_InGroup()
	{
		final String name = "XYZ";
		final List<CsticData> dirtyList = createDirtyListWithCstic(name);

		final List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData group = new UiGroupData();
		group.setCstics(dirtyList);
		groups.add(group);
		assertTrue("Must be 2 groups", groups.get(0).getCstics().size() > 1);

		classUnderTest.removeNullCstics(groups);
		assertEquals(1, groups.get(0).getCstics().size());
		Assert.assertEquals("Wrong cstic found", name, groups.get(0).getCstics().get(0).getName());
	}



	@Test
	public void testRemoveNullCStics_retractionTriggered()
	{
		final String name = "XYZ";
		final List<CsticData> dirtyList = createDirtyListWithCstic(name);
		dirtyList.get(0).setRetractTriggered(true);

		final List<UiGroupData> groups = new ArrayList<>();
		final UiGroupData group = new UiGroupData();
		group.setCstics(dirtyList);
		groups.add(group);
		assertTrue("Must be 2 groups", groups.get(0).getCstics().size() > 1);

		classUnderTest.removeNullCstics(groups);
		assertEquals(2, groups.get(0).getCstics().size());
		Assert.assertEquals("Wrong cstic found", name, groups.get(0).getCstics().get(1).getName());
		Assert.assertEquals("Wrong cstic found", "readOnly", groups.get(0).getCstics().get(0).getName());
	}

	protected List<CsticData> createDirtyListWithCstic(final String name)
	{
		final CsticData csticWithNameAndValue = new CsticData();
		csticWithNameAndValue.setName(name);
		csticWithNameAndValue.setValue("value");
		csticWithNameAndValue.setType(UiType.STRING);
		final CsticData readOnly = new CsticData();
		readOnly.setName("readOnly");
		readOnly.setType(UiType.READ_ONLY);
		final List<CsticData> dirtyList = new ArrayList<>();
		dirtyList.add(readOnly);
		dirtyList.add(csticWithNameAndValue);
		dirtyList.add(new CsticData());
		return dirtyList;
	}

	@Test
	public void testRemoveNullCStics_InSubGroup()
	{
		final String name = "XYZ";
		final List<CsticData> dirtyList = createDirtyListWithCstic(name);

		final List<UiGroupData> subGroups = new ArrayList<>();
		UiGroupData group = new UiGroupData();
		group.setCstics(dirtyList);
		subGroups.add(group);

		final ArrayList<UiGroupData> groups = new ArrayList<>();
		group = new UiGroupData();
		group.setSubGroups(subGroups);
		groups.add(group);

		assertTrue("Must be 2 groups", groups.get(0).getSubGroups().get(0).getCstics().size() > 1);

		classUnderTest.removeNullCstics(groups);

		final UiGroupData uiGroup = groups.get(0).getSubGroups().get(0);
		assertEquals(1, uiGroup.getCstics().size());
		Assert.assertSame(name, uiGroup.getCstics().get(0).getName());
	}

	@Test
	public void testLoggingOfConflictCheckDeviation() throws Exception
	{
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode("MY_PRODUCT");
		given(configData2.getKbKey()).willReturn(kbKey);

		// Test 1: Configuration is consistent and complete, no errors are returned from ConflictChecker -> No Logging.
		given(Boolean.valueOf(configData2.isComplete())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(configData2.isConsistent())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.FALSE);
		classUnderTest.logConfigurationCheckDeviation(bindingResults, configData2);
		// getKbKey() is only called if a log-statement is written
		verify(configData2, times(0)).getKbKey();

		// Test 2: Configuration is incomplete, errors are returned from ConflictChecker -> No Logging.
		given(Boolean.valueOf(configData2.isComplete())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(configData2.isConsistent())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.TRUE);
		classUnderTest.logConfigurationCheckDeviation(bindingResults, configData2);
		// getKbKey() is only called if a log-statement is written
		verify(configData2, times(0)).getKbKey();

		// Test 3: Configuration is inconsistent, errors are returned from ConflictChecker -> No Logging.
		given(Boolean.valueOf(configData2.isComplete())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(configData2.isConsistent())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.TRUE);
		classUnderTest.logConfigurationCheckDeviation(bindingResults, configData2);
		// getKbKey() is only called if a log-statement is written
		verify(configData2, times(0)).getKbKey();

		// Test 4: Configuration is inconsistent and incomplete, errors are returned from ConflictChecker -> No Logging.
		given(Boolean.valueOf(configData2.isComplete())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(configData2.isConsistent())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.TRUE);
		classUnderTest.logConfigurationCheckDeviation(bindingResults, configData2);
		// getKbKey() is only called if a log-statement is written
		verify(configData2, times(0)).getKbKey();

		// Test 5: Configuration is incomplete, but no errors are returned from ConflictChecker -> Logging first time.
		given(Boolean.valueOf(configData2.isComplete())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(configData2.isConsistent())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.FALSE);
		classUnderTest.logConfigurationCheckDeviation(bindingResults, configData2);
		// getKbKey() is only called if a log-statement is written
		verify(configData2, times(1)).getKbKey();

		// Test 6: Configuration is inconsistent, but no errors are returned from ConflictChecker -> Logging second time.
		given(Boolean.valueOf(configData2.isComplete())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(configData2.isConsistent())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.FALSE);
		classUnderTest.logConfigurationCheckDeviation(bindingResults, configData2);
		// getKbKey() is only called if a log-statement is written
		verify(configData2, times(2)).getKbKey();

		// Test 7: Configuration is inconsistent and incomplete, but no errors are returned from ConflictChecker -> Logging third time.
		given(Boolean.valueOf(configData2.isComplete())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(configData2.isConsistent())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(bindingResults.hasErrors())).willReturn(Boolean.FALSE);
		classUnderTest.logConfigurationCheckDeviation(bindingResults, configData2);
		// getKbKey() is only called if a log-statement is written
		verify(configData2, times(3)).getKbKey();

	}

	@Test
	public void testCleanUpSessionAttributeWithAttributeExist()
	{
		classUnderTest.cleanUpSessionAttribute(PRODUCT_CODE);

		verify(configurationProductLinkStrategy, times(1)).removeConfigIdForProduct(PRODUCT_CODE);
	}

	@Test
	public void testGetPageId()
	{
		final String pageId = classUnderTest.getPageId();
		assertEquals(ConfigureProductController.CMS_PC_PAGE_ID, pageId);
	}

	@Test
	public void testGetPageType()
	{
		final String pageType = classUnderTest.getPageType();
		assertEquals(ConfigureProductController.CMS_PC_PAGE_TYPE, pageType);
	}

	@Test
	public void testPopulateCMSAttributes() throws CMSItemNotFoundException
	{
		given(cmsPageService.getPageForId(ConfigureProductController.CMS_PC_PAGE_ID)).willReturn(new ProductConfigPageModel());
		classUnderTest.populateCMSAttributes(model);
		Mockito.verify(model).addAttribute(Mockito.eq(AbstractProductConfigController.CMS_PAGE_TYPE),
				Mockito.eq(ConfigureProductController.CMS_PC_PAGE_TYPE));
		Mockito.verify(model).addAttribute(Mockito.eq(AbstractProductConfigController.CMS_PAGE_MODEL),
				Mockito.any(ProductConfigPageModel.class));
	}

	@Test
	public void testAddRedirectAttributes()
	{
		final Map<String, Object> modelMap = createModelMap();
		given(model.asMap()).willReturn(modelMap);
		classUnderTest.addFilteredRedirectAttributes(model, redirectAttributes);
		verify(redirectAttributes, times(11)).addFlashAttribute(any(), any());
	}

	@Test
	public void testAddRedirectAttributesEmptyModelMap()
	{
		given(model.asMap()).willReturn(new HashMap());
		classUnderTest.addFilteredRedirectAttributes(model, redirectAttributes);
		verify(redirectAttributes, times(0)).addFlashAttribute(any(), any());
	}

	@Test
	public void testAddRedirectAttributesNoEntriesToFilter()
	{
		final Map<String, Object> modelMap = new HashMap();
		modelMap.put("key1", "value1");
		modelMap.put("key2", "value2");
		given(model.asMap()).willReturn(modelMap);
		classUnderTest.addFilteredRedirectAttributes(model, redirectAttributes);
		verify(redirectAttributes, times(0)).addFlashAttribute(any(), any());
	}


	private Map<String, Object> createModelMap()
	{
		final Map<String, Object> modelMap = new HashMap();
		modelMap.put("key1", "value1");
		modelMap.put("key2", "value2");
		modelMap.put(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE, "configDummy");
		modelMap.put(BindingResult.MODEL_KEY_PREFIX + SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE, "bindingResultDummy");
		modelMap.put(AbstractProductConfigController.GALLERY_IMAGES, "galleryImagesDummy");
		modelMap.put(AbstractProductConfigController.METATAGS, "metaTagsDummy");
		modelMap.put(GlobalMessages.CONF_MESSAGES_HOLDER, "confirmationMessagesDummy");
		modelMap.put(GlobalMessages.INFO_MESSAGES_HOLDER, "infoMessagesDummy");
		modelMap.put("key3", "value3");
		modelMap.put(SapproductconfigfrontendWebConstants.PRODUCT_ATTRIBUTE, "bindingResultDummy");
		modelMap.put(AbstractProductConfigController.CMS_PAGE_TYPE, "pageTypeDummy");
		modelMap.put(GlobalMessages.ERROR_MESSAGES_HOLDER, "erroMessagesDummy");
		modelMap.put("key4", "value4");
		modelMap.put("key5", "value5");
		modelMap.put(AbstractPageController.CMS_PAGE_MODEL, "pageModelDummy");
		modelMap.put(AbstractPageController.CMS_PAGE_TITLE, "pageTitleDummy");
		return modelMap;
	}
}
