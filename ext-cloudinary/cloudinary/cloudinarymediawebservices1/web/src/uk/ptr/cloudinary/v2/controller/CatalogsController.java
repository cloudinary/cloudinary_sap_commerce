/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.PageOption;
import de.hybris.platform.commercefacades.catalog.data.CatalogData;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.commercefacades.catalog.data.CatalogsData;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import de.hybris.platform.commercewebservicescommons.dto.catalog.CatalogListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.catalog.CatalogVersionWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.catalog.CatalogWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.catalog.CategoryHierarchyWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetBuilder;
import de.hybris.platform.webservicescommons.mapping.impl.FieldSetBuilderContext;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping(value = "/{baseSiteId}/catalogs")
@Api(tags = "Catalogs")
public class CatalogsController extends BaseController
{
	private static final Set<CatalogOption> OPTIONS;

	static
	{
		OPTIONS = getOptions();
	}

	@Resource(name = "cwsCatalogFacade")
	private CatalogFacade catalogFacade;
	@Resource(name = "fieldSetBuilder")
	private FieldSetBuilder fieldSetBuilder;

	protected static Set<CatalogOption> getOptions()
	{
		final Set<CatalogOption> opts = new HashSet<>();
		opts.add(CatalogOption.BASIC);
		opts.add(CatalogOption.CATEGORIES);
		opts.add(CatalogOption.SUBCATEGORIES);
		return opts;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCatalogs", value = "Get a list of catalogs", notes = "Returns all catalogs with versions defined for the base store.")
	@ApiBaseSiteIdParam
	public CatalogListWsDTO getCatalogs(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<CatalogData> catalogDataList = catalogFacade.getAllProductCatalogsForCurrentSite(OPTIONS);
		final CatalogsData catalogsData = new CatalogsData();
		catalogsData.setCatalogs(catalogDataList);

		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setRecurrencyLevel(countRecurrecyLevel(catalogDataList));
		final Set<String> fieldSet = fieldSetBuilder
				.createFieldSet(CatalogListWsDTO.class, DataMapper.FIELD_PREFIX, fields, context);

		return getDataMapper().map(catalogsData, CatalogListWsDTO.class, fieldSet);
	}

	@RequestMapping(value = "/{catalogId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCatalog", value = "Get a catalog", notes = "Returns information about a catalog based on its ID, along with the versions defined for the current base store.")
	@ApiBaseSiteIdParam
	public CatalogWsDTO getCatalog(@ApiParam(value = "Catalog identifier", required = true) @PathVariable final String catalogId,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final CatalogData catalogData = catalogFacade.getProductCatalogForCurrentSite(catalogId, OPTIONS);

		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setRecurrencyLevel(countRecurrencyForCatalogData(catalogData));
		final Set<String> fieldSet = fieldSetBuilder.createFieldSet(CatalogWsDTO.class, DataMapper.FIELD_PREFIX, fields, context);

		return getDataMapper().map(catalogData, CatalogWsDTO.class, fieldSet);
	}

	@RequestMapping(value = "/{catalogId}/{catalogVersionId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCatalogVersion", value = "Get information about catalog version", notes = "Returns information about the catalog version that exists for the current base store.")
	@ApiBaseSiteIdParam
	public CatalogVersionWsDTO getCatalogVersion(
			@ApiParam(value = "Catalog identifier", required = true) @PathVariable final String catalogId,
			@ApiParam(value = "Catalog version identifier", required = true) @PathVariable final String catalogVersionId,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final CatalogVersionData catalogVersionData = catalogFacade
				.getProductCatalogVersionForTheCurrentSite(catalogId, catalogVersionId, OPTIONS);

		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setRecurrencyLevel(countRecurrencyForCatalogVersionData(catalogVersionData));
		final Set<String> fieldSet = fieldSetBuilder
				.createFieldSet(CatalogVersionWsDTO.class, DataMapper.FIELD_PREFIX, fields, context);

		return getDataMapper().map(catalogVersionData, CatalogVersionWsDTO.class, fieldSet);
	}

	@RequestMapping(value = "/{catalogId}/{catalogVersionId}/categories/{categoryId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getCategories", value = "Get information about catagory in a catalog version", notes = "Returns information about a specified category that exists in a catalog version available for the current base store.")
	@ApiBaseSiteIdParam
	public CategoryHierarchyWsDTO getCategories(
			@ApiParam(value = "Catalog identifier", required = true) @PathVariable final String catalogId,
			@ApiParam(value = "Catalog version identifier", required = true) @PathVariable final String catalogVersionId,
			@ApiParam(value = "Category identifier", required = true) @PathVariable final String categoryId,
			@ApiFieldsParam @RequestParam(defaultValue = "DEFAULT") final String fields)
	{
		final PageOption page = PageOption.createForPageNumberAndPageSize(0, 10);
		final CategoryHierarchyData categoryHierarchyData = catalogFacade
				.getCategoryById(catalogId, catalogVersionId, categoryId, page, OPTIONS);

		final FieldSetBuilderContext context = new FieldSetBuilderContext();
		context.setRecurrencyLevel(countRecurrencyForCategoryHierarchyData(1, categoryHierarchyData));
		final Set<String> fieldSet = fieldSetBuilder
				.createFieldSet(CategoryHierarchyWsDTO.class, DataMapper.FIELD_PREFIX, fields, context);

		return getDataMapper().map(categoryHierarchyData, CategoryHierarchyWsDTO.class, fieldSet);
	}

	protected int countRecurrecyLevel(final List<CatalogData> catalogDataList)
	{
		int recurrencyLevel = 1;
		int value;
		for (final CatalogData catalog : catalogDataList)
		{
			value = countRecurrencyForCatalogData(catalog);
			if (value > recurrencyLevel)
			{
				recurrencyLevel = value;
			}
		}
		return recurrencyLevel;
	}

	protected int countRecurrencyForCatalogData(final CatalogData catalog)
	{
		int retValue = 1;
		int value;
		for (final CatalogVersionData version : catalog.getCatalogVersions())
		{
			value = countRecurrencyForCatalogVersionData(version);
			if (value > retValue)
			{
				retValue = value;
			}
		}
		return retValue;
	}

	protected int countRecurrencyForCatalogVersionData(final CatalogVersionData catalogVersion)
	{
		int retValue = 1;
		int value;
		for (final CategoryHierarchyData hierarchy : catalogVersion.getCategoriesHierarchyData())
		{
			value = countRecurrencyForCategoryHierarchyData(1, hierarchy);
			if (value > retValue)
			{
				retValue = value;
			}
		}
		return retValue;
	}

	protected int countRecurrencyForCategoryHierarchyData(final int currentValue, final CategoryHierarchyData hierarchy)
	{
		int calculatedValue = currentValue + 1;
		int subcategoryRecurrencyValue;
		for (final CategoryHierarchyData subcategory : hierarchy.getSubcategories())
		{
			subcategoryRecurrencyValue = countRecurrencyForCategoryHierarchyData(calculatedValue, subcategory);
			if (subcategoryRecurrencyValue > calculatedValue)
			{
				calculatedValue = subcategoryRecurrencyValue;
			}
		}
		return calculatedValue;
	}
}
