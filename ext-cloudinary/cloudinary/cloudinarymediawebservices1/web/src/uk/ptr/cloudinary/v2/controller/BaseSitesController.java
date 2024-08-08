/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import de.hybris.platform.commercefacades.basesites.BaseSiteFacade;
import de.hybris.platform.commercewebservicescommons.dto.basesite.BaseSiteListWsDTO;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import uk.ptr.cloudinary.basesite.data.BaseSiteDataList;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Controller
@RequestMapping(value = "/basesites")
@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 360)
@Api(tags = "Base Sites")
public class BaseSitesController extends BaseController
{
	@Resource(name = "baseSiteFacade")
	private BaseSiteFacade baseSiteFacade;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(nickname = "getBaseSites", value = "Get all base sites.", notes = "Get all base sites with corresponding base stores details in FULL mode.")
	public BaseSiteListWsDTO getBaseSites(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		final List<BaseSiteData> allBaseSites = baseSiteFacade.getAllBaseSites();
		final BaseSiteDataList baseSiteDataList = new BaseSiteDataList();
		baseSiteDataList.setBaseSites(allBaseSites);
		return getDataMapper().map(baseSiteDataList, BaseSiteListWsDTO.class, fields);
	}
}
