/****************************************/
/* ParticleEmitter.java					*/
/* Created on: 25-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;


import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.collision.BoundingBox;

import eu.cherrytree.zaria.game.GameObject;
import eu.cherrytree.zaria.game.messages.Message;
import eu.cherrytree.zaria.serialization.LoadCapsule;
import eu.cherrytree.zaria.serialization.SaveCapsule;
import eu.cherrytree.zaria.serialization.ValueAlreadySetException;
import eu.cherrytree.zaria.utilities.Random;

/**
 * 
 * Branched from libGDX particle system.
 */
public class ParticleEmitter extends GameObject<ParticleEmitterDefinition>
{
	//--------------------------------------------------------------------------

	private static final int UPDATE_SCALE =		1 << 0;
	private static final int UPDATE_ANGLE =		1 << 1;
	private static final int UPDATE_ROTATION =	1 << 2;
	private static final int UPDATE_VELOCITY =	1 << 3;
	private static final int UPDATE_WIND =		1 << 4;
	private static final int UPDATE_GRAVITY =	1 << 5;
	private static final int UPDATE_TINT =		1 << 6;
	
	//--------------------------------------------------------------------------

	private float accumulator;
	private Sprite sprite;
	private Particle[] particles;
	
	private float x, y;
	private String imagePath;
	private int activeCount;
	private boolean[] active;
	private boolean firstUpdate;
	private boolean flipX, flipY;
	private int updateFlags;
	private boolean allowCompletion;
	private BoundingBox bounds;

	private int emission, emissionDiff, emissionDelta;
	private int lifeOffset, lifeOffsetDiff;
	private int life, lifeDiff;
	private float spawnWidth, spawnWidthDiff;
	private float spawnHeight, spawnHeightDiff;
	public float duration = 1, durationTimer;
	private float delay, delayTimer;

	private boolean cleansUpBlendFunction = true;
	
	//--------------------------------------------------------------------------
	

	public ParticleEmitter(ParticleEmitterDefinition definition, String name)
	{
		super(definition, name);
		
		active = new boolean[getDefinition().getMaxParticleCount()];
		activeCount = 0;
		particles = new Particle[getDefinition().getMaxParticleCount()];
	}
	
	//--------------------------------------------------------------------------

	public void addParticle()
	{
		int active_count = this.activeCount;
		if (active_count == getDefinition().getMaxParticleCount())
			return;
		
		boolean[] is_active = active;
		
		for (int i = 0, n = is_active.length; i < n; i++)
		{
			if (!is_active[i])
			{
				activateParticle(i);
				is_active[i] = true;
				this.activeCount = active_count + 1;
				
				break;
			}
		}
	}
	
	//--------------------------------------------------------------------------

	public void addParticles(int count)
	{
		count = Math.min(count, getDefinition().getMaxParticleCount() - activeCount);
		
		if (count == 0)
			return;
			
		boolean[] is_active = active;
		int index = 0, n = is_active.length;
		
		outer:
		for (int i = 0; i < count; i++)
		{
			for (; index < n; index++)
			{
				if (!is_active[index])
				{
					activateParticle(index);
					is_active[index++] = true;
					continue outer;
				}
			}
			break;
		}
		
		this.activeCount += count;
	}
	
	//--------------------------------------------------------------------------

	public void update(float delta)
	{
		accumulator += delta * 1000;
		
		if (accumulator < 1)
			return;

		int deltaMillis = (int) accumulator;
		accumulator -= deltaMillis;

		if (delayTimer < delay)
		{
			delayTimer += deltaMillis;
		}
		else
		{
			boolean done = false;
			
			if (firstUpdate)
			{
				firstUpdate = false;
				addParticle();
			}

			if (durationTimer < duration)
				durationTimer += deltaMillis;
			else if (!getDefinition().isContinuous() || allowCompletion)
				done = true;
			else
				restart();

			if (!done)
			{
				emissionDelta += deltaMillis;
				float emissionTime = emission + emissionDiff * getDefinition().getEmissionValue().getScale(durationTimer / (float) duration);
				
				if (emissionTime > 0)
				{
					emissionTime = 1000 / emissionTime;
					if (emissionDelta >= emissionTime)
					{
						int emitCount = (int) (emissionDelta / emissionTime);
						emitCount = Math.min(emitCount, getDefinition().getMaxParticleCount() - activeCount);
						emissionDelta -= emitCount * emissionTime;
						emissionDelta %= emissionTime;
						addParticles(emitCount);
					}
				}
				
				if (activeCount < getDefinition().getMinParticleCount())
					addParticles(getDefinition().getMinParticleCount() - activeCount);
			}
		}

		boolean[] is_active = active;
		int active_count = activeCount;
		Particle[] particle_array = particles;
		
		for (int i = 0, n = is_active.length; i < n; i++)
		{
			if (is_active[i] && !updateParticle(particle_array[i], delta, deltaMillis))
			{
				is_active[i] = false;
				active_count--;
			}
		}
		
		this.activeCount = active_count;
	}

	//--------------------------------------------------------------------------
	
	public void draw(Batch batch)
	{
		if (getDefinition().isPremultipliedAlpha())
		{
			batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
		else if (getDefinition().isAdditive())
		{
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		}
		else
		{
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
		Particle[] particles = this.particles;
		boolean[] active = this.active;

		for (int i = 0, n = active.length; i < n; i++)
		{
			if (active[i])
				particles[i].draw(batch);
		}

		if (cleansUpBlendFunction && (getDefinition().isAdditive() || getDefinition().isPremultipliedAlpha()))
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Updates and draws the particles. This is slightly more efficient than calling {@link #update(float)} and
	 * {@link #draw(Batch)} separately.
	 */
	public void draw(Batch batch, float delta)
	{
		accumulator += delta * 1000;
		if (accumulator < 1)
		{
			draw(batch);
			return;
		}
		
		int deltaMillis = (int) accumulator;
		accumulator -= deltaMillis;

		if (getDefinition().isPremultipliedAlpha())
			batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		else if (getDefinition().isAdditive())
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		else
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		Particle[] particles = this.particles;
		boolean[] active = this.active;
		int activeCount = this.activeCount;
		
		for (int i = 0, n = active.length; i < n; i++)
		{
			if (active[i])
			{
				Particle particle = particles[i];
				
				if (updateParticle(particle, delta, deltaMillis))
				{
					particle.draw(batch);
				}
				else
				{
					active[i] = false;
					activeCount--;
				}
			}
		}
		
		this.activeCount = activeCount;

		if (cleansUpBlendFunction && (getDefinition().isAdditive() || getDefinition().isPremultipliedAlpha()))
			batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		if (delayTimer < delay)
		{
			delayTimer += deltaMillis;
			return;
		}

		if (firstUpdate)
		{
			firstUpdate = false;
			addParticle();
		}

		if (durationTimer < duration)
		{
			durationTimer += deltaMillis;
		}
		else
		{
			if (!getDefinition().isContinuous() || allowCompletion)
				return;

			restart();
		}

		emissionDelta += deltaMillis;
		float emissionTime = emission + emissionDiff * getDefinition().getEmissionValue().getScale(durationTimer / (float) duration);
		
		if (emissionTime > 0)
		{
			emissionTime = 1000 / emissionTime;
			
			if (emissionDelta >= emissionTime)
			{
				int emitCount = (int) (emissionDelta / emissionTime);
				emitCount = Math.min(emitCount, getDefinition().getMaxParticleCount() - activeCount);
				emissionDelta -= emitCount * emissionTime;
				emissionDelta %= emissionTime;
				addParticles(emitCount);
			}
		}
		
		if (activeCount < getDefinition().getMinParticleCount())
			addParticles(getDefinition().getMinParticleCount() - activeCount);
	}
	
	//--------------------------------------------------------------------------

	public void start()
	{
		firstUpdate = true;
		allowCompletion = false;
		restart();
	}
	
	//--------------------------------------------------------------------------

	public void reset()
	{
		emissionDelta = 0;
		durationTimer = duration;
		boolean[] active = this.active;
		
		for (int i = 0, n = active.length; i < n; i++)
			active[i] = false;

		activeCount = 0;
		start();
	}
	
	//--------------------------------------------------------------------------

	private void restart()
	{
		delay = getDefinition().getDelayValue().active ? getDefinition().getDelayValue().newLowValue() : 0;
		delayTimer = 0;

		durationTimer -= duration;
		duration = getDefinition().getDurationValue().newLowValue();

		emission = (int) getDefinition().getEmissionValue().newLowValue();
		emissionDiff = (int) getDefinition().getEmissionValue().newHighValue();
		
		if (!getDefinition().getEmissionValue().isRelative())
			emissionDiff -= emission;

		life = (int) getDefinition().getLifeValue().newLowValue();
		lifeDiff = (int) getDefinition().getLifeValue().newHighValue();
		
		if (!getDefinition().getLifeValue().isRelative())
			lifeDiff -= life;

		lifeOffset = getDefinition().getLifeOffsetValue().active ? (int) getDefinition().getLifeOffsetValue().newLowValue() : 0;
		lifeOffsetDiff = (int) getDefinition().getLifeOffsetValue().newHighValue();
		
		if (!getDefinition().getLifeOffsetValue().isRelative())
			lifeOffsetDiff -= lifeOffset;

		spawnWidth = getDefinition().getSpawnWidthValue().newLowValue();
		spawnWidthDiff = getDefinition().getSpawnWidthValue().newHighValue();
		
		if (!getDefinition().getSpawnWidthValue().isRelative())
			spawnWidthDiff -= spawnWidth;

		spawnHeight = getDefinition().getSpawnHeightValue().newLowValue();
		spawnHeightDiff = getDefinition().getSpawnHeightValue().newHighValue();
		
		if (!getDefinition().getSpawnHeightValue().isRelative())
			spawnHeightDiff -= spawnHeight;

		updateFlags = 0;
		
		if (getDefinition().getAngleValue().active && getDefinition().getAngleValue().getTimeline().length > 1)
			updateFlags |= UPDATE_ANGLE;

		if (getDefinition().getVelocityValue().active)
			updateFlags |= UPDATE_VELOCITY;

		if (getDefinition().getScaleValue().getTimeline().length > 1)
			updateFlags |= UPDATE_SCALE;
			
		if (getDefinition().getRotationValue().active && getDefinition().getRotationValue().getTimeline().length > 1)
			updateFlags |= UPDATE_ROTATION;

		if (getDefinition().getWindValue().active)
			updateFlags |= UPDATE_WIND;

		if (getDefinition().getGravityValue().active)
			updateFlags |= UPDATE_GRAVITY;
		
		if (getDefinition().getTintValue().getTimeline().length > 1)
			updateFlags |= UPDATE_TINT;
	}
	
	//--------------------------------------------------------------------------

	protected Particle createNewParticle(Sprite sprite)
	{
		return new Particle(sprite);
	}
	
	//--------------------------------------------------------------------------

	private void activateParticle(int index)
	{
		Particle particle = particles[index];

		if (particle == null)
		{
			particles[index] = particle = createNewParticle(sprite);
			particle.flip(flipX, flipY);
		}

		float percent = durationTimer / (float) duration;
		int updateFlags = this.updateFlags;

		particle.currentLife = particle.life = life + (int) (lifeDiff * getDefinition().getLifeValue().getScale(percent));

		if (getDefinition().getVelocityValue().active)
		{
			particle.velocity = getDefinition().getVelocityValue().newLowValue();
			particle.velocityDiff = getDefinition().getVelocityValue().newHighValue();

			if (!getDefinition().getVelocityValue().isRelative())
				particle.velocityDiff -= particle.velocity;
		}

		particle.angle = getDefinition().getAngleValue().newLowValue();
		particle.angleDiff = getDefinition().getAngleValue().newHighValue();

		if (!getDefinition().getAngleValue().isRelative())
			particle.angleDiff -= particle.angle;

		float angle = 0;

		if ((updateFlags & UPDATE_ANGLE) == 0)
		{
			angle = particle.angle + particle.angleDiff * getDefinition().getAngleValue().getScale(0);
			particle.angle = angle;
			particle.angleCos = MathUtils.cosDeg(angle);
			particle.angleSin = MathUtils.sinDeg(angle);
		}

		float spriteWidth = sprite.getWidth();
		particle.scale = getDefinition().getScaleValue().newLowValue() / spriteWidth;
		particle.scaleDiff = getDefinition().getScaleValue().newHighValue() / spriteWidth;
		
		if (!getDefinition().getScaleValue().isRelative())
			particle.scaleDiff -= particle.scale;
		
		particle.setScale(particle.scale + particle.scaleDiff * getDefinition().getScaleValue().getScale(0));

		if (getDefinition().getRotationValue().active)
		{
			particle.rotation = getDefinition().getRotationValue().newLowValue();
			particle.rotationDiff = getDefinition().getRotationValue().newHighValue();
			
			if (!getDefinition().getRotationValue().isRelative())
				particle.rotationDiff -= particle.rotation;
			
			float rotation = particle.rotation + particle.rotationDiff * getDefinition().getRotationValue().getScale(0);
			
			if (getDefinition().isAligned())
				rotation += angle;
			
			particle.setRotation(rotation);
		}

		if (getDefinition().getWindValue().active)
		{
			particle.wind = getDefinition().getWindValue().newLowValue();
			particle.windDiff = getDefinition().getWindValue().newHighValue();
			
			if (!getDefinition().getWindValue().isRelative())
				particle.windDiff -= particle.wind;
		}

		if (getDefinition().getGravityValue().active)
		{
			particle.gravity = getDefinition().getGravityValue().newLowValue();
			particle.gravityDiff = getDefinition().getGravityValue().newHighValue();
			if (!getDefinition().getGravityValue().isRelative())
				particle.gravityDiff -= particle.gravity;
		}

		float[] color = particle.tint;
		
		if (color == null)
			particle.tint = color = new float[3];
		
		float[] temp = getDefinition().getTintValue().getColor(0);
		color[0] = temp[0];
		color[1] = temp[1];
		color[2] = temp[2];

		particle.transparency = getDefinition().getTransparencyValue().newLowValue();
		particle.transparencyDiff = getDefinition().getTransparencyValue().newHighValue() - particle.transparency;

		// Spawn.
		float x = this.x;
		
		if (getDefinition().getXOffsetValue().active)
			x += getDefinition().getXOffsetValue().newLowValue();
		
		float y = this.y;
		
		if (getDefinition().getYOffsetValue().active)
			y += getDefinition().getYOffsetValue().newLowValue();
		
		switch (getDefinition().getSpawnShapeValue().getShape())
		{
			case Square:
			{
				float width = spawnWidth + (spawnWidthDiff * getDefinition().getSpawnWidthValue().getScale(percent));
				float height = spawnHeight + (spawnHeightDiff * getDefinition().getSpawnHeightValue().getScale(percent));
				x += MathUtils.random(width) - width / 2;
				y += MathUtils.random(height) - height / 2;
				break;
			}
			
			case Ellipse:
			{
				float width = spawnWidth + (spawnWidthDiff * getDefinition().getSpawnWidthValue().getScale(percent));
				float height = spawnHeight + (spawnHeightDiff * getDefinition().getSpawnHeightValue().getScale(percent));
				float radiusX = width / 2;
				float radiusY = height / 2;
				
				if (radiusX == 0 || radiusY == 0)
					break;
				
				float scaleY = radiusX / (float) radiusY;
				
				if (getDefinition().getSpawnShapeValue().isEdges())
				{
					float spawnAngle;
					switch (getDefinition().getSpawnShapeValue().getSide())
					{
						case Top:
							spawnAngle = -MathUtils.random(179f);
							break;
						case Bottom:
							spawnAngle = MathUtils.random(179f);
							break;
						default:
							spawnAngle = MathUtils.random(360f);
							break;
					}
					
					float cosDeg = MathUtils.cosDeg(spawnAngle);
					float sinDeg = MathUtils.sinDeg(spawnAngle);
					x += cosDeg * radiusX;
					y += sinDeg * radiusX / scaleY;
					
					if ((updateFlags & UPDATE_ANGLE) == 0)
					{
						particle.angle = spawnAngle;
						particle.angleCos = cosDeg;
						particle.angleSin = sinDeg;
					}
				}
				else
				{
					float radius2 = radiusX * radiusX;
					
					while (true)
					{
						float px = MathUtils.random(width) - radiusX;
						float py = MathUtils.random(height) - radiusY;
						if (px * px + py * py <= radius2)
						{
							x += px;
							y += py / scaleY;
							break;
						}
					}
				}
				break;
			}
			
			case Line:
			{
				float width = spawnWidth + (spawnWidthDiff * getDefinition().getSpawnWidthValue().getScale(percent));
				float height = spawnHeight + (spawnHeightDiff * getDefinition().getSpawnHeightValue().getScale(percent));
				
				if (width != 0)
				{
					float lineX = width * Random.getFloat();
					x += lineX;
					y += lineX * (height / (float) width);
				}
				else
				{
					y += height * Random.getFloat();
				}
				
				break;
			}
		}

		float spriteHeight = sprite.getHeight();
		particle.setBounds(x - spriteWidth / 2, y - spriteHeight / 2, spriteWidth, spriteHeight);

		int offsetTime = (int) (lifeOffset + lifeOffsetDiff * getDefinition().getLifeOffsetValue().getScale(percent));
		
		if (offsetTime > 0)
		{
			if (offsetTime >= particle.currentLife)
				offsetTime = particle.currentLife - 1;
			
			updateParticle(particle, offsetTime / 1000f, offsetTime);
		}
	}
	
	//--------------------------------------------------------------------------

	private boolean updateParticle(Particle particle, float delta, int deltaMillis)
	{
		int life_val = particle.currentLife - deltaMillis;
		
		if (life_val <= 0)
			return false;
		
		particle.currentLife = life_val;

		float percent = 1 - particle.currentLife / (float) particle.life;
		int updateFlags = this.updateFlags;

		if ((updateFlags & UPDATE_SCALE) != 0)
			particle.setScale(particle.scale + particle.scaleDiff * getDefinition().getScaleValue().getScale(percent));

		if ((updateFlags & UPDATE_VELOCITY) != 0)
		{
			float velocity = (particle.velocity + particle.velocityDiff * getDefinition().getVelocityValue().getScale(percent)) * delta;

			float velocityX, velocityY;
			
			if ((updateFlags & UPDATE_ANGLE) != 0)
			{
				float angle = particle.angle + particle.angleDiff * getDefinition().getAngleValue().getScale(percent);
				velocityX = velocity * MathUtils.cosDeg(angle);
				velocityY = velocity * MathUtils.sinDeg(angle);
				
				if ((updateFlags & UPDATE_ROTATION) != 0)
				{
					float rotation = particle.rotation + particle.rotationDiff * getDefinition().getRotationValue().getScale(percent);
					
					if (getDefinition().isAligned())
						rotation += angle;
					
					particle.setRotation(rotation);
				}
			}
			else
			{
				velocityX = velocity * particle.angleCos;
				velocityY = velocity * particle.angleSin;
				
				if (getDefinition().isAligned() || (updateFlags & UPDATE_ROTATION) != 0)
				{
					float rotation = particle.rotation + particle.rotationDiff * getDefinition().getRotationValue().getScale(percent);
					if (getDefinition().isAligned())
						rotation += particle.angle;
					
					particle.setRotation(rotation);
				}
			}

			if ((updateFlags & UPDATE_WIND) != 0)
				velocityX += (particle.wind + particle.windDiff * getDefinition().getWindValue().getScale(percent)) * delta;

			if ((updateFlags & UPDATE_GRAVITY) != 0)
				velocityY += (particle.gravity + particle.gravityDiff * getDefinition().getGravityValue().getScale(percent)) * delta;

			particle.translate(velocityX, velocityY);
		}
		else if ((updateFlags & UPDATE_ROTATION) != 0)
		{
			particle.setRotation(particle.rotation + particle.rotationDiff * getDefinition().getRotationValue().getScale(percent));
		}
		
		float[] color;
		
		if ((updateFlags & UPDATE_TINT) != 0)
			color = getDefinition().getTintValue().getColor(percent);
		else
			color = particle.tint;

		if (getDefinition().isPremultipliedAlpha())
		{
			float alphaMultiplier = getDefinition().isAdditive() ? 0 : 1;
			float a = particle.transparency + particle.transparencyDiff * getDefinition().getTransparencyValue().getScale(percent);
			particle.setColor(color[0] * a, color[1] * a, color[2] * a, a * alphaMultiplier);
		}
		else
		{
			particle.setColor(color[0], color[1], color[2], particle.transparency + particle.transparencyDiff * getDefinition().getTransparencyValue().getScale(percent));
		}
	
		return true;
	}
	
	//--------------------------------------------------------------------------

	public void setPosition(float x, float y)
	{
		if (getDefinition().isAttached())
		{
			float xAmount = x - this.x;
			float yAmount = y - this.y;
			
			boolean[] active = this.active;
			
			for (int i = 0, n = active.length; i < n; i++)
				if (active[i])
					particles[i].translate(xAmount, yAmount);
		}
		
		this.x = x;
		this.y = y;
	}
	
	//--------------------------------------------------------------------------

	public void setSprite(Sprite sprite)
	{
		this.sprite = sprite;
			
		if (sprite == null)
			return;
		
		float originX = sprite.getOriginX();
		float originY = sprite.getOriginY();
		Texture texture = sprite.getTexture();
		
		for (int i = 0, n = particles.length; i < n; i++)
		{
			Particle particle = particles[i];
			
			if (particle == null)
				break;
			
			particle.setTexture(texture);
			particle.setOrigin(originX, originY);
		}
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Ignores the {@link #setContinuous(boolean) getDefinition().isContinuous()} setting until the emitter is started again. This allows
	 * the emitter to stop smoothly.
	 */
	public void allowCompletion()
	{
		allowCompletion = true;
		durationTimer = duration;
	}
	
	//--------------------------------------------------------------------------

	public Sprite getSprite()
	{
		return sprite;
	}
	
	//--------------------------------------------------------------------------

	/**
	 * @return Whether this ParticleEmitter automatically returns the
	 * {@link com.badlogic.gdx.graphics.g2d.Batch Batch}'s blend function to the alpha-blending default (GL_SRC_ALPHA,
	 * GL_ONE_MINUS_SRC_ALPHA) when done drawing.
	 */
	public boolean cleansUpBlendFunction()
	{
		return cleansUpBlendFunction;
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Set whether to automatically return the {@link com.badlogic.gdx.graphics.g2d.Batch Batch}'s blend function to the
	 * alpha-blending default (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) when done drawing. Is true by default. If set to
	 * false, the Batch's blend function is left as it was for drawing this ParticleEmitter, which prevents the Batch
	 * from being flushed repeatedly if consecutive ParticleEmitters with the same getDefinition().isAdditive() or pre-multiplied alpha
	 * state are drawn in a row.
	 * <p>
	 * IMPORTANT: If set to false and if the next object to use this Batch expects alpha blending, you are responsible
	 * for setting the Batch's blend function to (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) before that next object is
	 * drawn.
	 *
	 * @param cleansUpBlendFunction
	 */
	public void setCleansUpBlendFunction(boolean cleansUpBlendFunction)
	{
		this.cleansUpBlendFunction = cleansUpBlendFunction;
	}

	//--------------------------------------------------------------------------

	public boolean isComplete()
	{
		if (getDefinition().isContinuous() && !allowCompletion)
			return false;
		
		if (delayTimer < delay)
			return false;
		
		return durationTimer >= duration && activeCount == 0;
	}
	
	//--------------------------------------------------------------------------
	
	public float getPercentComplete()
	{
		if (delayTimer < delay)
			return 0;
		return Math.min(1, durationTimer / (float) duration);
	}
	
	//--------------------------------------------------------------------------

	public float getX()
	{
		return x;
	}
	
	//--------------------------------------------------------------------------

	public float getY()
	{
		return y;
	}
	
	//--------------------------------------------------------------------------

	public int getActiveCount()
	{
		return activeCount;
	}
	
	//--------------------------------------------------------------------------

	public String getImagePath()
	{
		return imagePath;
	}

	public void setImagePath(String imagePath)
	{
		this.imagePath = imagePath;
	}
	
	//--------------------------------------------------------------------------

	public void setFlip(boolean flipX, boolean flipY)
	{
		this.flipX = flipX;
		this.flipY = flipY;
		if (particles == null)
			return;
		for (int i = 0, n = particles.length; i < n; i++)
		{
			Particle particle = particles[i];
			if (particle != null)
				particle.flip(flipX, flipY);
		}
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Returns the bounding box for all active particles. z axis will always be zero.
	 */
	public BoundingBox getBoundingBox()
	{
		if (bounds == null)
			bounds = new BoundingBox();

		Particle[] particles = this.particles;
		boolean[] active = this.active;
		BoundingBox bounds = this.bounds;

		bounds.inf();
		for (int i = 0, n = active.length; i < n; i++)
			if (active[i])
			{
				Rectangle r = particles[i].getBoundingRectangle();
				bounds.ext(r.x, r.y, 0);
				bounds.ext(r.x + r.width, r.y + r.height, 0);
			}

		return bounds;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void load(LoadCapsule capsule)
	{
		throw new UnsupportedOperationException("Particle emitters do not save their state.");
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void save(SaveCapsule capsule) throws ValueAlreadySetException
	{
		throw new UnsupportedOperationException("Particle emitters do not save their state."); 
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void destroy()
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void handleMessage(Message message)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
}
