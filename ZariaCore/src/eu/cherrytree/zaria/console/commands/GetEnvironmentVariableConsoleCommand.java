/************************************************/
/* GetEnvironmentVariableConsoleCommand.java   */
/* Created on: 06-Apr-2013						*/
/* Copyright Cherry Tree Studio 2013			*/
/* Released under EUPL v1.1						*/
/************************************************/

package eu.cherrytree.zaria.console.commands;

import eu.cherrytree.zaria.console.Console;
import eu.cherrytree.zaria.console.ConsoleFunction;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class GetEnvironmentVariableConsoleCommand implements ConsoleFunction
{
	//--------------------------------------------------------------------------
	
	@Override
	public String getName()
	{
		return "getenv";
	}

	//--------------------------------------------------------------------------
	
	@Override
	public String getHelp()
	{
		return "Returns the value of the input environment variable.";
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isSecret()
	{
		return false;
	}

	//--------------------------------------------------------------------------

	@Override
	public void run(String[] parameters)
	{
		for(String variablename : parameters)
		{
			String variablevalue = System.getenv(variablename);

			if(variablevalue == null)
				Console.printErr("No such environment variable a\"" + variablename + "\".");
			else
				Console.printOut(variablename + ": " + variablevalue);
		}
	}

	//--------------------------------------------------------------------------
}