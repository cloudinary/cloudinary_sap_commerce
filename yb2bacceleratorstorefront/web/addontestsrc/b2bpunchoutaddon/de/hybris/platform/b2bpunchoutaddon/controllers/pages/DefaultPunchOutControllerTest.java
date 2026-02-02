/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutaddon.controllers.pages;

import static de.hybris.platform.b2bpunchoutaddon.controllers.pages.DefaultPunchOutController.ADDON_PREFIX;
import static de.hybris.platform.b2bpunchoutaddon.controllers.pages.DefaultPunchOutController.BASE_ADDON_PAGE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.enums.PunchOutLevel;
import de.hybris.platform.b2b.punchout.security.PunchOutUserAuthenticationStrategy;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.configuration2.Configuration;
import org.cxml.CXML;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.ui.Model;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutControllerTest
{
	private static final String SESSION_ID = "session1234";
	private static final String USER_ID = "TestID";
	private static final String SELECTED_ITEM= "SelectedItem123";
	private static final String TARGET_PRODUCT_URL = "p/TargetID123";

	@InjectMocks
	private DefaultPunchOutController defaultPunchOutController;

	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private PunchOutSessionService punchoutSessionService;
	@Mock
	private PunchOutUserAuthenticationStrategy punchOutUserAuthenticationStrategy;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private RedirectStrategy redirectStrategy;
	@Mock
	private HttpSession session;
	@Mock
	private PunchOutSession punchOutSession;
	@Mock
	private Model model;
	@Mock
	private PunchOutService punchOutService;
	@Mock
	private CXML cxml;
	@Mock
	private CMSPageService cmsPageService;
	@Mock
	private CMSPreviewService cmsPreviewService;
	@Mock
	private Configuration configuration;
	@Mock
	private ProductFacade productFacade;
	@Mock
	private ProductData productData;

	@Before
	public void prepare()
	{
		when(punchoutSessionService.loadPunchOutSession(SESSION_ID)).thenReturn(punchOutSession);
		when(punchoutSessionService.retrieveUserId(punchOutSession)).thenReturn(USER_ID);
		when(request.getSession()).thenReturn(session);
		when(punchOutService.processCancelPunchOutOrderMessage()).thenReturn(cxml);
		when(punchoutSessionService.getCurrentPunchOutSession()).thenReturn(punchOutSession);
		when(punchOutSession.getBrowserFormPostUrl()).thenReturn("url");
		when(punchOutService.processPunchOutOrderMessage()).thenReturn(cxml);
		when(configurationService.getConfiguration()).thenReturn(configuration);
	}

	@Test
	public void testHandlePunchOutSession() throws IOException
	{
		when(punchOutSession.getPunchoutLevel()).thenReturn(PunchOutLevel.PRODUCT);
		when(punchOutSession.getSelectedItem()).thenReturn(SELECTED_ITEM);
		when(productData.getUrl()).thenReturn(TARGET_PRODUCT_URL);
		when(productFacade.getProductForCodeAndOptions(SELECTED_ITEM, Arrays.asList(ProductOption.URL))).thenReturn(productData);

		defaultPunchOutController.handlePunchOutSession(SESSION_ID, request, response);

		verify(punchoutSessionService).loadPunchOutSession(SESSION_ID);
		verify(punchoutSessionService).retrieveUserId(punchOutSession);
		verify(request).getSession();
		verify(session).removeAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER);
		verify(punchOutUserAuthenticationStrategy).authenticate(USER_ID, request, response);
		verify(punchoutSessionService).setCurrentCartFromPunchOutSetup(SESSION_ID);
		verify(session).setAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER, USER_ID);

		verify(productFacade).getProductForCodeAndOptions(SELECTED_ITEM, Arrays.asList(ProductOption.URL));
	}

	@Test
	public void getTargetPageWhenProductIsExisting ()
	{
		when(punchOutSession.getPunchoutLevel()).thenReturn(PunchOutLevel.PRODUCT);
		when(punchOutSession.getSelectedItem()).thenReturn(SELECTED_ITEM);
		when(productData.getUrl()).thenReturn(TARGET_PRODUCT_URL);
		when(productFacade.getProductForCodeAndOptions(SELECTED_ITEM, Arrays.asList(ProductOption.URL))).thenReturn(productData);

		final String result = defaultPunchOutController.getTargetPage(punchOutSession, "/");

		verify(productFacade).getProductForCodeAndOptions(SELECTED_ITEM, Arrays.asList(ProductOption.URL));
		assertThat(result).contains(productData.getUrl());
	}

	@Test
	public void getTargetPageWhenProductIsNotExisting ()
	{
		when(punchOutSession.getPunchoutLevel()).thenReturn(PunchOutLevel.PRODUCT);
		when(punchOutSession.getSelectedItem()).thenReturn(SELECTED_ITEM);
		when(productFacade.getProductForCodeAndOptions(SELECTED_ITEM, Arrays.asList(ProductOption.URL))).thenThrow(
				UnknownIdentifierException.class);

		final String result = defaultPunchOutController.getTargetPage(punchOutSession, "/");

		verify(productFacade).getProductForCodeAndOptions(SELECTED_ITEM, Arrays.asList(ProductOption.URL));
		assertEquals("/404", result);
	}

	@Test
	public void getTargetPageWhenPunchoutSessionIsNotProduct ()
	{
		when(punchOutSession.getPunchoutLevel()).thenReturn(PunchOutLevel.STORE);

		final String result = defaultPunchOutController.getTargetPage(punchOutSession, "/");

		verify(productFacade, times(0)).getProductForCodeAndOptions(SELECTED_ITEM, Arrays.asList(ProductOption.URL));
		assertEquals("/", result);

	}

	@Test
	public void testCancelRequisition() throws CMSItemNotFoundException
	{
		final String r = defaultPunchOutController.cancelRequisition(model);

		assertThat(r).isEqualTo(ADDON_PREFIX + BASE_ADDON_PAGE_PATH + "/punchout/punchoutSendOrderPage");
		verify(punchOutService, Mockito.times(1)).processCancelPunchOutOrderMessage();
	}

	@Test
	public void testPlaceRequisition() throws CMSItemNotFoundException
	{
		final String r = defaultPunchOutController.placeRequisition(model, request);
		assertThat(r).isEqualTo(ADDON_PREFIX + BASE_ADDON_PAGE_PATH + "/punchout/punchoutSendOrderPage");
		verify(punchOutService).processPunchOutOrderMessage();
	}
}
