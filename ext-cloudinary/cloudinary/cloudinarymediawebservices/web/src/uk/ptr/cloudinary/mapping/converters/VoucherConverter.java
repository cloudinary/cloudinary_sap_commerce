/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.converters;

import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commercewebservicescommons.dto.voucher.VoucherWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;


/**
 * Bidirectional converter between {@link VoucherData} and {@Link VoucherWsDTO}
 */
@WsDTOMapping
public class VoucherConverter extends BidirectionalConverter<VoucherWsDTO, String> //NOSONAR
{
	private static final Logger LOG = Logger.getLogger(VoucherConverter.class);

	private VoucherFacade voucherFacade;
	private DataMapper dataMapper;

	@Override
	public String convertTo(final VoucherWsDTO source, final Type<String> destinationType, final MappingContext mappingContext)
	{
		if (source != null && source.getCode() != null)
		{
			return source.getCode();
		}
		return null;
	}

	@Override
	public VoucherWsDTO convertFrom(final String source, final Type<VoucherWsDTO> destinationType,
			final MappingContext mappingContext)
	{
		try
		{
			final VoucherData voucherData = voucherFacade.getVoucher(source);
			return dataMapper.map(voucherData, VoucherWsDTO.class);
		}
		catch (final VoucherOperationException e)
		{
			LOG.debug("Couldn't get data for voucher: " + source, e);
			throw new IllegalArgumentException("Invalid voucher code " + source);
		}
	}

	@Required
	public void setVoucherFacade(final VoucherFacade voucherFacade)
	{
		this.voucherFacade = voucherFacade;
	}

	protected VoucherFacade getVoucherFacade()
	{
		return voucherFacade;
	}

	@Required
	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	protected DataMapper getDataMapper()
	{
		return dataMapper;
	}

}
