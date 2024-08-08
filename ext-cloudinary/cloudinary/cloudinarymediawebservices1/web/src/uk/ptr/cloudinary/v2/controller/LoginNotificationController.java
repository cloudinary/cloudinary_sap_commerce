/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/loginnotification")
@Api(tags = "Login Notification")
public class LoginNotificationController extends BaseCommerceController
{
	private static final Logger LOG = LoggerFactory.getLogger(LoginNotificationController.class);

	@Resource(name = "wsCustomerFacade")
	private CustomerFacade customerFacade;

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_TRUSTED_CLIENT", "ROLE_CUSTOMERMANAGERGROUP" })
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiOperation(nickname = "doPublishSuccessfulLoginEvent", value = "Notify about successful login.", notes = "Publish notification event about successful login.")
	@ApiBaseSiteIdAndUserIdParam
	public void doPublishSuccessfulLoginEvent(
			@ApiParam(value = "User identifier.", required = true) @PathVariable final String userId)
	{
		customerFacade.publishLoginSuccessEvent();
		LOG.debug("Login success event notification sent");
	}
}
