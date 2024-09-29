/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.breadcrumb;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.DefaultResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.history.BrowseHistory;
import de.hybris.platform.acceleratorstorefrontcommons.history.BrowseHistoryEntry;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.servicelayer.model.ItemContextBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit test for {@link ProductConfigureBreadcrumbBuilder}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigureBreadcrumbBuilderTest
{

	private static final String VARIANT_CODE = "12345";
	private static final String CONFIG_CART_BREADCRUMB = "Configuration for Test";
	private static final String QUOTE_ID = "0001";
	private static final String ORDER_ID = "0002";
	private static final String PRODUCT_CODE = "1234";
	private static final String PRODUCT_URL = "1234";
	private static final String PRODUCT_VARIANT_URL = VARIANT_CODE;
	private static final String CATEGORY_URL = "0815";
	private static final String CONFIG_URL = "/configuratorPage/CPQCONFIGURATOR";
	private static final String CART_URL = "/cart";
	private static final String CONFIG_FROM_CART_URL = "/cart/1/configuration/CPQCONFIGURATOR";
	private static final String CONFIG_FROM_CART_EXISTING_DRAFT_URL = "/cart/1/configureOnDraft/CPQCONFIGURATOR";
	private static final OverviewUiData overviewUiData = new OverviewUiData();
	private static final Integer CART_ENTRY_NUMBER = Integer.valueOf(1);

	private ProductConfigureDummyBreadcrumbBuilder breadCrumbBuilder;
	private ResourceBreadcrumbBuilder accountBreadcrumbBuider;

	@Mock
	private ProductService productService;
	@Mock
	private ProductConfigProductAndCategoryHelper productAndCategoryHelper;



	@Before
	public void setup()
	{
		final UrlResolver<ProductModel> productModelUrlResolver = new ProductUrlResolverTest();
		final UrlResolver<CategoryModel> categoryModelUrlResolver = new CategoryModelUrlResolverTest();
		final BrowseHistory browseHistory = new BrowseHistoryTest();
		breadCrumbBuilder = new ProductConfigureDummyBreadcrumbBuilder();

		breadCrumbBuilder.setProductModelUrlResolver(productModelUrlResolver);
		breadCrumbBuilder.setCategoryModelUrlResolver(categoryModelUrlResolver);
		breadCrumbBuilder.setBrowseHistory(browseHistory);
		breadCrumbBuilder.setProductService(productService);
		breadCrumbBuilder.setProductAndCategoryHelper(productAndCategoryHelper);



		final PK catPk = PK.fromLong(600);
		final CategoryModel catModel = new CategoryModel(
				ItemContextBuilder.createMockContext(CategoryModel.class, catPk, Locale.US, new HashMap<>()));
		catModel.setName("Category");
		catModel.setCode("0815");
		catModel.setSupercategories(Collections.emptyList());

		final Map<String, Object> prodValues = new HashMap<>();
		final PK prodPk = PK.fromLong(500);
		final ProductModel productModel = new ProductModel(
				ItemContextBuilder.createMockContext(ProductModel.class, prodPk, Locale.US, prodValues));
		productModel.setSupercategories(Collections.singletonList(catModel));
		productModel.setName("Test");
		productModel.setCode(PRODUCT_CODE);

		final ProductModel variantModel = new ProductModel(
				ItemContextBuilder.createMockContext(ProductModel.class, prodPk, Locale.US, prodValues));
		variantModel.setSupercategories(Collections.singletonList(catModel));
		variantModel.setName("Test");
		variantModel.setCode(VARIANT_CODE);

		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(productModel);
		given(productService.getProductForCode(VARIANT_CODE)).willReturn(variantModel);

		overviewUiData.setOverviewMode(OverviewMode.CONFIGURATION_OVERVIEW);
		overviewUiData.setSourceDocumentId(QUOTE_ID);
		overviewUiData.setCartEntryNumber(null);
		accountBreadcrumbBuider = new DefaultResourceBreadcrumbBuilder();
		breadCrumbBuilder.setAccountBreadcrumbBuilder(accountBreadcrumbBuider);

		given(productAndCategoryHelper.getBaseProduct(productModel)).willReturn(productModel);
		given(productAndCategoryHelper.getBaseProduct(variantModel)).willReturn(productModel);
		given(productAndCategoryHelper.isValidProductCategory(catModel)).willReturn(true);

	}

	@Test
	public void getBreadCrumbTest()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getBreadcrumbs(PRODUCT_CODE);

		assertEquals(3, breadCrumbs.size());

		assertEquals(CATEGORY_URL, breadCrumbs.get(0).getUrl());
		assertEquals(PRODUCT_URL, breadCrumbs.get(1).getUrl());
		assertEquals(PRODUCT_URL + CONFIG_URL, breadCrumbs.get(2).getUrl());
	}

	@Test
	public void getBreadcrumbsTest_NoCartEntry()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getBreadcrumbs(PRODUCT_CODE, null);

		assertEquals(3, breadCrumbs.size());

		assertEquals(CATEGORY_URL, breadCrumbs.get(0).getUrl());
		assertEquals(PRODUCT_URL, breadCrumbs.get(1).getUrl());
		assertEquals(PRODUCT_URL + CONFIG_URL, breadCrumbs.get(2).getUrl());
	}

	@Test
	public void getBreadcrumbsTest_CartEntry()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getBreadcrumbs(PRODUCT_CODE, Integer.valueOf(1));

		assertNotNull(breadCrumbs);
		assertEquals(2, breadCrumbs.size());
		assertEquals(CART_URL, breadCrumbs.get(0).getUrl());
		assertNull(breadCrumbs.get(0).getLinkClass());
		assertEquals(CONFIG_FROM_CART_URL, breadCrumbs.get(1).getUrl());
		assertEquals("active", breadCrumbs.get(1).getLinkClass());
		assertEquals(CONFIG_CART_BREADCRUMB, breadCrumbs.get(1).getName());
	}


	@Test
	public void getBreadCrumbLinkClassTest()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getBreadcrumbs(PRODUCT_CODE);

		assertEquals(3, breadCrumbs.size());

		assertNull("Link class must be null for second item", breadCrumbs.get(1).getLinkClass());
		assertNotNull("Link class must not be null for third item", breadCrumbs.get(2).getLinkClass());
	}

	@Test
	public void getOverviewBreadCrumbTest()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getOverviewBreadcrumbs(PRODUCT_CODE, overviewUiData);

		assertEquals(4, breadCrumbs.size());

		assertEquals(CATEGORY_URL, breadCrumbs.get(0).getUrl());
		assertEquals(PRODUCT_URL, breadCrumbs.get(1).getUrl());
		assertEquals(PRODUCT_URL + CONFIG_URL, breadCrumbs.get(2).getUrl());
		assertEquals(PRODUCT_URL + "/configOverview", breadCrumbs.get(3).getUrl());
	}

	@Test
	public void getBreadcrumbsForConfigFromCartTest()
	{
		final Integer entryNumber = Integer.valueOf(1);
		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getBreadcrumbsForConfigFromCart(PRODUCT_CODE, entryNumber);
		assertNotNull(breadCrumbs);
		assertEquals(2, breadCrumbs.size());
		assertEquals(CART_URL, breadCrumbs.get(0).getUrl());
		assertNull(breadCrumbs.get(0).getLinkClass());
		assertEquals(CONFIG_FROM_CART_URL, breadCrumbs.get(1).getUrl());
		assertEquals("active", breadCrumbs.get(1).getLinkClass());
		assertEquals(CONFIG_CART_BREADCRUMB, breadCrumbs.get(1).getName());
	}

	@Test
	public void getBreadcrumbsForConfigOverviewFromCartTest()
	{
		final Integer entryNumber = Integer.valueOf(1);
		overviewUiData.setCartEntryNumber(entryNumber);
		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getOverviewBreadcrumbs(PRODUCT_CODE, overviewUiData);
		assertNotNull(breadCrumbs);
		assertEquals(3, breadCrumbs.size());
		assertEquals(CART_URL, breadCrumbs.get(0).getUrl());
		assertNull(breadCrumbs.get(0).getLinkClass());
		assertEquals(CONFIG_FROM_CART_URL, breadCrumbs.get(1).getUrl());
		assertNull(breadCrumbs.get(1).getLinkClass());
		assertEquals(CONFIG_CART_BREADCRUMB, breadCrumbs.get(1).getName());
		assertEquals(ProductConfigureBreadcrumbBuilder.DEFAULT_TEXT_OVERVIEW, breadCrumbs.get(2).getName());
		assertEquals("active", breadCrumbs.get(2).getLinkClass());
	}


	@Test
	public void getOverviewQuotationBreadCrumbTest()
	{

		overviewUiData.setOverviewMode(OverviewMode.QUOTATION_OVERVIEW);
		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getOverviewBreadcrumbs(PRODUCT_CODE, overviewUiData);

		assertEquals(3, breadCrumbs.size());
	}

	@Test
	public void testAccountBreadcrumbBuilder()
	{
		assertEquals(accountBreadcrumbBuider, breadCrumbBuilder.getAccountBreadcrumbBuilder());
	}

	@Test
	public void testGetQuoteOverviewBreadcrumbsFirst()
	{
		checkFirstBreadcrumb(getQuoteBreadcrumbs(), ProductConfigureBreadcrumbBuilder.DEFAULT_TEXT_QUOTES,
				ProductConfigureBreadcrumbBuilder.URL_MY_ACCOUNT_MY_QUOTES);
	}

	@Test
	public void testGetOrderOverviewBreadcrumbsFirst()
	{
		checkFirstBreadcrumb(getOrderBreadcrumbs(), ProductConfigureBreadcrumbBuilder.DEFAULT_TEXT_ORDERS,
				ProductConfigureBreadcrumbBuilder.URL_MY_ACCOUNT_ORDERS);
	}

	@Test
	public void testGetSavedCartOverviewBreadcrumbsFirst()
	{
		checkFirstBreadcrumb(getSavedCartBreadcrumbs(), ProductConfigureBreadcrumbBuilder.DEFAULT_TEXT_SAVED_CARTS,
				ProductConfigureBreadcrumbBuilder.URL_MY_ACCOUNT_SAVED_CARTS);
	}

	protected void checkFirstBreadcrumb(final List<Breadcrumb> breadcrumbs, final String breadCrumbFirstName,
			final String breadCrumbFirstUrl)
	{
		final Breadcrumb breadcrumbFirst = breadcrumbs.get(0);
		assertEquals(breadCrumbFirstName, breadcrumbFirst.getName());
		assertEquals(breadCrumbFirstUrl, breadcrumbFirst.getUrl());
	}

	@Test
	public void testGetQuoteOverviewBreadcrumbsSecond()
	{
		checkSecondBreadcrumb(getQuoteBreadcrumbs(), QUOTE_ID, ProductConfigureBreadcrumbBuilder.URL_MY_ACCOUNT_MY_QUOTES,
				"Quote ");
	}

	@Test
	public void testGetOrderOverviewBreadcrumbsSecond()
	{
		checkSecondBreadcrumb(getOrderBreadcrumbs(), ORDER_ID, ProductConfigureBreadcrumbBuilder.URL_MY_ACCOUNT_ORDER, "Order ");
	}

	@Test
	public void testSavedCartOverviewBreadcrumbsSecond()
	{
		checkSecondBreadcrumb(getSavedCartBreadcrumbs(), ORDER_ID, ProductConfigureBreadcrumbBuilder.URL_MY_ACCOUNT_SAVED_CARTS,
				"Saved Cart ");
	}

	protected void checkSecondBreadcrumb(final List<Breadcrumb> breadcrumbs, final String code, final String url,
			final String namePrefix)
	{
		final Breadcrumb breadcrumbSecond = breadcrumbs.get(1);
		assertEquals(namePrefix + code, breadcrumbSecond.getName());
		assertEquals(url + "/" + code, breadcrumbSecond.getUrl());
	}


	@Test
	public void testGetQuoteOverviewBreadcrumbsThird()
	{
		checkThirdBreadcrumb(getQuoteBreadcrumbs(), ProductConfigureBreadcrumbBuilder.DEFAULT_TEXT_DOCUMENT_OVERVIEW);
	}

	@Test
	public void testGetOrderOverviewBreadcrumbsThird()
	{
		checkThirdBreadcrumb(getOrderBreadcrumbs(), ProductConfigureBreadcrumbBuilder.DEFAULT_TEXT_DOCUMENT_OVERVIEW);
	}

	@Test
	public void testGetSavedCartOverviewBreadcrumbsThird()
	{
		checkThirdBreadcrumb(getSavedCartBreadcrumbs(), ProductConfigureBreadcrumbBuilder.DEFAULT_TEXT_DOCUMENT_OVERVIEW);
	}

	protected void checkThirdBreadcrumb(final List<Breadcrumb> breadcrumbs, final String name)
	{
		final Breadcrumb breadcrumbThird = breadcrumbs.get(2);
		assertTrue(breadcrumbThird.getUrl().isEmpty());
		assertEquals(name, breadcrumbThird.getName());
	}


	protected List<Breadcrumb> getQuoteBreadcrumbs()
	{
		final List<Breadcrumb> breadcrumbsForQuotation = breadCrumbBuilder.getOverviewBreadcrumbsForQuotation(QUOTE_ID);
		return checkBreadcrumbLength(breadcrumbsForQuotation);
	}

	protected List<Breadcrumb> getOrderBreadcrumbs()
	{
		final List<Breadcrumb> breadcrumbsForOrder = breadCrumbBuilder.getOverviewBreadcrumbsForOrder(ORDER_ID);
		return checkBreadcrumbLength(breadcrumbsForOrder);
	}

	protected List<Breadcrumb> getSavedCartBreadcrumbs()
	{
		final List<Breadcrumb> breadcrumbsForSavedCart = breadCrumbBuilder.getOverviewBreadcrumbsForSavedCart(ORDER_ID);
		return checkBreadcrumbLength(breadcrumbsForSavedCart);
	}

	protected List<Breadcrumb> checkBreadcrumbLength(final List<Breadcrumb> breadcrumbsForSavedCart)
	{
		assertNotNull(breadcrumbsForSavedCart);
		assertEquals(3, breadcrumbsForSavedCart.size());
		return breadcrumbsForSavedCart;
	}

	@Test
	public void getOverviewBreadCrumbLinkClassTest()
	{

		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getOverviewBreadcrumbs(PRODUCT_CODE, overviewUiData);

		assertEquals(4, breadCrumbs.size());
		assertEquals(CATEGORY_URL, breadCrumbs.get(0).getUrl());


	}

	@Test
	public void getVariantOverviewBreadCrumbTest()
	{

		final ProductData variantModel = createVariantModel();
		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getVariantOverviewBreadcrumbs(variantModel.getBaseProduct(),
				variantModel.getCode());

		assertEquals(4, breadCrumbs.size());

		assertEquals(CATEGORY_URL, breadCrumbs.get(0).getUrl());
		assertEquals(PRODUCT_URL, breadCrumbs.get(1).getUrl());
		assertEquals(PRODUCT_URL + CONFIG_URL, breadCrumbs.get(2).getUrl());
		assertEquals(PRODUCT_VARIANT_URL + "/variantOverview", breadCrumbs.get(3).getUrl());
	}

	@Test
	public void testGetVariantOverviewCartContextBreadCrumbs()
	{

		final ProductData variantModel = createVariantModel();
		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder
				.getVariantOverviewCartContextBreadcrumbs(variantModel.getBaseProduct(), variantModel.getCode(), CART_ENTRY_NUMBER);

		assertNotNull(breadCrumbs);

		assertEquals(3, breadCrumbs.size());

		assertEquals(CART_URL, breadCrumbs.get(0).getUrl());
		assertEquals(CONFIG_FROM_CART_EXISTING_DRAFT_URL, breadCrumbs.get(1).getUrl());
		assertEquals(null, breadCrumbs.get(2).getUrl());
		assertEquals(ProductConfigureBreadcrumbBuilder.DEFAULT_TEXT_OVERVIEW, breadCrumbs.get(2).getName());
	}

	@Test
	public void testGetBreadcrumbsForConfigFromCartOnExistingDraft()
	{
		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getBreadcrumbsForConfigFromCartOnExistingDraft(PRODUCT_CODE,
				CART_ENTRY_NUMBER);
		assertNotNull(breadCrumbs);
		assertEquals(2, breadCrumbs.size());
		assertEquals(CART_URL, breadCrumbs.get(0).getUrl());
		assertEquals(CONFIG_FROM_CART_EXISTING_DRAFT_URL, breadCrumbs.get(1).getUrl());
	}

	@Test
	public void getVariantOverviewBreadCrumbLinkClassTest()
	{

		final ProductData variantModel = createVariantModel();
		final List<Breadcrumb> breadCrumbs = breadCrumbBuilder.getVariantOverviewBreadcrumbs(variantModel.getBaseProduct(),
				variantModel.getCode());

		assertEquals(4, breadCrumbs.size());

		assertNull("Link class must be null for second item", breadCrumbs.get(1).getLinkClass());
		assertNull("Link class must be null for second item", breadCrumbs.get(2).getLinkClass());
		assertNotNull("Link class must not be null for third item", breadCrumbs.get(3).getLinkClass());
	}

	private ProductData createVariantModel()
	{
		final ProductData productData = new ProductData();

		productData.setName("TestVariant");
		productData.setCode(VARIANT_CODE);
		productData.setBaseProduct(PRODUCT_CODE);

		return productData;
	}

	private static class ProductUrlResolverTest implements UrlResolver<ProductModel>
	{

		@Override
		public String resolve(final ProductModel source)
		{
			return source.getCode();
		}
	}


	private static class CategoryModelUrlResolverTest implements UrlResolver<CategoryModel>
	{

		@Override
		public String resolve(final CategoryModel source)
		{
			return source.getCode();
		}
	}

	private static class BrowseHistoryTest implements BrowseHistory
	{

		private final Map<String, BrowseHistoryEntry> history = new HashMap<>();

		@Override
		public void addBrowseHistoryEntry(final BrowseHistoryEntry browseHistoryEntry)
		{
			history.put(browseHistoryEntry.getUrl(), browseHistoryEntry);
		}

		@Override
		public BrowseHistoryEntry findEntryMatchUrlEndsWith(final String url)
		{
			return history.get(url);
		}

	}


	@Test
	public void testGetLocalizedTextOrDefault_notLocalized()
	{
		final String localizedText = breadCrumbBuilder.getLocalizedTextOrDefault("123", "default");
		assertEquals("default", localizedText);
	}


	@Test
	public void testGetLocalizedTextOrDefault_localized()
	{

		breadCrumbBuilder.addLocalizationForTest("123", "localizedValue");
		final String localizedText = breadCrumbBuilder.getLocalizedTextOrDefault("123", "default");
		assertEquals("localizedValue", localizedText);
	}

	@Test
	public void testFormatDefaultText()
	{
		final String text = breadCrumbBuilder.formatDefaultText("{0} {1}", new Object[]
		{ "test", Integer.valueOf(123) });
		assertEquals("test 123", text);
	}

	@Test
	public void testFormatDefaultText_nullArgs()
	{
		final String text = breadCrumbBuilder.formatDefaultText("{0} {1}", null);
		assertEquals("{0} {1}", text);
	}



	@Test
	public void testFormatDefaultText_emptyArgs()
	{
		final String text = breadCrumbBuilder.formatDefaultText("{0} {1}", new Object[] {});
		assertEquals("{0} {1}", text);
	}
}
