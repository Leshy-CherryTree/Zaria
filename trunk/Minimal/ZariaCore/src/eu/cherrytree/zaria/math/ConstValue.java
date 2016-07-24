/****************************************/
/* ConstValue.java						*/
/* Created on: 24-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.math;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ConstValue extends Value
{
	//--------------------------------------------------------------------------
	
	private float value = 0.0f;

	//--------------------------------------------------------------------------
	
	@Override
	public float get()
	{
		return value;
	}
		
	//--------------------------------------------------------------------------
}
