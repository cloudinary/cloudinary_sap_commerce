/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import org.junit.Test;


@UnitTest
public class ConstantHandlerTest
{
	@Test
	public void testGeneralGroupName()
	{
		assertEquals(InstanceModel.GENERAL_GROUP_NAME, ConstantHandler.getGeneralGroupName());
	}

}
