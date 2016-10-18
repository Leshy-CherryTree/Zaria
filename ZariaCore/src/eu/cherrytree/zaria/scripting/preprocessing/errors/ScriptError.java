/****************************************/
/* ScriptError.java						*/
/* Created on: 14-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting.preprocessing.errors;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class ScriptError extends Error
{
	//--------------------------------------------------------------------------
	
	public ScriptError(String scriptName)
	{
		super("There was error preprocessing script " + scriptName);
	}
	
	//--------------------------------------------------------------------------

	public ScriptError(String scriptName, Throwable cause)
	{
		super("There was error preprocessing script " + scriptName, cause);
	}
	
	//--------------------------------------------------------------------------
}
