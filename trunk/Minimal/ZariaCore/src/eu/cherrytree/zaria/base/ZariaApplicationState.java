/****************************************/
/* ZariaApplicationState.java			*/
/* Created on: 23-01-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.base;

import eu.cherrytree.zaria.console.CantAssignConsoleCommand;
import eu.cherrytree.zaria.console.Console;
import eu.cherrytree.zaria.console.ConsoleCommand;
import eu.cherrytree.zaria.debug.DebugManager;
import eu.cherrytree.zaria.serialization.Capsule;
import eu.cherrytree.zaria.serialization.LoadCapsule;
import eu.cherrytree.zaria.serialization.SaveCapsule;
import eu.cherrytree.zaria.serialization.ValueAlreadySetException;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinitionLibrary;
import eu.cherrytree.zaria.serialization.ZoneDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class ZariaApplicationState
{
	//--------------------------------------------------------------------------
	
	private enum InnerState
	{
		LOADING_REQUESTED,
		LOADING,
		RUNNING,
		SAVING_REQUESTED,
		SAVING;
	}
	
	//--------------------------------------------------------------------------
	
	private InnerState innerState = InnerState.LOADING_REQUESTED;
	
	private Future<Boolean> loadSaveFuture;
	
	private ZariaApplication application;
	private ApplicationStateParams stateParams;
	private ZariaObjectDefinitionLibrary objectlibrary;
	private Console console;

	private ArrayList<ConsoleCommand> commands = new ArrayList<>();
	
	//--------------------------------------------------------------------------
	
	final void init(ZariaApplication application, Console console, ApplicationStateParams stateParams)
	{
		this.stateParams = stateParams;
		this.application = application;
		this.console = console;
		
		onInit();
	}
			
	//--------------------------------------------------------------------------
	
	protected final void addConsoleCommand(ConsoleCommand command) throws CantAssignConsoleCommand
	{
		console.addConsoleCommand(command);
		commands.add(command);
	}
	
	//--------------------------------------------------------------------------
	
	private class LoadingCallable implements Callable<Boolean>
	{
		private String saveFilePath;

		public LoadingCallable(String saveFilePath)
		{
			this.saveFilePath = saveFilePath;
		}
						
		@Override
		public Boolean call() throws Exception
		{						
			DebugManager.trace("Started loading Application State " + ZariaApplicationState.this.getClass().getSimpleName() + " from " + saveFilePath, DebugManager.TraceLevel.INFO);
		
			long time = System.currentTimeMillis();
			
			if (objectlibrary != null)
				objectlibrary.preLoad(null);
			
			LoadCapsule capsule = application.loadCapsule(saveFilePath);
			load(capsule);
						
			time = System.currentTimeMillis() - time;
					
			DebugManager.trace("Finished loading Application State " + 
					ZariaApplicationState.this.getClass().getSimpleName() + " after " + String.format("%.2f", time / 1000.0f) + " seconds.", DebugManager.TraceLevel.INFO);
			
			return true;
		}	
	}
		
	//--------------------------------------------------------------------------
	
	private class SavingCallable implements Callable<Boolean>
	{
		private String saveFilePath;
		private SaveCapsule capsule;

		public SavingCallable(String saveFilePath, SaveCapsule capsule)
		{
			this.saveFilePath = saveFilePath;
			this.capsule = capsule;
		}
						
		@Override
		public Boolean call() throws Exception
		{						
			DebugManager.trace("Started saving Application State " + ZariaApplicationState.this.getClass().getSimpleName() + " to " + saveFilePath, DebugManager.TraceLevel.INFO);
		
			long time = System.currentTimeMillis();
					
			application.saveCapsule(capsule, saveFilePath);
			
			time = System.currentTimeMillis() - time;
					
			DebugManager.trace("Finished saving Application State " + 
					ZariaApplicationState.this.getClass().getSimpleName() + " after " + String.format("%.2f", time / 1000.0f) + " seconds.", DebugManager.TraceLevel.INFO);
			
			return true;
		}	
	}
	
	//--------------------------------------------------------------------------
	
	final void updateState(float deltaTime) throws IOException
	{
		switch(innerState)
		{
			case LOADING_REQUESTED:
				
				assert loadSaveFuture == null;
				
				if (stateParams.getSaveFilePath() != null)
					Console.printOut("Started loading " + stateParams.getSaveFilePath());
				
				if (objectlibrary != null)
					freeLibrary();
				
				if (stateParams.getLibraryPath() != null)
					objectlibrary = ZoneDeserializer.loadLibrary(stateParams.getLibraryPath());
				
				onLoadingStarted();
				
				loadSaveFuture = ApplicationInstance.getThreadPoolExecutor().submit(new LoadingCallable(stateParams.getSaveFilePath()));	
				
				innerState = InnerState.LOADING;
				
				break;
				
			case LOADING:
			
				if (loadSaveFuture != null)
				{
					if (loadSaveFuture.isDone())
					{
						try
						{
							if (loadSaveFuture.get())
							{
								onDataLoaded();

								loadSaveFuture = null;
							}
							else
							{
								throw new ApplicationRuntimeError("Loading failed.");
							}
						}
						catch (InterruptedException | ExecutionException ex)
						{
							throw new ApplicationRuntimeError("Loading failed.", ex);
						}	
					}
					else
					{
						updateLoading(deltaTime);
					}
				}
				else
				{
					boolean loading_done = updateLoading(deltaTime);
					
					if (loading_done)
					{
						Console.printOut("Game loaded.");

						innerState = InnerState.RUNNING;

						onLoadingFinished();
					}
				}
			
				break;
				
			case SAVING_REQUESTED:
			{
				assert loadSaveFuture == null;
				
				Console.printOut("Started saving.");

				onSavingStarted();

				SaveCapsule capsule = new Capsule();
				
				try
				{
					save(capsule);
					
					loadSaveFuture = ApplicationInstance.getThreadPoolExecutor().submit(new SavingCallable(stateParams.getSaveFilePath(), capsule));	
					innerState = InnerState.SAVING;
				}
				catch (ValueAlreadySetException ex)
				{
					// The user should also be notifed that saving has failed.
					DebugManager.alert("Saving failed", DebugManager.getThrowableText("", ex));	
					
					innerState = InnerState.RUNNING;
				}									
				
				update(deltaTime);
			}
				break;

			case SAVING:

				if (loadSaveFuture.isDone())
				{
					try
					{
						if (loadSaveFuture.get())
						{
							Console.printOut("Game saved to " + stateParams.getSaveFilePath());

							innerState = InnerState.RUNNING;
							
							onSavingFinished();
							
							loadSaveFuture = null;
						}
						else
						{
							throw new ApplicationRuntimeError("Saving failed.");
						}
					}
					catch (InterruptedException | ExecutionException ex)
					{
						throw new ApplicationRuntimeError("Saving failed.", ex);
					}
				}
				
				update(deltaTime);

				break;
				
			case RUNNING:
				update(deltaTime);
				break;
		}
	}
	
	//--------------------------------------------------------------------------
	
	boolean isSaveLoadDone()
	{
		return innerState == InnerState.RUNNING;
	}
			
	//--------------------------------------------------------------------------
	
	void initLoad(String saveFilePath)
	{
		if (canLoad())
		{
			assert innerState == InnerState.RUNNING;

			stateParams.setSaveFilePath(saveFilePath);
			innerState = InnerState.LOADING_REQUESTED;
		}		
	}
	
	//--------------------------------------------------------------------------
	
	void initSave(String saveFilePath)
	{
		if (canSave())
		{
			assert innerState == InnerState.RUNNING;

			stateParams.setSaveFilePath(saveFilePath);
			innerState = InnerState.SAVING_REQUESTED;
		}		
	}
	
	//--------------------------------------------------------------------------
	
	final void renderState(float deltaTime)
	{
		switch(innerState)
		{
			case LOADING_REQUESTED:
				// Intentionally empty.
				break;
				
			case LOADING:
				renderLoading(deltaTime);
				break;
				
			default:
				render(deltaTime);
				break;
		}
	}
	
	//--------------------------------------------------------------------------
	
	final void freeLibrary()
	{
		if (stateParams.getLibraryPath() != null)
		{
			assert objectlibrary != null;
		
			objectlibrary = null;
		}
	}

	//--------------------------------------------------------------------------

	public ZariaObjectDefinitionLibrary getObjectlibrary()
	{
		return objectlibrary;
	}
	
	//--------------------------------------------------------------------------
	

	public void onSavingStarted()
	{
		// Default empty.
	}
	
	//--------------------------------------------------------------------------
	
	public void onSavingFinished()
	{
		// Default empty.
	}
	
	//--------------------------------------------------------------------------
	
	public void onDataLoaded()
	{
		// Default empty.
	}
	
	//--------------------------------------------------------------------------
	
	public boolean canLoad()
	{
		return true;
	}
	
	//--------------------------------------------------------------------------	
	
	public boolean canSave()
	{
		return true;
	}
	
	//--------------------------------------------------------------------------	
	
	public void onPause()
	{
		// Default empty.
	}
	
	//--------------------------------------------------------------------------	
	
	public void onResume()
	{
		// Default empty.
	}
	
	//--------------------------------------------------------------------------	
	
	public void destroy()
	{
		for (ConsoleCommand command : commands)
			console.removeConsoleCommand(command);
	}
	
	//--------------------------------------------------------------------------	
		
	public abstract boolean updateLoading(float deltaTime);
	public abstract void renderLoading(float deltaTime);
	
	public abstract void onInit();
	public abstract void onLoadingFinished();
	public abstract void onLoadingStarted();
	
	public abstract void update(float deltaTime);
	public abstract void render(float deltaTime);		
	
	public abstract void load(LoadCapsule capsule);
	public abstract void save(SaveCapsule capsule) throws ValueAlreadySetException;		
		
	
	//--------------------------------------------------------------------------	
}
