/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues.impl;

import uk.ptr.cloudinary.queues.UpdateQueue;

import java.util.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;


/**
 * Abstract implementation of {@link uk.ptr.cloudinary.queues.UpdateQueue} using {@link TreeMap}
 * for storing elements WARNING: this queue has limited capacity due to its in-memory nature
 */
public abstract class AbstractUpdateQueue<T> extends TreeMap<Long, T> implements UpdateQueue<T> //NOSONAR
{
	protected static final int DEFAULT_MAX_CAPACITY = 1000;

	private int maxCapacity = DEFAULT_MAX_CAPACITY;

	@Override
	public List<T> getItems()
	{
		return Lists.newArrayList(values());
	}

	@Override
	public List<T> getItems(final Date newerThan)
	{
		return Lists.newArrayList(tailMap(Long.valueOf(newerThan.getTime())).values());
	}

	@Override
	public void addItem(final T item)
	{
		if (size() < maxCapacity)
		{
			Long timeKey = getTimeKey(item);
			while (containsKey(timeKey))
			{
				timeKey = Long.valueOf(timeKey.longValue() + 1);
			}
			put(timeKey, item);
		}
	}

	@Override
	public void addItems(final List<T> items)
	{
		for (final T item : items)
		{
			addItem(item);
		}
	}

	@Override
	public void removeItems(final Date olderThan)
	{
		final SortedMap<Long, T> clone = (SortedMap<Long, T>) clone();
		final SortedMap<Long, T> newerThan = clone.tailMap(Long.valueOf(olderThan.getTime()));
		clear();
		putAll(newerThan);
	}

	@Override
	public void removeItems()
	{
		clear();
	}

	@Override
	public void removeItems(final Predicate<T> predicate)
	{
		final Iterator<T> it = values().iterator();
		while (it.hasNext())
		{
			if (predicate.apply(it.next()))
			{
				it.remove();
			}
		}
	}

	@Override
	public T getLastItem()
	{
		T ret = null;
		if (!isEmpty())
		{
			ret = lastEntry().getValue();
		}
		return ret;
	}

	public int getMaxCapacity()
	{
		return maxCapacity;
	}

	public void setMaxCapacity(final int maxCapacity)
	{
		this.maxCapacity = maxCapacity;
	}

	protected Long getTimeKey(@SuppressWarnings("unused") final T item) //NOSONAR
	{
		return Long.valueOf(System.currentTimeMillis());
	}

}
