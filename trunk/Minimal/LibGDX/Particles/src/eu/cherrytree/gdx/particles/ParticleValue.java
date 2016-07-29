/****************************************/
/* ParticleValue.java					*/
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
public class ParticleValue
{
	//--------------------------------------------------------------------------

	protected boolean active;
	protected boolean alwaysActive;
	
	//--------------------------------------------------------------------------

	public void setAlwaysActive(boolean alwaysActive)
	{
		this.alwaysActive = alwaysActive;
	}
	
	//--------------------------------------------------------------------------

	public boolean isAlwaysActive()
	{
		return alwaysActive;
	}
	
	//--------------------------------------------------------------------------

	public boolean isActive()
	{
		return alwaysActive || active;
	}
	
	//--------------------------------------------------------------------------

	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	//--------------------------------------------------------------------------

	public void save(Writer output) throws IOException
	{
		if (!alwaysActive)
			output.write("active: " + active + "\n");
		else
			active = true;
	}
	
	//--------------------------------------------------------------------------

	public void load(BufferedReader reader) throws IOException
	{
		if (!alwaysActive)
			active = readBoolean(reader, "active");
		else
			active = true;
	}

	//--------------------------------------------------------------------------
	
	public void load(ParticleValue value)
	{
		active = value.active;
		alwaysActive = value.alwaysActive;
	}
	
	//--------------------------------------------------------------------------
	
	protected String readString(String line) throws IOException
	{
		return line.substring(line.indexOf(":") + 1).trim();
	}

	protected String readString(BufferedReader reader, String name) throws IOException
	{
		String line = reader.readLine();
		if (line == null)
			throw new IOException("Missing value: " + name);
		return readString(line);
	}

	protected boolean readBoolean(String line) throws IOException
	{
		return Boolean.parseBoolean(readString(line));
	}

	protected boolean readBoolean(BufferedReader reader, String name) throws IOException
	{
		return Boolean.parseBoolean(readString(reader, name));
	}

	protected int readInt(BufferedReader reader, String name) throws IOException
	{
		return Integer.parseInt(readString(reader, name));
	}

	protected float readFloat(BufferedReader reader, String name) throws IOException
	{
		return Float.parseFloat(readString(reader, name));
	}
	
	//--------------------------------------------------------------------------
}
