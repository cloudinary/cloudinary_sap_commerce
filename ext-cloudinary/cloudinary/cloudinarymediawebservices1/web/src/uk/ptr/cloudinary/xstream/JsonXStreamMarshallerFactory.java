/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.xstream;

import java.io.Writer;

import org.springframework.oxm.xstream.XStreamMarshaller;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonWriter;


public class JsonXStreamMarshallerFactory extends XmlXStreamMarshallerFactory
{
	private XStreamMarshaller jsonMarshallerInstance;

	@Override
	public void afterPropertiesSet() throws Exception
	{
		jsonMarshallerInstance = getObjectInternal();
	}

	@Override
	public Object getObject() throws Exception
	{
		return jsonMarshallerInstance;
	}

	/**
	 * creates a custom json writer which swallows top most root nodes
	 */
	@Override
	protected XStreamMarshaller createMarshaller()
	{
		final XStreamMarshaller marshaller = super.createMarshaller();
		marshaller.setStreamDriver(new com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver()
		{
			@Override
			public HierarchicalStreamWriter createWriter(final Writer writer)
			{
				return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
			}
		});
		return marshaller;
	}
}
