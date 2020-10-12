/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.xstream;

import de.hybris.platform.commercefacades.xstream.alias.AttributeAliasMapping;
import de.hybris.platform.commercefacades.xstream.alias.AttributeOmitMapping;
import de.hybris.platform.commercefacades.xstream.alias.FieldAliasMapping;
import de.hybris.platform.commercefacades.xstream.alias.ImplicitCollection;
import de.hybris.platform.commercefacades.xstream.alias.TypeAliasMapping;
import de.hybris.platform.commercefacades.xstream.conv.AttributeConverterMapping;
import de.hybris.platform.commercefacades.xstream.conv.TypeConverterMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.StaxWriter;

import javanet.staxutils.IndentingXMLStreamWriter;


/**
 * Factory for creating a {@link XStreamMarshaller} with given {@link XStream}, registered available
 * {@link TypeAliasMapping} and {@link TypeConverterMapping} instances as customization of the {@link XStreamMarshaller}
 * .
 */
public class XmlXStreamMarshallerFactory implements FactoryBean, ApplicationContextAware, InitializingBean
{
	private static final Logger LOG = Logger.getLogger(XmlXStreamMarshallerFactory.class.getName());

	private ApplicationContext ctx;

	private XStreamMarshaller xmlMarshallerInstance;

	private XStream xStream;

	private List<Class<?>> excludeClasses = new ArrayList<>();

	@Override
	public void afterPropertiesSet() throws Exception
	{
		xmlMarshallerInstance = getObjectInternal();
		configureXmlMarshaller(xmlMarshallerInstance);
	}

	@Override
	public Object getObject() throws Exception
	{
		return xmlMarshallerInstance;
	}

	protected XStreamMarshaller getObjectInternal()
	{
		final XStreamMarshaller marshaller = createMarshaller();

		//we use here BeanFactoryUtils.beansOfTypeIncludingAncestors lookup for assuring the lookup for the 'beans of type' in the parent spring context succeeds
		final Map<String, TypeAliasMapping> allTypeAliases = BeanFactoryUtils
				.beansOfTypeIncludingAncestors(ctx, TypeAliasMapping.class);
		setAliases(marshaller, allTypeAliases.values());

		final Map<String, TypeConverterMapping> allTypeConverter = BeanFactoryUtils
				.beansOfTypeIncludingAncestors(ctx, TypeConverterMapping.class);
		setConverters(marshaller, allTypeConverter.values());

		final Map<String, AttributeOmitMapping> allOmitersConverter = BeanFactoryUtils
				.beansOfTypeIncludingAncestors(ctx, AttributeOmitMapping.class);
		setOmitted(marshaller, allOmitersConverter.values());

		return marshaller;
	}

	protected void configureXmlMarshaller(final XStreamMarshaller marshaller)
	{
		final Map<String, ImplicitCollection> allImplicitCollections = BeanFactoryUtils
				.beansOfTypeIncludingAncestors(ctx, ImplicitCollection.class);
		setImplicitCollections(marshaller, allImplicitCollections.values());
	}

	protected void setImplicitCollections(final XStreamMarshaller marshaller, final Collection<ImplicitCollection> values)
	{
		for (final ImplicitCollection implicit : values)
		{
			marshaller.getXStream()
					.addImplicitCollection(implicit.getOwnerType(), implicit.getFieldName(), implicit.getItemFieldName(),
							implicit.getItemType());
		}
	}

	protected XStream getXStream()
	{
		return xStream;
	}

	protected XStreamMarshaller createMarshaller()
	{
		final StaxDriver driver = new StaxDriver()
		{
			@Override
			public StaxWriter createStaxWriter(final XMLStreamWriter out) throws XMLStreamException
			{
				out.writeStartDocument("UTF-8", "1.0");
				final IndentingXMLStreamWriter isw = new IndentingXMLStreamWriter(out);
				return createStaxWriter(isw, false);
			}
		};

		final XStreamMarshaller marshaller = new XStreamMarshaller()
		{
			@Override
			protected XStream constructXStream()
			{
				return XmlXStreamMarshallerFactory.this.getXStream();
			}

			@Override
			public boolean supports(final Class<?> clazz)
			{
				for (final Class excludeClass : excludeClasses)
				{
					if (excludeClass.isAssignableFrom(clazz))
					{
						return false;
					}
				}
				return super.supports(clazz);
			}
		};

		marshaller.setStreamDriver(driver);

		return marshaller;
	}

	@Required
	public void setXStream(final XStream xStream)
	{
		this.xStream = xStream;
	}

	protected void setOmitted(final XStreamMarshaller marshaller, final Collection<AttributeOmitMapping> omitters)
	{
		for (final AttributeOmitMapping singleOmit : omitters)
		{
			setAttributeOmitInternal(marshaller, singleOmit);
		}
	}

	protected void setConverters(final XStreamMarshaller marshaller, final Collection<TypeConverterMapping> typeConvereters)
	{
		for (final TypeConverterMapping converterMapping : typeConvereters)
		{
			if (converterMapping instanceof AttributeConverterMapping)
			{
				setAttributeConverterInternal(marshaller, (AttributeConverterMapping) converterMapping);
			}
			else
			{
				setTypeConverterInternal(marshaller, converterMapping);
			}

		}
	}

	protected void setAliases(final XStreamMarshaller marshaller, final Collection<TypeAliasMapping> aliases)
	{
		for (final TypeAliasMapping alias : aliases)
		{
			if (alias instanceof AttributeAliasMapping)
			{
				setAttributeAliasInternal(marshaller, alias);
			}
			else if (alias instanceof FieldAliasMapping)
			{
				setFieldAliasInternal(marshaller, alias);
			}
			else
			{
				setTypeAliasInternal(marshaller, alias);
			}
		}
	}

	protected void setAttributeOmitInternal(final XStreamMarshaller marshaller, final AttributeOmitMapping singleOmit)
	{
		marshaller.getXStream().omitField(singleOmit.getAliasedClass(), singleOmit.getAttributeName());
	}

	protected void setAttributeConverterInternal(final XStreamMarshaller marshaller,
			final AttributeConverterMapping converterMapping)
	{
		if (converterMapping.getConverter() instanceof SingleValueConverter)
		{
			marshaller.getXStream().registerLocalConverter(converterMapping.getAliasedClass(), converterMapping.getAttributeName(),
					(SingleValueConverter) converterMapping.getConverter());
		}
		else if (converterMapping.getConverter() instanceof Converter)
		{
			marshaller.getXStream().registerLocalConverter(converterMapping.getAliasedClass(), converterMapping.getAttributeName(),
					(Converter) converterMapping.getConverter());
		}
		else
		{
			throw new IllegalArgumentException(
					"Assigned converter mapping should be of SingleValueConverter or Converter, not a" + converterMapping
							.getConverter());
		}
	}

	protected void setTypeConverterInternal(final XStreamMarshaller marshaller, final TypeConverterMapping converterMapping)
	{
		if (converterMapping.getConverter() instanceof SingleValueConverter)
		{
			marshaller.getXStream().registerConverter((SingleValueConverter) converterMapping.getConverter());

		}
		else if (converterMapping.getConverter() instanceof Converter)
		{
			marshaller.getXStream().registerConverter((Converter) converterMapping.getConverter());
		}
		else
		{
			throw new IllegalArgumentException(
					"Assigned converter mapping should be of SingleValueConverter or Converter, not a" + converterMapping
							.getConverter());
		}
	}

	protected void setTypeAliasInternal(final XStreamMarshaller marshaller, final TypeAliasMapping alias)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("registering type alias " + alias.getAlias() + " , " + alias.getAliasedClass());
		}
		marshaller.getXStream().alias(alias.getAlias(), alias.getAliasedClass());
	}

	/**
	 * aliases property moving also from element to attribute
	 */
	protected void setAttributeAliasInternal(final XStreamMarshaller marshaller, final TypeAliasMapping alias)
	{
		final AttributeAliasMapping attrAlias = (AttributeAliasMapping) alias;
		if (LOG.isDebugEnabled())
		{
			LOG.debug("registering attribute alias " + attrAlias.getAlias() + " , " + attrAlias.getAttributeName() + "." + attrAlias
					.getAliasedClass());
		}

		marshaller.getXStream().aliasAttribute(attrAlias.getAliasedClass(), attrAlias.getAttributeName(), attrAlias.getAlias());
	}

	/**
	 * aliases property leaving it as element
	 */
	protected void setFieldAliasInternal(final XStreamMarshaller marshaller, final TypeAliasMapping alias)
	{
		final FieldAliasMapping attrAlias = (FieldAliasMapping) alias;
		if (LOG.isDebugEnabled())
		{
			LOG.debug("registering field alias " + attrAlias.getAlias() + " , " + attrAlias.getFieldName() + "." + attrAlias
					.getAliasedClass());
		}

		marshaller.getXStream().aliasField(attrAlias.getAlias(), attrAlias.getAliasedClass(), attrAlias.getFieldName());
	}

	@Override
	public Class getObjectType()
	{
		return XStreamMarshaller.class;
	}

	@Override
	public boolean isSingleton()
	{
		return true;
	}

	@Override
	public void setApplicationContext(final ApplicationContext ctx) throws BeansException //NOSONAR
	{
		this.ctx = ctx;
	}

	public List<Class<?>> getExcludeClasses()
	{
		return excludeClasses;
	}

	public void setExcludeClasses(final List<Class<?>> excludeClasses)
	{
		this.excludeClasses = excludeClasses;
	}

}
