/****************************************/
/* ParticleEffectDefinition.java			*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import eu.cherrytree.zaria.game.GameObjectDefinition;
import eu.cherrytree.zaria.serialization.ColorName;
import eu.cherrytree.zaria.serialization.DefinitionValidation;
import eu.cherrytree.zaria.serialization.LinkArray;
import eu.cherrytree.zaria.serialization.annotations.DefinitionCategory;
import eu.cherrytree.zaria.serialization.annotations.DefinitionColor;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@DefinitionCategory("Particles")
@DefinitionColor(ColorName.Lavender)
public class ParticleEffectDefinition extends GameObjectDefinition<ParticleEffect>
{
	//--------------------------------------------------------------------------
	
	private LinkArray<ParticleEmitterDefinition> emitters = new LinkArray<>();

	//--------------------------------------------------------------------------
		
	private transient boolean continuous;

	//--------------------------------------------------------------------------
	
	public LinkArray<ParticleEmitterDefinition> getEmitters()
	{
		return emitters;
	}
	
	//--------------------------------------------------------------------------

	public boolean isContinuous()
	{
		return continuous;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public ParticleEffect create(String name)
	{
		return new ParticleEffect(this, name);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onPreLoad()
	{
		continuous = true;
		
		for (ParticleEmitterDefinition def : emitters)
		{
			if (!def.isContinuous())
			{
				continuous = false;
				break;
			}
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onValidate(DefinitionValidation validation)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------
}
