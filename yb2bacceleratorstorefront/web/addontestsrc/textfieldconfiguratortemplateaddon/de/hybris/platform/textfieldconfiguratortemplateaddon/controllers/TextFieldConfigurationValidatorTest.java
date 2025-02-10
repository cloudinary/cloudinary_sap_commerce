/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplateaddon.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.textfieldconfiguratortemplateaddon.forms.TextFieldConfigurationForm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;


@UnitTest
public class TextFieldConfigurationValidatorTest
{
	private static final long QUANTITY = 4;
	private static final String ATTRIBUTE_KEY = "Key";
	private static final String ATTRIBUTE_VALUE = "Value";
	private static final String ATTRIBUTE_VALUE_ONE_CHARACTER = "V";
	TextFieldConfigurationValidator classUnderTest = new TextFieldConfigurationValidator();
	private final TextFieldConfigurationForm textFieldForm = new TextFieldConfigurationForm();
	private final Errors errors = new BeanPropertyBindingResult(textFieldForm, textFieldForm.getClass().getSimpleName());
	private final Map<ConfiguratorType, Map<String, String>> configurationsKeyValueMap = new HashMap<ConfiguratorType, Map<String, String>>();
	private final Map<String, String> keyValueMap = new HashMap<String, String>();


	@Before
	public void initialize()
	{
		textFieldForm.setQuantity(QUANTITY);
		textFieldForm.setConfigurationsKeyValueMap(configurationsKeyValueMap);
		configurationsKeyValueMap.put(ConfiguratorType.TEXTFIELD, keyValueMap);
		keyValueMap.put(ATTRIBUTE_KEY, ATTRIBUTE_VALUE);
	}

	@Test
	public void testSupports()
	{
		assertTrue(classUnderTest.supports(TextFieldConfigurationForm.class));
	}

	@Test
	public void testValidateOk()
	{
		classUnderTest.validate(textFieldForm, errors);
		assertEquals(0, errors.getErrorCount());
	}

	@Test
	public void testValidateValueTooLong()
	{
		keyValueMap.put(ATTRIBUTE_KEY,
				StringUtils.repeat(ATTRIBUTE_VALUE_ONE_CHARACTER, TextFieldConfigurationValidator.MAXIMUM_LENGTH_THRESHOLD + 1));
		classUnderTest.validate(textFieldForm, errors);
		assertEquals(1, errors.getErrorCount());
	}

	@Test
	public void testValidateValueLengthJustMatches()
	{
		keyValueMap.put(ATTRIBUTE_KEY,
				StringUtils.repeat(ATTRIBUTE_VALUE_ONE_CHARACTER, TextFieldConfigurationValidator.MAXIMUM_LENGTH_THRESHOLD));
		classUnderTest.validate(textFieldForm, errors);
		assertEquals(0, errors.getErrorCount());
	}

	@Test
	public void testValidateQuantityZero()
	{
		textFieldForm.setQuantity(Long.valueOf(0));
		classUnderTest.validate(textFieldForm, errors);
		assertEquals(1, errors.getErrorCount());
	}
}
