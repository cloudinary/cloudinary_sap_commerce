/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.util;

import uk.ptr.cloudinary.queues.data.ProductExpressUpdateElementData;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Predicate;


public class ProductExpressUpdateElementPredicate implements Predicate<ProductExpressUpdateElementData>
{
	private ProductExpressUpdateElementData productExpressUpdateElementData;

	public ProductExpressUpdateElementPredicate()
	{
		super();
	}

	public ProductExpressUpdateElementPredicate(final ProductExpressUpdateElementData productExpressUpdateElementData)
	{
		super();
		this.productExpressUpdateElementData = productExpressUpdateElementData;
	}

	@Override
	public boolean apply(@Nullable final ProductExpressUpdateElementData input)
	{

		return areElementsEqual(productExpressUpdateElementData, input);
	}

	protected boolean areElementsEqual(final ProductExpressUpdateElementData element1,
			final ProductExpressUpdateElementData element2)
	{
		if (element1 == element2) //NOSONAR
		{
			return true;
		}

		if (element1 == null || element2 == null)
		{
			return false;
		}

		if (!StringUtils.equals(element1.getCode(), element2.getCode()))
		{
			return false;
		}

		if (!StringUtils.equals(element1.getCatalogVersion(), element2.getCatalogVersion()))
		{
			return false;
		}

		if (!StringUtils.equals(element1.getCatalogId(), element2.getCatalogId()))
		{
			return false;
		}

		return true;
	}

	public ProductExpressUpdateElementData getProductExpressUpdateElementData()
	{
		return productExpressUpdateElementData;
	}

	public void setProductExpressUpdateElementData(final ProductExpressUpdateElementData productExpressUpdateElementData)
	{
		this.productExpressUpdateElementData = productExpressUpdateElementData;
	}

}
