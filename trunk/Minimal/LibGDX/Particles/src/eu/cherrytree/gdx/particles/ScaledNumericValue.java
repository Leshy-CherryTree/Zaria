/****************************************/
/* ScaledNumericValue.java				*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import eu.cherrytree.zaria.utilities.Random;
	
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

/**
 * 
 * Branched from libGDX particle system.
 */
public class ScaledNumericValue extends RangedNumericValue
{
	//--------------------------------------------------------------------------

	private float[] scaling = { 1 };
	private float[] timeline = { 0 };
	private float highMin, highMax;
	private boolean relative;
	
	//--------------------------------------------------------------------------

	public float newHighValue()
	{
		return highMin + (highMax - highMin) * Random.getFloat();
	}
	
	//--------------------------------------------------------------------------
	
	public void setHigh(float value)
	{
		highMin = value;
		highMax = value;
	}
	
	//--------------------------------------------------------------------------

	public void setHigh(float min, float max)
	{
		highMin = min;
		highMax = max;
	}
	
	//--------------------------------------------------------------------------

	public float getHighMin()
	{
		return highMin;
	}
	
	//--------------------------------------------------------------------------
	
	public void setHighMin(float highMin)
	{
		this.highMin = highMin;
	}
	
	//--------------------------------------------------------------------------

	public float getHighMax()
	{
		return highMax;
	}
	
	//--------------------------------------------------------------------------

	public void setHighMax(float highMax)
	{
		this.highMax = highMax;
	}
	
	//--------------------------------------------------------------------------

	public float[] getScaling()
	{
		return scaling;
	}
	
	//--------------------------------------------------------------------------

	public void setScaling(float[] values)
	{
		this.scaling = values;
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

	public boolean isRelative()
	{
		return relative;
	}

	//--------------------------------------------------------------------------
	
	public void setRelative(boolean relative)
	{
		this.relative = relative;
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

	public void save(Writer output) throws IOException
	{
		super.save(output);
		if (!active)
			return;
		output.write("highMin: " + highMin + "\n");
		output.write("highMax: " + highMax + "\n");
		output.write("relative: " + relative + "\n");
		output.write("scalingCount: " + scaling.length + "\n");
		for (int i = 0; i < scaling.length; i++)
			output.write("scaling" + i + ": " + scaling[i] + "\n");
		output.write("timelineCount: " + timeline.length + "\n");
		for (int i = 0; i < timeline.length; i++)
			output.write("timeline" + i + ": " + timeline[i] + "\n");
	}

	public void load(BufferedReader reader) throws IOException
	{
		super.load(reader);
		if (!active)
			return;
		highMin = readFloat(reader, "highMin");
		highMax = readFloat(reader, "highMax");
		relative = readBoolean(reader, "relative");
		scaling = new float[readInt(reader, "scalingCount")];
		for (int i = 0; i < scaling.length; i++)
			scaling[i] = readFloat(reader, "scaling" + i);
		timeline = new float[readInt(reader, "timelineCount")];
		for (int i = 0; i < timeline.length; i++)
			timeline[i] = readFloat(reader, "timeline" + i);
	}

	public void load(ScaledNumericValue value)
	{
		super.load(value);
		highMax = value.highMax;
		highMin = value.highMin;
		scaling = new float[value.scaling.length];
		System.arraycopy(value.scaling, 0, scaling, 0, scaling.length);
		timeline = new float[value.timeline.length];
		System.arraycopy(value.timeline, 0, timeline, 0, timeline.length);
		relative = value.relative;
	}
	
	//--------------------------------------------------------------------------
}