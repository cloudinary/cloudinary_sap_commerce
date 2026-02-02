/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.controllers.misc;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import de.hybris.platform.yb2bacceleratorstorefront.security.evaluator.impl.RequireHardLoginEvaluator;


/**
 * Controller for checking user's authentication status
 */
@Controller
@RequestMapping("/authentication")
public class AuthenticationController
{
	public static final String AUTHENTICATED = "authenticated";

	@Resource(name = "requireHardLoginEvaluator")
	private RequireHardLoginEvaluator requireHardLoginEvaluator;

	@GetMapping("/status")
	public ResponseEntity<String> status(final HttpServletRequest request, final HttpServletResponse response)
	{
		if (!requireHardLoginEvaluator.evaluate(request, response))
		{
			return new ResponseEntity<String>(AUTHENTICATED, HttpStatus.OK);
		}

		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
	}
}
