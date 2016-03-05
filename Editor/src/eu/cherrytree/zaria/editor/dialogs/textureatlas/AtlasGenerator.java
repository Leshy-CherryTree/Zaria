/****************************************/
/* AtlasGenerator.java					*/
/* Created on: 01-Jan-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs.textureatlas;

import eu.cherrytree.zaria.editor.debug.DebugConsole;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import javax.imageio.ImageIO;

/**
 * Based on: https://github.com/lukaszdk/texture-atlas-generator which is released as Public Domain code.
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class AtlasGenerator
{
	//--------------------------------------------------------------------------

	private class ImageName
	{
		private BufferedImage image;
		private String name;

		public ImageName(BufferedImage image, String name)
		{
			this.image = image;
			this.name = name;
		}

		public BufferedImage getImage()
		{
			return image;
		}

		public String getName()
		{
			return name;
		}				

		private void optimizeX()
		{
			assert image != null;
			
			int height = image.getHeight();
			int width = image.getWidth();
			
			int lines_to_clear = 0;
			
			for (int x = 0 ; x < width/2 ; x++)
			{
				boolean can_be_cleared = true;
				
				for (int y = 0 ; y < height ; y++)
				{
					int pixel_l = image.getRGB(x, y);
					
					if (pixel_l > 0)
					{
						can_be_cleared = false;
						break;
					}
					
					int pixel_r = image.getRGB(width - x - 1, y);
					
					if (pixel_r > 0)
					{
						can_be_cleared = false;
						break;
					}
				}
				
				if (!can_be_cleared)
					break;
				
				lines_to_clear++;
			}
			
			if (lines_to_clear > 0)
			{
				BufferedImage img = new BufferedImage((width/2 - lines_to_clear) * 2, height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = (Graphics2D) img.getGraphics();
				
				g.drawImage(image, -lines_to_clear, 0, null);		
				
				image = img;
			}
		}

		private void optimizeY()
		{
			assert image != null;
			
						int height = image.getHeight();
			int width = image.getWidth();
			
			int lines_to_clear = 0;
			
			for (int y = 0 ; y < height/2 ; y++)
			{
				boolean can_be_cleared = true;
				
				for (int x = 0 ; x < width ; x++)
				{
					int pixel_l = image.getRGB(x, y);
					
					if (pixel_l > 0)
					{
						can_be_cleared = false;
						break;
					}
					
					int pixel_r = image.getRGB(x, height - y - 1);
					
					if (pixel_r > 0)
					{
						can_be_cleared = false;
						break;
					}
				}
				
				if (!can_be_cleared)
					break;
				
				lines_to_clear++;
			}
			
			if (lines_to_clear > 0)
			{
				BufferedImage img = new BufferedImage(width, (height/2 - lines_to_clear) * 2, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = (Graphics2D) img.getGraphics();
				
				g.drawImage(image, 0, -lines_to_clear, null);		
				
				image = img;
			}
		}
	}
	
	//--------------------------------------------------------------------------

	private class ImageNameComparator implements Comparator<ImageName>
	{

		@Override
		public int compare(ImageName image1, ImageName image2)
		{
			int area1 = image1.getImage().getWidth() * image1.getImage().getHeight();
			int area2 = image2.getImage().getWidth() * image2.getImage().getHeight();

			if (area1 != area2)
			{
				return area2 - area1;
			}
			else
			{
				return image1.getName().compareTo(image2.getName());
			}
		}
	}
	
	//--------------------------------------------------------------------------

	private class SizeSearchException extends Exception
	{
		public SizeSearchException(String reason)
		{
			super("There was an error in determining the size of the texture atlas. " + reason);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private static final int padding = 2;
	
	
	//--------------------------------------------------------------------------	

	private int[] getAtlasSize(Set<ImageName> imageNameSet) throws IOException, SizeSearchException
	{
		int min_x = 0;
		int min_y = 0;
		int min_area = 0;

		int widecount = 0;
		int longcount = 0;

		// Getting minimal values.
		for (ImageName img_nam : imageNameSet)
		{
			BufferedImage image = img_nam.image;
			
			if (min_x < image.getWidth())
				min_x = image.getWidth();

			if (min_y < image.getHeight())
				min_y = image.getHeight();

			min_area += image.getWidth() * image.getHeight();

			if (image.getWidth() > image.getHeight())
				widecount++;
			else if (image.getHeight() > image.getWidth())
				longcount++;
		}

		if (min_y <= 0 || min_x <= 0 || min_area <= 0)
		{
			throw new SizeSearchException("No textures with valid sizes.");
		}

		// Making sure that minimal values are a multiple of 4
		int size_x = makeMultipleOf4(min_x);
		int size_y = makeMultipleOf4(min_y);

		while (size_x * size_y < min_area)
		{
			if (size_x < size_y)
				size_x += 4;
			else if (size_x > size_y)
				size_y += 4;
			else if (widecount > longcount)
				size_x += 4;
			else if (widecount < longcount)
				size_y += 4;
			// If the sizes are equal by default we widden the texture.
			else
				size_x += 4;
		}

		boolean checked = false;

		// Verifying the sizes to be sure all images will fit.
		while (!checked)
		{
			checked = true;

			AtlasNode root = new AtlasNode(0, 0, size_x, size_y);

			for (ImageName imageName : imageNameSet)
			{
				AtlasNode node = root.insert(imageName.image, padding);

				if (node == null)
				{
					if (size_x < size_y)
						size_x += 4;
					else if (size_x > size_y)
						size_y += 4;
					else if (widecount > longcount)
						size_x += 4;
					else if (widecount < longcount)
						size_y += 4;
					// If the sizes are equal by default we widden the texture.
					else
						size_x += 4;

					checked = false;

					break;
				}
			}
		}

		return new int[] { size_x, size_y };
	}
	
	//--------------------------------------------------------------------------	
	
	private void loadImages(List<File> imageFiles, Set<ImageName> imageNameSet) throws IOException
	{
		// Getting minimal values.
		for (File f : imageFiles)
		{
			BufferedImage image = ImageIO.read(f);

			imageNameSet.add(new ImageName(image, f.getPath().substring(0, f.getPath().lastIndexOf(".")).replace("\\", "/")));		
		}
	}
	
	//--------------------------------------------------------------------------	

	public String run(String name, String imageFormat, ArrayList<File> imageFiles, boolean xOptimize, boolean yOptimize)
	{
		if (imageFiles.isEmpty())
			return "No files selected!";
		
		Set<ImageName> imageNameSet = new TreeSet<ImageName>(new ImageNameComparator());					
		
		int width, height;
		
		try
		{
			loadImages(imageFiles, imageNameSet);
			
			if (xOptimize || yOptimize)
			{
				for (ImageName img_nam : imageNameSet)
				{
					if (xOptimize)
						img_nam.optimizeX();

					if (yOptimize)
						img_nam.optimizeY();
				}
			}
			
			int[] size = getAtlasSize(imageNameSet);

			width = size[0];
			height = size[1];
		}
		catch (Exception ex)
		{				
			DebugConsole.logger.log(Level.SEVERE, null, ex);
			return "Couldn't determine the size of the atlas.";
		}

		Atlas atlas = new Atlas(width, height);

		for (ImageName imageName : imageNameSet)
		{
			if (!atlas.addImage(imageName.image, imageName.name, padding))
				return "Not all images fit in a single atlas of the given dimensions!";
		}

		atlas.write(name, imageFormat);
		
		return "";
	}
	
	//--------------------------------------------------------------------------	

	private int makeMultipleOf4(int inValue)
	{
		int remainder = inValue % 4;

		if (remainder != 0)
		{
			inValue += (4 - remainder);
		}

		return inValue;
	}
	
	//--------------------------------------------------------------------------	
}
