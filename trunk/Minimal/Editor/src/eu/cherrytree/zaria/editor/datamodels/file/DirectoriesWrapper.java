/****************************************/
/* DirectoriesWrapper.java				*/
/* Created on: 09-Feb-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.file;

import java.io.File;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DirectoriesWrapper
{
	//--------------------------------------------------------------------------
	
	private FileNameWrapper[] directories;
	
	//--------------------------------------------------------------------------

	public DirectoriesWrapper(String ... paths)
	{
		directories = new FileNameWrapper[paths.length];
		
		for(int i = 0 ; i < directories.length ; i++)
			directories[i] = new FileNameWrapper(new File(paths[i]), true);
	}
	
	//--------------------------------------------------------------------------
	
	public int getChildCount()
	{
		return directories.length;
	}
	
	//--------------------------------------------------------------------------
	
	public FileNameWrapper getChild(int index)
	{
		return directories[index];
	}
	
	//--------------------------------------------------------------------------
	
	public int getIndexOfChild(FileNameWrapper child)
	{
		for(int i = 0 ; i < directories.length ; i++)
		{
			if(directories[i] == child)
				return i;
		}
		
		return -1;
	}
			
	//--------------------------------------------------------------------------
}
