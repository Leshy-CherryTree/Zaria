/****************************************/
/* CrashConsoleCommand.java				*/
/* Created on: 06-Apr-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.console.commands;

import eu.cherrytree.zaria.console.ConsoleProcedure;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class CrashConsoleCommand implements ConsoleProcedure
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
		return "Throws a RuntimeException.";
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
		throw new RuntimeException("Console triggered test exception.");
	}

	//--------------------------------------------------------------------------
}
