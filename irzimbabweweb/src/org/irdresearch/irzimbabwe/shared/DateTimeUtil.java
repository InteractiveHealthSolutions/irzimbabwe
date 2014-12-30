/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.irzimbabwe.shared;

import java.util.Date;
import com.google.gwt.i18n.client.DateTimeFormat;

public class DateTimeUtil
{
	public static final String	SQL_DATE		= "yyyy-MM-dd";
	public static final String	SQL_DATETIME	= "yyyy-MM-dd HH:mm:ss";

	public static String getFormattedDate (Date date, String format)
	{
		DateTimeFormat formatter = DateTimeFormat.getFormat (format);
		return formatter.format (date);
	}

	public static boolean isFutureDate (Date value)
	{
		return value.after (new Date ());
	}
	
	public static boolean isPastDate (Date value)
	{
		return value.before (new Date ());
	}

	@SuppressWarnings("deprecation")
	public static int compareDateOnly (Date date1, Date date2)
	{
		Date date1Only = new Date (date1.getYear (), date1.getMonth (), date1.getDate ());
		Date date2Only = new Date (date2.getYear (), date2.getMonth (), date2.getDate ());
		return date1Only.compareTo (date2Only);
	}
	
	@SuppressWarnings("deprecation")
	public static int compareTimeOnly (Date date1, Date date2)
	{
		Date time1 = new Date (date1.getYear (), date1.getMonth (), date1.getDate (), date1.getHours (), date1.getMinutes (), date1.getSeconds ());
		Date time2 = new Date (date1.getYear (), date1.getMonth (), date1.getDate (), date2.getHours (), date2.getMinutes (), date2.getSeconds ());
		return time1.compareTo (time2);
	}
}
