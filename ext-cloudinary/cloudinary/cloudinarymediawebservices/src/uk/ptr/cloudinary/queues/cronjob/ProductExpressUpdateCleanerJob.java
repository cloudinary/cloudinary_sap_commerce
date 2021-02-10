/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.cronjob;

import de.hybris.platform.commercewebservicescommons.model.expressupdate.cron.ProductExpressUpdateCleanerCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import uk.ptr.cloudinary.queues.impl.ProductExpressUpdateQueue;
import java.util.Date;


/**
 * A Cron Job for cleaning up {@link ProductExpressUpdateQueue}.
 */
public class ProductExpressUpdateCleanerJob extends AbstractJobPerformable<ProductExpressUpdateCleanerCronJobModel>
{
	private ProductExpressUpdateQueue productExpressUpdateQueue;

	@Override
	public PerformResult perform(final ProductExpressUpdateCleanerCronJobModel cronJob)
	{
		final Date timestamp = new Date(System.currentTimeMillis() - (cronJob.getQueueTimeLimit().intValue() * 60 * 1000));
		productExpressUpdateQueue.removeItems(timestamp);
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	public void setProductExpressUpdateQueue(final ProductExpressUpdateQueue productExpressUpdateQueue)
	{
		this.productExpressUpdateQueue = productExpressUpdateQueue;
	}
}
