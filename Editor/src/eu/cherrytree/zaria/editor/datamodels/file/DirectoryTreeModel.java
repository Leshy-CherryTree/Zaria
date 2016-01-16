/****************************************/
/* DirectoryTreeModel.java              */
/* Created on: 03-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.file;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DirectoryTreeModel implements TreeModel
{	
	//--------------------------------------------------------------------------
	
	private DirectoriesWrapper root;
	private FileFilter directoryFilter;

	//--------------------------------------------------------------------------

	public DirectoryTreeModel(FileFilter directoryFilter, String ... paths)
	{
		this.root = new DirectoriesWrapper(paths);
		this.directoryFilter = directoryFilter;
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getRoot()
	{
		return root;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isLeaf(Object node)
	{		
		if(node == root)
			return false;
		else
			return ((FileNameWrapper) node).getFile().isFile();		
	}

	//--------------------------------------------------------------------------

	@Override
	public int getChildCount(Object parent)
	{	
		if(parent == root)
		{
			return root.getChildCount();
		}
		else
		{
			File[] children = ((FileNameWrapper) parent).getFile().listFiles(directoryFilter);
		
			return children == null ? 0 : children.length;
		}
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getChild(Object parent, int index)
	{		
		if(parent == root)
		{
			return root.getChild(index);
		}
		else
		{
			File[] children = ((FileNameWrapper) parent).getFile().listFiles(directoryFilter);
			Arrays.sort(children);
		
			return new FileNameWrapper(children[index], true);
		}
	}

	//--------------------------------------------------------------------------

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{		
		if(parent == root)
		{
			return root.getIndexOfChild((FileNameWrapper) child);
		}
		else
		{
			File file = ((FileNameWrapper) child).getFile();
			File[] children = ((FileNameWrapper) parent).getFile().listFiles(directoryFilter);
			Arrays.sort(children);

			for(int i = 0 ; i < children.length ; i++)
			{
				if(file.equals(children[i]))
					return i;
			}

			return -1;
		}
	}

	//--------------------------------------------------------------------------

	@Override
	public void valueForPathChanged(TreePath path, Object newvalue)
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
