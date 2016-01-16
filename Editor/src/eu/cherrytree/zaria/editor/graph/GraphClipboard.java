/****************************************/
/* GraphClipboard.java					*/
/* Created on: 24-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.cherrytree.zaria.editor.debug.DebugConsole;

import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import java.util.logging.Level;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class GraphClipboard implements ClipboardOwner
{
	//--------------------------------------------------------------------------
	
	public void setNodes(ZoneGraphNode[] nodes)
	{
		String nodedata;
		ZariaObjectDefinition [] definitions = new ZariaObjectDefinition[nodes.length];
		
		for(int i = 0 ; i < definitions.length ; i++)
		{
			nodes[i].saveMetadata();
			definitions[i] = nodes[i].getDefinition();
		}
		
		try
		{
			nodedata = Serializer.getText(definitions);
		}
		catch(JsonProcessingException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);			
			return;
		}

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents( new StringSelection(nodedata), this );
	}	

	//--------------------------------------------------------------------------
	
	public ZoneGraphNode[] getNodes()
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();		
		Transferable contents = clipboard.getContents(null);

		if((contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor))
		{
			try
			{
				String nodedata = (String) contents.getTransferData(DataFlavor.stringFlavor);	
				nodedata = nodedata.trim();
				
				// Make sure the data is formatted as an array.				
				if(nodedata.endsWith(","))
					nodedata = nodedata.substring(0,nodedata.length()-1);				
				if(!(nodedata.startsWith("[") && nodedata.endsWith("]")))
					nodedata = "[" + nodedata + "]";
								
				ZariaObjectDefinition[] definitions = Serializer.createDeserializationMapper().readValue(nodedata, ZariaObjectDefinition[].class);		
				
				ZoneGraphNode [] nodes = new ZoneGraphNode[definitions.length];
				
				for(int i = 0 ; i < definitions.length ; i++)
					nodes[i] = new ZoneGraphNode(definitions[i]);
				
				return nodes;
			}
			catch(JsonProcessingException ex)
			{
				// Do nothing as the data is not in proper format.
			}
			catch(Exception ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void lostOwnership(Clipboard clpbrd, Transferable t)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
}
