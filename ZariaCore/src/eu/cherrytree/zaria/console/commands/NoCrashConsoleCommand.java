/****************************************/
/* NoCrashConsoleCommand.java           */
/* Created on: 06-Apr-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.console.commands;

import eu.cherrytree.zaria.console.Console;
import eu.cherrytree.zaria.console.ConsoleProcedure;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class NoCrashConsoleCommand implements ConsoleProcedure
{
	//--------------------------------------------------------------------------

	@Override
	public String getName()
	{
		return "crash";
	}

	//--------------------------------------------------------------------------
	
	@Override
	public String getHelp()
	{
		return "In debug mode throws a RuntimeException.";
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isSecret()
	{
		return true;
	}

	//--------------------------------------------------------------------------

	@Override
	public void run()
	{
		Console.printOut("\"My ship don't crash. If she crashes, you crashed her.\"");	
	}

	//--------------------------------------------------------------------------
}