/****************************************/
/* ParticleEffectPool.java				*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;


import com.badlogic.gdx.utils.Pool;

/**
 * 
 * Branched from libGDX particle system.
 */
public class ParticleEffectPool extends Pool<ParticleEffectPool.PooledEffect>
{
	//--------------------------------------------------------------------------
	
	public class PooledEffect extends ParticleEffect
	{

		PooledEffect(ParticleEffect effect)
		{
			super(effect);
		}

		@Override
		public void reset()
		{
			super.reset();
		}

		public void free()
		{
			ParticleEffectPool.this.free(this);
		}
	}
		
	//--------------------------------------------------------------------------

	private final ParticleEffect effect;
	
	//--------------------------------------------------------------------------

	public ParticleEffectPool(ParticleEffect effect, int initialCapacity, int max)
	{
		super(initialCapacity, max);
		this.effect = effect;
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected PooledEffect newObject()
	{
		return new PooledEffect(effect);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public PooledEffect obtain()
	{
		PooledEffect obtained = super.obtain();
		obtained.reset();
		
		return obtained;
	}
	
	//--------------------------------------------------------------------------
}
