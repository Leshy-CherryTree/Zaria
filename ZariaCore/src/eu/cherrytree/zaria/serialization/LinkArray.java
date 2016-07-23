/****************************************/
/* LinkArray.java						*/
/* Created on: 27-Mar-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

@JsonAutoDetect(	fieldVisibility=JsonAutoDetect.Visibility.ANY, 
					getterVisibility=JsonAutoDetect.Visibility.NONE, 
					isGetterVisibility=JsonAutoDetect.Visibility.NONE, 
					setterVisibility=JsonAutoDetect.Visibility.NONE, 
					creatorVisibility=JsonAutoDetect.Visibility.NONE	)

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public final class LinkArray<Definition extends ZariaObjectDefinition> implements Serializable, Iterable<Definition>
{
	//--------------------------------------------------------------------------
	
	public class LinkArrayIterator implements Iterator<Definition>
	{
		private int index = 0;
		
		@Override
		public boolean hasNext()
		{
			return index < objects.length;
		}

		@Override
		public Definition next()
		{
			index++;
			return (Definition) objects[index-1];
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("Items cannot be removed!");
		}
		
	}
	
	//--------------------------------------------------------------------------
	
	private UUID [] uuids = {};
	private transient Object [] objects = {};
	
	//--------------------------------------------------------------------------
	
	public boolean isEmpty()
	{
		return size() == 0;
	}
	
	//--------------------------------------------------------------------------
	
	public int size()
	{
		return uuids.length;
	}
	
	//--------------------------------------------------------------------------

	void load(ZariaObjectDefinitionLibrary library) throws CantResolveLinkException
	{
		assert library != null;
		
		objects = new Object[uuids.length];
		
		String not_found = "";
		
		for(int i = 0 ; i < objects.length ; i++)
		{
			objects[i] = (Definition) library.getDefinition(uuids[i]);			
			
			if(objects[i] == null)
				not_found += uuids[i] + ", ";
		}
		
		if(!not_found.isEmpty())
			throw new CantResolveLinkException(not_found, library.getID());
	}
	
	//--------------------------------------------------------------------------
	
	public UUID getUUID(int index)
	{
		return uuids[index];
	}
	
	//--------------------------------------------------------------------------
	
	public UUID[] getUUIDs()
	{
		return uuids;
	}
	
	//--------------------------------------------------------------------------
	
	public boolean contains(UUID uuid)
	{
		for(UUID ar_uuid : uuids)
		{
			if(ar_uuid.equals(uuid))
				return true;
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------
	
	public boolean contains(Definition definition)
	{
		for(Object obj : objects)
		{
			if(obj == definition)
				return true;
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------
	
	public Definition getDefinition(int index)
	{
		return (Definition) objects[index];
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Iterator<Definition> iterator()
	{
		return new LinkArrayIterator();
	}
	
	//--------------------------------------------------------------------------
	
	public void fill(Collection<Definition> collection)
	{
		for (Object obj : objects)
			collection.add((Definition) obj);
	}
	
	//--------------------------------------------------------------------------
}
