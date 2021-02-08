/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.commercefacades.product.ProductExportFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductResultData;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductListWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import uk.ptr.cloudinary.formatters.WsDateFormatter;
import uk.ptr.cloudinary.product.data.ProductDataList;

import javax.annotation.Resource;

import java.util.Date;
import java.util.EnumSet;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;


/**
 * Web Services Controller to expose the functionality of the
 * {@link de.hybris.platform.commercefacades.product.ProductFacade} and SearchFacade.
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/export/products")
@Api(tags = "Export")
public class ExportController extends BaseController
{
	private static final EnumSet<ProductOption> OPTIONS = EnumSet.allOf(ProductOption.class);
	private static final String DEFAULT_PAGE_VALUE = "0";
	private static final String MAX_INTEGER = "20";

	@Resource(name = "cwsProductExportFacade")
	private ProductExportFacade productExportFacade;
	@Resource(name = "wsDateFormatter")
	private WsDateFormatter wsDateFormatter;

	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getExportedProducts", value = "Get a list of product exports.", notes = "Used for product export. Depending on the timestamp parameter, it can return all products or only products modified after the given time.", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	@ApiBaseSiteIdParam
	public ProductListWsDTO getExportedProducts(
			@ApiParam(value = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@ApiParam(value = "The number of results returned per page.") @RequestParam(defaultValue = MAX_INTEGER) final int pageSize,
			@ApiParam(value = "The catalog to retrieve products from. The catalog must be provided along with the version.") @RequestParam(required = false) final String catalog,
			@ApiParam(value = "The catalog version. The catalog version must be provided along with the catalog.") @RequestParam(required = false) final String version,
			@ApiParam(value = "When this parameter is set, only products modified after the given time will be returned. This parameter should be in ISO-8601 format (for example, 2018-01-09T16:28:45+0000).") @RequestParam(required = false) final String timestamp,
			@ApiFieldsParam @RequestParam(required = false, defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		if (StringUtils.isEmpty(catalog) && !StringUtils.isEmpty(version))
		{
			throw new RequestParameterException("Both 'catalog' and 'version' parameters have to be provided or ignored.",
					RequestParameterException.MISSING, catalog == null ? "catalog" : "version");
		}

		if (StringUtils.isEmpty(version) && !StringUtils.isEmpty(catalog))
		{
			throw new RequestParameterException("Both 'catalog' and 'version' parameters have to be provided or ignored.",
					RequestParameterException.MISSING, catalog == null ? "catalog" : "version");
		}

		if (StringUtils.isEmpty(timestamp))
		{
			return fullExport(fields, currentPage, pageSize, catalog, version);
		}
		else
		{
			return incrementalExport(fields, currentPage, pageSize, catalog, version, timestamp);
		}
	}

	protected ProductListWsDTO incrementalExport(final String fields, final int currentPage, final int pageSize,
			final String catalog, final String version, final String timestamp)
	{
		final Date timestampDate;
		try
		{
			timestampDate = wsDateFormatter.toDate(timestamp);
		}
		catch (final IllegalArgumentException e)
		{
			throw new RequestParameterException("Wrong time format. The only accepted format is ISO-8601.",
					RequestParameterException.INVALID, "timestamp", e);
		}

		final ProductResultData modifiedProducts = productExportFacade
				.getOnlyModifiedProductsForOptions(catalog, version, timestampDate, OPTIONS, currentPage, pageSize);

		return getDataMapper()
				.map(convertResultset(currentPage, pageSize, catalog, version, modifiedProducts), ProductListWsDTO.class, fields);
	}

	protected ProductListWsDTO fullExport(final String fields, final int currentPage, final int pageSize, final String catalog,
			final String version)
	{
		final ProductResultData products = productExportFacade
				.getAllProductsForOptions(catalog, version, OPTIONS, currentPage, pageSize);

		return getDataMapper()
				.map(convertResultset(currentPage, pageSize, catalog, version, products), ProductListWsDTO.class, fields);
	}

	protected ProductDataList convertResultset(final int page, final int pageSize, final String catalog, final String version,
			final ProductResultData modifiedProducts)
	{
		final ProductDataList result = new ProductDataList();
		result.setProducts(modifiedProducts.getProducts());
		if (pageSize > 0)
		{
			result.setTotalPageCount(((modifiedProducts.getTotalCount() % pageSize) == 0) ?
					(modifiedProducts.getTotalCount() / pageSize) :
					((modifiedProducts.getTotalCount() / pageSize) + 1));
		}
		result.setCurrentPage(page);
		result.setTotalProductCount(modifiedProducts.getTotalCount());
		result.setCatalog(catalog);
		result.setVersion(version);
		return result;
	}
}
