/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.xstream;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.xstream.alias.AttributeAliasMapping;
import de.hybris.platform.commercefacades.xstream.alias.AttributeOmitMapping;
import de.hybris.platform.commercefacades.xstream.alias.FieldAliasMapping;
import de.hybris.platform.commercefacades.xstream.alias.TypeAliasMapping;
import de.hybris.platform.commercefacades.xstream.conv.AttributeConverterMapping;
import de.hybris.platform.commercefacades.xstream.conv.TypeConverterMapping;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.NullConverter;
import com.thoughtworks.xstream.converters.enums.EnumConverter;
import com.thoughtworks.xstream.converters.extended.TextAttributeConverter;
import com.thoughtworks.xstream.converters.extended.ThrowableConverter;
import com.thoughtworks.xstream.core.DefaultConverterLookup;


@UnitTest
public class XmlXStreamMarshallerFactoryTest
{
	@Mock
	private XStream xStream;

	@Mock
	private ApplicationContext ctx;

	private static final String SERVICE_BEAN_DEF = "" + "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n"//
			+ "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"//
			+ "       xsi:schemaLocation=\"http://www.springframework.org/schema/beans\n"//
			+ "                            http://www.springframework.org/schema/beans/spring-beans-3.1.xsd\">\n" //

			+ "       <bean\n"//
			+ "             id=\"onlyOneInstance\"\n"//
			+ "             class=\"uk.ptr.cloudinary.xstream.XmlXStreamMarshallerFactory\">\n"//
			+ "       </bean>\n"//
			+ "</beans>";

	@Before
	public void createMocks()
	{
		MockitoAnnotations.initMocks(this);
	}

	public Object createMarshaller() throws Exception
	{
		final XmlXStreamMarshallerFactory factory = new XmlXStreamMarshallerFactory();
		factory.setApplicationContext(ctx);
		factory.setXStream(xStream);
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Test
	public void testCreateAliasing() throws Exception
	{
		final TypeAliasMapping typeMappingOne = new TypeAliasMapping();
		typeMappingOne.setAlias("aliOne");
		typeMappingOne.setAliasedClass(String.class);
		final TypeAliasMapping typeMappingTwo = new TypeAliasMapping();
		typeMappingTwo.setAlias("aliTwo");
		typeMappingTwo.setAliasedClass(Object.class);

		final AttributeAliasMapping attMappingOne = new AttributeAliasMapping();
		attMappingOne.setAlias("aliOne");
		attMappingOne.setAttributeName("aliAttr");
		attMappingOne.setAliasedClass(String.class);
		final AttributeAliasMapping attMappingTwo = new AttributeAliasMapping();
		attMappingTwo.setAlias("aliTwo");
		attMappingTwo.setAttributeName("aliTwoAttr");
		attMappingTwo.setAliasedClass(Object.class);

		final FieldAliasMapping fieldMappingOne = new FieldAliasMapping();
		fieldMappingOne.setAlias("fOne");
		fieldMappingOne.setFieldName("fAttr");
		fieldMappingOne.setAliasedClass(String.class);
		final FieldAliasMapping fieldMappingTwo = new FieldAliasMapping();
		fieldMappingTwo.setAlias("fTwo");
		fieldMappingTwo.setFieldName("fTwoAttr");
		fieldMappingTwo.setAliasedClass(Object.class);

		final Map<String, TypeAliasMapping> mapping = new HashMap<String, TypeAliasMapping>();
		mapping.put("tm1", typeMappingOne);
		mapping.put("tm2", typeMappingTwo);

		mapping.put("am1", attMappingOne);
		mapping.put("am2", attMappingTwo);


		mapping.put("fm1", fieldMappingOne);
		mapping.put("fm2", fieldMappingTwo);

		Mockito.when(ctx.getBeansOfType(TypeAliasMapping.class)).thenReturn(mapping);

		final Object marshallerObject = createMarshaller();

		Assert.assertTrue(marshallerObject instanceof XStreamMarshaller);

		Mockito.verify(xStream).alias("aliOne", String.class);
		Mockito.verify(xStream).alias("aliTwo", Object.class);

		Mockito.verify(xStream).aliasAttribute(Object.class, "aliTwoAttr", "aliTwo");
		Mockito.verify(xStream).aliasAttribute(String.class, "aliAttr", "aliOne");

		Mockito.verify(xStream).aliasField("fOne", String.class, "fAttr");
		Mockito.verify(xStream).aliasField("fTwo", Object.class, "fTwoAttr");
	}

	@Test
	public void testCreateConverters() throws Exception
	{
		final EnumConverter enumConverter = new EnumConverter();
		final NullConverter nullConverter = new NullConverter();
		final ThrowableConverter thConverter = new ThrowableConverter(new DefaultConverterLookup());
		final TextAttributeConverter txtConverter = new TextAttributeConverter();

		final TypeConverterMapping typeMappingOne = new TypeConverterMapping();
		typeMappingOne.setConverter(enumConverter);
		typeMappingOne.setAliasedClass(String.class);
		final TypeConverterMapping typeMappingTwo = new TypeConverterMapping();
		typeMappingTwo.setConverter(nullConverter);
		typeMappingTwo.setAliasedClass(Object.class);

		final AttributeConverterMapping attMappingOne = new AttributeConverterMapping();
		attMappingOne.setConverter(thConverter);
		attMappingOne.setAttributeName("aliAttr");
		attMappingOne.setAliasedClass(String.class);

		final AttributeConverterMapping attMappingTwo = new AttributeConverterMapping();
		attMappingTwo.setConverter(txtConverter);
		attMappingTwo.setAttributeName("aliTwoAttr");
		attMappingTwo.setAliasedClass(Object.class);

		final Map<String, TypeConverterMapping> mapping = new HashMap<String, TypeConverterMapping>();
		mapping.put("tm1", typeMappingOne);
		mapping.put("tm2", typeMappingTwo);

		mapping.put("am1", attMappingOne);
		mapping.put("am2", attMappingTwo);

		Mockito.when(ctx.getBeansOfType(TypeConverterMapping.class)).thenReturn(mapping);

		final Object marshallerObject = createMarshaller();

		Assert.assertTrue(marshallerObject instanceof XStreamMarshaller);

		Mockito.verify(xStream).registerConverter(enumConverter);
		Mockito.verify(xStream).registerConverter(nullConverter);

		Mockito.verify(xStream).registerLocalConverter(Object.class, "aliTwoAttr", txtConverter);
		Mockito.verify(xStream).registerLocalConverter(String.class, "aliAttr", thConverter);
	}

	@Test
	public void testCreateOmitters() throws Exception
	{
		final AttributeOmitMapping attMappingOne = new AttributeOmitMapping();
		attMappingOne.setAttributeName("aliOneAttr");
		attMappingOne.setAliasedClass(String.class);

		final AttributeOmitMapping attMappingTwo = new AttributeOmitMapping();
		attMappingTwo.setAttributeName("aliTwoAttr");
		attMappingTwo.setAliasedClass(Object.class);

		final Map<String, AttributeOmitMapping> mapping = new HashMap<String, AttributeOmitMapping>();

		mapping.put("am1", attMappingOne);
		mapping.put("am2", attMappingTwo);

		Mockito.when(ctx.getBeansOfType(AttributeOmitMapping.class)).thenReturn(mapping);

		final Object marshallerObject = createMarshaller();

		Assert.assertTrue(marshallerObject instanceof XStreamMarshaller);

		Mockito.verify(xStream).omitField(Object.class, "aliTwoAttr");
		Mockito.verify(xStream).omitField(String.class, "aliOneAttr");
	}

	@Test
	public void testInstantiateBeanFactory()
	{
		final GenericApplicationContext applicationContext = new GenericApplicationContext();
		final XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(applicationContext);
		xmlReader.loadBeanDefinitions(new ByteArrayResource(SERVICE_BEAN_DEF.getBytes()));
		applicationContext.refresh();

		final XStreamMarshaller instanceOneXStream = (XStreamMarshaller) applicationContext.getBean("onlyOneInstance");
		final XStreamMarshaller instanceTwoXStream = (XStreamMarshaller) applicationContext.getBean("onlyOneInstance");

		Assert.assertSame("Factory should produce same instance ", instanceOneXStream, instanceTwoXStream);
	}
}
