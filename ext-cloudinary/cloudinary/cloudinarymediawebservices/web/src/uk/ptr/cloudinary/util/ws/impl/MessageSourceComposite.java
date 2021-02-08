/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.util.ws.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;


/**
 * Composite class which combines multiple message sources into one.
 */
public class MessageSourceComposite extends ReloadableResourceBundleMessageSource
{
	private static final Logger LOG = LoggerFactory.getLogger(MessageSourceComposite.class);
	private final AddonAwareMessageSource addonAwareMessageSource;
	private final ClasspathAwareMessageSource classpathAwareMessageSource;

	public MessageSourceComposite(final AddonAwareMessageSource addonAwareMessageSource,
			final ClasspathAwareMessageSource classpathAwareMessageSource)
	{
		this.addonAwareMessageSource = addonAwareMessageSource;
		this.classpathAwareMessageSource = classpathAwareMessageSource;
	}

	@PostConstruct
	public void setupMessages()
	{
		setBasenames(getAddonAwareMessageSource().getBasenameSet().toArray(String[]::new));
		addBasenames(getClasspathAwareMessageSource().getBasenameSet().toArray(String[]::new));
		LOG.debug("Loaded message bundles: {}", getBasenameSet());
	}

	protected AddonAwareMessageSource getAddonAwareMessageSource()
	{
		return addonAwareMessageSource;
	}

	protected ClasspathAwareMessageSource getClasspathAwareMessageSource()
	{
		return classpathAwareMessageSource;
	}
}
