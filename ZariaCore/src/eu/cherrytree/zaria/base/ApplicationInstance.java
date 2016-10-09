/****************************************/
/* ApplicationInstance.java				*/
/* Created on: Jan 27, 2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/* Ported from the jPAJ project.			*/
/****************************************/

package eu.cherrytree.zaria.base;

import java.util.concurrent.ScheduledThreadPoolExecutor;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ApplicationInstance
{
	//--------------------------------------------------------------------------
	
	private static ZariaApplication instance;
	
	//--------------------------------------------------------------------------

	static void setInstance(ZariaApplication instance)
	{
		ApplicationInstance.instance = instance;
	}
	
	//--------------------------------------------------------------------------
	
	public static float getScreenHeight()
	{
		return instance.getScreenHeight();
	}
	
	//--------------------------------------------------------------------------

	public static float getScreenWidth()
	{
		return instance.getScreenWidth();
	}
	
	//--------------------------------------------------------------------------
	
	public static boolean isCursorHidded()
	{
		return instance.isCursorHidded();	
	}
	
	//--------------------------------------------------------------------------
	
	public static void showCursor()
	{
		instance.showCursor();	
	}
	
	//--------------------------------------------------------------------------
	
	public static void hideCursor()
	{
		instance.hideCursor();	
	}
	
	//--------------------------------------------------------------------------
			
	public static String getFullApplicationName()
	{
		return ApplicationProperties.getApplicationFullName();
	}
	
	//--------------------------------------------------------------------------
	
	public static String getShortApplicationName()
	{
		return ApplicationProperties.getApplicationShortName();
	}
	
	//--------------------------------------------------------------------------	
	
	public static String getDeveloperFullName()
	{
		return ApplicationProperties.getDeveloperFullName();
	}
	
	//--------------------------------------------------------------------------
	
	public static String getDeveloperShortName()
	{
		return ApplicationProperties.getDeveloperShortName();
	}
	
	//--------------------------------------------------------------------------
	
	public static String getVersion()
	{
		return ApplicationProperties.getMajorVersionNumber() + "." + ApplicationProperties.getMinorVersionNumber() + "." + ApplicationProperties.getRevisionNumber();
	}

	//--------------------------------------------------------------------------
	
	public static void save(String fileName)
	{
		instance.save(fileName);
	}
		
	//--------------------------------------------------------------------------
	
	public static void load(String fileName)
	{
		instance.load(fileName);
	}
		
	//--------------------------------------------------------------------------
	
    public static void quit(int status)
	{
		instance.quit(status);
	}
	
	//--------------------------------------------------------------------------
	
	public static long getMainThreadId()
	{
		return instance.getMainThreadId();
	}
	
	//--------------------------------------------------------------------------
	
	public static boolean isMainThread()
	{
		return Thread.currentThread().getId() == instance.getMainThreadId();
	}

	//--------------------------------------------------------------------------

	public static ScheduledThreadPoolExecutor getThreadPoolExecutor()
	{
		return instance.getThreadPoolExecutor();
	}
	
	//--------------------------------------------------------------------------
	
	public static String getSavePath()
	{
		return getUserDataLocation() + "/saves/";
	}

	//--------------------------------------------------------------------------

	public static String getUserDataLocation()
	{
		return SystemProperties.getUserHome() + "/." + getShortApplicationName();
	}
	
	//--------------------------------------------------------------------------
}
