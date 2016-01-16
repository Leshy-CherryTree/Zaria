/****************************************/
/* ShowMemoryConsoleCommand.java        */
/* Created on: 06-Apr-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.console.commands;

import eu.cherrytree.zaria.console.Console;
import eu.cherrytree.zaria.console.ConsoleProcedure;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ShowMemoryConsoleCommand implements ConsoleProcedure
{
	//--------------------------------------------------------------------------

	@Override
	public String getName()
	{
		return "show memory";
	}

	//--------------------------------------------------------------------------
	
	@Override
	public String getHelp()
	{
		return "Shows the free and total amounts of memory.";
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isSecret()
	{
		return false;
	}

	//--------------------------------------------------------------------------

	@Override
	public void run()
	{
		Console.printOut("Memory: " + Runtime.getRuntime().freeMemory() + " / " + Runtime.getRuntime().totalMemory());
	}

	//--------------------------------------------------------------------------
}