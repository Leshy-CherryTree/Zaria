/****************************************/
/* SpawnShapeValue.java					*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

/**
 * 
 * Branched from libGDX particle system.
 */
public class SpawnShapeValue extends ParticleValue
{
	//--------------------------------------------------------------------------
	
	private SpawnShape shape = SpawnShape.Point;
	private boolean edges = false;
	private SpawnEllipseSide side = SpawnEllipseSide.Both;

	//--------------------------------------------------------------------------
	
	public SpawnShape getShape()
	{
		return shape;
	}
	
	//--------------------------------------------------------------------------

	public boolean isEdges()
	{
		return edges;
	}
	
	//--------------------------------------------------------------------------

	public SpawnEllipseSide getSide()
	{
		return side;
	}

	//--------------------------------------------------------------------------
}
