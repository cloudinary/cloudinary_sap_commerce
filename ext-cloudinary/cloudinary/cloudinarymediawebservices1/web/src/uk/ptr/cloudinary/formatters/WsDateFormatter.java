/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package uk.ptr.cloudinary.formatters;

import java.util.Date;


public interface WsDateFormatter
{
	Date toDate(String timestamp);

	String toString(Date date);
}
