/****************************************/
/* ScriptParser.java						*/
/* Created on: 31-May-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document.parsers;

import eu.cherrytree.zaria.editor.scripting.FileScriptPreprocessor;
import eu.cherrytree.zaria.scripting.preprocessing.errors.ScriptNotFoundException;
import eu.cherrytree.zaria.scripting.preprocessing.errors.ScriptPreprocessorError;
import eu.cherrytree.zaria.scripting.preprocessing.imports.ImportInfo;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.RhinoException;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptParser extends AbstractParser
{
	//--------------------------------------------------------------------------
	
	private ScriptDocument document;
	private String location;
	
	//--------------------------------------------------------------------------
	
	public ScriptParser(ScriptDocument document, String location)
	{
		this.document = document;
		this.location = location;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public ParseResult parse(RSyntaxDocument rsd, String string)
	{
		Context context = Context.enter();
		
		DefaultParseResult result = new DefaultParseResult(this);
		
		String source = document.getText();
		
		try
		{
			FileScriptPreprocessor preprocessor = new FileScriptPreprocessor(location);
			
			// Rhino doesn't get import keyword thats why we strip them before further parsing.
			source = preprocessor.checkImports(source);
		}
		catch(ScriptPreprocessorError ex)
		{
			for (ScriptPreprocessorError.Info info : ex.getInfo())
			{
				DefaultParserNotice notice = new DefaultParserNotice(this, info.getDetails(), info.getLineIndex(), info.getOffset(), info.getLineSource().length());
				result.addNotice(notice);
			}
			
			return result;
		}
		catch (ScriptNotFoundException ex)
		{
			String[] lines = source.split("\n");
			
			for (ImportInfo info : ex.getImports())
			{
				String error;
				
				if (info.getError() != null)
					error = info.getError();
				else if (info.getTarget() != null)
					error = "Couldn't find import \"" + info.getTarget() + "\"";
				else
					error = "Couldn't find \"" + info.getSource() + "\"";
				
				int idx = 0;
				
				for (int i = 0 ; i < lines.length ; i++)
				{
					if (lines[i].contains(info.getSource()))
					{
						idx = i;
						break;
					}
				}
				
				DefaultParserNotice notice = new DefaultParserNotice(this, error, idx, info.getOffset(), info.getSource().length());
				result.addNotice(notice);
			}
			
			return result;
		}
		
		try
		{
			context.compileString(source, document.getTitle(), 1, null);
		}
		catch (RhinoException ex)
		{
			int offset = source.indexOf(ex.lineSource());
			
			DefaultParserNotice notice = new DefaultParserNotice(this, ex.details(), ex.lineNumber(), offset, ex.lineSource().length());
			result.addNotice(notice);
		}
				
		Context.exit();
		
		return result;
	}

	//--------------------------------------------------------------------------
}
