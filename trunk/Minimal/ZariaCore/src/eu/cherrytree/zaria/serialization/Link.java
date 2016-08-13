/****************************************/
/* Link.java								*/
/* Created on: 20-May-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;
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
public final class Link<Definition extends ZariaObjectDefinition> implements Serializable
{
	//--------------------------------------------------------------------------
	
	private UUID uuid;	
	private transient Definition definition;
	
	//--------------------------------------------------------------------------

	void load(ZariaObjectDefinitionLibrary library) throws CantResolveLinkException
	{
		assert library != null;
		
		if(uuid != null)
		{
			definition = (Definition) library.getDefinition(uuid);
		
			if(definition == null)
				throw new CantResolveLinkException(uuid, library.getID());
		}
	}
	
	//--------------------------------------------------------------------------
	
	public UUID getUUID()
	{
		return uuid;
	}
			
	//--------------------------------------------------------------------------

	public Definition getDefinition()
	{		
		return definition;
	}
	
	//--------------------------------------------------------------------------
}
