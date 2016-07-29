/****************************************/
/* ParticleEmitter.java					*/
/* Created on: 25-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.collision.BoundingBox;
import eu.cherrytree.zaria.utilities.Random;

/**
 * 
 * Branched from libGDX particle system.
 */
public class ParticleEmitter
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

	private RangedNumericValue delayValue = new RangedNumericValue();
	private ScaledNumericValue lifeOffsetValue = new ScaledNumericValue();
	private RangedNumericValue durationValue = new RangedNumericValue();
	private ScaledNumericValue lifeValue = new ScaledNumericValue();
	private ScaledNumericValue emissionValue = new ScaledNumericValue();
	private ScaledNumericValue scaleValue = new ScaledNumericValue();
	private ScaledNumericValue rotationValue = new ScaledNumericValue();
	private ScaledNumericValue velocityValue = new ScaledNumericValue();
	private ScaledNumericValue angleValue = new ScaledNumericValue();
	private ScaledNumericValue windValue = new ScaledNumericValue();
	private ScaledNumericValue gravityValue = new ScaledNumericValue();
	private ScaledNumericValue transparencyValue = new ScaledNumericValue();
	private GradientColorValue tintValue = new GradientColorValue();
	private RangedNumericValue xOffsetValue = new ScaledNumericValue();
	private RangedNumericValue yOffsetValue = new ScaledNumericValue();
	private ScaledNumericValue spawnWidthValue = new ScaledNumericValue();
	private ScaledNumericValue spawnHeightValue = new ScaledNumericValue();
	private SpawnShapeValue spawnShapeValue = new SpawnShapeValue();

	private float accumulator;
	private Sprite sprite;
	private Particle[] particles;
	private int minParticleCount, maxParticleCount = 4;
	private float x, y;
	private String name;
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

	private boolean attached;
	private boolean continuous;
	private boolean aligned;
	private boolean behind;
	private boolean additive = true;
	private boolean premultipliedAlpha = false;
	private boolean cleansUpBlendFunction = true;
	
	//--------------------------------------------------------------------------

	public ParticleEmitter()
	{
		initialize();
	}
	
	//--------------------------------------------------------------------------

	public ParticleEmitter(BufferedReader reader) throws IOException
	{
		initialize();
		load(reader);
	}
	
	//--------------------------------------------------------------------------

	public ParticleEmitter(ParticleEmitter emitter)
	{
		sprite = emitter.sprite;
		name = emitter.name;
		imagePath = emitter.imagePath;
		setMaxParticleCount(emitter.maxParticleCount);
		minParticleCount = emitter.minParticleCount;
		delayValue.load(emitter.delayValue);
		durationValue.load(emitter.durationValue);
		emissionValue.load(emitter.emissionValue);
		lifeValue.load(emitter.lifeValue);
		lifeOffsetValue.load(emitter.lifeOffsetValue);
		scaleValue.load(emitter.scaleValue);
		rotationValue.load(emitter.rotationValue);
		velocityValue.load(emitter.velocityValue);
		angleValue.load(emitter.angleValue);
		windValue.load(emitter.windValue);
		gravityValue.load(emitter.gravityValue);
		transparencyValue.load(emitter.transparencyValue);
		tintValue.load(emitter.tintValue);
		xOffsetValue.load(emitter.xOffsetValue);
		yOffsetValue.load(emitter.yOffsetValue);
		spawnWidthValue.load(emitter.spawnWidthValue);
		spawnHeightValue.load(emitter.spawnHeightValue);
		spawnShapeValue.load(emitter.spawnShapeValue);
		attached = emitter.attached;
		continuous = emitter.continuous;
		aligned = emitter.aligned;
		behind = emitter.behind;
		additive = emitter.additive;
		premultipliedAlpha = emitter.premultipliedAlpha;
		cleansUpBlendFunction = emitter.cleansUpBlendFunction;
	}
	
	//--------------------------------------------------------------------------

	private void initialize()
	{
		durationValue.setAlwaysActive(true);
		emissionValue.setAlwaysActive(true);
		lifeValue.setAlwaysActive(true);
		scaleValue.setAlwaysActive(true);
		transparencyValue.setAlwaysActive(true);
		spawnShapeValue.setAlwaysActive(true);
		spawnWidthValue.setAlwaysActive(true);
		spawnHeightValue.setAlwaysActive(true);
	}
	
	//--------------------------------------------------------------------------

	public final void setMaxParticleCount(int maxParticleCount)
	{
		this.maxParticleCount = maxParticleCount;
		active = new boolean[maxParticleCount];
		activeCount = 0;
		particles = new Particle[maxParticleCount];
	}
	
	//--------------------------------------------------------------------------

	public void addParticle()
	{
		int active_count = this.activeCount;
		if (active_count == maxParticleCount)
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
		count = Math.min(count, maxParticleCount - activeCount);
		
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
			else if (!continuous || allowCompletion)
				done = true;
			else
				restart();

			if (!done)
			{
				emissionDelta += deltaMillis;
				float emissionTime = emission + emissionDiff * emissionValue.getScale(durationTimer / (float) duration);
				
				if (emissionTime > 0)
				{
					emissionTime = 1000 / emissionTime;
					if (emissionDelta >= emissionTime)
					{
						int emitCount = (int) (emissionDelta / emissionTime);
						emitCount = Math.min(emitCount, maxParticleCount - activeCount);
						emissionDelta -= emitCount * emissionTime;
						emissionDelta %= emissionTime;
						addParticles(emitCount);
					}
				}
				
				if (activeCount < minParticleCount)
					addParticles(minParticleCount - activeCount);
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
		if (premultipliedAlpha)
		{
			batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		}
		else if (additive)
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

		if (cleansUpBlendFunction && (additive || premultipliedAlpha))
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

		if (premultipliedAlpha)
			batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
		else if (additive)
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

		if (cleansUpBlendFunction && (additive || premultipliedAlpha))
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
			if (!continuous || allowCompletion)
				return;

			restart();
		}

		emissionDelta += deltaMillis;
		float emissionTime = emission + emissionDiff * emissionValue.getScale(durationTimer / (float) duration);
		
		if (emissionTime > 0)
		{
			emissionTime = 1000 / emissionTime;
			
			if (emissionDelta >= emissionTime)
			{
				int emitCount = (int) (emissionDelta / emissionTime);
				emitCount = Math.min(emitCount, maxParticleCount - activeCount);
				emissionDelta -= emitCount * emissionTime;
				emissionDelta %= emissionTime;
				addParticles(emitCount);
			}
		}
		
		if (activeCount < minParticleCount)
			addParticles(minParticleCount - activeCount);
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
		delay = delayValue.active ? delayValue.newLowValue() : 0;
		delayTimer = 0;

		durationTimer -= duration;
		duration = durationValue.newLowValue();

		emission = (int) emissionValue.newLowValue();
		emissionDiff = (int) emissionValue.newHighValue();
		
		if (!emissionValue.isRelative())
			emissionDiff -= emission;

		life = (int) lifeValue.newLowValue();
		lifeDiff = (int) lifeValue.newHighValue();
		
		if (!lifeValue.isRelative())
			lifeDiff -= life;

		lifeOffset = lifeOffsetValue.active ? (int) lifeOffsetValue.newLowValue() : 0;
		lifeOffsetDiff = (int) lifeOffsetValue.newHighValue();
		
		if (!lifeOffsetValue.isRelative())
			lifeOffsetDiff -= lifeOffset;

		spawnWidth = spawnWidthValue.newLowValue();
		spawnWidthDiff = spawnWidthValue.newHighValue();
		
		if (!spawnWidthValue.isRelative())
			spawnWidthDiff -= spawnWidth;

		spawnHeight = spawnHeightValue.newLowValue();
		spawnHeightDiff = spawnHeightValue.newHighValue();
		
		if (!spawnHeightValue.isRelative())
			spawnHeightDiff -= spawnHeight;

		updateFlags = 0;
		
		if (angleValue.active && angleValue.getTimeline().length > 1)
			updateFlags |= UPDATE_ANGLE;

		if (velocityValue.active)
			updateFlags |= UPDATE_VELOCITY;

		if (scaleValue.getTimeline().length > 1)
			updateFlags |= UPDATE_SCALE;
			
		if (rotationValue.active && rotationValue.getTimeline().length > 1)
			updateFlags |= UPDATE_ROTATION;

		if (windValue.active)
			updateFlags |= UPDATE_WIND;

		if (gravityValue.active)
			updateFlags |= UPDATE_GRAVITY;
		
		if (tintValue.getTimeline().length > 1)
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

		particle.currentLife = particle.life = life + (int) (lifeDiff * lifeValue.getScale(percent));

		if (velocityValue.active)
		{
			particle.velocity = velocityValue.newLowValue();
			particle.velocityDiff = velocityValue.newHighValue();

			if (!velocityValue.isRelative())
				particle.velocityDiff -= particle.velocity;
		}

		particle.angle = angleValue.newLowValue();
		particle.angleDiff = angleValue.newHighValue();

		if (!angleValue.isRelative())
			particle.angleDiff -= particle.angle;

		float angle = 0;

		if ((updateFlags & UPDATE_ANGLE) == 0)
		{
			angle = particle.angle + particle.angleDiff * angleValue.getScale(0);
			particle.angle = angle;
			particle.angleCos = MathUtils.cosDeg(angle);
			particle.angleSin = MathUtils.sinDeg(angle);
		}

		float spriteWidth = sprite.getWidth();
		particle.scale = scaleValue.newLowValue() / spriteWidth;
		particle.scaleDiff = scaleValue.newHighValue() / spriteWidth;
		
		if (!scaleValue.isRelative())
			particle.scaleDiff -= particle.scale;
		
		particle.setScale(particle.scale + particle.scaleDiff * scaleValue.getScale(0));

		if (rotationValue.active)
		{
			particle.rotation = rotationValue.newLowValue();
			particle.rotationDiff = rotationValue.newHighValue();
			if (!rotationValue.isRelative())
				particle.rotationDiff -= particle.rotation;
			float rotation = particle.rotation + particle.rotationDiff * rotationValue.getScale(0);
			if (aligned)
				rotation += angle;
			particle.setRotation(rotation);
		}

		if (windValue.active)
		{
			particle.wind = windValue.newLowValue();
			particle.windDiff = windValue.newHighValue();
			if (!windValue.isRelative())
				particle.windDiff -= particle.wind;
			
			System.out.println("wind: " + particle.wind);
			System.out.println("diff: " + particle.windDiff);
		}

		if (gravityValue.active)
		{
			particle.gravity = gravityValue.newLowValue();
			particle.gravityDiff = gravityValue.newHighValue();
			if (!gravityValue.isRelative())
				particle.gravityDiff -= particle.gravity;
		}

		float[] color = particle.tint;
		
		if (color == null)
			particle.tint = color = new float[3];
		
		float[] temp = tintValue.getColor(0);
		color[0] = temp[0];
		color[1] = temp[1];
		color[2] = temp[2];

		particle.transparency = transparencyValue.newLowValue();
		particle.transparencyDiff = transparencyValue.newHighValue() - particle.transparency;

		// Spawn.
		float x = this.x;
		
		if (xOffsetValue.active)
			x += xOffsetValue.newLowValue();
		
		float y = this.y;
		
		if (yOffsetValue.active)
			y += yOffsetValue.newLowValue();
		
		switch (spawnShapeValue.getShape())
		{
			case Square:
			{
				float width = spawnWidth + (spawnWidthDiff * spawnWidthValue.getScale(percent));
				float height = spawnHeight + (spawnHeightDiff * spawnHeightValue.getScale(percent));
				x += MathUtils.random(width) - width / 2;
				y += MathUtils.random(height) - height / 2;
				break;
			}
			
			case Ellipse:
			{
				float width = spawnWidth + (spawnWidthDiff * spawnWidthValue.getScale(percent));
				float height = spawnHeight + (spawnHeightDiff * spawnHeightValue.getScale(percent));
				float radiusX = width / 2;
				float radiusY = height / 2;
				
				if (radiusX == 0 || radiusY == 0)
					break;
				
				float scaleY = radiusX / (float) radiusY;
				
				if (spawnShapeValue.isEdges())
				{
					float spawnAngle;
					switch (spawnShapeValue.getSide())
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
				float width = spawnWidth + (spawnWidthDiff * spawnWidthValue.getScale(percent));
				float height = spawnHeight + (spawnHeightDiff * spawnHeightValue.getScale(percent));
				
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

		int offsetTime = (int) (lifeOffset + lifeOffsetDiff * lifeOffsetValue.getScale(percent));
		
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
			particle.setScale(particle.scale + particle.scaleDiff * scaleValue.getScale(percent));

		if ((updateFlags & UPDATE_VELOCITY) != 0)
		{
			float velocity = (particle.velocity + particle.velocityDiff * velocityValue.getScale(percent)) * delta;

			float velocityX, velocityY;
			
			if ((updateFlags & UPDATE_ANGLE) != 0)
			{
				float angle = particle.angle + particle.angleDiff * angleValue.getScale(percent);
				velocityX = velocity * MathUtils.cosDeg(angle);
				velocityY = velocity * MathUtils.sinDeg(angle);
				
				if ((updateFlags & UPDATE_ROTATION) != 0)
				{
					float rotation = particle.rotation + particle.rotationDiff * rotationValue.getScale(percent);
					
					if (aligned)
						rotation += angle;
					
					particle.setRotation(rotation);
				}
			}
			else
			{
				velocityX = velocity * particle.angleCos;
				velocityY = velocity * particle.angleSin;
				
				if (aligned || (updateFlags & UPDATE_ROTATION) != 0)
				{
					float rotation = particle.rotation + particle.rotationDiff * rotationValue.getScale(percent);
					if (aligned)
						rotation += particle.angle;
					
					particle.setRotation(rotation);
				}
			}

			if ((updateFlags & UPDATE_WIND) != 0)
				velocityX += (particle.wind + particle.windDiff * windValue.getScale(percent)) * delta;

			if ((updateFlags & UPDATE_GRAVITY) != 0)
				velocityY += (particle.gravity + particle.gravityDiff * gravityValue.getScale(percent)) * delta;

			particle.translate(velocityX, velocityY);
		}
		else if ((updateFlags & UPDATE_ROTATION) != 0)
		{
			particle.setRotation(particle.rotation + particle.rotationDiff * rotationValue.getScale(percent));
		}
		
		float[] color;
		
		if ((updateFlags & UPDATE_TINT) != 0)
			color = tintValue.getColor(percent);
		else
			color = particle.tint;

		if (premultipliedAlpha)
		{
			float alphaMultiplier = additive ? 0 : 1;
			float a = particle.transparency + particle.transparencyDiff * transparencyValue.getScale(percent);
			particle.setColor(color[0] * a, color[1] * a, color[2] * a, a * alphaMultiplier);
		}
		else
		{
			particle.setColor(color[0], color[1], color[2], particle.transparency + particle.transparencyDiff * transparencyValue.getScale(percent));
		}
	
		return true;
	}
	
	//--------------------------------------------------------------------------

	public void setPosition(float x, float y)
	{
		if (attached)
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
	 * Ignores the {@link #setContinuous(boolean) continuous} setting until the emitter is started again. This allows
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

	public String getName()
	{
		return name;
	}
	
	//--------------------------------------------------------------------------

	public void setName(String name)
	{
		this.name = name;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getLife()
	{
		return lifeValue;
	}

	public ScaledNumericValue getScale()
	{
		return scaleValue;
	}

	public ScaledNumericValue getRotation()
	{
		return rotationValue;
	}

	public GradientColorValue getTint()
	{
		return tintValue;
	}

	public ScaledNumericValue getVelocity()
	{
		return velocityValue;
	}

	public ScaledNumericValue getWind()
	{
		return windValue;
	}

	public ScaledNumericValue getGravity()
	{
		return gravityValue;
	}

	public ScaledNumericValue getAngle()
	{
		return angleValue;
	}

	public ScaledNumericValue getEmission()
	{
		return emissionValue;
	}

	public ScaledNumericValue getTransparency()
	{
		return transparencyValue;
	}

	public RangedNumericValue getDuration()
	{
		return durationValue;
	}

	public RangedNumericValue getDelay()
	{
		return delayValue;
	}

	public ScaledNumericValue getLifeOffset()
	{
		return lifeOffsetValue;
	}

	public RangedNumericValue getXOffsetValue()
	{
		return xOffsetValue;
	}

	public RangedNumericValue getYOffsetValue()
	{
		return yOffsetValue;
	}

	public ScaledNumericValue getSpawnWidth()
	{
		return spawnWidthValue;
	}

	public ScaledNumericValue getSpawnHeight()
	{
		return spawnHeightValue;
	}

	public SpawnShapeValue getSpawnShape()
	{
		return spawnShapeValue;
	}
	
	

	public boolean isAttached()
	{
		return attached;
	}

	public void setAttached(boolean attached)
	{
		this.attached = attached;
	}

	public boolean isContinuous()
	{
		return continuous;
	}

	public void setContinuous(boolean continuous)
	{
		this.continuous = continuous;
	}

	public boolean isAligned()
	{
		return aligned;
	}

	public void setAligned(boolean aligned)
	{
		this.aligned = aligned;
	}

	public boolean isAdditive()
	{
		return additive;
	}

	public void setAdditive(boolean additive)
	{
		this.additive = additive;
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
	 * from being flushed repeatedly if consecutive ParticleEmitters with the same additive or pre-multiplied alpha
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

	public boolean isBehind()
	{
		return behind;
	}
	
	//--------------------------------------------------------------------------

	public void setBehind(boolean behind)
	{
		this.behind = behind;
	}

	//--------------------------------------------------------------------------
	
	public boolean isPremultipliedAlpha()
	{
		return premultipliedAlpha;
	}
	
	//--------------------------------------------------------------------------

	public void setPremultipliedAlpha(boolean premultipliedAlpha)
	{
		this.premultipliedAlpha = premultipliedAlpha;
	}
	
	//--------------------------------------------------------------------------

	public int getMinParticleCount()
	{
		return minParticleCount;
	}

	public void setMinParticleCount(int minParticleCount)
	{
		this.minParticleCount = minParticleCount;
	}

	public int getMaxParticleCount()
	{
		return maxParticleCount;
	}
	
	//--------------------------------------------------------------------------

	public boolean isComplete()
	{
		if (continuous && !allowCompletion)
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

	public void flipY()
	{
		angleValue.setHigh(-angleValue.getHighMin(), -angleValue.getHighMax());
		angleValue.setLow(-angleValue.getLowMin(), -angleValue.getLowMax());

		gravityValue.setHigh(-gravityValue.getHighMin(), -gravityValue.getHighMax());
		gravityValue.setLow(-gravityValue.getLowMin(), -gravityValue.getLowMax());

		windValue.setHigh(-windValue.getHighMin(), -windValue.getHighMax());
		windValue.setLow(-windValue.getLowMin(), -windValue.getLowMax());

		rotationValue.setHigh(-rotationValue.getHighMin(), -rotationValue.getHighMax());
		rotationValue.setLow(-rotationValue.getLowMin(), -rotationValue.getLowMax());

		yOffsetValue.setLow(-yOffsetValue.getLowMin(), -yOffsetValue.getLowMax());
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

	public void save(Writer output) throws IOException
	{
		output.write(name + "\n");
		output.write("- Delay -\n");
		delayValue.save(output);
		output.write("- Duration - \n");
		durationValue.save(output);
		output.write("- Count - \n");
		output.write("min: " + minParticleCount + "\n");
		output.write("max: " + maxParticleCount + "\n");
		output.write("- Emission - \n");
		emissionValue.save(output);
		output.write("- Life - \n");
		lifeValue.save(output);
		output.write("- Life Offset - \n");
		lifeOffsetValue.save(output);
		output.write("- X Offset - \n");
		xOffsetValue.save(output);
		output.write("- Y Offset - \n");
		yOffsetValue.save(output);
		output.write("- Spawn Shape - \n");
		spawnShapeValue.save(output);
		output.write("- Spawn Width - \n");
		spawnWidthValue.save(output);
		output.write("- Spawn Height - \n");
		spawnHeightValue.save(output);
		output.write("- Scale - \n");
		scaleValue.save(output);
		output.write("- Velocity - \n");
		velocityValue.save(output);
		output.write("- Angle - \n");
		angleValue.save(output);
		output.write("- Rotation - \n");
		rotationValue.save(output);
		output.write("- Wind - \n");
		windValue.save(output);
		output.write("- Gravity - \n");
		gravityValue.save(output);
		output.write("- Tint - \n");
		tintValue.save(output);
		output.write("- Transparency - \n");
		transparencyValue.save(output);
		output.write("- Options - \n");
		output.write("attached: " + attached + "\n");
		output.write("continuous: " + continuous + "\n");
		output.write("aligned: " + aligned + "\n");
		output.write("additive: " + additive + "\n");
		output.write("behind: " + behind + "\n");
		output.write("premultipliedAlpha: " + premultipliedAlpha + "\n");
		output.write("- Image Path -\n");
		output.write(imagePath + "\n");
	}

	public final void load(BufferedReader reader) throws IOException
	{
		try
		{
			name = readString(reader, "name");
			reader.readLine();
			delayValue.load(reader);
			reader.readLine();
			durationValue.load(reader);
			reader.readLine();
			setMinParticleCount(readInt(reader, "minParticleCount"));
			setMaxParticleCount(readInt(reader, "maxParticleCount"));
			reader.readLine();
			emissionValue.load(reader);
			reader.readLine();
			lifeValue.load(reader);
			reader.readLine();
			lifeOffsetValue.load(reader);
			reader.readLine();
			xOffsetValue.load(reader);
			reader.readLine();
			yOffsetValue.load(reader);
			reader.readLine();
			spawnShapeValue.load(reader);
			reader.readLine();
			spawnWidthValue.load(reader);
			reader.readLine();
			spawnHeightValue.load(reader);
			reader.readLine();
			scaleValue.load(reader);
			reader.readLine();
			velocityValue.load(reader);
			reader.readLine();
			angleValue.load(reader);
			reader.readLine();
			rotationValue.load(reader);
			reader.readLine();
			windValue.load(reader);
			reader.readLine();
			gravityValue.load(reader);
			reader.readLine();
			tintValue.load(reader);
			reader.readLine();
			transparencyValue.load(reader);
			reader.readLine();
			attached = readBoolean(reader, "attached");
			continuous = readBoolean(reader, "continuous");
			aligned = readBoolean(reader, "aligned");
			additive = readBoolean(reader, "additive");
			behind = readBoolean(reader, "behind");

			// Backwards compatibility
			String line = reader.readLine();
			if (line.startsWith("premultipliedAlpha"))
			{
				premultipliedAlpha = readBoolean(line);
				reader.readLine();
			}
			setImagePath(reader.readLine());
		}
		catch (RuntimeException ex)
		{
			if (name == null)
				throw ex;
			throw new RuntimeException("Error parsing emitter: " + name, ex);
		}
	}

	static String readString(String line) throws IOException
	{
		return line.substring(line.indexOf(":") + 1).trim();
	}

	static String readString(BufferedReader reader, String name) throws IOException
	{
		String line = reader.readLine();
		if (line == null)
			throw new IOException("Missing value: " + name);
		return readString(line);
	}

	static boolean readBoolean(String line) throws IOException
	{
		return Boolean.parseBoolean(readString(line));
	}

	static boolean readBoolean(BufferedReader reader, String name) throws IOException
	{
		return Boolean.parseBoolean(readString(reader, name));
	}

	static int readInt(BufferedReader reader, String name) throws IOException
	{
		return Integer.parseInt(readString(reader, name));
	}

	static float readFloat(BufferedReader reader, String name) throws IOException
	{
		return Float.parseFloat(readString(reader, name));
	}
	
	//--------------------------------------------------------------------------
}
