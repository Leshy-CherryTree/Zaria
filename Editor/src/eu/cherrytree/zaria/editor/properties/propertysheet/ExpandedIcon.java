/****************************************/
/* ExpandedIcon.java					*/
/* Created on: 13-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.propertysheet;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 * 
 * Based upon the unmaintained code from L2FProd.com.
 */
public class ExpandedIcon implements Icon
{
	//--------------------------------------------------------------------------

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		Color backgroundColor = c.getBackground();

		if(backgroundColor != null)
			g.setColor(backgroundColor);
		else
			g.setColor(Color.white);
		
		g.fillRect(x, y, 8, 8);
		g.setColor(Color.gray);
		g.drawRect(x, y, 8, 8);
		g.setColor(Color.black);
		g.drawLine(x + 2, y + 4, x + (6), y + 4);
	}

	//--------------------------------------------------------------------------

	@Override
	public int getIconWidth()
	{
		return 9;
	}

	//--------------------------------------------------------------------------

	@Override
	public int getIconHeight()
	{
		return 9;
	}
	
	//--------------------------------------------------------------------------
}