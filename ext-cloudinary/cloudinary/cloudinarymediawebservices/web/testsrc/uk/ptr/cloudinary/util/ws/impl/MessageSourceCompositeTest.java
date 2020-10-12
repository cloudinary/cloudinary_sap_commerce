/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.util.ws.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


/**
 * Test suite for {@link MessageSourceComposite}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MessageSourceCompositeTest
{
	private static final String TEST_OCC_EXT_MESSAGES_BUNDLE_RELATIVE_PATH = "classpath:/occ/v2/testocc/messages/base";
	private static final String TEST_ADDON_MESSAGES_BUNDLE_RELATIVE_PATH = "/WEB-INF/messages/addons/testaddon/messages";
	@Mock
	private AddonAwareMessageSource addonAwareMessageSource;
	@Mock
	private ClasspathAwareMessageSource classpathAwareMessageSource;
	private MessageSourceComposite messageSourceComposite;

	@Before
	public void setUp()
	{
		when(addonAwareMessageSource.getBasenameSet()).thenReturn(Sets.newSet(TEST_ADDON_MESSAGES_BUNDLE_RELATIVE_PATH));
		when(classpathAwareMessageSource.getBasenameSet()).thenReturn(Sets.newSet(TEST_OCC_EXT_MESSAGES_BUNDLE_RELATIVE_PATH));
		messageSourceComposite = new MessageSourceComposite(addonAwareMessageSource, classpathAwareMessageSource);
	}

	@Test
	public void setupMessagesTest()
	{
		messageSourceComposite.setupMessages();
		assertEquals(2, messageSourceComposite.getBasenameSet().size());
		assertTrue(messageSourceComposite.getBasenameSet().contains(TEST_ADDON_MESSAGES_BUNDLE_RELATIVE_PATH));
		assertTrue(messageSourceComposite.getBasenameSet().contains(TEST_OCC_EXT_MESSAGES_BUNDLE_RELATIVE_PATH));
	}
}
