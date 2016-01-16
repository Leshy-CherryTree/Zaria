/****************************************/
/* ZoneGraphNode.java					*/
/* Created on: 16-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.editor.properties.ZoneProperty;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.annotations.WeakLink;
import eu.cherrytree.zaria.editor.document.ZoneMetadata;
import static eu.cherrytree.zaria.editor.graph.ZoneGraphElement.nodeMetrics;
import eu.cherrytree.zaria.editor.ReflectionTools;
import eu.cherrytree.zaria.editor.properties.PropertyTools;
import eu.cherrytree.zaria.serialization.DefinitionValidation;
import eu.cherrytree.zaria.serialization.Link;
import eu.cherrytree.zaria.serialization.LinkArray;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;



/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public final class ZoneGraphNode extends ZoneGraphElement implements PropertyChangeListener
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	private static final float size = 5.0f;
	
	//--------------------------------------------------------------------------
	
	private transient ZariaObjectDefinition definition;
	
	private ZoneGraphPort [] ports;
	private mxCell caption;
		
	private transient ZoneGraph graph;
		
	private boolean selected = false;
	private boolean validated = true;
	private String validationInfo = "";
	
	//--------------------------------------------------------------------------

	public ZoneGraphNode(ZariaObjectDefinition definition)
	{
		assert definition != null;
		
		this.definition = definition;
		setConnectable(false);
		setValue(definition.getClass().getSimpleName());											
		
		ArrayList<Field> fields = getAllFields(definition.getClass());
		
		ArrayList<Class> links = new ArrayList<>();
		ArrayList<Boolean> arrays = new ArrayList<>();
		ArrayList<String> names = new ArrayList<>();
		
		for(int i = 0 ; i < fields.size() ; i++)
		{
			if(!Modifier.isTransient(fields.get(i).getModifiers()))
			{
				if(Link.class.isAssignableFrom(fields.get(i).getType()))
				{
					Field field = fields.get(i);
					field.setAccessible(true);
					
					links.add(PropertyTools.getSpecializationClass(field));
					names.add(fields.get(i).getName());
					arrays.add(false);					
				}
				else if(LinkArray.class.isAssignableFrom(fields.get(i).getType()))
				{
					Field field = fields.get(i);
					field.setAccessible(true);
					
					links.add(PropertyTools.getSpecializationClass(field));
					names.add(fields.get(i).getName());
					arrays.add(true);					
				} 					
				else
				{
					WeakLink link = fields.get(i).getAnnotation(WeakLink.class);

					if(link != null)
					{
						fields.get(i).setAccessible(true);

						links.add(link.value());
						names.add(link.name().isEmpty() ? fields.get(i).getName() : link.name());
						arrays.add(fields.get(i).getType().isArray());
					}
				}
			}
		}
		
		ports = new ZoneGraphPort[links.size()+1];
		
		float space = size * 2.5f;
		float height = (ports.length == 1.0f ? size * 4.0f : size * 4.0f * (ports.length-1)) + space;
		float shift = size * 3.0f + space;
		float step = size * 4.0f;
		float width = nodeMetrics.stringWidth(definition.getClass().getSimpleName()) + step + 2.0f;

		for(int i = 0 ; i < ports.length-1 ; i++)
		{
			ports[i] = new ZoneGraphInputPort(links.get(i), names.get(i), arrays.get(i), (shift + step * i) / height);
			width = Math.max(width, portMetrics.stringWidth(names.get(i) + ports[i].getGeometry().getWidth()));
		}
				
		ports[ports.length - 1] = new ZoneGraphOutputPort(definition.getClass(), width, size * 3.0f);
		
		validate();
		
		setStyle(getNodeStyle());
		
		setGeometry(new mxGeometry(0.0f, 0.0f, width, height));
		setVertex(true);

		caption = new mxCell();

		mxGeometry geo = new mxGeometry(0.0f, 0.0f, width, size);
		geo.setOffset(new mxPoint(0.0f, height + size));
		geo.setRelative(true);
		
		caption.setGeometry(geo);
		caption.setValue("[" + definition.getID() + "]");
		caption.setStyle(getTextStyle());
		caption.setVertex(true);
		caption.setConnectable(false);

		EditorApplication.getDebugConsole().addLine("Created node: " + definition.getID());		
	}
		
	//--------------------------------------------------------------------------
	
	public int getNumberOfInputs()
	{
		return ports.length - 1;
	}
	
	//--------------------------------------------------------------------------
	
	public ZoneGraphPort getInputPort(int index)
	{
		if(index >= ports.length - 1)
			throw new ArrayIndexOutOfBoundsException(index);
		
		return ports[index];
	}
	
	//--------------------------------------------------------------------------
	
	public ZoneGraphPort getOutputPort()
	{
		return ports[ports.length - 1];
	}
	
	//--------------------------------------------------------------------------
	
	private String getSelectedStyle()
	{					
		return	"fontFamily=" + nodeMetrics.getFont().getFamily() + ";"
			+	"fontSize=" +  nodeMetrics.getFont().getSize() + ";"
			+	"fontColor=#000000;"
			+	"verticalAlign=top;"
			+	"strokeWidth=4;"
			+	"strokeColor=#1f8eff;"
			+	"fillColor=#ffffff;"
			+	"gradientColor=" + getColorString(definition.getClass()) + ";";			
	}
	
	//--------------------------------------------------------------------------
	
	private String getTextStyle()
	{		
		return	"fontFamily=" + nodeMetrics.getFont().getFamily() + ";"
			+	"fontSize=" +  (nodeMetrics.getFont().getSize()+1) + ";"
			+	"fontColor=#C6C6C6;"
			+	"verticalAlign=center;"
			+	"labelPosition=center;"
			+	"strokeColor=none;"
			+	"fillColor=none";
	}

	//--------------------------------------------------------------------------
	
	public String getNodeStyle()
	{
		if(selected)
			return getSelectedStyle();
		else if(validated)
			return getValidStyle();
		else
			return getInvalidStyle();
	}
	
	//--------------------------------------------------------------------------
	
	private String getInvalidStyle()
	{
			return	"fontFamily=" + nodeMetrics.getFont().getFamily() + ";"
			+	"fontSize=" +  nodeMetrics.getFont().getSize() + ";"
			+	"fontColor=#000000;"
			+	"verticalAlign=top;"
			+	"strokeWidth=4;"
			+	"strokeColor=#ff1f1f;"
			+	"fillColor=#ffffff;"
			+	"gradientColor=" + getColorString(definition.getClass()) + ";";	
	}
	
	//--------------------------------------------------------------------------
	
	private String getValidStyle()
	{
		return	"fontFamily=" + nodeMetrics.getFont().getFamily() + ";"
			+	"fontSize=" +  nodeMetrics.getFont().getSize() + ";"
			+	"fontColor=#000000;"
			+	"verticalAlign=top;"
			+	"strokeWidth=1;"
			+	"strokeColor=#707070;"
			+	"fillColor=#ffffff;"
			+	"gradientColor=" + getColorString(definition.getClass()) + ";";
	}
	
	//--------------------------------------------------------------------------
	
	public void attach(ZoneGraph graph)
	{
		graph.addNode(this);		
		graph.addCells(ports, this);
		graph.addCell(caption, this);
						
		this.graph = graph;
		
		ZoneMetadata metadata = ZoneDocument.getMetadata(definition);
		graph.foldCells(metadata.isFolded(), false, new Object[]{this});
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getToolTip()
	{
		return validated ? (definition.getID() + " [" + definition.getClass().getSimpleName() + "]") : (validationInfo);
	}

	//--------------------------------------------------------------------------

	@Override
	public Class<? extends ZariaObjectDefinition> getDefinitionClass()
	{
		return definition.getClass();
	}
	
	//--------------------------------------------------------------------------
		
	public ZariaObjectDefinition getDefinition()
	{
		return definition;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void propertyChange(PropertyChangeEvent pce)
	{	
		ZoneProperty prop = (ZoneProperty)pce.getSource();		
        prop.writeToObject(definition);
	
		if(prop.isLink())
		{
			try
			{
				updateLinkLists();
			}
			catch(IllegalArgumentException | IllegalAccessException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
			
			graph.setLinks(this);
		}
		else if(prop.getName().equals("id"))
		{
			caption.setValue("[" + definition.getID() + "]");
		}
		
		validate();
		
		if(graph != null)
		{
			graph.getModel().setStyle(this, getNodeStyle());
			graph.getDocument().setModified();
			EditorApplication.getDebugConsole().addLine("Modified " + prop.getDisplayName() + " from [" + pce.getOldValue() + "] to [" + pce.getNewValue() + "].");
		}				
	}
	
	//--------------------------------------------------------------------------
	
	private void validate()			
	{
		DefinitionValidation validation = definition.validate();		
		boolean val_has_errors = validation.hasErrors();
		
		// Inverted conditions.
		if(validated == val_has_errors || val_has_errors)
		{
			validated = !val_has_errors;
		
			if(val_has_errors)
				validationInfo = "<html>" + validation.getInfo().replaceAll("\n", "<br>") + "</html>";		
			else
				validationInfo = "";
		}
	}
	
	//--------------------------------------------------------------------------
		
	public void updateLinkLists() throws IllegalArgumentException, IllegalAccessException
	{
		// This assumes that the ports array has the same order as the
		// returned field array. 
		
		ArrayList<Field> fields = getAllFields(definition.getClass());

		ArrayList<ArrayList<ZoneGraphPort.LinkInfo>> link_ids = new ArrayList<>();
		
		for(int i = 0 ; i < fields.size() ; i++)
		{
			if(!Modifier.isTransient(fields.get(i).getModifiers()))
			{
				if(Link.class.isAssignableFrom(fields.get(i).getType()))
				{
					fields.get(i).setAccessible(true);

					ArrayList<ZoneGraphPort.LinkInfo> ids = new ArrayList<>();

					Object obj = fields.get(i).get(definition);

					if(obj != null)
					{
						Link link = (Link) obj;		
						
						if(link.getUUID() != null)
							ids.add(new ZoneGraphPort.LinkInfo(link.getUUID(), graph.findNode(link.getUUID()) == null));
					}

					link_ids.add(ids);
				}
				else if(LinkArray.class.isAssignableFrom(fields.get(i).getType()))
				{
					fields.get(i).setAccessible(true);

					ArrayList<ZoneGraphPort.LinkInfo> ids = new ArrayList<>();

					Object obj = fields.get(i).get(definition);

					if(obj != null)
					{
						LinkArray link_array = (LinkArray) obj;		
						
						for(int j = 0 ; j < link_array.size() ; j++)
						{
							// Check for links not yet filled out.
							if(link_array.getUUID(j) != null)
								ids.add(new ZoneGraphPort.LinkInfo(link_array.getUUID(j), graph.findNode(link_array.getUUID(j)) == null));
						}
					}

					link_ids.add(ids);
				}
				else
				{				
					WeakLink link = fields.get(i).getAnnotation(WeakLink.class);

					if(link != null)
					{
						fields.get(i).setAccessible(true);

						ArrayList<ZoneGraphPort.LinkInfo> ids = new ArrayList<>();

						Object obj = fields.get(i).get(definition);

						if(obj != null)
						{
							if(fields.get(i).getType().isArray())
							{
								UUID[] uuids = (UUID[]) obj;

								for(int j = 0 ; j < uuids.length ; j++)
								{
									// Check for links not yet filled out.
									if(uuids[j] != null)
										ids.add(new ZoneGraphPort.LinkInfo(uuids[j], graph.findNode(uuids[j]) == null));
								}
							}
							else
							{
								UUID field_uuid = (UUID) obj;							
								ids.add(new ZoneGraphPort.LinkInfo(field_uuid, graph.findNode(field_uuid) == null));
							}
						}

						link_ids.add(ids);
					}
				}
			}
		}
				
		for(int i = 0 ; i < ports.length-1 ; i++)
		{
			ports[i].setLinks(link_ids.get(i));
			ports[i].update(graph);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void updateOutputLinks(UUID oldUUID, UUID newUUID)
	{
		ZoneGraphPort port = getOutputPort();
				
		int nofedges = port.getEdgeCount();
		
		for(int i = 0 ; i < nofedges ; i++)
		{
			mxCell portedge = (mxCell) port.getEdgeAt(i);

			ZoneGraphInputPort srcport;
			
			if(portedge.getSource() instanceof ZoneGraphInputPort)
				srcport = (ZoneGraphInputPort) portedge.getSource();				
			else
				srcport = (ZoneGraphInputPort) portedge.getTarget();
			
			ZoneGraphNode src = (ZoneGraphNode) srcport.getParent();
			
			srcport.removeLink(oldUUID);
			srcport.getLinks().add(new ZoneGraphPort.LinkInfo(newUUID, false));
			srcport.update(graph);
			
			src.updateInputLinks(srcport);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void updateInputLinks(ZoneGraphPort port)
	{				
		try
		{					
			Field field = ReflectionTools.getField(definition.getClass(), port.getName());		
			
			assert field != null;						
			
			UUID [] uuids = new UUID[port.getLinks().size()];
			
			for(int i = 0 ; i < port.getLinks().size() ; i++)
				uuids[i] = port.getLinks().get(i).getUUID();
			
			// Inputting an array.
			if(uuids.length > 1)
			{								
				if(LinkArray.class.isAssignableFrom(field.getType()))						
					field.set(definition, PropertyTools.createNewLinkArray(uuids));
				else
				{
					assert field.getType().isArray();
					
					field.set(definition, uuids);
				}
			}
			// Inputting a single element.
			else if(uuids.length > 0)
			{
				if(field.getType().isArray())
					field.set(definition, uuids);
				else if(LinkArray.class.isAssignableFrom(field.getType()))						
					field.set(definition, PropertyTools.createNewLinkArray(uuids));
				else if(Link.class.isAssignableFrom(field.getType()))
					field.set(definition, PropertyTools.createNewLink(uuids[0]));
				else
					field.set(definition, uuids[0]);
			}
			// Inputting empty.
			else
			{
				if(field.getType().isArray())
					field.set(definition, new String[0]);
				else if(Link.class.isAssignableFrom(field.getType()))
					field.set(definition, PropertyTools.createNewLink(null));
				else if(LinkArray.class.isAssignableFrom(field.getType()))						
					field.set(definition, PropertyTools.createNewLinkArray(new UUID[0]));
				else
					field.set(definition, "");
			}
			
			updateLinkLists();
		}
		catch(SecurityException | IllegalAccessException | IllegalArgumentException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void connectionAdded(String field, UUID uuid)
	{
//		graph.setEventsEnabled(false);
		
		for(ZoneGraphPort port : ports)
		{
			if(port.getName().equals(field))
			{
				boolean external = graph.findNode(uuid) == null;
				
				if(!port.isArray())
				{
					if((external && port.getEdgeCount() > 0) || port.getEdgeCount() > 1)
						graph.removeCells(new Object[]{port.getEdgeAt(0)});				
					
					port.getLinks().clear();
				}
				
				port.getLinks().add(new ZoneGraphPort.LinkInfo(uuid, external));
								
				port.update(graph);
				
				updateInputLinks(port);
				
				break;
			}
		}		
		
		validate();
		
//		graph.setEventsEnabled(true);
	}
	
	//--------------------------------------------------------------------------
	
	public void connectionRemoved(String field, UUID uuid)
	{
//		graph.setEventsEnabled(false);
		
		for(ZoneGraphPort port : ports)
		{
			if(port.getName().equals(field))
			{
				port.removeLink(uuid);
				port.update(graph);
				
				updateInputLinks(port);
				
				break;
			}
		}	
		
		validate();
		
//		graph.setEventsEnabled(true);
	}
	
	//--------------------------------------------------------------------------
	
	private ArrayList<Field> getAllFields(Class c)
	{
		ArrayList<Field> ret = new ArrayList<>();
		
		while(c != null && ZariaObjectDefinition.class.isAssignableFrom(c))
		{
			Field [] fields = c.getDeclaredFields();
			ret.addAll(Arrays.asList(fields));
			
			c = c.getSuperclass();
		}
		
		return ret;
	}
	
	//--------------------------------------------------------------------------

	public void saveMetadata()
	{	
		ZoneMetadata metadata = ZoneDocument.getMetadata(definition);
		metadata.setLocX((int) geometry.getX());
		metadata.setLocY((int) geometry.getY());
		metadata.setFolded(isCollapsed());
		
		ZoneDocument.saveMetadata(definition, metadata);
	}
	
	//--------------------------------------------------------------------------
	
	public void applyMetadata()
	{
		ZoneMetadata metadata = ZoneDocument.getMetadata(definition);
		
		geometry.setX(metadata.getLocX());
		geometry.setY(metadata.getLocY());		
	}
	
	//--------------------------------------------------------------------------
	
	public void setPosition(int x, int y)
	{
		geometry.setX(x);
		geometry.setY(y);
	}

	//--------------------------------------------------------------------------
	
	public void setSelected(boolean selected)
	{		
		this.selected = selected;
	}
	
	//--------------------------------------------------------------------------
}
