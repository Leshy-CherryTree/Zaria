/****************************************/
/* ZariaApplication.java                */
/* Created on: Feb 2, 2012				*/
/* Copyright Cherry Tree Studio 2012		*/
/* Released under EUPL v1.1				*/
/* Ported from the jPAJ project.			*/
/****************************************/

package eu.cherrytree.zaria.base;

import eu.cherrytree.zaria.console.Console;
import eu.cherrytree.zaria.debug.DebugManager;
import eu.cherrytree.zaria.debug.DebugUI;
import eu.cherrytree.zaria.game.GameObject;
import eu.cherrytree.zaria.scripting.ScriptEngine;
import eu.cherrytree.zaria.serialization.Capsule;
import eu.cherrytree.zaria.serialization.LoadCapsule;
import eu.cherrytree.zaria.serialization.SaveCapsule;
import eu.cherrytree.zaria.serialization.ValueAlreadySetException;
import eu.cherrytree.zaria.utilities.Random;
import java.io.IOException;

import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class ZariaApplication<States extends Enum>
{
    //--------------------------------------------------------------------------

    private static class Arguments
    {
        public boolean fullscreen;
        public boolean windowed;
        public boolean resetSettings;
        public boolean noSound;
        public boolean flushConsole;
        public boolean enableConsole;

        public String argumentString;

        Arguments()
        {
            fullscreen = false;
            windowed = false;
            resetSettings = false;
            noSound = false;
            flushConsole = false;
            enableConsole = false;
        }

		Arguments(String[] args)
        {
            this();

            argumentString = "";

            processArguments(args);
        }

        private void processArguments(String[] args)
        {
			// Processing arguments.
			for (String arg : args)
			{
				argumentString += arg;
				switch (arg)
				{
					case "-force_fullscreen":
						fullscreen = true;
						break;
					case "-force_windowed":
						windowed = true;
						break;
					case "-reset_settings":
						resetSettings = true;
						break;
					case "-no_sound":
						noSound = true;
						break;
					case "-flush_console":
						flushConsole = true;
						break;
					case "-enable_console":
						enableConsole = true;
						break;
				}
			}
			
			// Validating arguments.
			if (fullscreen && windowed)
			{
				System.err.println("Can't force use [-force_fullscreen] and [-force_windowed] at the same time. Disabling both.");
				fullscreen = false;
				windowed = false;				
			}
        }
    }
	
	//--------------------------------------------------------------------------
	
	private StateMachine<States, ApplicationStateParams> stateMachine;
	
    private Arguments arguments;
	private Console console;

	private long mainThreadId;
	
    //--------------------------------------------------------------------------

	public ZariaApplication(String[] args, States initialState, ApplicationStateParams initialParams, DebugUI debugUI)
	{
		try
		{				
			stateMachine = new StateMachine<>(initialState, initialParams);							

			arguments = new Arguments(args);
			
			ApplicationProperties.load();

			if (DebugManager.isActive())
			{
				DebugManager.init(debugUI);

				if (!arguments.argumentString.isEmpty())
					DebugManager.trace("Arguments: " + arguments.argumentString);
			}
			else
			{
				Logger.getLogger("").setLevel(Level.OFF);
			}

			Random.init(System.currentTimeMillis());

			SystemProperties.initOS();
		}
		catch(ApplicationProperties.ApplicationPropertiesNotFoundException e)
		{
			handleFatalError(e, "Couldn't load appliacation properties. Initialization failed. Exiting.");
			
			System.exit(-1);
		}
		catch(Throwable e)
		{
			handleFatalError(e, "Coulnd't create the application. Exiting.");
			
			System.exit(-1);
		}
	}

	//--------------------------------------------------------------------------
	
	protected final boolean getArgumentFlag(String[] args, String flag)
	{
		flag = "-" + flag;
		
		for (String arg : args)
		{
			if (arg.toLowerCase().equals(flag.toLowerCase()))
				return true;
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------
	
	protected final String[] getArgumentParameters(String[] args, String name)
	{
		ArrayList<String> parameters = new ArrayList<>();
		
		name = "-" + name + ":";
		
		for (String arg : args)
		{
			if (arg.toLowerCase().startsWith(name.toLowerCase()))
				parameters.add(arg.substring(name.length()));
		}

		return parameters.toArray(new String[parameters.size()]);
	}

	//--------------------------------------------------------------------------

	public final void initialize()
	{
		try
		{
			ScriptEngine.init();
			Console.setInstance(console);
			
			mainThreadId = Thread.currentThread().getId();
			
			ApplicationInstance.setInstance(this, getScreenWidth(), getScreenHeight());

			float[] o_col = ApplicationProperties.getConsoleFontOutColor();
			float[] e_col = ApplicationProperties.getConsoleFontErrColor();
			float[] b_col = ApplicationProperties.getConsoleBackgroundColor();

			console.init(	o_col[0], o_col[1], o_col[2],
							e_col[0], e_col[1], e_col[2],
							b_col[0], b_col[1], b_col[2], b_col[3],
							ApplicationProperties.getConsoleFontSize(), ApplicationProperties.getConsoleFontPath(),
							getScreenWidth(), getScreenHeight(), 0.45f,
							ApplicationProperties.getConsoleOpenSpeed());

			console.setFlush(arguments.flushConsole);
			console.setEnabled(ApplicationProperties.isConsoleDefaultEnabled() || arguments.enableConsole);
			
			onInit();
						
			Runtime runtime = Runtime.getRuntime();

			DebugManager.trace("Memory: " + runtime.freeMemory() + " / " + runtime.totalMemory());								
			DebugManager.trace("Game intiated");
		}
		catch(Throwable e)
		{
			handleFatalError(e, "Coulnd't initialize the application. Exiting.");
			
			System.exit(-1);
		}
	}

	//--------------------------------------------------------------------------

	protected String getSystemInformation()
	{
		String infoString = "";

		infoString += "Application Info: \n";
		infoString += ApplicationProperties.getString();

		infoString += "System Info: \n";
		infoString += SystemProperties.getAnynomousString();

		infoString += "\n";

		return infoString;
	}

	//--------------------------------------------------------------------------

	public String getFullApplicationName()
	{
		return ApplicationProperties.getApplicationFullName();
	}

	//--------------------------------------------------------------------------

	public String getShortApplicationName()
	{
		return ApplicationProperties.getApplicationShortName();
	}

	//--------------------------------------------------------------------------

	public String getDeveloperFullName()
	{
		return ApplicationProperties.getDeveloperFullName();
	}

	//--------------------------------------------------------------------------

	public String getDeveloperShortName()
	{
		return ApplicationProperties.getDeveloperShortName();
	}

	//--------------------------------------------------------------------------

	public String getVersion()
	{
		return ApplicationProperties.getMajorVersionNumber() + "." + ApplicationProperties.getMinorVersionNumber() + "." + ApplicationProperties.getRevisionNumber();
	}

	//--------------------------------------------------------------------------
	
	public String getSavePath()
	{
		return SystemProperties.getUserHome() + "/." + ApplicationProperties.getApplicationShortName();
	}

	//--------------------------------------------------------------------------

	public void run()
	{
		try
		{
			executeMachineState();
		}
		// Gotta catch'em all ^_^
		catch(Throwable e)
		{
			handleFatalError(e, "Exiting application.");			
			exitApp(-1);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void handleFatalError(Throwable throwable, String message)
	{
		DebugManager.showErrorDialog("Application error!", "Application has encountered a fatal error and will have to shutdown.\n" + throwable.toString());
		
		DebugManager.trace(throwable);			
		DebugManager.trace(message);

		if (DebugManager.isActive())
			DebugManager.dumpLog();
	}
	
	//--------------------------------------------------------------------------
	
	public void load(String fileName)
	{
		try
		{
			LoadCapsule loadCapsule = Capsule.loadCapsule(fileName);
			GameObject.loadCounters(loadCapsule);		
			
			onLoad(loadCapsule);
		}
		catch (IOException | ClassNotFoundException ex)
		{
			handleFatalError(ex, "Loading failed!");
		}
	}
	
	//--------------------------------------------------------------------------	
	
	public void save(String fileName)
	{
		try
		{
			Capsule saveCapsule = new Capsule();
			GameObject.saveCounters(saveCapsule);
			
			onSave(saveCapsule);
			
			Capsule.saveCapsule(fileName, saveCapsule);
		}
		catch (ValueAlreadySetException | IOException ex)
		{
			handleFatalError(ex, "Saving failed!");
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void executeMachineState() throws Throwable
	{
		if (!stateMachine.isStateStarted())
		{
			if (!stateMachine.isStateEnded())
				endMachineState();
			
			startMachineState();						
		}		
		
		runState();
	}

	//--------------------------------------------------------------------------
	
	private void startMachineState() throws Throwable
	{	
		startApplicationState(stateMachine.getState(), stateMachine.getStartParams());

		stateMachine.setStateStarted();
	}

	//--------------------------------------------------------------------------

	private void endMachineState() throws Throwable
	{
		endApplicationState();
		
		//Just in case.
		System.gc();

		stateMachine.setStateEnded();
	}
		
	//--------------------------------------------------------------------------
	
	protected StateMachineInterface<States, ApplicationStateParams> getStateMachine()
	{
		return stateMachine;
	}
	
	//--------------------------------------------------------------------------	

	private void runState()
	{
		float deltatime = getDeltaTime();

		console.update(deltatime);

		update(deltatime);

		console.render(deltatime);

		render(deltatime);
	}
	
	//--------------------------------------------------------------------------

	protected final void exitApp(int status)
	{
		console.destroy();

		onExit();

		if (DebugManager.isActive())
			DebugManager.deinit();
		
		ScriptEngine.destroy();
		
		System.exit(status);
	}

	//--------------------------------------------------------------------------

	public void quit(int status)
	{
		endApplicationState();				
		
		exitApp(status);
	}

	//--------------------------------------------------------------------------

	public long getMainThreadId()
	{
		return mainThreadId;
	}
	
	//--------------------------------------------------------------------------
		
	protected abstract void update(float deltatime);
	protected abstract void render(float deltatime);
	
	protected abstract float getDeltaTime();
	
	protected abstract float getScreenWidth();
	protected abstract float getScreenHeight();
	
	protected abstract void startApplicationState(States state, ApplicationStateParams startParams);
	protected abstract void endApplicationState();
	
	protected abstract void onLoad(LoadCapsule capsule);
	protected abstract void onSave(SaveCapsule capsule);
	
    protected abstract void onInit();
    protected abstract void onExit();
	
	protected abstract Object loadAsset(String path);
    
	//--------------------------------------------------------------------------
}
