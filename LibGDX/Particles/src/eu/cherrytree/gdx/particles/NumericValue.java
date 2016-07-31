/****************************************/
/* NumericValue.java						*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

/**
 * 
 * Branched from libGDX particle system.
 */
public class NumericValue extends ParticleValue
{
	//--------------------------------------------------------------------------
	
	private float value;

	//--------------------------------------------------------------------------
	
	public NumericValue(float value)
	{
		this.value = value;
	}
	
	//--------------------------------------------------------------------------
	
	public float getValue()
	{
		return value;
	}
	
	//--------------------------------------------------------------------------
}