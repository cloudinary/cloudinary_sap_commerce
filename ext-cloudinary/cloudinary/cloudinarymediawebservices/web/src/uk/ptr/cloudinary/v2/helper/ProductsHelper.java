/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.helper;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.enums.SearchQueryContext;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductCategorySearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import uk.ptr.cloudinary.util.ws.SearchQueryCodec;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;


@Component
public class ProductsHelper extends AbstractHelper
{
	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;
	@Resource(name = "cwsSearchQueryCodec")
	private SearchQueryCodec<SolrSearchQueryData> searchQueryCodec;
	@Resource(name = "solrSearchStateConverter")
	private Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;

	/**
	 * @deprecated since 6.6. Please use {@link #searchProducts(String, int, int, String, String, String)} instead.
	 */
	@Deprecated(since = "6.6", forRemoval = true)
	public ProductSearchPageWsDTO searchProducts(final String query, final int currentPage, final int pageSize, final String sort,
			final String fields)
	{
		final ProductSearchPageData<SearchStateData, ProductData> sourceResult = searchProducts(query, currentPage, pageSize, sort);
		if (sourceResult instanceof ProductCategorySearchPageData)
		{
			return getDataMapper().map(sourceResult, ProductCategorySearchPageWsDTO.class, fields);
		}

		return getDataMapper().map(sourceResult, ProductSearchPageWsDTO.class, fields);
	}

	public ProductSearchPageData<SearchStateData, ProductData> searchProducts(final String query, final int currentPage,
			final int pageSize, final String sort)
	{
		final SolrSearchQueryData searchQueryData = searchQueryCodec.decodeQuery(query);
		final PageableData pageable = createPageableData(currentPage, pageSize, sort);

		return productSearchFacade.textSearch(solrSearchStateConverter.convert(searchQueryData), pageable);
	}

	public ProductSearchPageWsDTO searchProducts(final String query, final int currentPage, final int pageSize, final String sort,
			final String fields, final String searchQueryContext)
	{
		final SearchQueryContext context = decodeContext(searchQueryContext);

		final ProductSearchPageData<SearchStateData, ProductData> sourceResult = searchProducts(query, currentPage, pageSize, sort,
				context);
		if (sourceResult instanceof ProductCategorySearchPageData)
		{
			return getDataMapper().map(sourceResult, ProductCategorySearchPageWsDTO.class, fields);
		}

		return getDataMapper().map(sourceResult, ProductSearchPageWsDTO.class, fields);
	}

	protected ProductSearchPageData<SearchStateData, ProductData> searchProducts(final String query, final int currentPage,
			final int pageSize, final String sort, final SearchQueryContext searchQueryContext)
	{
		final SolrSearchQueryData searchQueryData = searchQueryCodec.decodeQuery(query);
		searchQueryData.setSearchQueryContext(searchQueryContext);

		final PageableData pageable = createPageableData(currentPage, pageSize, sort);

		return productSearchFacade.textSearch(solrSearchStateConverter.convert(searchQueryData), pageable);
	}

	protected SearchQueryContext decodeContext(final String searchQueryContext)
	{
		if (StringUtils.isBlank(searchQueryContext))
		{
			return null;
		}

		try
		{
			return SearchQueryContext.valueOf(searchQueryContext);
		}
		catch (final IllegalArgumentException e)
		{
			throw new RequestParameterException(searchQueryContext + " context does not exist", RequestParameterException.INVALID,
					e);
		}
	}
}
