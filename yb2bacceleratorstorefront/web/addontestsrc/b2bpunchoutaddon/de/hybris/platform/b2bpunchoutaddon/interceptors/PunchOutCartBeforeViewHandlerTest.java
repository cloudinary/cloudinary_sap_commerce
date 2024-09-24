/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutaddon.interceptors;

import static de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants.PUNCHOUT_USER;
import static de.hybris.platform.b2bpunchoutaddon.constants.B2bpunchoutaddonConstants.VIEW_PAGE_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutCartBeforeViewHandlerTest
{
	public final static String OLD_VIEW = "/oldPage";
	public final static String NEW_VIEW = "/newPage";

	@InjectMocks
	private PunchOutBeforeViewHandler viewHandler;

	@Mock
	private MockHttpServletRequest request;
	@Mock
	private MockHttpServletResponse response;

	@Mock
	private HttpSession session;

	private ModelAndView modelAndView;

	@Before
	public void setup()
	{
		final Map<String, Map<String, String>> viewMap = new HashMap<>();
		final Map<String, String> viewName = new HashMap<>();
		viewName.put("viewName", NEW_VIEW);
		viewMap.put(OLD_VIEW, viewName);
		viewHandler.setViewMap(viewMap);

		modelAndView = new ModelAndView();
		modelAndView.setViewName(OLD_VIEW);
	}

	@Test
	public void changesViewForPunchOutUser()
	{
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(PUNCHOUT_USER)).thenReturn("myUser");

		viewHandler.beforeView(request, response, modelAndView);

		assertThat(modelAndView.getViewName()).isNotNull()
											  .isEqualTo(VIEW_PAGE_PREFIX + NEW_VIEW);
	}

	@Test
	public void keepsViewForNonPunchOutUser()
	{
		when(request.getSession()).thenReturn(session);
		when(session.getAttribute(PUNCHOUT_USER)).thenReturn(null);

		viewHandler.beforeView(request, response, modelAndView);

		assertThat(modelAndView.getViewName()).isNotNull()
											  .isEqualTo(OLD_VIEW);
	}
}
