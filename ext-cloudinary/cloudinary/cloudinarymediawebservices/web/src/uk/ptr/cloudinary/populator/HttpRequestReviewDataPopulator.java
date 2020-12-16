/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.populator;

import static de.hybris.platform.customerreview.model.CustomerReviewModel.*;

import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


/**
 * Populates {@link ReviewData} instance based on http request parameters:<br>
 * <ul>
 * <li>rating</li>
 * <li>headline</li>
 * <li>comment</li>
 * <li>alias</li>
 * </ul>
 */
@Component("httpRequestReviewDataPopulator")
@Scope("prototype")
public class HttpRequestReviewDataPopulator extends AbstractHttpRequestDataPopulator
		implements Populator<HttpServletRequest, ReviewData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final HttpServletRequest request, final ReviewData reviewData) throws ConversionException //NOSONAR
	{
		Assert.notNull(request, "Parameter source cannot be null.");
		Assert.notNull(reviewData, "Parameter target cannot be null.");

		reviewData.setAlias(updateStringValueFromRequest(request, ALIAS, reviewData.getAlias()));
		reviewData.setHeadline(updateStringValueFromRequest(request, HEADLINE, reviewData.getHeadline()));
		reviewData.setComment(updateStringValueFromRequest(request, COMMENT, reviewData.getComment()));
		reviewData.setRating(updateDoubleValueFromRequest(request, RATING, reviewData.getRating()));

	}

}
