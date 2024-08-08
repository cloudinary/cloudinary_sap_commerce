/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v1.controller;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.DeliveryModesData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.promotion.CommercePromotionRestrictionFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.promotion.CommercePromotionRestrictionException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.LowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.PaymentAuthorizationException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ProductLowStockException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import uk.ptr.cloudinary.exceptions.InvalidPaymentInfoException;
import uk.ptr.cloudinary.exceptions.NoCheckoutCartException;
import uk.ptr.cloudinary.exceptions.UnsupportedDeliveryAddressException;
import uk.ptr.cloudinary.exceptions.UnsupportedDeliveryModeException;
import de.hybris.platform.commercefacades.order.data.CartModificationDataList;
import uk.ptr.cloudinary.populator.HttpRequestPaymentInfoPopulator;
import uk.ptr.cloudinary.populator.options.PaymentInfoOption;
import uk.ptr.cloudinary.stock.CommerceStockFacade;
import uk.ptr.cloudinary.validator.CCPaymentInfoValidator;
import uk.ptr.cloudinary.validator.PlaceOrderCartValidator;
import uk.ptr.cloudinary.validator.PointOfServiceValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Controller("cartControllerV1")
@RequestMapping(value = "/{baseSiteId}/cart")
public class CartController extends BaseController
{
	private static final Logger LOG = Logger.getLogger(CartController.class);
	@Resource(name = "commerceWebServicesCartFacade")
	private CartFacade cartFacade;
	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;
	@Resource(name = "userFacade")
	private UserFacade userFacade;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;
	@Resource(name = "commerceCartService")
	private CommerceCartService commerceCartService;
	@Resource(name = "cartRestorationConverter")
	private Converter<CommerceCartRestoration, CartRestorationData> cartRestorationConverter;
	@Resource(name = "commercePromotionRestrictionFacade")
	private CommercePromotionRestrictionFacade commercePromotionRestrictionFacade;
	@Resource(name = "voucherFacade")
	private VoucherFacade voucherFacade;
	@Resource(name = "commerceStockFacade")
	private CommerceStockFacade commerceStockFacade;
	@Resource(name = "ccPaymentInfoValidator")
	private Validator ccPaymentInfoValidator;
	@Resource(name = "deliveryAddressValidator")
	private Validator deliveryAddressValidator;
	@Resource(name = "httpRequestPaymentInfoPopulator")
	private ConfigurablePopulator<HttpServletRequest, CCPaymentInfoData, PaymentInfoOption> httpRequestPaymentInfoPopulator;
	@Resource(name = "placeOrderCartValidator")
	private PlaceOrderCartValidator placeOrderCartValidator;
	@Resource(name = "pointOfServiceValidator")
	private PointOfServiceValidator pointOfServiceValidator;
	@Resource(name = "httpRequestAddressDataPopulator")
	private Populator<HttpServletRequest, AddressData> httpRequestAddressDataPopulator;
	@Resource(name = "addressValidator")
	private Validator addressValidator;

	public CartData getSessionCart()
	{
		return getSessionCart(false);
	}

	/**
	 * Web service for getting session cart. If there is no cart in the current session it will be restored if possible,
	 * otherwise new one will be created. <br>
	 * Sample call: http://localhost:9001/rest/v1/mysite/cart/ <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 *
	 * @param restore
	 *           enables cart restoration (true by default)
	 *
	 * @return {@link CartData} as response body.
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public CartData getSessionCart(@RequestParam(required = false, defaultValue = "true") final boolean restore)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSessionCart");
		}

		if (!userFacade.isAnonymousUser() && !cartFacade.hasSessionCart() && restore)
		{
			try
			{
				final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
				commerceCartParameter.setCart(commerceCartService.getCartForGuidAndSiteAndUser(null,
						baseSiteService.getCurrentBaseSite(), userService.getCurrentUser()));

				commerceCartService.restoreCart(commerceCartParameter);
			}
			catch (final CommerceCartRestorationException e)
			{
				LOG.error("Couldn't restore cart: " + sanitize(e.getMessage()));
				LOG.debug("Exception thrown: " + e);
			}
		}
		return cartFacade.getSessionCart();
	}

	/**
	 * Web service handler for adding new products to the session cart.<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/entry.<br>
	 * Client should provide product code and quantity (optional) as POST body.<br>
	 * It's also possible to add product that will be pickedup in store by specifying optional storeName parameter
	 * (product must be in stock in that particular store).<br>
	 * For Content-Type=application/x-www-form-urlencoded;charset=UTF-8 a sample body is: (urlencoded) is:
	 * entryNumber=1&qty=2..<br>
	 *
	 * Request Method = <code>POST<code>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.
	 *
	 * @param code
	 * @param qty
	 * @param storeName
	 * @return {@link CartModificationData} as response body.
	 * @throws CommerceCartModificationException
	 * @throws WebserviceValidationException
	 * @throws StockSystemException
	 * @throws ProductLowStockException
	 */

	@RequestMapping(value = "/entry", method = RequestMethod.POST)
	@ResponseBody
	public CartModificationData addToCart(@PathVariable final String baseSiteId, @RequestParam(required = true) final String code,
			@RequestParam(required = false, defaultValue = "1") final long qty,
			@RequestParam(required = false) final String storeName) throws CommerceCartModificationException,
			WebserviceValidationException, ProductLowStockException, StockSystemException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("addToCart : code = " + sanitize(code) + ", qty = " + qty);
		}
		final CartModificationData cartModificationData;
		if (StringUtils.isNotEmpty(storeName))
		{
			final Errors errors = new BeanPropertyBindingResult(storeName, "storeName");
			pointOfServiceValidator.validate(storeName, errors);
			if (errors.hasErrors())
			{
				throw new WebserviceValidationException(errors);
			}

			if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
			{
				throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED,
						baseSiteId);
			}
			final StockData stock = commerceStockFacade.getStockDataForProductAndPointOfService(code, storeName);
			if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
			{
				throw new ProductLowStockException("Product is currently out of stock", LowStockException.NO_STOCK, code);
			}
			else if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.LOWSTOCK))
			{
				throw new ProductLowStockException("Not enough product in stock", LowStockException.LOW_STOCK, code);
			}
			cartModificationData = cartFacade.addToCart(code, qty, storeName);
		}
		else
		{
			cartModificationData = cartFacade.addToCart(code, qty);
		}
		return cartModificationData;
	}

	/**
	 * Web service for modifying cart entry quantity.<br>
	 * Client should provide cart entry number as path variable and new quantity as url request parameter.<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/entry/0?qty=2 <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * Request Method = <code>PUT<code>
	 *
	 * @param entryNumber
	 * @param qty
	 * @return {@link CartModificationData} as response body.
	 * @throws CommerceCartModificationException
	 */
	@RequestMapping(value = "/entry/{entryNumber}", method = RequestMethod.PUT)
	@ResponseBody
	public CartModificationData updateCartEntry(@PathVariable final long entryNumber, @RequestParam(required = true) final long qty)
			throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateCartEntry : entryNumber = " + entryNumber + ", qty = " + qty);
		}
		return cartFacade.updateCartEntry(entryNumber, qty);
	}

	/**
	 * Web service for deleting cart entry.<br>
	 * Client should provide cart entry number as path variable.<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/entry/0<br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * Request Method = <code>DELETE<code>
	 *
	 * @param entryNumber
	 * @return {@link CartModificationData} as response body.
	 * @throws CommerceCartModificationException
	 */
	@RequestMapping(value = "/entry/{entryNumber}", method = RequestMethod.DELETE)
	@ResponseBody
	public CartModificationData deleteCartEntry(@PathVariable final long entryNumber) throws CommerceCartModificationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("deleteCartEntry : entryNumber = " + entryNumber);
		}
		return cartFacade.updateCartEntry(entryNumber, 0);
	}

	/**
	 * Web service for setting store where cart entry will be picked up.<br>
	 * Client should provide cart entry number as path variable and storeName parameter in body.<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/entry/{entryNumber}/store<br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * Request Method = <code>PUT<code>
	 *
	 * @param entryNumber
	 *           identifier of entry which should be updated
	 * @param storeName
	 *           name of store where items will be picked
	 * @return {@link CartModificationData} as response body.
	 * @throws CommerceCartModificationException
	 * @throws WebserviceValidationException
	 * @throws StockSystemException
	 * @throws LowStockException
	 */
	@RequestMapping(value = "/entry/{entryNumber}/store", method = RequestMethod.PUT)
	@ResponseBody
	public CartModificationData pickupEntryInStore(@PathVariable final String baseSiteId, @PathVariable final long entryNumber,
			@RequestParam(required = true) final String storeName)
			throws CommerceCartModificationException, LowStockException, StockSystemException, WebserviceValidationException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateCartEntry : entryNumber = " + entryNumber + ", storeName = " + sanitize(storeName));
		}

		final Errors errors = new BeanPropertyBindingResult(storeName, "storeName");
		pointOfServiceValidator.validate(storeName, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled for " + sanitize(baseSiteId) + " site");
		}
		final OrderEntryData orderEntry = getCartEntryForNumber(entryNumber);
		if (orderEntry != null)
		{
			final StockData stock = commerceStockFacade.getStockDataForProductAndPointOfService(orderEntry.getProduct().getCode(),
					storeName);
			if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
			{
				throw new LowStockException("Product [" + orderEntry.getProduct().getCode() + "] is currently out of stock", //NOSONAR
						LowStockException.NO_STOCK, String.valueOf(entryNumber));
			}
			else if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.LOWSTOCK))
			{
				throw new LowStockException("Not enough product [" + orderEntry.getProduct().getCode() + "] in stock",
						LowStockException.LOW_STOCK, String.valueOf(entryNumber));
			}
		}

		return cartFacade.updateCartEntry(entryNumber, storeName);
	}

	/**
	 * Web service reseting store where entry should be picked up. Entry will be delivered by selected delivery method<br>
	 * Client should provide cart entry number as path variable.<br>
	 * Sample target URL : http://localhost:9001/rest/v1/cart/entry/{entryNumber}/store<br>
	 * Request Method = <code>DELETE<code>
	 *
	 * @param entryNumber
	 *           identifier of entry which should be updated
	 * @return {@link CartModificationData} as response body.
	 * @throws CommerceCartModificationException
	 * @throws StockSystemException
	 * @throws LowStockException
	 */
	@RequestMapping(value = "/entry/{entryNumber}/store", method = RequestMethod.DELETE)
	@ResponseBody
	public CartModificationData updateEntryToDelivery(@PathVariable final String baseSiteId, @PathVariable final long entryNumber)
			throws CommerceCartModificationException, LowStockException, StockSystemException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateEntryToDelivery : entryNumber = " + entryNumber);
		}

		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled for " + sanitize(baseSiteId) + " site");
		}
		final OrderEntryData orderEntry = getCartEntryForNumber(entryNumber);
		if (orderEntry != null)
		{
			final StockData stock = commerceStockFacade.getStockDataForProductAndBaseSite(orderEntry.getProduct().getCode(),
					baseSiteId);
			if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.OUTOFSTOCK))
			{
				throw new LowStockException("Product [" + orderEntry.getProduct().getCode()
						+ "] cannot be shipped - out of stock online", LowStockException.NO_STOCK, String.valueOf(entryNumber));
			}
			else if (stock != null && stock.getStockLevelStatus().equals(StockLevelStatus.LOWSTOCK))
			{
				throw new LowStockException("Product [" + orderEntry.getProduct().getCode()
						+ "] cannot be shipped - not enough product in stock online", LowStockException.LOW_STOCK,
						String.valueOf(entryNumber));
			}
		}
		return cartFacade.updateCartEntry(entryNumber, null);
	}

	/**
	 * Web service for setting cart's delivery address by address id.<br>
	 * Address id must be given as path variable.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/address/delivery/1234 <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication.<br>
	 * Method type : <code>PUT</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return true if carts delivery address was changed.
	 * @throws UnsupportedDeliveryAddressException
	 * @throws NoCheckoutCartException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/address/delivery/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public CartData setCartDeliveryAddress(@PathVariable final String id) //NOSONAR
			throws UnsupportedDeliveryAddressException, NoCheckoutCartException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setCartDeliveryAddress : id = " + sanitize(id));
		}
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot set delivery address. There was no checkout cart created yet!");
		}
		final AddressData address = new AddressData();
		address.setId(id);

		final Errors errors = new BeanPropertyBindingResult(address, "addressData");
		deliveryAddressValidator.validate(address, errors);
		if (errors.hasErrors())
		{
			throw new UnsupportedDeliveryAddressException(id);
		}

		if (checkoutFacade.setDeliveryAddress(address))
		{
			return getSessionCart();
		}

		throw new UnsupportedDeliveryAddressException(id);
	}

	/**
	 * Web service for removing delivery address from current cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/address/delivery <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication.<br>
	 * Method type : <code>DELETE</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return true if carts delivery address was removed.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/address/delivery", method = RequestMethod.DELETE)
	@ResponseBody
	public CartData removeDeliveryAddress()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removeDeliveryAddress");
		}
		checkoutFacade.removeDeliveryAddress();
		return getSessionCart();
	}

	/**
	 * Web service for setting cart's delivery mode by delivery mode code.<br>
	 * Delivery mode code must be given as path variable.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/deliverymode/expressDelivery <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication.<br>
	 * Method type : <code>PUT</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return true if carts delivery mode was changed.
	 * @throws UnsupportedDeliveryModeException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/deliverymodes/{code}", method = RequestMethod.PUT)
	@ResponseBody
	public CartData setCartDeliveryMode(@PathVariable final String code) throws UnsupportedDeliveryModeException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setCartDeliveryMode : code = " + sanitize(code));
		}

		if (checkoutFacade.setDeliveryMode(code))
		{
			return getSessionCart();
		}
		throw new UnsupportedDeliveryModeException(code);
	}

	/**
	 * Web service for removing delivery mode from current cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/deliverymode <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication.<br>
	 * Method type : <code>DELETE</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return true if cart's delivery mode was removed.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/deliverymodes", method = RequestMethod.DELETE)
	@ResponseBody
	public CartData removeDeliveryMode()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("removeDeliveryMode");
		}
		checkoutFacade.removeDeliveryMode();
		return getSessionCart();
	}

	/**
	 * Web service for placing order from current session cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/placeorder. <br>
	 * This method requires authentication.<br>
	 * Method type : <code>POST</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return {@link OrderData} as response body
	 * @throws InvalidCartException
	 * @throws NoCheckoutCartException
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/placeorder", method = RequestMethod.POST)
	@ResponseBody
	public OrderData placeOrder(final HttpSession session) //NOSONAR
			throws InvalidCartException, NoCheckoutCartException, WebserviceValidationException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("placeOrder");
		}

		validateCartForPlaceOrder();

		final OrderData orderData = checkoutFacade.placeOrder();
		final String orderGuid = orderData.getGuid();
		session.setAttribute("orderGuid", orderGuid);
		return orderData;
	}

	protected void validateCartForPlaceOrder() throws NoCheckoutCartException, InvalidCartException, WebserviceValidationException //NOSONAR
	{
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot place order. There was no checkout cart created yet!");
		}

		final CartData cartData = cartFacade.getSessionCart();
		final Errors errors = new BeanPropertyBindingResult(cartData, "sessionCart");
		placeOrderCartValidator.validate(cartData, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		try
		{
			List<CartModificationData> modificationList;
			modificationList = cartFacade.validateCartData();
			if (modificationList != null && !modificationList.isEmpty())
			{
				final CartModificationDataList cartModificationDataList = new CartModificationDataList();
				cartModificationDataList.setCartModificationList(modificationList);
				throw new WebserviceValidationException(cartModificationDataList);
			}
		}
		catch (final CommerceCartModificationException e)
		{
			throw new InvalidCartException(e);
		}
	}

	/**
	 * Web service for creating a credit card payment subscription.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/paymentinfo <br>
	 * CCPaymentInfoData parameters need to be send as post body.<br>
	 * Method uses dedicated populator - {@link HttpRequestPaymentInfoPopulator} - to populate the
	 * {@link CCPaymentInfoData} from request parameters.<br>
	 * Method uses dedicated validator - {@link CCPaymentInfoValidator} - to validate request parameters.<br>
	 * This method requires authentication and is restricted for <code>HTTPS</code> channel.<br>
	 * Method type : <code>POST</code>.<br>
	 *
	 * @param request
	 *           incoming HttpServletRequest. As there are many potential query parameters to handle they are not mapped
	 *           using annotations.
	 *
	 * @return {@link CartData} as response body
	 * @throws WebserviceValidationException
	 * @throws InvalidPaymentInfoException
	 * @throws NoCheckoutCartException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/paymentinfo", method = RequestMethod.POST)
	@ResponseBody
	public CartData addPaymentInfo(final HttpServletRequest request)  //NOSONAR
			throws WebserviceValidationException, InvalidPaymentInfoException, NoCheckoutCartException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("addPaymentInfo");
		}
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot add PaymentInfo. There was no checkout cart created yet!");
		}


		final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
		final Errors errors = new BeanPropertyBindingResult(paymentInfoData, "paymentInfoData");

		final Collection<PaymentInfoOption> options = new ArrayList<PaymentInfoOption>();
		options.add(PaymentInfoOption.BASIC);
		options.add(PaymentInfoOption.BILLING_ADDRESS);

		httpRequestPaymentInfoPopulator.populate(request, paymentInfoData, options);
		ccPaymentInfoValidator.validate(paymentInfoData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		final boolean emptySavedPaymentInfos = userFacade.getCCPaymentInfos(true).isEmpty();
		final CCPaymentInfoData createdPaymentInfoData = checkoutFacade.createPaymentSubscription(paymentInfoData);

		if (createdPaymentInfoData == null)
		{
			throw new InvalidPaymentInfoException("null");
		}

		if (createdPaymentInfoData.isSaved() && (paymentInfoData.isDefaultPaymentInfo() || emptySavedPaymentInfos))
		{
			userFacade.setDefaultPaymentInfo(createdPaymentInfoData);
		}

		if (checkoutFacade.setPaymentDetails(createdPaymentInfoData.getId()))
		{
			return getSessionCart();
		}
		throw new InvalidPaymentInfoException(createdPaymentInfoData.getId());
	}

	/**
	 * Web service for assigning given payment (by payment id) to the checkout cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/paymentinfo/1234 <br>
	 * This method requires authentication and is restricted for <code>HTTPS</code> channel.<br>
	 * Method type : <code>PUT</code>.
	 *
	 * @return <code>true</code> if paymentInfo was assigned to the session cart.
	 * @throws InvalidPaymentInfoException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/paymentinfo/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public CartData setPaymentDetails(@PathVariable final String id) throws InvalidPaymentInfoException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setPaymentDetails : id = " + sanitize(id));
		}
		if (checkoutFacade.setPaymentDetails(id))
		{
			return getSessionCart();
		}
		throw new InvalidPaymentInfoException(id);
	}

	/**
	 * Web service for getting all supported delivery modes for the session cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/deliverymodes <br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication and is restricted to <code>HTTPS<code> channel only.<br>
	 * Method type : <code>GET</code>.
	 *
	 * @return List of {@link DeliveryModeData} as response body.
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/deliverymodes", method = RequestMethod.GET)
	@ResponseBody
	public DeliveryModesData getSupportedDeliveryModes()
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSupportedDeliveryModes");
		}
		final DeliveryModesData deliveryModesData = new DeliveryModesData();
		deliveryModesData.setDeliveryModes(checkoutFacade.getSupportedDeliveryModes());
		return deliveryModesData;
	}

	/**
	 * Web service for authorizing cart's credit cart payment.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/authorizePayment <br>
	 * authorization security code - ccv - must be sent as a post body.<br>
	 * Response contains a set-cookie header with the jsessionId associated with the cart.<br>
	 * This method requires authentication and is restricted to <code>HTTPS<code> channel only.<br>
	 * Method type : <code>POST</code>.
	 *
	 * @return true if the payment was authorized
	 * @throws PaymentAuthorizationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/authorize", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.ACCEPTED)
	public CartData authorizePayment(@RequestParam(required = true) final String securityCode)
			throws PaymentAuthorizationException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("authorizePayment");
		}
		if (checkoutFacade.authorizePayment(securityCode))
		{
			return getSessionCart();
		}
		throw new PaymentAuthorizationException();
	}

	/**
	 * Web service for restoring anonymous cart by guid.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/restore <br>
	 * This method requires authentication and is restricted to <code>HTTPS<code> channel only.<br>
	 * Method type : <code>GET</code>.
	 *
	 * @param guid
	 *
	 * @return {@link CartRestorationData}
	 * @throws CommerceCartRestorationException
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/restore", method = RequestMethod.GET)
	@ResponseBody
	public CartRestorationData restoreCart(@RequestParam final String guid) throws CommerceCartRestorationException
	{
		final CartModel cartModel = commerceCartService.getCartForGuidAndSiteAndUser(guid, baseSiteService.getCurrentBaseSite(),
				userService.getAnonymousUser());

		if (cartModel == null)
		{
			throw new CommerceCartRestorationException("Cannot find cart for a given guid: " + sanitize(guid));
		}

		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		commerceCartParameter.setCart(cartModel);

		return cartRestorationConverter.convert(commerceCartService.restoreCart(commerceCartParameter));
	}

	/**
	 * Web service for enabling order promotions.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/promotion/{promotionCode}<br>
	 * This method requires authentication and is restricted to <code>HTTPS<code> channel only.<br>
	 * Method type : <code>POST</code>.
	 *
	 * @param promotionCode
	 *           promotion code
	 * @return {@link CartData}
	 * @throws CommercePromotionRestrictionException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT" })
	/*
	 * To allow trusted client logged in as a customer. ROLE_TRUSTED_CLIENT is forced in spring security configuration.
	 */
	@RequestMapping(value = "/promotion/{promotionCode}", method = RequestMethod.POST)
	@ResponseBody
	public CartData applyPromotion(@PathVariable final String promotionCode) throws CommercePromotionRestrictionException
	{
		commercePromotionRestrictionFacade.enablePromotionForCurrentCart(promotionCode);
		return getSessionCart();
	}

	/**
	 * Web service for disabling order promotions.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/promotion/{promotionCode}<br>
	 * This method requires authentication and is restricted to <code>HTTPS<code> channel only.<br>
	 * Method type : <code>DELETE</code>.
	 *
	 * @param promotionCode
	 *           promotion code
	 * @return {@link CartData}
	 * @throws CommercePromotionRestrictionException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT" })
	/*
	 * To allow trusted client logged in as a customer. ROLE_TRUSTED_CLIENT is forced in spring security configuration.
	 */
	@RequestMapping(value = "/promotion/{promotionCode}", method = RequestMethod.DELETE)
	@ResponseBody
	public CartData removePromotion(@PathVariable final String promotionCode) //NOSONAR
			throws CommercePromotionRestrictionException, NoCheckoutCartException //NOSONAR
	{
		commercePromotionRestrictionFacade.disablePromotionForCurrentCart(promotionCode);
		return getSessionCart();
	}

	/**
	 * Web service for applying voucher to cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/voucher/abc-9PSW-EDH2-RXKA <br>
	 * This method requires authentication.<br>
	 * Method type : <code>POST</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return cart data with applied voucher.
	 * @throws NoCheckoutCartException
	 * @throws VoucherOperationException
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/voucher/{voucherCode}", method = RequestMethod.POST)
	@ResponseBody
	public CartData applyVoucherForCart(@PathVariable final String voucherCode) //NOSONAR
			throws NoCheckoutCartException, VoucherOperationException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("apply voucher : voucherCode = " + sanitize(voucherCode));
		}
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot apply voucher. There was no checkout cart created yet!");
		}

		voucherFacade.applyVoucher(voucherCode);
		return getSessionCart();
	}

	/**
	 * Web service for removing voucher from cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/voucher/abc-9PSW-EDH2-RXKA <br>
	 * This method requires authentication.<br>
	 * Method type : <code>DELETE</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @return updated cart data.
	 * @throws NoCheckoutCartException
	 * @throws VoucherOperationException
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_GUEST" })
	@RequestMapping(value = "/voucher/{voucherCode}", method = RequestMethod.DELETE)
	@ResponseBody
	public CartData releaseVoucherFromCart(@PathVariable final String voucherCode) //NOSONAR
			throws NoCheckoutCartException, VoucherOperationException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("release voucher : voucherCode = " + sanitize(voucherCode));
		}
		if (!checkoutFacade.hasCheckoutCart())
		{
			throw new NoCheckoutCartException("Cannot realese voucher. There was no checkout cart created yet!");
		}
		voucherFacade.releaseVoucher(voucherCode);
		return getSessionCart();
	}

	/**
	 * Web service for one-step checkout from current session cart.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/cart/checkout <br>
	 * This method requires authentication.<br>
	 * Method type : <code>POST</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 *
	 * @param addressId
	 *           id of created address
	 * @param addressIsocode
	 *           country isocode, parameter is also used as a flag to decide if new address should be created
	 * @param deliveryMode
	 *           delivery mode
	 * @param voucherCode
	 *           voucher code
	 * @param paymentInfoId
	 *           id of created payment info
	 * @param securityCode
	 *           security code for payment validation
	 * @param request
	 *           incoming HttpServletRequest. As there are many potential query parameters to handle they are not mapped
	 *           using annotations.
	 * @return {@link OrderData}
	 * @throws NoCheckoutCartException
	 * @throws UnsupportedDeliveryAddressException
	 * @throws UnsupportedDeliveryModeException
	 * @throws InvalidPaymentInfoException
	 * @throws PaymentAuthorizationException
	 * @throws InvalidCartException
	 * @throws WebserviceValidationException
	 * @throws VoucherOperationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
	@ResponseBody
	public OrderData oneStepCheckout(@RequestParam(required = false) final String addressId, //NOSONAR
			@RequestParam(value = "country.isocode", required = false) final String addressIsocode,
			@RequestParam final String deliveryMode, @RequestParam(required = false) final String voucherCode,
			@RequestParam(required = false) final String paymentInfoId, @RequestParam final String securityCode,
			final HttpServletRequest request) throws NoCheckoutCartException, UnsupportedDeliveryAddressException,
			UnsupportedDeliveryModeException, InvalidPaymentInfoException, PaymentAuthorizationException, InvalidCartException,
			WebserviceValidationException, VoucherOperationException //NOSONAR
	{
		//redeeming voucher if needed
		if (voucherCode != null)
		{
			applyVoucherForCart(voucherCode);
		}

		//delivery address
		if (addressId != null)
		{
			LOG.info("oneStepCheckout : addressId=" + sanitize(addressId));
			setCartDeliveryAddress(addressId);
		}
		else if (addressIsocode != null)
		{
			final AddressData address = createAddress(request);
			setCartDeliveryAddress(address.getId());
		}

		//deliveryMode
		setCartDeliveryMode(deliveryMode);

		//paymentInfo
		if (paymentInfoId != null)
		{
			setPaymentDetails(paymentInfoId);
		}
		else
		{
			addPaymentInfo(request);
		}

		//authorize
		authorizePayment(securityCode);

		//placeorder
		return placeOrder(request.getSession());
	}

	protected OrderEntryData getCartEntryForNumber(final long number)
	{
		final List<OrderEntryData> entries = cartFacade.getSessionCart().getEntries();
		if (entries != null && !entries.isEmpty())
		{
			final Integer requestedEntryNumber = Integer.valueOf((int) number);
			for (final OrderEntryData entry : entries)
			{
				if (entry != null && requestedEntryNumber.equals(entry.getEntryNumber()))
				{
					return entry;
				}
			}
		}
		return null;
	}

	protected AddressData createAddress(final HttpServletRequest request)
	{
		final AddressData address = new AddressData();
		final Errors errors = new BeanPropertyBindingResult(address, "addressData");

		httpRequestAddressDataPopulator.populate(request, address);
		addressValidator.validate(address, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		address.setShippingAddress(true);
		address.setVisibleInAddressBook(true);
		userFacade.addAddress(address);
		return address;
	}
}
