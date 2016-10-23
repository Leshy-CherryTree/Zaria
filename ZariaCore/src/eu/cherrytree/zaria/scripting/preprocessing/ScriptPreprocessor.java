/****************************************/
/* ScriptPreprocessor.java				*/
/* Created on: 14-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting.preprocessing;

import eu.cherrytree.zaria.scripting.preprocessing.errors.ScriptNotFoundException;
import eu.cherrytree.zaria.scripting.preprocessing.errors.ScriptPreprocessorError;
import eu.cherrytree.zaria.scripting.preprocessing.errors.SubImportNotFoundException;
import eu.cherrytree.zaria.scripting.preprocessing.imports.ImportInfo;
import eu.cherrytree.zaria.scripting.preprocessing.imports.ImportSource;

import java.util.ArrayList;
import java.util.Collections;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class ScriptPreprocessor
{
	//--------------------------------------------------------------------------
	
	private enum ParseState
	{
		LINE_COMMENT,
		COMMENT,
		SOURCE,
		WHITESPACE,
		IMPORT;
	}

	//--------------------------------------------------------------------------
	
	protected ArrayList<ImportInfo> getImports(String source)
	{
		char[] chars = source.toCharArray();
		ArrayList<ImportInfo> imports = new ArrayList<>();
		StringBuilder importBuilder = new StringBuilder();

		ParseState state = ParseState.WHITESPACE;

		int index;

		for (index = 0; index < chars.length; index++)
		{
			switch (state)
			{
				case SOURCE:
				case WHITESPACE:

					state = ParseState.SOURCE;

					if (chars[index] == '/')
					{
						if (index < chars.length - 1)
						{
							if (chars[index + 1] == '/')
							{
								state = ParseState.LINE_COMMENT;
								index++;
							}
							else if (chars[index + 1] == '*')
							{
								state = ParseState.COMMENT;
								index++;
							}
						}
					}
					else if (chars[index] == 'i')
					{
						if (index < chars.length - 6)
						{
							if (chars[index + 1] == 'm' && chars[index + 2] == 'p' && chars[index + 3] == 'o' && chars[index + 4] == 'r' && chars[index + 5] == 't')
							{
								state = ParseState.IMPORT;
								importBuilder.append("import");
								index += 5;
							}
						}
					}
					else if (Character.isWhitespace(chars[index]))
					{
						state = ParseState.WHITESPACE;
					}

					break;

				case COMMENT:

					if (chars[index] == '*')
					{
						if (index < chars.length - 1)
						{
							if (chars[index + 1] == '/')
							{
								state = ParseState.WHITESPACE;
								index++;
							}
						}
					}

					break;

				case LINE_COMMENT:

					if (chars[index] == '\n')
					{
						state = ParseState.WHITESPACE;
					}

					break;

				case IMPORT:

					if (chars[index] == ';')
					{
						state = ParseState.WHITESPACE;

						importBuilder.append(chars[index]);
						imports.add(new ImportInfo(importBuilder.toString(), index - importBuilder.length() + 1));
						importBuilder.delete(0, importBuilder.length());
					}
					else if (chars[index] == '\n')
					{
						state = ParseState.SOURCE;
					}
					else if (chars[index] == '/' && index < chars.length - 1 && chars[index + 1] == '/')
					{
						state = ParseState.LINE_COMMENT;
						index++;
						importBuilder.delete(0, importBuilder.length());
					}
					else
					{
						importBuilder.append(chars[index]);
					}

					break;
			}

			if (state == ParseState.SOURCE)
			{
				break;
			}
		}

		return imports;
	}

	//--------------------------------------------------------------------------
	
	private String getImportSource(ArrayList<ImportInfo> imports) throws SubImportNotFoundException
	{
		ArrayList<ImportSource> sources = new ArrayList<>();
		ArrayList<SubImportNotFoundException.Info> error_info = new ArrayList<>();
		ArrayList<ImportInfo> checkedImports = new ArrayList<>();
		
		for (ImportInfo info : imports)
		{
			try
			{
				sources.addAll(getImportSources(checkedImports, info, info));
			}
			catch (ScriptNotFoundException ex)
			{
				error_info.add(new SubImportNotFoundException.Info(info, ex.getImports()));
			}				
		}				
		
		if (!error_info.isEmpty())
			throw new SubImportNotFoundException(error_info);
		
		StringBuilder sourceBuilder = new StringBuilder();
		
		sourceBuilder.append("\n// ----- IMPORTS_BEGIN ----- \n\n");
		
		for (ImportSource src : sources)
		{
			sourceBuilder.append("// Imported from ").append(src.getImportName()).append("\n\n");
			sourceBuilder.append(src.getSource()).append("\n\n");
			sourceBuilder.append("// ").append(src.getImportName()).append("\n\n");
		}
		
		sourceBuilder.append("// ----- IMPORTS_END ----- \n");
		
		return sourceBuilder.toString();
	}

	//--------------------------------------------------------------------------

	private ArrayList<ImportSource> getImportSources(ArrayList<ImportInfo> checkedImports, ImportInfo importInfo, ImportInfo topImportInfo) throws ScriptNotFoundException
	{		
		if (checkedImports.contains(importInfo))
			return new ArrayList<>();
						
		ArrayList<ImportInfo> imports = new ArrayList<>();
		ArrayList<ImportInfo> importsTmp = new ArrayList<>();

		ArrayList<ImportSource> sources = new ArrayList<>();

		sources.add(new ImportSource(getSourceAndImports(importInfo.getTarget(), imports), importInfo.getTarget()));

		importsTmp.addAll(imports);

		for (ImportInfo info : importsTmp)
		{
			if (checkedImports.contains(info))
				imports.remove(info);
		}

		checkedImports.add(importInfo);

		for (ImportInfo info : imports)
			sources.addAll(getImportSources(checkedImports, info, topImportInfo));	

		return sources;			
	}

	//--------------------------------------------------------------------------
	
	private String getSourceAndImports(String path, ArrayList<ImportInfo> imports) throws ScriptNotFoundException
	{
		if (!scriptExist(path))
			throw new ScriptNotFoundException(path);

		String source = getSource(path);
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
	
	protected void parseForImports(String source, ArrayList<ImportInfo> imports) throws ScriptNotFoundException
	{
		imports.addAll(getImports(source));
		ArrayList<ImportInfo> error_info = new ArrayList<>();

		for (ImportInfo info : imports)
		{
			if (info.getTarget() == null || (!info.getTarget().endsWith("*") && !scriptExist(info.getTarget())))
			{
				error_info.add(info);
			}
		}

		if (!error_info.isEmpty())
			throw new ScriptNotFoundException(error_info);
	}

	//--------------------------------------------------------------------------
	
	public String preProcess(String source)
	{
		ArrayList<ImportInfo> imports = new ArrayList<>();
		
		try
		{
			parseForImports(source, imports);
		}
		catch(ScriptNotFoundException ex)
		{
			ArrayList<String> lines = new ArrayList<>();
			Collections.addAll(lines, source.split("\n"));
		
			ArrayList<ScriptPreprocessorError.Info> infos = new ArrayList<>();
				
			for (ImportInfo info : ex.getImports())
				infos.add(new ScriptPreprocessorError.Info("Couldn't find import.", info.getSource(), lines.indexOf(info.getSource()), info.getOffset()));

			throw new ScriptPreprocessorError(infos);
		}
		
		StringBuilder sourceBuilder = new StringBuilder(source);

		if (!imports.isEmpty())
		{
			for (ImportInfo info : imports)
			{
				int offset = sourceBuilder.indexOf(info.getSource());				
				sourceBuilder.replace(offset, offset + info.getSource().length(), "");
			}

			try
			{
				sourceBuilder.insert(imports.get(0).getOffset(), getImportSource(imports));
			}
			catch(SubImportNotFoundException ex)
			{
				ArrayList<String> lines = new ArrayList<>();
				Collections.addAll(lines, source.split("\n"));
			
				ArrayList<ScriptPreprocessorError.Info> infos = new ArrayList<>();
				
				for (SubImportNotFoundException.Info info : ex.getInfos())
					infos.add(new ScriptPreprocessorError.Info("Couldn't process sub imports " + info.toString(), info.getTopImport().getSource(), lines.indexOf(info.getTopImport().getSource()), info.getTopImport().getOffset()));
			
				throw new ScriptPreprocessorError(infos);
			}
		}

		return sourceBuilder.toString();
	}	
	
	//--------------------------------------------------------------------------
	
	protected abstract String getSource(String path);
	protected abstract boolean scriptExist(String path);
	
	//--------------------------------------------------------------------------
}
