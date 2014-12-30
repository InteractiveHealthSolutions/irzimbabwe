
package org.irdresearch.irzimbabwe.server;

import java.util.Date;

public class MainClass
{
	public static void main (String[] args)
	{
		try
		{
			ServerServiceImpl impl = new ServerServiceImpl ();
			Date dt = new Date ();
			for (int i = 0; i < 1; i++)
			{
				String query = "insert into irzimbabwe_prt.tmp values (" + dt.getTime () + ");";
				impl.execute (query);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}

	@SuppressWarnings("deprecation")
	public static Date parseDate (String str)
	{
		try
		{
			String[] parts = str.split (" ");
			String[] dateParts = parts[0].split ("/");
			int date, month, year, hour = 0, min = 0;
			date = Integer.parseInt (dateParts[0]);
			month = Integer.parseInt (dateParts[1]);
			year = Integer.parseInt (dateParts[2]);

			try
			{
				String[] timeParts = parts[1].split (":");
				hour = Integer.parseInt (timeParts[0]);
				min = Integer.parseInt (timeParts[1]);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}

			Date dt = new Date (year - 1900, month - 1, date, hour, min, 0);
			return dt;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static int findIndex (String[] array, String str)
	{
		for (int i = 0; i < array.length; i++)
			if (array[i].equalsIgnoreCase (str))
				return i;
		return -1;
	}
}
