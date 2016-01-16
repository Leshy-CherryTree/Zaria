/****************************************/
/* ObjectDefinitionWrappere.java 		*/
/* Created on: 27-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.definitions;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import java.util.UUID;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ObjectDefinitionWrapper
{
	//--------------------------------------------------------------------------
	
	private ZariaObjectDefinition definition;
	
	//--------------------------------------------------------------------------

	public ObjectDefinitionWrapper(ZariaObjectDefinition definition)
	{
		this.definition = definition;
	}
	
	//--------------------------------------------------------------------------
	
	public UUID getUUID()
	{
		return definition.getUUID();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public String toString()
	{
		return definition.getID();
	}
	
	//--------------------------------------------------------------------------
}
