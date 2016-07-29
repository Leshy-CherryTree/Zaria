/****************************************/
/* SpawnShapeValue.java					*/
/* Created on: 29-07-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

/**
 * 
 * Branched from libGDX particle system.
 */
public class SpawnShapeValue extends ParticleValue
{
	//--------------------------------------------------------------------------
	
	private SpawnShape shape = SpawnShape.Point;
	private boolean edges;
	private SpawnEllipseSide side = SpawnEllipseSide.Both;

	//--------------------------------------------------------------------------
	
	public SpawnShape getShape()
	{
		return shape;
	}
	
	//--------------------------------------------------------------------------

	public void setShape(SpawnShape shape)
	{
		this.shape = shape;
	}
	
	//--------------------------------------------------------------------------

	public boolean isEdges()
	{
		return edges;
	}
	
	//--------------------------------------------------------------------------

	public void setEdges(boolean edges)
	{
		this.edges = edges;
	}
	
	//--------------------------------------------------------------------------

	public SpawnEllipseSide getSide()
	{
		return side;
	}
	
	//--------------------------------------------------------------------------

	public void setSide(SpawnEllipseSide side)
	{
		this.side = side;
	}
	
	//--------------------------------------------------------------------------

	public void save(Writer output) throws IOException
	{
		super.save(output);
		if (!active)
			return;
		output.write("shape: " + shape + "\n");
		if (shape == SpawnShape.Ellipse)
		{
			output.write("edges: " + edges + "\n");
			output.write("side: " + side + "\n");
		}
	}

	public void load(BufferedReader reader) throws IOException
	{
		super.load(reader);
		if (!active)
			return;
		shape = SpawnShape.valueOf(readString(reader, "shape"));
		if (shape == SpawnShape.Ellipse)
		{
			edges = readBoolean(reader, "edges");
			side = SpawnEllipseSide.valueOf(readString(reader, "side"));
		}
	}

	public void load(SpawnShapeValue value)
	{
		super.load(value);
		shape = value.shape;
		edges = value.edges;
		side = value.side;
	}
	
	//--------------------------------------------------------------------------
}
