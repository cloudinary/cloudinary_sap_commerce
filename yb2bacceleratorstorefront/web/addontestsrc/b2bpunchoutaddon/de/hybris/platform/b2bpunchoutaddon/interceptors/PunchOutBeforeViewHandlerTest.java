/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutaddon.interceptors;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.ModelAndView;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:b2bpunchoutaddon/web/spring/b2bpunchoutaddon-web-spring.xml",
		"classpath:b2bpunchoutaddon/test/spring/b2bpunchoutaddon-web-spring-mock.xml"})
public class PunchOutBeforeViewHandlerTest
{
	public final static String OLD_VIEW = "/oldPage";
	public final static String NEW_VIEW_FOR_PUNCHOUT_USER = "/newPage";
	public final static String ERROR_PAGE = "pages/error/errorNotFoundPage";

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();
	@InjectMocks
	private PunchOutBeforeViewHandler viewHandler;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private HttpSession session;
	@Resource
	private Set<String> punchOutAllowListUrlSet;

	private final ModelAndView modelAndView = new ModelAndView();
	private final Map<String, Map<String, String>> viewMap = new HashMap<String, Map<String, String>>();
	private final Map<String, String> viewName = new HashMap<>();
	private final Set<String> expectedPunchOutAllowListUrlSet = new HashSet<>(Arrays.asList(
			"addon:/b2bacceleratoraddon/pages/product/productLayout2Page",
			"addon:/profiletagaddon/fragments/cart/addToCartPopup",
			"fragments/cart/cartPopup",
			"fragments/cart/addToCartPopup",
			"fragments/cart/miniCartPanel",
			"fragments/product/futureStockPopup",
			"addon:/b2bpunchoutaddon/pages/punchout/punchoutSendOrderPage",
			"addon:/b2bpunchoutaddon/pages/cart/inspect"));

	@Before
	public void setup()
	{
		viewName.put("viewName", NEW_VIEW_FOR_PUNCHOUT_USER);
		viewMap.put(OLD_VIEW, viewName);
		viewHandler.setViewMap(viewMap);
		viewHandler.setPunchOutAllowListUrlSet(punchOutAllowListUrlSet);
	}

	@Test
	public void changeInvalidViewToPageNotFoundForPunchOutUser() throws Exception
	{
		modelAndView.setViewName("exactlyInvalidView");
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER)).thenReturn("punchOutUser");

		viewHandler.beforeView(request, response, modelAndView);
		verify(response).sendRedirect(ERROR_PAGE);
	}

	@Test
	public void keepViewForNonPunchOutUser() throws Exception
	{
		modelAndView.setViewName(OLD_VIEW);
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER)).thenReturn(null);

		viewHandler.beforeView(request, response, modelAndView);
		assertThat(modelAndView.getViewName()).isEqualTo(OLD_VIEW);
	}

	@Test
	public void changeViewForPunchOutUser() throws Exception
	{
		modelAndView.setViewName(OLD_VIEW);
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER)).thenReturn("punchOutUser");

		viewHandler.beforeView(request, response, modelAndView);
		assertThat(modelAndView.getViewName()).isEqualTo(B2bpunchoutaddonConstants.VIEW_PAGE_PREFIX + NEW_VIEW_FOR_PUNCHOUT_USER);
	}

	@Test
	public void keepAllowListViewForPunchOutUser() throws Exception
	{
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(B2bpunchoutaddonConstants.PUNCHOUT_USER)).thenReturn("punchOutUser");

		this.punchOutAllowListUrlSet.forEach(viewName -> {
			modelAndView.setViewName(viewName);
			viewHandler.beforeView(request, response, modelAndView);
			assertThat(modelAndView.getViewName()).isEqualTo(viewName);
		});
	}

	@Test
	public void checkPunchOutWhiteListUrl()
	{
		assertThat(this.punchOutAllowListUrlSet).isEqualTo(this.expectedPunchOutAllowListUrlSet);
	}
}
