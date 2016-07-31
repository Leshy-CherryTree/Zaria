/****************************************/
/* Particle.java							*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import java.io.File;
import java.util.HashMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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

	public void draw(Batch spriteBatch, float delta)
	{
		for (ParticleEmitter emitter : emitters)
			emitter.draw(spriteBatch, delta);
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
	
	/**
	 * Returns the emitter with the specified name, or null.
	 */
	public ParticleEmitter findEmitter(String name)
	{
		for (ParticleEmitter emitter : emitters)
		{
			if (emitter.getName().equals(name))
				return emitter;
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------

	public void loadEmitterImages(TextureAtlas atlas, String atlasPrefix)
	{
		for (ParticleEmitter emitter : emitters)
		{
			String imagePath = emitter.getImagePath();
			if (imagePath == null)
				continue;
			String imageName = new File(imagePath.replace('\\', '/')).getName();
			int lastDotIndex = imageName.lastIndexOf('.');
			if (lastDotIndex != -1)
				imageName = imageName.substring(0, lastDotIndex);
			if (atlasPrefix != null)
				imageName = atlasPrefix + imageName;
			Sprite sprite = atlas.createSprite(imageName);
			if (sprite == null)
				throw new IllegalArgumentException("SpriteSheet missing image: " + imageName);
			emitter.setSprite(sprite);
		}
	}

	public void loadEmitterImages(FileHandle imagesDir)
	{
//		ownsTexture = true;
		HashMap<String, Sprite> loadedSprites = new HashMap<>(emitters.size());
		
		for (ParticleEmitter emitter : emitters)
		{
			String imagePath = emitter.getImagePath();
			if (imagePath == null)
				continue;
			String imageName = new File(imagePath.replace('\\', '/')).getName();
			Sprite sprite = loadedSprites.get(imageName);
			if (sprite == null)
			{
				sprite = new Sprite(loadTexture(imagesDir.child(imageName)));
				loadedSprites.put(imageName, sprite);
			}
			emitter.setSprite(sprite);
		}
	}

	protected Texture loadTexture(FileHandle file)
	{
		return new Texture(file, false);
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
