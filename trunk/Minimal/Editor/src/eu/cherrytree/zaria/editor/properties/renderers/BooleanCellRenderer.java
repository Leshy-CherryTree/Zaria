/********************************************/
/* BooleanCellRenderer.java					*/
/*											*/
/* Initial version released by L2FProd.com	*/
/* under the Apache License, Version 2.0	*/
/*											*/
/* Adapted, modified and released			*/
/* by Cherry Tree Studio under EUPL v1.1	*/
/*											*/
/* Copyright Cherry Tree Studio 2013		*/
/********************************************/

package eu.cherrytree.zaria.editor.properties.renderers;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

public class BooleanCellRenderer extends JCheckBox implements TableCellRenderer, ListCellRenderer
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if(isSelected)
		{
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		}
		else
		{
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}

		setSelected(Boolean.TRUE.equals(value));

		return this;
	}

	//--------------------------------------------------------------------------

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		if(isSelected)
		{
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		}
		else
		{
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setSelected(Boolean.TRUE.equals(value));

		return this;
	}
}
