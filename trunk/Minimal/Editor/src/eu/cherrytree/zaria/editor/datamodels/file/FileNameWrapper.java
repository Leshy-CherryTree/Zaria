/****************************************/
/* FileNameWrapper.java                 */
/* Created on: 03-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.file;

import java.io.File;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class FileNameWrapper
{
	//--------------------------------------------------------------------------
			
	private File file;
	private String sizeString = "";
	
	//--------------------------------------------------------------------------

	public FileNameWrapper(File file, boolean showSize)
	{		
		this.file = file;
		
		if(file.isFile() && showSize)
		{
			float size = file.length();
			
			String end;
			
			if(size > 1024 * 1024)
			{
				size /= 1024 * 1024;
				
				size *= 100.0f;
				size = (float) Math.ceil(size);
				size /= 100.0f;
				
				end = "mb";
			}
			else if(size > 1024)
			{
				size /= 1024;
				
				size *= 100.0f;
				size = (float) Math.ceil(size);
				size /= 100.0f;
				
				end = "kb";
			}
			else
			{
				end = "b";
			}
			
			sizeString = " [" + size + " " + end + "]";			
		}
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
		return file.getName() + sizeString;
	}
	
	//--------------------------------------------------------------------------
}
