/****************************************/
/* PaletteCellRenderer.java            */
/* Created on: 09-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.components;

import eu.cherrytree.zaria.editor.datamodels.palette.PaletteElement;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class PaletteCellRenderer extends DefaultTreeCellRenderer
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	public final static ImageIcon [] icons = {
					new ImageIcon(PaletteCellRenderer.class.getResource("/eu/cherrytree/zaria/editor/res/icons/pallette/category_closed.png")) , 
					new ImageIcon(PaletteCellRenderer.class.getResource("/eu/cherrytree/zaria/editor/res/icons/pallette/category_open.png"))
									};
	
	
	//--------------------------------------------------------------------------
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		PaletteElement element = ((PaletteElement)value);
						
		setToolTipText(element.getToolTip());
  				
		Icon icon = element.getIcon();		
		
		if(icon != null)
			setIcon(icon);
		else
		{				
			setOpenIcon(icons[1]);
			setClosedIcon(icons[0]);
		}
		
		return this;
	}
	
	//--------------------------------------------------------------------------
}
