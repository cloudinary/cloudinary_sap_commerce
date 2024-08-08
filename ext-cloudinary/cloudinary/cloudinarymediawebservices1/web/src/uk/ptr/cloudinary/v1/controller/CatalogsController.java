/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v1.controller;

import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.data.CatalogData;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.commercefacades.catalog.data.CatalogsData;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller("catalogsControllerV1")
@RequestMapping(value = "/{baseSiteId}/catalogs")
public class CatalogsController extends BaseController
{
	@Resource(name = "cwsCatalogFacade")
	private CatalogFacade catalogFacade;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public CatalogsData getCatalogs(@RequestParam(required = false, defaultValue = "BASIC") final String options)
	{
		final Set<CatalogOption> opts = getOptions(options);

		final CatalogsData catalogsData = new CatalogsData();
		catalogsData.setCatalogs(catalogFacade.getAllProductCatalogsForCurrentSite(opts));

		return catalogsData;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public CatalogData getCatalog(@PathVariable final String id,
			@RequestParam(required = false, defaultValue = "BASIC") final String options)
	{

		final Set<CatalogOption> opts = getOptions(options);
		return catalogFacade.getProductCatalogForCurrentSite(id, opts);
	}

	@RequestMapping(value = "/{catalogId}/{catalogVersionId}", method = RequestMethod.GET)
	@ResponseBody
	public CatalogVersionData getCatalogVersion(@PathVariable final String catalogId, @PathVariable final String catalogVersionId,
			@RequestParam(required = false, defaultValue = "BASIC") final String options)
	{
		final Set<CatalogOption> opts = getOptions(options);
		return catalogFacade.getProductCatalogVersionForTheCurrentSite(catalogId, catalogVersionId, opts);
	}

	@RequestMapping(value = "/{catalogId}/{catalogVersionId}/categories/{category}", method = RequestMethod.GET)
	@ResponseBody
	public CategoryHierarchyData getCategories(@PathVariable final String catalogId, @PathVariable final String catalogVersionId,
			@PathVariable final String category, @RequestParam(required = false, defaultValue = "BASIC") final String options,
			@RequestParam(required = false, defaultValue = "0") final int currentPage,
			@RequestParam(required = false, defaultValue = "2147483647") final int pageSize)
	{
		final Set<CatalogOption> opts = getOptions(options);
		final PageOption page = opts.contains(CatalogOption.SUBCATEGORIES) ? PageOption.createWithoutLimits() : PageOption
				.createForPageNumberAndPageSize(currentPage, pageSize);
		return catalogFacade.getCategoryById(catalogId, catalogVersionId, category, page, opts);
	}

	protected Set<CatalogOption> getOptions(final String options)
	{
		final String[] optionsStrings = options.split(",");

		final Set<CatalogOption> opts = new HashSet<CatalogOption>();
		for (final String option : optionsStrings)
		{
			opts.add(CatalogOption.valueOf(option));
		}
		return opts;
	}
}
