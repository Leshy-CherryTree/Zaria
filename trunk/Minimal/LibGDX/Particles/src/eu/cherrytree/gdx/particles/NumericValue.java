/****************************************/
/* NumericValue.java						*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

/**
 * 
 * Branched from libGDX particle system.
 */
public class NumericValue extends ParticleValue
{
	//--------------------------------------------------------------------------
	
	private float value;

	//--------------------------------------------------------------------------
	
	public float getValue()
	{
		return value;
	}
	
	//--------------------------------------------------------------------------

	public void setValue(float value)
	{
		this.value = value;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void save(Writer output) throws IOException
	{
		super.save(output);
		if (!active)
			return;
		output.write("value: " + value + "\n");
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void load(BufferedReader reader) throws IOException
	{
		super.load(reader);
		if (!active)
			return;
		value = readFloat(reader, "value");
	}
	
	//--------------------------------------------------------------------------

	public void load(NumericValue value)
	{
		super.load(value);
		this.value = value.value;
	}
	
	//--------------------------------------------------------------------------
}
