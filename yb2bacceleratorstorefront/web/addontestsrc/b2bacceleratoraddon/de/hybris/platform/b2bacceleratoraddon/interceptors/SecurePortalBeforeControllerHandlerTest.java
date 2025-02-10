/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratoraddon.interceptors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.method.HandlerMethod;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SecurePortalBeforeControllerHandlerTest
{

	private static final String OTHER_REQ_PARAMS = "other request params";

	private static final String SECURELOGIN_URI = "/secureLogin";
	private static final String DEFAULT_LOGIN_URI = "/login";
	private static final String CHECKOUT_LOGIN_URI = "/checkout-login";
	private static final String SOME_URI = "/someUri";
	private static final String HOME_URI = "/";

	@Spy
	@InjectMocks
	private SecurePortalBeforeControllerHandler beforeControllerHandler;

	@Mock
	private CMSSiteService cmsSiteService;
	@Mock
	private UserService userService;
	@Mock
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
	@Mock
	private RedirectStrategy redirectStrategy;
	@Mock
	private SecurePortalRequestProcessor requestProcessor;
	@Mock
	private SessionService sessionService;

	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private HandlerMethod handler;
	@Mock
	private CMSSiteModel site;
	@Mock
	private UserModel user;

	private final Set<String> unsecuredUris = new HashSet<String>();
	private final Set<String> controlUris = new HashSet<String>();

	@Before
	public void setup()
	{

		unsecuredUris.add(SECURELOGIN_URI);
		beforeControllerHandler.setUnsecuredUris(unsecuredUris);
		beforeControllerHandler.setControlUris(controlUris);
		beforeControllerHandler.setDefaultLoginUri(DEFAULT_LOGIN_URI);
		beforeControllerHandler.setCheckoutLoginUri(CHECKOUT_LOGIN_URI);

		when(requestProcessor.skipSecureCheck()).thenReturn(false);
		when(requestProcessor.getOtherRequestParameters(request)).thenReturn(OTHER_REQ_PARAMS);

		when(cmsSiteService.getCurrentSite()).thenReturn(site);
		when(site.isRequiresAuthentication()).thenReturn(true);

		when(userService.getCurrentUser()).thenReturn(user);
		when(userService.isAnonymousUser(user)).thenReturn(true);

		doReturn(DEFAULT_LOGIN_URI).when(beforeControllerHandler).getRedirectUrl(DEFAULT_LOGIN_URI, true, OTHER_REQ_PARAMS);

	}

	@Test
	public void givenNonLoginRequestAndUserIsAnonymous_WhenBeforeControllerIsCalled_ThenItRedirectsToLoginAndReturnsFalse()
			throws Exception
	{
		// GIVEN
		doReturn(false).when(beforeControllerHandler).isUriPartOfSet(request, unsecuredUris);
		doReturn(true).when(beforeControllerHandler).isNotLoginRequest(request);

		// WHEN-THEN
		assertFalse(beforeControllerHandler.beforeController(request, response, handler));
		verify(beforeControllerHandler).redirect(request, response, DEFAULT_LOGIN_URI);
	}

	@Test
	public void givenNonLoginRequestAndUserRememberMe_WhenBeforeControllerIsCalled_ThenItReturnsTrue()
			throws Exception
	{
		// GIVEN
		doReturn(false).when(beforeControllerHandler).isUriPartOfSet(request, unsecuredUris);
		doReturn(true).when(beforeControllerHandler).isNotLoginRequest(request);
		doReturn(true).when(beforeControllerHandler).isUserSoftLoggedIn();

		// WHEN-THEN
		assertTrue(beforeControllerHandler.beforeController(request, response, handler));
	}

	@Test
	public void givenNonLoginRequestAndUserIsNotRememberMe_WhenBeforeControllerIsCalled_ThenItRedirectsToLoginAndReturnsFalse()
			throws Exception
	{
		// GIVEN
		doReturn(false).when(beforeControllerHandler).isUriPartOfSet(request, unsecuredUris);
		doReturn(true).when(beforeControllerHandler).isNotLoginRequest(request);
		doReturn(false).when(beforeControllerHandler).isUserSoftLoggedIn();

		// WHEN-THEN
		assertFalse(beforeControllerHandler.beforeController(request, response, handler));
		verify(beforeControllerHandler).redirect(request, response, DEFAULT_LOGIN_URI);
	}

	@Test
	public void givenLoginRequestAndUserIsAnonymous_WhenBeforeControllerIsCalled_ThenItReturnsTrue() throws Exception
	{
		// GIVEN
		doReturn(false).when(beforeControllerHandler).isUriPartOfSet(request, unsecuredUris);
		doReturn(false).when(beforeControllerHandler).isNotLoginRequest(request);

		// WHEN-THEN
		assertTrue(beforeControllerHandler.beforeController(request, response, handler));
	}

	@Test
	public void givenSecureLoginPreviewRequestAndUserIsAnonymous_WhenBeforeControllerIsCalled_ThenItRedirectsToLoginAndReturnsFalse()
			throws Exception
	{
		// GIVEN
		doReturn(true).when(beforeControllerHandler).isUriPartOfSet(request, unsecuredUris);
		doReturn(true).when(beforeControllerHandler).isPreview();
		when(request.getRequestURI()).thenReturn(SECURELOGIN_URI);

		// WHEN-THEN
		assertFalse(beforeControllerHandler.beforeController(request, response, handler));
		verify(beforeControllerHandler).redirect(request, response, DEFAULT_LOGIN_URI);
	}

	@Test
	public void givenSomeUnsecuredUriNonPreviewRequestAndUserIsNonAnonymous_WhenBeforeControllerIsCalled_ThenItRedirectsToHomeAndReturnsFalse()
			throws Exception
	{
		// GIVEN
		when(userService.isAnonymousUser(user)).thenReturn(false);

		doReturn(true).when(beforeControllerHandler).isUriPartOfSet(request, unsecuredUris);
		doReturn(false).when(beforeControllerHandler).isUriPartOfSet(request, controlUris);
		doReturn(false).when(beforeControllerHandler).isPreview();
		doReturn(HOME_URI).when(beforeControllerHandler).getRedirectUrl(HOME_URI, true, OTHER_REQ_PARAMS);

		// WHEN-THEN
		assertFalse(beforeControllerHandler.beforeController(request, response, handler));
		verify(beforeControllerHandler).redirect(request, response, HOME_URI);
	}

	@Test
	public void givenSomeUnsecuredUriPreviewRequestAndUserIsNonAnonymous_WhenBeforeControllerIsCalled_ThenItReturnsTrue()
			throws Exception
	{
		// GIVEN
		when(userService.isAnonymousUser(user)).thenReturn(false);

		doReturn(true).when(beforeControllerHandler).isUriPartOfSet(request, unsecuredUris);
		doReturn(false).when(beforeControllerHandler).isUriPartOfSet(request, controlUris);
		doReturn(true).when(beforeControllerHandler).isPreview();
		when(request.getRequestURI()).thenReturn(SOME_URI);

		// WHEN-THEN
		assertTrue(beforeControllerHandler.beforeController(request, response, handler));
	}

}
