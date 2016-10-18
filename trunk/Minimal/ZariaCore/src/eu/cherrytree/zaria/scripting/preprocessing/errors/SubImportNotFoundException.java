/****************************************/
/* SubImportNotFoundException.java		*/
/* Created on: 17-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting.preprocessing.errors;

import eu.cherrytree.zaria.scripting.preprocessing.imports.ImportInfo;
import java.util.ArrayList;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class SubImportNotFoundException extends Exception
{
	//--------------------------------------------------------------------------
	
	public static class Info
	{
		private ImportInfo topImport;
		private ArrayList<ImportInfo> imports;

		public Info(ImportInfo topImport, ArrayList<ImportInfo> imports)
		{
			this.topImport = topImport;
			this.imports = imports;
		}

		public ArrayList<ImportInfo> getImports()
		{
			return imports;
		}

		public ImportInfo getTopImport()
		{
			return topImport;
		}

		@Override
		public String toString()
		{
			String str = "Top " + topImport.getSource() + " sub ";
			
			for (ImportInfo info : imports)
				str += info.getSource();
			
			return str;
		}
	}
	
	//--------------------------------------------------------------------------
	
	private ArrayList<Info> infos = new ArrayList<>();

	//--------------------------------------------------------------------------
	
	public SubImportNotFoundException(ArrayList<Info> infos)
	{
		this.infos = infos;
	}

	//--------------------------------------------------------------------------
	
	public ArrayList<Info> getInfos()
	{
		return infos;
	}
	
	//--------------------------------------------------------------------------
}
