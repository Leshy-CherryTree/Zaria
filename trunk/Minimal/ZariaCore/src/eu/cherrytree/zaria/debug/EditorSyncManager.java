/****************************************/
/* SyncManager.java						*/
/* Created on: 02-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.debug;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinitionLibrary;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface EditorSyncManager
{
	public void update();
	
	public void init();
	public void deinit();
	
	public void addLibrary(ZariaObjectDefinitionLibrary library);
	public void removeLibrary(ZariaObjectDefinitionLibrary library);
}
