/****************************************/
/* GraphEditorState.java                */
/* Created on: 14-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;

import eu.cherrytree.zaria.editor.classlist.ZoneClass;
import eu.cherrytree.zaria.editor.classlist.ZoneClassList;
import eu.cherrytree.zaria.editor.database.DataBase;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.dialogs.WaitDialog;
import eu.cherrytree.zaria.editor.graph.GraphClipboard;
import eu.cherrytree.zaria.editor.graph.ZoneGraphNode;
import eu.cherrytree.zaria.editor.graph.ZoneGraph;
import eu.cherrytree.zaria.editor.graph.ZoneGraphComponent;
import eu.cherrytree.zaria.editor.graph.ZoneGraphInputPort;
import eu.cherrytree.zaria.editor.graph.ZoneGraphManager;
import eu.cherrytree.zaria.editor.graph.ZoneGraphOutputPort;
import eu.cherrytree.zaria.editor.graph.ZoneGraphPort;
import eu.cherrytree.zaria.editor.graph.ZoneGraphTransferHandler;
import eu.cherrytree.zaria.editor.graph.actions.CopyAction;
import eu.cherrytree.zaria.editor.graph.actions.CutAction;
import eu.cherrytree.zaria.editor.graph.actions.PasteAction;
import eu.cherrytree.zaria.editor.graph.undo.AddUndoAction;
import eu.cherrytree.zaria.editor.graph.undo.ConnectUndoAction;
import eu.cherrytree.zaria.editor.graph.undo.DisconnectUndoAction;
import eu.cherrytree.zaria.editor.graph.undo.MoveUndoAction;
import eu.cherrytree.zaria.editor.graph.undo.RemoveUndoAction;
import eu.cherrytree.zaria.editor.ReflectionTools;
import eu.cherrytree.zaria.editor.properties.Property;
import eu.cherrytree.zaria.editor.properties.PropertyTools;
import eu.cherrytree.zaria.editor.properties.ZoneProperty;
import eu.cherrytree.zaria.editor.properties.propertysheet.PropertySheetPanel;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.editor.undo.UndoAction;
import eu.cherrytree.zaria.editor.undo.UndoStack;
import eu.cherrytree.zaria.editor.undo.UndoTarget;
import eu.cherrytree.zaria.serialization.LinkArray;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class GraphEditorState implements EditorState, MouseWheelListener, ZoneGraphManager, UndoTarget, mxEventSource.mxIEventListener, MouseListener, MouseMotionListener, PropertyChangeListener
{
	//--------------------------------------------------------------------------
		
	private class AddNodeMenuItem extends JMenuItem
	{
		public AddNodeMenuItem(final ZoneClass cls, final ZoneGraphPort port, final int srcX, final int srcY)
		{
			super(cls.getName(), cls.getIcon());
			
			addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					ZoneGraphNode node = new ZoneGraphNode(ReflectionTools.createNewDefinition(cls.getObjectClass()));
					
					if(node.getDefinition() != null)
					{
						addNode(node, srcX + (int) node.getGeometry().getWidth()/2 + 20, srcY);
						
						graph.setEventsEnabled(false);
						
						graph.insertEdge(graph.getDefaultParent(), null, null, port, node.getOutputPort());
						
						graph.setEventsEnabled(true);
						
						ZoneGraphNode src_node = (ZoneGraphNode) port.getParent();
						src_node.connectionAdded(port.getName(), node.getDefinition().getUUID());
						
						graph.getModel().setStyle(src_node, src_node.getNodeStyle());
					}
				}
			});
		}	
	}
			
	//--------------------------------------------------------------------------
		
	private class OpenFileMenuItem extends JMenuItem
	{	
		private String fileName;
		
		public OpenFileMenuItem(String definitionName, String fileName)
		{						
			super(definitionName);
			
			this.fileName = fileName;
			
			addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					if(OpenFileMenuItem.this.fileName != null)
						document.getDocumentManager().openDocument(new File(OpenFileMenuItem.this.fileName));
				}
			});						
		}	

		@Override
		public boolean isEnabled()
		{
			return fileName != null ? super.isEnabled() : false;
		}				
	}
			
	//--------------------------------------------------------------------------
	
	private class BrowseMenuItem extends JMenuItem
	{
		public BrowseMenuItem(final ZoneGraphPort port, final String ... paths)
		{
			super("Browse...");
					
			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e)
				{
					UUID uuid = DocumentManager.openBrowseDefinitionsDialog(port.getDefinitionClass(), paths);
					
					boolean found = false;
							
					for(ZoneGraphPort.LinkInfo linkInfo : port.getLinks())
					{
						if(linkInfo.getUUID().equals(uuid))
						{
							found = true;
							break;
						}
					}

					if(found)
						return;
					
					ZoneGraphNode node = graph.findNode(uuid);
					
					if(node != null)
					{
						graph.setEventsEnabled(false);

						graph.insertEdge(graph.getDefaultParent(), null, null, port, node.getOutputPort());

						graph.setEventsEnabled(true);
					}
					
					if(uuid != null)
					{
						ZoneGraphNode src_node = (ZoneGraphNode) port.getParent();
						src_node.connectionAdded(port.getName(), uuid);
						
						graph.getModel().setStyle(src_node, src_node.getNodeStyle());						
					}
				}
			});
		}	
	}
	
	//--------------------------------------------------------------------------
	
	private class CopyNodesMenuItem extends JMenuItem
	{
		public CopyNodesMenuItem(final ZoneGraphNode[] nodes)
		{
			super("Copy");
			
			addActionListener(new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					graphClipboard.setNodes(nodes);
				}
			});
			
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private class CutNodesMenuItem extends JMenuItem
	{
		public CutNodesMenuItem(final ZoneGraphNode[] nodes)
		{
			super("Cut");
			
			addActionListener(new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					graphClipboard.setNodes(nodes);					
					graph.removeCells(nodes);
				}
			});
			
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private class CopyUUIDsMenuItem extends JMenuItem
	{
		public CopyUUIDsMenuItem(final ZoneGraphNode[] nodes)
		{
			super("CopyUUIDs");
			
			addActionListener(new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					String uuids = "";
					
					for (int i = 0 ; i < nodes.length ; i++)
					{
						uuids += "\"" + nodes[i].getDefinition().getUUID() + "\"";
						
						if (i < nodes.length-1)
							uuids += ",";
						
						uuids += "\n";
					}
					
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(uuids), null);
				}
			});
			
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private class PasteNodesMenuItem extends JMenuItem
	{
		public PasteNodesMenuItem(final int x, final int y)
		{
			super("Paste");
			
			addActionListener(new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent ae)
				{
					ZoneGraphNode [] nodes = graphClipboard.getNodes();

					if(nodes != null)
					{
						for(ZoneGraphNode node : nodes)
						{
							node.applyMetadata();
							addNode(node, x, y);
						}

						for(ZoneGraphNode node : nodes)
							graph.setLinks(node);
					}
				}
			});
			
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private class DeleteNodesMenuItem extends JMenuItem
	{				
		public DeleteNodesMenuItem(final ZoneGraphNode[] nodes)
		{
			super("Delete");
			
			addActionListener(new ActionListener()
			{				
				@Override
				public void actionPerformed(ActionEvent ae)
				{			
					graph.removeCells(nodes);
				}
			});
			
		}		
	}
	
	//--------------------------------------------------------------------------
	
	private static GraphClipboard graphClipboard = new GraphClipboard();
	
	//--------------------------------------------------------------------------
	
	private ObjectMapper objectMapper = Serializer.createDeserializationMapper();
	
	private ArrayList<ZoneGraphNode> nodes = new ArrayList<>();
	private ArrayList<ZoneGraphNode> selectedNodes = new ArrayList<>();
	
	private ZoneGraphComponent graphComponent;
	private ZoneGraph graph;
	
	private PropertySheetPanel propertySheetPanel;
	private ZoneDocument document;
	
	private Point mouseScrollPoint;
	
	private JPopupMenu popupMenu;
	private ZoneClassList zoneClassList;
	
	private UndoStack<GraphEditorState> undoStack;
	
	private double zoomFactor = 1.0f;
	private boolean ctrlPressed = false;
	private boolean shiftPressed = false;
	
	//--------------------------------------------------------------------------
	
	public GraphEditorState(String text, ZoneDocument document, PropertySheetPanel propertySheetPanel, JPopupMenu popupMenu, ZoneClassList zoneClassList)
	{
		assert document.getDocumentType() == ZoneDocument.DocumentType.ZONE;
		
		this.propertySheetPanel = propertySheetPanel;
		this.document = document;
		this.popupMenu = popupMenu;
		this.zoneClassList =  zoneClassList;

		graph = new ZoneGraph(document);
				
		graphComponent = new ZoneGraphComponent(graph, this);
		graphComponent.addMouseWheelListener(this);
		graphComponent.setTransferHandler(new ZoneGraphTransferHandler(this));
		graphComponent.getConnectionHandler().addListener(null, this);				
		graphComponent.setAutoscrolls(true);
		graphComponent.getGraphControl().addMouseListener(this);		
		graphComponent.getGraphControl().addMouseMotionListener(this);	
		
		undoStack = new UndoStack(this);			
	}
	
	//--------------------------------------------------------------------------

	public static GraphClipboard getGraphClipboard()
	{
		return graphClipboard;
	}

	//--------------------------------------------------------------------------

	@Override
	public void onSave()
	{
		graphComponent.setName(document.getFile().getName());
	}

	//--------------------------------------------------------------------------

	@Override
	public void undo()
	{
		undoStack.undo();
	}

	//--------------------------------------------------------------------------

	@Override
	public void redo()
	{
		undoStack.redo();
	}

	//--------------------------------------------------------------------------

	@Override
	public void cut()
	{
		CutAction.perform(graph);
	}

	//--------------------------------------------------------------------------

	@Override
	public void copy()
	{
		CopyAction.perform(graph);
	}

	//--------------------------------------------------------------------------

	@Override
	public void paste()
	{
		try
		{
			PasteAction.perform(graph, this);
		}
		catch (SecurityException | IllegalArgumentException | NoSuchFieldException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}

	//--------------------------------------------------------------------------

	@Override
	public void selectAll()
	{
		graph.selectAll();
	}

	//--------------------------------------------------------------------------
	
	@Override
	public void editSelected()
	{							
//		if(selectedNodes.size() == 1)
//		{
//			showProperties(selectedNodes.get(0));
//		}						
	}
	
	//--------------------------------------------------------------------------

	@Override
	public boolean find(String text, FindOptions options)
	{
		// TODO Implement this.
		return false;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean replace(String text, String replace, FindOptions options)
	{
		throw new UnsupportedOperationException("Not supported.");
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean replaceFind(String text, String replace, FindOptions options)
	{
		throw new UnsupportedOperationException("Not supported.");
	}

	//--------------------------------------------------------------------------

	@Override
	public int replaceAll(String text, String replace, FindOptions options)
	{
		throw new UnsupportedOperationException("Not supported.");
	}

	//--------------------------------------------------------------------------
	
	@Override
	public Object getSelected()
	{
		return selectedNodes;
	}		
	
	//--------------------------------------------------------------------------

	@Override
	public void unmark()
	{
		// Not supported yet, but exception will cause problems.
	}

	//--------------------------------------------------------------------------

	@Override
	public void goToLine(int line) throws BadLocationException
	{
		throw new UnsupportedOperationException("Not supported.");
	}

	//--------------------------------------------------------------------------

	@Override
	public void attach(JComponent parent)
	{
		parent.add(graphComponent);
	}

	//--------------------------------------------------------------------------

	@Override
	public void detach(JComponent parent)
	{
		for(ZoneGraphNode n : nodes)
			propertySheetPanel.removePropertySheetChangeListener(n);
		
		undoStack.clear();
		
		parent.remove(graphComponent);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public boolean isDirty()
	{
		// The graph is always ready for update.
		return false;
	}

	//--------------------------------------------------------------------------
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe)
	{
		if (mwe.getWheelRotation() < 0)
		{
			if(ctrlPressed)
			{
				if(zoomFactor < 3.0f)
					zoomFactor += 0.04f;	
				
				graphComponent.zoomTo(zoomFactor, false);				
			}
			else if(shiftPressed)
			{
				scrollX(10);
			}
			else
			{
				scrollY(10);
			}			
		}
		else
		{
			if(ctrlPressed)
			{
				if(zoomFactor > 0.2f)
					zoomFactor -= 0.04f;
				
				graphComponent.zoomTo(zoomFactor, false);
			}
			else if(shiftPressed)
			{
				scrollX(-10);
			}
			else
			{
				scrollY(-10);
			}
		}		
						
		
	}
	
	//--------------------------------------------------------------------------
	
	public void resetZoom()
	{
		graphComponent.zoomTo(1.0f, false);
		zoomFactor = 1.0f;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public float getZoomFactor()
	{
		return (float) zoomFactor;
	}
	
	//--------------------------------------------------------------------------
	
	private void setContent()
	{
		graph.getModel().beginUpdate();
		
		for(ZoneGraphNode node : nodes)
			node.attach(graph);

		graph.setLinks();
		
		graph.getModel().endUpdate();

	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public ZoneDocument getDocument()
	{
		return document;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public ZariaObjectDefinition[] getDefinitions()
	{
		ZariaObjectDefinition [] definitions = new ZariaObjectDefinition[nodes.size()];

		for(int i = 0 ; i < definitions.length ; i++)
		{
			nodes.get(i).saveMetadata();
			definitions[i] = nodes.get(i).getDefinition();
		}

		return definitions;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getText()
	{
		return DocumentManager.showWaitDialog("Please wait...", new WaitDialog.ResultRunnable<String>() {

			private String string;
			
			@Override
			public String get()
			{
				return string;
			}

			@Override
			public void run()
			{
				string = document.getHeader();
							
				try
				{
					string += Serializer.getText(getDefinitions());
				}
				catch(JsonProcessingException ex)
				{
					JOptionPane.showMessageDialog(null, "Couldn't generate zone source for " + document.getTitle() +  ".\n" + ex, "Error!", JOptionPane.ERROR_MESSAGE);

					DebugConsole.logger.log(Level.SEVERE, null, ex);
				}
				
				finish();
			}
		});
	}
	
	//--------------------------------------------------------------------------

	@Override
	public final void setText(String text)
	{
		ZariaObjectDefinition[] definitions;
		
		try
		{
			definitions = objectMapper.readValue(text, ZariaObjectDefinition[].class);			
		}
		catch(Exception ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
			
			JOptionPane.showMessageDialog(null, "Couldn't deserialize " + document.getTitle() +  ".\n" + ex, "Error!", JOptionPane.ERROR_MESSAGE);
			
			return;
		}

		graph = new ZoneGraph(document);
		graphComponent.setGraph(graph);
		
		if(document.getFile() != null)
			graphComponent.setName(document.getFile().getName());
		else
			graphComponent.setName("untitled.zone");
			
		
		graph.addListener(null, this);
		graph.getSelectionModel().addListener(null, this);
		graph.getModel().addListener(null, this);
		graph.getView().addListener(null, this);
		
		nodes.clear();
		selectedNodes.clear();
		
		for(ZariaObjectDefinition def : definitions)
		{
			ZoneGraphNode node = new ZoneGraphNode(def);
			validateId(node);
			nodes.add(node);
			node.applyMetadata();
		}
		
		setContent();
	}
			
	//--------------------------------------------------------------------------

	@Override
	public void addNode(ZoneGraphNode node, int x, int y)
	{
		doAdd(node, x, y);
		
		undoStack.addUndoAction(new AddUndoAction(node), true);
	}
	
	//--------------------------------------------------------------------------
	
	private void doAdd(ZoneGraphNode node, int x, int y)
	{
		assert !nodes.contains(node) : "Node " + node + " already added to the graph!";
		
		validateId(node);
		
		nodes.add(node);
		
		graph.getModel().beginUpdate();
		
		node.attach(graph);
		node.setPosition(x, y);

		try
		{
			node.updateLinkLists();
		}
		catch(IllegalArgumentException | IllegalAccessException ex)
		{
			throw new RuntimeException(ex);
		}
		
		graph.getModel().endUpdate();

		document.setModified();
	}

	//--------------------------------------------------------------------------

	@Override
	public void removeNode(ZoneGraphNode node)
	{
		doRemove(node);
		
		undoStack.addUndoAction(new RemoveUndoAction(node), true);
	}
	//--------------------------------------------------------------------------
	
	private void doRemove(ZoneGraphNode node)
	{
		graph.getModel().beginUpdate();
		
		graph.removeCells(new Object[]{node});
		
		graph.getModel().endUpdate();

		document.setModified();
	}
	
	//--------------------------------------------------------------------------
	
	private void validateId(ZoneGraphNode node)
	{
		String id = node.getDefinition().getID();
		int index = 0;
		
		if(id == null || id.isEmpty())
			id = node.getDefinitionClass().getSimpleName();
		
		boolean verified = false;
		
		while(!verified)
		{
			verified = true;
			
			for(ZoneGraphNode n : nodes)
			{
				if(n == node)
					continue;
				
				String n_id = n.getDefinition().getID();
				
				if(n_id != null)
				{
					if((index == 0 && n_id.equals(id)) || n_id.equals(id + "_" + index))
					{
						index++;
						verified = false;
						break;
					}
				}
			}
		}
		
		if(index > 0)
			id = id + "_" + index;
		
		Property[] properties = PropertyTools.getProperties(node.getDefinitionClass(), document);	
		
		Property idproperty = null;
		
		for(Property prop : properties)
		{
			if(prop.getName().equals("id"))
			{
				idproperty = prop;
				break;
			}
		}
		
		assert idproperty != null;
		
		idproperty.setValue(id);
		node.propertyChange(new PropertyChangeEvent(idproperty, "id", null, id));
	}
	
	//--------------------------------------------------------------------------
			
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		ZoneProperty property = (ZoneProperty) evt.getSource();

		if(property.getType() == LinkArray.class || property.getType().isArray())
		{					
			if(selectedNodes.size() == 1)
				showProperties(selectedNodes.get(0));
			else
				hideProperties();
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void showProperties(ZoneGraphNode node)
	{
		Property[] properties = PropertyTools.getProperties(node.getDefinitionClass(), document);		
		
		for (int i = 0, c = properties.length; i < c; i++)
			properties[i].readFromObject(node.getDefinition());
		
		propertySheetPanel.setProperties(properties);
						
		for(ZoneGraphNode n : nodes)
			propertySheetPanel.removePropertySheetChangeListener(n);
		
		propertySheetPanel.addPropertySheetChangeListener(node);
		
		for (int i = 0, c = properties.length; i < c; i++)
			properties[i].addPropertyChangeListener(this);
	}
	
	//--------------------------------------------------------------------------
	
	private void hideProperties()
	{
		propertySheetPanel.setProperties(new Property[0]);
		
		for(ZoneGraphNode n : nodes)
			propertySheetPanel.removePropertySheetChangeListener(n);
	}
	
	//--------------------------------------------------------------------------
	
	private void setSelected(ArrayList<mxCell> cells, boolean selected)
	{
		for(mxCell cell : cells)
		{
			if(cell instanceof ZoneGraphNode)
			{
				ZoneGraphNode node = (ZoneGraphNode) cell;
				
				if(selected)
					selectedNodes.add(node);	
				else
					selectedNodes.remove(node);										
				
				node.setSelected(selected);
				graph.getModel().setStyle(node, node.getNodeStyle());		
			}
		}
		
		if(selectedNodes.size() == 1)
		{
			showProperties(selectedNodes.get(0));		
			document.setEditingObjectEnabled(false);
		}
		else
		{
			hideProperties();
			document.setEditingObjectEnabled(false);
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void invoke(Object sender, mxEventObject evt)
	{
		switch(evt.getName())
		{
			case mxEvent.CHANGE:
				onSelectionChange(evt);
				break;
				
			case mxEvent.CONNECT:
				onCellsConnect(evt);
				break;
				
			case mxEvent.CELLS_REMOVED:
				onCellsRemoved(evt);
				break;
				
			case mxEvent.CELLS_MOVED:
				onCellsMoved(evt);
				break;
				
//			default:
//				System.out.println("EVENT: " + evt.getName() + " from: " + sender);
//				
//				for(String key : evt.getProperties().keySet())
//					System.out.println("key: " + key + " value: " + evt.getProperty(key));
//				break;				
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void onCellsMoved(mxEventObject evt)
	{			
		document.setModified();
		
		if(!undoStack.isLocked())
		{
			double dx = (double) evt.getProperty("dx");
			double dy = (double) evt.getProperty("dy");

			Object [] objects = (Object[]) evt.getProperty("cells");

			for(Object obj : objects)
			{
				if(obj instanceof ZoneGraphNode)
				{
					ZoneGraphNode node = (ZoneGraphNode) obj;					
					
					undoStack.addUndoAction(new MoveUndoAction(node, (float)(node.getGeometry().getX() - dx), (float)(node.getGeometry().getY() - dy)), true);
				}
			} 
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void onSelectionChange(mxEventObject evt)
	{
		for(String key : evt.getProperties().keySet())
		{
			if(evt.getProperty(key) instanceof ArrayList)
			{
				ArrayList<mxCell> changed_nodes = (ArrayList) evt.getProperty(key);
				
				// Yes, "add" is unselect, "remove" is select.
				switch(key)
				{
					case "added":
						setSelected(changed_nodes,false);
						break;
						
					case "removed":
						setSelected(changed_nodes,true);
						break;
				}
			}
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void onCellsConnect(mxEventObject evt)
	{				
		mxCell cell = (mxCell) evt.getProperty("cell");
		
		ZoneGraphPort srcport = (ZoneGraphPort) cell.getSource();		
		ZoneGraphNode src = (ZoneGraphNode) srcport.getParent();
		
		ZoneGraphPort tgtport = (ZoneGraphPort) cell.getTarget();
		ZoneGraphNode tgt = (ZoneGraphNode) tgtport.getParent();
		
		if(srcport.isInput())
		{
			src.connectionAdded(srcport.getName(), tgt.getDefinition().getUUID());
			
			if(!undoStack.isLocked())
				undoStack.addUndoAction(new ConnectUndoAction((ZoneGraphInputPort) srcport, (ZoneGraphOutputPort) tgtport), true);
			
			graph.getModel().setStyle(src, src.getNodeStyle());
		}
		else if(tgtport.isInput())
		{
			tgt.connectionAdded(tgtport.getName(), src.getDefinition().getUUID());
			
			if(!undoStack.isLocked())
				undoStack.addUndoAction(new ConnectUndoAction((ZoneGraphInputPort) tgtport, (ZoneGraphOutputPort) srcport), true);
			
			graph.getModel().setStyle(tgt, tgt.getNodeStyle());
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void disconnectRemovedNode(ZoneGraphNode node)
	{	
		// This is the most trivial implementation. If need be this can be 
		// optimized and extended.
		
		for(ZoneGraphNode n : nodes)
		{
			for(int i = 0 ; i < n.getNumberOfInputs() ; i++)
			{
				if(n.getInputPort(i).linksContain(node.getDefinition().getUUID()))
				{
					n.connectionRemoved(n.getInputPort(i).getName(), node.getDefinition().getUUID());

					if(!undoStack.isLocked())
						undoStack.addUndoAction(new DisconnectUndoAction((ZoneGraphInputPort) n.getInputPort(i), (ZoneGraphOutputPort) node.getOutputPort()), true);
					
					graph.getModel().setStyle(n, n.getNodeStyle());
				}
			}
		}
	}
	
	//--------------------------------------------------------------------------

	private void onCellsRemoved(mxEventObject evt)
	{
		Object [] objects = (Object[]) evt.getProperty("cells");
		
		ArrayList<ZoneGraphNode> toremove = new ArrayList<>();	
		ArrayList<mxCell> todisconnect = new ArrayList<>();
		
		for(Object obj : objects)
		{
			if(!(obj instanceof ZoneGraphInputPort.ExternalMarker))
			{
				if(obj instanceof ZoneGraphNode)
					toremove.add((ZoneGraphNode)obj);
				else if(obj instanceof mxCell)
					todisconnect.add((mxCell) obj);
				else
					throw new RuntimeException("Removing unkown object: " + obj);	
			}			
		} 
			
		for(ZoneGraphNode node : toremove)
		{
			disconnectRemovedNode(node);
			graph.removeNode(node);
			
			if(!undoStack.isLocked())
				undoStack.addUndoAction(new RemoveUndoAction(node), true);
			
			propertySheetPanel.removePropertySheetChangeListener(node);
		}
		
		for(mxCell cell : todisconnect)
		{
			ZoneGraphPort srcport = (ZoneGraphPort) cell.getSource();
			ZoneGraphNode src = (ZoneGraphNode) srcport.getParent();

			ZoneGraphPort tgtport = (ZoneGraphPort) cell.getTarget();
			ZoneGraphNode tgt = (ZoneGraphNode) tgtport.getParent();				

			// No need to disconnect nodes that just have been removed.
			if(!toremove.contains(tgt) && !toremove.contains(src))
			{
				if(srcport.isInput())
				{
					src.connectionRemoved(srcport.getName(), tgt.getDefinition().getUUID());

					if(!undoStack.isLocked())
						undoStack.addUndoAction(new DisconnectUndoAction((ZoneGraphInputPort) srcport, (ZoneGraphOutputPort) tgtport), true);
					
					graph.getModel().setStyle(src, src.getNodeStyle());
				}
				else if(tgtport.isInput())
				{
					tgt.connectionRemoved(tgtport.getName(), src.getDefinition().getUUID());

					if(!undoStack.isLocked())
						undoStack.addUndoAction(new DisconnectUndoAction((ZoneGraphInputPort) tgtport, (ZoneGraphOutputPort) srcport), true);
					
					graph.getModel().setStyle(tgt, tgt.getNodeStyle());
				}	
			}			
		}
		
		nodes.removeAll(toremove);
		
		document.setModified();
	}
	//--------------------------------------------------------------------------

	@Override
	public void onFocusGained(JComponent parent)
	{
		// Intentionally empty
	}

	//--------------------------------------------------------------------------

	@Override
	public void onFocusLost(JComponent parent)
	{
		for(ZoneGraphNode n : nodes)
			propertySheetPanel.removePropertySheetChangeListener(n);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void updateSettings()
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
	
	public void undoAdd(ZoneGraphNode node, UndoAction.Type type)
	{
		undoStack.lock();
		
		doRemove(node);
		
		undoStack.unlock();
		
		if(type == UndoAction.Type.UNDO)
			undoStack.addRedoAction(new RemoveUndoAction(node));
		else if(type == UndoAction.Type.REDO)
			undoStack.addUndoAction(new RemoveUndoAction(node), false);
	}
	
	//--------------------------------------------------------------------------
	
	public void undoMove(ZoneGraphNode node, float x, float y, UndoAction.Type type)
	{
		if(type == UndoAction.Type.UNDO)
			undoStack.addRedoAction(new MoveUndoAction(node, (float)node.getGeometry().getX(), (float)node.getGeometry().getY()));
		else if(type == UndoAction.Type.REDO)
			undoStack.addUndoAction(new MoveUndoAction(node, (float)node.getGeometry().getX(), (float)node.getGeometry().getY()), false);
				
		undoStack.lock();
		
		graph.getModel().beginUpdate();
		
		graph.moveCells(new Object[]{node}, x - node.getGeometry().getX(), y - node.getGeometry().getY());

		graph.getModel().endUpdate();

		document.setModified();
		
		undoStack.unlock();			
	}
	
	//--------------------------------------------------------------------------

	public void undoRemove(ZoneGraphNode node, UndoAction.Type type)
	{
		undoStack.lock();
		
		doAdd(node, (int)node.getGeometry().getX(), (int)node.getGeometry().getY());
		
		graph.setLinks(node);
				
		undoStack.unlock();
		
		if(type == UndoAction.Type.UNDO)
			undoStack.addRedoAction(new AddUndoAction(node));
		else if(type == UndoAction.Type.REDO)
			undoStack.addUndoAction(new AddUndoAction(node), false);
	}
	
	//--------------------------------------------------------------------------

	public void undoConnect(ZoneGraphInputPort inputPort, ZoneGraphOutputPort outputPort, UndoAction.Type type)
	{
		mxICell cell = null;
		
		for(int i = 0 ; i < inputPort.getEdgeCount() && cell == null; i++)
		{
			if(inputPort.getEdgeAt(i).getTerminal(false) == outputPort)
				cell = inputPort.getEdgeAt(i);
		}
		
		assert cell != null;
		
		undoStack.lock();	
		
		graph.getModel().beginUpdate();
		
		graph.removeCells(new Object[]{cell});
		
		graph.getModel().endUpdate();

		document.setModified();
		
		undoStack.unlock();	
		
		if(type == UndoAction.Type.UNDO)
			undoStack.addRedoAction(new DisconnectUndoAction(inputPort, outputPort));
		else if(type == UndoAction.Type.REDO)
			undoStack.addUndoAction(new DisconnectUndoAction(inputPort, outputPort), false);
	}
	
	//--------------------------------------------------------------------------

	public void undoDisconnect(ZoneGraphInputPort inputPort, ZoneGraphOutputPort outputPort, UndoAction.Type type)
	{
		undoStack.lock();	
		
		graph.getModel().beginUpdate();
		
		graph.insertEdge(graph.getDefaultParent(), null, "", inputPort, outputPort);
		
		graph.getModel().endUpdate();

		document.setModified();
		
		undoStack.unlock();	
			
		ZoneGraphNode input = (ZoneGraphNode) inputPort.getParent();
		ZoneGraphNode output = (ZoneGraphNode) outputPort.getParent();

		input.connectionAdded(inputPort.getName(), output.getDefinition().getUUID());
		
		if(type == UndoAction.Type.UNDO)
			undoStack.addRedoAction(new ConnectUndoAction(inputPort, outputPort));
		else if(type == UndoAction.Type.REDO)
			undoStack.addUndoAction(new ConnectUndoAction(inputPort, outputPort), false);
		
		graph.getModel().setStyle(input, input.getNodeStyle());
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseClicked(MouseEvent evt)
	{
		if(evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
		{
			Object cell = graphComponent.getCellAt(evt.getX(), evt.getY());

			if(cell instanceof ZoneGraphNode)
			{
				ZoneGraphNode node = (ZoneGraphNode) cell;

				graph.foldCells(!node.isCollapsed(), false, new Object[]{node});
			}
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mousePressed(MouseEvent evt)
	{
		if(evt.isPopupTrigger())
		{
			Object cell = graphComponent.getCellAt(evt.getX(), evt.getY());
			
			if(cell != null && ZoneGraphInputPort.class.isAssignableFrom(cell.getClass()))
			{				
				ZoneGraphPort port = (ZoneGraphPort) cell;
				
				if (port.isInput())
				{
					popupMenu.removeAll();				
					
					ArrayList<ZoneClass> classes = zoneClassList.getClasses(port.getDefinitionClass());										
					
					if(classes.isEmpty())
						return;
					
					HashMap<String , JMenu> categories = new HashMap<>();
					
					for(ZoneClass cls : classes)
					{
						JMenu menu;
						
						if(categories.containsKey(cls.getCategory()))
							menu = categories.get(cls.getCategory());
						else
						{
							menu = new JMenu(cls.getCategory());
							categories.put(cls.getCategory(), menu);
							popupMenu.add(menu);
						}
						
						menu.add(new AddNodeMenuItem(cls, port, evt.getX(), evt.getY()));
					}
										
					popupMenu.add(new BrowseMenuItem(port, document.getDocumentType().getLocationType().getPath()));
					
					popupMenu.show(graphComponent, evt.getX() - graphComponent.getHorizontalScrollBar().getValue(), evt.getY() - graphComponent.getVerticalScrollBar().getValue());
				}
			}
			else if(cell != null && ZoneGraphInputPort.ExternalMarker.class.isAssignableFrom(cell.getClass()))
			{				
				popupMenu.removeAll();	
				
				try
				{
					for(ZoneGraphPort.LinkInfo link : ((ZoneGraphInputPort.ExternalMarker)cell).getLinks())
						popupMenu.add(new OpenFileMenuItem(DataBase.getID(link.getUUID()), DataBase.getLocation(link.getUUID())));
					
					popupMenu.show(graphComponent, evt.getX() - graphComponent.getHorizontalScrollBar().getValue(), evt.getY() - graphComponent.getVerticalScrollBar().getValue());
				}
				catch (DataBase.DuplicateUUIDFoundException ex)
				{
					DebugConsole.logger.log(Level.SEVERE, null, ex);
				}								
			}
			else if(cell != null && selectedNodes.isEmpty())
			{
				if(ZoneGraphOutputPort.class.isAssignableFrom(cell.getClass()))
				{
					ZoneGraphOutputPort port = (ZoneGraphOutputPort) cell;
					showNodePopupMenu((ZoneGraphNode) port.getParent(), evt.getX() - graphComponent.getHorizontalScrollBar().getValue(), evt.getY() - graphComponent.getVerticalScrollBar().getValue());
				}
				else if(ZoneGraphNode.class.isAssignableFrom(cell.getClass()))
				{
					showNodePopupMenu((ZoneGraphNode) cell, evt.getX() - graphComponent.getHorizontalScrollBar().getValue(), evt.getY() - graphComponent.getVerticalScrollBar().getValue());
				}	
			}	
			else if(!selectedNodes.isEmpty())
			{
				showNodePopupMenu(selectedNodes, evt.getX() - graphComponent.getHorizontalScrollBar().getValue(), evt.getY() - graphComponent.getVerticalScrollBar().getValue());
			}
			else if(graphClipboard.getNodes() != null)
			{
				popupMenu.removeAll();
				
				popupMenu.add(new PasteNodesMenuItem(evt.getX() - graphComponent.getHorizontalScrollBar().getValue(), evt.getY() - graphComponent.getVerticalScrollBar().getValue()));
				
				popupMenu.show(graphComponent, evt.getX() - graphComponent.getHorizontalScrollBar().getValue(), evt.getY() - graphComponent.getVerticalScrollBar().getValue());
			}
				
		}		
		else if(evt.getButton() == MouseEvent.BUTTON2)
		{
			mouseScrollPoint = evt.getLocationOnScreen();
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseReleased(MouseEvent evt)
	{
		if(evt.getButton() == MouseEvent.BUTTON2)
		{
			mouseScrollPoint = null;
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseEntered(MouseEvent evt)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseExited(MouseEvent evt)
	{
		mouseScrollPoint = null;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseDragged(MouseEvent evt)
	{
		if(mouseScrollPoint != null)
		{
			int dx = evt.getXOnScreen() - mouseScrollPoint.x;
			int dy = evt.getYOnScreen() - mouseScrollPoint.y;
												
			mouseScrollPoint.x = evt.getXOnScreen();
			mouseScrollPoint.y = evt.getYOnScreen();
					
			scrollX(dx);
			scrollY(dy);		
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void scrollX(int dx)
	{
		int x = (int) (graphComponent.getHorizontalScrollBar().getValue() - dx * zoomFactor);
		graphComponent.getHorizontalScrollBar().setValue(x);
	}
	
	//--------------------------------------------------------------------------
	
	private void scrollY(int dy)
	{
		int y = (int) (graphComponent.getVerticalScrollBar().getValue() - dy * zoomFactor);
		graphComponent.getVerticalScrollBar().setValue(y);	
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void setCtrlButton(boolean pressed)
	{
		ctrlPressed = pressed;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void setAltButton(boolean pressed)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void setShiftButton(boolean pressed)
	{
		shiftPressed = pressed;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public int getScrollX()
	{
		return graphComponent.getHorizontalScrollBar().getValue();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public int getScrollY()
	{
		return graphComponent.getVerticalScrollBar().getValue();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseMoved(MouseEvent evt)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
	
	private void showNodePopupMenu(ZoneGraphNode node, int x, int y)
	{
		showNodePopupMenu(new ZoneGraphNode[]{node}, x , y);
	}
	
	//--------------------------------------------------------------------------
	
	private void showNodePopupMenu(ArrayList<ZoneGraphNode> nodes, int x, int y)
	{
		ZoneGraphNode[] node_array = new ZoneGraphNode[nodes.size()];
		nodes.toArray(node_array);
		
		showNodePopupMenu(node_array, x , y);
	}
	
	//--------------------------------------------------------------------------
	
	private void showNodePopupMenu(ZoneGraphNode[] nodes, int x, int y)
	{
		popupMenu.removeAll();
		
		popupMenu.add(new CutNodesMenuItem(nodes));
		popupMenu.add(new CopyNodesMenuItem(nodes));
		popupMenu.add(new CopyUUIDsMenuItem(nodes));
		popupMenu.add(new JPopupMenu.Separator());		
		popupMenu.add(new DeleteNodesMenuItem(nodes));				
				
		popupMenu.show(graphComponent, x, y);
	}
	
	//--------------------------------------------------------------------------
}
