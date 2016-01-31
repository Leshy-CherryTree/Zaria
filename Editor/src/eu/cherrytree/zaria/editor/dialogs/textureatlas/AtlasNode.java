/****************************************/
/* AtlasNode.java						*/
/* Created on: 01-Jan-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs.textureatlas;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Algorithm from http://www.blackpawn.com/texts/lightmaps/
 * 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
class AtlasNode
{
	//--------------------------------------------------------------------------

	public Rectangle rect;
	public AtlasNode child[];
	public BufferedImage image;

	//--------------------------------------------------------------------------
	
	public AtlasNode(int x, int y, int width, int height)
	{
		rect = new Rectangle(x, y, width, height);
		child = new AtlasNode[2];
		child[0] = null;
		child[1] = null;
		image = null;
	}

	//--------------------------------------------------------------------------
	
	public boolean isLeaf()
	{
		return child[0] == null && child[1] == null;
	}

	//--------------------------------------------------------------------------
	
	public AtlasNode insert(BufferedImage image, int padding)
	{
		if (!isLeaf())
		{
			AtlasNode newNode = child[0].insert(image, padding);

			if (newNode != null)
			{
				return newNode;
			}

			return child[1].insert(image, padding);
		}
		else
		{
			if (this.image != null)
			{
				// occupied
				return null; 
			}
			if (image.getWidth() > rect.width || image.getHeight() > rect.height)
			{
				// does not fit
				return null; 
			}
			if (image.getWidth() == rect.width && image.getHeight() == rect.height)
			{
				// perfect fit
				this.image = image; 
				return this;
			}

			int dw = rect.width - image.getWidth();
			int dh = rect.height - image.getHeight();

			if (dw > dh)
			{
				child[0] = new AtlasNode(rect.x, rect.y, image.getWidth(), rect.height);
				child[1] = new AtlasNode(padding + rect.x + image.getWidth(), rect.y, rect.width - image.getWidth() - padding, rect.height);
			}
			else
			{
				child[0] = new AtlasNode(rect.x, rect.y, rect.width, image.getHeight());
				child[1] = new AtlasNode(rect.x, padding + rect.y + image.getHeight(), rect.width, rect.height - image.getHeight() - padding);
			}

			return child[0].insert(image, padding);
		}
	}

	//--------------------------------------------------------------------------
}
