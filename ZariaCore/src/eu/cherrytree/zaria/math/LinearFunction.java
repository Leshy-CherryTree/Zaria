/****************************************/
/* LinearFunction.java					*/
/* Created on: 06-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.math;

import eu.cherrytree.zaria.serialization.DefinitionValidation;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class LinearFunction extends Function
{
	//--------------------------------------------------------------------------
	
	private float a = 1.0f;
	private float b = 0.0f;
	
	//--------------------------------------------------------------------------

	@Override
	public float getValue(float x)
	{
		return a * x + b;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onPreLoad()
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onValidate(DefinitionValidation validation)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------
}
