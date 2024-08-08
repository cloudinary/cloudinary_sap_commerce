/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.mapping.mappers;

import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.SpellingSuggestionData;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.SpellingSuggestionWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;

import ma.glasnost.orika.MappingContext;


public class SpellingSuggestionMapper extends AbstractCustomMapper<SpellingSuggestionData, SpellingSuggestionWsDTO>
{
	@Override
	public void mapAtoB(final SpellingSuggestionData a, final SpellingSuggestionWsDTO b, final MappingContext context)
	{
		// other fields are mapped automatically

		context.beginMappingField("query.query.value", getAType(), a, "query", getBType(), b);
		try
		{
			if (shouldMap(a, b, context))
			{
				final SearchStateData stateData = (SearchStateData) a.getQuery();
				b.setQuery(mapperFacade.map(stateData.getQuery().getValue(), String.class, context));
			}
		}
		finally
		{
			context.endMappingField();
		}
	}

	@Override
	public void mapBtoA(final SpellingSuggestionWsDTO b, final SpellingSuggestionData a, final MappingContext context)
	{
		// other fields are mapped automatically

		context.beginMappingField("query", getBType(), b, "query.query.value", getAType(), a);
		try
		{
			if (shouldMap(b, a, context))
			{
				final SearchStateData stateData = new SearchStateData();
				final SearchQueryData queryData = new SearchQueryData();
				queryData.setValue(mapperFacade.map(b.getQuery(), String.class, context));
				stateData.setQuery(queryData);
				a.setQuery(stateData);
			}
		}
		finally
		{
			context.endMappingField();
		}
	}
}
