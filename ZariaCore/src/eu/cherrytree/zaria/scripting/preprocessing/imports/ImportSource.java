/****************************************/
/* ImportSource.java						*/
/* Created on: 14-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting.preprocessing.imports;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ImportSource
{
	//--------------------------------------------------------------------------
	
	private String source;
	private String importName;
	
	//--------------------------------------------------------------------------

	public ImportSource(String source, String importName)
	{
		this.source = source.trim();
		this.importName = importName.trim();
	}
	
	//--------------------------------------------------------------------------

	public String getSource()
	{
		return source;
	}
	
	//--------------------------------------------------------------------------

	public String getImportName()
	{
		return importName;
	}				
	
	//--------------------------------------------------------------------------
}
