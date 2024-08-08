/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.filter;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.consent.AnonymousConsentFacade;
import de.hybris.platform.commercefacades.consent.CustomerConsentDataStrategy;
import de.hybris.platform.commercefacades.consent.data.AnonymousConsentData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.site.BaseSiteService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import static de.hybris.platform.commercefacades.constants.CommerceFacadesConstants.CONSENT_GIVEN;
import static de.hybris.platform.commercefacades.constants.CommerceFacadesConstants.CONSENT_WITHDRAWN;
import static de.hybris.platform.commercewebservicescommons.constants.CommercewebservicescommonsConstants.ANONYMOUS_CONSENT_HEADER;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
public class ConsentFilterTest
{
	public static final String TEMPLATE_CODE_GIVEN = "templateCodeGiven";
	public static final String TEMPLATE_CODE_WITHDRAWN = "templateCodeWithdrawn";

	private static final ObjectMapper mapper = new ObjectMapper();

	@Spy
	private final HttpServletResponse response = new MockHttpServletResponse();

	@InjectMocks
	private ConsentFilter consentFilter;

	@Mock
	private UserFacade userFacade;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private AnonymousConsentFacade anonymousConsentFacade;
	@Mock
	private CustomerConsentDataStrategy customerConsentDataStrategy;
	@Mock
	private HttpServletRequest request;
	@Mock
	private FilterChain filterChain;

	@Captor
	private ArgumentCaptor<String> headerCaptor;

	private List<AnonymousConsentData> anonymousConsents;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		// anonymous consent data setup
		final AnonymousConsentData given = createAnonymousConsent(TEMPLATE_CODE_GIVEN, 1, CONSENT_GIVEN);
		final AnonymousConsentData withdrawn = createAnonymousConsent(TEMPLATE_CODE_WITHDRAWN, 1, CONSENT_WITHDRAWN);
		anonymousConsents = Arrays.asList(given, withdrawn);

		// stubbing callbacks
		doAnswer(delegatesTo((AnonymousConsentFacade) (in, out) -> out.accept(in.get()))) //
				.when(anonymousConsentFacade).synchronizeAnonymousConsents(any(), any());

		// other methods
		when(Boolean.valueOf(userFacade.isAnonymousUser())).thenReturn(Boolean.TRUE);
		when(baseSiteService.getCurrentBaseSite()).thenReturn(mock(BaseSiteModel.class));
	}

	protected AnonymousConsentData createAnonymousConsent(final String templateCode, final int templateVersion,
			final String consentState)
	{
		final AnonymousConsentData anonymousConsent = new AnonymousConsentData();
		anonymousConsent.setConsentState(consentState);
		anonymousConsent.setTemplateCode(templateCode);
		anonymousConsent.setTemplateVersion(templateVersion);
		return anonymousConsent;
	}

	@Test
	public void shouldSkipNonAnonymousUser() throws Exception
	{
		// given
		when(Boolean.valueOf(userFacade.isAnonymousUser())).thenReturn(Boolean.FALSE);

		// when
		consentFilter.doFilterInternal(request, response, filterChain);

		// then
		verify(response, times(0)).addHeader(eq(ANONYMOUS_CONSENT_HEADER), any());
		verify(response, times(0)).setHeader(eq(ANONYMOUS_CONSENT_HEADER), any());
		verify(anonymousConsentFacade, times(0)).synchronizeAnonymousConsents(any(), any());
	}

	@Test
	public void shouldSkipWhenCurrentBaseSiteIsNotSet() throws Exception
	{
		// given
		when(baseSiteService.getCurrentBaseSite()).thenReturn(null);

		// when
		consentFilter.doFilterInternal(request, response, filterChain);

		// then
		verify(response, times(0)).addHeader(eq(ANONYMOUS_CONSENT_HEADER), any());
		verify(response, times(0)).setHeader(eq(ANONYMOUS_CONSENT_HEADER), any());
		verify(anonymousConsentFacade, times(0)).synchronizeAnonymousConsents(any(), any());
	}

	@Test
	public void shouldSetHeaderIfNotAlreadyExists() throws Exception
	{
		// given
		when(request.getHeader(ANONYMOUS_CONSENT_HEADER)).thenReturn(null);

		// when
		consentFilter.doFilterInternal(request, response, filterChain);

		// then
		verify(response).setHeader(eq(ANONYMOUS_CONSENT_HEADER), any());
	}

	@Test
	public void shouldSetHeaderWhenAnonymousUserVisitsPageFirstTime() throws Exception
	{
		// given
		doAnswer(delegatesTo((AnonymousConsentFacade) (in, out) -> {
			in.get();
			out.accept(anonymousConsents);
		})).when(anonymousConsentFacade).synchronizeAnonymousConsents(any(), any());

		// when
		consentFilter.doFilterInternal(request, response, filterChain);

		// then
		assertAllAnonymousConsentsPresent(TEMPLATE_CODE_GIVEN, TEMPLATE_CODE_WITHDRAWN);
	}

	protected void assertAllAnonymousConsentsPresent(final String... consentsId) throws IOException
	{
		final List<AnonymousConsentData> capturedAnonymousConsents = captureAnonymousConsentsFromHeader();
		assertEquals(capturedAnonymousConsents.size(), consentsId.length);
		for (int i = 0; i < consentsId.length; i++)
		{
			assertEquals(consentsId[i], capturedAnonymousConsents.get(i).getTemplateCode());
		}
	}

	protected final List<AnonymousConsentData> captureAnonymousConsentsFromHeader() throws IOException
	{
		verify(response).setHeader(eq(ANONYMOUS_CONSENT_HEADER), headerCaptor.capture());
		final String rawHeader = headerCaptor.getValue();
		final String headerValue = URLDecoder.decode(rawHeader, UTF_8);
		return Arrays.asList(mapper.readValue(headerValue, AnonymousConsentData[].class));
	}

	@Test
	public void shouldPopulateConsentsInSessionForCustomer() throws Exception
	{
		// given
		when(Boolean.valueOf(userFacade.isAnonymousUser())).thenReturn(Boolean.FALSE);

		// when
		consentFilter.doFilterInternal(request, response, filterChain);

		// then
		verify(customerConsentDataStrategy).populateCustomerConsentDataInSession();
	}
}
