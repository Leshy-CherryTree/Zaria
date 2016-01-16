/****************************************/
/* PaletteBranch.java					*/
/* Created on: 09-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.palette;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface PaletteBranch
{
	//--------------------------------------------------------------------------
	
	public Object getChild(int index);
	public int getChildCount();
	public int getIndexOfChild(Object object);
	
	//--------------------------------------------------------------------------
}
