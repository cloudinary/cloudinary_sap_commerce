/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v1.controller;

import de.hybris.platform.commercefacades.address.AddressVerificationFacade;
import de.hybris.platform.commercefacades.address.data.AddressVerificationResult;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.customergroups.CustomerGroupFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoDatas;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commercefacades.user.data.UserGroupDataList;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.SessionAttributeException;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import uk.ptr.cloudinary.populator.HttpRequestAddressDataPopulator;
import uk.ptr.cloudinary.populator.HttpRequestCustomerDataPopulator;
import uk.ptr.cloudinary.populator.HttpRequestPaymentInfoPopulator;
import uk.ptr.cloudinary.populator.options.PaymentInfoOption;
import uk.ptr.cloudinary.user.data.AddressDataList;
import uk.ptr.cloudinary.user.data.LoginChangeResponse;
import uk.ptr.cloudinary.user.data.PasswordRestoreResponse;
import uk.ptr.cloudinary.validation.data.AddressValidationData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.routines.EmailValidator;
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


/**
 * Main Controller for CustomerFacade WebServices
 */
@Controller("customersControllerV1")
@RequestMapping(value = "/{baseSiteId}/customers")
public class CustomersController extends BaseController
{
	private static final Logger LOG = Logger.getLogger(CustomersController.class);
	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;
	@Resource(name = "userFacade")
	private UserFacade userFacade;
	@Resource(name = "userService")
	private UserService userService;
	@Resource(name = "customerGroupFacade")
	private CustomerGroupFacade customerGroupFacade;
	@Resource(name = "addressVerificationFacade")
	private AddressVerificationFacade addressVerificationFacade;
	@Resource(name = "httpRequestCustomerDataPopulator")
	private HttpRequestCustomerDataPopulator httpRequestCustomerDataPopulator;
	@Resource(name = "httpRequestAddressDataPopulator")
	private Populator<HttpServletRequest, AddressData> httpRequestAddressDataPopulator;
	@Resource(name = "addressValidator")
	private Validator addressValidator;
	@Resource(name = "httpRequestPaymentInfoPopulator")
	private ConfigurablePopulator<HttpServletRequest, CCPaymentInfoData, PaymentInfoOption> httpRequestPaymentInfoPopulator;
	@Resource(name = "addressDataErrorsPopulator")
	private Populator<AddressVerificationResult<AddressVerificationDecision>, Errors> addressDataErrorsPopulator;
	@Resource(name = "validationErrorConverter")
	private Converter<Object, List<ErrorWsDTO>> validationErrorConverter;

	/**
	 * Client should pass customer's data as POST Body. Content-Type needs to be set to
	 * application/x-www-form-urlencoded; charset=UTF-8 and sample body (urlencoded) is: old=1234&new=1111<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/customers <br>
	 * Method requires authentication and is restricted to <code>HTTPS<code> channel.<br>
	 * Method type : <code>POST</code> Register data need to be sent as post body..<br>
	 *
	 * @param login
	 *           - login to be created
	 * @param password
	 *           - customer password
	 * @param firstName
	 *           - customer first name
	 * @param lastName
	 *           - customer last name
	 * @param titleCode
	 *           - customer's title
	 * @throws DuplicateUidException
	 *            in case the requested login already exists
	 * @throws RequestParameterException
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	public void registerUser(@RequestParam final String login, @RequestParam final String password,
			@RequestParam(required = false) final String titleCode, @RequestParam final String firstName,
			@RequestParam final String lastName) throws DuplicateUidException, RequestParameterException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("registerUser: login=" + sanitize(login));
		}

		if (!EmailValidator.getInstance().isValid(login))
		{
			throw new RequestParameterException("Login [" + sanitize(login) + "] is not a valid e-mail address!",
					RequestParameterException.INVALID, "login");
		}

		final RegisterData registration = new RegisterData();
		registration.setFirstName(firstName);
		registration.setLastName(lastName);
		registration.setLogin(login);
		registration.setPassword(password);
		registration.setTitleCode(titleCode);
		customerFacade.register(registration);
	}

	/**
	 * Creates the user dedicated to do a guest checkout. Newly created user will be available only by cartService and
	 * only during the current session.
	 *
	 * @param email
	 *           - guest user's e-mail, it will be part of user id
	 * @throws DuplicateUidException
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/current/guestlogin", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void guestLogin(@RequestParam final String email) throws DuplicateUidException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("createGuestUserForAnonymousCheckout: email=" + sanitize(email));
		}
		customerFacade.createGuestUserForAnonymousCheckout(email, "guest");
	}

	/**
	 * Converts currently logged guest user to full user. It is possible to call it after guest user has placed order
	 * during the same session.
	 *
	 * @param password
	 * @param httpSession
	 * @throws DuplicateUidException
	 * @throws SessionAttributeException
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/current/convert", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void convertAnonymousUserToCustomer(@RequestParam final String password, final HttpSession httpSession)
			throws DuplicateUidException, SessionAttributeException //NOSONAR
	{
		final String orderGuid = (String) httpSession.getAttribute("orderGuid");
		if (null == orderGuid)
		{
			throw new SessionAttributeException("No order guid in the session. Probably session has expired!",
					SessionAttributeException.NOT_FOUND, "orderGuid");
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("convert user: order guid=" + sanitize(orderGuid));
		}
		customerFacade.changeGuestToCustomer(password, orderGuid);
	}

	/**
	 * Update customer's default address
	 *
	 * @param id
	 *           - Address id to be set as default address
	 * @throws DuplicateUidException
	 */
	@Secured("ROLE_CUSTOMERGROUP")
	@RequestMapping(value = "/current/addresses/default/{id}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public void updateDefaultAddress(@PathVariable final String id) throws DuplicateUidException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateDefaultAddress: id=" + sanitize(id));
		}

		final AddressData address = userFacade.getAddressForCode(id);
		if (address == null)
		{
			throw new RequestParameterException("Address with given id: '" + sanitize(id) //NOSONAR
					+ "' doesn't exist or belong to another user", RequestParameterException.INVALID, "addressId"); //NOSONAR
		}

		userFacade.setDefaultAddress(address);
	}

	/**
	 * Update customer's profile
	 *
	 * @param request
	 *           - http request
	 * @return updated profile
	 * @throws DuplicateUidException
	 */
	@Secured("ROLE_CUSTOMERGROUP")
	@RequestMapping(value = "/current/profile", method =
	{ RequestMethod.PUT, RequestMethod.POST })
	@ResponseBody
	public CustomerData updateProfile(final HttpServletRequest request) throws DuplicateUidException
	{
		final CustomerData customer = customerFacade.getCurrentCustomer();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updateCustomer: userId=" + customer.getUid());
		}
		httpRequestCustomerDataPopulator.populate(request, customer);
		customerFacade.updateFullProfile(customer);
		return customerFacade.getCurrentCustomer();
	}

	/**
	 * Get all customer's addresses
	 *
	 * @return List of customer addresses
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/current/addresses", method = RequestMethod.GET)
	@ResponseBody
	public AddressDataList getAddresses()
	{
		final AddressDataList addressDataList = new AddressDataList();
		addressDataList.setAddresses(userFacade.getAddressBook());
		return addressDataList;
	}

	/**
	 * Create new address for current customer
	 *
	 * @param request
	 * @return address created
	 * @throws DuplicateUidException
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/current/addresses", method = RequestMethod.POST)
	@ResponseBody
	public AddressData createAddress(final HttpServletRequest request) 
			throws DuplicateUidException, WebserviceValidationException //NOSONAR
	{
		final AddressData addressData = new AddressData();
		final Errors errors = new BeanPropertyBindingResult(addressData, "addressData");

		httpRequestAddressDataPopulator.populate(request, addressData);
		addressValidator.validate(addressData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		addressData.setShippingAddress(true);
		addressData.setVisibleInAddressBook(true);
		userFacade.addAddress(addressData);
		return addressData;
	}

	/**
	 * Edit address from current customer
	 *
	 * @param id
	 *           - id of address to be edited
	 * @return modified address
	 * @throws WebserviceValidationException
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/current/addresses/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public AddressData editAddress(@PathVariable final String id, final HttpServletRequest request)
			throws WebserviceValidationException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("editAddress: id=" + sanitize(id));
		}

		final AddressData address = userFacade.getAddressForCode(id);
		if (address == null)
		{
			throw new RequestParameterException("Address with given id: '" + sanitize(id)
					+ "' doesn't exist or belong to another user", RequestParameterException.INVALID, "addressId");
		}

		final Errors errors = new BeanPropertyBindingResult(address, "addressData");

		httpRequestAddressDataPopulator.populate(request, address);
		addressValidator.validate(address, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		if (address.getId().equals(userFacade.getDefaultAddress().getId()))
		{
			address.setDefaultAddress(true);
			address.setVisibleInAddressBook(true);
		}
		userFacade.editAddress(address);
		return address;
	}

	/**
	 * Remove address from current customer
	 *
	 * @param id
	 *           - id of address to be removed
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/current/addresses/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteAddress(@PathVariable final String id)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("deleteAddress: id=" + sanitize(id));
		}

		final AddressData address = userFacade.getAddressForCode(id);
		if (address == null)
		{
			throw new RequestParameterException("Address with given id: '" + sanitize(id)
					+ "' doesn't exist or belong to another user", RequestParameterException.INVALID, "addressId");
		}

		userFacade.removeAddress(address);
	}

	/**
	 * Get customer data
	 *
	 * @return CustomerData object containing customer information
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/current", method = RequestMethod.GET)
	@ResponseBody
	public CustomerData getCurrentCustomer()
	{
		return customerFacade.getCurrentCustomer();
	}

	/**
	 * Client should pass old and new password in Body. Content-Type needs to be set to
	 * application/x-www-form-urlencoded; charset=UTF-8 and sample body (urlencoded) is: old=1234&new=1111
	 *
	 * @param old
	 *           - old password
	 * @param newPassword
	 *           - new password
	 */
	@Secured("ROLE_CUSTOMERGROUP")
	@RequestMapping(value = "/current/password", method =
	{ RequestMethod.PUT, RequestMethod.POST })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public void changePassword(@RequestParam final String old, @RequestParam(value = "new") final String newPassword)
	{
		customerFacade.changePassword(old, newPassword);
	}

	/**
	 * Web service for getting current user's credit card payment infos.<br>
	 * Sample call: http://localhost:9001/rest/v1/mysite/customers/paymentinfos?saved=true <br>
	 * Method requires authentication and is restricted to <code>HTTPS<code> channel.<br>
	 * Method type : <code>GET</code>.<br>
	 *
	 * @param saved
	 *           - <code>true</code> to retrieve only saved payment infos. <code>false</code> by default
	 *
	 * @return List of {@link CCPaymentInfoData} as response body
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/current/paymentinfos", method = RequestMethod.GET)
	@ResponseBody
	public CCPaymentInfoDatas getPaymentInfos(@RequestParam(required = false, defaultValue = "false") final boolean saved)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getPaymentInfos");
		}

		final CCPaymentInfoDatas data = new CCPaymentInfoDatas();
		data.setPaymentInfos(userFacade.getCCPaymentInfos(saved));
		return data;
	}

	/**
	 * Web service for getting current user's credit card payment info by id.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/customers/paymentinfos/123 <br>
	 * Method requires authentication and is restricted to <code>HTTPS<code> channel.<br>
	 * Method type : <code>GET</code>.<br>
	 *
	 *
	 * @return {@link CCPaymentInfoData} as response body
	 */
	@Secured("ROLE_CUSTOMERGROUP")
	@RequestMapping(value = "/current/paymentinfos/{id}", method = RequestMethod.GET)
	@ResponseBody
	public CCPaymentInfoData getPaymentInfo(@PathVariable final String id)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getPaymentInfo : id = " + sanitize(id));
		}
		return userFacade.getCCPaymentInfoForCode(id);
	}

	/**
	 * Web service for deleting current user's credit card payment info by id.<br>
	 * Sample call: http://localhost:9001/rest/v1/mysite/customers/paymentinfos/123<br>
	 * Method requires authentication and is restricted to <code>HTTPS<code> channel.<br>
	 * Method type : <code>DELETE</code>.<br>
	 *
	 */
	@Secured("ROLE_CUSTOMERGROUP")
	@RequestMapping(value = "/current/paymentinfos/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deletePaymentInfo(@PathVariable final String id)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("deletePaymentInfo: id = " + sanitize(id));
		}
		userFacade.removeCCPaymentInfo(id);
	}

	/**
	 * Web service for modifying billing address data for the specific payment info.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/paymentinfos/123/address<br>
	 * Method requires authentication and is restricted to <code>HTTPS<code> channel.<br>
	 * Method type : <code>POST</code>. Address data need to be sent as post body.<br>
	 * Method uses {@link HttpRequestAddressDataPopulator} to populate address data from request parameters.
	 *
	 * @param paymentInfoId
	 * @param request
	 * @throws RequestParameterException
	 */
	@RequestMapping(value = "/current/paymentinfos/{paymentInfoId}/address", method = RequestMethod.POST)
	@ResponseBody
	@Secured(
	{ "ROLE_CUSTOMERGROUP" })
	public void updatePaymentInfoAddress(@PathVariable final String paymentInfoId, final HttpServletRequest request)
			throws RequestParameterException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updatePaymentInfoAddress: id = " + sanitize(paymentInfoId));
		}
		final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentInfoId);

		if (paymentInfoData != null)
		{
			final AddressData billingAddressData = paymentInfoData.getBillingAddress();
			httpRequestAddressDataPopulator.populate(request, billingAddressData);
			paymentInfoData.setBillingAddress(billingAddressData);
			userFacade.updateCCPaymentInfo(paymentInfoData);
		}
		else
		{
			throw new RequestParameterException("Payment info [" + sanitize(paymentInfoId) + "] not found.",
					RequestParameterException.UNKNOWN_IDENTIFIER, "paymentInfoId");
		}
	}

	/**
	 * Web service for modifying existing payment info.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/paymentinfos/123<br>
	 * Method requires authentication and is restricted to <code>HTTPS<code> channel.<br>
	 * Method type : <code>PUT</code>. PaymentInfo data need to be sent in body.<br>
	 * Method uses {@link HttpRequestPaymentInfoPopulator} to populate payment info data from request parameters.
	 *
	 * @param paymentInfoId
	 * @param request
	 * @throws RequestParameterException
	 */
	@RequestMapping(value = "/current/paymentinfos/{paymentInfoId}", method = RequestMethod.PUT)
	@ResponseBody
	@Secured("ROLE_CUSTOMERGROUP")
	public void updatePaymentInfo(@PathVariable final String paymentInfoId, final HttpServletRequest request)
			throws RequestParameterException //NOSONAR
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("updatePaymentInfo: id = " + sanitize(paymentInfoId));
		}
		final CCPaymentInfoData paymentInfoData = getPaymentInfo(paymentInfoId);
		if (paymentInfoData != null)
		{
			final boolean isAlreadyDefaultPaymentInfo = paymentInfoData.isDefaultPaymentInfo();
			httpRequestPaymentInfoPopulator.populate(request, paymentInfoData, Collections.singletonList(PaymentInfoOption.BASIC));
			userFacade.updateCCPaymentInfo(paymentInfoData);
			if (paymentInfoData.isSaved() && !isAlreadyDefaultPaymentInfo && paymentInfoData.isDefaultPaymentInfo())
			{
				userFacade.setDefaultPaymentInfo(paymentInfoData);
			}
		}
		else
		{
			throw new RequestParameterException("Payment info [" + sanitize(paymentInfoId) + "] not found.",
					RequestParameterException.UNKNOWN_IDENTIFIER, "paymentInfoId");
		}
	}

	@Secured("ROLE_CUSTOMERGROUP")
	@RequestMapping(value = "/current/customergroups", method = RequestMethod.GET)
	@ResponseBody
	public UserGroupDataList getAllCustomerGroupsForCurrentCustomer()
	{
		final UserGroupDataList userGroupDataList = new UserGroupDataList();
		userGroupDataList.setUserGroups(customerGroupFacade.getCustomerGroupsForCurrentUser());
		return userGroupDataList;
	}

	/**
	 * Web service for getting customer groups for current user.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/customers/{uid}/customergroups<br>
	 * Method requires authentication and is restricted to <code>HTTPS<code> channel.<br>
	 * Method type : <code>GET</code>.
	 *
	 * @return {@link UserGroupDataList} as response body
	 */
	@Secured("ROLE_CUSTOMERMANAGERGROUP")
	@RequestMapping(value = "/{uid}/customergroups", method = RequestMethod.GET)
	@ResponseBody
	public UserGroupDataList getAllCustomerGroupsForCustomer(@PathVariable final String uid)
	{
		final UserGroupDataList userGroupDataList = new UserGroupDataList();
		userGroupDataList.setUserGroups(customerGroupFacade.getCustomerGroupsForUser(uid));
		return userGroupDataList;
	}

	/**
	 * Web service for changing customer login.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/customers/current/login?newLogin=:newLogin&password=:password<br>
	 * Method requires customer authentication and is restricted to <code>HTTPS<code> channel.<br>
	 * Method type : <code>POST</code>.
	 *
	 * @param newLogin
	 * @param password
	 * @return {@link LoginChangeResponse}
	 * @throws DuplicateUidException
	 * @throws PasswordMismatchException
	 * @throws RequestParameterException
	 */
	@Secured("ROLE_CUSTOMERGROUP")
	@RequestMapping(value = "/current/login", method =
	{ RequestMethod.PUT, RequestMethod.POST })
	@ResponseBody
	public LoginChangeResponse changeLogin(@RequestParam final String newLogin, @RequestParam final String password)
			throws DuplicateUidException, PasswordMismatchException, RequestParameterException //NOSONAR
	{
		if (!EmailValidator.getInstance().isValid(newLogin))
		{
			throw new RequestParameterException("Login [" + sanitize(newLogin) + "] is not a valid e-mail address!",
					RequestParameterException.INVALID, "newLogin");
		}
		customerFacade.changeUid(newLogin, password);
		final LoginChangeResponse loginChangeResponse = new LoginChangeResponse();
		loginChangeResponse.setSuccess(true);
		return loginChangeResponse;
	}

	/**
	 * Web service for restoring password of current customer.<br>
	 * Sample call: https://localhost:9002/rest/v1/mysite/customers/current/forgottenpassword?login=:login/<br>
	 * Method requires client or trusted_client authentication and is restricted to <code>HTTPS<code> channel.<br>
	 * Method type : <code>POST</code>.
	 *
	 * @param login
	 * @return {@link PasswordRestoreResponse}
	 */
	@Secured(
	{ "ROLE_CLIENT", "ROLE_TRUSTED_CLIENT" })
	@RequestMapping(value = "/current/forgottenpassword", method = RequestMethod.POST)
	@ResponseBody
	public PasswordRestoreResponse restorePassword(@RequestParam final String login)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("restorePassword: uid=" + sanitize(login));
		}
		customerFacade.forgottenPassword(login);
		final PasswordRestoreResponse passwordRestoreResponse = new PasswordRestoreResponse();
		passwordRestoreResponse.setSuccess(true);
		return passwordRestoreResponse;
	}

	/**
	 * Client should pass old and new password in Body. Content-Type needs to be set to
	 * application/x-www-form-urlencoded; charset=UTF-8 and sample body (urlencoded) is: new=1111
	 *
	 * @param newPassword
	 *           - new password
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/{customerId}/password", method =
	{ RequestMethod.PUT, RequestMethod.POST })
	@ResponseBody
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	public void changeCustomerPassword(@PathVariable final String customerId, @RequestParam(value = "new") final String newPassword)
	{
		userService.setPassword(customerId, newPassword);
	}

	/**
	 * Verifies address
	 *
	 * @param request
	 * @return address created
	 */
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_GUEST" })
	@RequestMapping(value = "/current/addresses/verify", method = RequestMethod.POST)
	@ResponseBody
	public AddressValidationData verifyAddress(final HttpServletRequest request)
	{
		final AddressData addressData = new AddressData();
		final Errors errors = new BeanPropertyBindingResult(addressData, "addressData");
		final AddressValidationData validationData = new AddressValidationData();

		httpRequestAddressDataPopulator.populate(request, addressData);
		if (!isAddressValid(addressData, errors, validationData))
		{
			return validationData;
		}


		return verifyAddresByService(addressData, errors, validationData);
	}

	/**
	 * Checks if address is valid by a validators
	 *
	 * @param addressData
	 * @param errors
	 * @param validationData
	 * @return <code>true</code> if address is valid; <code>false</code> otherwise
	 */
	protected boolean isAddressValid(final AddressData addressData, final Errors errors, final AddressValidationData validationData)
	{
		addressValidator.validate(addressData, errors);

		if (errors.hasErrors())
		{
			validationData.setDecision(AddressVerificationDecision.REJECT.toString());
			validationData.setErrors(createResponseErrors(errors));
			return false;
		}
		return true;
	}

	/**
	 * Verifies address by commerce service
	 *
	 * @param addressData
	 * @param errors
	 * @param validationData
	 * @return verified address
	 */
	protected AddressValidationData verifyAddresByService(final AddressData addressData, final Errors errors,
			final AddressValidationData validationData)
	{
		final AddressVerificationResult<AddressVerificationDecision> verificationDecision = addressVerificationFacade
				.verifyAddressData(addressData);
		if (verificationDecision.getErrors() != null && !verificationDecision.getErrors().isEmpty())
		{
			populateErrors(errors, verificationDecision);
			validationData.setErrors(createResponseErrors(errors));
		}

		validationData.setDecision(verificationDecision.getDecision().toString());

		if (verificationDecision.getSuggestedAddresses() != null && !verificationDecision.getSuggestedAddresses().isEmpty())
		{
			final AddressDataList addressDataList = new AddressDataList();
			addressDataList.setAddresses(verificationDecision.getSuggestedAddresses());
			validationData.setSuggestedAddressesList(addressDataList);
		}

		return validationData;
	}

	protected ErrorListWsDTO createResponseErrors(final Errors errors)
	{
		final List<ErrorWsDTO> webserviceErrorDto = new ArrayList<>();
		validationErrorConverter.convert(errors, webserviceErrorDto);
		final ErrorListWsDTO webserviceErrorList = new ErrorListWsDTO();
		webserviceErrorList.setErrors(webserviceErrorDto);
		return webserviceErrorList;
	}

	/**
	 * Populates Errors object
	 *
	 * @param errors
	 * @param addressVerificationResult
	 */
	protected void populateErrors(final Errors errors,
			final AddressVerificationResult<AddressVerificationDecision> addressVerificationResult)
	{
		addressDataErrorsPopulator.populate(addressVerificationResult, errors);
	}
}
