/****************************************/
/* ColorRGBAPropertyEditor.java         */
/* Created on: 15-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.properties.renderers.ColorARGBPropertyRenderer;
import java.awt.Color;

import java.awt.event.ActionEvent;
import javax.swing.JColorChooser;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ColorRGBAPropertyEditor extends DialogPropertyEditor
{
	//--------------------------------------------------------------------------
	
	private int color = 0xFFFFFFFF;

	//--------------------------------------------------------------------------

	public ColorRGBAPropertyEditor()
	{
		super(new ColorARGBPropertyRenderer());
	}
		
	//--------------------------------------------------------------------------

	@Override
	public Object getValue()
	{
		return color;
	}

	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{
		((ColorARGBPropertyRenderer) getComponent()).setValue(color);
	}

	//--------------------------------------------------------------------------
	
	private float clamp(float inColor)
	{
		return Math.min(Math.max(inColor, 0.0f), 1.0f);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent ae)
	{
		Color awt_color = new Color(color);
		
		awt_color = JColorChooser.showDialog(getCustomEditor(), "Choose color", awt_color);
		
		if(awt_color != null)
		{
			int old = color;
			color = awt_color.getRGB();

			((ColorARGBPropertyRenderer) getComponent()).setValue(color);

			firePropertyChange(old, color);
		}
	}
	
	//--------------------------------------------------------------------------
}
