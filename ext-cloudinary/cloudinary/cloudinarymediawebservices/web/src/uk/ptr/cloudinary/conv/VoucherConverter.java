/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.conv;

import java.util.Collection;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


public class VoucherConverter extends AbstractRedirectableConverter
{
	@Override
	public boolean canConvert(final Class type)
	{
		return getConvertedClass().isAssignableFrom(type);
	}

	@Override
	public void marshal(final Object source, final HierarchicalStreamWriter writerOrig, final MarshallingContext context)
	{
		final Collection<String> vouchersCode = (Collection) source;
		final ExtendedHierarchicalStreamWriter writer = (ExtendedHierarchicalStreamWriter) writerOrig.underlyingWriter();

		if (vouchersCode != null && !vouchersCode.isEmpty())
		{
			writer.startNode("appliedVouchers", String.class);
			for (final String voucherCode : vouchersCode)
			{
				writer.startNode("voucherCode", String.class);
				writer.setValue(voucherCode);
				writer.endNode();
			}
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context)
	{
		return getTargetConverter().unmarshal(reader, context);
	}

	@Override
	public Class getConvertedClass()
	{
		return Collection.class;
	}
}
