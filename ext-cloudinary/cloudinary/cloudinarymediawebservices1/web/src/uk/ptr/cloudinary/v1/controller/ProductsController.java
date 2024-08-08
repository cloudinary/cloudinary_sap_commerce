/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v1.controller;

import static uk.ptr.cloudinary.constants.YcommercewebservicesConstants.ENUM_VALUES_SEPARATOR;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.catalog.CatalogFacade;
import de.hybris.platform.commercefacades.product.ProductExportFacade;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.ProductReferencesData;
import de.hybris.platform.commercefacades.product.data.ProductResultData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercefacades.product.data.SuggestionData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.AutocompleteSuggestionData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.storefinder.StoreFinderStockFacade;
import de.hybris.platform.commercefacades.storefinder.data.StoreFinderStockSearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.FacetSearchPageData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.store.data.GeoPoint;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.StockSystemException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import uk.ptr.cloudinary.formatters.WsDateFormatter;
import uk.ptr.cloudinary.product.data.ProductDataList;
import uk.ptr.cloudinary.product.data.SuggestionDataList;
import uk.ptr.cloudinary.queues.data.ProductExpressUpdateElementData;
import uk.ptr.cloudinary.queues.data.ProductExpressUpdateElementDataList;
import uk.ptr.cloudinary.queues.impl.ProductExpressUpdateQueue;
import uk.ptr.cloudinary.stock.CommerceStockFacade;
import uk.ptr.cloudinary.util.ws.SearchQueryCodec;
import uk.ptr.cloudinary.validator.PointOfServiceValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;


/**
 * Web Services Controller to expose the functionality of the {@link ProductFacade} and SearchFacade.
 */
@Controller("productsControllerV1")
@RequestMapping(value = "/{baseSiteId}/products")
public class ProductsController extends BaseController
{
	private static final String BASIC_OPTION = "BASIC";
	private static final String MAX_INTEGER = "2147483647";
	private static final String DEFAULT_PAGE_VALUE = "0";
	private static final int CATALOG_ID_POS = 0;
	private static final int CATALOG_VERSION_POS = 1;
	private static final Logger LOG = Logger.getLogger(ProductsController.class);
	@Resource(name = "storeFinderStockFacade")
	private StoreFinderStockFacade storeFinderStockFacade;
	@Resource(name = "cwsProductFacade")
	private ProductFacade productFacade;
	@Resource(name = "cwsProductExportFacade")
	private ProductExportFacade productExportFacade;
	@Resource(name = "cwsSearchQueryCodec")
	private SearchQueryCodec<SolrSearchQueryData> searchQueryCodec;
	@Resource(name = "wsDateFormatter")
	private WsDateFormatter wsDateFormatter;
	@Resource(name = "productSearchFacade")
	private ProductSearchFacade<ProductData> productSearchFacade;
	@Resource(name = "solrSearchStateConverter")
	private Converter<SolrSearchQueryData, SearchStateData> solrSearchStateConverter;
	@Resource(name = "httpRequestReviewDataPopulator")
	private Populator<HttpServletRequest, ReviewData> httpRequestReviewDataPopulator;
	@Resource(name = "reviewValidator")
	private Validator reviewValidator;
	@Resource(name = "productExpressUpdateQueue")
	private ProductExpressUpdateQueue productExpressUpdateQueue;
	@Resource(name = "catalogFacade")
	private CatalogFacade catalogFacade;
	@Resource(name = "commerceStockFacade")
	private CommerceStockFacade commerceStockFacade;
	@Resource(name = "pointOfServiceValidator")
	private PointOfServiceValidator pointOfServiceValidator;

	/**
	 * Web service handler for search. Implementation has to catch up once the SearchFacade exists.
	 * 
	 * @param query
	 *           serialized query in format: freeTextSearch:sort:facetKey1:facetValue1:facetKey2:facetValue2
	 * @param currentPage
	 *           the current result page requested
	 * @param pageSize
	 *           the number of results returned per page
	 * @param sort
	 *           sorting method applied to the display search results
	 * @return {@link FacetSearchPageData}
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ProductSearchPageData<SearchStateData, ProductData> searchProducts(@RequestParam(required = false) final String query,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = "20") final int pageSize,
			@RequestParam(required = false) final String sort)
	{
		final SolrSearchQueryData searchQueryData = searchQueryCodec.decodeQuery(query);
		final PageableData pageable = new PageableData();
		pageable.setCurrentPage(currentPage);
		pageable.setPageSize(pageSize);
		pageable.setSort(sort);

		return productSearchFacade.textSearch(solrSearchStateConverter.convert(searchQueryData), pageable);
	}

	/**
	 * Web service handler for getting stock level in a given store.<br/>
	 * Sample Call: http://localhost:9001/rest/v1/:site/products/:code/stock?storeName=
	 * 
	 * @param storeName
	 *           name of the store
	 * @return {@link StockData}
	 * @throws WebserviceValidationException
	 * @throws StockSystemException
	 */
	@RequestMapping(value = "/{productCode}/stock", method = RequestMethod.GET)
	@ResponseBody
	public StockData getStockData(@PathVariable final String baseSiteId, @PathVariable final String productCode,
			@RequestParam(required = true) final String storeName) throws WebserviceValidationException, StockSystemException //NOSONAR
	{
		final Errors errors = new BeanPropertyBindingResult(storeName, "storeName");
		pointOfServiceValidator.validate(storeName, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		if (!commerceStockFacade.isStockSystemEnabled(baseSiteId))
		{
			throw new StockSystemException("Stock system is not enabled on this site", StockSystemException.NOT_ENABLED, baseSiteId);
		}
		return commerceStockFacade.getStockDataForProductAndPointOfService(productCode, storeName);
	}

	/**
	 * Web service handler for product export. If no 'options' query parameter is defined, it will assume BASIC. The
	 * options are turned into a Set<ProductOption> and passed on to the facade. <br>
	 * Sample Call: http://localhost:9001/rest/v1/{SITE}/products/export/full
	 * 
	 * @param currentPage
	 *           - index position of the first Product, which will be included in the returned List
	 * @param pageSize
	 *           - number of Products which will be returned in each page
	 * @param options
	 *           - a String enumerating the detail level, values are BASIC, PROMOTIONS, STOCK, REVIEW, CLASSIFICATION,
	 *           REFERENCES. Combine by using a ',', which needs to be encoded as part of a URI using URLEncoding: %2C
	 * @return {@link ProductDataList}
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/export/full", method = RequestMethod.GET)
	@ResponseBody
	public ProductDataList exportProducts(
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize,
			@RequestParam(required = false, defaultValue = BASIC_OPTION) final String options,
			@RequestParam(required = false) final String catalog, @RequestParam(required = false) final String version)
	{
		final Set<ProductOption> opts = extractOptions(options);

		final ProductResultData products = productExportFacade.getAllProductsForOptions(catalog, version, opts, currentPage,
				pageSize);

		return convertResultset(currentPage, pageSize, catalog, version, products);
	}

	/**
	 * Web service handler for incremental product export. Timestamp specifies which product to export. If no 'options'
	 * query parameter is defined, it will assume BASIC. The options are turned into a Set<ProductOption> and passed on
	 * to the facade. <br>
	 * Sample Call: http://localhost:9001/rest/v1/{SITE}/products/export/incremental
	 * 
	 * @param currentPage
	 *           - index position of the first Product, which will be included in the returned List
	 * @param pageSize
	 *           - number of Products which will be returned in each page
	 * @param options
	 *           - a String enumerating the detail level, values are BASIC, PROMOTIONS, STOCK, REVIEW, CLASSIFICATION,
	 *           REFERENCES. Combine by using a ',', which needs to be encoded as part of a URI using URLEncoding: %2C
	 * @param catalog
	 *           catalog from which get products
	 * @param version
	 *           version of catalog
	 * @param timestamp
	 *           time in ISO-8601 format
	 * @return {@link ProductDataList}
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/export/incremental", method = RequestMethod.GET)
	@ResponseBody
	public ProductDataList exportProducts(
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize,
			@RequestParam(required = false, defaultValue = BASIC_OPTION) final String options,
			@RequestParam(required = false) final String catalog, @RequestParam(required = false) final String version,
			@RequestParam final String timestamp)
	{
		final Set<ProductOption> opts = extractOptions(options);

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

		final ProductResultData modifiedProducts = productExportFacade.getOnlyModifiedProductsForOptions(catalog, version,
				timestampDate, opts, currentPage, pageSize);

		return convertResultset(currentPage, pageSize, catalog, version, modifiedProducts);
	}

	/**
	 * Web service handler for export product references. Reference type specifies which references to return. If no
	 * 'options' query parameter is defined, it will assume BASIC. The options are turned into a Set<ProductOption> and
	 * passed on to the facade. Sample Call:
	 * http://localhost:9001/rest/v1/{SITE}/products/export/references/{code}?referenceType
	 * =CROSSELLING&catalog=hwcatalog&version=Online
	 * 
	 * @param code
	 *           - product code
	 * @param referenceType
	 *           - reference type according to enum ProductReferenceTypeEnum
	 * @param pageSize
	 *           - number of Products which will be returned in each page
	 * @param options
	 *           - a String enumerating the detail level, values are BASIC, PROMOTIONS, STOCK, REVIEW, CLASSIFICATION,
	 *           REFERENCES. Combine by using a ',', which needs to be encoded as part of a URI using URLEncoding: %2C
	 * @return collection of {@link ProductReferenceData}
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/export/references/{code}", method = RequestMethod.GET)
	@ResponseBody
	public ProductReferencesData exportProductReferences(@PathVariable final String code,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize,
			@RequestParam(required = false, defaultValue = BASIC_OPTION) final String options,
			@RequestParam final String referenceType)
	{
		final List<ProductOption> opts = Lists.newArrayList(extractOptions(options));
		final ProductReferenceTypeEnum referenceTypeEnum = ProductReferenceTypeEnum.valueOf(referenceType);

		final List<ProductReferenceData> productReferences = productFacade.getProductReferencesForCode(code,
				Arrays.asList(referenceTypeEnum), opts, Integer.valueOf(pageSize));

		final ProductReferencesData productReferencesData = new ProductReferencesData();
		productReferencesData.setReferences(productReferences);

		return productReferencesData;
	}

	/**
	 * Web service handler for product express update. Returns only elements newer than timestamp. Sample Call:
	 * http://localhost:9001/rest/v1/{SITE}/products/expressUpdate<br>
	 * This method requires trusted client authentication.<br>
	 * Method type : <code>GET</code>.<br>
	 * Method is restricted for <code>HTTPS</code> channel.
	 * 
	 * @param timestamp
	 *           - time in ISO-8601 format
	 * @param catalog
	 *           - the product catalog to return queue for. If not set all products from all catalogs in queue will be
	 *           returned. Format: catalogId:catalogVersion
	 * @return {@link ProductExpressUpdateElementDataList}
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/expressUpdate", method = RequestMethod.GET)
	@ResponseBody
	public ProductExpressUpdateElementDataList expressUpdate(@RequestParam final String timestamp,
			@RequestParam(required = false) final String catalog) throws RequestParameterException //NOSONAR
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

		final ProductExpressUpdateElementDataList productExpressUpdateDataList = new ProductExpressUpdateElementDataList();
		productExpressUpdateDataList.setProductExpressUpdateElements(productExpressUpdateQueue.getItems(timestampDate));
		filterExpressUpdateQueue(productExpressUpdateDataList, validateAndSplitCatalog(catalog));
		return productExpressUpdateDataList;
	}

	protected void filterExpressUpdateQueue(final ProductExpressUpdateElementDataList productExpressUpdateDataList,
			final List<String> catalogInfo)
	{
		if (catalogInfo.size() == 2 && StringUtils.isNotEmpty(catalogInfo.get(CATALOG_ID_POS))
				&& StringUtils.isNotEmpty(catalogInfo.get(CATALOG_VERSION_POS))
				&& CollectionUtils.isNotEmpty(productExpressUpdateDataList.getProductExpressUpdateElements()))
		{
			final Iterator<ProductExpressUpdateElementData> dataIterator = productExpressUpdateDataList
					.getProductExpressUpdateElements().iterator();
			while (dataIterator.hasNext())
			{
				final ProductExpressUpdateElementData productExpressUpdateElementData = dataIterator.next();
				if (!catalogInfo.get(CATALOG_ID_POS).equals(productExpressUpdateElementData.getCatalogId())
						|| !catalogInfo.get(CATALOG_VERSION_POS).equals(productExpressUpdateElementData.getCatalogVersion()))
				{
					dataIterator.remove();
				}
			}
		}
	}

	protected List<String> validateAndSplitCatalog(final String catalog) throws RequestParameterException //NOSONAR
	{
		final List<String> catalogInfo = new ArrayList<>();
		if (StringUtils.isNotEmpty(catalog))
		{
			catalogInfo.addAll(Lists.newArrayList(Splitter.on(':').trimResults().omitEmptyStrings().split(catalog)));
			if (catalogInfo.size() == 2)
			{
				catalogFacade.getProductCatalogVersionForTheCurrentSite(catalogInfo.get(CATALOG_ID_POS),
						catalogInfo.get(CATALOG_VERSION_POS), Collections.emptySet());
			}
			else if (!catalogInfo.isEmpty())
			{
				throw new RequestParameterException("Invalid format. You have to provide catalog as 'catalogId:catalogVersion'",
						RequestParameterException.INVALID, "catalog");
			}
		}
		return catalogInfo;
	}

	protected ProductDataList convertResultset(final int page, final int pageSize, final String catalog, final String version,
			final ProductResultData modifiedProducts)
	{
		final ProductDataList result = new ProductDataList();
		result.setProducts(modifiedProducts.getProducts());
		if (pageSize > 0)
		{
			result.setTotalPageCount((modifiedProducts.getTotalCount() % pageSize == 0) ? modifiedProducts.getTotalCount()
					/ pageSize : modifiedProducts.getTotalCount() / pageSize + 1);
		}
		result.setCurrentPage(page);
		result.setTotalProductCount(modifiedProducts.getTotalCount());
		result.setCatalog(catalog);
		result.setVersion(version);
		return result;
	}

	protected Set<ProductOption> extractOptions(final String options)
	{
		final String[] optionsStrings = options.split(ENUM_VALUES_SEPARATOR);

		final Set<ProductOption> opts = new HashSet<ProductOption>();
		for (final String option : optionsStrings)
		{
			opts.add(ProductOption.valueOf(option));
		}
		return opts;
	}

	/**
	 * Web service handler for the getProductByCode call. If no 'options' query parameter is defined, it will assume
	 * BASIC. The options are turned into a Set<ProductOption> and passed on to the facade. Sample Call:
	 * http://localhost:9001/rest/v1/{SITE}/products/{CODE}?options=BASIC%2CPROMOTIONS Keep in mind ',' needs to be
	 * encoded as %2C
	 * 
	 * @param code
	 *           - the unique code used to identify a product
	 * @param options
	 *           - a String enumerating the detail level, values are BASIC, PROMOTIONS, STOCK, REVIEW, CLASSIFICATION,
	 *           REFERENCES. Combine by using a ',', which needs to be encoded as part of a URI using URLEncoding: %2C
	 * @return the ProdcutData DTO which will be marshaled to JSON or XML based on Accept-Header
	 */
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	public ProductData getProductByCode(@PathVariable final String code,
			@RequestParam(required = false, defaultValue = BASIC_OPTION) final String options)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getProductByCode: code=" + sanitize(code) + " | options=" + sanitize(options));
		}

		final Set<ProductOption> opts = extractOptions(options);

		return productFacade.getProductForCodeAndOptions(code, opts);
	}

	/**
	 * Web service handler for giving the auto complete suggestions as List<String>
	 * 
	 * @param term
	 *           - the term that user inputs for search
	 * @param max
	 *           - the limit of the suggestions
	 * @return the list of auto suggestions
	 */
	@RequestMapping(value = "/suggest", method = RequestMethod.GET)
	@ResponseBody
	public SuggestionDataList getSuggestions(@RequestParam(required = true) final String term,
			@RequestParam(required = true, defaultValue = "10") final int max)
	{
		final List<SuggestionData> suggestions = new ArrayList<SuggestionData>();
		final List<AutocompleteSuggestionData> autoSuggestions;
		if (max < productSearchFacade.getAutocompleteSuggestions(term).size())
		{
			autoSuggestions = productSearchFacade.getAutocompleteSuggestions(term).subList(0, max);
		}
		else
		{
			autoSuggestions = productSearchFacade.getAutocompleteSuggestions(term);
		}
		for (final AutocompleteSuggestionData autoSuggestion : autoSuggestions)
		{
			final SuggestionData suggestionData = new SuggestionData();
			suggestionData.setValue(autoSuggestion.getTerm());
			suggestions.add(suggestionData);
		}
		final SuggestionDataList suggestionDataList = new SuggestionDataList();
		suggestionDataList.setSuggestions(suggestions);
		return suggestionDataList;
	}

	/**
	 * Web service handler for the postReview call. Review will be posted as anonymous principal. Method uses
	 * {@link uk.ptr.cloudinary.populator.HttpRequestReviewDataPopulator} to populate review data
	 * from request parameters.
	 * <p/>
	 * There is no default validation for the posted value!
	 * <p/>
	 * Request Method: <code>POST<code>
	 * Sample Call: http://localhost:9001/rest/v1/{SITE}/products/{CODE}/review
	 * Request parameters:
	 * <ul>
	 * <li>rating (required)</li>
	 * <li>headline</li>
	 * <li>comment</li>
	 * <li>alias</li>
	 * </ul>
	 * 
	 * @param code
	 *           - the unique code used to identify a product
	 * @param request
	 * @return the ReviewData DTO which will be marshaled to JSON or XML based on Accept-Header
	 */
	@RequestMapping(value = "/{code}/reviews", method = RequestMethod.POST)
	@ResponseBody
	public ReviewData createReview(@PathVariable final String code, final HttpServletRequest request)
			throws WebserviceValidationException //NOSONAR
	{
		final ReviewData reviewData = new ReviewData();
		httpRequestReviewDataPopulator.populate(request, reviewData);
		final Errors errors = new BeanPropertyBindingResult(reviewData, "reviewData");

		reviewValidator.validate(reviewData, errors);

		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}

		return productFacade.postReview(code, reviewData);
	}

	/**
	 * Web service handler for searching product's stock level sorted by distance from specific location passed by the
	 * free-text parameter. Sample Call: http://localhost:9001/rest/v1/{SITE}/products/{CODE}/nearLocation
	 * 
	 * @param code
	 *           - the unique code used to identify a product
	 * @param location
	 *           - free-text location
	 * @return the StoreFinderStockSearchPageData of ProductData objects sorted by distance from location ascending
	 */
	@RequestMapping(value = "/{code}/nearLocation", method = RequestMethod.GET)
	@ResponseBody
	public StoreFinderStockSearchPageData<ProductData> searchProductStockByLocation(@PathVariable final String code,
			@RequestParam(required = true) final String location,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getProductStockByLocation: code=" + sanitize(code) + " | location=" + sanitize(location));
		}

		final Set<ProductOption> opts = extractOptions(BASIC_OPTION);

		return this.storeFinderStockFacade.productSearch(location, productFacade.getProductForCodeAndOptions(code, opts),
				createPageableData(currentPage, pageSize));

	}

	/**
	 * Web service handler for searching product's stock level sorted by distance from specific location passed by the
	 * free-text parameter. Sample Call: http://localhost:9001/rest/v1/{SITE}/products/{CODE}/nearLatLong
	 * 
	 * @param code
	 *           - the unique code used to identify a product
	 * @param latitude
	 *           - location's latitude
	 * @param longitude
	 *           - location's longitude
	 * @return the StoreFinderStockSearchPageData of ProductData objects sorted by distance from location ascending
	 */
	@RequestMapping(value = "/{code}/nearLatLong", method = RequestMethod.GET)
	@ResponseBody
	public StoreFinderStockSearchPageData<ProductData> searchProductStockByLocationGeoCode(@PathVariable final String code,
			@RequestParam(required = true) final Double latitude, @RequestParam(required = true) final Double longitude,
			@RequestParam(required = false, defaultValue = DEFAULT_PAGE_VALUE) final int currentPage,
			@RequestParam(required = false, defaultValue = MAX_INTEGER) final int pageSize)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("getProductStockByLocationGeoCode: code=" + sanitize(code) + " | latitude=" + latitude + " | longitude="
					+ longitude);
		}
		final Set<ProductOption> opts = extractOptions(BASIC_OPTION);

		return this.storeFinderStockFacade.productSearch(createGeoPoint(latitude, longitude),
				productFacade.getProductForCodeAndOptions(code, opts), createPageableData(currentPage, pageSize));
	}

	protected PageableData createPageableData(final int currentPage, final int pageSize)
	{
		final PageableData pageable = new PageableData();

		pageable.setCurrentPage(currentPage);
		pageable.setPageSize(pageSize);
		return pageable;
	}

	protected GeoPoint createGeoPoint(final Double latitude, final Double longitude)
	{
		final GeoPoint point = new GeoPoint();
		point.setLatitude(latitude.doubleValue());
		point.setLongitude(longitude.doubleValue());

		return point;
	}
}
