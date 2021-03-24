/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.conv;

import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonWriter;


public class FeaturesMapConverter implements Converter
{


	@Override
	public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context)
	{
		for (final Map.Entry<String, String> entry : ((Map<String, String>) source).entrySet())
		{
			writer.startNode("feature");
			if (writer instanceof JsonWriter)
			{
				((JsonWriter) writer).startNode("key", String.class);
				((JsonWriter) writer).setValue(entry.getKey());
				((JsonWriter) writer).endNode();

				((JsonWriter) writer).startNode("value", String.class);
				((JsonWriter) writer).setValue(entry.getValue());
				((JsonWriter) writer).endNode();
			}
			else
			{
				writer.addAttribute("key", entry.getKey());
				writer.addAttribute("value", entry.getValue() == null ? "" : entry.getValue());
			}
			writer.endNode();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
	 */
	@Override
	public boolean canConvert(final Class arg0)
	{
		return Map.class.isAssignableFrom(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
	 * com.thoughtworks.xstream.converters.UnmarshallingContext)
	 */
	@Override
	public Object unmarshal(final HierarchicalStreamReader arg0, final UnmarshallingContext arg1)
	{
		return null;
	}
}
