/****************************************/
/* NameRenderer.java					*/
/* Created on: 13-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.propertysheet;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 * 
 * Based upon the unmaintained code from L2FProd.com.
 */
public class NameRenderer extends DefaultTableCellRenderer
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------

	private CellBorder border;
	private PropertySheetTable propertySheetTable;

	//--------------------------------------------------------------------------

	public NameRenderer(PropertySheetTable propertySheetTable)
	{
		this.propertySheetTable = propertySheetTable;
		
		border = new CellBorder();		
	}

	//--------------------------------------------------------------------------

	private Color getForeground(boolean isProperty, boolean isSelected)
	{
		return (isProperty ?
				(isSelected ? propertySheetTable.getSelectedPropertyForeground() : propertySheetTable.getPropertyForeground()) : 
				(isSelected ? propertySheetTable.getSelectedCategoryForeground() : propertySheetTable.getCategoryForeground()));
	}

	//--------------------------------------------------------------------------

	private Color getBackground(boolean isProperty, boolean isSelected)
	{
		return (isProperty ? 
				(isSelected ? propertySheetTable.getSelectedPropertyBackground() : propertySheetTable.getPropertyBackground()) : 
				(isSelected ? propertySheetTable.getSelectedCategoryBackground() : propertySheetTable.getCategoryBackground()));
	}

	//--------------------------------------------------------------------------

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
		PropertySheetElement item = (PropertySheetElement) value;

		// shortcut if we are painting the category column
		if(column == PropertySheetTableModel.VALUE_COLUMN && !item.isProperty())
		{
			setBackground(getBackground(item.isProperty(), isSelected));
			setText("");
			return this;
		}

		setBorder(border);

		// configure the border
		border.configure((PropertySheetTable) table, item);

		setBackground(getBackground(item.isProperty(), isSelected));
		setForeground(getForeground(item.isProperty(), isSelected));

		setEnabled(isSelected || !item.isProperty() ? true : item.getProperty().isEditable());
		setText(item.getName());

		return this;
	}

	//--------------------------------------------------------------------------
}