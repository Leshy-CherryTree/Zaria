/****************************************/
/* ColorRGBAIcon.java					*/
/* Created on: 15-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ColorARGBIcon implements Icon
{
	//--------------------------------------------------------------------------

	private final Color color;
	private final int width;
	private final int height;

	//--------------------------------------------------------------------------

	public ColorARGBIcon(int colorARGB, int width, int height)
	{
		this.color = new Color(colorARGB);
		this.width = width;
		this.height = height;
	}

	//--------------------------------------------------------------------------
	
	@Override
	public int getIconHeight()
	{
		return height;
	}

	//--------------------------------------------------------------------------

	@Override
	public int getIconWidth()
	{
		return width;
	}

	//--------------------------------------------------------------------------

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		Graphics2D g2d = (Graphics2D) g;
		Paint oldPaint = g2d.getPaint();

		if(color != null)
		{
			g2d.setColor(color);
			g.fillRect(x, y, getIconWidth(), getIconHeight());
		}

		g.setColor(UIManager.getColor("controlDkShadow"));
		g.drawRect(x, y, getIconWidth(), getIconHeight());

		g2d.setPaint(oldPaint);
	}
	
	//--------------------------------------------------------------------------
}
