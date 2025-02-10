/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.eventtracking.ws.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;


public class DefaultRawEventEnricherTest {

	private DefaultRawEventEnricher defaultRawEventEnricher;

	@Mock
	private UserService userService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private CustomerModel customer;

	@Mock
	private BaseSiteModel baseSite;

	@Mock
	private HttpServletRequest request;


	@Mock
	private HttpSession session;


	private static final String SITE_ID = "test";

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		defaultRawEventEnricher = new DefaultRawEventEnricher(userService, baseSiteService, new ObjectMapper());

	}

	@Test
	public void verifyRawEventEnriched()
	{

		when(request.getSession()).thenReturn(session);
		when(session.getId()).thenReturn("sessionId");

		when(userService.getCurrentUser()).thenReturn(customer);
		when(customer.getContactEmail()).thenReturn("email@hybris.com");
		when(customer.getCustomerID()).thenReturn("customerId");

		when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		when(baseSite.getUid()).thenReturn(SITE_ID);

		String result = defaultRawEventEnricher.enrich(getJson(), request);

		assertTrue(result.contains("\"session_id\":\"sessionId\""));
		assertTrue(result.contains("\"timestamp\""));
		assertTrue(result.contains("\"user_id\":\"customerId\""));
		assertTrue(result.contains("\"user_email\":\"email@hybris.com\""));
		assertTrue(result.contains("\"base_site_id\":\"" + SITE_ID + "\""));


	}


	private String getJson(){
		return "{\"_viewts\":\"1461149581\",\"idsite\":\"electronics\",\"_refts\":\"0\",\"wma\":\"0\"," +
				"\"cvar\":\"{\\\"1\\\":[\\\"ec_id\\\",\\\"00001000\\\"],\\\"2\\\":[\\\"_pkp\\\",\\\"1\\\"],\\\"3\\\":[\\\"_pks\\\",\\\"1382080\\\"],\\\"4\\\":[\\\"_pkn\\\",\\\"EOS450'D + 18-55 IS Kit\\\"],\\\"5\\\":[\\\"_pkc\\\",\\\"\\\"]}\"," +
				"\"_idvc\":\"2\",\"dir\":\"0\",\"rec\":\"1\",\"revenue\":\"0\"," +
				"\"_idts\":\"1461149460\",\"java\":\"0\",\"_ects\":\"1461149557\"," +
				"\"_idn\":\"0\",\"gt_ms\":\"7130\",\"fla\":\"1\",\"gears\":\"0\"," +
				"\"res\":\"1920x1200\",\"qt\":\"0\"," +
				"\"urlref\":\"https:\\/\\/electronics.local:9002\\/yb2bacceleratorstorefront\\/\"," +
				"\"cookie\":\"1\"," +
				"\"ec_items\":\"[[\\\"1382080\\\",\\\"EOS450D + 18-55 IS Kit\\\",[],\\\"574.88\\\",\\\"1\\\"]]\",\"ag\":\"0\"," +
				"\"realp\":\"0\",\"h\":\"14\",\"m\":\"8\"," +
				"\"url\":\"https:\\/\\/electronics.local:9002\\/yb2bacceleratorstorefront\\/electronics\\/en\\/Open-Catalogue\\/Cameras\\/Digital-Cameras\\/Digital-SLR\\/EOS450D-%2B-18-55-IS-Kit\\/p\\/1382080\"," +
				"\"idgoal\":\"0\",\"r\":\"306416\",\"s\":\"51\",\"pdf\":\"1\"," +
				"\"eventtype\":\"ecommerce\",\"_id\":\"c35e7323191132e6\"}";
	}
}