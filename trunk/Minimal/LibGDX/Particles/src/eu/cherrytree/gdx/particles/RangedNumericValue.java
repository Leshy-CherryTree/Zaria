/****************************************/
/* RangedNumericValue.java				*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import eu.cherrytree.zaria.utilities.Random;

@JsonAutoDetect(	fieldVisibility=JsonAutoDetect.Visibility.ANY, 
					getterVisibility=JsonAutoDetect.Visibility.NONE, 
					isGetterVisibility=JsonAutoDetect.Visibility.NONE, 
					setterVisibility=JsonAutoDetect.Visibility.NONE, 
					creatorVisibility=JsonAutoDetect.Visibility.NONE	)

/**
 * 
 * Branched from libGDX particle system.
 */
public class RangedNumericValue extends ParticleValue
{
	//--------------------------------------------------------------------------

	private float lowMin;
	private float lowMax;

	//--------------------------------------------------------------------------

	public RangedNumericValue()
	{
		super(false);
		
		// For serialization only.
	}
	
	//--------------------------------------------------------------------------
	
	public RangedNumericValue(float lowMin, float lowMax, boolean alwaysActive)
	{
		super(alwaysActive);
		
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
