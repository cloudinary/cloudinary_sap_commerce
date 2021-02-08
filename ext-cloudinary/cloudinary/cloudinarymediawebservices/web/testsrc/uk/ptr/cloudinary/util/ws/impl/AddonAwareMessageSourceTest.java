/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.util.ws.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.File;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(Parameterized.class)
public class AddonAwareMessageSourceTest
{
	private static final String ADDON_PATH = "/Users/Develop/yenv/dev/sources/commercewebservices/cloudinarymediawebservices/web/webroot/WEB-INF/messages/addons/webserviceaddon";
	private static final String ADDON_BASE_PATH = "/WEB-INF/messages/addons/webserviceaddon/base".replace("/", File.separator);

	private final String input;
	private final String output;

	private final AddonAwareMessageSource addonAwareMessageSource;

	public AddonAwareMessageSourceTest(final String in, final String out)
	{
		input = in;
		output = out;

		addonAwareMessageSource = new AddonAwareMessageSource();
	}

	@Parameters
	public static Collection<String[]> parameters()
	{
		return Lists.newArrayList(new String[] { "/path/that/does/not/contain/basepath", null },
				new String[] { (ADDON_PATH + "/base.properties").replace("/", File.separator), ADDON_BASE_PATH },
				new String[] { (ADDON_PATH + "/base_en.properties").replace("/", File.separator), ADDON_BASE_PATH },
				new String[] { (ADDON_PATH + "/base_en_US.properties").replace("/", File.separator), ADDON_BASE_PATH },
				new String[] { (ADDON_PATH + "/base_message_en_US.properties").replace("/", File.separator), ADDON_BASE_PATH });
	}

	@Test
	public void formatPathTest()
	{
		final String formattedPath = addonAwareMessageSource
				.formatPath(input, "/WEB-INF/messages/addons/".replace("/", File.separator));
		Assert.assertEquals(output, formattedPath);
	}

}
