/****************************************/
/* CollapsedIcon.java					*/
/* Created on: 13-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.propertysheet;

import java.awt.Component;
import java.awt.Graphics;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 * 
 * Based upon the unmaintained code from L2FProd.com.
 */
public class CollapsedIcon extends ExpandedIcon
{
	//--------------------------------------------------------------------------

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		super.paintIcon(c, g, x, y);
		g.drawLine(x + 4, y + 2, x + 4, y + 6);
	}

	//--------------------------------------------------------------------------
}