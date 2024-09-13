/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.test.webservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.oauth2.constants.OAuth2Constants;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.testsupport.client.WebservicesAssert;
import de.hybris.platform.webservicescommons.testsupport.client.WsRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.client.WsSecuredRequestBuilder;
import de.hybris.platform.webservicescommons.testsupport.server.NeedsEmbeddedServer;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import jersey.repackaged.com.google.common.collect.Lists;
import uk.ptr.cloudinary.constants.CloudinarymediawebservicesConstants;
import uk.ptr.cloudinary.dto.SampleWsDTO;
import uk.ptr.cloudinary.dto.UserWsDTO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.ptr.cloudinary.constants.CloudinarymediawebservicesConstants.SAMPLE_LIST_DOUBLE_VALUE;
import static uk.ptr.cloudinary.constants.CloudinarymediawebservicesConstants.SAMPLE_LIST_STRING_VALUE;
import static uk.ptr.cloudinary.constants.CloudinarymediawebservicesConstants.SAMPLE_MAP_INTEGER_KEY;
import static uk.ptr.cloudinary.constants.CloudinarymediawebservicesConstants.SAMPLE_MAP_INTEGER_VALUE;
import static uk.ptr.cloudinary.constants.CloudinarymediawebservicesConstants.SAMPLE_MAP_STRING_KEY;
import static uk.ptr.cloudinary.constants.CloudinarymediawebservicesConstants.SAMPLE_MAP_STRING_VALUE;


@NeedsEmbeddedServer(webExtensions = { CloudinarymediawebservicesConstants.EXTENSIONNAME, OAuth2Constants.EXTENSIONNAME })
@IntegrationTest
public class SampleWebServicesTest extends ServicelayerTest
{
	public static final String OAUTH_CLIENT_ID = "mobile_android";
	public static final String OAUTH_CLIENT_PASS = "secret";

	private static final String BASE_URI = "sample";
	private static final String MAP_URI = BASE_URI + "/map";
	private static final String URI = BASE_URI + "/users";
	private static final String PAGED = BASE_URI + "/usersPaged";

	private WsRequestBuilder wsRequestBuilder;
	private WsSecuredRequestBuilder wsSecuredRequestBuilder;

	@Before
	public void setUp() throws Exception
	{
		wsRequestBuilder = new WsRequestBuilder()//
				.extensionName(CloudinarymediawebservicesConstants.EXTENSIONNAME);

		wsSecuredRequestBuilder = new WsSecuredRequestBuilder()//
				.extensionName(CloudinarymediawebservicesConstants.EXTENSIONNAME)//
				.client(OAUTH_CLIENT_ID, OAUTH_CLIENT_PASS)//
				.grantClientCredentials();

		createCoreData();
		createDefaultUsers();
		importCsv("/cloudinarymediawebservices/test/democustomer-data.impex", "utf-8");
	}

	@Test
	public void testGetSampleUsersWithoutAuthorization()
	{
		final Response result = wsRequestBuilder//
				.path(URI)//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.UNAUTHORIZED, result);
	}

	@Test
	public void testGetSampleUserUsingClientCredentials()
	{
		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("user1")//
				.build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, result);
	}

	@Test
	public void testGetUsersPagedUsingClientCredentials()
	{
		final Response result = wsSecuredRequestBuilder//
				.path(PAGED)//
				.queryParam("fields", "FULL").build()//
				.accept(MediaType.APPLICATION_XML)//
				.get();
		result.bufferEntity();
		WebservicesAssert.assertResponse(Status.OK, result);
		final String entity = result.readEntity(String.class);
		assertTrue(entity + "is missing <firstName>User1</firstName>", entity.contains("<firstName>User1</firstName>"));
		assertTrue(entity + "is missing <town>Berlin</town>", entity.contains("<town>Berlin</town>"));
		assertTrue(entity + "is missing <billingAddress>", entity.contains("<billingAddress>"));
	}

	@Test
	public void testPostSampleDTO()
	{
		final SampleWsDTO sampleWSDTO = new SampleWsDTO();
		sampleWSDTO.setValue("123");
		final Response result = wsSecuredRequestBuilder//
				.path("sample/dto")//
				.build()//
				.post(Entity.entity(sampleWSDTO, MediaType.APPLICATION_JSON));
		final SampleWsDTO respSampleWSDTO = result.readEntity(SampleWsDTO.class);
		assertNotNull(respSampleWSDTO);
		assertEquals("123", respSampleWSDTO.getValue());

	}

	@Test
	public void testPostEmptySampleDTO()
	{
		final SampleWsDTO sampleWSDTO = new SampleWsDTO();
		final Response response = wsSecuredRequestBuilder.path("sample/dto").build()
				.post(Entity.entity(sampleWSDTO, MediaType.APPLICATION_JSON));
		WebservicesAssert.assertResponse(Status.BAD_REQUEST, response);
		final ErrorListWsDTO errors = response.readEntity(ErrorListWsDTO.class);
		assertNotNull(errors);
		assertNotNull(errors.getErrors());
		assertEquals(1, errors.getErrors().size());
		final ErrorWsDTO error = errors.getErrors().get(0);
		assertEquals("missing", error.getReason());
		assertEquals("value", error.getSubject());
		assertEquals("parameter", error.getSubjectType());

	}

	@Test
	public void testGetObjectWithMap()
	{
		final Response result = wsSecuredRequestBuilder.path(MAP_URI).build().accept(MediaType.APPLICATION_XML).get();

		WebservicesAssert.assertResponse(Status.OK, result);
		final String entity = result.readEntity(String.class);
		assertNotNull(entity);
		assertTrue(entity.contains(SAMPLE_MAP_INTEGER_KEY));
		assertTrue(entity.contains(String.valueOf(SAMPLE_MAP_INTEGER_VALUE)));
		assertTrue(entity.contains(SAMPLE_MAP_STRING_KEY));
		assertTrue(entity.contains(SAMPLE_MAP_STRING_VALUE));
	}

	@Test
	public void testPlainString()
	{
		final StringWrapped input = new StringWrapped();
		input.setString("testString");

		final Response result = wsSecuredRequestBuilder.path(BASE_URI).path("plain/string").build()
				.accept(MediaType.APPLICATION_JSON).post(Entity.json(input));
		WebservicesAssert.assertResponse(Status.OK, result);
		final StringWrapped entity = result.readEntity(StringWrapped.class);
		assertNotNull(entity);
		assertEquals(input.string + "1", entity.string);
	}

	private static class StringWrapped
	{
		private String string;

		public void setString(final String string)
		{
			this.string = string;
		}

		public String getString()
		{
			return string;
		}
	}

	@Test
	public void testPlainLong()
	{
		final LongWrapped input = new LongWrapped();
		input.setValue(Long.valueOf(123456789L));

		final Response result = wsSecuredRequestBuilder.path(BASE_URI).path("plain/long").build().accept(MediaType.APPLICATION_JSON)
				.post(Entity.json(input));
		WebservicesAssert.assertResponse(Status.OK, result);
		final LongWrapped entity = result.readEntity(LongWrapped.class);
		assertNotNull(entity);
		assertNotNull(entity.value);
		assertEquals(123456789L + 1L, entity.value.longValue());
	}

	private static class LongWrapped
	{
		private Long value;

		public Long getValue()
		{
			return value;
		}

		public void setValue(final Long value)
		{
			this.value = value;
		}
	}

	@Test
	public void testPlainDouble()
	{
		final DoubleWrapped input = new DoubleWrapped();
		input.setValue(Double.valueOf(12345.6789d));

		final Response result = wsSecuredRequestBuilder.path(BASE_URI).path("plain/double").build()
				.accept(MediaType.APPLICATION_JSON).post(Entity.json(input));
		WebservicesAssert.assertResponse(Status.OK, result);
		final DoubleWrapped entity = result.readEntity(DoubleWrapped.class);
		assertNotNull(entity);
		assertNotNull(entity.value);
		assertEquals(12345.6789d + 1d, entity.value.doubleValue(), 0.0001d);
	}

	private static class DoubleWrapped
	{
		private Double value;

		public Double getValue()
		{
			return value;
		}

		public void setValue(final Double value)
		{
			this.value = value;
		}
	}

	@Test
	public void testGetPlainList()
	{
		final ListWrapper expected = new ListWrapper();
		expected.value = Lists.newArrayList(SAMPLE_LIST_STRING_VALUE, Double.valueOf(SAMPLE_LIST_DOUBLE_VALUE));

		final Response result = wsSecuredRequestBuilder.path(BASE_URI).path("plain/list").build().accept(MediaType.APPLICATION_JSON)
				.get();

		WebservicesAssert.assertResponse(Status.OK, result);
		final ListWrapper entity = result.readEntity(ListWrapper.class);
		assertNotNull(entity);
		assertEquals(expected.value, entity.value);
	}

	private static class ListWrapper
	{
		private List value;

		public void setValue(final List list)
		{
			this.value = list;
		}

		public List getValue()
		{
			return value;
		}
	}

	@Test
	public void testGetPlainMap()
	{
		final Map<String, Object> map = new HashMap<>();
		map.put("a", "Ala");
		map.put("b", Integer.valueOf(1));
		map.put("c", Lists.newArrayList("a", "b", "c"));

		final Response result = wsSecuredRequestBuilder.path(BASE_URI).path("plain/map").build().accept(MediaType.APPLICATION_JSON)
				.get();

		WebservicesAssert.assertResponse(Status.OK, result);
		final MapWrapped entity = result.readEntity(MapWrapped.class);
		assertNotNull(entity);
		assertNotNull(entity.value);
		assertEquals(map.get("a"), entity.value.get("a"));
		assertEquals(map.get("b"), entity.value.get("b"));
		assertEquals(map.get("c"), entity.value.get("c"));
	}

	private static class MapWrapped
	{
		private Map value;

		public void setValue(final Map value)
		{
			this.value = value;
		}

		public Map getValue()
		{
			return value;
		}
	}

	@Test
	public void testUpdateSampleUserInfoUsingClientCredentials()
	{
		final UserWsDTO userWsDTO = new UserWsDTO();
		userWsDTO.setFirstName("Sample");
		userWsDTO.setLastName("User2");
		userWsDTO.setInfo("Sample description");

		final Response result = wsSecuredRequestBuilder//
				.path(URI)//
				.path("user2")//
				.build()//
				.accept(MediaType.APPLICATION_JSON)//
				.method("PATCH", Entity.json(userWsDTO));

		WebservicesAssert.assertResponse(Status.OK, result);
		result.close();
	}
}
