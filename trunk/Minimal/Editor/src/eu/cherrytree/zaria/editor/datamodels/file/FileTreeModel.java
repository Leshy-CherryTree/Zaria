/****************************************/
/* FileTreeModel.java					*/
/* Created on: 04-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
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
public class FileTreeModel implements TreeModel
{	
	//--------------------------------------------------------------------------
	
	private FileNameWrapper root;
	private FileFilter directoryFilter;

	//--------------------------------------------------------------------------

	public FileTreeModel(FileFilter directoryFilter, String path)
	{
		this.root = new FileNameWrapper(new File(path), false);
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
		return ((FileNameWrapper) node).getFile().isFile();		
	}

	//--------------------------------------------------------------------------

	@Override
	public int getChildCount(Object parent)
	{	
		File[] children = ((FileNameWrapper) parent).getFile().listFiles(directoryFilter);
		
		return children == null ? 0 : children.length;
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getChild(Object parent, int index)
	{		
		File[] children = ((FileNameWrapper) parent).getFile().listFiles(directoryFilter);
		Arrays.sort(children);
		
		return new FileNameWrapper(children[index], true);
	}

	//--------------------------------------------------------------------------

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{		
		File file = ((FileNameWrapper) child).getFile();
		File[] children = ((FileNameWrapper) parent).getFile().listFiles(directoryFilter);
		Arrays.sort(children);

		for (int i = 0 ; i < children.length ; i++)
		{
			if (file.equals(children[i]))
				return i;
		}

		return -1;
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
