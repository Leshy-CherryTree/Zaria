/****************************************/
/* Const.java							*/
/* Created on: 17-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.math;

import eu.cherrytree.zaria.serialization.DefinitionValidation;
import eu.cherrytree.zaria.serialization.annotations.DefinitionDescription;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@DefinitionDescription("A constant floating point value.")
public class Const extends Value
{
	//--------------------------------------------------------------------------
	
	private float value = 0.0f;

	@Override
	public float getValue(float x)
	{
		return value;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onPreLoad()
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onValidate(DefinitionValidation dv)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------
}
