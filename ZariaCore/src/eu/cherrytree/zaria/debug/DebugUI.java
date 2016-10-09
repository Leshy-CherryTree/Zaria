/****************************************/
/* DebugConsoleWindow.java				*/
/* Created on: 10-01-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.debug;

import java.util.logging.Handler;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface DebugUI
{
	public void init();
	public Handler getLogHandler();
	public void deinit();

	public void addText(String string, DebugManager.TraceLevel level);	
	
	public void showWarningDialog(String title, String message);
	public void showErrorDialog(String title, String message, boolean fatal);
	
	public void saveLog(String string);
}
