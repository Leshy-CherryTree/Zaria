/********************************************/
/* DefaultObjectRenderer.java				*/
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

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

public class DefaultCellRenderer extends DefaultTableCellRenderer implements ListCellRenderer
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private DefaultObjectRenderer objectRenderer = new DefaultObjectRenderer();

	//--------------------------------------------------------------------------

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		setBorder(null);

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

		setValue(value);

		return this;
	}

	//--------------------------------------------------------------------------

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		setValue(value);

		return this;
	}

	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{
		String text = convertToString(value);
		Icon icon = convertToIcon(value);

		setText(text == null ? "" : text);
		setIcon(icon);
		setDisabledIcon(icon);
	}

	//--------------------------------------------------------------------------

	protected String convertToString(Object value)
	{
		return objectRenderer.getText(value);
	}

	//--------------------------------------------------------------------------

	protected Icon convertToIcon(Object value)
	{
		return null;
	}

	//--------------------------------------------------------------------------
}
