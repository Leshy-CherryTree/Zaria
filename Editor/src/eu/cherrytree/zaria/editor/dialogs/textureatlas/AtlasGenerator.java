/****************************************/
/* AtlasGenerator.java					*/
/* Created on: 01-Jan-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs.textureatlas;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private int[] getAtlasSize(List<File> imageFiles, Set<ImageName> imageNameSet) throws IOException, SizeSearchException
	{
		int min_x = 0;
		int min_y = 0;
		int min_area = 0;

		int widecount = 0;
		int longcount = 0;

		// Getting minimal values.
		for (File f : imageFiles)
		{
			BufferedImage image = ImageIO.read(f);

			if (min_x < image.getWidth())
				min_x = image.getWidth();

			if (min_y < image.getHeight())
				min_y = image.getHeight();

			min_area += image.getWidth() * image.getHeight();

			imageNameSet.add(new ImageName(image, f.getPath().substring(0, f.getPath().lastIndexOf(".")).replace("\\", "/")));

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

	public String run(String name, ArrayList<File> imageFiles)
	{
		if (imageFiles.isEmpty())
			return "No files selected!";
		
		Set<ImageName> imageNameSet = new TreeSet<ImageName>(new ImageNameComparator());
			
		int width, height;
		
		try
		{
			int[] size = getAtlasSize(imageFiles, imageNameSet);

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

		atlas.write(name);
		
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
