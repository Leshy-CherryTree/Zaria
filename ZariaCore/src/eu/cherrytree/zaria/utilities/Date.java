/****************************************/
/* Date.java							*/
/* Created on: Feb 3, 2012              */
/* Copyright Cherry Tree Studio 2012	*/
/* Released under EUPL v1.1				*/
/****************************************/
package eu.cherrytree.zaria.utilities;

import java.util.Calendar;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class Date
{
	//--------------------------------------------------------------------------
	
	private static final Calendar cal = Calendar.getInstance();
	
	//--------------------------------------------------------------------------
	
	private static void init()
	{
		cal.clear();
		cal.setTimeInMillis(System.currentTimeMillis());
	}
	
	//--------------------------------------------------------------------------

	public static String getDate()
	{
		init();

		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);

		String thetime = "";

		if(day < 10)
			thetime += "0" + day + ".";
		else
			thetime += day + ".";

		if(month < 10)
			thetime += "0" + month + ".";
		else
			thetime += month + ".";

		if(year < 10)
			thetime += "0" + year;
		else
			thetime += year;

		return thetime;
	}
	
	//--------------------------------------------------------------------------
	
	public static long getSeconds()
	{
		init();
		
		return cal.getTimeInMillis();
	}

	//--------------------------------------------------------------------------

	public static String getTime()
	{
		init();

		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);

		String thetime = "";

		if(hour < 10)
			thetime += "0" + hour + ":";
		else
			thetime += hour + ":";

		if(minute < 10)
			thetime += "0" + minute + ":";
		else
			thetime += minute + ":";

		if(second < 10)
			thetime += "0" + second;
		else
			thetime += second;

		return thetime;
	}

	//--------------------------------------------------------------------------

	public static String getFullDate()
	{
		return getDate() + " " + getTime();
	}

	//--------------------------------------------------------------------------

	public static int getSecond()
	{
		init();

		return cal.get(Calendar.SECOND);
	}

	//--------------------------------------------------------------------------

	public static int getMinute()
	{
		init();

		return cal.get(Calendar.MINUTE);
	}

	//--------------------------------------------------------------------------

	public static int getHour()
	{
		init();

		return cal.get(Calendar.HOUR_OF_DAY);
	}

	//--------------------------------------------------------------------------

	public static int getDay()
	{
		init();

		return cal.get(Calendar.DAY_OF_MONTH);
	}

	//--------------------------------------------------------------------------

	public static int getMonth()
	{
		init();

		return cal.get(Calendar.MONTH);
	}

	//--------------------------------------------------------------------------

	public static int getYear()
	{
		init();

		return cal.get(Calendar.YEAR);
	}

	//--------------------------------------------------------------------------
}
