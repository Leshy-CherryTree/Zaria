/****************************************/
/* DesktopSyncManager.java				*/
/* Created on: 02-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.debug;

import eu.cherrytree.zaria.base.ApplicationRuntimeError;
import eu.cherrytree.zaria.scripting.ScriptEngine;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinitionLibrary;
import eu.cherrytree.zaria.serialization.ZoneDeserializer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DesktopSyncManager implements SyncInterface, EditorSyncManager
{
	//--------------------------------------------------------------------------
	
	private class ScriptSyncInfo
	{
		private String path;
		private String source;

		public ScriptSyncInfo(String path, String source)
		{
			this.path = path;
			this.source = source;
		}

		public String getPath()
		{
			return path;
		}

		public String getSource()
		{
			return source;
		}
	}
	
	//--------------------------------------------------------------------------
	
	private ArrayList<ZariaObjectDefinitionLibrary> libraries = new ArrayList<>();
	private ArrayList<ZariaObjectDefinition> definitionsToSync = new ArrayList<>();
	private ArrayList<ScriptSyncInfo> scriptsToSync = new ArrayList<>();

	//--------------------------------------------------------------------------
	
	@Override
	public void init()
	{
		try
		{	
			String classpath = System.getProperty("java.class.path");			
			String[] paths = classpath.split(File.pathSeparator);
			
			String files = "";
			
			for (String path : paths)
				files += "file:/" + path + " ";
						
			System.setProperty("java.rmi.server.codebase", files.replaceAll("//", "/").trim());
			
			SyncInterface intf = (SyncInterface) UnicastRemoteObject.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();			
			registry.rebind("SyncManager", intf);
			
			DebugManager.trace("Sync Manager started.");
		}
		catch (Exception ex)
		{
			DebugManager.trace(ex);
		}				
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void deinit()
	{
		try
		{						
			Registry registry = LocateRegistry.getRegistry();
			registry.unbind("SyncManager");		
		}
		catch (Exception ex)
		{
			DebugManager.trace(ex);
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private void getAllFields(Class cls, ArrayList<Field> fields)
	{
		int index = 0;
		
		for(Field field : cls.getDeclaredFields())
		{						
			if(!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
			{
				fields.add(index, field);
				index++;
			}
		}
				
		Class superClass = cls.getSuperclass();
		
		if(superClass != null)
			getAllFields(superClass, fields);
	}
	
	//--------------------------------------------------------------------------
	
	private void copy(ZariaObjectDefinition from, ZariaObjectDefinition to)
	{
		assert to.getClass() == from.getClass();
		
		try
		{
			ArrayList<Field> fields = new ArrayList<>();
			getAllFields(to.getClass(), fields);

			for (Field field : fields)
			{
				field.setAccessible(true);

				if (field.getType().isPrimitive())
				{
					if (field.getType() == byte.class)
					{
						field.setByte(to, field.getByte(from));
					}
					else if (field.getType() == short.class)
					{
						field.setShort(to, field.getShort(from));
					}
					else if (field.getType() == int.class)
					{
						field.setInt(to, field.getInt(from));
					}
					else if (field.getType() == long.class)
					{
						field.setLong(to, field.getLong(from));
					}
					else if (field.getType() == char.class)
					{
						field.setChar(to, field.getChar(from));
					}
					else if (field.getType() == float.class)
					{
						field.setFloat(to, field.getFloat(from));
					}
					else if (field.getType() == double.class)
					{
						field.setDouble(to, field.getDouble(from));
					}
					else if (field.getType() == boolean.class)
					{
						field.setBoolean(to, field.getBoolean(from));	
					}
				}
				else
				{
					field.set(to, field.get(from));
				}
			}		
		}
		catch(SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			throw new ApplicationRuntimeError("Couldn't sync " + to.getID(), ex);
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void update()
	{
		if (!definitionsToSync.isEmpty())
		{
			DebugManager.trace("Syncing " + definitionsToSync.size() + " objects.");
			
			HashMap<UUID, ZariaObjectDefinitionLibrary> lib_map = new HashMap<>();
			
			for (ZariaObjectDefinition definition : definitionsToSync)
			{
				for (ZariaObjectDefinitionLibrary library : libraries)
				{
					if (library.containsObjectDefinition(definition.getUUID()))
					{
						copy(definition, library.getDefinition(definition.getUUID()));															
						lib_map.put(definition.getUUID(), library);
						break;
					}
					else
					{
						// TODO Add the definition but run the preload together with others.
						//		(To all libraries?)
					}
				}
			}
			
			for (ZariaObjectDefinition definition : definitionsToSync)
			{
				if (lib_map.containsKey(definition.getUUID()))
					definition.preLoad(lib_map.get(definition.getUUID()));
			}
			
			definitionsToSync.clear();
			
			DebugManager.trace("Object sync done.");
		}
		
		if (!scriptsToSync.isEmpty())
		{
			DebugManager.trace("Syncing " + scriptsToSync.size() + " scripts.");
			
			for (ScriptSyncInfo info : scriptsToSync)
			{
				ScriptEngine.updateScripts(info.getPath(), info.getSource());
			}
			
			scriptsToSync.clear();
			
			DebugManager.trace("Script sync done.");
		}
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void syncObjects(String zoneSource) throws RemoteException
	{
		if (libraries.isEmpty())
		{
			DebugManager.trace("Recieved sync info, but no libraries are prepared for sync.", DebugManager.TraceLevel.WARNING);
			return;
		}
		
		try
		{
			ZariaObjectDefinition[] definitions = ZoneDeserializer.parse(zoneSource);
			
			DebugManager.trace("Recieved " + definitions.length + " objects to sync.");
			
			for (ZariaObjectDefinition def : definitions)
			{
				DebugManager.trace("Syncing " + def.getID() + " of type" + def.getClass().getSimpleName());			
				definitionsToSync.add(def);
			}
		}
		catch (IOException ex)
		{
			DebugManager.trace(ex);
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void syncScript(String path, String source) throws RemoteException
	{
		scriptsToSync.add(new ScriptSyncInfo(path, source));
		
		DebugManager.trace("Syncing script " + path);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void addLibrary(ZariaObjectDefinitionLibrary library)
	{
		assert !libraries.contains(library);
		
		DebugManager.trace("Enabled sync for " + library.getID());
		
		libraries.add(library);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void removeLibrary(ZariaObjectDefinitionLibrary library)
	{
		assert libraries.contains(library);
		
		DebugManager.trace("Disabled sync for " + library.getID());
		
		libraries.remove(library);
	}
	
	//--------------------------------------------------------------------------
}
