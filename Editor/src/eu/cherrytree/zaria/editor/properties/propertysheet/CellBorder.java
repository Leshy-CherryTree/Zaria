/****************************************/
/* CellBorder.java						*/
/* Created on: 13-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.propertysheet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
* Paints the border around the name cell. It handles the indent from the
* left side and the painting of the toggle knob.
* 
* @author Leszek Szczepański <leszek.gamedev@gmail.com>
* 
* Based upon the unmaintained code from L2FProd.com.
*/
public class CellBorder implements Border
{	
	//--------------------------------------------------------------------------
	
	private int indentWidth; // space before hotspot
	private boolean showToggle;
	private boolean toggleState;
	private Icon expandedIcon;
	private Icon collapsedIcon;
	private Insets insets = new Insets(1, 0, 1, 1);
	private boolean isProperty;

	//--------------------------------------------------------------------------

	public CellBorder()
	{
		expandedIcon = (Icon) UIManager.get(PropertySheetTable.TREE_EXPANDED_ICON_KEY);
		collapsedIcon = (Icon) UIManager.get(PropertySheetTable.TREE_COLLAPSED_ICON_KEY);
		
		if(expandedIcon == null)
			expandedIcon = new ExpandedIcon();

		if(collapsedIcon == null)
			collapsedIcon = new CollapsedIcon();
	}

	//--------------------------------------------------------------------------

	public void configure(PropertySheetTable table, PropertySheetElement item)
	{
		isProperty = item.isProperty();
		toggleState = item.isVisible();
		showToggle = item.hasToggle();

		indentWidth = PropertySheetTable.getIndent(table, item);
		insets.left = indentWidth + (showToggle ? PropertySheetTable.HOTSPOT_SIZE : 0) + 2;
	}

	//--------------------------------------------------------------------------

	@Override
	public Insets getBorderInsets(Component c)
	{
		return insets;
	}

	//--------------------------------------------------------------------------

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		if(!isProperty)
		{
			Color oldColor = g.getColor();
			g.setColor(c.getBackground());
			g.fillRect(x, y, x + PropertySheetTable.HOTSPOT_SIZE - 2, y + height);
			g.setColor(oldColor);
		}

		if(showToggle)
		{
			Icon drawIcon = (toggleState ? expandedIcon : collapsedIcon);
			drawIcon.paintIcon(c, g,
					x + indentWidth + (PropertySheetTable.HOTSPOT_SIZE - 2 - drawIcon.getIconWidth()) / 2,
					y + (height - drawIcon.getIconHeight()) / 2);
		}
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isBorderOpaque()
	{
		return true;
	}
	
	//--------------------------------------------------------------------------
}
