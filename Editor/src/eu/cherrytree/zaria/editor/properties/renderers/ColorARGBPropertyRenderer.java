/****************************************/
/* ColorRGBAPropertyRenderer.java       */
/* Created on: 15-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.renderers;

import javax.swing.Icon;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ColorARGBPropertyRenderer extends DefaultCellRenderer
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private int getColor(Object value)
	{
		int color = -1;
		
		if (value instanceof Integer)
			color = (Integer) value;
		
		return color;
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected String convertToString(Object value)
	{		
		int color = getColor(value);

		if (color >= 0)			
		{
			return "#" + Integer.toHexString(color);
		}
		else 
			return null;
	}

	//--------------------------------------------------------------------------

	@Override
	protected Icon convertToIcon(Object value)
	{
		int color = getColor(value);
		
		if (color >= 0)
			return new ColorARGBIcon(color, 20, 10);
		else
			return null;
	}
	
	//--------------------------------------------------------------------------
}
