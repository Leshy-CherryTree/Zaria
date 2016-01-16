/****************************************/
/* FileListDataListener.java 			*/
/* Created on: 27-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.listeners;

import eu.cherrytree.zaria.editor.classlist.ZoneClass;
import eu.cherrytree.zaria.editor.datamodels.definitions.ObjectDefinitonTreeModel;
import eu.cherrytree.zaria.editor.datamodels.file.FileNameWrapper;
import eu.cherrytree.zaria.editor.debug.DebugConsole;

import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class FileListSelectionListener implements ListSelectionListener
{
	//--------------------------------------------------------------------------
	
	private ObjectDefinitonTreeModel model;
	private FileNameWrapper lastSelected;
	
	//--------------------------------------------------------------------------

	public FileListSelectionListener(ObjectDefinitonTreeModel model)
	{
		this.model = model;
	}
	
	//--------------------------------------------------------------------------
		
	@Override
	public void valueChanged(ListSelectionEvent evt)
	{
		JList list = (JList) evt.getSource();
		
		FileNameWrapper selected = (FileNameWrapper) list.getSelectedValue();
		
		if(lastSelected != selected)
		{			
			try
			{
				model.setFile(selected.getFile());
				lastSelected = selected;
			}
			catch (IOException | ZoneClass.InvalidClassHierarchyException | InstantiationException | IllegalAccessException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}
	}

	//--------------------------------------------------------------------------
}
