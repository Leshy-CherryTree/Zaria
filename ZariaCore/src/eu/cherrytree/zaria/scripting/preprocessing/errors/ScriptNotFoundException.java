/****************************************/
/* ScriptNotFoundException.java			*/
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
public class ScriptNotFoundException extends Exception
{
	//--------------------------------------------------------------------------
	
	private ArrayList<ImportInfo> imports = new ArrayList<>();
	
	//--------------------------------------------------------------------------

	public ScriptNotFoundException(String path)
	{
		super("Couldn't find script " + path + ".");
	}
	
	//--------------------------------------------------------------------------
	
	public ScriptNotFoundException(ArrayList<ImportInfo> imports)
	{
		super();
		
		this.imports.addAll(imports);
	}
	
	//--------------------------------------------------------------------------

	public ArrayList<ImportInfo> getImports()
	{
		return imports;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getMessage()
	{
		if (imports.isEmpty())
		{
			return super.getMessage();
		}
		else
		{
			String msg = "Couldn't find import:";
		
			for (ImportInfo info : imports)
				msg += "\n" + info.getTarget() == null ? info.getSource() : info.getTarget();
		
			return msg;
		}
	}
	
	//--------------------------------------------------------------------------
}
