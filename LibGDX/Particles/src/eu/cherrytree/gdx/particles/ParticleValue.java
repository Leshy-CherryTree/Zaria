/****************************************/
/* ParticleValue.java					*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

/**
 * 
 * Branched from libGDX particle system.
 */
public class ParticleValue implements Serializable
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
}
