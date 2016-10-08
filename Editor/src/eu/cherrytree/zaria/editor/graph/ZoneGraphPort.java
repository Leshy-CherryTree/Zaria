/****************************************/
/* ZoneGraphPort.java					*/
/* Created on: 16-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.graph;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class ZoneGraphPort extends ZoneGraphElement
{	
	//--------------------------------------------------------------------------
	
	public static class LinkInfo implements Serializable
	{
		private boolean external;
		private UUID uuid;

		public LinkInfo(UUID uuid, boolean external)
		{
			assert uuid != null;
			
			this.external = external;
			this.uuid = uuid;
		}

		public UUID getUUID()
		{
			return uuid;
		}

		public boolean isExternal()
		{
			return external;
		}				
	}
	
	//--------------------------------------------------------------------------
	
	private Class<? extends ZariaObjectDefinition> defintionClass;
	private String name;
	protected String displayName;
	
	private ArrayList<LinkInfo> links;
	
	//--------------------------------------------------------------------------
	
	public ZoneGraphPort(Class<? extends ZariaObjectDefinition> defintionClass, String name, String displayName)
	{
		this.defintionClass = defintionClass;
		this.name = name;
		this.displayName = getName(displayName);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public String getToolTip()
	{
		return defintionClass.getSimpleName();
	}

	//--------------------------------------------------------------------------

	@Override
	public Class<? extends ZariaObjectDefinition> getDefinitionClass()
	{
		return defintionClass;
	}
	
	//--------------------------------------------------------------------------	 
	
	public final String getName()
	{
		return name;
	}
	
	//--------------------------------------------------------------------------

	public String getDisplayName()
	{
		return displayName;
	}
			
	//--------------------------------------------------------------------------
	
	public UUID getParentUUID()
	{
		ZoneGraphNode node = (ZoneGraphNode) getParent();
		
		return node.getDefinition().getUUID();
	}

	//--------------------------------------------------------------------------

	public ArrayList<LinkInfo> getLinks()
	{
		return links;
	}

	//--------------------------------------------------------------------------

	public void setLinks(ArrayList<LinkInfo> links)
	{
		this.links = links;
	}
	
	//--------------------------------------------------------------------------
	
	public void removeLink(UUID uuid)
	{
		LinkInfo toremove = null;
		
		for(LinkInfo link : links)
		{
			if(link.getUUID() != null && link.getUUID().equals(uuid))
			{
				toremove = link;
				break;
			}
		}
		
		assert toremove != null;
		
		links.remove(toremove);
	}
	
	//--------------------------------------------------------------------------	
	
	public boolean linksContain(UUID uuid)
	{
		for(LinkInfo link : links)
		{
			if(link.getUUID() != null && link.getUUID().equals(uuid))
				return true;
		}

		return false;
	}
	
	//--------------------------------------------------------------------------
	
	public void update(ZoneGraph graph)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
	
	public abstract boolean isInput();
	public abstract boolean isArray();
	
	//--------------------------------------------------------------------------	
}
