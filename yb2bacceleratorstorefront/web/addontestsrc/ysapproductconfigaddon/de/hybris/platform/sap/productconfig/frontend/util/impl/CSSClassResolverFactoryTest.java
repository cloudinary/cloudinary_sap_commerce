/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.util.CSSClassResolver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CSSClassResolverFactoryTest
{

	private CsticData cstic;
	private CsticValueData csticValue;
	private UiGroupData group;
	private ProductConfigMessageData message;
	@Mock
	private CSSClassResolver resolver;

	@Before
	public void setUp()
	{
		CSSClassResolverFactory.setResolver(resolver);
		cstic = new CsticData();
		group = new UiGroupData();
		message = new ProductConfigMessageData();


		Mockito.when(resolver.getLabelStyleClass(cstic)).thenReturn("labelStyle");
		Mockito.when(resolver.getValueStyleClass(cstic)).thenReturn("valueStyle");
		Mockito.when(resolver.getValuePromoStyleClass(cstic, csticValue)).thenReturn("valuePromoStyle");
		Mockito.when(resolver.getMessageTextAdditionalStyleClass(message)).thenReturn("messageTextAdditionalStyle");
		Mockito.when(resolver.getExtendedMessageStyleClass(message)).thenReturn("extendedMessageStyle");
		Mockito.when(resolver.getMessageIconStyleClass(message)).thenReturn("messageIconStyle");
	}

	@Test
	public void testGetLabelStyleClassForCstic()
	{
		final String labelStyleClassForCstic = CSSClassResolverFactory.getLabelStyleClassForCstic(cstic);
		assertEquals("labelStyle", labelStyleClassForCstic);
	}

	@Test
	public void testGetValueStyleClassForCstic()
	{
		final String valueStyleClassForCstic = CSSClassResolverFactory.getValueStyleClassForCstic(cstic);
		assertEquals("valueStyle", valueStyleClassForCstic);
	}

	@Test
	public void testGetValuePromoStyleClassForCsticWithOutPromo()
	{
		final String valuePromoStyleClassForCstic = CSSClassResolverFactory.getValuePromoStyleClass(cstic, csticValue);
		assertEquals("valuePromoStyle", valuePromoStyleClassForCstic);
	}

	@Test
	public void testGetMessageTextAdditionalStyleClass()
	{
		final String additionalStyleClass = CSSClassResolverFactory.getMessageTextAdditionalStyleClass(message);
		assertEquals("messageTextAdditionalStyle", additionalStyleClass);

	}

	@Test
	public void testGetExtendedMessageStyleClass()
	{
		final String extendedMessageStyleClass = CSSClassResolverFactory.getExtendedMessageStyleClass(message);
		assertEquals("extendedMessageStyle", extendedMessageStyleClass);
	}

	@Test
	public void testGetMessageIconStyleClass()
	{
		final String extendedMessageStyleClass = CSSClassResolverFactory.getMessageIconStyleClass(message);
		assertEquals("messageIconStyle", extendedMessageStyleClass);
	}
}
