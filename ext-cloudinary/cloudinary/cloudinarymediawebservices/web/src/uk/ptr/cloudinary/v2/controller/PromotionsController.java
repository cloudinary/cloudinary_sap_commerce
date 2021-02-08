/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v2.controller;

import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commercefacades.promotion.CommercePromotionFacade;
import de.hybris.platform.commercefacades.promotion.PromotionOption;
import de.hybris.platform.commercewebservicescommons.dto.product.PromotionListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.PromotionWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import uk.ptr.cloudinary.product.data.PromotionDataList;

import javax.annotation.Resource;

import java.util.EnumSet;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;


/**
 * Main Controller for Promotions
 *
 * @pathparam code Promotion identifier (code)
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/promotions")
@CacheControl(directive = CacheControlDirective.PUBLIC, maxAge = 300)
@Api(tags = "Promotions")
public class PromotionsController extends BaseController
{
	private static final String ORDER_PROMOTION = "order";
	private static final String PRODUCT_PROMOTION = "product";
	private static final String ALL_PROMOTIONS = "all";
	private static final EnumSet<PromotionOption> OPTIONS = EnumSet.allOf(PromotionOption.class);

	@Resource(name = "commercePromotionFacade")
	private CommercePromotionFacade commercePromotionFacade;

	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Cacheable(value = "promotionCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'getPromotions',#type,#promotionGroup,#fields)")
	@ApiOperation(nickname = "getPromotions", value = "Get a list of promotions.", notes =
			"Returns promotions defined for a current base site. Requests pertaining to promotions have been developed "
					+ "for the previous version of promotions and vouchers and therefore some of them are currently not compatible with the new promotion engine.", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	@ApiBaseSiteIdParam
	public PromotionListWsDTO getPromotions(@ApiParam(value =
			"Defines what type of promotions should be returned. Values supported for that parameter are: <ul><li>all: All available promotions are "
					+ "returned</li><li>product: Only product promotions are returned</li><li>order: Only order promotions are returned</li></ul>", allowableValues = "all, product, order", required = true) @RequestParam final String type,
			@ApiParam(value = "Only promotions from this group are returned") @RequestParam(required = false) final String promotionGroup,
			@ApiFieldsParam(defaultValue = BASIC_FIELD_SET) @RequestParam(defaultValue = BASIC_FIELD_SET) final String fields)
	{
		validateTypeParameter(type);

		final PromotionDataList promotionDataList = new PromotionDataList();
		promotionDataList.setPromotions(getPromotionList(type, promotionGroup));
		return getDataMapper().map(promotionDataList, PromotionListWsDTO.class, fields);
	}

	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@Cacheable(value = "promotionCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(false,true,'getPromotions',#code,#fields)")
	@ResponseBody
	@ApiOperation(nickname = "getPromotion", value = "Get a promotion based on code", notes =
			"Returns details of a single promotion specified by a promotion code. Requests pertaining to "
					+ "promotions have been developed for the previous version of promotions and vouchers and therefore some of them are currently not compatible with the new promotion engine.", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	@ApiBaseSiteIdParam
	public PromotionWsDTO getPromotion(
			@ApiParam(value = "Promotion identifier (code)", required = true) @PathVariable final String code,
			@ApiFieldsParam(defaultValue = BASIC_FIELD_SET) @RequestParam(defaultValue = BASIC_FIELD_SET) final String fields)
	{
		final PromotionData promotionData = commercePromotionFacade.getPromotion(code, OPTIONS);
		return getDataMapper().map(promotionData, PromotionWsDTO.class, fields);
	}

	protected void validateTypeParameter(final String type)
	{
		if (!ORDER_PROMOTION.equals(type) && !PRODUCT_PROMOTION.equals(type) && !ALL_PROMOTIONS.equals(type))
		{
			throw new RequestParameterException("Parameter type=" + sanitize(type)
					+ " is not supported. Permitted values for this parameter are : 'order', 'product' or 'all'",
					RequestParameterException.INVALID, "type");
		}
	}

	protected List<PromotionData> getPromotionList(final String type, final String promotionGroup)
	{
		if (promotionGroup == null || promotionGroup.isEmpty())
		{
			return getPromotionList(type);
		}

		List<PromotionData> promotions = null;
		if (ORDER_PROMOTION.equals(type))
		{
			promotions = getCommercePromotionFacade().getOrderPromotions(promotionGroup);
		}
		else if (PRODUCT_PROMOTION.equals(type))
		{
			promotions = getCommercePromotionFacade().getProductPromotions(promotionGroup);
		}
		else if (ALL_PROMOTIONS.equals(type))
		{
			promotions = getCommercePromotionFacade().getProductPromotions(promotionGroup);
			promotions.addAll(getCommercePromotionFacade().getOrderPromotions(promotionGroup));
		}
		return promotions;

	}

	protected List<PromotionData> getPromotionList(final String type)
	{
		List<PromotionData> promotions = null;
		if (ORDER_PROMOTION.equals(type))
		{
			promotions = getCommercePromotionFacade().getOrderPromotions();
		}
		else if (PRODUCT_PROMOTION.equals(type))
		{
			promotions = getCommercePromotionFacade().getProductPromotions();
		}
		else if (ALL_PROMOTIONS.equals(type))
		{
			promotions = getCommercePromotionFacade().getProductPromotions();
			promotions.addAll(getCommercePromotionFacade().getOrderPromotions());
		}
		return promotions;
	}

	protected CommercePromotionFacade getCommercePromotionFacade()
	{
		return commercePromotionFacade;
	}
}
