/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;


import de.hybris.platform.commercefacades.storefinder.StoreFinderFacade;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceDataList;
import de.hybris.platform.commerceservices.storefinder.data.StoreFinderSearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.PointOfServiceWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.StoreCountListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.store.StoreFinderSearchPageWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import uk.ptr.cloudinary.store.data.StoreCountListData;
import uk.ptr.cloudinary.v2.helper.StoresHelper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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
@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 1800)
@RequestMapping(value = "/{baseSiteId}/stores")
@Api(tags = "Stores")
public class StoresController extends BaseController
{
	private static final String DEFAULT_SEARCH_RADIUS_METRES = "100000.0";
	private static final String DEFAULT_ACCURACY = "0.0";

	@Resource(name = "storesHelper")
	private StoresHelper storesHelper;
	@Resource(name = "storeFinderFacade")
	private StoreFinderFacade storeFinderFacade;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getStoreLocations", value = "Get a list of store locations", notes = "Lists all store locations that are near the location specified in a query or based on latitude and longitude.")
	@ApiBaseSiteIdParam
	public StoreFinderSearchPageWsDTO getStoreLocations(
			@ApiParam(value = "Location in natural language i.e. city or country.") @RequestParam(required = false) final String query,
			@ApiParam(value = "Coordinate that specifies the north-south position of a point on the Earth's surface.") @RequestParam(required = false) final Double latitude,
			@ApiParam(value = "Coordinate that specifies the east-west position of a point on the Earth's surface.") @RequestParam(required = false) final Double longitude,
			@ApiParam(value = "The current result page requested.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
			@ApiParam(value = "The number of results returned per page.") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize,
			@ApiParam(value = "Sorting method applied to the return results.") @RequestParam(defaultValue = "asc") final String sort,
			@ApiParam(value = "Radius in meters. Max value: 40075000.0 (Earth's perimeter).") @RequestParam(defaultValue = DEFAULT_SEARCH_RADIUS_METRES) final double radius,
			@ApiParam(value = "Accuracy in meters.") @RequestParam(defaultValue = DEFAULT_ACCURACY) final double accuracy,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields, final HttpServletResponse response)
	{
		final StoreFinderSearchPageWsDTO result = storesHelper
				.locationSearch(query, latitude, longitude, currentPage, pageSize, sort, radius, accuracy,
						addPaginationField(fields));

		// X-Total-Count header
		setTotalCountHeader(response, result.getPagination());

		return result;
	}

	@RequestMapping(value = { "/country/{countryIso}" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getStoresByCountry", value = "Get a list of store locations for a given country", notes = "Lists all store locations that are in the specified country.")
	@ApiBaseSiteIdParam
	public PointOfServiceListWsDTO getStoresByCountry(
			@ApiParam(value = "Country ISO code", required = true) @PathVariable final String countryIso,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final PointOfServiceDataList pointsOfService = new PointOfServiceDataList();
		pointsOfService.setPointOfServices(storeFinderFacade.getPointsOfServiceForCountry(countryIso));

		return getDataMapper().map(pointsOfService, PointOfServiceListWsDTO.class, fields);
	}

	@RequestMapping(value = { "/country/{countryIso}/region/{regionIso}" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getStoresByCountryAndRegion", value = "Get a list of store locations for a given country and region", notes = "Lists all store locations that are in the specified country and region.")
	@ApiBaseSiteIdParam
	public PointOfServiceListWsDTO getStoresByCountryAndRegion(
			@ApiParam(value = "Country ISO code", required = true) @PathVariable final String countryIso,
			@ApiParam(value = "Region ISO code", required = true) @PathVariable final String regionIso,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final PointOfServiceDataList pointsOfService = new PointOfServiceDataList();
		pointsOfService.setPointOfServices(storeFinderFacade.getPointsOfServiceForRegion(countryIso, regionIso));

		return getDataMapper().map(pointsOfService, PointOfServiceListWsDTO.class, fields);
	}

	@RequestMapping(method = RequestMethod.HEAD)
	@ApiOperation(nickname = "countStoreLocations", value = "Get a header with the number of store locations.", notes =
			"In the response header, the \"x-total-count\" indicates the number of "
					+ "all store locations that are near the location specified in a query, or based on latitude and longitude.")
	@ApiBaseSiteIdParam
	public void countStoreLocations(
			@ApiParam(value = "Location in natural language i.e. city or country.") @RequestParam(required = false) final String query,
			@ApiParam(value = "Coordinate that specifies the north-south position of a point on the Earth's surface.") @RequestParam(required = false) final Double latitude,
			@ApiParam(value = "Coordinate that specifies the east-west position of a point on the Earth's surface.") @RequestParam(required = false) final Double longitude,
			@ApiParam(value = "Radius in meters. Max value: 40075000.0 (Earth's perimeter).") @RequestParam(defaultValue = DEFAULT_SEARCH_RADIUS_METRES) final double radius,
			@ApiParam(value = "Accuracy in meters.") @RequestParam(defaultValue = DEFAULT_ACCURACY) final double accuracy,
			final HttpServletResponse response)
	{
		final StoreFinderSearchPageData<PointOfServiceData> result = storesHelper
				.locationSearch(query, latitude, longitude, 0, 1, "asc", radius, accuracy);

		// X-Total-Count header
		setTotalCountHeader(response, result.getPagination());
	}


	@RequestMapping(value = "/{storeId}", method = RequestMethod.GET)
	@ApiOperation(nickname = "getStoreLocation", value = "Get a store location", notes = "Returns store location based on its unique name.")
	@ApiBaseSiteIdParam
	@ResponseBody
	public PointOfServiceWsDTO getStoreLocation(
			@ApiParam(value = "Store identifier (currently store name)", required = true) @PathVariable final String storeId,
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		return storesHelper.locationDetails(storeId, fields);
	}

	@RequestMapping(value = "/storescounts", method = RequestMethod.GET)
	@ApiOperation(nickname = "getLocationCounts", value = "Gets a store location count per country and regions", notes = "Returns store counts in countries and regions")
	@ApiBaseSiteIdParam
	@ResponseBody
	public StoreCountListWsDTO getLocationCounts()
	{
		final StoreCountListData storeCountListData = new StoreCountListData();
		storeCountListData.setCountriesAndRegionsStoreCount(storeFinderFacade.getStoreCounts());
		return getDataMapper().map(storeCountListData, StoreCountListWsDTO.class);
	}
}
