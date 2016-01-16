/************************************************/
/* GetApplicationPropertyConsoleCommand.java    */
/* Created on: 18-Apr-2013						*/
/* Copyright Cherry Tree Studio 2013				*/
/* Released under EUPL v1.1						*/
/************************************************/

package eu.cherrytree.zaria.console.commands;

import eu.cherrytree.zaria.base.ApplicationProperties;
import eu.cherrytree.zaria.console.Console;
import eu.cherrytree.zaria.console.ConsoleFunction;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class GetApplicationPropertyConsoleCommand implements ConsoleFunction
{
	//--------------------------------------------------------------------------
	
	@Override
	public String getName()
	{
		return "getappproperty";
	}

	//--------------------------------------------------------------------------
	
	@Override
	public String getHelp()
	{
		return "Returns the value of the input application property.";
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isSecret()
	{
		return true;
	}

	//--------------------------------------------------------------------------

	@Override
	public void run(String[] parameters)
	{
		for(String propertyname : parameters)
		{
			String propertyvalue = ApplicationProperties.getProperty(propertyname);

			if(propertyvalue == null)
				Console.printErr("No such application property \"" + propertyname + "\".");
			else
				Console.printOut(propertyname + ": " + propertyvalue);
		}
	}

	//--------------------------------------------------------------------------
}