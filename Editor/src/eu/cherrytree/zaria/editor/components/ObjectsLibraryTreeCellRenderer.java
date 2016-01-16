/****************************************/
/* ObjectsLibraryTreeCellRenderer.java	*/
/* Created on: 19-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/


package eu.cherrytree.zaria.editor.components;

import eu.cherrytree.zaria.editor.datamodels.PlacableCategory;
import eu.cherrytree.zaria.editor.datamodels.PlacableTreeElement;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ObjectsLibraryTreeCellRenderer extends DefaultTreeCellRenderer
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	public final static ImageIcon [] icons = {
					new ImageIcon(PaletteCellRenderer.class.getResource("/eu/cherrytree/zaria/editor/res/icons/mapobjects/category_closed.png")) , 
					new ImageIcon(PaletteCellRenderer.class.getResource("/eu/cherrytree/zaria/editor/res/icons/mapobjects/category_open.png")) ,
					new ImageIcon(PaletteCellRenderer.class.getResource("/eu/cherrytree/zaria/editor/res/icons/mapobjects/item.png")) ,
									};
	
	
	//--------------------------------------------------------------------------
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		if(PlacableTreeElement.class.isAssignableFrom(value.getClass()))
			setIcon(icons[2]);
		else if(PlacableCategory.class.isAssignableFrom(value.getClass()))
		{				
			setOpenIcon(icons[1]);
			setClosedIcon(icons[0]);
		}
		
		return this;
	}
	
	//--------------------------------------------------------------------------
}
