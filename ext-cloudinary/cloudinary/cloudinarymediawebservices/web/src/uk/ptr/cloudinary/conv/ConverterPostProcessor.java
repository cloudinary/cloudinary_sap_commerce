/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.conv;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.thoughtworks.xstream.XStream;


/**
 * Sets default converter as a target of converter redirection in converters implementing {@link RedirectableConverter}
 * interface.
 */
public class ConverterPostProcessor implements BeanPostProcessor
{
	private final XStream xStream;

	public ConverterPostProcessor(final XStream xStream)
	{
		this.xStream = xStream;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException //NOSONAR
	{
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException //NOSONAR
	{
		if (bean instanceof RedirectableConverter)
		{
			((RedirectableConverter) bean).setTargetConverter(
					getxStream().getConverterLookup().lookupConverterForType(((RedirectableConverter) bean).getConvertedClass()));

		}
		return bean;
	}

	public XStream getxStream()
	{
		return xStream;
	}

}
