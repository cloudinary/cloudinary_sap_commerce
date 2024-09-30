/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.ui.Model;


public class DummyModel implements Model
{

	private final Map modelMap = new HashMap();

	@Override
	public Model addAttribute(final String paramString, final Object paramObject)
	{
		modelMap.put(paramString, paramObject);
		return this;
	}

	@Override
	public Model addAttribute(final Object paramObject)
	{
		modelMap.put(paramObject.getClass().getName(), paramObject);
		return this;
	}

	@Override
	public Model addAllAttributes(final Collection<?> paramCollection)
	{
		for (final Object obj : paramCollection)
		{
			addAttribute(obj);
		}
		return this;
	}

	@Override
	public Model addAllAttributes(final Map<String, ?> paramMap)
	{
		for (final Entry<String, ?> entry : paramMap.entrySet())
		{
			addAttribute(entry.getKey(), entry.getValue());
		}
		return this;
	}

	@Override
	public Model mergeAttributes(final Map<String, ?> paramMap)
	{
		addAllAttributes(paramMap);
		return this;
	}

	@Override
	public boolean containsAttribute(final String paramString)
	{
		return modelMap.containsKey(paramString);
	}

	@Override
	public Map<String, Object> asMap()
	{
		return modelMap;
	}

	public Object getAttribute(final String paramString)
	{
		return modelMap.get(paramString);
	}


}
