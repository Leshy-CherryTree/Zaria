/****************************************/
/* ZoneGraph.java						*/
/* Created on: 16-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph;

import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.ZoneDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneGraph extends mxGraph
{
	//--------------------------------------------------------------------------
	
	private static final String arrowStyle = "fontColor=#000000;edgeStyle=entityRelationEdgeStyle;labelBackgroundColor=#ffffff;shape=connector;rounded=true;strokeColor=#425f5f;";
			
	//--------------------------------------------------------------------------
	
	// Ids of nodes can change so this is not a hash map.
	private ArrayList<ZoneGraphNode> nodes = new ArrayList<>();
	private ZoneDocument document;		
	
	//--------------------------------------------------------------------------

	public ZoneGraph(ZoneDocument document)
	{
		this.document = document;
		
		setAllowDanglingEdges(false);
		setCellsDeletable(true);
		setCellsDisconnectable(true);
		setCellsMovable(true);
		setCellsResizable(false);
		setGridEnabled(true);
		setGridSize(20);
		setKeepEdgesInBackground(true);	
		setKeepEdgesInForeground(false);
		setCellsEditable(false);
		setAllowNegativeCoordinates(false);
		setDropEnabled(false);		
			
		Map<String, Object> edgeStyle = new HashMap<>();
		
		edgeStyle.put(mxConstants.STYLE_STROKECOLOR,				"#425f5f");
		edgeStyle.put(mxConstants.STYLE_FONTCOLOR,				"#000000");
		edgeStyle.put(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR,	"#ffffff");

		mxStylesheet sheet = new mxStylesheet();
		sheet.setDefaultEdgeStyle(edgeStyle);
		setStylesheet(sheet);						
	}

	//--------------------------------------------------------------------------
	
	@Override
	public Object createEdge(Object parent, String id, Object value, Object source, Object target, String style)
	{
		ZoneGraphPort port = (ZoneGraphPort) source;
		
		if(port.isInput())
			return super.createEdge(parent, id, value, source, target, arrowStyle + "startArrow=;endArrow=classic;");
		else
			return super.createEdge(parent, id, value, source, target, arrowStyle + "startArrow=classic;endArrow=;");
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public boolean isPort(Object object)
	{
		return false;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean isCellSelectable(Object object)
	{
		return model.isEdge(object) || object instanceof ZoneGraphNode;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public boolean isValidConnection(Object source, Object target)
	{
		if(source instanceof ZoneGraphPort && target instanceof ZoneGraphPort)
		{		
			ZoneGraphPort src = (ZoneGraphPort) source;
			ZoneGraphPort tgt = (ZoneGraphPort) target;
				
			if(src.isInput() && !tgt.isInput())
			{
				return	src.getParent() != tgt.getParent() && 
						(src.isConnectable() || tgt.isConnectable()) && 
						src.getDefinitionClass().isAssignableFrom(tgt.getDefinitionClass()) &&
						!src.linksContain(tgt.getParentUUID());
			}
			else if(!src.isInput() && tgt.isInput())
			{
				return	src.getParent() != tgt.getParent() && 
						(src.isConnectable() || tgt.isConnectable()) && 
						tgt.getDefinitionClass().isAssignableFrom(src.getDefinitionClass()) &&
						!tgt.linksContain(src.getParentUUID());
			}
		}
		
		return false;
	}
			
	//--------------------------------------------------------------------------

	@Override
	public String getToolTipForCell(Object object)
	{
		if(model.isEdge(object))
			return super.getToolTipForCell(object);
		else if(object instanceof ZoneGraphElement)
			return ((ZoneGraphElement)object).getToolTip();
		else
			return null;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public boolean isCellFoldable(Object object, boolean collapse)
	{
		return object instanceof ZoneGraphNode;
	}

	//--------------------------------------------------------------------------		
	
	public void addNode(ZoneGraphNode node)
	{
		addCell(node, getDefaultParent());
		
		assert !nodes.contains(node) : "Node " + node.getDefinition().getID() + " was already added to the graph.";
		
		nodes.add(node);
	}
	
	//--------------------------------------------------------------------------
	
	public void removeNode(ZoneGraphNode node)
	{
		assert nodes.contains(node) : "Node " + node.getDefinition().getID() + " was not added to the graph.";
		
		nodes.remove(node);
	}
	
	//--------------------------------------------------------------------------
	
	public ZoneGraphNode findNode(UUID uuid)
	{
		for(ZoneGraphNode node : nodes)
		{	
			if(node.getDefinition().getUUID().equals(uuid))
				return node;
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public void setLinks(ZoneGraphNode node)
	{
		setEventsEnabled(false);
		
		int nof_ports = node.getNumberOfInputs();
			
		for(int i = 0 ; i < nof_ports ; i++)
			setLinks(node.getInputPort(i));
		
		setEventsEnabled(true);
	}
	
	//--------------------------------------------------------------------------
	
	public void setLinks()
	{
		setEventsEnabled(false);
		
		for(ZoneGraphNode node : nodes)
		{
			try
			{
				node.updateLinkLists();
			}
			catch(IllegalArgumentException | IllegalAccessException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
			
			int nof_ports = node.getNumberOfInputs();
			
			for(int i = 0 ; i < nof_ports ; i++)
				setLinks(node.getInputPort(i));
		}
		
		setEventsEnabled(true);
	}
	
	//--------------------------------------------------------------------------
	
	private void setLinks(ZoneGraphPort port)
	{		
		mxICell[] toremove = new mxICell[port.getEdgeCount()];
		
		for(int i = 0 ; i < port.getEdgeCount() ; i++)
			toremove[i] = port.getEdgeAt(i);

		removeCells(toremove);
		
		ArrayList<ZoneGraphPort.LinkInfo> links = port.getLinks();
		
		for(ZoneGraphPort.LinkInfo link : links)
		{
			ZoneGraphNode node = findNode(link.getUUID());
			
			if(node != null)
				insertEdge(getDefaultParent(), null, null, port, node.getOutputPort());			
		}			
	}
	
	//--------------------------------------------------------------------------

	public ZoneDocument getDocument()
	{
		return document;
	}
	
	//--------------------------------------------------------------------------
}
