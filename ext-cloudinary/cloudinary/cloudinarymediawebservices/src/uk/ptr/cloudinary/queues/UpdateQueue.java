/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.queues;

import java.util.Date;
import java.util.List;

import com.google.common.base.Predicate;


/**
 * Queue for storing update elements
 */
public interface UpdateQueue<T>
{
	/**
	 * Returns all elements in the queue.
	 *
	 * @return list of stored elements
	 */
	List<T> getItems();

	/**
	 * Returns all elements in the queue newer than a specific day
	 *
	 * @param newerThan
	 * 		threshold date
	 * @return list of stored elements
	 */
	List<T> getItems(Date newerThan);

	/**
	 * Adds all elements from the list to the queue
	 *
	 * @param items
	 * 		elements to be stored
	 */
	void addItems(List<T> items);

	/**
	 * Adds item to the queue
	 *
	 * @param items
	 * 		elements to be stored
	 */
	void addItem(T items);

	/**
	 * Removes all elements from the queue older than a specific date
	 *
	 * @param olderThan
	 * 		threshold date
	 */
	void removeItems(Date olderThan);

	/**
	 * Removes all elements from the queue
	 */
	void removeItems();

	/**
	 * Returns last item from the queue
	 *
	 * @return last element
	 */
	T getLastItem();

	/**
	 * Removes all matched elements
	 *
	 * @param predicate
	 */
	void removeItems(Predicate<T> predicate);
}
