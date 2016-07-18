/****************************************/
/* Value.java							*/
/* Created on: 17-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.math;

import eu.cherrytree.zaria.serialization.ColorName;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.annotations.DefinitionCategory;
import eu.cherrytree.zaria.serialization.annotations.DefinitionColor;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@DefinitionCategory("Math")
@DefinitionColor(ColorName.CadetBlue)
public abstract class Value extends ZariaObjectDefinition
{
	//--------------------------------------------------------------------------
	
	public abstract float getValue(float x);

	//--------------------------------------------------------------------------
}
