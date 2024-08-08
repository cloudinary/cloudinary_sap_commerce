/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.util.ws.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.FileSystemResource;

import static uk.ptr.cloudinary.util.ws.impl.ClasspathAwareMessageSource.classpathResourceMapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;


/**
 * Test suite for {@link ClasspathAwareMessageSource}
 */
@UnitTest
@RunWith(Parameterized.class)
public class ClasspathAwareMessageSourceTest
{
	private static final String OCC_EXT_BASE_DIR_PATH = "/occ/v2/testocc/messages/";
	private static final String OCC_EXT_ABSOLUTE_PATH = "/dev/sources/testocc/resources" + OCC_EXT_BASE_DIR_PATH;
	private static final String ADDON_BUNDLE_BASE_PATH = OCC_EXT_BASE_DIR_PATH + "base";
	private static final String RELATIVE_PATH_TO_PROPERTIES_BUNDLE = ADDON_BUNDLE_BASE_PATH;
	private static final String FILE_URL_PREFIX = "file:";

	private final String absolutePathToPropertiesFile;
	private final String relativePathToPropertiesFile;

	@Mock
	private ContextResource baseOccExtensionDir;
	private final ClasspathAwareMessageSource classpathAwareMessageSource = new ClasspathAwareMessageSource(baseOccExtensionDir);

	public ClasspathAwareMessageSourceTest(final String fileName)
	{
		absolutePathToPropertiesFile = OCC_EXT_ABSOLUTE_PATH + fileName;
		relativePathToPropertiesFile = OCC_EXT_BASE_DIR_PATH + fileName;
	}

	@Parameterized.Parameters
	public static String[] parameters()
	{
		return new String[] { "base.properties", "base_en.properties", "base_en_US.properties", "base_message_en_US.properties" };
	}

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		when(baseOccExtensionDir.getPathWithinContext()).thenReturn(OCC_EXT_BASE_DIR_PATH);
	}

	@Test
	public void mapToMessageBundleFileClassPathTest()
	{
		final FileSystemResource fileSystemResource = new FileSystemResource(absolutePathToPropertiesFile);
		final String bundleFilePath = classpathAwareMessageSource.mapToMessageBundleFilePath(fileSystemResource);
		assertEquals(FILE_URL_PREFIX + absolutePathToPropertiesFile, bundleFilePath);
	}

	@Test
	public void mapToMessageBundlePathTest()
	{
		final String bundlePath = classpathAwareMessageSource.mapToMessageBundlePath(relativePathToPropertiesFile);
		assertEquals(RELATIVE_PATH_TO_PROPERTIES_BUNDLE, bundlePath);
	}

	@Test
	public void validateCorrectFilename()
	{
		assertTrue(classpathAwareMessageSource.validateFilename("/test/path/messages.properties"));
	}

	@Test
	public void validateWrongFilename()
	{
		assertFalse(classpathAwareMessageSource.validateFilename("/test/path/messages.xml"));
	}

	@Test
	public void mapToMessageBundlePath()
	{
		assertEquals(RELATIVE_PATH_TO_PROPERTIES_BUNDLE,
				classpathAwareMessageSource.mapToMessageBundlePath(relativePathToPropertiesFile));
	}

	@Test
	public void applyOnClasspathResourceMapper()
	{
		final ContextResource contextResource = Mockito.mock(ContextResource.class);
		when(contextResource.getPathWithinContext()).thenReturn(RELATIVE_PATH_TO_PROPERTIES_BUNDLE);
		assertEquals(CLASSPATH_ALL_URL_PREFIX + RELATIVE_PATH_TO_PROPERTIES_BUNDLE, classpathResourceMapper.apply(contextResource));
	}
}
