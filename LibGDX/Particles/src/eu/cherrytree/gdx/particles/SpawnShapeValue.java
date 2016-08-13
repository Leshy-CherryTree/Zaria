/****************************************/
/* SpawnShapeValue.java					*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(	fieldVisibility=JsonAutoDetect.Visibility.ANY, 
					getterVisibility=JsonAutoDetect.Visibility.NONE, 
					isGetterVisibility=JsonAutoDetect.Visibility.NONE, 
					setterVisibility=JsonAutoDetect.Visibility.NONE, 
					creatorVisibility=JsonAutoDetect.Visibility.NONE	)

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

	public SpawnShapeValue()
	{
		super(false);
	}
	
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
