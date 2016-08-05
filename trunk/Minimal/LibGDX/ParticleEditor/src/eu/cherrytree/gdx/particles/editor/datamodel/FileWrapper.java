/****************************************/
/* FileWrapper.java						*/
/* Created on: 01-08-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor.datamodel;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class FileWrapper
{
	//--------------------------------------------------------------------------
	
	private File file;
	private ArrayList<File> children = new ArrayList<>();

	//--------------------------------------------------------------------------
	
	public FileWrapper(File file)
	{
		this.file = file;
		
		if (file.isDirectory())
		{
			File[] dirs = file.listFiles(new FileFilter()
			{
				@Override
				public boolean accept(File file)
				{
					return file.isDirectory();
				}
			});

			File[] files = file.listFiles(new ZoneFileFilter());

			if (dirs.length > 0)
				children.addAll(Arrays.asList(dirs));

			if (files.length > 0)
				children.addAll(Arrays.asList(files));
		}
	}
	
	//--------------------------------------------------------------------------
	
	public int getChildCount()
	{
		return children.size();
	}
	
	//--------------------------------------------------------------------------
	
	public FileWrapper getChild(int index)
	{
		return new FileWrapper(children.get(index));
	}
	
	//--------------------------------------------------------------------------
	
	public int getIndexOf(FileWrapper fileWrapper)
	{
		return children.indexOf(fileWrapper.file);
	}
	
	//--------------------------------------------------------------------------
	
	public boolean isFile()
	{
		return file.isFile();
	}
	
	//--------------------------------------------------------------------------

	public File getFile()
	{
		return file;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String toString()
	{
		return file.getName();
	}
	
	//--------------------------------------------------------------------------
}
