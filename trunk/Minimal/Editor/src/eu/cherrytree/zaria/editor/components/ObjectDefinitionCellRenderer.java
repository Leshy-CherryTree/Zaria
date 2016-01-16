/****************************************/
/* ObjectDefinitionCellRenderer.java 	*/
/* Created on: 27-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.components;

import eu.cherrytree.zaria.editor.datamodels.definitions.ObjectDefinitiontType;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ObjectDefinitionCellRenderer extends DefaultTreeCellRenderer
{
	//--------------------------------------------------------------------------

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		if(value instanceof ObjectDefinitiontType)
		{
			ObjectDefinitiontType type = (ObjectDefinitiontType) value;
			setIcon(type.getIcon());
		}
		else
			setIcon(null);
		
		return this;
	}
	
	//--------------------------------------------------------------------------
}
