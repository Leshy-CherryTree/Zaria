/****************************************/
/* ZoneFileListCellRenderer.java        */
/* Created on: 05-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.components;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.datamodels.file.FileNameWrapper;
import eu.cherrytree.zaria.editor.document.DocumentManager;
import eu.cherrytree.zaria.editor.document.ZoneDocument;

import java.awt.Component;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneFileListCellRenderer extends DefaultListCellRenderer
{
	//--------------------------------------------------------------------------
	
	private enum FileType
	{
		ZONE,
		SCRIPT,
		UNKNOWN;
	}
	
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private DocumentManager documentManager;

	//--------------------------------------------------------------------------
	
	public ZoneFileListCellRenderer()
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
		
	public ZoneFileListCellRenderer(DocumentManager documentManager)
	{
		this.documentManager = documentManager;
	}
	
	//--------------------------------------------------------------------------
	
	private FileType getFileType(String path)
	{	
		if(ZoneDocument.isZone(path))
			return FileType.ZONE;
		
		if(ZoneDocument.isScript(path))
			return FileType.SCRIPT;
		
		return FileType.UNKNOWN;
	}
	
	//--------------------------------------------------------------------------
				
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		ZoneFileListCellRenderer comp = (ZoneFileListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		FileNameWrapper wrapper = (FileNameWrapper) value;
		
		String path = wrapper.getFile().getAbsolutePath();
		
		FileType type = getFileType(path);
		
		switch(type)
		{				
			case ZONE:
				if(documentManager == null || documentManager.isFileOpened(wrapper.getFile().getAbsolutePath()))
					comp.setIcon(EditorApplication.icons[0]);
				else
					comp.setIcon(EditorApplication.icons[2]);
				break;
				
			case SCRIPT:
				if(documentManager == null || documentManager.isFileOpened(wrapper.getFile().getAbsolutePath()))
					comp.setIcon(EditorApplication.icons[4]);
				else
					comp.setIcon(EditorApplication.icons[6]);
				break;
				
			case UNKNOWN:
			{
				comp.setIcon(EditorApplication.icons[3]);
				break;
			}
		}		
		
		
		return comp;
	}
	
	//--------------------------------------------------------------------------
}