/****************************************/
/* ScriptImportTransferHandler.java 	*/
/* Created on: 06-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.file;

import eu.cherrytree.zaria.editor.EditorApplication;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import java.io.File;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptImportTransferHandler extends TransferHandler
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
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
		
		if(path.startsWith(EditorApplication.getScriptsLocation()))
		{
			String name = path.substring(path.indexOf(EditorApplication.getScriptsLocation()) + EditorApplication.getScriptsLocation().length());
			name = name.replace(File.separator, ".").replace(".zonescript", "");
					
			return new StringSelection("import " + name + ";\n");	
		}
		else
			return null;
	}

	//--------------------------------------------------------------------------
}