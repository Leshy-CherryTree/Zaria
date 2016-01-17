/****************************************/
/* ZariaApplication.java                */
/* Created on: Feb 2, 2012				*/
/* Copyright Cherry Tree Studio 2012		*/
/* Released under EUPL v1.1				*/
/* Ported from the jPAJ project.			*/
/****************************************/

package eu.cherrytree.zaria.base;

import eu.cherrytree.zaria.console.Console;
import eu.cherrytree.zaria.console.commands.CrashConsoleCommand;
import eu.cherrytree.zaria.console.commands.ExitAppConsoleCommand;
import eu.cherrytree.zaria.console.commands.GetApplicationPropertyConsoleCommand;
import eu.cherrytree.zaria.console.commands.GetEnvironmentVariableConsoleCommand;
import eu.cherrytree.zaria.console.commands.GetSystemPropertyConsoleCommand;
import eu.cherrytree.zaria.console.commands.NoCrashConsoleCommand;
import eu.cherrytree.zaria.console.commands.ShowMemoryConsoleCommand;
import eu.cherrytree.zaria.debug.DebugManager;
import eu.cherrytree.zaria.debug.DebugUI;
import eu.cherrytree.zaria.game.GameObject;
import eu.cherrytree.zaria.scripting.ScriptEngine;
import eu.cherrytree.zaria.serialization.Capsule;
import eu.cherrytree.zaria.serialization.LoadCapsule;
import eu.cherrytree.zaria.serialization.SaveCapsule;
import eu.cherrytree.zaria.serialization.ValueAlreadySetException;
import eu.cherrytree.zaria.utilities.Random;
import java.io.File;
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
	protected Console console;
	protected ErrorUI errorUI;

	private long mainThreadId;
	
    //--------------------------------------------------------------------------

	public ZariaApplication(String[] args, States initialState, ApplicationStateParams initialParams, Console console, ErrorUI errorUI, DebugUI debugUI)
	{				
		try
		{				
			assert initialState != null;
			assert initialParams != null;
			assert console != null;
			assert errorUI != null;
			
			this.stateMachine = new StateMachine<>(initialState, initialParams);													
			this.arguments = new Arguments(args);
			this.console = console;			
			this.errorUI = errorUI;
			
			// Setting the global application instance.
			ApplicationInstance.setInstance(this);

			// Loading global properties.
			ApplicationProperties.load();
			
			// Initializing debug ui.
			if (DebugManager.isActive() && debugUI != null)
			{
				DebugManager.init(debugUI);

				if (!arguments.argumentString.isEmpty())
					DebugManager.trace("Arguments: " + arguments.argumentString);
			}
			else
			{
				Logger.getLogger("").setLevel(Level.OFF);
			}						

			// Initializing random number generator.
			Random.init(System.currentTimeMillis());

			// Gathering system properties.
			SystemProperties.initOS();
			
			// Creating application save path.
			File save = new File(getSavePath());
			save.mkdirs();
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
			// Initialzing the script engine.
			ScriptEngine.init();
			
			// Seting global console instance.
			Console.setInstance(console);
			
			// Gettin ID of the main thread.
			mainThreadId = Thread.currentThread().getId();						

			float[] o_col = ApplicationProperties.getConsoleFontOutColor();
			float[] e_col = ApplicationProperties.getConsoleFontErrColor();
			float[] b_col = ApplicationProperties.getConsoleBackgroundColor();

			// Initialzing console.
			console.init(	o_col[0], o_col[1], o_col[2],
							e_col[0], e_col[1], e_col[2],
							b_col[0], b_col[1], b_col[2], b_col[3],
							ApplicationProperties.getConsoleFontSize(), ApplicationProperties.getConsoleFontPath(),
							getScreenWidth(), getScreenHeight(), 0.45f,
							ApplicationProperties.getConsoleOpenSpeed());
			
			console.setFlush(arguments.flushConsole);
			console.setEnabled(ApplicationProperties.isConsoleDefaultEnabled() || arguments.enableConsole);
			
			// Init callback.
			onInit();
									
			Runtime runtime = Runtime.getRuntime();
			
			// Adding standard console commands.
			console.addConsoleCommand(new ExitAppConsoleCommand());
			console.addConsoleCommand(new GetApplicationPropertyConsoleCommand());
			console.addConsoleCommand(new GetEnvironmentVariableConsoleCommand());
			console.addConsoleCommand(new GetSystemPropertyConsoleCommand());
			console.addConsoleCommand(new ShowMemoryConsoleCommand());
			
			if (DebugManager.isActive())
				console.addConsoleCommand(new CrashConsoleCommand());
			else
				console.addConsoleCommand(new NoCrashConsoleCommand());
			
			// Game initialized notify user.
			Console.printOut("Memory: " + runtime.freeMemory() + " / " + runtime.totalMemory());								
			Console.printOut(ApplicationProperties.getApplicationFullName() + " intiated");
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

	public final String getFullApplicationName()
	{
		return ApplicationProperties.getApplicationFullName();
	}

	//--------------------------------------------------------------------------

	public final String getShortApplicationName()
	{
		return ApplicationProperties.getApplicationShortName();
	}

	//--------------------------------------------------------------------------

	public final String getDeveloperFullName()
	{
		return ApplicationProperties.getDeveloperFullName();
	}

	//--------------------------------------------------------------------------

	public final String getDeveloperShortName()
	{
		return ApplicationProperties.getDeveloperShortName();
	}

	//--------------------------------------------------------------------------

	public final String getVersion()
	{
		return ApplicationProperties.getMajorVersionNumber() + "." + ApplicationProperties.getMinorVersionNumber() + "." + ApplicationProperties.getRevisionNumber();
	}

	//--------------------------------------------------------------------------
	
	public final String getSavePath()
	{
		return SystemProperties.getUserHome() + "/." + ApplicationProperties.getApplicationShortName();
	}

	//--------------------------------------------------------------------------

	protected void run()
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
		if (DebugManager.isActive())
		{
			DebugManager.trace(throwable);			
			DebugManager.trace(message);
		}
		else
		{
			System.err.println(message + "\n");
			System.err.println(DebugManager.getThrowableText("", throwable));
		}
		
		errorUI.showDialog("Application error!", "Application has encountered a fatal error and will have to shutdown.\n" + throwable.toString());				

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

		update(deltatime);		
		
		console.update(deltatime);

		render(deltatime);
		
		console.render(deltatime);
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
	
	protected abstract <Type> Type loadAsset(String path, Class<Type> type);
    
	//--------------------------------------------------------------------------
}
