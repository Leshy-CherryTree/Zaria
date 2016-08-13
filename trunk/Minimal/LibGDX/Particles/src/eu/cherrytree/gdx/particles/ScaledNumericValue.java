/****************************************/
/* ScaledNumericValue.java				*/
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
public class ScaledNumericValue extends RangedNumericValue
{
	//--------------------------------------------------------------------------

	private float[] scaling = { 1 };
	private float[] timeline = { 0 };
	private float highMin;
	private float highMax;
	private boolean relative = false;
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue()
	{
		// For serialization only.
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue(float highMin, float highMax, float lowMin, float lowMax, boolean alwaysActive)
	{
		super(lowMin, lowMax, alwaysActive);
		
		this.highMin = highMin;
		this.highMax = highMax;
	}
	
	//--------------------------------------------------------------------------

	public float newHighValue()
	{
		return highMin + (highMax - highMin) * Random.getFloat();
	}
	
	//--------------------------------------------------------------------------

	public float getHighMin()
	{
		return highMin;
	}
	
	//--------------------------------------------------------------------------

	public float getHighMax()
	{
		return highMax;
	}
	
	//--------------------------------------------------------------------------

	public float[] getScaling()
	{
		return scaling;
	}
	
	//--------------------------------------------------------------------------

	public float[] getTimeline()
	{
		return timeline;
	}
	
	//--------------------------------------------------------------------------

	public boolean isRelative()
	{
		return relative;
	}
	
	//--------------------------------------------------------------------------

	public float getScale(float percent)
	{
		int endIndex = -1;
		float[] timeline = this.timeline;
		int n = timeline.length;
		
		for (int i = 1; i < n; i++)
		{
			float t = timeline[i];
			
			if (t > percent)
			{
				endIndex = i;
				break;
			}
			
		}
		
		if (endIndex == -1)
			return scaling[n - 1];
		
		float[] scaling = this.scaling;
		
		int startIndex = endIndex - 1;
		float startValue = scaling[startIndex];
		float startTime = timeline[startIndex];
		
		return startValue + (scaling[endIndex] - startValue) * ((percent - startTime) / (timeline[endIndex] - startTime));
	}
	
	//--------------------------------------------------------------------------
}