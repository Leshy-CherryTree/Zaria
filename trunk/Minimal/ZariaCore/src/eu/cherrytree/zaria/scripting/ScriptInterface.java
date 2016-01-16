/****************************************/
/* ScriptInterface.java					*/
/* Created on: 03-Jun-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting;

import eu.cherrytree.zaria.console.Console;
import eu.cherrytree.zaria.debug.DebugManager;
import eu.cherrytree.zaria.utilities.Random;

import eu.cherrytree.zaria.scripting.annotations.ScriptFunction;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptInterface
{
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "General",
		description = "Prints out standard text in the in game console.",
		parameters = {"text", "Text to print out."}
	)
	public static void consoleOut(String text)
	{
		Console.printOut(text);
	}
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "General",
		description = "Prints out error text in the in game console.",
		parameters = {"text", "Text to print out."}
	)
	public static void consoleErr(String text)
	{
		Console.printErr(text);
	}
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Random",
		description = "Generates a pseudorandom integer.",
		returnValue = "Pseudorandom integer.",
		parameters = {"range", "Range in which the value will be generated."}
	)
	public static int random(int range)
	{
		return Random.getInteger() % range;
	}
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Random",
		description = "Generates a pseudorandom float.",
		returnValue = "Pseudorandom float.",
		parameters = {}
	)
	public static double randomFloat()
	{
		return Random.getFloat();
	}
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Random",
		description = "Generates a pseudorandom integer value based on a dice roll.",
		returnValue = "Pseudorandom integer.",
		parameters =
		{
			"amount", "The amount of throws to generate.",
			"sides", "How many sides should the thrown dice have."
		}
	)
	public static int dice(int amount, int sides)			
	{
		return Random.rollDice(amount, sides);
	}
	
	//--------------------------------------------------------------------------
			
	@ScriptFunction
	(
		category = "Random",
		description = "Generates a pseudorandom, Gaussian distributed float value based on given standard deviation and mean value (peak).",
		returnValue = "Pseudorandom, uniformly distributed float value.",
		parameters =
		{
			"deviation", "Standard deviation.",
			"peak", "Mean value."
		}
	)
	public static double guassian(double deviation, double peak)
	{
		return Random.getGuassian((float) deviation, (float) peak);
	}
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Debug",
		description = "Prints out text in the debug console.",
		parameters = {"text", "Text to print out."}
	)
	public static void trace(String text)
	{
		DebugManager.trace(text);
	}
	
	//--------------------------------------------------------------------------
	
	@ScriptFunction
	(
		category = "Debug",
		description = "Shows an alert.",
		parameters = {"text", "Text to to be shown in the alert."}
	)
	public static void alert(String text)
	{
		DebugManager.alert("Script alert", text);
	}
	
	//--------------------------------------------------------------------------
}
