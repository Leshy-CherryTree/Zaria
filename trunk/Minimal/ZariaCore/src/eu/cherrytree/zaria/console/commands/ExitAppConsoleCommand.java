/****************************************/
/* ExitAppConsoleCommand.java			*/
/* Created on: 08-Apr-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.console.commands;

import eu.cherrytree.zaria.base.ApplicationInstance;
import eu.cherrytree.zaria.console.ConsoleProcedure;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ExitAppConsoleCommand implements ConsoleProcedure
{
	//--------------------------------------------------------------------------
	
	@Override
	public void run()
	{
		ApplicationInstance.quit(0);
	}

	//--------------------------------------------------------------------------

	@Override
	public String getName()
	{		
		return "exit";
	}

	//--------------------------------------------------------------------------

	@Override
	public String getHelp()
	{
		return "Immediately exits the application.";
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isSecret()
	{
		return false;
	}

	//--------------------------------------------------------------------------
}
