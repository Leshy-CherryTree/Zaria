/****************************************/
/* PaletteTransferHandler.java         */
/* Created on: 09-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.palette;

import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class PaletteTransferHandler extends TransferHandler
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
		PaletteElement element = (PaletteElement) ((JTree)comp).getSelectionPath().getLastPathComponent();
		
		if(Transferable.class.isAssignableFrom(element.getClass()))
			return (Transferable) element;
		else 
			return null;
	}

	//--------------------------------------------------------------------------
}
