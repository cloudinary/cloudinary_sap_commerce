/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.mappers;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercewebservicescommons.dto.order.CardTypeWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.PaymentDetailsWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;

import ma.glasnost.orika.MappingContext;


public class CCPaymentInfoDataMapper extends AbstractCustomMapper<CCPaymentInfoData, PaymentDetailsWsDTO>
{
	@Override
	public void mapAtoB(final CCPaymentInfoData a, final PaymentDetailsWsDTO b, final MappingContext context)
	{
		// other fields are mapped automatically
		mapCartTypeAtoB(a, b, context);
		mapdDefaultPaymentAtoB(a, b, context);
	}

	protected void mapCartTypeAtoB(final CCPaymentInfoData a, final PaymentDetailsWsDTO b, final MappingContext context)
	{
		context.beginMappingField("cardType", getAType(), a, "cardType", getBType(), b);
		try
		{
			if (shouldMap(a, b, context))
			{
				if (a.getCardTypeData() != null && a.getCardTypeData().getCode() != null)
				{
					b.setCardType(mapperFacade.map(a.getCardTypeData(), CardTypeWsDTO.class, context));
				}
				else if (a.getCardType() != null)
				{
					final CardTypeWsDTO cardType = new CardTypeWsDTO();
					cardType.setCode(a.getCardType());
					b.setCardType(cardType);
				}
			}
		}
		finally
		{
			context.endMappingField();
		}
	}

	protected void mapdDefaultPaymentAtoB(final CCPaymentInfoData a, final PaymentDetailsWsDTO b, final MappingContext context)
	{
		context.beginMappingField("defaultPaymentInfo", getAType(), a, "defaultPayment", getBType(), b);
		try
		{
			if (shouldMap(a, b, context))
			{
				if (a.isDefaultPaymentInfo())
				{
					b.setDefaultPayment(Boolean.TRUE);
				}
				else
				{
					b.setDefaultPayment(Boolean.FALSE);
				}
			}
		}
		finally
		{
			context.endMappingField();
		}
	}

	@Override
	public void mapBtoA(final PaymentDetailsWsDTO b, final CCPaymentInfoData a, final MappingContext context)
	{
		// other fields are mapped automatically

		mapCartTypeBtoA(b, a, context);
		mapDefaultPaymentBtoA(b, a, context);
	}

	protected void mapCartTypeBtoA(final PaymentDetailsWsDTO b, final CCPaymentInfoData a, final MappingContext context)
	{
		context.beginMappingField("cardType", getBType(), b, "cardType", getAType(), a);
		try
		{
			if (shouldMap(b, a, context) && b.getCardType() != null)
			{
				a.setCardType(b.getCardType().getCode());
				a.setCardTypeData(mapperFacade.map(b.getCardType(), CardTypeData.class, context));
			}
		}
		finally
		{
			context.endMappingField();
		}
	}

	protected void mapDefaultPaymentBtoA(final PaymentDetailsWsDTO b, final CCPaymentInfoData a, final MappingContext context)
	{
		context.beginMappingField("defaultPayment", getBType(), b, "defaultPaymentInfo", getAType(), a);
		try
		{
			if (shouldMap(b, a, context) && b.getDefaultPayment() != null)
			{
				a.setDefaultPaymentInfo(b.getDefaultPayment().booleanValue());
			}
		}
		finally
		{
			context.endMappingField();
		}
	}
}
