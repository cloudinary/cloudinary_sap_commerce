/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.util.ws.impl;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import static java.util.stream.Collectors.toSet;
import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;


/**
 * MessageSource which can be aware of OCC extensions and extract messages bundles from them.
 */
public class ClasspathAwareMessageSource extends ReloadableResourceBundleMessageSource
{
	protected static final Predicate<String> FILE_FILTER = n -> StringUtils.endsWithIgnoreCase(n, "properties");
	protected static final Function<ContextResource, String> classpathResourceMapper = r -> CLASSPATH_ALL_URL_PREFIX //
			+ r.getPathWithinContext();
	private static final Logger LOG = LoggerFactory.getLogger(ClasspathAwareMessageSource.class);
	protected final ContextResource baseOccExtensionDir;
	protected final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	public ClasspathAwareMessageSource(final ContextResource baseOccExtensionDir)
	{
		this.baseOccExtensionDir = baseOccExtensionDir;
	}

	@PostConstruct
	public void setupMessages()
	{
		if (Objects.isNull(getBaseOccExtensionDir()))
		{
			LOG.debug("Base OCC extension directory for messages is not set.");
		}
		else
		{
			super.setBasenames(getMessagesSet().toArray(String[]::new));
			LOG.debug("Loaded message bundles: {}", getBasenameSet());
		}
	}

	protected Set<String> getMessagesSet()
	{
		if (getBaseOccExtensionDir() != null)
		{
			try
			{
				final String baseResourcePath = classpathResourceMapper.apply(getBaseOccExtensionDir());
				final String messagesResourcesPath = baseResourcePath + "**";
				return Stream.of(resolver.getResources(messagesResourcesPath)) //
						.map(this::mapToMessageBundleFilePath) //
						.filter(this::validateFilename) //
						.map(this::mapToMessageBundlePath) //
						.collect(toSet());
			}
			catch (final IOException ex)
			{
				LOG.debug("Scan for OCC extension messages failed", ex);
			}
			catch (final IllegalArgumentException ex)
			{
				LOG.warn("Scan for OCC extension messages failed", ex);
			}
		}
		return Collections.emptySet();
	}

	protected String mapToMessageBundleFilePath(final Resource resource)
	{
		try
		{
			return resource.getURI().toString();
		}
		catch (final IOException ex)
		{
			throw new IllegalArgumentException("Resource file not found", ex);
		}
	}

	protected boolean validateFilename(final String path)
	{
		final String filename = FilenameUtils.getName(path);
		return FILE_FILTER.test(filename);
	}

	protected String mapToMessageBundlePath(final String filePath)
	{
		final String filename = FilenameUtils.getName(filePath);
		final int firstUnderscoreIndex = filename.indexOf('_');
		if (firstUnderscoreIndex != -1)
		{
			final String messageBundleName = filename.substring(0, firstUnderscoreIndex);
			return filePath.replace(filename, messageBundleName);
		}
		return FilenameUtils.removeExtension(filePath);
	}

	protected ContextResource getBaseOccExtensionDir()
	{
		return baseOccExtensionDir;
	}

}
