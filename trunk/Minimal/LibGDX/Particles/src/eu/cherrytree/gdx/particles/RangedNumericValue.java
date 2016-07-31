/****************************************/
/* RangedNumericValue.java				*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import eu.cherrytree.zaria.utilities.Random;


/**
 * 
 * Branched from libGDX particle system.
 */
public class RangedNumericValue extends ParticleValue
{
	//--------------------------------------------------------------------------

	private float lowMin, lowMax;

	//--------------------------------------------------------------------------
	
	public RangedNumericValue(float lowMin, float lowMax)
	{
		this.lowMin = lowMin;
		this.lowMax = lowMax;
	}
	
	//--------------------------------------------------------------------------

	public float newLowValue()
	{		
		return lowMin + (lowMax - lowMin) * Random.getFloat();
	}
	
	//--------------------------------------------------------------------------
	
	public float getLowMin()
	{
		return lowMin;
	}

	//--------------------------------------------------------------------------

	public float getLowMax()
	{
		return lowMax;
	}
	
	//--------------------------------------------------------------------------
}
