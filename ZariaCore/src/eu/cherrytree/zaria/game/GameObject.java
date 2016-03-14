/****************************************/
/* GameObject.java						*/
/* Created on: 05-Apr-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.game;

import eu.cherrytree.zaria.game.messages.MessageHandler;

import eu.cherrytree.zaria.scripting.annotations.ScriptMethod;
import eu.cherrytree.zaria.serialization.LoadCapsule;
import eu.cherrytree.zaria.serialization.SaveCapsule;
import eu.cherrytree.zaria.serialization.ValueAlreadySetException;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class GameObject<Definition extends GameObjectDefinition> implements MessageHandler
{
	//--------------------------------------------------------------------------
	
	private static ConcurrentHashMap<String, Integer> objectCounters = new ConcurrentHashMap<>();
	
	//--------------------------------------------------------------------------
	
	public static void saveCounters(SaveCapsule capsule) throws ValueAlreadySetException
	{
		capsule.writeObject("GameObjectCounters", objectCounters);
	}
	
	//--------------------------------------------------------------------------
	
	public static void loadCounters(LoadCapsule capsule)
	{
		ConcurrentHashMap<String, Integer> counters = (ConcurrentHashMap<String, Integer>) capsule.readObject("GameObjectCounters");
		
		if(counters != null)
			 objectCounters = counters;
	}
	
	//--------------------------------------------------------------------------
	
	private Definition definition;
	private String name;
	
	//--------------------------------------------------------------------------

	public GameObject(Definition definition, String name)
	{
		assert definition != null;
		assert name == null || !name.isEmpty();
		
		this.definition = definition;
		
		if(name == null)
		{
			int count;

			if(objectCounters.containsKey(definition.getID()))
			{
				count = objectCounters.get(definition.getID()).intValue();		
				count++;			
			}
			else
				count = 1;

			objectCounters.put(definition.getID(), count);

			this.name = definition.getID() + "_" + String.format("%02d", count);	
		}
		else
		{
			this.name = name;	
		}
	}

	//--------------------------------------------------------------------------

	public Definition getDefinition()
	{
		return definition;
	}
	
	//--------------------------------------------------------------------------

	@ScriptMethod
	(
		description = "Gets the unique name of ths GameObject.",
		returnValue = "The unique name of ths GameObject",
		parameters = {}
	)
	public final String getName()
	{
		return name;
	}
	
	//--------------------------------------------------------------------------
	
	public abstract void load(LoadCapsule capsule);
	public abstract void save(SaveCapsule capsule) throws ValueAlreadySetException;    
	public abstract void destroy();

	//--------------------------------------------------------------------------
}
