/****************************************/
/* Particle.java							*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * 
 * Branched from libGDX particle system.
 */
public class Particle extends Sprite
{
	//--------------------------------------------------------------------------

	protected int life, currentLife;
	protected float scale, scaleDiff;
	protected float rotation, rotationDiff;
	protected float velocity, velocityDiff;
	protected float angle, angleDiff;
	protected float angleCos, angleSin;
	protected float transparency, transparencyDiff;
	protected float wind, windDiff;
	protected float gravity, gravityDiff;
	protected float[] tint;
	
	//--------------------------------------------------------------------------

	public Particle(Sprite sprite)
	{
		super(sprite);
	}
	
	//--------------------------------------------------------------------------
}
