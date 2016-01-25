/****************************************/
/* CantAssignConsoleCommand.java        */
/* Created on: 06-Apr-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.console;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class CantAssignConsoleCommand extends Exception
{
	//--------------------------------------------------------------------------
	
    public CantAssignConsoleCommand(ConsoleCommand function)
	{
		this(function,"");
    }
	
	//--------------------------------------------------------------------------

    public CantAssignConsoleCommand(ConsoleCommand function, String msg)
	{
        super("Can't assign Console command " + function.getName() + "." + msg);
    }
	
	//--------------------------------------------------------------------------
}
