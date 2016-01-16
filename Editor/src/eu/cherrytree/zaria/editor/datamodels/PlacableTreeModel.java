/****************************************/
/* PlacableTreeModel.java				*/
/* Created on: 20-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/


package eu.cherrytree.zaria.editor.datamodels;

import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class PlacableTreeModel implements TreeModel
{
	//--------------------------------------------------------------------------
	
	private JTree tree;
	
	protected PlacableCategory root = new PlacableCategory("temp_root","");
	
	//--------------------------------------------------------------------------
	
	public PlacableTreeModel(JTree tree)
	{
		this.tree = tree;
	}		
	
	//--------------------------------------------------------------------------
	
	protected void updateTree()
	{
		root.sort();
		tree.updateUI();
		tree.expandPath(new TreePath(root));
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getRoot()
	{
		return root;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getChild(Object object, int i)
	{
		if(PlacableCategory.class.isAssignableFrom(object.getClass()))
		{
			PlacableCategory category = (PlacableCategory) object;
			
			return category.getChild(i);
		}
		else
			return null;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int getChildCount(Object object)
	{
		if(PlacableCategory.class.isAssignableFrom(object.getClass()))
		{
			PlacableCategory category = (PlacableCategory) object;
			
			return category.getChildCount();
		}
		else
			return 0;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean isLeaf(Object object)
	{
		return !PlacableCategory.class.isAssignableFrom(object.getClass());
	}
		
	//--------------------------------------------------------------------------

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		if(PlacableCategory.class.isAssignableFrom(parent.getClass()))
		{
			PlacableCategory category = (PlacableCategory) parent;
			
			return category.getIndexOfChild(child);
		}
		else
			return -1;
	}

	//--------------------------------------------------------------------------

	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------

	@Override
	public void addTreeModelListener(TreeModelListener listener)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------

	@Override
	public void removeTreeModelListener(TreeModelListener listener)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
	
	public void setFilter(String filter)
	{
		root.setFilter(filter);
		updateTree();
	}
	
	//--------------------------------------------------------------------------
}
