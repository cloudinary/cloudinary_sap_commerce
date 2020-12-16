/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v1.controller;

import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.UserFacade;
import uk.ptr.cloudinary.order.data.CardTypeDataList;
import uk.ptr.cloudinary.storesession.data.CurrencyDataList;
import uk.ptr.cloudinary.storesession.data.LanguageDataList;
import uk.ptr.cloudinary.user.data.CountryDataList;
import uk.ptr.cloudinary.user.data.TitleDataList;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Main Controller for available data
 */
@Controller("storeControllerV1")
public class StoreController extends BaseController
{
	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "storeSessionFacade")
	private StoreSessionFacade storeSessionFacade;

	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;

	/**
	 * returns all languages defined for the base store.<br>
	 * Sample call : http://localhost:9001/rest/v1/{SITE}/languages<br>
	 * Request method : <code>GET</code>
	 * 
	 * @return list of languages
	 */
	@RequestMapping(value = "/{baseSiteId}/languages", method = RequestMethod.GET)
	@ResponseBody
	public LanguageDataList getLanguages()
	{
		final LanguageDataList languageDataList = new LanguageDataList();
		languageDataList.setLanguages(storeSessionFacade.getAllLanguages());
		return languageDataList;
	}

	/**
	 * returns all currencies defined for the current store.<br>
	 * Sample call : http://localhost:9001/rest/v1/{SITE}/currencies<br>
	 * Request method : <code>GET</code>
	 * 
	 * @return list of currencies
	 */
	@RequestMapping(value = "/{baseSiteId}/currencies", method = RequestMethod.GET)
	@ResponseBody
	public CurrencyDataList getCurrencies()
	{
		final CurrencyDataList currencyDataList = new CurrencyDataList();
		currencyDataList.setCurrencies(storeSessionFacade.getAllCurrencies());
		return currencyDataList;
	}

	/**
	 * returns all delivery countries defined for the curent store.<br>
	 * Sample call : http://localhost:9001/rest/v1/{SITE}/deliverycountries<br>
	 * Request method : <code>GET</code>
	 * 
	 * @return list of countries
	 */
	@RequestMapping(value = "/{baseSiteId}/deliverycountries", method = RequestMethod.GET)
	@ResponseBody
	public CountryDataList getDeliveryCountries()
	{
		final CountryDataList countryDataList = new CountryDataList();
		countryDataList.setCountries(checkoutFacade.getDeliveryCountries());
		return countryDataList;
	}

	/**
	 * returns all possible titles (code and name).<br>
	 * Sample call : http://localhost:9001/rest/v1/titles<br>
	 * Request method : <code>GET</code>
	 * 
	 * @return list of titles
	 */
	@RequestMapping(value = "/{baseSiteId}/titles", method = RequestMethod.GET)
	@ResponseBody
	public TitleDataList getTitles()
	{
		final TitleDataList titleDataList = new TitleDataList();
		titleDataList.setTitles(userFacade.getTitles());
		return titleDataList;
	}

	/**
	 * returns all possible card types.<br>
	 * Sample call : http://localhost:9001/rest/v1/cardtypes<br>
	 * Request method : <code>GET</code>
	 * 
	 * @return list of card types
	 */
	@RequestMapping(value = "/{baseSiteId}/cardtypes", method = RequestMethod.GET)
	@ResponseBody
	public CardTypeDataList getCardTypes()
	{
		final CardTypeDataList cardTypeDataList = new CardTypeDataList();
		cardTypeDataList.setCardTypes(checkoutFacade.getSupportedCardTypes());
		return cardTypeDataList;
	}

}
