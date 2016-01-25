/****************************************/
/* AssetFileFilter.java					*/
/* Created on: 09-Feb-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document;

import java.io.File;
import java.util.Locale;
import javax.swing.filechooser.FileFilter;

/**
 * File filter showing only files of specific type in specific directories.
 * 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class AssetFileFilter extends FileFilter
{
	//--------------------------------------------------------------------------
	
    /** Description of this filter. */
    private final String description;
    
	/** Cached extensions. */
    private final String[] extensions;

	//--------------------------------------------------------------------------

	public AssetFileFilter(String description, String[] extensions)
	{		
		if (extensions == null || extensions.length == 0)
			throw new IllegalArgumentException("Extensions must be non-null and not empty");

		this.description = description;
		this.extensions = new String[extensions.length];
		
		for (int i = 0; i < extensions.length; i++)
		{
			if (extensions[i] == null || extensions[i].length() == 0)
				throw new IllegalArgumentException("Each extension must be non-null and not empty");
			
			this.extensions[i] = extensions[i].toLowerCase(Locale.ENGLISH);
		}
	}

	//--------------------------------------------------------------------------
	
	@Override
	public boolean accept(File f)
	{
		if (f != null)
		{
			if (f.isDirectory())
				return true;
			
			for (String extension : extensions)
			{
				if (f.getName().toLowerCase().endsWith(extension))
					return true;
			}
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getDescription()
	{
		String ext = "";
		
		for(int i = 0 ; i < extensions.length ; i++)
		{
			ext += "*" + extensions[i];
			
			if(i < extensions.length -1)
				ext += ", ";
		}
		
		
		return "(" + ext + ") " + description;
	}
	
	//--------------------------------------------------------------------------
}
