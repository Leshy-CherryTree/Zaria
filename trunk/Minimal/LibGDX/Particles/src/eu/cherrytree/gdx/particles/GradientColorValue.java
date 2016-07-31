/****************************************/
/* GradientColorValue.java				*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

/**
 * 
 * Branched from libGDX particle system.
 */
public class GradientColorValue extends ParticleValue
{
	//--------------------------------------------------------------------------
	
	private float[] colors = { 1, 1, 1 };
	private float[] timeline = { 0 };
	
	//--------------------------------------------------------------------------
	
	private transient float[] worker = new float[4];
	
	//--------------------------------------------------------------------------

	public GradientColorValue(float[] colors)
	{
		this.colors = colors;
		this.alwaysActive = true;
	}
	
	//--------------------------------------------------------------------------

	public float[] getTimeline()
	{
		return timeline;
	}
	
	//--------------------------------------------------------------------------

	public void setTimeline(float[] timeline)
	{
		this.timeline = timeline;
	}
	
	//--------------------------------------------------------------------------

	/**
	 * @return the r, g and b values for every timeline position
	 */
	public float[] getColors()
	{
		return colors;
	}
	//--------------------------------------------------------------------------
	

	/**
	 * @param colors the r, g and b values for every timeline position
	 */
	public void setColors(float[] colors)
	{
		this.colors = colors;
	}
	
	//--------------------------------------------------------------------------

	public float[] getColor(float percent)
	{
		int startIndex = 0, endIndex = -1;
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
			
			startIndex = i;
		}
		
		float startTime = timeline[startIndex];
		startIndex *= 3;
		float r1 = colors[startIndex];
		float g1 = colors[startIndex + 1];
		float b1 = colors[startIndex + 2];
		
		if (endIndex == -1)
		{
			worker[0] = r1;
			worker[1] = g1;
			worker[2] = b1;
			return worker;
		}
		
		float factor = (percent - startTime) / (timeline[endIndex] - startTime);
		
		endIndex *= 3;
		worker[0] = r1 + (colors[endIndex] - r1) * factor;
		worker[1] = g1 + (colors[endIndex + 1] - g1) * factor;
		worker[2] = b1 + (colors[endIndex + 2] - b1) * factor;
		
		return worker;
	}
	
	//--------------------------------------------------------------------------
}
