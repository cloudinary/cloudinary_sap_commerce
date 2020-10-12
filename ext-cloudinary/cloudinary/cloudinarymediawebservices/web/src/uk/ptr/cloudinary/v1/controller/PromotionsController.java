/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.v1.controller;

import static uk.ptr.cloudinary.constants.YcommercewebservicesConstants.ENUM_VALUES_SEPARATOR;

import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.commercefacades.promotion.CommercePromotionFacade;
import de.hybris.platform.commercefacades.promotion.PromotionOption;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import uk.ptr.cloudinary.product.data.PromotionDataList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;


/**
 * Main Controller for Promotions
 */
@Controller("promotionsControllerV1")
@RequestMapping(value = "/{baseSiteId}/promotions")
public class PromotionsController extends BaseController
{
	private static final String ORDER_PROMOTION = "order";
	private static final String PRODUCT_PROMOTION = "product";
	private static final String ALL_PROMOTIONS = "all";
	@Resource(name = "commercePromotionFacade")
	private CommercePromotionFacade commercePromotionFacade;

	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public PromotionDataList getPromotions(@RequestParam final String type,
			@RequestParam(required = false) final String promotionGroup) throws RequestParameterException //NOSONAR
	{
		validateTypeParameter(type);
		final PromotionDataList promotionDataList = new PromotionDataList();
		promotionDataList.setPromotions(getPromotionList(type, promotionGroup));
		return promotionDataList;
	}

	/**
	 * Web service handler for the getPromotionByCode call. The options are turned into a Set<PromotionOption> and passed
	 * on to the facade. BASIC is included by default. <br/>
	 * Sample Call: https://localhost:9002/rest/v1/{SITE}/promotions/{CODE}?options=BASIC%2CEXTENDED <br/>
	 * Keep in mind ',' needs to be encoded as %2C
	 *
	 * @param code
	 *           - the unique code used to identify a promotion
	 * @param options
	 *           - a String enumerating the detail level, values are BASIC (included by default), EXTENDED. Combine by
	 *           using a ',', which needs to be encoded as part of a URI using URLEncoding: %2C.
	 * @return the {@link PromotionData} which will be marshaled to JSON or XML based on Accept-Header
	 */
	@Secured("ROLE_TRUSTED_CLIENT")
	@RequestMapping(value = "/{code}", method = RequestMethod.GET)
	@ResponseBody
	public PromotionData getPromotionByCode(@PathVariable final String code, @RequestParam(required = false) final String options)
	{
		final PromotionData promotionData;
		if (Strings.isNullOrEmpty(options))
		{
			promotionData = commercePromotionFacade.getPromotion(code);
		}
		else
		{
			promotionData = commercePromotionFacade.getPromotion(code, extractOptions(options));
		}
		return promotionData;
	}

	protected void validateTypeParameter(final String type) throws RequestParameterException //NOSONAR
	{
		if (!ORDER_PROMOTION.equals(type) && !PRODUCT_PROMOTION.equals(type) && !ALL_PROMOTIONS.equals(type))
		{
			throw new RequestParameterException(
					"Parameter type=" + sanitize(type)
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

	protected Set<PromotionOption> extractOptions(final String options)
	{
		final String[] optionsStrings = options.split(ENUM_VALUES_SEPARATOR);

		final Set<PromotionOption> opts = new HashSet<PromotionOption>();
		for (final String option : optionsStrings)
		{
			opts.add(PromotionOption.valueOf(option));
		}
		return opts;
	}

	public CommercePromotionFacade getCommercePromotionFacade()
	{
		return commercePromotionFacade;
	}
}
