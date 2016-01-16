/****************************************/
/* IconMaker.java						*/
/* Created on: 09-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.palette;

import eu.cherrytree.zaria.editor.components.ButtonTabComponent;
import eu.cherrytree.zaria.serialization.ColorName;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.ImageIcon;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class IconMaker
{
	//--------------------------------------------------------------------------
	
	private static final BufferedImage image;
	
	private static HashMap<ColorName,ImageIcon> icons = new HashMap<>();
	
	//--------------------------------------------------------------------------
	
	static
	{
		ImageIcon icon = new ImageIcon(ButtonTabComponent.class.getResource("/eu/cherrytree/zaria/editor/res/icons/pallette/item.png"));
		
		image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = image.createGraphics();
		icon.paintIcon(null, g, 0,0);
		g.dispose();
	}

	//--------------------------------------------------------------------------
		
	public static ImageIcon getIcon(ColorName color)
	{
		if(!icons.containsKey(color))
			icons.put(color, generateIcon(color));
		
		return icons.get(color);
	}

	//--------------------------------------------------------------------------

	private static ImageIcon generateIcon(ColorName color)
	{
		BufferedImage newimage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		for(int i = 0 ; i < image.getWidth() ; i++)
		{
			for(int j = 0 ; j < image.getHeight() ; j++)
			{
				Color c = new Color(image.getRGB(i, j), true);
				
				int r = c.getRed() * color.getRed() / 255;
				int g = c.getGreen() * color.getGreen() / 255;
				int b = c.getBlue() * color.getBlue() / 255;

				newimage.setRGB(i, j, new Color(r,g,b,c.getAlpha()).getRGB());				
			}
		}
		
		return new ImageIcon(newimage);
	}
	
	//--------------------------------------------------------------------------
}
