/****************************************/
/* FileScriptPreprocessor.java			*/
/* Created on: 18-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.scripting;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.scripting.preprocessing.ScriptPreprocessor;
import eu.cherrytree.zaria.scripting.preprocessing.errors.ScriptNotFoundException;
import eu.cherrytree.zaria.scripting.preprocessing.errors.ScriptPreprocessorError;
import eu.cherrytree.zaria.scripting.preprocessing.imports.ImportInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class FileScriptPreprocessor extends ScriptPreprocessor
{
	//--------------------------------------------------------------------------
	
	private String scriptsFolder;
	
	//--------------------------------------------------------------------------

	public FileScriptPreprocessor(String scriptsFolder)
	{
		this.scriptsFolder = scriptsFolder;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	protected String getSource(String path)
	{		
		File file = new File(scriptsFolder + path.replace(".", File.separator) + ".zonescript");
		
		try
		{
			return ZoneDocument.loadFileAsString(file);
		}
		catch (IOException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
			return "";
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected boolean scriptExist(String path)
	{
		File file = new File(scriptsFolder + path.replace(".", File.separator) + ".zonescript");
		
		return file.exists();
	}
	
	//--------------------------------------------------------------------------
	
	public String checkImports(String source) throws ScriptNotFoundException
	{
		ArrayList<ImportInfo> imports = new ArrayList<>();
		parseForImports(source, imports);
		
		StringBuilder sourceBuilder = new StringBuilder(source);

		for (ImportInfo info : imports)
		{
			int offset = sourceBuilder.indexOf(info.getSource());				
			sourceBuilder.replace(offset, offset + info.getSource().length(), "");
		}

		return sourceBuilder.toString();
	}
	
	//--------------------------------------------------------------------------
	
	public String preProcess(File sourceFile) throws ScriptPreprocessorError, IOException
	{
		return preProcess(ZoneDocument.loadFileAsString(sourceFile));
	}

	//--------------------------------------------------------------------------
}
