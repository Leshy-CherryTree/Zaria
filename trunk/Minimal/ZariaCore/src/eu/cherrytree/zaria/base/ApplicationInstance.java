/****************************************/
/* ApplicationInstance.java				*/
/* Created on: Jan 27, 2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/* Ported from the jPAJ project.			*/
/****************************************/

package eu.cherrytree.zaria.base;

import java.io.InputStream;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ApplicationInstance
{
	//--------------------------------------------------------------------------
	
	private static ZariaApplication instance;
	
	//--------------------------------------------------------------------------

	public static void setInstance(ZariaApplication instance)
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
			
	public static String getFullApplicationName()
	{
		return instance.getFullApplicationName();
	}
	
	//--------------------------------------------------------------------------
	
	public static String getShortApplicationName()
	{
		return instance.getShortApplicationName();
	}
	
	//--------------------------------------------------------------------------	
	
	public static String getDeveloperFullName()
	{
		return instance.getDeveloperFullName();
	}
	
	//--------------------------------------------------------------------------
	
	public static String getDeveloperShortName()
	{
		return instance.getDeveloperShortName();
	}
	
	//--------------------------------------------------------------------------
	
	public static String getVersion()
	{
		return instance.getVersion();
	}
	
	//--------------------------------------------------------------------------
	
	public static String getSavePath()
	{
		return instance.getSavePath();
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
	
	public static <Type> Type loadAsset(String path, Class<Type> type)
	{
		return (Type) instance.loadAsset(path, type);
	}

	//--------------------------------------------------------------------------
}
