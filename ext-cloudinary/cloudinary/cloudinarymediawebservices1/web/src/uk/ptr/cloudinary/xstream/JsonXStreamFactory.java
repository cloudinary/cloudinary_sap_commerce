/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;


/**
 * JSon specific {@link XStream} instances factory.
 */
public class JsonXStreamFactory extends XmlXStreamFactory
{
	private XStream jsonInstance;

	@Override
	public void afterPropertiesSet() throws Exception
	{
		jsonInstance = getObjectInternal();
	}

	@Override
	public Object getObject() throws Exception
	{
		return jsonInstance;
	}

	@Override
	protected XStream getObjectInternal()
	{
		final XStream stream = new XStream(new com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver())
		{
			@Override
			protected MapperWrapper wrapMapper(final MapperWrapper next)
			{
				return createMapperWrapper(next);
			}
		};

		stream.setMode(com.thoughtworks.xstream.XStream.NO_REFERENCES);
		return stream;
	}
}
