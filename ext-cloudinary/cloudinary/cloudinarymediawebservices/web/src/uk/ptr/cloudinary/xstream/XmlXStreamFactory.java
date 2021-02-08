/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.xstream;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;


/**
 * {@link XStream} instance factory
 */
public class XmlXStreamFactory implements FactoryBean, InitializingBean
{
	private XStream xmlInstance;

	@Override
	public void afterPropertiesSet() throws Exception
	{
		xmlInstance = getObjectInternal();
	}

	@Override
	public Object getObject() throws Exception
	{
		return xmlInstance;
	}

	protected XStream getObjectInternal()
	{
		final XStream stream = new XStream()
		{
			@Override
			protected MapperWrapper wrapMapper(final MapperWrapper next)
			{
				return createMapperWrapper(next);
			}
		};

		stream.setMode(com.thoughtworks.xstream.XStream.NO_REFERENCES);
		stream.addDefaultImplementation(ArrayList.class, Collection.class);

		return stream;
	}

	/**
	 * Due to schema compatibility requirements, customizes a {@link MapperWrapper} for arrays to don't generate a -array
	 * suffixes.
	 */
	protected MapperWrapper createMapperWrapper(final MapperWrapper parent)
	{
		return new ArrayMapper(parent)
		{
			@Override
			public String aliasForSystemAttribute(final String attribute)
			{
				return "class".equals(attribute) ? null : attribute;
			}
		};
	}

	@Override
	public Class getObjectType()
	{
		return XStream.class;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}
}
