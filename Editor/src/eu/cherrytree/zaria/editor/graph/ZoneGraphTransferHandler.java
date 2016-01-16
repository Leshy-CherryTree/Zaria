/****************************************/
/* ZoneGraphTransferHandler.java        */
/* Created on: 19-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph;

import com.mxgraph.swing.handler.mxGraphTransferHandler;
import eu.cherrytree.zaria.editor.debug.DebugConsole;

import eu.cherrytree.zaria.editor.datamodels.palette.PaletteObjectTemplate;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.IOException;

import java.util.logging.Level;

import javax.swing.JComponent;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneGraphTransferHandler extends mxGraphTransferHandler
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private ZoneGraphManager manager;
	
	//--------------------------------------------------------------------------

	public ZoneGraphTransferHandler(ZoneGraphManager manager)
	{
		super();
		this.manager = manager;
	}
		
	//-------------------------------------------------------------------------- 

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors)
	{
		if(comp instanceof ZoneGraphComponent)
		{
			for(DataFlavor df : transferFlavors)
			{
				if(df == PaletteObjectTemplate.graphDataFlavor)
					return true;
			}			
		}
		
		return super.canImport(comp, transferFlavors);
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean importData(JComponent comp, Transferable transferable)
	{
		boolean zoneObj = false;
		
		for(DataFlavor df : transferable.getTransferDataFlavors())
		{
			if(df == PaletteObjectTemplate.graphDataFlavor)
			{
				zoneObj = true;
				break;
			}
		}

		if(zoneObj)
		{			
			try
			{
				ZariaObjectDefinition def = (ZariaObjectDefinition) transferable.getTransferData(PaletteObjectTemplate.graphDataFlavor);
				ZoneGraphNode node = new ZoneGraphNode(def);
				
				manager.addNode(node,	(int)((comp.getMousePosition().x + manager.getScrollX()) * (1.0f / manager.getZoomFactor())), 
										(int)((comp.getMousePosition().y + manager.getScrollY()) * (1.0f / manager.getZoomFactor())));

				return true;
			}
			catch(UnsupportedFlavorException | IOException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);		
			}

			return false;
		}
		else
			return super.importData(comp, transferable);
	}
	
	//--------------------------------------------------------------------------	
}
