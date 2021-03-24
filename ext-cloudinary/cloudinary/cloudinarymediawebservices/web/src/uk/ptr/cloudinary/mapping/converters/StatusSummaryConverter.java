/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.converters;

import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercewebservicescommons.dto.order.StatusSummaryWsDTO;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import static de.hybris.platform.catalog.enums.ProductInfoStatus.valueOf;


/**
 * Bidirectional converter between {@link Map<ProductInfoStatus, Integer>} and {@link List<StatusSummaryWsDTO>}
 */
@WsDTOMapping
public class StatusSummaryConverter extends BidirectionalConverter<Map<ProductInfoStatus, Integer>, List<StatusSummaryWsDTO>>
{
	@Override
	public Map<ProductInfoStatus, Integer> convertFrom(final List<StatusSummaryWsDTO> statusSummaryList,
			final Type<Map<ProductInfoStatus, Integer>> type, final MappingContext mappingContext)
	{
		if (statusSummaryList != null)
		{
			return statusSummaryList.stream()
					.collect(Collectors.toMap(entry -> valueOf(entry.getStatus()), entry -> entry.getNumberOfIssues()));
		}
		return Collections.emptyMap();
	}

	@Override
	public List<StatusSummaryWsDTO> convertTo(final Map<ProductInfoStatus, Integer> productInfoStatusMap,
			final Type<List<StatusSummaryWsDTO>> type, final MappingContext mappingContext)
	{
		if (productInfoStatusMap != null)
		{
			return productInfoStatusMap.entrySet().stream() //
					.map(entry -> convertEntrytoWsDTO(entry)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	protected StatusSummaryWsDTO convertEntrytoWsDTO(final Entry<ProductInfoStatus, Integer> entry)
	{
		final StatusSummaryWsDTO result = new StatusSummaryWsDTO();
		result.setStatus(entry.getKey().toString());
		result.setNumberOfIssues(entry.getValue());
		return result;
	}

}
