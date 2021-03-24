/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.cronjob;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.dao.CommerceCartDao;
import de.hybris.platform.commercewebservicescommons.model.OldCartRemovalCronJobModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;


/**
 * A Cron Job to clean up old carts.
 */
public class OldCartRemovalJob extends AbstractJobPerformable<OldCartRemovalCronJobModel>
{
	private static final Logger LOG = Logger.getLogger(OldCartRemovalJob.class);

	private CommerceCartDao commerceCartDao;
	private TimeService timeService;
	private UserService userService;

	private static final int DEFAULT_CART_MAX_AGE = 2419200;
	private static final int DEFAULT_ANONYMOUS_CART_MAX_AGE = 1209600;

	@Override
	public PerformResult perform(final OldCartRemovalCronJobModel job)
	{
		try
		{
			if (job.getSites() == null || job.getSites().isEmpty())
			{
				LOG.warn("There is no sites defined for " + job.getCode());
				return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
			}

			final int cartAge = job.getCartRemovalAge() != null ? job.getCartRemovalAge().intValue() : DEFAULT_CART_MAX_AGE;
			final int anonymousCartAge = job.getAnonymousCartRemovalAge() != null ?
					job.getAnonymousCartRemovalAge().intValue() :
					DEFAULT_ANONYMOUS_CART_MAX_AGE;

			for (final BaseSiteModel site : job.getSites())
			{
				for (final CartModel oldCart : getCommerceCartDao().getCartsForRemovalForSiteAndUser(
						new DateTime(getTimeService().getCurrentTime()).minusSeconds(cartAge).toDate(), site, null))
				{
					getModelService().remove(oldCart);
				}

				for (final CartModel oldCart : getCommerceCartDao().getCartsForRemovalForSiteAndUser(
						new DateTime(getTimeService().getCurrentTime()).minusSeconds(anonymousCartAge).toDate(), site,
						getUserService().getAnonymousUser()))
				{
					getModelService().remove(oldCart);
				}
			}

			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
		}
		catch (final Exception e)
		{
			LOG.error("Exception occurred during cart cleanup", e);
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}
	}

	protected CommerceCartDao getCommerceCartDao()
	{
		return commerceCartDao;
	}

	@Required
	public void setCommerceCartDao(final CommerceCartDao commerceCartDao)
	{
		this.commerceCartDao = commerceCartDao;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}
}
