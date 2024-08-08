/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.stock.impl;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.pos.PointOfServiceService;
import de.hybris.platform.webservicescommons.util.YSanitizer;
import uk.ptr.cloudinary.stock.CommerceStockFacade;
import uk.ptr.cloudinary.strategies.BaseStoreForSiteSelectorStrategy;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link uk.ptr.cloudinary.stock.CommerceStockFacade}<br/>
 * TODO: when moving to commercelayer:<br/>
 * TODO: please add validation to the default implementation of BaseSiteService and throw exceptions from there</br>
 * TODO: ideally get rid of createStockData()
 */
public class DefaultCommerceStockFacade implements CommerceStockFacade
{
	private BaseSiteService baseSiteService;
	private CommerceStockService commerceStockService;
	private ProductService productService;
	private PointOfServiceService pointOfServiceService;
	private BaseStoreForSiteSelectorStrategy baseStoreForSiteSelectorStrategy;

	@Override
	public boolean isStockSystemEnabled(final String baseSiteId) throws UnknownIdentifierException //NOSONAR
	{
		// it's not checked in the service layer (!) :
		ServicesUtil.validateParameterNotNull(baseSiteId, "Parameter baseSiteId must not be null");
		final BaseSiteModel baseSiteModel = getBaseSiteService().getBaseSiteForUID(baseSiteId);
		if (baseSiteModel == null)
		{
			throw new UnknownIdentifierException("Base site with uid '" + YSanitizer.sanitize(baseSiteId) + "' not found!");
		}

		return getCommerceStockService().isStockSystemEnabled(getBaseStoreForSiteSelectorStrategy().getBaseStore(baseSiteModel));
	}

	@Override
	public StockData getStockDataForProductAndBaseSite(final String productCode, final String baseSiteId)
			throws UnknownIdentifierException, IllegalArgumentException, AmbiguousIdentifierException //NOSONAR
	{
		// it's not checked in the service layer (!) :
		ServicesUtil.validateParameterNotNull(baseSiteId, "Parameter baseSiteId must not be null");
		final BaseSiteModel baseSiteModel = getBaseSiteService().getBaseSiteForUID(baseSiteId);
		if (baseSiteModel == null)
		{
			throw new UnknownIdentifierException("Base site with uid '" + YSanitizer.sanitize(baseSiteId) + "' not found!");
		}

		final ProductModel productModel = getProductService().getProductForCode(productCode);

		return createStockData(getCommerceStockService().getStockLevelStatusForProductAndBaseStore(productModel,
				getBaseStoreForSiteSelectorStrategy().getBaseStore(baseSiteModel)), getCommerceStockService()
				.getStockLevelForProductAndBaseStore(productModel,
						getBaseStoreForSiteSelectorStrategy().getBaseStore(baseSiteModel)));
	}

	@Override
	public StockData getStockDataForProductAndPointOfService(final String productCode, final String storeName)
			throws UnknownIdentifierException, IllegalArgumentException, AmbiguousIdentifierException //NOSONAR
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		final PointOfServiceModel pointOfServiceModel = getPointOfServiceService().getPointOfServiceForName(storeName);

		return createStockData(
				getCommerceStockService().getStockLevelStatusForProductAndPointOfService(productModel, pointOfServiceModel),
				getCommerceStockService().getStockLevelForProductAndPointOfService(productModel, pointOfServiceModel));
	}

	/**
	 * This method is used here instead of regular populator beacause {@link CommerceStockService} returns all values
	 * separately.<br/>
	 * Ideally would be to improve it someday by returning StockLevelModel.
	 *
	 * @param stockLevelStatus
	 * 		stock level status
	 * @param stockLevel
	 * 		stock level
	 * @return stockData
	 */
	protected StockData createStockData(final StockLevelStatus stockLevelStatus, final Long stockLevel)
	{
		final StockData stockData = new StockData();
		stockData.setStockLevelStatus(stockLevelStatus);
		stockData.setStockLevel(stockLevel);
		return stockData;
	}

	public CommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	@Required
	public void setCommerceStockService(final CommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public BaseStoreForSiteSelectorStrategy getBaseStoreForSiteSelectorStrategy()
	{
		return baseStoreForSiteSelectorStrategy;
	}

	@Required
	public void setBaseStoreForSiteSelectorStrategy(final BaseStoreForSiteSelectorStrategy baseStoreForSiteSelectorStrategy)
	{
		this.baseStoreForSiteSelectorStrategy = baseStoreForSiteSelectorStrategy;
	}

	public ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	public PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	@Required
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}
}
