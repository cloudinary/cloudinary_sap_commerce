/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.security.evaluator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface SecurityTraitEvaluator
{
	/**
	 * Evaluates a security trait.
	 *
	 * @return true if security trait needs to be enforced.
	 */
	boolean evaluate(final HttpServletRequest request, final HttpServletResponse response);
}
