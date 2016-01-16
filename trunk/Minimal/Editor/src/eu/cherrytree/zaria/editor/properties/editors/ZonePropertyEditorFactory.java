/****************************************/
/* ZonePropertyEditorFactory.java 		*/
/* Created on: 25-Jun-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.editor.properties.Property;
import eu.cherrytree.zaria.editor.properties.ZoneProperty;
import eu.cherrytree.zaria.serialization.LinkArray;

import java.beans.PropertyEditor;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZonePropertyEditorFactory extends PropertyEditorFactory
{
	//--------------------------------------------------------------------------

	@Override
	public PropertyEditor createPropertyEditor(Property property)
	{
		ZoneProperty z_prop = (ZoneProperty) property;
		
		assert z_prop.getDocumentType() != ZoneDocument.DocumentType.ZONE_SCRIPT;
		
		if(z_prop.getType().isArray() || z_prop.getType() == LinkArray.class)
		{
			if(z_prop.isLink())
				return new ArrayPropertyEditor("[Link] " + z_prop.getLinkClassName(), z_prop.getDisplayName(), true);
			else if(z_prop.getResourceType() != null)
				return new ArrayPropertyEditor("[Resource] " + z_prop.getResourceType().getSimpleName(), z_prop.getDisplayName(), false);
			else 
				return new ArrayPropertyEditor(property.getType().getComponentType().getSimpleName(), z_prop.getDisplayName(), false);
		}
		else if(z_prop.isLink())
		{
			if(z_prop.getDocumentType() != null)
				return new LinkPropertyEditor(z_prop.getLinkClass(), z_prop.getDocumentType().getLocationType() == ZoneDocument.DocumentLocationType.ASSETS);
			else
				return null;
		}
		else if(z_prop.getResourceType() != null)
		{
			return new ResourcePropertyEditor(z_prop.getResourceType());
		}
		else if(z_prop.isScriptLink())
		{
			return new ScriptLinkPropertyEditor();
		}
		else if(z_prop.getMinMax() != null)
		{						
			if (property.getType() == Float.class || property.getType() == float.class)
			{
				return new FloatSliderPropertyEditor(z_prop.getMinMax());
			}
			else if (	property.getType() == Integer.class || property.getType() == int.class || 
						property.getType() == Short.class || property.getType() == short.class ||
						property.getType() == Byte.class || property.getType() == byte.class)
			{
				return new IntegerSliderPropertyEditor(z_prop.getMinMax());
			}
			else
			{
				// TODO Add an popup for the user, as somebody might make a mistake.
				
				assert false : "Invalid type " + property.getType() + " for use of sliders.";
				
				return null;
			}
		}
		else
			return loadPropertyEditor(property.getType());			
	}
	
	//--------------------------------------------------------------------------
}
