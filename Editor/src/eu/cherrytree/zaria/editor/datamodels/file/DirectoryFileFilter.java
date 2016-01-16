/****************************************/
/* DirectoryFileFilter.java 			*/
/* Created on: 27-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.file;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DirectoryFileFilter implements FileFilter
{	
	//--------------------------------------------------------------------------
	
	private boolean showHidden;
	private FilenameFilter filenameFilter;
	
	//--------------------------------------------------------------------------
	
	public DirectoryFileFilter()
	{
		showHidden = false;
	}
			
	//--------------------------------------------------------------------------

	public DirectoryFileFilter(boolean showHidden, FilenameFilter filenameFilter)
	{
		this.showHidden = showHidden;
		this.filenameFilter = filenameFilter;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean accept(File file)
	{
		return file.isDirectory() && (showHidden || !file.isHidden()) && (filenameFilter == null || file.list(filenameFilter).length != 0);
	}		
	
	//--------------------------------------------------------------------------
}