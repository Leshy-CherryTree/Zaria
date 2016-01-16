/****************************************/
/* PropertySheetElement.java			*/
/* Created on: 13-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.propertysheet;

import eu.cherrytree.zaria.editor.properties.Property;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 * 
 * Based upon the unmaintained code from L2FProd.com.
 */
public class PropertySheetElement
{
	//--------------------------------------------------------------------------
	
	private String name;
	private Property property;
	private PropertySheetElement parent;
	private boolean hasToggle = true;
	private boolean visible = true;

	private PropertySheetTableModel tableModel;
	
	//--------------------------------------------------------------------------

	public PropertySheetElement(String name, PropertySheetElement parent, PropertySheetTableModel tableModel)
	{
		this.name = name;
		this.parent = parent;
		this.tableModel = tableModel;
		
		// this is not a property but a category, always has toggle
		this.hasToggle = true;		
	}

	//--------------------------------------------------------------------------

	public PropertySheetElement(Property property, PropertySheetElement parent, PropertySheetTableModel tableModel)
	{
		this.name = property.getDisplayName();
		this.property = property;
		this.parent = parent;
		this.visible = (property == null);
		this.tableModel = tableModel;

		// properties toggle if there are sub-properties
		Property[] subProperties = property.getSubProperties();
		hasToggle = subProperties != null && subProperties.length > 0;
	}

	//--------------------------------------------------------------------------

	public String getName()
	{
		return name;
	}

	//--------------------------------------------------------------------------
	
	public boolean isProperty()
	{
		return property != null;
	}

	//--------------------------------------------------------------------------

	public Property getProperty()
	{
		return property;
	}

	//--------------------------------------------------------------------------

	public PropertySheetElement getParent()
	{
		return parent;
	}

	//--------------------------------------------------------------------------

	public int getDepth()
	{
		int depth = 0;
		
		if(parent != null)
		{
			depth = parent.getDepth();
			
			if(parent.isProperty())
				++depth;
		}
		
		return depth;
	}

	//--------------------------------------------------------------------------

	public boolean hasToggle()
	{
		return hasToggle;
	}

	//--------------------------------------------------------------------------

	public void toggle()
	{
		if(hasToggle())
		{
			visible = !visible;
			tableModel.visibilityChanged(false);
			tableModel.fireTableDataChanged();
		}
	}

	//--------------------------------------------------------------------------

	public void setVisible(final boolean visible)
	{
		this.visible = visible;
	}

	//--------------------------------------------------------------------------

	public boolean isVisible()
	{
		return (parent == null || parent.isVisible()) && (!hasToggle || visible);
	}

	//--------------------------------------------------------------------------

	public String getKey()
	{
		StringBuilder key = new StringBuilder(name);
		PropertySheetElement itemParent = parent;
		
		while(itemParent != null)
		{
			key.append(":");
			key.append(itemParent.getName());
			itemParent = itemParent.getParent();
		}
		
		return key.toString();
	}
	
	//--------------------------------------------------------------------------
}
