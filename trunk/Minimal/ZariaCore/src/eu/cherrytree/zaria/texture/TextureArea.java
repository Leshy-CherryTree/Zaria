/****************************************/
/* TextureArea.java						*/
/* Created on: 25-01-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.texture;

import eu.cherrytree.zaria.serialization.ColorName;
import eu.cherrytree.zaria.serialization.DefinitionValidation;
import eu.cherrytree.zaria.serialization.ResourceType;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.annotations.DefinitionCategory;
import eu.cherrytree.zaria.serialization.annotations.DefinitionColor;
import eu.cherrytree.zaria.serialization.annotations.MinInt;
import eu.cherrytree.zaria.serialization.annotations.Resource;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@DefinitionCategory("Textures")
@DefinitionColor(ColorName.BurlyWood)
public class TextureArea extends ZariaObjectDefinition
{
	//--------------------------------------------------------------------------
	
	@Resource(ResourceType.TEXTURE)
	private String texture  = "";

	@MinInt(0)
	private int x;
	
	@MinInt(0)
	private int y;
	
	@MinInt(1)
	private int w;
	
	@MinInt(1)
	private int h;
	
	//--------------------------------------------------------------------------

	public String getTexture()
	{
		return texture;
	}

	//--------------------------------------------------------------------------
	
	public int getX()
	{
		return x;
	}

	//--------------------------------------------------------------------------
	
	public int getY()
	{
		return y;
	}

	//--------------------------------------------------------------------------
	
	public int getW()
	{
		return w;
	}
	
	//--------------------------------------------------------------------------
	
	public int getH()
	{
		return h;
	}

	
	//--------------------------------------------------------------------------

	@Override
	public void onPreLoad()
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onValidate(DefinitionValidation dv)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------
}
