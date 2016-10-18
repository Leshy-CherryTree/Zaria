/****************************************/
/* TextEditorState.java                */
/* Created on: 14-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.document.parsers.ZoneParser;
import eu.cherrytree.zaria.editor.Settings;
import eu.cherrytree.zaria.editor.classlist.ZoneClassList;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.autocomplete.ScriptCompletionProvider;
import eu.cherrytree.zaria.editor.document.parsers.ScriptDocument;
import eu.cherrytree.zaria.editor.document.parsers.ScriptParser;
import eu.cherrytree.zaria.editor.modes.ZoneTokenMaker;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;

import java.io.IOException;
import java.util.UUID;

import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

import org.fife.ui.autocomplete.AutoCompletion;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.modes.JavaScriptTokenMaker;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class TextEditorState implements EditorState, DocumentListener, ScriptDocument
{
	//--------------------------------------------------------------------------
	
	private class GenerateUUIDAction extends TextAction
	{
		public GenerateUUIDAction()
		{
			super("Generate UUID");
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{			
			JTextComponent tc = getTextComponent(e);
			tc.replaceSelection(UUID.randomUUID().toString());				
		}
		
	}
	
	//--------------------------------------------------------------------------
	
	private static boolean antialiasing;
	private static boolean fractionalFontMetrics;
	private static int fontSize = 12;
	
	private static Theme theme;
	
	private static final AbstractTokenMakerFactory factory = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
	
	//--------------------------------------------------------------------------
	
	static			
	{
		try
		{									
			theme = Theme.load(ZoneDocument.class.getResourceAsStream("/eu/cherrytree/zaria/editor/res/themes/dark.xml"));
			
			// For all Zone files.
			factory.putMapping("zone_syntax", ZoneTokenMaker.class.getCanonicalName());
			
			// For java properties files.
			factory.putMapping("script_syntax", JavaScriptTokenMaker.class.getCanonicalName());			

			TokenMakerFactory.setDefaultInstance(factory);
		}
		catch(IOException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private RSyntaxTextArea textArea;
	private RTextScrollPane pane;

	private ZoneDocument document;
	
	//--------------------------------------------------------------------------

	public TextEditorState(ZoneDocument document, ZoneClassList classList)
	{		
		this.document = document;

		textArea = new RSyntaxTextArea();	
		
		if (document.getDocumentType() == ZoneDocument.DocumentType.ZONE_SCRIPT)
			textArea.setSyntaxEditingStyle("script_syntax");
		else
			textArea.setSyntaxEditingStyle("zone_syntax");
		
		textArea.setAutoIndentEnabled(true);		
		textArea.setCloseCurlyBraces(true);
		textArea.setCodeFoldingEnabled(true);		
		textArea.addCaretListener(document.getDocumentManager());			
		textArea.getDocument().addDocumentListener(this);
		
		textArea.getPopupMenu().add(new JPopupMenu.Separator());
		textArea.getPopupMenu().add(new GenerateUUIDAction());	
		
		switch(document.getDocumentType())
		{
			case ZONE:
				textArea.addParser(new ZoneParser(this));
				break;
				
			case ZONE_SCRIPT:
			{
				textArea.addParser(new ScriptParser(this, EditorApplication.getScriptsLocation()));
					     
				AutoCompletion ac = new AutoCompletion(new ScriptCompletionProvider(classList, EditorApplication.getScriptsLocation()));
				ac.install(textArea);
				
				ac.setChoicesWindowSize(800, 200);
			}
				break;					
		}				
		
		updateSettings();				
				
		pane = new RTextScrollPane();
		pane.setViewportView(textArea);	
		pane.setLineNumbersEnabled(true);
	}
	
	//--------------------------------------------------------------------------
		
	private void applyFontSize(float size)
	{
		SyntaxScheme scheme = (SyntaxScheme) textArea.getSyntaxScheme().clone();

		int count = scheme.getStyleCount();
		
		for (int i = 0; i < count; i++)
		{
			Style ss = scheme.getStyle(i);
			if (ss != null)
			{
				Font font = ss.font;
							
				if (font != null)
					ss.font = font.deriveFont(size);				
			}
		}

		Font font = textArea.getFont();
		textArea.setFont(font.deriveFont(size));
		
		textArea.setSyntaxScheme(scheme);
		
		Component parent = textArea.getParent();
		
		if (parent instanceof JViewport)
		{
			parent = parent.getParent();
			
			if (parent instanceof JScrollPane)
				parent.repaint();
		}
		
		textArea.setSyntaxScheme(scheme);
	}

	//--------------------------------------------------------------------------
	
	@Override
	public void attach(JComponent parent)
	{
		parent.add(pane);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void detach(JComponent parent)
	{
		parent.remove(pane);
	}
	
	//--------------------------------------------------------------------------
	
	public static boolean isAntialiasingEnabled()
	{
		return antialiasing;
	}
	
	//--------------------------------------------------------------------------

	public static boolean isFractionalFontMetricsEnabled()
	{
		return fractionalFontMetrics;
	}
	
	//--------------------------------------------------------------------------

	public static int getFontSize()
	{
		return fontSize;
	}

	//--------------------------------------------------------------------------
	
	public static Theme getTheme()
	{
		return theme;
	}
	
	//--------------------------------------------------------------------------
	
	public static void setAntialiasing(boolean antialiasing)
	{
		TextEditorState.antialiasing = antialiasing;
	}
	
	//--------------------------------------------------------------------------

	public static void setFractionalFontMetrics(boolean fractionalFontMetrics)
	{
		TextEditorState.fractionalFontMetrics = fractionalFontMetrics;
	}
	
	//--------------------------------------------------------------------------

	public static void setFontSize(int fontSize)
	{
		TextEditorState.fontSize = fontSize;
	}
	
	//--------------------------------------------------------------------------

	public static void loadSettings()
	{
		antialiasing = Settings.getAntialiasing();
		fractionalFontMetrics = Settings.getFractionalFontMetrics();
		fontSize = Settings.getEditorFontSize();
	}
	
	//--------------------------------------------------------------------------
	
	public static void saveSettings()
	{
		Settings.setAntialiasing(antialiasing);
		Settings.setFractionalFontMetrics(fractionalFontMetrics);
		Settings.setEditorFontSize(fontSize);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public final void updateSettings()
	{
		textArea.setAntiAliasingEnabled(antialiasing);
		textArea.setFractionalFontMetricsEnabled(fractionalFontMetrics);
		
		theme.apply(textArea);		
		
		applyFontSize(fontSize);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void insertUpdate(DocumentEvent event)
	{
		document.setModified();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void removeUpdate(DocumentEvent event)
	{
		document.setModified();
	}

	//--------------------------------------------------------------------------

	@Override
	public void changedUpdate(DocumentEvent event)
	{
		document.setModified();
	}
			
	//--------------------------------------------------------------------------
	
	@Override
	public void undo()
	{
		textArea.undoLastAction();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void redo()
	{
		textArea.redoLastAction();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void cut()
	{
		textArea.cut();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void copy()
	{
		textArea.copy();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void paste()
	{
		textArea.paste();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void selectAll()
	{
		textArea.selectAll();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public Object getSelected()
	{
		return textArea.getSelectedText();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void editSelected()
	{
		throw new UnsupportedOperationException("This editor does not support editing of selected elements.");
	}
	
	//--------------------------------------------------------------------------
	
	private SearchContext getSearchContext(FindOptions options)
	{
		SearchContext context = new SearchContext();
		
		context.setMatchCase(options.isCaseSensitive());
		context.setRegularExpression(false);
		context.setSearchForward(options.getDirection().equals(FindOptions.Direction.FORWARD));
		context.setWholeWord(options.isWholeWord());
		context.setSearchSelectionOnly(options.isInSelection());
		context.setMarkAll(options.isMark());
		
		return context;
	}

	//--------------------------------------------------------------------------
	
	@Override
	public boolean find(String text, FindOptions options)
	{	
//		textArea.setMarkOccurrences(options.isMark());
		
		SearchContext context = getSearchContext(options);
		
		context.setSearchFor(text);	
		
		if (options.isWrap())
		{
			SearchResult found = SearchEngine.find(textArea, context);
			
			if (!found.wasFound())
				textArea.setCaretPosition(context.getSearchForward() ? 0 : textArea.getText().length());
			else
				return true;
		}
		
		return SearchEngine.find(textArea, context).wasFound();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public boolean replace(String text, String replace, FindOptions options)
	{
//		textArea.setMarkOccurrences(options.isMark());
		
		SearchContext context = getSearchContext(options);
		
		context.setSearchFor(text);
		context.setReplaceWith(replace);	
		
		if (options.isWrap())
		{
			SearchResult found = SearchEngine.replace(textArea, context);

			if (!found.wasFound())
				textArea.setCaretPosition(context.getSearchForward() ? 0 : textArea.getText().length());
			else
				return true;
		}
		
		return SearchEngine.replace(textArea, context).wasFound();
	}
	
	//--------------------------------------------------------------------------
	
	private boolean replaceFind(SearchContext context, boolean wrap)
	{				
		SearchResult replace = SearchEngine.replace(textArea, context);
	
		if (!replace.wasFound() && wrap)
		{
			textArea.setCaretPosition(context.getSearchForward() ? 0 : textArea.getText().length());
			replace = SearchEngine.replace(textArea, context);
		}
				
		if (replace.wasFound())
		{
			SearchResult find = SearchEngine.find(textArea, context);

			if (!find.wasFound() && wrap)
			{
				textArea.setCaretPosition(context.getSearchForward() ? 0 : textArea.getText().length());
				SearchEngine.find(textArea, context);
			}
		}
		
		return replace.wasFound();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public boolean replaceFind(String text, String replace, FindOptions options)
	{
//		textArea.setMarkOccurrences(options.isMark());
		
		SearchContext context = getSearchContext(options);
		
		context.setSearchFor(text);
		context.setReplaceWith(replace);		
		
		return replaceFind(context, options.isWrap());
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public int replaceAll(String text, String replace, FindOptions options)
	{
		SearchContext context = getSearchContext(options);
		
		context.setSearchFor(text);
		context.setReplaceWith(replace);		
		
		return SearchEngine.replaceAll(textArea, context).getCount();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public ZoneDocument getDocument()
	{
		return document;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void unmark()
	{
		textArea.setMarkOccurrences(false);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void goToLine(int line) throws BadLocationException
	{
		line--;
		
		if ( line >= 0 && line < textArea.getLineCount() )
		{
			textArea.scrollRectToVisible( textArea.modelToView( textArea.getLineStartOffset( line ) ) );
			textArea.setCaretPosition( textArea.getLineStartOffset( line ) );
		}
	}	
	
	//--------------------------------------------------------------------------
	
	public RSyntaxTextArea getTextArea()
	{
		return textArea;
	}
		
	//--------------------------------------------------------------------------
	
	@Override
	public ZariaObjectDefinition[] getDefinitions()
	{
		try
		{
			return Serializer.createDeserializationMapper().readValue(getText(), ZariaObjectDefinition[].class);
		}
		catch (IOException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void updateText()
	{
		String text = textArea.getText();
		
		if (text.startsWith("//"))
		{
			int idx = text.indexOf("// Last modified on:") + "// Last modified on: xx-XXX-XXXX\n\n".length();
			textArea.setText(document.getHeader() + text.substring(idx));
		}
		else
		{
			textArea.setText(document.getHeader() + text);			
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getText()
	{
		return textArea.getText();
	}
	
	//--------------------------------------------------------------------------
	@Override
	public String getTitle()
	{
		return document.getTitle();
	}
		
	//--------------------------------------------------------------------------
			
	@Override
	public boolean setText(String text)
	{
		textArea.setText(text);
		
		textArea.setCaretPosition(0);
		textArea.discardAllEdits();	
		
		updateSettings();
		
		return true;
	}


	//--------------------------------------------------------------------------

	@Override
	public void onFocusGained(JComponent parent)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------

	@Override
	public void onFocusLost(JComponent parent)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void setCtrlButton(boolean pressed)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void setAltButton(boolean pressed)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void setShiftButton(boolean pressed)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
}
