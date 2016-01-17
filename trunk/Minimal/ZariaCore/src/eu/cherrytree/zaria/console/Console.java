/****************************************/
/* Console.java							*/
/* Created on: 10-01-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.console;

import eu.cherrytree.zaria.debug.DebugManager;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class Console
{
	//--------------------------------------------------------------------------
	
	private static Console instance;
	
	//--------------------------------------------------------------------------
	
	private HashMap<String,ConsoleProcedure> consoleProcedures = new HashMap<>();
	private HashMap<String,ConsoleFunction> consoleFunctions = new HashMap<>();
	
	protected ArrayList<String> commandHistory = new ArrayList<>();
	
	//--------------------------------------------------------------------------
	
	public abstract void init(float oR, float oG, float oB, float eR, float eG, float eB, float bR, float bG, float bB, float bA, float fontSize, String font, float screenWidth, float screenHeight, float screenCover, float toggleSpeed);
	
	public abstract void setFlush(boolean flushConsole);
	public abstract void setEnabled(boolean b);
	
	public abstract void destroy();
	public abstract void update(float deltatime);
	public abstract void render(float deltatime);
	
	public abstract void printToStd(String str);
	public abstract void printToErr(String str);
	
	public abstract void toggle();
	public abstract boolean isConsoleOpen();
	
	//--------------------------------------------------------------------------
	
	public final void addConsoleCommand(ConsoleCommand command) throws CantAssignConsoleCommand
	{
		if (command instanceof ConsoleProcedure)
		{
			if(consoleProcedures.containsKey(command.getName()))
				throw new CantAssignConsoleCommand(command, "Procedure already added to ApplicationState.");
		
			consoleProcedures.put(command.getName(), (ConsoleProcedure) command);
		}
		else if (command instanceof ConsoleFunction)
		{
			if (consoleFunctions.containsKey(command.getName()))
				throw new CantAssignConsoleCommand(command, "Function already added to ApplicationState.");
		
			consoleFunctions.put(command.getName(), (ConsoleFunction) command);
		}
		
		DebugManager.trace("Console command " + command.getName() + " added.");
	}		
	
	//--------------------------------------------------------------------------
	
	public final void removeConsoleCommand(ConsoleCommand command)
	{
		if(command instanceof ConsoleProcedure)
		{
			if (consoleProcedures.containsKey(command.getName()))		
				consoleProcedures.remove(command.getName());
		}
		else if(command instanceof ConsoleFunction)
		{
			if (consoleFunctions.containsKey(command.getName()))
				consoleFunctions.remove(command.getName());
		}
		
		DebugManager.trace("Console command " + command.getName() + " added.");
	}
			
	//--------------------------------------------------------------------------
	
	private boolean verifyConsoleFunctionParameters(String parameters)
	{
		char [] test = parameters.toCharArray();
		int counter = 0;

		for (int i = 0 ; i < test.length ; i++)
		{
			if(test[i] == '\"')
				counter++;
		}

		return (counter % 2) == 0;
	}
	
	//--------------------------------------------------------------------------
	
	private String[] extractFunctionParameters(String parameters)
	{
		// If there are no parameters return empty string
		if(parameters.isEmpty())
		{
			return new String[0];
		}
		// If there are strings in the parameters extract them.
		else if(parameters.contains("\""))
		{
			// Parameters left of the quotation mark.
			String lparam = parameters.substring(0,parameters.indexOf("\""));
			
			// What is left right of the quotation mark.
			String tmpparam = parameters.substring(parameters.indexOf("\"")+1);			
			
			// The String parameter.
			String strparam = tmpparam.substring(0,tmpparam.indexOf("\""));
			
			// Parameters rihht of the second quotation mark.
			String rparam = tmpparam.substring(tmpparam.indexOf("\"")+1);	
			
			// Recursively extracting left and right paramaters.
			String [] lparams = extractFunctionParameters(lparam);
			String [] rparams = extractFunctionParameters(rparam);
			
			String [] params = new String[lparams.length + 1 + rparams.length];
			
			//Stiching the array according to the original order.
			int index = 0;
						
			for(String str : lparams)
			{
				params[index] = str;
				index++;
			}
			
			params[index] = strparam;
			index++;
			
			for(String str : rparams)
			{
				params[index] = str;
				index++;
			}
			
			// Done.
			return params;
		}
		// If there are no strings in the parameters just split the string using space.
		else
		{
			return parameters.split(" ");			
		}
	}
		
	//--------------------------------------------------------------------------
	
	protected final boolean onConsoleCommand(String command)
	{
		commandHistory.add(command);
		
		if(command.equals("help"))
		{
			for (ConsoleProcedure procedure : consoleProcedures.values())
			{
				if(!procedure.isSecret())
					Console.printOut(procedure.getName());
			}
			
			for (ConsoleFunction function : consoleFunctions.values())
			{
				if(!function.isSecret())
					Console.printOut(function.getName());
			}
			
			return true;
		}
		else if(command.startsWith("help "))
		{
			String name = command.substring(command.indexOf("help ")+5);
		
			if(consoleProcedures.containsKey(name))
				Console.printOut(consoleProcedures.get(name).getHelp());
			else if(consoleFunctions.containsKey(name))
				Console.printOut(consoleFunctions.get(name).getHelp());
			else
				Console.printErr("Unknown command \"" + name + "\".");
			
			return true;
		}
		else if(consoleProcedures.containsKey(command))
		{
			consoleProcedures.get(command).run();
			
			return true;
		}
		else if(command.contains(" "))
		{
			String name = command.substring(0, command.indexOf(" "));
			
			if(consoleFunctions.containsKey(name))
			{			
				String parameters = command.substring(command.indexOf(" ")+1);
				
				if(verifyConsoleFunctionParameters(parameters))
					consoleFunctions.get(name).run(extractFunctionParameters(parameters));
				else
					Console.printErr("Cannot execute function \"" + name + "\" with parameters \"" + parameters + "\".");
				
				return true;
			}						
		}
		else if(consoleFunctions.containsKey(command))
		{
			Console.printErr("Console command \"" + command + "\" needs parameters. Try \"help "+ command +"\" for more info.");
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------
	
	public static void setInstance(Console console)
	{
		assert instance == null;
		
		instance = console;
	}
	
	//--------------------------------------------------------------------------
			
	public static void printOut(String str)
	{
		instance.printToStd(str);
		DebugManager.trace("[CONSOLE OUT]: " + str);
	}
	
	//--------------------------------------------------------------------------
	
	public static void printErr(String str)
	{
		instance.printToErr(str);
		DebugManager.trace("[CONSOLE ERR]: " + str);
	}
	
	//--------------------------------------------------------------------------	
	
	public static boolean isOpen()
	{
		return instance.isConsoleOpen();
	}
	
	//--------------------------------------------------------------------------	
}
