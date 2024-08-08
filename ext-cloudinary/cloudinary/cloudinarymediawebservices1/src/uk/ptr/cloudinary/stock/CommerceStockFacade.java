/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.stock;


import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;


/**
 * Commerce stock facade. Deals with methods related to stock of products in sites and points of service.
 */
public interface CommerceStockFacade
{
	/**
	 * Indicates if stock system is enabled for given base store
	 *
	 * @param baseSiteId
	 * 		to be checked
	 * @return true if stock system is enabled
	 * @throws UnknownIdentifierException
	 * 		the unknown identifier exception when no base site with given id was found
	 */
	boolean isStockSystemEnabled(String baseSiteId) throws UnknownIdentifierException; //NOSONAR

	/**
	 * Returns stock data for combination of given product and base site
	 *
	 * @param productCode
	 * 		Product code
	 * @param baseSiteId
	 * 		Base site ID
	 * @return {@link StockData} information
	 * @throws UnknownIdentifierException
	 * 		the unknown identifier exception when no base site or product with given id was found
	 * @throws IllegalArgumentException
	 * 		the illegal argument exception when any one parameter is null
	 * @throws AmbiguousIdentifierException
	 * 		the ambiguous identifier exception when there is more than one product with given code
	 */
	StockData getStockDataForProductAndBaseSite(String productCode, String baseSiteId)
			throws UnknownIdentifierException, IllegalArgumentException, AmbiguousIdentifierException; //NOSONAR

	/**
	 * Returns stock data for given product and point of service (that also indicates warehouse)
	 *
	 * @param productCode
	 * @param storeName
	 * @return {@link StockData} information
	 * @throws UnknownIdentifierException
	 * 		the unknown identifier exception when no store or product with given id was found
	 * @throws IllegalArgumentException
	 * 		the illegal argument exception when any one parameter is null
	 * @throws AmbiguousIdentifierException
	 * 		the ambiguous identifier exception when there is more than one product with given code
	 */
	StockData getStockDataForProductAndPointOfService(String productCode, String storeName)
			throws UnknownIdentifierException, IllegalArgumentException, AmbiguousIdentifierException; //NOSONAR
}
