/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.conv;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.util.Assert;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


public class Oauth2AccessTokenConverter extends AbstractRedirectableConverter
{
	@Override
	public boolean canConvert(final Class type)
	{
		return type == getConvertedClass();
	}

	protected String formattedValue(final String value)
	{
		return value;
	}

	@Override
	public void marshal(final Object source, final HierarchicalStreamWriter writerOrig, final MarshallingContext context)
	{
		final OAuth2AccessToken token = (OAuth2AccessToken) source;
		final ExtendedHierarchicalStreamWriter writer = (ExtendedHierarchicalStreamWriter) writerOrig.underlyingWriter();


		writer.startNode(OAuth2AccessToken.ACCESS_TOKEN, String.class);
		writer.setValue(formattedValue(token.getValue()));
		writer.endNode();

		writer.startNode(OAuth2AccessToken.TOKEN_TYPE, String.class);
		writer.setValue(formattedValue(token.getTokenType()));
		writer.endNode();

		final OAuth2RefreshToken refreshToken = token.getRefreshToken();
		if (refreshToken != null)
		{
			writer.startNode(OAuth2AccessToken.REFRESH_TOKEN, String.class);
			writer.setValue(formattedValue(refreshToken.getValue()));
			writer.endNode();

		}
		final Date expiration = token.getExpiration();
		if (expiration != null)
		{
			final long now = System.currentTimeMillis();
			writer.startNode(OAuth2AccessToken.EXPIRES_IN, Integer.class);
			writer.setValue(String.valueOf((expiration.getTime() - now) / 1000));
			writer.endNode();
		}
		final Set<String> scope = token.getScope();
		if (scope != null && !scope.isEmpty())
		{
			final StringBuilder scopes = new StringBuilder();
			for (final String s : scope)
			{
				Assert.hasLength(s, "Scopes cannot be null or empty. Got " + scope);
				scopes.append(s);
				scopes.append(' ');
			}

			writer.startNode(OAuth2AccessToken.SCOPE, String.class);
			writer.setValue(formattedValue(scopes.substring(0, scopes.length() - 1)));
			writer.endNode();
		}
		final Map<String, Object> additionalInformation = token.getAdditionalInformation();
		for (final Map.Entry<String, Object> entry : additionalInformation.entrySet())
		{
			writer.startNode(entry.getKey(), String.class);
			writer.setValue(formattedValue(String.valueOf(entry.getValue())));
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
		return DefaultOAuth2AccessToken.class;
	}
}
