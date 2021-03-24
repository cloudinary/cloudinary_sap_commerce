/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.channel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import uk.ptr.cloudinary.queues.UpdateQueue;
import uk.ptr.cloudinary.queues.data.ProductExpressUpdateElementData;
import uk.ptr.cloudinary.queues.util.ProductExpressUpdateElementPredicate;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Predicate;


public class ProductExpressUpdateChannelListener
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ProductExpressUpdateChannelListener.class);
	private final ProductExpressUpdateElementPredicate productExpressUpdateElementPredicate = new ProductExpressUpdateElementPredicate();
	private UpdateQueue<ProductExpressUpdateElementData> productExpressUpdateQueue;
	private Converter<ProductModel, ProductExpressUpdateElementData> productExpressUpdateElementConverter;

	public void onMessage(final ProductModel product)
	{
		LOG.debug("ProductExpressUpdateChannelListener got product with code " + product.getCode());
		final ProductExpressUpdateElementData productExpressUpdateElementData = getProductExpressUpdateElementConverter()
				.convert(product);
		getProductExpressUpdateQueue().removeItems(getPredicate(productExpressUpdateElementData));
		getProductExpressUpdateQueue().addItem(productExpressUpdateElementData);
	}

	/**
	 * Method return object which will be used to determine if element is equal to productExpressUpdateElementData
	 * parameter.
	 *
	 * @param productExpressUpdateElementData
	 * 		- element data for comparison
	 * @return object implementing Predicate interface which should return true from apply method if element is equal to
	 * productExpressUpdateElementData parameter
	 */
	protected Predicate<ProductExpressUpdateElementData> getPredicate(
			final ProductExpressUpdateElementData productExpressUpdateElementData)
	{
		productExpressUpdateElementPredicate.setProductExpressUpdateElementData(productExpressUpdateElementData);
		return productExpressUpdateElementPredicate;
	}

	public UpdateQueue<ProductExpressUpdateElementData> getProductExpressUpdateQueue()
	{
		return productExpressUpdateQueue;
	}

	@Required
	public void setProductExpressUpdateQueue(final UpdateQueue<ProductExpressUpdateElementData> productExpressUpdateQueue)
	{
		this.productExpressUpdateQueue = productExpressUpdateQueue;
	}

	public Converter<ProductModel, ProductExpressUpdateElementData> getProductExpressUpdateElementConverter()
	{
		return productExpressUpdateElementConverter;
	}

	@Required
	public void setProductExpressUpdateElementConverter(
			final Converter<ProductModel, ProductExpressUpdateElementData> productExpressUpdateElementConverter)
	{
		this.productExpressUpdateElementConverter = productExpressUpdateElementConverter;
	}

}
