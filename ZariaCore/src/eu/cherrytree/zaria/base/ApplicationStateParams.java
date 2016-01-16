/****************************************/
/* ApplicationStateParams.java 			*/
/* Created on: 17-Mar-2015				*/
/* Copyright Cherry Tree Studio 2015	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.base;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ApplicationStateParams
{
	//--------------------------------------------------------------------------
	
	private String libraryPath;
	private String saveFilePath;
	
	//--------------------------------------------------------------------------
	
	public ApplicationStateParams(String libraryPath, String saveFilePath)
	{
		this.libraryPath = libraryPath;
		this.saveFilePath = saveFilePath;
	}

	//--------------------------------------------------------------------------
	
	public String getLibraryPath()
	{
		return libraryPath;
	}
	
	//--------------------------------------------------------------------------
	
	public void setSaveFilePath(String saveFilePath)
	{
		this.saveFilePath = saveFilePath;
	}
	
	//--------------------------------------------------------------------------
	
	public String getSaveFilePath()
	{
		return saveFilePath;
	}
				
	//--------------------------------------------------------------------------	
}
