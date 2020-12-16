/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.auth;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class GuestAuthenticationTokenTest
{
	/**
	 * Fortify issue test
	 */
	@Test
	public void shouldReturnFalseWhenComparedToNull()
	{
		final GuestAuthenticationToken token = new GuestAuthenticationToken("email", Collections.emptyList());
		Assert.assertFalse(token.equals(null));
	}

}
