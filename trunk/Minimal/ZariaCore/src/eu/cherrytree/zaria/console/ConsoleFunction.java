/****************************************/
/* ConsoleFunction.java					*/
/* Created on: 06-Apr-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.console;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface ConsoleFunction extends ConsoleCommand
{
	//--------------------------------------------------------------------------
	
	public void run(String [] parameters);
	
	//--------------------------------------------------------------------------
}
