/****************************************/
/* FileListModel.java					*/
/* Created on: 03-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import javax.swing.AbstractListModel;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class FileListModel extends AbstractListModel
{	
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;

	//--------------------------------------------------------------------------
	
	protected File directory;
	protected File [] files = null;
	protected FilenameFilter filter;
	
	private boolean showSize;
	
	//--------------------------------------------------------------------------
	
	public FileListModel(FilenameFilter filter, boolean showSize)
	{
		this.showSize = showSize;
		this.filter = filter;
	}		
	
	//--------------------------------------------------------------------------
	
	public void setDirectory(File file)
	{
		directory = file;
		
		files = file.listFiles(filter);
		
		if(files != null)
			Arrays.sort(files);
		
		fireContentsChanged(this,0,getSize());
	}
		
	//--------------------------------------------------------------------------
	
	public void refresh()
	{
		setDirectory(directory);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public int getSize()
	{
		return files == null ? 0 : files.length;
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getElementAt(int i)
	{
		return new FileNameWrapper(files[i], showSize);
	}

	//--------------------------------------------------------------------------
}
