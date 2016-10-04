/****************************************/
/* FileViewController.java				*/
/* Created on: 03-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.listeners;

import eu.cherrytree.zaria.editor.datamodels.file.FileNameWrapper;
import eu.cherrytree.zaria.editor.datamodels.file.FileListModel;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import java.io.File;
import java.util.logging.Level;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class FileViewController implements TreeSelectionListener
{
	//--------------------------------------------------------------------------
	
	private FileListModel listModel;
	
	//--------------------------------------------------------------------------


	public FileViewController(FileListModel listModel)
	{
		this.listModel = listModel;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void valueChanged(TreeSelectionEvent event)
	{
		try
		{
			File file = ((FileNameWrapper) event.getPath().getLastPathComponent()).getFile();		       
			listModel.setDirectory(file);
		}
		catch (Exception ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}

	//--------------------------------------------------------------------------
}
