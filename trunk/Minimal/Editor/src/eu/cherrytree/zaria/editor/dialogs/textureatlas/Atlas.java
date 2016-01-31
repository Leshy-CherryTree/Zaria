/****************************************/
/* Atlas.java							*/
/* Created on: 01-Jan-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs.textureatlas;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.ReflectionTools;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.editor.document.ZoneMetadata;
import eu.cherrytree.zaria.editor.serialization.Serializer;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.texture.TextureArea;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class Atlas
{
	//--------------------------------------------------------------------------	
	
	private static final String separator = System.getProperty("line.separator");
	private static final String assetDirectory = "Game_Assets";
	
	//--------------------------------------------------------------------------	
	
	private BufferedImage image;
	private Graphics2D graphics;
	private AtlasNode root;
	private Map<String, Rectangle> rectangleMap;
	private int textureWidth;
	private int textureHeight;
	
	//--------------------------------------------------------------------------	

	public Atlas(int width, int height)
	{
		image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		graphics = image.createGraphics();

		root = new AtlasNode(0, 0, width, height);
		rectangleMap = new TreeMap<>();

		textureWidth = width;
		textureHeight = height;
	}
	
	//--------------------------------------------------------------------------	

	public boolean addImage(BufferedImage image, String name, int padding)
	{
		AtlasNode node = root.insert(image, padding);

		if (node == null)
		{
			return false;
		}

		rectangleMap.put(name, node.rect);

		int w = textureWidth;
		int h = textureHeight;

		// left strip (1 pixel wide)
		if (node.rect.x > 0)
		{
			graphics.drawImage(image,
				node.rect.x - 1, node.rect.y, node.rect.x, node.rect.y + node.rect.height,
				0, 0, 1, node.rect.height, null);
		}

		// right strip (1 pixel wide)
		if (node.rect.x < w - 1)
		{
			graphics.drawImage(image,
				node.rect.x + node.rect.width, node.rect.y, node.rect.x + node.rect.width + 1, node.rect.y + node.rect.height,
				node.rect.width - 1, 0, node.rect.width, node.rect.height, null);
		}

		// top strip (1 pixel high)
		if (node.rect.y > 0)
		{
			graphics.drawImage(image,
				node.rect.x, node.rect.y - 1, node.rect.x + node.rect.width, node.rect.y,
				0, 0, node.rect.width, 1, null);
		}

		// bottom strip (1 pixel high)
		if (node.rect.y < h - 1)
		{
			graphics.drawImage(image,
				node.rect.x, node.rect.y + node.rect.height, node.rect.x + node.rect.width, node.rect.y + node.rect.height + 1,
				0, node.rect.height - 1, node.rect.width, node.rect.height, null);
		}

		// topleft
		if (node.rect.x > 0 && node.rect.y > 0)
		{
			graphics.drawImage(image,
				node.rect.x - 1, node.rect.y - 1, node.rect.x, node.rect.y,
				0, 0, 1, 1, null);
		}

		// topright
		if (node.rect.x < w - 1 && node.rect.y > 0)
		{
			graphics.drawImage(image,
				node.rect.x + node.rect.width, node.rect.y - 1, node.rect.x + node.rect.width + 1, node.rect.y,
				node.rect.width - 1, 0, node.rect.width, 1, null);
		}

		// bottomright
		if (node.rect.x < w - 1 && node.rect.y < h - 1)
		{
			graphics.drawImage(image,
				node.rect.x + node.rect.width, node.rect.y + node.rect.height, node.rect.x + node.rect.width + 1, node.rect.y + node.rect.height + 1,
				node.rect.width - 1, node.rect.height - 1, node.rect.width, node.rect.height, null);
		}

		// bottomleft
		if (node.rect.x > 0 && node.rect.y < h - 1)
		{
			graphics.drawImage(image,
				node.rect.x - 1, node.rect.y + node.rect.height, node.rect.x, node.rect.y + node.rect.height + 1,
				0, node.rect.height - 1, 1, node.rect.height, null);
		}

		graphics.drawImage(image, node.rect.x, node.rect.y, null);

		return true;
	}
	
	//--------------------------------------------------------------------------	

	private void writeRects(String path) throws IOException, SecurityException, IllegalArgumentException, NoSuchFieldException
	{
		ZariaObjectDefinition[] areas = new ZariaObjectDefinition[rectangleMap.size()];
		int index = 0;
		
		for (Map.Entry<String, Rectangle> e : rectangleMap.entrySet())
		{
			Rectangle r = e.getValue();
			
			String element_name = e.getKey().substring(e.getKey().lastIndexOf('/') + 1);
			String base = element_name + path;
			
			UUID uuid = UUID.nameUUIDFromBytes(base.getBytes());		
			
			TextureArea area = ReflectionTools.createNewDefinition(TextureArea.class, element_name, uuid);
			
			ReflectionTools.setDefinitionFieldValue(area, "x", r.x);
			ReflectionTools.setDefinitionFieldValue(area, "y", r.y);
			ReflectionTools.setDefinitionFieldValue(area, "w", r.width);
			ReflectionTools.setDefinitionFieldValue(area, "h", r.height);
			ReflectionTools.setDefinitionFieldValue(area, "texture", path);
			
			ZoneMetadata metadata = new ZoneMetadata();
			metadata.setLocX(50);
			metadata.setLocY(25 + 65 * index);
			
			ZoneDocument.saveMetadata(area, metadata);
			
			areas[index] = area;
			index++;
		}
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(EditorApplication.getAssetsLocation() + path + ".zone"))))
		{
			writer.write(Serializer.getText(areas));
		}
	}
	
	//--------------------------------------------------------------------------	

	public void write(String path)
	{
		try
		{
			ImageIO.write(image, "png", new File(EditorApplication.getAssetsLocation() + path + ".png"));
			writeRects(path);
		}
		catch (IOException | SecurityException | IllegalArgumentException | NoSuchFieldException ex)
		{
			Logger.getLogger(Atlas.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------	
}
