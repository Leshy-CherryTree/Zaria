/****************************************/
/* ApplicationInstance.java				*/
/* Created on: Jan 27, 2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/* Ported from the jPAJ project.			*/
/****************************************/

package eu.cherrytree.zaria.base;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ApplicationInstance
{
	//--------------------------------------------------------------------------
	
	private static ZariaApplication instance;
	private static float screenWidth;
	private static float screenHeight;
	
	//--------------------------------------------------------------------------

	public static void setInstance(ZariaApplication instance, float screenWidth, float screenHeight)
	{
		ApplicationInstance.instance = instance;
		ApplicationInstance.screenWidth = screenWidth;
		ApplicationInstance.screenHeight = screenHeight;
	}
	
	//--------------------------------------------------------------------------
	
	public static float getScreenHeight()
	{
		return screenHeight;
	}
	
	//--------------------------------------------------------------------------

	public static float getScreenWidth()
	{
		return screenWidth;
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
	
	public static Object loadAsset(String path)
	{
		return instance.loadAsset(path);
	}
	
	//--------------------------------------------------------------------------
}
