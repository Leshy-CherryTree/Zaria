/****************************************/
/* ColorSlider.java						*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public abstract class ColorSlider extends JPanel
{
	//--------------------------------------------------------------------------

	private JSlider slider;
	private ColorPicker colorPicker;
	
	//--------------------------------------------------------------------------

	public ColorSlider(Color[] paletteColors)
	{
		setLayout(new GridBagLayout());
		{
			slider = new JSlider(0, 1000, 0);
			slider.setPaintTrack(false);
			add(slider, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
			new Insets(0, 6, 0, 6), 0, 0));
		}
		{
			colorPicker = new ColorPicker(paletteColors, slider);
			add(colorPicker, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
			GridBagConstraints.HORIZONTAL, new Insets(0, 6, 0, 6), 0, 0));
		}

		slider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent event)
			{
				colorPicked();
			}
		});
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Dimension getPreferredSize()
	{
		Dimension size = super.getPreferredSize();
		size.width = 10;
		return size;
	}
	
	//--------------------------------------------------------------------------

	public void setPercentage(float percent)
	{
		slider.setValue((int) (1000 * percent));
	}
	
	//--------------------------------------------------------------------------

	public float getPercentage()
	{
		return slider.getValue() / 1000f;
	}
	
	//--------------------------------------------------------------------------
	
	public void setColors(Color[] colors)
	{
		colorPicker.updateColors(colors);
		
		repaint();
	}

	//--------------------------------------------------------------------------
	
	protected abstract void colorPicked();
	
	//--------------------------------------------------------------------------
}
