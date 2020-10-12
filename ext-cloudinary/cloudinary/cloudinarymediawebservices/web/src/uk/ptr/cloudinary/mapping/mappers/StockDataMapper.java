/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.mappers;

import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commercewebservicescommons.dto.product.StockWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;

import ma.glasnost.orika.MappingContext;


public class StockDataMapper extends AbstractCustomMapper<StockData, StockWsDTO>
{
	@Override
	public void mapAtoB(final StockData a, final StockWsDTO b, final MappingContext context)
	{
		// other fields are mapped automatically
		if (hideStockLevel(a))
		{
			b.setStockLevel(Long.valueOf(a.getStockThreshold()));
			b.setIsValueRounded(Boolean.TRUE);
		}
		else
		{
			b.setStockLevel(a.getStockLevel());
			b.setIsValueRounded(Boolean.FALSE);
		}
	}

	private boolean hideStockLevel(final StockData stock)
	{
		return stock.getStockThreshold() != null && stock.getStockLevel() != null
				&& stock.getStockLevel() > stock.getStockThreshold();
	}
}
