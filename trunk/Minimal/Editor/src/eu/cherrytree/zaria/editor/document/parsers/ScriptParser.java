/****************************************/
/* ScriptParser.java					*/
/* Created on: 31-May-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document.parsers;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.document.TextEditorState;
import eu.cherrytree.zaria.scripting.preprocessor.ScriptPreprocessor;

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
			ScriptPreprocessor.setScriptLocation(location);
			source = ScriptPreprocessor.prepareForParsing(source, document.getTitle());
		}
		catch(ScriptPreprocessor.ScriptProcessorError ex)
		{
			for(ScriptPreprocessor.ScriptProcessorError.Info info : ex.getInfo())
			{
				DefaultParserNotice notice = new DefaultParserNotice(this, info.getDetails(), info.getLineNumber(), info.getOffset(), info.getLineSource().length());
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
