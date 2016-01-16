/****************************************/
/* PaletteElement.java					*/
/* Created on: 06-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.palette;

import javax.swing.Icon;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class PaletteElement<Type extends PaletteElement> implements Comparable<Type>
{
	//--------------------------------------------------------------------------
	
	private String name;

	//--------------------------------------------------------------------------

	public PaletteElement(String name)
	{
		this.name = name;
	}

	//--------------------------------------------------------------------------

	public String getName()
	{
		return name;
	}		
	
	//--------------------------------------------------------------------------

	@Override
	public String toString()
	{
		return name;
	}
	
	//--------------------------------------------------------------------------
	
	public Icon getIcon()
	{
		return null;
	}

	//--------------------------------------------------------------------------
	
	public String getToolTip()
	{
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public abstract boolean isLeaf();
	
	//--------------------------------------------------------------------------
}
