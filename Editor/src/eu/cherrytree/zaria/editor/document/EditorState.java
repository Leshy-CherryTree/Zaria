/****************************************/
/* EditorState.java						*/
/* Created on: 14-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import javax.swing.JComponent;

import javax.swing.text.BadLocationException;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public interface EditorState
{
	//--------------------------------------------------------------------------
	
	public void updateText();
	
	public void undo();
	public void redo();
	public void cut();
	public void copy();
	public void paste();
	
	public void selectAll();
	public void unmark();
	
	public void setCtrlButton(boolean pressed);
	public void setAltButton(boolean pressed);
	public void setShiftButton(boolean pressed);
	
	public boolean find(String text, FindOptions options);
	public boolean replace(String text, String replace, FindOptions options);
	public boolean replaceFind(String text, String replace, FindOptions options);
	public int replaceAll(String text, String replace, FindOptions options);	
	
	public void updateSettings();
	
	public void goToLine(int line) throws BadLocationException;
	
	public void attach(JComponent parent);
	public void detach(JComponent parent);
	
	public void onFocusGained(JComponent parent);
	public void onFocusLost(JComponent parent);
	
	public ZariaObjectDefinition[] getDefinitions();
	public String getText();
	public void setText(String text);
	
	public Object getSelected();
	public void editSelected();
	
	public ZoneDocument getDocument();
	
	//--------------------------------------------------------------------------	
}
