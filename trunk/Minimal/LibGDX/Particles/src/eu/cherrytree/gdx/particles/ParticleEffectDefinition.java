/****************************************/
/* ParticleEffectDefinition.java			*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import eu.cherrytree.zaria.game.GameObjectDefinition;
import eu.cherrytree.zaria.serialization.DefinitionValidation;
import eu.cherrytree.zaria.serialization.LinkArray;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ParticleEffectDefinition extends GameObjectDefinition<ParticleEffect>
{
	//--------------------------------------------------------------------------
	
	private LinkArray<ParticleEmitterDefinition> emitters = new LinkArray<>();

	//--------------------------------------------------------------------------
	
	public LinkArray<ParticleEmitterDefinition> getEmitters()
	{
		return emitters;
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
