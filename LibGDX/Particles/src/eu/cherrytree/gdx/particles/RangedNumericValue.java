/****************************************/
/* RangedNumericValue.java				*/
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
public class RangedNumericValue extends ParticleValue
{
	//--------------------------------------------------------------------------

	private float lowMin, lowMax;
	
	//--------------------------------------------------------------------------

	public float newLowValue()
	{
		System.out.println("lowMin: " + lowMin);
		System.out.println("lowMax:" + lowMax);
		
		return lowMin + (lowMax - lowMin) * Random.getFloat();
	}
	
	//--------------------------------------------------------------------------

	public void setLow(float value)
	{
		lowMin = value;
		lowMax = value;
	}
	
	//--------------------------------------------------------------------------

	public void setLow(float min, float max)
	{
		lowMin = min;
		lowMax = max;
	}

	//--------------------------------------------------------------------------
	
	public float getLowMin()
	{
		return lowMin;
	}
	
	//--------------------------------------------------------------------------

	public void setLowMin(float lowMin)
	{
		this.lowMin = lowMin;
	}
	
	//--------------------------------------------------------------------------

	public float getLowMax()
	{
		return lowMax;
	}
	
	//--------------------------------------------------------------------------

	public void setLowMax(float lowMax)
	{
		this.lowMax = lowMax;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void save(Writer output) throws IOException
	{
		super.save(output);
		if (!active)
			return;
		output.write("lowMin: " + lowMin + "\n");
		output.write("lowMax: " + lowMax + "\n");
	}

	//--------------------------------------------------------------------------
	
	@Override
	public void load(BufferedReader reader) throws IOException
	{
		super.load(reader);
		if (!active)
			return;
		lowMin = readFloat(reader, "lowMin");
		lowMax = readFloat(reader, "lowMax");
	}
	
	//--------------------------------------------------------------------------

	public void load(RangedNumericValue value)
	{
		super.load(value);
		lowMax = value.lowMax;
		lowMin = value.lowMin;
	}
	
	//--------------------------------------------------------------------------
}
