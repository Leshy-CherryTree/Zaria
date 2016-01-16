/****************************************/
/* ApplicationProperties.java           */
/* Created on: Feb 2, 2012				*/
/* Copyright Cherry Tree Studio 2012	*/
/* Released under EUPL v1.1				*/
/* Ported from the jPAJ project.		*/
/****************************************/

package eu.cherrytree.zaria.base;

import java.io.IOException;

import java.util.Properties;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ApplicationProperties
{	
	//--------------------------------------------------------------------------
	
	public static final class ApplicationPropertiesNotFoundException extends Exception
	{
		public ApplicationPropertiesNotFoundException(Exception ex)
		{
			super("App definition missing or corrupt! Check for " + ApplicationProperties.getPropertyPath(), ex);
		}
	}
	
    //--------------------------------------------------------------------------

	private static Properties properties = new Properties();
	private static String propertyPath = "/Configuration/app.properties";
	
    private static String applicationFullName;
    private static String applicationShortName;

    private static String developerFullName;
    private static String developerShortName;
	
	private static String lookAndFeel;

    private static String settingsImage;

    private static String [] icons;

    private static int majorVersionNumber;
    private static int minorVersionNumber;
    private static int revisionNumber;

    private static String consoleFontPath;
    private static float consoleFontSize;

    private static float consoleScreenCoverage;
    private static float consoleOpenSpeed;

    private static float[] consoleFontOutColor = new float[3];
    private static float[] consoleFontErrColor = new float[3];
    private static float[] consoleBackgroundColor = new float[4];
	private static boolean consoleDefaultEnabled;
	
	//--------------------------------------------------------------------------
	
	public static void setPropertyPath(String propertyPath)
	{
		ApplicationProperties.propertyPath = propertyPath;
	}

	//--------------------------------------------------------------------------
	
	public static String getPropertyPath()
	{
		return propertyPath;
	}
			
	//--------------------------------------------------------------------------

	public static void load() throws ApplicationPropertiesNotFoundException
	{
		try
		{
			properties.load(ApplicationProperties.class.getResourceAsStream(propertyPath));
		}
		catch (IOException ex)
		{
			throw new ApplicationPropertiesNotFoundException(ex);
		}

		applicationFullName = properties.getProperty("applicationFullName");
		applicationShortName = properties.getProperty("applicationShortName");

		developerFullName = properties.getProperty("developerFullName");
		developerShortName = properties.getProperty("developerShortName");
		
		lookAndFeel = properties.getProperty("lookAndFeel", "javax.swing.plaf.nimbus.NimbusLookAndFeel");

		settingsImage = properties.getProperty("settingsImage", null);

		String ico = properties.getProperty("icons", null);

		if(ico != null)
		{
			icons = ico.split(";");

			for(int i = 0 ; i < icons.length ; i++)
				icons[i] = icons[i].trim();
		}

		majorVersionNumber = Integer.parseInt(properties.getProperty("majorVersionNumber"));
		minorVersionNumber = Integer.parseInt(properties.getProperty("minorVersionNumber"));
		revisionNumber = Integer.parseInt(properties.getProperty("revisionNumber"));

		consoleFontPath = properties.getProperty("consoleFontPath", "Interface/Fonts/Default.fnt");

		consoleFontSize = Float.parseFloat(properties.getProperty("consoleFontSize", "14.0"));
		consoleScreenCoverage = Float.parseFloat(properties.getProperty("consoleScreenCoverage", "0.35"));
		consoleOpenSpeed = Float.parseFloat(properties.getProperty("consoleOpenSpeed", "500.0f"));

		consoleFontOutColor[0] = Float.parseFloat(properties.getProperty("consoleFontOutColorR", "1.0"));
		consoleFontOutColor[1] = Float.parseFloat(properties.getProperty("consoleFontOutColorG", "1.0"));
		consoleFontOutColor[2] = Float.parseFloat(properties.getProperty("consoleFontOutColorB", "1.0"));

		consoleFontErrColor[0] = Float.parseFloat(properties.getProperty("consoleFontErrColorR", "1.0"));
		consoleFontErrColor[1] = Float.parseFloat(properties.getProperty("consoleFontErrColorG", "0.0"));
		consoleFontErrColor[2] = Float.parseFloat(properties.getProperty("consoleFontErrColorB", "0.0"));

		consoleBackgroundColor[0] = Float.parseFloat(properties.getProperty("consoleFontBgdColorR", "0.0"));
		consoleBackgroundColor[1] = Float.parseFloat(properties.getProperty("consoleFontBgdColorG", "0.0"));
		consoleBackgroundColor[2] = Float.parseFloat(properties.getProperty("consoleFontBgdColorB", "0.0"));
		consoleBackgroundColor[3] = Float.parseFloat(properties.getProperty("consoleFontBgdColorA", "0.6"));

		consoleDefaultEnabled = Boolean.parseBoolean(properties.getProperty("consoleDefaultEnabled", "true"));
	}

	//--------------------------------------------------------------------------

	public static String getApplicationFullName()
	{
		return applicationFullName;
	}

	//--------------------------------------------------------------------------

	public static String getApplicationShortName()
	{
		return applicationShortName;
	}

	//--------------------------------------------------------------------------

	public static String getDeveloperFullName()
	{
		return developerFullName;
	}

	//--------------------------------------------------------------------------

	public static String getDeveloperShortName()
	{
		return developerShortName;
	}

	//--------------------------------------------------------------------------
	
	public static String getLookAndFeel()
	{
		return lookAndFeel;
	}
	
	//--------------------------------------------------------------------------

	public static int getMajorVersionNumber()
	{
		return majorVersionNumber;
	}

	//--------------------------------------------------------------------------

	public static int getMinorVersionNumber()
	{
		return minorVersionNumber;
	}

	//--------------------------------------------------------------------------

	public static int getRevisionNumber()
	{
		return revisionNumber;
	}

	//--------------------------------------------------------------------------

	public static String getConsoleFontPath()
	{
		return consoleFontPath;
	}

	//--------------------------------------------------------------------------

	public static float getConsoleFontSize()
	{
		return consoleFontSize;
	}

	//--------------------------------------------------------------------------

	public static float getConsoleScreenCoverage()
	{
		return consoleScreenCoverage;
	}

	//--------------------------------------------------------------------------

	public static float[] getConsoleBackgroundColor()
	{
		return consoleBackgroundColor;
	}

	//--------------------------------------------------------------------------

	public static float[] getConsoleFontErrColor()
	{
		return consoleFontErrColor;
	}

	//--------------------------------------------------------------------------

	public static float[] getConsoleFontOutColor()
	{
		return consoleFontOutColor;
	}

	//--------------------------------------------------------------------------

	public static float getConsoleOpenSpeed()
	{
		return consoleOpenSpeed;
	}

	//--------------------------------------------------------------------------

	public static boolean isConsoleDefaultEnabled()
	{
		return consoleDefaultEnabled;
	}

	//--------------------------------------------------------------------------

	public static String getSettingsImage()
	{
		return settingsImage;
	}

	//--------------------------------------------------------------------------

	public static String[] getIconPaths()
	{
		return icons;
	}

	//--------------------------------------------------------------------------
	
	public static String getProperty(String name)
	{
		return properties.getProperty(name);
	}
	
	//--------------------------------------------------------------------------

	static String getString()
	{
		String ret = "";

		ret += "Application Full Name: " + applicationFullName + "\n";
		ret += "Application Short Name: " + applicationShortName + "\n";

		ret += "\n";

		ret += "Developer Full Name: " + developerFullName + "\n";
		ret += "Developer Short Name: " + developerShortName + "\n";

		ret += "\n";

		ret += "Settings Image: " + settingsImage + "\n";

		ret += "\n";

		if(icons == null)
			ret += "Icons: null \n";
		else
		{
			for(int i = 0 ; i < icons.length ; i++)
				ret += "Icon " + "[" + i + "]: " + icons[i] + "\n";
		}

		ret += "\n";

		ret += "Application version: " + majorVersionNumber + "." + minorVersionNumber + "." + revisionNumber + "\n";

		ret += "\n";

		ret += "Console Font Path: " + consoleFontPath + "\n";
		ret += "Console Font Size: " + consoleFontSize + "\n";
		ret += "Console Font Color: " + consoleFontOutColor[0] + " " + consoleFontOutColor[1] + " " + consoleFontOutColor[2] + " " + "\n";
		ret += "Console Font Err Color: " + consoleFontErrColor[0] + " " + consoleFontErrColor[1] + " " + consoleFontErrColor[2] + " " + "\n";
		ret += "Console Background Color: " + consoleBackgroundColor[0] + " " + consoleBackgroundColor[1] + " " + consoleBackgroundColor[2] + " " + consoleBackgroundColor[3] + " " + "\n";
		ret += "Console Screen Coverage: " + consoleScreenCoverage + "\n";
		ret += "Console Open Speed: " + consoleOpenSpeed + "\n";
		ret += "Console Open on Default: " + consoleDefaultEnabled + "\n";

		return ret;
	}

	//--------------------------------------------------------------------------
}