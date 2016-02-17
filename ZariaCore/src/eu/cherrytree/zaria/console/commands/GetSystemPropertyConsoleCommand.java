/********************************************/
/* GetSystemPropertyConsoleCommand.java		*/
/* Created on: 06-Apr-2013					*/
/* Copyright Cherry Tree Studio 2013			*/
/* Released under EUPL v1.1					*/
/********************************************/

package eu.cherrytree.zaria.console.commands;

import eu.cherrytree.zaria.console.Console;
import eu.cherrytree.zaria.console.ConsoleFunction;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class GetSystemPropertyConsoleCommand implements ConsoleFunction
{
	//--------------------------------------------------------------------------
	
	@Override
	public String getName()
	{
		return "getproperty";
	}

	//--------------------------------------------------------------------------
	
	@Override
	public String getHelp()
	{
		return "Returns the value of the input system property.";
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
		for(String propertyname : parameters)
		{
			String propertyvalue = System.getProperty(propertyname);

			if(propertyvalue == null)
				Console.printErr("No such system property \"" + propertyname + "\".");
			else
				Console.printOut(propertyname + ": " + propertyvalue);
		}
	}

	//--------------------------------------------------------------------------
}
