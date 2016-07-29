/****************************************/
/* ColorPicker.java						*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public class ColorPicker extends JPanel
{
	//--------------------------------------------------------------------------
	
	private Color[] paletteColors;
	
	//--------------------------------------------------------------------------

	public ColorPicker(Color[] paletteColors, final JSlider slider)
	{
		this.paletteColors = paletteColors;
		
		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
				slider.setValue((int) (event.getX() / (float) getWidth() * 1000));
			}
		});
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		int width = getWidth() - 1;
		int height = getHeight() - 1;
		for (int i = 0, n = paletteColors.length - 1; i < n; i++)
		{
			Color color1 = paletteColors[i];
			Color color2 = paletteColors[i + 1];
			float point1 = i / (float) n * width;
			float point2 = (i + 1) / (float) n * width;
			g.setPaint(new GradientPaint(point1, 0, color1, point2, 0, color2, false));
			g.fillRect((int) point1, 0, (int) Math.ceil(point2 - point1), height);
		}
		g.setPaint(null);
		g.setColor(Color.black);
		g.drawRect(0, 0, width, height);
	}
	
	//--------------------------------------------------------------------------

	public void updateColors(Color[] colors)
	{
		paletteColors = colors;
	}
	
	//--------------------------------------------------------------------------
}
