/****************************************/
/* ZoneParser.java						*/
/* Created on: 11-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document.parsers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.TextEditorState;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.serialization.DefinitionValidation;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneParser extends AbstractParser
{
	//--------------------------------------------------------------------------
	
	private class SourcePart
	{
		public String source;
		public int offset;			

		SourcePart(String source, int offset)
		{
			this.source = source;
			this.offset = offset;
		}				
	}
	
	//--------------------------------------------------------------------------
	
	private ObjectMapper objectMapper = Serializer.createDeserializationMapper();
	
	private TextEditorState editor;
	
	//--------------------------------------------------------------------------
	
	public ZoneParser(TextEditorState editor)
	{
		this.editor = editor;				
	}
	//--------------------------------------------------------------------------
	
	private DefaultParseResult parse()
	{				
		assert editor.getDocument().getDocumentType() == ZoneDocument.DocumentType.ZONE;
		
		String text = editor.getText();		
		
		DefaultParseResult result = new DefaultParseResult(this);
		ZariaObjectDefinition[] definitions;
		
		try
		{									
			definitions = objectMapper.readValue(text, ZariaObjectDefinition[].class);			
		}
		// First check if the document is valid.
		catch(JsonParseException | JsonMappingException ex)
		{
			int len = 1;
			
			try
			{
				len = editor.getTextArea().getLineEndOffset(ex.getLocation().getLineNr()-1);
				len -= ex.getLocation().getCharOffset();
			}
			catch(BadLocationException ex1)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex1);
			}
			
			DefaultParserNotice notice = new DefaultParserNotice(this, ex.getMessage(), ex.getLocation().getLineNr(), (int)ex.getLocation().getCharOffset(), len);
			result.addNotice(notice);
			return result;
		}
		catch(Exception ex)
		{		
			DefaultParserNotice notice = new DefaultParserNotice(this, ex.getMessage(), 0, 0, text.length());
			result.addNotice(notice);			
			result.setError(ex);
			
			return result;
		}

		
		// Validate all definitions.
		ArrayList<DefinitionValidation> errors = new ArrayList<>();
		ArrayList<SourcePart> sourceParts = new ArrayList<>();
						
		for(int i = 0 ; i < definitions.length ; i++)
		{
			DefinitionValidation val = definitions[i].validate();
			
			if(val.hasErrors())
			{
				errors.add(val);
				sourceParts.add(getDefinitionSourceByObjectIndex(i,text));				
			}
		}
		
		for(int i = 0 ; i < errors.size() ; i++)
		{			
			for(DefinitionValidation.ErrorInfo info : errors.get(i).getErrors())
			{
				boolean no_field = info.getField().isEmpty();
				
				int offset = no_field ? 0 : findFieldOffset(0, info.getField(), sourceParts.get(i).source);
				int eol = no_field ? sourceParts.get(i).source.length() : getEOL(offset, sourceParts.get(i).source);
				int line = 0;
				
				int srcoffset = sourceParts.get(i).offset;
				
				try
				{
					line = editor.getTextArea().getLineOfOffset(offset + srcoffset);
				}
				catch(BadLocationException ex)
				{
					DebugConsole.logger.log(Level.SEVERE, null, ex);
				}
			
				DefaultParserNotice notice = new DefaultParserNotice(this, info.getError(), line, offset + srcoffset, eol);
				result.addNotice(notice);
			}
		}
		
		return result;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public ParseResult parse(RSyntaxDocument rsd, String string)
	{
		long begin = System.currentTimeMillis();
		
		EditorApplication.getDebugConsole().addLine("--------------------------------------");
		EditorApplication.getDebugConsole().addLine("Parsing " + editor.getDocument().getTitle());
		
		DefaultParseResult result = parse();
		
		List<ParserNotice> notices = result.getNotices();
		
		if(notices.isEmpty())
			EditorApplication.getDebugConsole().addLine("Everything is ok.");
		else
		{
			EditorApplication.getDebugConsole().addLine("Found " + notices.size() + " errors:");
						
			for(Object obj : notices)
			{
				ParserNotice notice = (ParserNotice) obj;

				EditorApplication.getDebugConsole().addLine("Line: " + notice.getLine() + " Error: " + notice.getMessage().replace("\n", " "));
			}
		}		
		
		long time = System.currentTimeMillis() - begin;
		result.setParseTime(time);
		
		EditorApplication.getDebugConsole().addLine("Done in " + time + "ms.\n");
						
		return result;
	}
	
	//--------------------------------------------------------------------------
	
	/**
	 * Recursively finds the offset of the input field in the input source.
	 */
	private int findFieldOffset(int offset, String field, String source)
	{	
		assert field != null && !field.isEmpty() : "Field is empty";
		
		try
		{
			int index = source.indexOf(field, offset);

			if(index < offset)
				return 0;

			boolean found = false;

			for(int i = index ; i < source.length() ; i++)
			{
				char c = source.charAt(i + field.length());

				if(!Character.isWhitespace(c))
				{				
					if(c == ':')
						found = true;

					break;
				}
			}

			if(found)
				return index;
			else
				return findFieldOffset(index + field.length(), field, source);
		}
		catch(StackOverflowError error)
		{			
			//TODO Maken handling of this error better.
			System.err.println("offset: " + offset);
			System.err.println("field: " + field);
			error.printStackTrace();
			return -1;
		}
	}

	//--------------------------------------------------------------------------
	
	private int getEOL(int offset, String source)
	{
		int len = 0;
		int depth = -1;
		boolean str_open = false;
		boolean chr_open = false;
				
		for(int i = offset ; i < source.length() ; i++, len = i - offset)
		{
			char c = source.charAt(i);
			
			if(c == '{' || c == '[')
				depth++;
			else if(c == '}' || c == ']')
				depth--;
			else if(c == '\"')
			{
				if(str_open)
					depth--;
				else
					depth++;
				
				str_open = !str_open;
			}
			else if(c == '\'')
			{
				if(chr_open)
					depth--;
				else
					depth++;
				
				chr_open = !chr_open;
			}
			else if(c == ',')
				break;
			
			if(depth < -1)
			{
				len--;
				break;
			}
		}
		
		return len;
	}
	
	//--------------------------------------------------------------------------
	
	private SourcePart getDefinitionSourceByObjectIndex(int objIndex, String text)
	{
		int offset = -1;
		int depth = -1;
		int index = -1;
		
		while(true)
		{
			int open = text.indexOf('{', offset+1);
			int close = text.indexOf('}', offset+1);

			if(open < close)
			{
				depth++;
				offset = open;

				if(depth == 0)
					index++;
			}
			else if(open > close)
			{
				depth--;
				offset = close;
			}
			else
				throw new RuntimeException();

			if(index == objIndex)
				return getDefinitionSourceByOffset(offset, text);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private SourcePart getDefinitionSourceByOffset(int sourcePartOffset, String text)
	{
		String src = "";
		int offset = -1;
		
		int depth = -1;
		boolean begun = false;
		
		while(true)
		{
			char c = text.charAt(sourcePartOffset);
			
						
			if(!begun && c == '{')
			{
				begun = true;
				offset = sourcePartOffset;
			}
			
			sourcePartOffset++;
			
			if(begun)
			{
				if(c == '{')
					depth++;
				else if(c == '}')
					depth--;
												
				src += c;	
				
				if(depth < 0)
					break;
			}						
		}
		
		return new SourcePart(src, offset);
	}
	
	//--------------------------------------------------------------------------
}
