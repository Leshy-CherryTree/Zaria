/****************************************/
/* ParticleEmitterDefinition.java		*/
/* Created on: 31-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import eu.cherrytree.zaria.game.GameObjectDefinition;
import eu.cherrytree.zaria.serialization.ColorName;
import eu.cherrytree.zaria.serialization.DefinitionValidation;
import eu.cherrytree.zaria.serialization.Link;
import eu.cherrytree.zaria.serialization.annotations.DefinitionCategory;
import eu.cherrytree.zaria.serialization.annotations.DefinitionColor;
import eu.cherrytree.zaria.texture.TextureArea;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@DefinitionCategory("Particles")
@DefinitionColor(ColorName.LavenderBlush)
public class ParticleEmitterDefinition extends GameObjectDefinition<ParticleEmitter>
{
	//--------------------------------------------------------------------------
	
	private Link<TextureArea> texture = new Link<>();
	
	private RangedNumericValue delayValue = new RangedNumericValue(0.0f, 0.0f, false);
	private ScaledNumericValue lifeOffsetValue = new ScaledNumericValue(0.0f, 0.0f, 0.0f, 0.0f, false);
	private RangedNumericValue durationValue = new RangedNumericValue(2500, 1500, true);
	private ScaledNumericValue lifeValue = new ScaledNumericValue(2000, 2000, 1000, 1000, true);
	private ScaledNumericValue emissionValue = new ScaledNumericValue(10, 10, 5, 5, true);
	private ScaledNumericValue scaleValue = new ScaledNumericValue(32, 32, 16, 16, false);
	private ScaledNumericValue rotationValue = new ScaledNumericValue(0, 0, 0, 0, false);
	private ScaledNumericValue velocityValue = new ScaledNumericValue(0, 0, 0, 0, false);
	private ScaledNumericValue angleValue = new ScaledNumericValue(0, 0, 0, 0, false);
	private ScaledNumericValue windValue = new ScaledNumericValue(0, 0, 0, 0, false);
	private ScaledNumericValue gravityValue = new ScaledNumericValue(10, 10, 10, 10, false);
	private ScaledNumericValue transparencyValue = new ScaledNumericValue(1, 1, 0, 0, false);
	private GradientColorValue tintValue = new GradientColorValue(new float[]{1.0f, 0.12156863f, 0.047058824f});
	private RangedNumericValue xOffsetValue = new ScaledNumericValue(0 , 0, 0, 0, false);
	private RangedNumericValue yOffsetValue = new ScaledNumericValue(0, 0, 0, 0, false);
	private ScaledNumericValue spawnWidthValue = new ScaledNumericValue(1, 1, 1, 1, false);
	private ScaledNumericValue spawnHeightValue = new ScaledNumericValue(1, 1, 1, 1, false);
	private SpawnShapeValue spawnShapeValue = new SpawnShapeValue();
	
	private boolean attached = false;
	private boolean continuous = false;
	private boolean aligned = false;
	private boolean behind = false;
	private boolean additive = true;
	private boolean premultipliedAlpha = false;
	
	private int minParticleCount = 0;
	private int maxParticleCount = 50;

	//--------------------------------------------------------------------------
	
	public TextureArea getTexture()
	{
		return texture.getDefinition();
	}
			
	//--------------------------------------------------------------------------

	public RangedNumericValue getDelayValue()
	{
		return delayValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getLifeOffsetValue()
	{
		return lifeOffsetValue;
	}
	
	//--------------------------------------------------------------------------

	public RangedNumericValue getDurationValue()
	{
		return durationValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getLifeValue()
	{
		return lifeValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getEmissionValue()
	{
		return emissionValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getScaleValue()
	{
		return scaleValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getRotationValue()
	{
		return rotationValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getVelocityValue()
	{
		return velocityValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getAngleValue()
	{
		return angleValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getWindValue()
	{
		return windValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getGravityValue()
	{
		return gravityValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getTransparencyValue()
	{
		return transparencyValue;
	}
	
	//--------------------------------------------------------------------------

	public GradientColorValue getTintValue()
	{
		return tintValue;
	}
	
	//--------------------------------------------------------------------------

	public RangedNumericValue getXOffsetValue()
	{
		return xOffsetValue;
	}
	
	//--------------------------------------------------------------------------

	public RangedNumericValue getYOffsetValue()
	{
		return yOffsetValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getSpawnWidthValue()
	{
		return spawnWidthValue;
	}
	
	//--------------------------------------------------------------------------

	public ScaledNumericValue getSpawnHeightValue()
	{
		return spawnHeightValue;
	}
	
	//--------------------------------------------------------------------------

	public SpawnShapeValue getSpawnShapeValue()
	{
		return spawnShapeValue;
	}
	
	//--------------------------------------------------------------------------
	
	public boolean isAttached()
	{
		return attached;
	}
		
	//--------------------------------------------------------------------------

	public boolean isContinuous()
	{
		return continuous;
	}
	
	//--------------------------------------------------------------------------

	public boolean isAligned()
	{
		return aligned;
	}
	
	//--------------------------------------------------------------------------

	public boolean isBehind()
	{
		return behind;
	}
	
	//--------------------------------------------------------------------------
	
	public boolean isAdditive()
	{
		return additive;
	}
	
	//--------------------------------------------------------------------------

	public boolean isPremultipliedAlpha()
	{
		return premultipliedAlpha;
	}
	
	//--------------------------------------------------------------------------

	public int getMinParticleCount()
	{
		return minParticleCount;
	}
	
	//--------------------------------------------------------------------------

	public int getMaxParticleCount()
	{
		return maxParticleCount;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public ParticleEmitter create(String name)
	{
		return new ParticleEmitter(this, name);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onPreLoad()
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onValidate(DefinitionValidation validation)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------
}
