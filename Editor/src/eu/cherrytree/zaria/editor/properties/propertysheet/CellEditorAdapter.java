/********************************************/
/* CellEditorAdapter.java					*/
/*											*/
/* Initial version released by L2FProd.com	*/
/* under the Apache License, Version 2.0	*/
/*											*/
/* Adapted, modified and released			*/
/* by Cherry Tree Studio under EUPL v1.1	*/
/*											*/
/* Copyright Cherry Tree Studio 2013		*/
/********************************************/

package eu.cherrytree.zaria.editor.properties.propertysheet;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellEditor;

/**
 * Allows to use any PropertyEditor as a Table or Tree cell editor. <br>
 */
public class CellEditorAdapter extends AbstractCellEditor implements TableCellEditor, TreeCellEditor
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	protected PropertyEditor editor;
	protected int clickCountToStart = 1;
	
	//--------------------------------------------------------------------------

	private class CommitEditing implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			stopCellEditing();
		}
	}
	
	//--------------------------------------------------------------------------

	private class CancelEditing implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			CellEditorAdapter.this.cancelCellEditing();
		}
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Select all text when focus gained, deselect when focus lost.
	 */
	private class SelectOnFocus implements FocusListener
	{
		@Override
		public void focusGained(final FocusEvent e)
		{
			if(!(e.getSource() instanceof JTextField))
				return;
			
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					((JTextField) e.getSource()).selectAll();
				}
			});
		}

		@Override
		public void focusLost(final FocusEvent e)
		{
			if(!(e.getSource() instanceof JTextField))
				return;
			
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					((JTextField) e.getSource()).select(0, 0);
				}
			});
		}
	}
	
	//--------------------------------------------------------------------------

	public CellEditorAdapter(PropertyEditor editor)
	{
		this.editor = editor;
		Component component = editor.getCustomEditor();
		if(component instanceof JTextField)
		{
			JTextField field = (JTextField) component;
			field.addFocusListener(new SelectOnFocus());
			field.addActionListener(new CommitEditing());
			field.registerKeyboardAction(new CancelEditing(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);
		}

		// when the editor notifies a change, commit the changes
		editor.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				stopCellEditing();
			}
		});
	}

	//--------------------------------------------------------------------------

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row)
	{
		return getEditor(value);
	}

	//--------------------------------------------------------------------------

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column)
	{
		return getEditor(value);
	}

	//--------------------------------------------------------------------------

	public void setClickCountToStart(int count)
	{
		clickCountToStart = count;
	}

	//--------------------------------------------------------------------------

	public int getClickCountToStart()
	{
		return clickCountToStart;
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getCellEditorValue()
	{
		return editor.getValue();
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isCellEditable(EventObject event)
	{
		if(event instanceof MouseEvent)
			return ((MouseEvent) event).getClickCount() >= clickCountToStart;

		return true;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean shouldSelectCell(EventObject event)
	{
		return true;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean stopCellEditing()
	{
		fireEditingStopped();
		return true;
	}

	//--------------------------------------------------------------------------

	@Override
	public void cancelCellEditing()
	{
		fireEditingCanceled();
	}

	//--------------------------------------------------------------------------

	private Component getEditor(Object value)
	{
		editor.setValue(value);

		final Component cellEditor = editor.getCustomEditor();

		// request focus later so the editor can be used to enter value as soon as
		// made visible
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				cellEditor.requestFocus();
			}
		});

		return cellEditor;
	}

	//--------------------------------------------------------------------------
}
