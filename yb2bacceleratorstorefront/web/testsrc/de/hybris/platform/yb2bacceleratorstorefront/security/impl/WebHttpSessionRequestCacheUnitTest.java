/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.security.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WebHttpSessionRequestCacheUnitTest
{
	@InjectMocks
	private final WebHttpSessionRequestCache cache = new WebHttpSessionRequestCache();

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private SessionService sessionService;

	@Mock
	private Authentication authentication;
	@Captor
	ArgumentCaptor<DefaultSavedRequest> savedRequestCaptor;

	@Test
	public void testSaveRequest()
	{

		SecurityContextHolder.getContext().setAuthentication(authentication);
		BDDMockito.given(request.getRequestURL()).willReturn(new StringBuffer("dummy"));
		BDDMockito.given(request.getScheme()).willReturn("dummy");
		BDDMockito.given(request.getHeader("referer")).willReturn("some blah");
		BDDMockito.given(request.getSession(false)).willReturn(null);

		cache.saveRequest(request, response);

		Mockito.verify(request.getSession()).setAttribute(Mockito.eq("SPRING_SECURITY_SAVED_REQUEST"),
				Mockito.argThat(new DefaultSavedRequestArgumentMatcher("some blah")));
	}


	@Test
	public void testSerializeSavedRequest() throws IOException
	{
		SecurityContextHolder.getContext().setAuthentication(authentication);
		BDDMockito.given(request.getRequestURL()).willReturn(new StringBuffer("dummy"));
		BDDMockito.given(request.getScheme()).willReturn("dummy");
		BDDMockito.given(request.getHeader("referer")).willReturn("some blah");
		BDDMockito.given(request.getSession(false)).willReturn(null);

		cache.saveRequest(request, response);

		Mockito.verify(request.getSession())
				.setAttribute(Mockito.eq("SPRING_SECURITY_SAVED_REQUEST"), savedRequestCaptor.capture());

		assertThat(savedRequestCaptor.getValue())
				.is(new Condition<>(this::isSerializable, "Expected savedRequest to be serializable but was not"));
	}

	private boolean isSerializable(final Object obj)
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new ByteArrayOutputStream());
			oos.writeObject(obj);
			oos.flush();
			return true;
		}
		catch(final IOException e)
		{
			Assertions.fail("Expected savedRequest to be serializable but was not", e);
		}
		return false;
	}

	@Test
	public void testCalcRedirectUrlWithEncodingAttrs()
	{
		assertEquals(
				"/",
				executeCalculateRelativeRedirectUrl("electronics/en", "/yb2bacceleratorstorefront/electronics/en",
						"https://electronics.local:9002/yb2bacceleratorstorefront/electronics/en"));
		assertEquals(
				"/",
				executeCalculateRelativeRedirectUrl("electronics/en", "/yb2bacceleratorstorefront/electronics/en",
						"https://electronics.local:9002/yb2bacceleratorstorefront/electronics/en/"));
	}


	@Test
	public void testCalcRedirectUrlWithMismatchEncodingAttrs()
	{
		assertEquals(
				"electronics/en",
				executeCalculateRelativeRedirectUrl("electronics/ja/Y/Z", "/yb2bacceleratorstorefront/electronics/ja/Y/Z",
						"https://electronics.local:9002/yb2bacceleratorstorefront/electronics/en"));
		assertEquals(
				"/",
				executeCalculateRelativeRedirectUrl("electronics/ja/Y/Z", "/yb2bacceleratorstorefront/electronics/en",
						"https://electronics.local:9002/yb2bacceleratorstorefront/electronics/en/"));
	}

	@Test
	public void testCalcRedirectUrlWithoutEncodingAttrs()
	{
		assertEquals(
				"/",
				executeCalculateRelativeRedirectUrl("", "/yb2bacceleratorstorefront",
						"https://electronics.local:9002/yb2bacceleratorstorefront"));
		assertEquals(
				"/",
				executeCalculateRelativeRedirectUrl("", "/yb2bacceleratorstorefront",
						"https://electronics.local:9002/yb2bacceleratorstorefront/"));
	}

	@Test
	public void testCalcRedirectUrlWithEncodingAttrsServletPath()
	{
		assertEquals(
				"/Open-Catalogue/Cameras/Digital-Cameras/c/575",
				executeCalculateRelativeRedirectUrl("electronics/en", "/yb2bacceleratorstorefront/electronics/en",
						"https://electronics.local:9002/yb2bacceleratorstorefront/electronics/en/Open-Catalogue/Cameras/Digital-Cameras/c/575"));
	}

	@Test
	public void testCalcRedirectUrlEmptyContextWithoutEncodingAttrs()
	{
		assertEquals("/", executeCalculateRelativeRedirectUrl("", "", "https://electronics.local:9002/"));
	}

	@Test
	public void testCalcRedirectUrlEmptyContextWithEncodingAttrs()
	{
		assertEquals(
				"/",
				executeCalculateRelativeRedirectUrl("electronics/en", "/electronics/en",
						"https://electronics.local:9002/electronics/en"));
		assertEquals(
				"/",
				executeCalculateRelativeRedirectUrl("electronics/en", "/electronics/en",
						"https://electronics.local:9002/electronics/en/"));
	}

	@Test
	public void testCalcRedirectUrlEmptyContextWithEncAttrsServletPath()
	{
		assertEquals(
				"/login",
				executeCalculateRelativeRedirectUrl("electronics/en", "/electronics/en",
						"https://electronics.local:9002/electronics/en/login"));
		assertEquals(
				"/login/",
				executeCalculateRelativeRedirectUrl("electronics/en", "/electronics/en",
						"https://electronics.local:9002/electronics/en/login/"));
		assertEquals(
				"/Open-Catalogue/Cameras/Hand-held-Camcorders/c/584",
				executeCalculateRelativeRedirectUrl("electronics/en", "/electronics/en",
						"https://electronics.local:9002/electronics/en/Open-Catalogue/Cameras/Hand-held-Camcorders/c/584"));
	}

	@Test
	public void testCalcRedirectUrlEmptyContextWithoutEncAttrsServletPath()
	{
		assertEquals(
				"Open-Catalogue/Cameras/Hand-held-Camcorders/c/584",
				executeCalculateRelativeRedirectUrl("", "",
						"https://electronics.local:9002/Open-Catalogue/Cameras/Hand-held-Camcorders/c/584"));
	}
	@Test
	public void testCalcRedirectUrlWithReferHasEncodingForStoreFront()

	{
		final String commonStoreName = "yaccacceleratorstorefront";
		assertEquals("powertools/Open-Catalogue/Cameras/Hand-held-Camcorders/c/584",
				executeCalculateRelativeRedirectUrl("powertools/en", commonStoreName + "/powertools/en",
						"https://electronics.local:9002/" + commonStoreName
								+ "/powertools/Open-Catalogue/Cameras/Hand-held-Camcorders/c/584"));

		final String emptyStoreName = "";
		assertEquals("/powertools/Open-Catalogue/Cameras/Hand-held-Camcorders/c/584",
				executeCalculateRelativeRedirectUrl("powertools/en", emptyStoreName + "/powertools/en",
						"https://electronics.local:9002/" + emptyStoreName
								+ "/powertools/Open-Catalogue/Cameras/Hand-held-Camcorders/c/584"));
	}

	protected String executeCalculateRelativeRedirectUrl(final String urlEncodingAttrs, final String contextPath, final String url)
	{
		BDDMockito.given(sessionService.getAttribute(WebConstants.URL_ENCODING_ATTRIBUTES)).willReturn(urlEncodingAttrs);
		return cache.calculateRelativeRedirectUrl(contextPath, url);
	}


	class DefaultSavedRequestArgumentMatcher implements ArgumentMatcher<DefaultSavedRequest>
	{

		private final String url;

		DefaultSavedRequestArgumentMatcher(final String url)
		{
			this.url = url;
		}

		@Override
		public boolean matches(final DefaultSavedRequest argument)
		{
			return url.equals(argument.getRedirectUrl());
		}

	}
}
