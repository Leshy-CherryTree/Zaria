/********************************************/
/* ZariaObjectDefinitionLibrary.java		*/
/* Created on: 16-Mar-2013					*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1					*/
/********************************************/

package eu.cherrytree.zaria.serialization;

import eu.cherrytree.zaria.base.ApplicationInstance;
import eu.cherrytree.zaria.debug.DebugManager;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public final class ZariaObjectDefinitionLibrary extends ZariaObjectDefinition
{
	//--------------------------------------------------------------------------
	
	public class CantAddDefinitionException extends Exception	
	{
		public CantAddDefinitionException(ZariaObjectDefinition definition, String message)
		{
			super("Can't add object " + definition.getUUID() + " to library " + getUUID() + "! " + message);
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private String [] zoneFiles = {};
	
	//--------------------------------------------------------------------------
	
	private transient HashMap<UUID,ZariaObjectDefinition> objectDefinitions = new HashMap<>();
	
	//--------------------------------------------------------------------------
	
	public void setLibraryFile(String file)	
	{
		assert getFile() == null;
		
		setFile(file);
	}
	
	//--------------------------------------------------------------------------
			
	public synchronized boolean containsObjectDefinition(UUID uuid)
	{
		return objectDefinitions.containsKey(uuid);
	}
	
	//--------------------------------------------------------------------------
	
	public synchronized ZariaObjectDefinition getDefinition(UUID uuid)
	{
		assert uuid != null;
		
		ZariaObjectDefinition objdef = objectDefinitions.get(uuid);
		
		if(objdef == null)
		{
			DebugManager.alert("Object definition not found", "Couldn't find object definition " + uuid + " in library " + getUUID());
			DebugManager.traceStack(DebugManager.TraceLevel.ERROR);
		}
		
		return objdef;
	}

	//--------------------------------------------------------------------------
	
	// No extends because it is valid to use any interface.
	public synchronized <DefClass> ArrayList<DefClass> getDefinitions(Class<DefClass> cls)
	{
		ArrayList<DefClass> ret = new ArrayList<>();
		
		for(ZariaObjectDefinition definition : objectDefinitions.values())
		{
			if (cls.isAssignableFrom(definition.getClass()))
				ret.add((DefClass) definition);
		}
		
		return ret;
	}
		
	//--------------------------------------------------------------------------
	
	public synchronized <DefClass extends ZariaObjectDefinition> DefClass getDefinitionByID(Class<DefClass> cls, String id)
	{
		// If running debug we want to have info if the object is unique, and if not alert about duplicates.
		if(DebugManager.isActive())
		{
			ArrayList<DefClass> ret = new ArrayList<>();
		
			for(ZariaObjectDefinition definition : objectDefinitions.values())
			{
				if (cls.isAssignableFrom(definition.getClass()) && definition.getID().equals(id))
					ret.add((DefClass) definition);
			}
			
			if(!ret.isEmpty())
			{
				if(ret.size() > 1)
				{
					String list = "";
					
					for(DefClass def : ret)
						list += def.getID() + " of type " + def.getClass().getCanonicalName() + " in " + def.getFile() + "\n";
					
					DebugManager.alert("Duplicate objects found", 
							"Found " + ret.size() + " objects of type " + cls.getCanonicalName() + " with ID " + 
							id + " in library " + getID() + "\n" + list + "[" + getFile() + "]");
				}
				
				return ret.get(0);
			}
		}
		else
		{
			for(ZariaObjectDefinition definition : objectDefinitions.values())
			{
				if (cls.isAssignableFrom(definition.getClass()) && definition.getID().equals(id))
					return (DefClass) definition;
			}
		}
		
		DebugManager.alert("Object not found", "Couldn't find object " + id + " of type " + cls.getCanonicalName() + " in library " + getID() + "\n[" + getFile() + "]");
		
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public synchronized void addDefinition(ZariaObjectDefinition definition) throws CantAddDefinitionException
	{
		if(objectDefinitions.containsKey(definition.getUUID()))
			throw new CantAddDefinitionException(definition, "Object with such ID alrady exists!");
		
		if(objectDefinitions.containsValue(definition))
			throw new CantAddDefinitionException(definition, "Object is already in library!");
		
		addDefinitionToMap(definition, "");
		
		definition.preLoad(this);
	}
			
	//--------------------------------------------------------------------------
	
	public synchronized void addDefinitions(ArrayList<ZariaObjectDefinition> definitions) throws CantAddDefinitionException
	{
		for(ZariaObjectDefinition def : definitions)
		{
			if(objectDefinitions.containsKey(def.getUUID()))
				throw new CantAddDefinitionException(def, "Object with such ID alrady exists!");

			if(objectDefinitions.containsValue(def))
				throw new CantAddDefinitionException(def, "Object is already in library!");

			addDefinitionToMap(def, "");
		}
		
		for(ZariaObjectDefinition def : definitions)
			def.preLoad(this);
	}	
		
	//--------------------------------------------------------------------------
	
	private void addDefinitionToMap(ZariaObjectDefinition definition, String file)
	{
		if(DebugManager.isActive())
			definition.setFile(file);
		
		objectDefinitions.put(definition.getUUID(), definition);
	}
	
	//--------------------------------------------------------------------------
	
	private ValidationException findValidationException(Throwable throwable)
	{
		if(ValidationException.class.isAssignableFrom(throwable.getClass()))
			return (ValidationException) throwable;
		else if(throwable.getCause() != null)
			return findValidationException(throwable.getCause());
		else
			return null;			
	}
	
	//--------------------------------------------------------------------------
	
	private String addDefinitionsFromFile(String file, ArrayList<DefinitionValidation> validations)
	{
		DebugManager.trace("Loading " + file + " into library " + getUUID());		
		
		ZariaObjectDefinition[] objects;
		
		try
		{
			objects = ZoneDeserializer.loadDefinitions(file);
		}
		catch(IOException ex)
		{
			DebugManager.fatalAlert("Loading zone file failed!", DebugManager.getThrowableText("", ex));
			return "";
		}
		catch(Exception ex)
		{
			ValidationException valex = findValidationException(ex);
			
			if(valex != null)
			{
				for(DefinitionValidation val : valex.getValidation())
					validations.add(val);
				
				return "Validation of " + file + " failed."; 
			}
			else
			{
				throw ex;
			}	
		}
		
		ArrayList<UUID> ids = new ArrayList<>();
		
		for(ZariaObjectDefinition obj : objects)
		{
			if(!ids.contains(obj.getUUID()) && !objectDefinitions.containsKey(obj.getUUID()))
			{
				ids.add(obj.getUUID());
			}
			else
			{			
				return "Object Definition " + obj.getUUID() + " from " + file + " is either aleready added or duplicates name of another object definition!";
			}				
		}
		
		for(ZariaObjectDefinition obj : objects)
			addDefinitionToMap(obj, file);
		
		return "";
	}
	
	//--------------------------------------------------------------------------
	
	private String addLibrary(String file, ArrayList<DefinitionValidation> validations)
	{
		DebugManager.trace("Loading sublibary " + file + " into library " + getUUID());		
		
		ZariaObjectDefinitionLibrary library;
		
		try
		{
			library = ZoneDeserializer.loadLibrary(file);
		}
		catch(IOException ex)
		{
			DebugManager.fatalAlert("Loading library failed!", DebugManager.getThrowableText("", ex));
			return "";
		}
		catch(Exception ex)
		{
			ValidationException valex = findValidationException(ex);
			
			if (valex != null)
			{
				for(DefinitionValidation val : valex.getValidation())
					validations.add(val);
				
				return "Validation of " + file + " failed."; 
			}
			else 
			{
				throw ex;
			}
		}
		
		ArrayList<UUID> ids = new ArrayList<>();
		
		for(ZariaObjectDefinition obj : library.objectDefinitions.values())
		{
			if(!ids.contains(obj.getUUID()) && !objectDefinitions.containsKey(obj.getUUID()))
			{
				ids.add(obj.getUUID());
			}
			else
			{			
				return "Object Definition " + obj.getUUID() + " from sublibrary " + file + " is either aleready added or duplicates name of another object definition!";
			}				
		}
		
		objectDefinitions.putAll(library.objectDefinitions);
		
		return "";	
	}
	
	//--------------------------------------------------------------------------
	
	private String serializeAndValidate(String file, ArrayList<DefinitionValidation> validations)
	{
		if(file.endsWith("zonelib"))
			return addLibrary(file, validations);
		else
			return addDefinitionsFromFile(file, validations);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void onPreLoad()
	{			
		for(ZariaObjectDefinition objdef : objectDefinitions.values())
			objdef.preLoad(this);
	}

	//--------------------------------------------------------------------------
	
	@Override
	public void onValidate(DefinitionValidation val)
	{	
		for(String file : zoneFiles)
		{							
			ArrayList<DefinitionValidation> validations = new ArrayList<>();
			
			try
			{			
				String err = serializeAndValidate(file, validations);
			
				assert !err.isEmpty() || validations.isEmpty() : "Validation of library " + getUUID() + " returned no errors but validations are of length " + validations.size();
			
				val.addError(!err.isEmpty(), "library " + getUUID(), file, err);
			}
			catch(Exception ex)
			{
				val.addException(ex);
			}
			
			for(DefinitionValidation subval: validations)
				val.addSubValidation(subval);
		}
	}

	//--------------------------------------------------------------------------
}
