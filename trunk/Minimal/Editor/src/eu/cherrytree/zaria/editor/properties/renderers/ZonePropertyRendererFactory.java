/****************************************/
/* ZonePropertyRendererFactory.java 	*/
/* Created on: 25-Jun-2014				*/
/* Copyright Cherry Tree Studio 2014		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.renderers;

import eu.cherrytree.zaria.editor.properties.Property;
import eu.cherrytree.zaria.editor.properties.ZoneProperty;
import eu.cherrytree.zaria.serialization.LinkArray;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZonePropertyRendererFactory extends PropertyRendererFactory
{
	//--------------------------------------------------------------------------
	
	private LinkRenderer linkRenderer = new LinkRenderer();
	
	//--------------------------------------------------------------------------

	@Override
	public synchronized TableCellRenderer getRenderer(Property property)
	{
		ZoneProperty zone_prop = (ZoneProperty) property;
		
		if(zone_prop.getType().isArray() || zone_prop.getType() == LinkArray.class)
		{						
			if(zone_prop.isLink())
				return getArrayPropertyRenderer("[Link] " + zone_prop.getLinkClassName());
			else if(zone_prop.getResourceInfo() != null)
				return getArrayPropertyRenderer("[Resource] " + zone_prop.getResourceInfo().getName());
			else 
				return getArrayPropertyRenderer(property.getType().getComponentType().getSimpleName());
		}
		else if(zone_prop.isLink())
		{
			return linkRenderer;
		}
		else
		{
			return getRenderer(property.getType());
		}
	}
	
	//--------------------------------------------------------------------------
}
