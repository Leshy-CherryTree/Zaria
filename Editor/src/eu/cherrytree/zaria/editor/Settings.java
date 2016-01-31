/****************************************/
/* Settings.java							*/
/* Created on: 07-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.TextEditorState;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class Settings
{
	//--------------------------------------------------------------------------
	
	private static Preferences preferences;
	
	private static final String windowPosX = "window posx";
	private static final String windowPosY = "window posy";	
	private static final String windowHeight = "window width";
	private static final String windowWidth = "window height";	
	private static final String windowMaximized = "window maximized";
	
	private static final String consolePosX = "consolePosX";
	private static final String consolePosY = "consolePosY";
	private static final String consoleHeight = "consoleWidth";
	private static final String consoleWidth = "consoleHeight";	
	
	private static final String antialiasing = "antialiasing";
	private static final String fractionalFontMetrics = "fractionalFontMetrics";
	private static final String editorFontSize = "editorFontSize";
	private static final String largeUI = "largeUI";
	
	private static final String recentFile = "recentFile";
	
	//--------------------------------------------------------------------------
	
	static void init(String projectName)
	{
		Settings.preferences = Preferences.userNodeForPackage(EditorApplication.class).node(projectName);		
	}
	
	//--------------------------------------------------------------------------

	public static int getWindowPosX()
	{
		return preferences.getInt(windowPosX, 0);
	}
	
	//--------------------------------------------------------------------------

	public static void setWindowPosX(int value)
	{
		 preferences.putInt(windowPosX, value);
	}
	
	//--------------------------------------------------------------------------

	public static int getWindowPosY()
	{
		return preferences.getInt(windowPosY, 0);
	}
	
	//--------------------------------------------------------------------------

	public static void setWindowPosY(int value)
	{
		 preferences.putInt(windowPosY, value);
	}
	//--------------------------------------------------------------------------
	

	public static int getWindowHeight()
	{
		return preferences.getInt(windowHeight, 600);
	}

	//--------------------------------------------------------------------------
	
	public static void setWindowHeight(int value)
	{
		 preferences.putInt(windowHeight, value);
	}
	
	//--------------------------------------------------------------------------

	public static int getWindowWidth()
	{
		return preferences.getInt(windowWidth, 800);
	}
	
	//--------------------------------------------------------------------------

	public static void setWindowWidth(int value)
	{
		 preferences.putInt(windowWidth, value);
	}
	
	//--------------------------------------------------------------------------

	public static boolean getWindowMaximized()
	{
		return preferences.getBoolean(windowMaximized, true);
	}
	
	//--------------------------------------------------------------------------

	public static void setWindowMaximized(boolean value)
	{
		 preferences.putBoolean(windowMaximized, value);
	}
	
	//--------------------------------------------------------------------------

	public static int getConsolePosX()
	{
		return preferences.getInt(consolePosX, 0);
	}
	
	//--------------------------------------------------------------------------

	public static void setConsolePosX(int value)
	{
		 preferences.putInt(consolePosX, value);
	}
	
	//--------------------------------------------------------------------------

	public static int getConsolePosY()
	{
		return preferences.getInt(consolePosY, 0);
	}
	
	//--------------------------------------------------------------------------

	public static void setConsolePosY(int value)
	{
		 preferences.putInt(consolePosY, value);
	}
	//--------------------------------------------------------------------------
	

	public static int getConsoleHeight()
	{
		return preferences.getInt(consoleHeight, 600);
	}

	//--------------------------------------------------------------------------
	
	public static void setConsoleHeight(int value)
	{
		 preferences.putInt(consoleHeight, value);
	}
	
	//--------------------------------------------------------------------------

	public static int getConsoleWidth()
	{
		return preferences.getInt(consoleWidth, 800);
	}
	
	//--------------------------------------------------------------------------

	public static void setConsoleWidth(int value)
	{
		 preferences.putInt(consoleWidth, value);
	}

	//--------------------------------------------------------------------------


	public static boolean getAntialiasing()
	{
		return preferences.getBoolean(antialiasing, true);
	}
	
	//--------------------------------------------------------------------------

	public static void setAntialiasing(boolean value)
	{
		 preferences.putBoolean(antialiasing, value);
	}
	
	//--------------------------------------------------------------------------

	public static boolean getFractionalFontMetrics()
	{
		return preferences.getBoolean(fractionalFontMetrics, true);
	}
	
	//--------------------------------------------------------------------------

	public static void setFractionalFontMetrics(boolean value)
	{
		 preferences.putBoolean(fractionalFontMetrics, value);
	}
	
	//--------------------------------------------------------------------------

	public static int getEditorFontSize()
	{
		return preferences.getInt(editorFontSize, 12);
	}
	
	//--------------------------------------------------------------------------

	public static void setEditorFontSize(int size)
	{
		 preferences.putInt(editorFontSize, size);
	}

	//--------------------------------------------------------------------------

	public static void setLargeUI(boolean large)
	{
		 preferences.putBoolean(largeUI, large);
	}
	
	//--------------------------------------------------------------------------

	public static boolean getLargeUI()
	{
		 return preferences.getBoolean(largeUI, false);
	}
	
	//--------------------------------------------------------------------------

	public static String getRecentFile(int index)
	{
		return preferences.get(recentFile + " " + index, "");
	}
	
	//--------------------------------------------------------------------------

	public static void setRecentFile(int index, String value)
	{
		 preferences.put(recentFile + " " + index, value);
	}
	
	//--------------------------------------------------------------------------

	public static void removeRecentFile(int index)
	{
		 preferences.remove(recentFile + " " + index);
	}
	
	//--------------------------------------------------------------------------

	public static String getUiFontSize()
	{
		return getLargeUI() ? "16" : "13";
	}

	//--------------------------------------------------------------------------
	
	public static String getSmallUiFontSize()
	{
		return getLargeUI() ? "14" : "10";
	}
	
	//--------------------------------------------------------------------------

	public static int getNodeFontSize()
	{
		return getLargeUI() ? 13 : 11;
	}

	//--------------------------------------------------------------------------

	public static int getNodePortFontSize()
	{
		return getLargeUI() ? 12 : 10;
	}

	//--------------------------------------------------------------------------
	
	public static int getNodeOutsideFontSize()
	{
		return getLargeUI() ? 10 : 8;
	}
	
	//--------------------------------------------------------------------------
}
