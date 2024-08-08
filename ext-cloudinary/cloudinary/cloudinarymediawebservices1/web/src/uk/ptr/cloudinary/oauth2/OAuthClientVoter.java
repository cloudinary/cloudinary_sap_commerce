/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.oauth2;


import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;


/**
 * Votes to grant access if current OAuth Client ID matches with any config attribute
 * <p>
 * Only config attributes starting with 'prefix' will be took into consideration. Default prefix is 'CLIENT_'. Prefix
 * can be customized, in particular when null all config attributes will be analyzed.
 * <p>
 * If none of the configuration attributes starts with 'prefix', the voter abstains from voting.
 * <p>
 * If there are some configuration attributes starting with prefix but none matches, the voter denies access.
 * <p>
 * Comparisions are case insensitive.
 * <p>
 * Example: if config attribute = 'CLIENT_MOBILE_ANDROID' and client id is 'mobile_android' access by this voter is
 * granted
 */
public class OAuthClientVoter implements AccessDecisionVoter<Object>
{
	private String clientPrefix = "CLIENT_";

	public String getClientPrefix()
	{
		return clientPrefix;
	}

	public void setClientPrefix(final String clientPrefix)
	{
		this.clientPrefix = clientPrefix;
	}

	@Override
	public boolean supports(final ConfigAttribute attribute)
	{
		return (attribute.getAttribute() != null) && attribute.getAttribute().startsWith(getClientPrefix());
	}

	/**
	 * This implementation supports any type of class, because it does not query the presented secure object.
	 *
	 * @param clazz
	 * 		the secure object
	 * @return always <code>true</code>
	 */
	@Override
	public boolean supports(final Class<?> clazz)
	{
		return true;
	}

	@Override
	public int vote(final Authentication authentication, final Object object, final Collection<ConfigAttribute> attributes)
	{
		int result = ACCESS_ABSTAIN;

		if (!(authentication instanceof OAuth2Authentication))
		{
			return ACCESS_ABSTAIN;
		}

		final OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) authentication;
		final String clientId = oAuth2Authentication.getOAuth2Request().getClientId();
		final String clientIdPattern = getClientPrefix() + clientId.toUpperCase();

		for (final ConfigAttribute attribute : attributes)
		{
			if (this.supports(attribute))
			{
				result = ACCESS_DENIED;

				if (attribute.getAttribute().equalsIgnoreCase(clientIdPattern))
				{
					return ACCESS_GRANTED;
				}
			}
		}

		return result;
	}
}
