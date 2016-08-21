/****************************************/
/* DocumentManager.java					*/
/* Created on: 04-May-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.EditorFrame;
import eu.cherrytree.zaria.editor.classlist.ZoneClassList;
import eu.cherrytree.zaria.editor.components.ButtonTabComponent;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.dialogs.ArrayEditDialog;
import eu.cherrytree.zaria.editor.dialogs.BrowseDefinitionsDialog;
import eu.cherrytree.zaria.editor.dialogs.BrowseScriptsDialog;
import eu.cherrytree.zaria.editor.dialogs.EditorDialog;
import eu.cherrytree.zaria.editor.dialogs.EditorFactory;
import eu.cherrytree.zaria.editor.dialogs.ExtendedInformationDialog;
import eu.cherrytree.zaria.editor.dialogs.ListSearchDialog;
import eu.cherrytree.zaria.editor.dialogs.WaitDialog;
import eu.cherrytree.zaria.editor.properties.propertysheet.PropertySheetPanel;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;

import org.fife.ui.rtextarea.RTextArea;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DocumentManager implements ChangeListener, CaretListener
{	
	//--------------------------------------------------------------------------
	
	private static final int maxSize = 15;
	private static String lastPath = null;
	
	private static EditorFrame editorFrame;
		
	//--------------------------------------------------------------------------
	
	private ArrayList<ZoneDocument> documents = new ArrayList<>();
	private ZoneDocument currentDocument;
	private JTabbedPane tabPane;
	private PropertySheetPanel propertySheetPanel;	
	
	private JPopupMenu popupMenu;
	private ZoneClassList zoneClassList;
	
	//--------------------------------------------------------------------------

	public DocumentManager(JTabbedPane tabPane, EditorFrame editorFrame, PropertySheetPanel propertySheetPanel, JPopupMenu popupMenu, ZoneClassList zoneClassList)
	{
		DocumentManager.editorFrame = editorFrame;
		
		this.tabPane = tabPane;
		this.propertySheetPanel = propertySheetPanel;
		this.popupMenu = popupMenu;
		this.zoneClassList = zoneClassList;
		
		tabPane.addChangeListener(this);
	}
	
	//--------------------------------------------------------------------------
	
	public void setEditType(ZoneDocument.EditType editType)
	{
		if (currentDocument != null)
			currentDocument.setState(editType);	
		
		popupMenu.removeAll();
		popupMenu.setVisible(false);
		
		propertySheetPanel.removePropertySheetChangeListener(propertySheetPanel);
	}
	
	//--------------------------------------------------------------------------
	
	public void newDocument(ZoneDocument.DocumentType type)
	{
		openDocument(null, type);
	}
	
	//--------------------------------------------------------------------------
	
	public void openDocument()
	{
		File file = showOpenDialog();
		
		if (file != null)
			openDocument(file);
	}
	
	//--------------------------------------------------------------------------
	
	public void openDocument(File file)
	{
		EditorApplication.getDebugConsole().addLine("Opening: " + file.getAbsolutePath());
		
		// Checking if file exists.
		if (!file.exists())
		{
			JOptionPane.showMessageDialog(editorFrame, "File " + file.getPath() + " does not exist!", "File not found!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Checking if the file is not too big.
		if (file.length() > 1024 * 1024 * maxSize)
		{
			float size = file.length() / (1024.0f * 1024.0f);

			JOptionPane.showMessageDialog(editorFrame, "File " + file.getName() + " is " + size + " mb.\n"
					+ "You can't edit files bigger than " + maxSize + " mb", "File is too large!", JOptionPane.ERROR_MESSAGE);

			return;
		}

		// Checking if the file is not already opened.
		for(int i = 0 ; i < documents.size() ; i++)
		{
			if (documents.get(i).getPath() != null && documents.get(i).getPath().equals(file.getAbsolutePath()))
			{
				tabPane.setSelectedIndex(i);
				return;
			}
		}

		ZoneDocument.DocumentType type = null;
		
		for(ZoneDocument.DocumentType doc_type : ZoneDocument.DocumentType.values())
		{			
			if (file.getName().toLowerCase().endsWith(doc_type.getSuffix()))
			{
				if (file.getAbsolutePath().contains(doc_type.getLocationType().getPath()))
					type = doc_type;
			}
		}
		
		if (type == null)
		{
			int choice = JOptionPane.showConfirmDialog(editorFrame, "File " + file.getName() + " is of unkown type. Open as a Zone file?", 
					"File is of unkown type!", JOptionPane.WARNING_MESSAGE);
			
			if (choice == JOptionPane.NO_OPTION)
				return;
			
			type = ZoneDocument.DocumentType.ZONE;
		}
		
		openDocument(file, type);
	}
	
	//--------------------------------------------------------------------------
	
	private void setPaneTooltip(ZoneDocument document)
	{
		int index = documents.indexOf(document);
		File file = document.getFile();
		
		tabPane.setToolTipTextAt(index, file != null ? file.getAbsolutePath() : "not saved");
	}
	
	//--------------------------------------------------------------------------
	
	private void openDocument(File file, ZoneDocument.DocumentType type)
	{
		ZoneDocument doc = new ZoneDocument(type, tabPane, file, this, propertySheetPanel, popupMenu, zoneClassList);
		documents.add(doc);
		
		int index = documents.size()-1;
		
		tabPane.setSelectedIndex(index);		
		tabPane.setTabComponentAt(index, new ButtonTabComponent(tabPane, doc, this));
		
		setPaneTooltip(doc);
		
		currentDocument = doc;
		
		editorFrame.onDocumentOpened(doc);
	}
	
	//--------------------------------------------------------------------------
	
	public void saveDocument()
	{
		if (currentDocument == null)
			return;
		
		try
		{
			String oldPath = currentDocument.getPath();
			
			currentDocument.save();
			tabPane.repaint();
			
			if (oldPath == null)
				editorFrame.onDocumentOpened(currentDocument);
			
			setPaneTooltip(currentDocument);
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null,"Couldn't save document " + currentDocument.getTitle() + ".\n" + ex,"Error!",JOptionPane.ERROR_MESSAGE);
				
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void saveDocumentAs()
	{
		if (currentDocument == null)
			return;
		
		try
		{							
			File file = showSaveDialog(currentDocument.getDocumentType());
			
			if (file != null)
			{
				String oldPath = currentDocument.getPath();
				
				currentDocument.save(file);
				tabPane.repaint();
				
				if (oldPath == null)
					editorFrame.onDocumentOpened(currentDocument);
				else if (!currentDocument.getPath().equals(oldPath))
					editorFrame.onDocumentSavedAs(currentDocument, oldPath);		
				
				setPaneTooltip(currentDocument);
			}
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null,"Couldn't save document " + currentDocument.getTitle() + ".\n" + ex,"Error!",JOptionPane.ERROR_MESSAGE);
				
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void saveAllDocuments()
	{
		for(ZoneDocument doc : documents)
		{
			try
			{
				doc.save();
				setPaneTooltip(doc);
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(null,"Couldn't save document " + doc.getTitle() + ".\n" + ex,"Error!",JOptionPane.ERROR_MESSAGE);
				
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}
		
		tabPane.repaint();
	}
	
	//--------------------------------------------------------------------------
	
	private void checkSaved(ZoneDocument document)
	{
		if (!document.isSaved())
		{
			if (JOptionPane.showConfirmDialog(editorFrame, "File  " + document.getTitle() + " is not saved. Save file?", "File not saved!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)			
			{
				try
				{
					document.save();
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(null,"Couldn't save document " + document.getTitle() + ".\n" + ex,"Error!",JOptionPane.ERROR_MESSAGE);
				
					DebugConsole.logger.log(Level.SEVERE, null, ex);
				}
			}
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void closeDocument()
	{
		if (currentDocument == null)
			return;
		
		checkSaved(currentDocument);
				
		editorFrame.onDocumentClosed(currentDocument);
		
		documents.remove(currentDocument);
		currentDocument.close();	
		
		checkEditStateForNoDocuments();
	}
	
	//--------------------------------------------------------------------------
	
	public void closeDocument(int index)
	{
		ZoneDocument document = documents.get(index);
		
		checkSaved(document);
		editorFrame.onDocumentClosed(document);
		
		documents.remove(document);
		document.close();
		
		checkEditStateForNoDocuments();
	}
	
	//--------------------------------------------------------------------------
	
	public void closeAllDocuments()
	{
		currentDocument = null;
		
		for(ZoneDocument doc : documents)
		{
			checkSaved(doc);
			editorFrame.onDocumentClosed(doc);
			doc.close();
		}
		
		documents.clear();
		
		checkEditStateForNoDocuments();
	}
	
	//--------------------------------------------------------------------------
	
	private void checkEditStateForNoDocuments()
	{
		if (documents.isEmpty())
			editorFrame.goToEditMode(ZoneDocument.EditType.TEXT_EDIT);
	}
	
	//--------------------------------------------------------------------------
	
	public static UUID openBrowseDefinitionsDialog(Class<? extends ZariaObjectDefinition> baseClass, String ... paths)
	{
		BrowseDefinitionsDialog dialog = new BrowseDefinitionsDialog(editorFrame, baseClass, paths);
		
		return dialog.getChosenID();
	}
	
	//--------------------------------------------------------------------------
	
	public static Object openListSearchDialog(String type, Object[] objects)
	{
		ListSearchDialog dialog = new ListSearchDialog(editorFrame, type, objects);
		dialog.setVisible(true);
		
		return dialog.getSelected();
	}
	
	//--------------------------------------------------------------------------
	
	public static String openBrowseScriptsDialog(String path)
	{
		BrowseScriptsDialog dialog = new BrowseScriptsDialog(editorFrame, path);
		
		return dialog.getScript();
	}
	
	//--------------------------------------------------------------------------
	
	public static Object openEditArrayDialog(String arrayName, Object array, boolean showingLinks)
	{
		ArrayEditDialog dialog = new ArrayEditDialog(editorFrame, arrayName, array, showingLinks);
		
		return dialog.getArray();
	}
	
	//--------------------------------------------------------------------------
	
	public void updateSettings()
	{
		for(ZoneDocument doc : documents)
			doc.updateSettings();
	}	

	//--------------------------------------------------------------------------

	public void setCtrlButton(boolean pressed)
	{
		for(ZoneDocument doc : documents)
		{
			for(ZoneDocument.EditType type : ZoneDocument.EditType.values())
			{
				EditorState state = doc.getEditorState(type);
				
				if (state != null)
					state.setCtrlButton(pressed);
			}
		}
	}
	
	//--------------------------------------------------------------------------

	public void setAltButton(boolean pressed)
	{
		for(ZoneDocument doc : documents)
		{
			for(ZoneDocument.EditType type : ZoneDocument.EditType.values())
			{
				EditorState state = doc.getEditorState(type);
				
				if (state != null)
					state.setAltButton(pressed);
			}
		}
	}
		
	//--------------------------------------------------------------------------

	public void setShiftButton(boolean pressed)
	{
		for(ZoneDocument doc : documents)
		{
			for(ZoneDocument.EditType type : ZoneDocument.EditType.values())
			{
				EditorState state = doc.getEditorState(type);
				
				if (state != null)
					state.setShiftButton(pressed);
			}
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void undo()
	{
		if (currentDocument != null)
			currentDocument.undo();
	}
	
	//--------------------------------------------------------------------------
	
	public void redo()
	{
		if (currentDocument != null)
			currentDocument.redo();
	}
	
	//--------------------------------------------------------------------------
	
	public void cut()
	{
		if (currentDocument != null)
			currentDocument.cut();
	}
	
	//--------------------------------------------------------------------------
	
	public void copy()
	{
		if (currentDocument != null)
			currentDocument.copy();
	}
	
	//--------------------------------------------------------------------------
	
	public void paste()
	{
		if (currentDocument != null)
			currentDocument.paste();
	}
	
	//--------------------------------------------------------------------------
	
	public void selectAll()
	{
		if (currentDocument != null)
			currentDocument.selectAll();
	}
	
	//--------------------------------------------------------------------------
	
	public void goToLine(int line)
	{
		try
		{
			if (currentDocument != null)
				currentDocument.goToLine(line);
		}
		catch (BadLocationException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void find(String text, FindOptions options)
	{
		if (currentDocument != null && !text.isEmpty())
		{
			if (!currentDocument.find(text, options))
				JOptionPane.showMessageDialog(editorFrame, "Couldn't find \"" + text + "\".", "Find result.", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void replace(String text, String replace, FindOptions options)
	{
		if (currentDocument != null && !text.isEmpty())
		{
			if (!currentDocument.replace(text, replace, options))
				JOptionPane.showMessageDialog(editorFrame, "Couldn't replace \"" + text + "\".", "Replace result.", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void replaceFind(String text, String replace, FindOptions options)
	{
		if (currentDocument != null && !text.isEmpty())
		{
			if (!currentDocument.replaceFind(text, replace, options))
				JOptionPane.showMessageDialog(editorFrame, "Couldn't replace \"" + text + "\".", "Replace result.", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void replaceAll(String text, String replace, FindOptions options)
	{
		if (currentDocument != null && !text.isEmpty())
		{
			if (currentDocument.replaceAll(text, replace, options) < 0)
				JOptionPane.showMessageDialog(editorFrame, "Couldn't replace \"" + text + "\".", "Replace result.", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void unmark()
	{
		if (currentDocument != null)
			currentDocument.unmark();
	}
	
	//--------------------------------------------------------------------------
	
	public void editSelected()
	{
		if (currentDocument != null)
			currentDocument.editSelected();
	}
	
	//--------------------------------------------------------------------------
	
	public void setEditingObjectEnabled(boolean enabled)
	{
		editorFrame.setEditingObjectEnabled(enabled);
	}
	
	//--------------------------------------------------------------------------
	
	public boolean isFileOpened(String path)
	{
		for(ZoneDocument doc : documents)
		{
			if (doc.getPath() != null && doc.getPath().equals(path))
				return true;
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void stateChanged(ChangeEvent event)
	{		
		int index = tabPane.getSelectedIndex();
		
		popupMenu.removeAll();
		popupMenu.setVisible(false);
		
		if (documents.size() > 0 && index >= 0)
		{			
			if (currentDocument != null)
				currentDocument.onFocusLost();
			
			currentDocument = documents.get(index);
			currentDocument.onFocusGained();
			editorFrame.onDocumentSwitched(currentDocument);
		}
		else
		{
			currentDocument = null;
		}
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void caretUpdate(CaretEvent ce)
	{
		RTextArea textComp = (RTextArea) ce.getSource(); 		
		editorFrame.onCursorPostion(textComp.getCaretLineNumber()+1, textComp.getCaretOffsetFromLineStart()+1);
	}
	
	//--------------------------------------------------------------------------
	
	private static FileFilter createFileFilter(ZoneDocument.DocumentType type)
	{
		return new AssetFileFilter(type.getName(), new String[]{type.getSuffix()});
	}
	
	//--------------------------------------------------------------------------
	
	public static File showSaveDialog(ZoneDocument.DocumentType type)
	{
		return showSaveDialog(type, type.getLocationType().getPath(), true);
	}
	
	//--------------------------------------------------------------------------
	
	public static File showSaveDialog(ZoneDocument.DocumentType type, String path, boolean storePath)
	{				
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(lastPath == null ? path : lastPath.startsWith(path) ? lastPath : path));
		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		
		fileChooser.addChoosableFileFilter(createFileFilter(type));
		
		if (fileChooser.showSaveDialog(editorFrame) == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			
			String name = file.getName().toLowerCase();
			
			if (!name.endsWith(type.getSuffix()))
				file = new File(file.getAbsolutePath() + type.getSuffix());
			
			if (file.exists())
			{
				if (JOptionPane.showConfirmDialog(editorFrame, "File " + file.getName() + " already exits. Overwrite file?", "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION)			
					file = showSaveDialog(type);
			}
			
			if (storePath)
				lastPath = file.getAbsolutePath();
			
			return file;
		}		
		else
			return null;
	}
		
	//--------------------------------------------------------------------------
	
	public static File showOpenDialog()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(lastPath == null ? EditorApplication.getAssetsLocation() : lastPath));
		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		
		for(ZoneDocument.DocumentType type : ZoneDocument.DocumentType.values())
			fileChooser.addChoosableFileFilter(createFileFilter(type));

		if (fileChooser.showOpenDialog(editorFrame) == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			lastPath = file.getAbsolutePath();
			return file;
		}
		else
			return null;
	}

	//--------------------------------------------------------------------------
	
	public static <T> T showWaitDialog(String message, WaitDialog.ResultRunnable<T> runnable)
	{
		WaitDialog<T> dialog = new WaitDialog<>(editorFrame, runnable, message);
		dialog.run();		
		T result = dialog.getResult();
		
		// These operations usuall allocate a lot of memory. Once they're done it's a good idea to force gc.
		try
		{
			Thread.sleep(25);
			System.gc();
			Thread.sleep(25);
		}
		catch (InterruptedException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}		
		
		return result;
	}
	
	//--------------------------------------------------------------------------
	
	public static void openEditorDialog(ZariaObjectDefinition definition)
	{
		assert EditorFactory.hasEditor(definition.getClass());
		
		try
		{
			EditorDialog editor = EditorFactory.getEditor(definition, editorFrame);
			editor.setVisible(true);
		}
		catch(Exception ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}				
	}
	
	//--------------------------------------------------------------------------
	
	public static void showInfoDialog(String title, String info)
	{
		ExtendedInformationDialog.show(editorFrame, title, info);
	}
	
	
	//--------------------------------------------------------------------------

	public ZoneDocument getCurrentDocument()
	{
		return currentDocument;
	}
	
	//--------------------------------------------------------------------------
}
