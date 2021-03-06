/****************************************/
/* Particle.java							*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;

import eu.cherrytree.zaria.game.GameObject;
import eu.cherrytree.zaria.game.messages.Message;
import eu.cherrytree.zaria.serialization.LoadCapsule;
import eu.cherrytree.zaria.serialization.SaveCapsule;
import eu.cherrytree.zaria.serialization.ValueAlreadySetException;

import java.util.ArrayList;

/**
 * 
 * Branched from libGDX particle system.
 */
public class ParticleEffect extends GameObject<ParticleEffectDefinition>
{
	//--------------------------------------------------------------------------

	private ArrayList<ParticleEmitter> emitters = new ArrayList<>();
	private BoundingBox bounds;
	
	//--------------------------------------------------------------------------

	public ParticleEffect(ParticleEffectDefinition definition, String name)
	{
		super(definition, name);

		for (ParticleEmitterDefinition emitter_def : definition.getEmitters())
			emitters.add(emitter_def.create());
		
		assert definition.getEmitters().size() == emitters.size();
	}
	
	//--------------------------------------------------------------------------

	public void start()
	{
		for (ParticleEmitter emitter : emitters)
			emitter.start();
	}
	
	//--------------------------------------------------------------------------

	public void reset()
	{
		for (ParticleEmitter emitter : emitters)
			emitter.reset();
	}
	
	//--------------------------------------------------------------------------

	public void update(float delta)
	{
		for (ParticleEmitter emitter : emitters)
			emitter.update(delta);
	}
	
	//--------------------------------------------------------------------------

	public void draw(Batch spriteBatch)
	{
		for (ParticleEmitter emitter : emitters)
			emitter.draw(spriteBatch);
	}
	
	//--------------------------------------------------------------------------

	public void allowCompletion()
	{
		for (ParticleEmitter emitter : emitters)
			emitter.allowCompletion();
	}
	
	//--------------------------------------------------------------------------

	public boolean isComplete()
	{
		for (ParticleEmitter emitter : emitters)
		{
			if (!emitter.isComplete())
				return false;
		}
		
		return true;
	}
	
	//--------------------------------------------------------------------------
	
	public float getPercentComplete()
	{
		// We're looking for the emitter with the longest duration.
		float duration = 0.0f;
		ParticleEmitter found_emitter = null;
		
		for (ParticleEmitter emitter : emitters)
		{
			float emitter_duration = emitter.getDuration();
			
			if (emitter_duration > duration)
			{
				duration = emitter_duration;
				found_emitter = emitter;
			}		
		}
		
		return found_emitter.getPercentComplete();
	}
	
	//--------------------------------------------------------------------------
	
	public void setTransformVectors(Vector2 transformX, Vector2 transformY)
	{
		for (ParticleEmitter emitter : emitters)
			emitter.setTransformVectors(transformX, transformY);
	}
	
	//--------------------------------------------------------------------------
	
	public void setUnitScale(float unitScale)
	{
		for (ParticleEmitter emitter : emitters)
			emitter.setUnitScale(unitScale);
	}
	
	//--------------------------------------------------------------------------

	public void setOffset(float x, float y)
	{
		for (ParticleEmitter emitter : emitters)
			emitter.setOffset(x, y);
	}
	
	//--------------------------------------------------------------------------
	
	public void setPosition(float x, float y)
	{
		for (ParticleEmitter emitter : emitters)
			emitter.setPosition(x, y);
	}
	
	//--------------------------------------------------------------------------

	public void setFlip(boolean flipX, boolean flipY)
	{
		for (ParticleEmitter emitter : emitters)
			emitter.setFlip(flipX, flipY);
	}
	
	//--------------------------------------------------------------------------

	public ArrayList<ParticleEmitter> getEmitters()
	{
		return emitters;
	}
	
	//--------------------------------------------------------------------------
	
	public void loadAssets(AssetManager assetManager)
	{
		for (ParticleEmitter emitter : emitters)
			emitter.loadAssets(assetManager);
	}

	//--------------------------------------------------------------------------

	/**
	 * Returns the bounding box for all active particles. z axis will always be zero.
	 */
	public BoundingBox getBoundingBox()
	{
		if (bounds == null)
			bounds = new BoundingBox();

		BoundingBox bounds = this.bounds;
		bounds.inf();
		
		for (ParticleEmitter emitter : this.emitters)
			bounds.ext(emitter.getBoundingBox());
		
		return bounds;
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Sets the
	 * {@link eu.cherrytree.gdx.particles.ParticleEmitter#setCleansUpBlendFunction(boolean) cleansUpBlendFunction}
	 * parameter on all {@link eu.cherrytree.gdx.particles.ParticleEmitter ParticleEmitters} currently in this
	 * ParticleEffect.
	 * <p>
	 * IMPORTANT: If set to false and if the next object to use this Batch expects alpha blending, you are responsible
	 * for setting the Batch's blend function to (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) before that next object is
	 * drawn.
	 *
	 * @param cleanUpBlendFunction
	 */
	public void setEmittersCleanUpBlendFunction(boolean cleanUpBlendFunction)
	{
		for (ParticleEmitter emitter : emitters)
			emitter.setCleansUpBlendFunction(cleanUpBlendFunction);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void load(LoadCapsule capsule)
	{
		throw new UnsupportedOperationException("Particle effects do not support state saving.");
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void save(SaveCapsule capsule) throws ValueAlreadySetException
	{
		throw new UnsupportedOperationException("Particle effects do not support state saving.");
	}
	
	//--------------------------------------------------------------------------
	

	@Override
	public void destroy()
	{
		// What should be here?
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void handleMessage(Message message)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
}
