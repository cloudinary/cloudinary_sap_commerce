/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.interceptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.ui.ModelMap;

import com.hybris.merchandising.constants.MerchandisingConstants;
import com.hybris.merchandising.constants.MerchandisingaddonConstants;
import com.hybris.merchandising.context.ContextRepository;
import com.hybris.merchandising.context.ContextService;
import com.hybris.merchandising.context.impl.DefaultContextRepository;
import com.hybris.merchandising.context.impl.DefaultContextService;
import com.hybris.merchandising.model.Breadcrumbs;
import com.hybris.merchandising.model.ContextMap;
import com.hybris.merchandising.model.Facet;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorservices.storefront.data.JavaScriptVariableData;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.strategies.ConsumedDestinationLocatorStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.facetdata.BreadcrumbData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.session.impl.DefaultSession;
import de.hybris.platform.site.BaseSiteService;

/**
 * Test class for {@link HybrisMerchandisingBeforeViewHandlerAdaptee}
 *
 */
@UnitTest
public class HybrisMerchandisingBeforeViewHandlerAdapteeTest
{
	private static final String CATEGORY_CODE = "MyCode";
	private static final String BASE_SITE_ID = "apparel-uk";
	private static final String SEARCH_PAGE_DATA = "searchPageData";
	private static final String LANGUAGE = "en";
	private static final String TENANT = "mytenant";

	private HybrisMerchandisingBeforeViewHandlerAdaptee adaptee;
	private ContextService contextService;
	private BaseSiteService baseSiteService;
	private ConsumedDestinationLocatorStrategy consumedDestinationLocatorStrategy;
	private SessionService sessionService;
	private ContextRepository contextRepo;
	private static final String PRODUCT_ID = "myproduct123";

	@Before
	public void setUp()
	{
		adaptee = new HybrisMerchandisingBeforeViewHandlerAdaptee();
		contextService = Mockito.mock(DefaultContextService.class);
		adaptee.setContextService(contextService);

		contextRepo = new DefaultContextRepository();
		Mockito.when(contextService.getContextRepository()).thenReturn(contextRepo);

		final BaseSiteModel baseSiteModel = Mockito.mock(BaseSiteModel.class);
		Mockito.when(baseSiteModel.getUid()).thenReturn(BASE_SITE_ID);
		baseSiteService = Mockito.mock(BaseSiteService.class);
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		adaptee.setBaseSiteService(baseSiteService);

		DestinationTargetModel destinationTargetModel = Mockito.mock(DestinationTargetModel.class);
		Mockito.when(destinationTargetModel.getId()).thenReturn(TENANT);

		ConsumedDestinationModel consumedDestinationModel = Mockito.mock(ConsumedDestinationModel.class);
		Mockito.when(consumedDestinationModel.getDestinationTarget()).thenReturn(destinationTargetModel);

		consumedDestinationLocatorStrategy = Mockito.mock(ConsumedDestinationLocatorStrategy.class);
		adaptee.setConsumedDestinationLocatorStrategy(consumedDestinationLocatorStrategy);
		Mockito.when(consumedDestinationLocatorStrategy.lookup(eq(MerchandisingaddonConstants.STRATEGY_SERVICE))).thenReturn(consumedDestinationModel);


		sessionService = Mockito.mock(SessionService.class);
		adaptee.setSessionService(sessionService);
	}

	@Test
	public void testStoreBaseSite()
	{
		adaptee.storeBaseSite();
		final String site = (String) contextService.getContextRepository()
									.get(MerchandisingConstants.SITE_ID)
									.getProperty(MerchandisingConstants.SITE_ID);
		assertNotNull("Expected site to not be null", site);
		assertEquals("Expected site to be", BASE_SITE_ID, site);
	}

	@Test
	public void testStoreLanguage()
	{
		final Session mockSession = new EnhancedMockSession();
		final LanguageModel englishLanguage = Mockito.mock(LanguageModel.class);
		Mockito.when(englishLanguage.getIsocode()).thenReturn(LANGUAGE);
		mockSession.setAttribute(MerchandisingConstants.LANGUAGE, englishLanguage);
		adaptee.storeLanguage(mockSession);

		final String language = (String) contextService.getContextRepository()
				.get(MerchandisingConstants.LANGUAGE)
				.getProperty(MerchandisingConstants.LANGUAGE);
		assertNotNull("Expected language to not be null", language);
		assertEquals("Expected language to be set", LANGUAGE, language);
	}

	@Test
	public void testStoreTenant()
	{
		final Session mockSession = new EnhancedMockSession();
		adaptee.storeTenant(mockSession);
		final String tenant = (String) contextService.getContextRepository()
				.get(MerchandisingConstants.PAGE_CONTEXT_TENANT)
				.getProperty(MerchandisingConstants.PAGE_CONTEXT_TENANT);
		assertNotNull("Expected tenant to not be null", tenant);
		assertEquals("Expected tenant to be set", TENANT, tenant);
	}

	@Test
	public void testStoreBreadcrumbs()
	{
		final Session mockSession = new EnhancedMockSession();
		final ModelMap mockModelMap = Mockito.mock(ModelMap.class);
		final List<Breadcrumb> breadcrumbs = new ArrayList<>();
		final Breadcrumb b1 = new Breadcrumb("https://localhost/bread1", "bread1", "category");
		breadcrumbs.add(b1);

		final Breadcrumb b2 = new Breadcrumb("https://localhost/bread2", "bread2", "category");
		breadcrumbs.add(b2);

		final Breadcrumbs generatedBreadcrumbs = adaptee.getBreadcrumbs(breadcrumbs);
		assertNotNull("Expected generated breadcrumbs to not be null", generatedBreadcrumbs);

		Mockito.when(mockModelMap.get("breadcrumbs")).thenReturn(breadcrumbs);
		adaptee.storeBreadcrumbs(mockSession, mockModelMap);
		final Breadcrumbs breadcrumbTrail = (Breadcrumbs) contextService.getContextRepository()
				.get(MerchandisingConstants.PAGE_CONTEXT_BREADCRUMBS)
				.getProperty(MerchandisingConstants.PAGE_CONTEXT_BREADCRUMBS);
		assertNotNull("Expected breadcrumbs to be set", breadcrumbTrail);
		assertEquals("Expected trail to contain 2 breadcrumbs", 2, breadcrumbTrail.getListOfBreadcrumbs().size());
	}

	@Test
	public void testStoreFacets()
	{
		final Session mockSession = new EnhancedMockSession();
		final ModelMap mockModelMap = Mockito.mock(ModelMap.class);

		final ProductCategorySearchPageData data = new ProductCategorySearchPageData();
		final BreadcrumbData bread1 = new BreadcrumbData();
		bread1.setFacetCode("bread1");
		bread1.setFacetName("bread1");
		bread1.setFacetValueName("bread1value");
		bread1.setFacetValueCode("bread1value");

		final BreadcrumbData bread2 = new BreadcrumbData();
		bread2.setFacetCode("bread2");
		bread2.setFacetName("bread2");
		bread2.setFacetValueName("bread2value");
		bread2.setFacetValueCode("bread2value");

		data.setBreadcrumbs(new ArrayList<BreadcrumbData>(Arrays.asList(bread1, bread2)));
		Mockito.when(mockModelMap.get(SEARCH_PAGE_DATA)).thenReturn(data);
		adaptee.storeFacets(mockSession, mockModelMap);

		@SuppressWarnings("unchecked")
		final List<Facet> facets = (List<Facet>) contextService.getContextRepository()
				.get(MerchandisingConstants.PAGE_CONTEXT_FACETS)
				.getProperty(MerchandisingConstants.PAGE_CONTEXT_FACETS);
		assertNotNull("Expected facets to not be null", facets);
	}

	@Test
	public void testStoreCategory()
	{
		final Session mockSession = new EnhancedMockSession();
		final ModelMap mockModelMap = Mockito.mock(ModelMap.class);

		final String categoryCode = "cat1";
		final CategoryModel cm = Mockito.mock(CategoryModel.class);
		Mockito.when(cm.getCode()).thenReturn(categoryCode);
		final RequestContextData requestContext = new RequestContextData();
		requestContext.setCategory(cm);

		final ProductCategorySearchPageData data = new ProductCategorySearchPageData();
		Mockito.when(mockModelMap.get(SEARCH_PAGE_DATA)).thenReturn(data);

		adaptee.storeCategory(requestContext, mockSession, mockModelMap);
		final String storedCategoryCode = (String) contextService.getContextRepository()
				.get(MerchandisingConstants.PAGE_CONTEXT_CATEGORY)
				.getProperty(MerchandisingConstants.PAGE_CONTEXT_CATEGORY);
		assertEquals("Expected category code to match", categoryCode, storedCategoryCode);
	}

	@Test
	public void testStoreCategoryNotSearchPage()
	{
		final String expectedCategoryCode = "code2";
		final Session mockSession = new EnhancedMockSession();
		final ModelMap mockModelMap = Mockito.mock(ModelMap.class);

		final String categoryCode = "cat1";
		final CategoryModel cm = Mockito.mock(CategoryModel.class);
		Mockito.when(cm.getCode()).thenReturn(categoryCode);
		final RequestContextData requestContext = new RequestContextData();
		requestContext.setCategory(cm);

		final Breadcrumbs bc = new Breadcrumbs();
		bc.addBreadcrumb("url1", "name1");
		bc.addBreadcrumb("url2", "name2");
		bc.addBreadcrumb("url3", "name3");
		mockSession.setAttribute(MerchandisingConstants.PAGE_CONTEXT_BREADCRUMBS, bc);

		final ProductData product = Mockito.mock(ProductData.class);
		final CategoryData mockCategoryData = Mockito.mock(CategoryData.class);
		Mockito.when(mockCategoryData.getName()).thenReturn("name2");
		Mockito.when(mockCategoryData.getCode()).thenReturn(expectedCategoryCode);
		Mockito.when(mockCategoryData.getUrl()).thenReturn("url2");
		Mockito.when(product.getCategories()).thenReturn(Collections.singletonList(mockCategoryData));
		Mockito.when(product.getCode()).thenReturn(PRODUCT_ID);
		Mockito.when(mockModelMap.get(MerchandisingConstants.PRODUCT)).thenReturn(product);

		adaptee.storeCategory(requestContext, mockSession, mockModelMap);
		assertEquals("Expected categoryCode to match", expectedCategoryCode, mockSession.getAttribute(MerchandisingConstants.PAGE_CONTEXT_CATEGORY));
	}

	@Test
	public void testStoreCategoryNotSearchPageNoCategories()
	{
		final Session mockSession = new EnhancedMockSession();
		final ModelMap mockModelMap = Mockito.mock(ModelMap.class);

		final String categoryCode = "cat1";
		final CategoryModel cm = Mockito.mock(CategoryModel.class);
		Mockito.when(cm.getCode()).thenReturn(categoryCode);
		final RequestContextData requestContext = new RequestContextData();
		requestContext.setCategory(cm);

		final Breadcrumbs bc = new Breadcrumbs();
		bc.addBreadcrumb("url1", "name1");
		bc.addBreadcrumb("url2", "name2");
		bc.addBreadcrumb("url3", "name3");
		mockSession.setAttribute(MerchandisingConstants.PAGE_CONTEXT_BREADCRUMBS, bc);

		final ProductData product = Mockito.mock(ProductData.class);
		Mockito.when(product.getCategories()).thenReturn(null);
		Mockito.when(product.getCode()).thenReturn(PRODUCT_ID);
		Mockito.when(mockModelMap.get(MerchandisingConstants.PRODUCT)).thenReturn(product);

		adaptee.storeCategory(requestContext, mockSession, mockModelMap);
		assertNull("Expected categoryCode to match", mockSession.getAttribute(MerchandisingConstants.PAGE_CONTEXT_CATEGORY));
	}

	@Test
	public void testRetrieveHybrisConvertJSAddOnVariables()
	{
		final ModelMap mockModelMap = Mockito.mock(ModelMap.class);
		final List<JavaScriptVariableData> variables = adaptee.retrieveHybrisConvertJSAddOnVariables(mockModelMap);
		assertNotNull("Expected variables to not be null", variables);

		final Map<String, List<JavaScriptVariableData>> populatedVariables = new HashMap<>();

		final JavaScriptVariableData jsData = new JavaScriptVariableData();
		jsData.setQualifier("qualifier");
		jsData.setValue("value");

		final List<JavaScriptVariableData> data = new ArrayList<>(Arrays.asList(jsData));
		populatedVariables.put(MerchandisingaddonConstants.EXTENSIONNAME, data);
		Mockito.when(mockModelMap.get(MerchandisingConstants.JS_ADDONS_CONTEXT_VARIABLES)).thenReturn(populatedVariables);

		final List<JavaScriptVariableData> retrievedVariables = adaptee.retrieveHybrisConvertJSAddOnVariables(mockModelMap);
		assertEquals("Expected retrieved variables size to be same as data", data.size(), retrievedVariables.size());
	}

	@Test
	public void testPopulateJSAddonVariables()
	{
		final ModelMap mockModelMap = Mockito.mock(ModelMap.class);
		Mockito.when(sessionService.getAttribute(MerchandisingConstants.CONTEXT_STORE_KEY)).thenReturn(contextRepo);
		adaptee.populateJSAddOnVariables(mockModelMap);

		final ContextMap language = new ContextMap();
		language.addProperty(MerchandisingConstants.LANGUAGE, "en");
		contextRepo.put(MerchandisingConstants.LANGUAGE, language);

		final ContextMap site = new ContextMap();
		site.addProperty(MerchandisingConstants.SITE_ID, "apparel-uk");
		contextRepo.put(MerchandisingConstants.SITE_ID, site);

		adaptee.populateJSAddOnVariables(mockModelMap);
	}

	@Test
	public <STATE, RESULT, CATEGORY> void testStoreNavigatedCategory()
	{
		final RequestContextData mockRequestContextData = Mockito.mock(RequestContextData.class);
		final Session mockSession = new EnhancedMockSession();
		final ModelMap mockModelMap = Mockito.mock(ModelMap.class);

		final ProductCategorySearchPageData<STATE, RESULT, CATEGORY> searchPageData = new ProductCategorySearchPageData<>();
		Mockito.when(mockModelMap.get(SEARCH_PAGE_DATA)).thenReturn(searchPageData);

		final CategoryModel category = new CategoryModel();
		category.setCode(CATEGORY_CODE);
		Mockito.when(mockRequestContextData.getCategory()).thenReturn(category);

		final ContentPageModel homePage = new ContentPageModel();
		homePage.setUid("homepage");
		homePage.setHomepage(true);

		final ContentPageModel otherPage = new ContentPageModel();
		otherPage.setUid("categorypage");
		otherPage.setHomepage(false);

		//Test storing a category when we have the category provided and are on a search result page.
		adaptee.storeCategory(mockRequestContextData, mockSession, mockModelMap);
		Map<String, Object> attributes = mockSession.getAllAttributes();
		assertEquals("Attributes should have 1 value in it as we have a category", 1, attributes.values().size());
		assertNotNull("A result should be returned for key",
				attributes.get(MerchandisingConstants.PAGE_CONTEXT_CATEGORY));
		assertEquals("Attribute value should be 'MyCode'", CATEGORY_CODE,
				attributes.get(MerchandisingConstants.PAGE_CONTEXT_CATEGORY));

		//Test being on a search result page without a category.
		Mockito.when(mockRequestContextData.getCategory()).thenReturn(null);
		adaptee.storeCategory(mockRequestContextData, mockSession, mockModelMap);
		attributes = mockSession.getAllAttributes();
		assertEquals("Attributes should have 0 values in it as we have a null category", 0, attributes.values().size());

		//Test being on a page other than a search results page but not the home page.
		mockSession.setAttribute(MerchandisingConstants.PAGE_CONTEXT_CATEGORY, "TESTVALUE");
		Mockito.when(mockModelMap.get("cmsPage")).thenReturn(otherPage);
		Mockito.when(mockModelMap.get(SEARCH_PAGE_DATA)).thenReturn(null);
		adaptee.storeCategory(mockRequestContextData, mockSession, mockModelMap);
		attributes = mockSession.getAllAttributes();
		assertEquals(
				"Attributes should have 1 value in it as we did not have an instance of searchPageData but are not on homepage", 1,
				attributes.values().size());

		//Test being not on the homepage. This should effectively be a no-op case.
		Mockito.when(mockModelMap.get("cmsPage")).thenReturn(searchPageData);
		Mockito.when(mockModelMap.get(SEARCH_PAGE_DATA)).thenReturn(null);
		adaptee.storeCategory(mockRequestContextData, mockSession, mockModelMap);
		attributes = mockSession.getAllAttributes();
		assertEquals(
				"Attributes should have 0 value in it as we did not have an instance of searchPageData but are not on homepage", 1,
				attributes.values().size());

		//Test being on the homepage.
		Mockito.when(mockModelMap.get("cmsPage")).thenReturn(homePage);
		Mockito.when(mockModelMap.get(SEARCH_PAGE_DATA)).thenReturn(homePage);
		adaptee.storeCategory(mockRequestContextData, mockSession, mockModelMap);
		attributes = mockSession.getAllAttributes();
		assertEquals(
				"Attributes should have 0 values in it as we did not have an instance of searchPageData but are on homepage",
				0,
				attributes.values().size());
	}

	@Test
	public <STATE> void testGetFacetBreadcrumbs() {
		final String facetCode = "bd1_code";
		final BreadcrumbData<STATE> bd1 = new BreadcrumbData<>();
		bd1.setFacetCode("bd1_code");
		bd1.setFacetName("bd1_name");
		bd1.setFacetValueName("bd1_valuename");
		bd1.setFacetValueCode("bd1_valuecode");

		final List<BreadcrumbData<STATE>> breadcrumbData = new ArrayList<>(Arrays.asList(bd1));
		final Map<String, Facet> facets = adaptee.getFacetBreadcrumbs(breadcrumbData);
		assertNotNull("Expected facets to not be null", facets);
		facets.forEach((key, facet) -> {
			assertEquals("Expected facet code to be set", facetCode, facet.getCode());
		});
	}

	@Test
	public void testStoreCurrentlyViewedProduct()
	{
		final ProductData product = Mockito.mock(ProductData.class);
		Mockito.when(product.getCode()).thenReturn(PRODUCT_ID);
		final ModelMap mockModelMap = Mockito.mock(ModelMap.class);
		Mockito.when(mockModelMap.get(MerchandisingConstants.PRODUCT)).thenReturn(product);
		adaptee.storeCurrentlyViewedProductCode(mockModelMap);
		assertEquals("Expected product ID to match the one stored in context repository", PRODUCT_ID,
				contextRepo.get(MerchandisingConstants.PRODUCT).getProperty(MerchandisingConstants.PRODUCT));
		contextRepo.clear();

		final ModelMap nonProductMap = Mockito.mock(ModelMap.class);
		adaptee.storeCurrentlyViewedProductCode(nonProductMap);
		assertNull("Expected context repository to not store the product ID",
				contextRepo.get(MerchandisingConstants.PRODUCT));
	}

	/**
	 * EnhancedMockSession is an enhanced implementation of the default hybris mock session. It does not extend the
	 * MockSession as we are unable to get visibility of the attributes map.
	 *
	 */
	class EnhancedMockSession extends DefaultSession
	{
		private static final long serialVersionUID = 1L;
		private long sessionIdCounter = 1L;
		private final Map<String, Object> attributes = new ConcurrentHashMap<>();
		private final String sessionId;

		@Override
		public String getSessionId()
		{
			return this.sessionId;
		}

		public EnhancedMockSession()
		{
			this.sessionId = String.valueOf(this.sessionIdCounter++);
		}

		@Override
		public Map<String, Object> getAllAttributes()
		{
			return Collections.unmodifiableMap(this.attributes);
		}

		@Override
		public Object getAttribute(final String name)
		{
			return this.attributes.get(name);
		}

		@Override
		public void setAttribute(final String name, final Object value)
		{
			this.attributes.put(name, value);
		}

		@Override
		public void removeAttribute(final String name)
		{
			this.attributes.remove(name);
		}
	}
}
