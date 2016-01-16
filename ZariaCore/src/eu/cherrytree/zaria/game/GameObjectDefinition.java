/****************************************/
/* GameObjectDefinition.java			*/
/* Created on: 04-Apr-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.game;

import eu.cherrytree.zaria.serialization.ColorName;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.annotations.DefinitionCategory;
import eu.cherrytree.zaria.serialization.annotations.DefinitionColor;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@DefinitionCategory("Game Objects")
@DefinitionColor(ColorName.LightSteelBlue)
public abstract class GameObjectDefinition<Type extends GameObject> extends ZariaObjectDefinition
{
	//--------------------------------------------------------------------------
	
	public abstract Type create(String name);
	
	//--------------------------------------------------------------------------
}
