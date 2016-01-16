/****************************************/
/* AssetFileNameFilter.java 			*/
/* Created on: 27-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.file;

import eu.cherrytree.zaria.editor.document.ZoneDocument;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class AssetFileNameFilter implements FilenameFilter
{
	//--------------------------------------------------------------------------

	@Override
	public boolean accept(File file, String string)
	{
		String str = string.toLowerCase();

		if (ZoneDocument.isZone(str) ||  ZoneDocument.isScript(str))
			return true;

		return false;
	}	
	
	//--------------------------------------------------------------------------
}