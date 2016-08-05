/****************************************/
/* ZoneFileFilter.java					*/
/* Created on: 04-08-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor.datamodel;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneFileFilter implements FileFilter
{
	//--------------------------------------------------------------------------
	
	@Override
	public boolean accept(File pathname)
	{
		return pathname.isFile() && pathname.getName().toLowerCase().endsWith("zone");
	}
	
	//--------------------------------------------------------------------------
}
