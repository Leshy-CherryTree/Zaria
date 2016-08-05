/****************************************/
/* DefinitionWrapper.java				*/
/* Created on: 02-08-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor.datamodel;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DefinitionWrapper
{
	//--------------------------------------------------------------------------
	
	private ZariaObjectDefinition definition;

	//--------------------------------------------------------------------------
	
	public DefinitionWrapper(ZariaObjectDefinition definition)
	{
		this.definition = definition;
	}
	
	//--------------------------------------------------------------------------

	public ZariaObjectDefinition getDefinition()
	{
		return definition;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String toString()
	{
		return definition.getID();
	}
	
	//--------------------------------------------------------------------------
}
