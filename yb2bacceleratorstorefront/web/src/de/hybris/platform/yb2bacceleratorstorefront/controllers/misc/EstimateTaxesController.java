/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yb2bacceleratorstorefront.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller to handle the tax estimation on a cart.
 */
@Controller
public class EstimateTaxesController extends AbstractController
{
	@Resource(name = "cartFacade")
	private CartFacade cartFacade;


	@GetMapping(value = "/cart/estimate", produces = "application/json")
	@ResponseBody
	public CartData addToCart(@RequestParam("zipCode") final String zipCode, @RequestParam("isocode") final String countryIsoCode,
			final Model model)
	{
		return cartFacade.estimateExternalTaxes(zipCode, countryIsoCode);
	}
}
