/****************************************/
/* PaletteDataModel.java				*/
/* Created on: 06-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.palette;

import java.util.ArrayList;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class PaletteDataModel implements TreeModel
{
	//--------------------------------------------------------------------------
	
	protected PaletteRoot root = new PaletteRoot();
	
	protected ArrayList<TreeModelListener> listeners = new ArrayList<>();
	
	//--------------------------------------------------------------------------
	
	@Override
	public Object getRoot()
	{
		return root;
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getChild(Object o, int i)
	{
		if(o instanceof PaletteBranch)
		{
			return ((PaletteBranch)o).getChild(i);
		}
		else
			return null;
	}

	//--------------------------------------------------------------------------

	@Override
	public int getChildCount(Object o)
	{
		if(o instanceof PaletteBranch)
		{	
			return ((PaletteBranch)o).getChildCount();
		}
		else
			return 0;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isLeaf(Object o)
	{	
		return ((PaletteElement)o).isLeaf();
	}

	//--------------------------------------------------------------------------

	@Override
	public int getIndexOfChild(Object o, Object object)
	{
		if(o instanceof PaletteBranch)
		{
			return ((PaletteBranch)o).getIndexOfChild(object);
		}
		else
			return -1;
	}

	//--------------------------------------------------------------------------
	
	@Override
	public void valueForPathChanged(TreePath tp, Object o)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------

	@Override
	public void addTreeModelListener(TreeModelListener tl)
	{				
		listeners.add(tl);
	}

	//--------------------------------------------------------------------------

	@Override
	public void removeTreeModelListener(TreeModelListener tl)
	{
		listeners.remove(tl);
	}
	
	//--------------------------------------------------------------------------
	
	public abstract void setFilter(String filter);
	
	//--------------------------------------------------------------------------
}
