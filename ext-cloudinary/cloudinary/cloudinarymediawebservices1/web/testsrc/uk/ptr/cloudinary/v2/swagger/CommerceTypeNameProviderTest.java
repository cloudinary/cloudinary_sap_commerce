package uk.ptr.cloudinary.v2.swagger;


import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class CommerceTypeNameProviderTest
{
	private CommerceTypeNameProvider commerceTypeNameProvider;

	@Before
	public void setUp()
	{
		commerceTypeNameProvider = new CommerceTypeNameProvider();
	}

	@Test
	public void testSuffixNotInDefaultTypeName()
	{
		final String expectedTypeName = "Test";
		final String actualTypeName = commerceTypeNameProvider.nameFor(TestWsDTO.class);
		Assert.assertEquals(expectedTypeName, actualTypeName);
	}

	static class TestWsDTO
	{
		// Used for testing
	}
}
