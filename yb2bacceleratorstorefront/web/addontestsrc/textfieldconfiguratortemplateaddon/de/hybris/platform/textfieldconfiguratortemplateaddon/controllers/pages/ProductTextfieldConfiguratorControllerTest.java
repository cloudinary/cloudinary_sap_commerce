/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplateaddon.controllers.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.ProductBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.forms.ConfigureForm;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.textfieldconfiguratortemplateaddon.forms.TextFieldConfigurationForm;
import de.hybris.platform.textfieldconfiguratortemplatefacades.TextFieldFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@UnitTest
public class ProductTextfieldConfiguratorControllerTest
{
	private static final String PRODUCT_CODE = "CODE1";
	private static final String REDIRECT_ADD_TO_CART_ERROR = "redirect:/p/" + PRODUCT_CODE + "/configuratorPage/TEXTFIELD";
	private static final String REDIRECT_ADD_TO_CART_SUCCESS = "redirect:/cart";
	private static final Long QUANTITY = Long.valueOf(23);
	private static final String DOCUMENT_CODE = "123";
	private static final int ENTRY_NUMBER = 4;
	private static final String ATTRIBUTE_KEY = "key";
	private static final String ATTRIBUTE_VALUE = "value";
	private static final String ATTRIBUTE_KEY2 = "key2";
	private static final String ATTRIBUTE_VALUE2 = "value2";

	@Mock
	private TextFieldFacade textFieldFacade;
	@Mock
	private CartFacade cartFacade;
	@Mock
	private ProductFacade productFacade;

	@InjectMocks
	ProductTextfieldConfiguratorController classUnderTest;

	@Mock
	private Model model;
	private final TextFieldConfigurationForm form = new TextFieldConfigurationForm();
	@Mock
	private ConfigureForm configureForm;
	@Mock
	private BindingResult bindingErrors;
	@Mock
	private HttpServletRequest request;
	@Mock
	private RedirectAttributes redirectModel;
	@Mock
	private ProductBreadcrumbBuilder productBreadcrumbBuilder;
	@Mock
	private CMSPageService cmsPageService;
	@Mock
	private CMSPreviewService cmsPreviewService;
	@Mock
	private OrderFacade orderFacade;
	@Mock
	private QuoteFacade quoteFacade;
	@Mock
	private SaveCartFacade saveCartFacade;

	private final CartModificationData cartModification = new CartModificationData();
	private final OrderEntryData entry = new OrderEntryData();
	private final OrderData order = new OrderData();
	private final ProductData productData = new ProductData();
	private final QuoteData quote = new QuoteData();
	private final CartData cart = new CartData();

	private final CommerceSaveCartResultData cartResultData = new CommerceSaveCartResultData();
	private final Map<ConfiguratorType, Map<String, String>> valueMap = new HashMap<ConfiguratorType, Map<String, String>>();
	private final Map<String, String> textFieldMap = new HashMap<String, String>();





	@Before
	public void initialize() throws CommerceCartModificationException, CommerceSaveCartException
	{
		MockitoAnnotations.initMocks(this);
		form.setQuantity(QUANTITY);
		form.setConfigurationsKeyValueMap(valueMap);
		when(cartFacade.addToCart(PRODUCT_CODE, QUANTITY)).thenReturn(cartModification);
		when(cartFacade.getSessionCart()).thenReturn(cart);
		when(orderFacade.getOrderDetailsForCode(DOCUMENT_CODE)).thenReturn(order);
		when(quoteFacade.getQuoteForCode(DOCUMENT_CODE)).thenReturn(quote);
		when(saveCartFacade.getCartForCodeAndCurrentUser(Mockito.any())).thenReturn(cartResultData);
		when(textFieldFacade.getAbstractOrderEntry(ENTRY_NUMBER, order)).thenReturn(entry);
		when(textFieldFacade.getAbstractOrderEntry(ENTRY_NUMBER, quote)).thenReturn(entry);
		when(textFieldFacade.getAbstractOrderEntry(ENTRY_NUMBER, cart)).thenReturn(entry);
		cartModification.setQuantityAdded(QUANTITY);
		cartModification.setEntry(entry);
		entry.setProduct(productData);
		cartResultData.setSavedCartData(cart);
		valueMap.put(ConfiguratorType.TEXTFIELD, textFieldMap);
		textFieldMap.put(ATTRIBUTE_KEY, ATTRIBUTE_VALUE);
		textFieldMap.put(ATTRIBUTE_KEY2, ATTRIBUTE_VALUE2);
		final List<ConfigurationInfoData> configurationInfos = new ArrayList<>();
		configurationInfos.add(createConfigInfoData(ATTRIBUTE_KEY, ATTRIBUTE_VALUE));
		configurationInfos.add(createConfigInfoData(ATTRIBUTE_KEY2, ATTRIBUTE_VALUE2));
		when(productFacade.getConfiguratorSettingsForCode(PRODUCT_CODE)).thenReturn(configurationInfos);
	}

	protected ConfigurationInfoData createConfigInfoData(final String key, final String value)
	{
		final ConfigurationInfoData infoData = new ConfigurationInfoData();
		infoData.setConfigurationLabel(key);
		infoData.setConfigurationValue(value);
		infoData.setConfiguratorType(ConfiguratorType.TEXTFIELD);
		infoData.setStatus(ProductInfoStatus.SUCCESS);
		return infoData;
	}

	@Test
	public void testTextFieldFacade()
	{
		assertEquals(textFieldFacade, classUnderTest.getTextFieldFacade());
	}

	@Test
	public void testAddToCartSuccess()
	{
		final String addToCart = classUnderTest.addToCart(PRODUCT_CODE, model, form, bindingErrors, request, redirectModel);
		assertEquals(REDIRECT_ADD_TO_CART_SUCCESS, addToCart);

	}

	@Test
	public void testAddToCartHasBindingErrors()
	{
		when(bindingErrors.hasErrors()).thenReturn(true);
		final String addToCart = classUnderTest.addToCart(PRODUCT_CODE, model, form, bindingErrors, request, redirectModel);
		assertEquals(REDIRECT_ADD_TO_CART_ERROR, addToCart);
	}

	@Test
	public void testAddToCartAddToCartResultNull() throws CommerceCartModificationException
	{
		when(cartFacade.addToCart(PRODUCT_CODE, QUANTITY)).thenReturn(null);
		assertEquals(REDIRECT_ADD_TO_CART_ERROR,
				classUnderTest.addToCart(PRODUCT_CODE, model, form, bindingErrors, request, redirectModel));
	}

	@Test
	public void testAddToCartAddToCartNothingAdded() throws CommerceCartModificationException
	{
		cartModification.setQuantityAdded(0);
		assertEquals(REDIRECT_ADD_TO_CART_ERROR,
				classUnderTest.addToCart(PRODUCT_CODE, model, form, bindingErrors, request, redirectModel));
	}

	@Test
	public void testAddToCartAddToCartLessWasAdded() throws CommerceCartModificationException
	{
		cartModification.setQuantityAdded(QUANTITY - 1);
		assertEquals(REDIRECT_ADD_TO_CART_ERROR,
				classUnderTest.addToCart(PRODUCT_CODE, model, form, bindingErrors, request, redirectModel));
	}

	@Test
	public void testProductConfiguratorGet() throws CommerceCartModificationException, CMSItemNotFoundException
	{
		final String redirect = classUnderTest.productConfiguratorGet(PRODUCT_CODE, model, configureForm);
		assertEquals(ProductTextfieldConfiguratorController.PRODUCT_CONFIGURATOR_PAGE, redirect);
	}

	@Test
	public void testProductConfiguratorPost() throws CommerceCartModificationException, CMSItemNotFoundException
	{
		final String redirect = classUnderTest.productConfiguratorPost(PRODUCT_CODE, model, configureForm);
		assertEquals(ProductTextfieldConfiguratorController.PRODUCT_CONFIGURATOR_PAGE, redirect);
	}


	@Test
	public void testDisplayConfigurationInOrderEntry() throws CommerceCartModificationException, CMSItemNotFoundException
	{
		final String redirect = classUnderTest.displayConfigurationInOrderEntry(DOCUMENT_CODE, ENTRY_NUMBER, model);
		assertEquals(ProductTextfieldConfiguratorController.ENTRY_READ_ONLY_PAGE, redirect);
	}

	@Test
	public void testDisplayConfigurationInQuoteEntry() throws CommerceCartModificationException, CMSItemNotFoundException
	{
		final String redirect = classUnderTest.displayConfigurationInQuoteEntry(DOCUMENT_CODE, ENTRY_NUMBER, model);
		assertEquals(ProductTextfieldConfiguratorController.ENTRY_READ_ONLY_PAGE, redirect);
	}

	@Test
	public void testDisplayConfigurationInSavedCartEntry()
			throws CommerceCartModificationException, CMSItemNotFoundException, CommerceSaveCartException
	{
		final String redirect = classUnderTest.displayConfigurationInSavedCartEntry(DOCUMENT_CODE, ENTRY_NUMBER, model);
		assertEquals(ProductTextfieldConfiguratorController.ENTRY_READ_ONLY_PAGE, redirect);
	}

	@Test
	public void testEditConfigurationInEntry()
			throws CommerceCartModificationException, CMSItemNotFoundException, CommerceSaveCartException
	{
		final String redirect = classUnderTest.editConfigurationInEntry(ENTRY_NUMBER, model);
		assertEquals(ProductTextfieldConfiguratorController.ENTRY_CONFIGURATOR_PAGE, redirect);
	}

	@Test
	public void testEnrichOrderEntryWithConfigurationData()
	{
		final OrderEntryData entryAfterEnrichment = classUnderTest.enrichOrderEntryWithConfigurationData(form, entry);
		assertNotNull(entryAfterEnrichment);
		final List<ConfigurationInfoData> configurationInfos = entryAfterEnrichment.getConfigurationInfos();
		assertNotNull(configurationInfos);
		assertEquals(2, configurationInfos.size());
		final ConfigurationInfoData configurationInfoData = configurationInfos.get(0);
		assertEquals(ATTRIBUTE_KEY2, configurationInfoData.getConfigurationLabel());
		assertEquals(ATTRIBUTE_VALUE2, configurationInfoData.getConfigurationValue());
	}

	@Test
	public void testGetValidConfigurationLabels()
	{
		final Map<String, String> result = classUnderTest.getValidConfigurationLabels(PRODUCT_CODE);
		assertEquals(2, result.size());
		assertTrue(result.containsKey(ATTRIBUTE_KEY));
		assertTrue(result.containsKey(ATTRIBUTE_KEY2));
	}

	@Test
	public void testValidateProductConfigurations()
	{
		final BindingResult bindingErrors = new BeanPropertyBindingResult(form, "foo");
		classUnderTest.validateProductConfigurations(form, PRODUCT_CODE, bindingErrors);
		assertFalse(bindingErrors.hasErrors());
	}

	@Test
	public void testValidateProductConfigurationsWithError()
	{
		textFieldMap.put("invalidKey", "value");
		final BindingResult bindingErrors = new BeanPropertyBindingResult(form, "foo");
		classUnderTest.validateProductConfigurations(form, PRODUCT_CODE, bindingErrors);
		assertTrue(bindingErrors.hasErrors());
		assertEquals("configuration.invalid.key", bindingErrors.getAllErrors().get(0).getCode().toString());
	}

	@Test
	public void testAddToCartWithInvalidForm()
	{
		textFieldMap.put("invalidKey", "value");
		final BindingResult bindingErrors = new BeanPropertyBindingResult(form, "foo");
		final String addToCart = classUnderTest.addToCart(PRODUCT_CODE, model, form, bindingErrors, request, redirectModel);
		assertEquals(REDIRECT_ADD_TO_CART_ERROR, addToCart);
	}

	@Test
	public void testUpdateConfigurationInEntryWithInvalidForm() throws CommerceCartModificationException
	{

		textFieldMap.put("invalidKey", "value");
		final BindingResult bindingErrors = new BeanPropertyBindingResult(form, "foo");
		final String updateCart = classUnderTest.updateConfigurationInEntry(ENTRY_NUMBER, model, form, bindingErrors, request,
				redirectModel);
		assertNotEquals(REDIRECT_ADD_TO_CART_SUCCESS, updateCart);
	}

}
