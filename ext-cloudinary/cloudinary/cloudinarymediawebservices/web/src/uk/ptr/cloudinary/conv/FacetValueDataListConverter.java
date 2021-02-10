/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.conv;

import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;

import java.util.List;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonWriter;


/**
 * Converts FacetValueData to simple response syntax
 */
public class FacetValueDataListConverter implements Converter
{
	private static final Logger LOG = Logger.getLogger(FacetValueDataListConverter.class);

	@Override
	public void marshal(final Object object, final HierarchicalStreamWriter writer, final MarshallingContext context)
	{
		for (final FacetValueData<SearchStateData> facetValueData : (List<FacetValueData>) object)
		{
			writer.startNode("value");
			if (writer instanceof JsonWriter)
			{
				((JsonWriter) writer).startNode("count", Long.class);
				context.convertAnother(Long.valueOf(facetValueData.getCount()));
				((JsonWriter) writer).endNode();
				((JsonWriter) writer).startNode("name", String.class);
				context.convertAnother(facetValueData.getName());
				((JsonWriter) writer).endNode();
				((JsonWriter) writer).startNode("query", String.class);
				((JsonWriter) writer).setValue(facetValueData.getQuery().getQuery().getValue());
				((JsonWriter) writer).endNode();
				((JsonWriter) writer).startNode("selected", Boolean.class);
				context.convertAnother(Boolean.valueOf(facetValueData.isSelected()));
				((JsonWriter) writer).endNode();
			}
			else
			{
				writer.startNode("count");
				context.convertAnother(Long.valueOf(facetValueData.getCount()));
				writer.endNode();
				writer.startNode("name");
				context.convertAnother(facetValueData.getName());
				writer.endNode();
				writer.startNode("query");
				writer.setValue(facetValueData.getQuery().getQuery().getValue());
				writer.endNode();
				writer.startNode("selected");
				context.convertAnother(Boolean.valueOf(facetValueData.isSelected()));
				writer.endNode();
			}
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader hierarchicalStreamReader,
			final UnmarshallingContext unmarshallingContext)
	{
		LOG.debug("unmarshal method for FacetValueData is not implemented");
		return null;
	}

	@Override
	public boolean canConvert(final Class aClass)
	{
		return List.class.isAssignableFrom(aClass);
	}
}
