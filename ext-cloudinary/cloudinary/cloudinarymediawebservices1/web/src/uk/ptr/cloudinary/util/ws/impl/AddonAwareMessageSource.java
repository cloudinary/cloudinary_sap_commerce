/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.util.ws.impl;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;

import com.google.common.collect.Lists;


/**
 * MessageSource which can be aware of installed addons and extract message bundles from them.
 */
public class AddonAwareMessageSource extends ReloadableResourceBundleMessageSource implements ApplicationContextAware
{
	private static final Logger LOG = LoggerFactory.getLogger(AddonAwareMessageSource.class);

	protected boolean scanForAddons;
	protected ContextResource baseAddonDir;
	protected Predicate<String> fileFilter;
	protected Predicate<String> dirFilter;
	protected List<String> basenames;
	private ApplicationContext applicationContext;

	public AddonAwareMessageSource()
	{
		this.scanForAddons = true;
		this.dirFilter = n -> {
			final String base = StringUtils.substringAfterLast(n, baseAddonDir.getPathWithinContext());
			return StringUtils.contains(base, File.separator);
		};
		this.fileFilter = n -> StringUtils.endsWithIgnoreCase(n, "xml") || StringUtils.endsWithIgnoreCase(n, "properties");
	}

	/**
	 * Searches for messages in installed addons and adds them to basenames
	 */
	@PostConstruct
	public void setupAddonMessages()
	{
		final List<String> basenameList = new ArrayList<>();

		if (baseAddonDir == null)
		{
			LOG.debug("baseLocation is null");
			return;
		}

		if (!scanForAddons)
		{
			return;
		}

		try
		{
			final String basePath = baseAddonDir.getPathWithinContext();

			final Collection<String> addonsPath = getAddonsMessages();
			final Collection<String> addonsMessages = mapAddonLocation(addonsPath, basePath);

			basenameList.addAll(addonsMessages);
		}
		catch (final Exception ex)
		{
			LOG.warn("Scan for addon messages failed", ex);
		}

		basenameList.addAll(basenames);

		final String[] result = basenameList.toArray(new String[basenameList.size()]);
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Loaded message bundles: {}", basenameList);
		}
		super.setBasenames(result);
	}

	/**
	 * Searches for files defined by fileFilter under directories defined by dirFilter.
	 *
	 * @return Collection of paths to message bundle files
	 * @throws IOException
	 */
	protected Collection<String> getAddonsMessages() throws IOException
	{
		final List<String> result = Lists.newArrayList();

		final Resource[] resources = applicationContext.getResources(baseAddonDir.getFilename() + "**");

		for (final Resource resource : resources)
		{
			final String path = resource.getURL().toExternalForm();
			if (validatePath(path) && validateFilename(path))
			{
				result.add(path);
			}
		}
		return result;
	}

	protected boolean validatePath(final String path)
	{
		if (dirFilter == null)
		{
			return true;
		}
		final String basePath = FilenameUtils.getPath(path);
		return dirFilter.test(basePath);
	}

	protected boolean validateFilename(final String path)
	{
		if (fileFilter == null)
		{
			return true;
		}
		final String filename = FilenameUtils.getName(path);
		return fileFilter.test(filename);
	}

	/**
	 * Maps each element of <b>addonsPath</b> to valid message bundle path. Result collection is also filtered to remove
	 * empty, invalid and duplicated entries.
	 *
	 * @param addonsPath
	 * 		paths to transform
	 * @param basePath
	 * 		from where result path should start
	 * @return collection of paths to message bundles
	 */
	protected Collection<String> mapAddonLocation(final Collection<String> addonsPath, final String basePath)
	{
		return addonsPath.stream().map(p -> formatPath(p, basePath)).filter(StringUtils::isNotBlank).collect(Collectors.toSet());
	}

	/**
	 * Formats absolute file path using basePath to format acceptable by @link ReloadableResourceBundleMessageSource}
	 * Basename property
	 */
	protected String formatPath(final String path, final String basePath)
	{
		int pos = path.lastIndexOf(basePath);
		//base path is not in the path -> shouldn't happen
		if (pos == -1)
		{
			return null;
		}

		final String pathFromBase = path.substring(pos);
		String fileName = FilenameUtils.getBaseName(pathFromBase);
		final String targetPath = FilenameUtils.getFullPath(pathFromBase);

		pos = fileName.indexOf('_');
		if (pos != -1)
		{
			fileName = fileName.substring(0, pos);
		}

		return FilenameUtils.concat(targetPath, fileName);
	}

	@Override
	public void setBasename(final String basename)
	{
		this.setBasenames(basename);
	}

	@Override
	public void setBasenames(final String... basenames)
	{
		this.basenames = Lists.newArrayList(basenames);
		super.setBasenames(basenames);
	}

	/**
	 * @return the scanForAddons
	 */
	public boolean isScanForAddons()
	{
		return scanForAddons;
	}

	/**
	 * @param scanForAddons
	 * 		the scanForAddons to set
	 */
	public void setScanForAddons(final boolean scanForAddons)
	{
		this.scanForAddons = scanForAddons;
	}

	/**
	 * @return the baseAddonDir
	 */
	public ContextResource getBaseAddonDir()
	{
		return baseAddonDir;
	}

	/**
	 * @param baseAddonDir
	 * 		the baseAddonDir to set
	 */
	public void setBaseAddonDir(final ContextResource baseAddonDir)
	{
		this.baseAddonDir = baseAddonDir;
	}

	/**
	 * @return the fileFilter
	 */
	public Predicate<String> getFileFilter()
	{
		return fileFilter;
	}

	/**
	 * @param fileFilter
	 * 		the fileFilter to set
	 */
	public void setFileFilter(final Predicate<String> fileFilter)
	{
		this.fileFilter = fileFilter;
	}

	/**
	 * @return the dirFilter
	 */
	public Predicate<String> getDirFilter()
	{
		return dirFilter;
	}

	/**
	 * @param dirFilter
	 * 		the dirFilter to set
	 */
	public void setDirFilter(final Predicate<String> dirFilter)
	{
		this.dirFilter = dirFilter;
	}

	@Override
	public void setApplicationContext(final ApplicationContext arg0)
	{
		applicationContext = arg0;
	}
}
