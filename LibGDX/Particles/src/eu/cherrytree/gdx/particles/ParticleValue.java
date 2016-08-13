/****************************************/
/* ParticleValue.java					*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import java.io.Serializable;

/**
 * 
 * Branched from libGDX particle system.
 */
public abstract class ParticleValue implements Serializable
{
	//--------------------------------------------------------------------------

	protected boolean active;
	private final boolean alwaysActive;

	//--------------------------------------------------------------------------
	
	public ParticleValue(boolean alwaysActive)
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
}
