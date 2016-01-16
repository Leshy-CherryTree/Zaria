/****************************************/
/* FileTransferHandler.java 			*/
/* Created on: 23-Feb-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.file;

import eu.cherrytree.zaria.editor.EditorApplication;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class FileTransferHandler extends TransferHandler
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private String basePath;
	
	//--------------------------------------------------------------------------
	
	public FileTransferHandler(String basePath)
	{
		this.basePath = basePath;
	}
			
	//--------------------------------------------------------------------------

	@Override
	public int getSourceActions(JComponent c)
	{
		return TransferHandler.COPY;
	}

	//--------------------------------------------------------------------------

	@Override
	protected Transferable createTransferable(JComponent comp)
	{
		JList list = (JList) comp;
		FileNameWrapper selected = (FileNameWrapper) list.getSelectedValue();
		String path = selected.getFile().getAbsolutePath();
		
		if(path.startsWith(basePath))
		{
			String path_beg = basePath;
			
			if(path.startsWith(EditorApplication.getAssetsLocation()))
				path_beg = EditorApplication.getAssetsLocation();
			else if(path.startsWith(EditorApplication.getScriptsLocation()))
				path_beg = EditorApplication.getScriptsLocation();
					
			return new StringSelection("\"" + path.substring(path.indexOf(path_beg) + path_beg.length()).replace("\\", "/") + "\"");	
		}
		else
			return null;
	}

	//--------------------------------------------------------------------------
}
