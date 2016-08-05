/****************************************/
/* FileTree.java							*/
/* Created on: 01-08-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor.datamodel;

import java.io.File;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class FileTree implements TreeModel
{
	//--------------------------------------------------------------------------
	
	private FileWrapper assetFolder;

	//--------------------------------------------------------------------------
	
	public FileTree(String path)
	{
		assetFolder = new FileWrapper(new File(path));
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getRoot()
	{
		return assetFolder;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getChild(Object parent, int index)
	{
		return ((FileWrapper) parent).getChild(index);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int getChildCount(Object parent)
	{
		return ((FileWrapper) parent).getChildCount();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean isLeaf(Object node)
	{
		return ((FileWrapper) node).isFile();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		return ((FileWrapper) parent).getIndexOf((FileWrapper) child);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------
}
