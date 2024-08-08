/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.cronjob;

import de.hybris.platform.commercewebservicescommons.model.expressupdate.cron.OrderStatusUpdateCleanerCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import uk.ptr.cloudinary.queues.impl.OrderStatusUpdateQueue;

import java.util.Date;


/**
 * A Cron Job for cleaning up {@link uk.ptr.cloudinary.queues.impl.OrderStatusUpdateQueue}.
 */
public class OrderStatusUpdateCleanerJob extends AbstractJobPerformable<OrderStatusUpdateCleanerCronJobModel>
{
	private OrderStatusUpdateQueue orderStatusUpdateQueue;

	@Override
	public PerformResult perform(final OrderStatusUpdateCleanerCronJobModel cronJob)
	{
		final Date timestamp = new Date(System.currentTimeMillis() - (cronJob.getQueueTimeLimit().intValue() * 60 * 1000));
		getOrderStatusUpdateQueue().removeItems(timestamp);
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected OrderStatusUpdateQueue getOrderStatusUpdateQueue()
	{
		return orderStatusUpdateQueue;
	}

	public void setOrderStatusUpdateQueue(final OrderStatusUpdateQueue orderStatusUpdateQueue)
	{
		this.orderStatusUpdateQueue = orderStatusUpdateQueue;
	}
}
