/********************************************/
/* PropertySheetTable.java					*/
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

import eu.cherrytree.zaria.editor.properties.Property;
import eu.cherrytree.zaria.editor.properties.editors.PropertyEditorFactory;
import eu.cherrytree.zaria.editor.properties.renderers.PropertyRendererFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyEditor;

import javax.swing.AbstractAction;
import javax.swing.CellEditor;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;


/**
 * A table which allows the editing of Properties through
 * PropertyEditors. The PropertyEditors can be changed by using the
 * PropertyEditorRegistry.
 */
public class PropertySheetTable extends JTable
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	/**
	 * Cancels the cell editing if any update happens while modifying a value.
	 */
	private class CancelEditing implements TableModelListener
	{
		@Override
		public void tableChanged(TableModelEvent e)
		{
			// in case the table changes for the following reasons:
			// * the editing row has changed
			// * the editing row was removed
			// * all rows were changed
			// * rows were added
			//
			// it is better to cancel the editing of the row as our editor
			// may no longer be the right one. It happens when you play with
			// the sorting while having the focus in one editor.
			if(e.getType() == TableModelEvent.UPDATE)
			{
				int first = e.getFirstRow();
				int last = e.getLastRow();
				int editingRow = PropertySheetTable.this.getEditingRow();

				TableCellEditor editor = PropertySheetTable.this.getCellEditor();
				
				if(editor != null && first <= editingRow && editingRow <= last)
					editor.cancelCellEditing();
			}
		}
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Starts value cell editing even if value cell does not have the focus but
	 * only if row is selected.
	 */
	private static class StartEditingAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e)
		{
			JTable table = (JTable) e.getSource();
			if(!table.hasFocus())
			{
				CellEditor cellEditor = table.getCellEditor();
				
				if(cellEditor != null && !cellEditor.stopCellEditing())
					return;
				
				table.requestFocus();
				
				return;
			}
			
			ListSelectionModel rsm = table.getSelectionModel();
			int anchorRow = rsm.getAnchorSelectionIndex();
			table.editCellAt(anchorRow, PropertySheetTableModel.VALUE_COLUMN);
			Component editorComp = table.getEditorComponent();
			
			if(editorComp != null)
				editorComp.requestFocus();
		}
	}

	//--------------------------------------------------------------------------
	
	/**
	 * Toggles the state of a row between expanded/collapsed. Works only for
	 * rows with "toggle" knob.
	 */
	private class ToggleAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e)
		{
			int row = PropertySheetTable.this.getSelectedRow();
			PropertySheetElement item = PropertySheetTable.this.getSheetModel().getPropertySheetElement(row);
			item.toggle();
			PropertySheetTable.this.addRowSelectionInterval(row, row);
		}

		@Override
		public boolean isEnabled()
		{
			int row = PropertySheetTable.this.getSelectedRow();
			if(row != -1)
			{
				PropertySheetElement item = PropertySheetTable.this.getSheetModel() .getPropertySheetElement(row);
				return item.hasToggle();
			}
			else
			{
				return false;
			}
		}
	}

	//--------------------------------------------------------------------------
	
	private static class ToggleMouseHandler extends MouseAdapter
	{
		@Override
		public void mouseReleased(MouseEvent event)
		{
			PropertySheetTable table = (PropertySheetTable) event.getComponent();
			int row = table.rowAtPoint(event.getPoint());
			int column = table.columnAtPoint(event.getPoint());
			
			if(row != -1 && column == 0)
			{
				// if we clicked on an PropertySheetElement, see if we clicked on its hotspot
				PropertySheetElement item = table.getSheetModel().getPropertySheetElement(row);
				int x = event.getX() - getIndent(table, item);
				if(x > 0 && x < HOTSPOT_SIZE)
					item.toggle();
			}
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static final int HOTSPOT_SIZE = 18;

	public static final String TREE_EXPANDED_ICON_KEY = "Tree.expandedIcon";
	public static final String TREE_COLLAPSED_ICON_KEY = "Tree.collapsedIcon";
	public static final String TABLE_BACKGROUND_COLOR_KEY = "Table.background";
	public static final String TABLE_FOREGROUND_COLOR_KEY = "Table.foreground";
	public static final String TABLE_SELECTED_BACKGROUND_COLOR_KEY = "Table.selectionBackground";
	public static final String TABLE_SELECTED_FOREGROUND_COLOR_KEY = "Table.selectionForeground";
	public static final String PANEL_BACKGROUND_COLOR_KEY = "Panel.background";

	//--------------------------------------------------------------------------

	private PropertyEditorFactory editorFactory;
	private PropertyRendererFactory rendererFactory;

	private TableCellRenderer nameRenderer;  

	private boolean wantsExtraIndent = false;

	// Cancel editing when editing row is changed
	private TableModelListener cancelEditing;

	// Colors used by renderers
	private Color categoryBackground;
	private Color categoryForeground;
	private Color propertyBackground;
	private Color propertyForeground;
	private Color selectedPropertyBackground;
	private Color selectedPropertyForeground;
	private Color selectedCategoryBackground;
	private Color selectedCategoryForeground;

	//--------------------------------------------------------------------------
  
	public PropertySheetTable(PropertyRendererFactory rendererFactory, PropertyEditorFactory editorFactory)
	{
		this(new PropertySheetTableModel(), rendererFactory, editorFactory);
	}

	//--------------------------------------------------------------------------

	public PropertySheetTable(PropertySheetTableModel dm, PropertyRendererFactory rendererFactory, PropertyEditorFactory editorFactory)
	{
		super(dm);
		
		this.rendererFactory = rendererFactory;
		this.editorFactory = editorFactory;
		
		initDefaultColors();

		// select only one property at a time
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// hide the table header, we do not need it
		Dimension nullSize = new Dimension(0, 0);
		getTableHeader().setPreferredSize(nullSize);
		getTableHeader().setMinimumSize(nullSize);
		getTableHeader().setMaximumSize(nullSize);
		getTableHeader().setVisible(false);

		// table header not being visible, make sure we can still resize the columns
		new HeaderlessColumnResizer(this);

		nameRenderer = new NameRenderer(this);

		// force the JTable to commit the edit when it losts focus
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		// only full rows can be selected
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);

		// replace the edit action to always trigger the editing of the value column
		getActionMap().put("startEditing", new StartEditingAction());

		// ensure navigating with "TAB" moves to the next row
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "selectNextRowCell");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK), "selectPreviousRowCell");

		// allow category toggle with SPACE and mouse
		getActionMap().put("toggle", new ToggleAction());
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "toggle");
		addMouseListener(new ToggleMouseHandler());
	}

	//--------------------------------------------------------------------------

	/**
	 * Initialises the default set of colours used by the PropertySheetTable.
	 *
	 * @see #categoryBackground
	 * @see #categoryForeground
	 * @see #selectedCategoryBackground
	 * @see #selectedCategoryForeground
	 * @see #propertyBackground
	 * @see #propertyForeground
	 * @see #selectedPropertyBackground
	 * @see #selectedPropertyForeground
	 */
	private void initDefaultColors()
	{
		this.categoryBackground = UIManager.getColor(PANEL_BACKGROUND_COLOR_KEY);
		this.categoryForeground = UIManager.getColor(TABLE_FOREGROUND_COLOR_KEY).darker().darker().darker();

		this.selectedCategoryBackground = categoryBackground.darker();
		this.selectedCategoryForeground = categoryForeground;

		this.propertyBackground = UIManager.getColor(TABLE_BACKGROUND_COLOR_KEY);
		this.propertyForeground = UIManager.getColor(TABLE_FOREGROUND_COLOR_KEY);

		this.selectedPropertyBackground = UIManager.getColor(TABLE_SELECTED_BACKGROUND_COLOR_KEY);
		this.selectedPropertyForeground = UIManager.getColor(TABLE_SELECTED_FOREGROUND_COLOR_KEY);

		setGridColor(categoryBackground);
	}

	//--------------------------------------------------------------------------

	public Color getCategoryBackground()
	{
		return categoryBackground;
	}

	//--------------------------------------------------------------------------

	/**
	 * Sets the colour used to paint a Category background.
	 *
	 * @param categoryBackground
	 */
	public void setCategoryBackground(Color categoryBackground)
	{
		this.categoryBackground = categoryBackground;
		repaint();
	}

	//--------------------------------------------------------------------------

	public Color getCategoryForeground()
	{
		return categoryForeground;
	}

	//--------------------------------------------------------------------------

	/**
	 * Sets the colour used to paint a Category foreground.
	 *
	 * @param categoryForeground
	 */
	public void setCategoryForeground(Color categoryForeground)
	{
		this.categoryForeground = categoryForeground;
		repaint();
	}

	//--------------------------------------------------------------------------

	public Color getSelectedCategoryBackground()
	{
		return selectedCategoryBackground;
	}

	//--------------------------------------------------------------------------

	/**
	 * Sets the colour used to paint a selected/focused Category background.
	 *
	 * @param selectedCategoryBackground
	 */
	public void setSelectedCategoryBackground(Color selectedCategoryBackground)
	{
		this.selectedCategoryBackground = selectedCategoryBackground;
		repaint();
	}

	//--------------------------------------------------------------------------

	public Color getSelectedCategoryForeground()
	{
		return selectedCategoryForeground;
	}

	//--------------------------------------------------------------------------

	/**
	 * Sets the colour used to paint a selected/focused Category foreground.
	 *
	 * @param selectedCategoryForeground
	 */
	public void setSelectedCategoryForeground(Color selectedCategoryForeground)
	{
		this.selectedCategoryForeground = selectedCategoryForeground;
		repaint();
	}

	//--------------------------------------------------------------------------

	public Color getPropertyBackground()
	{
		return propertyBackground;
	}

	//--------------------------------------------------------------------------

	/**
	 * Sets the colour used to paint a Property background.
	 *
	 * @param propertyBackground
	 */
	public void setPropertyBackground(Color propertyBackground)
	{
		this.propertyBackground = propertyBackground;
		repaint();
	}

	//--------------------------------------------------------------------------

	public Color getPropertyForeground()
	{
		return propertyForeground;
	}

	//--------------------------------------------------------------------------

	/**
	 * Sets the colour used to paint a Property foreground.
	 *
	 * @param propertyForeground
	 */
	public void setPropertyForeground(Color propertyForeground)
	{
		this.propertyForeground = propertyForeground;
		repaint();
	}

	//--------------------------------------------------------------------------

	public Color getSelectedPropertyBackground()
	{
		return selectedPropertyBackground;
	}

	//--------------------------------------------------------------------------

	/**
	 * Sets the colour used to paint a selected/focused Property background.
	 *
	 * @param selectedPropertyBackground
	 */
	public void setSelectedPropertyBackground(Color selectedPropertyBackground)
	{
		this.selectedPropertyBackground = selectedPropertyBackground;
		repaint();
	}

	//--------------------------------------------------------------------------

	public Color getSelectedPropertyForeground()
	{
		return selectedPropertyForeground;
	}

	//--------------------------------------------------------------------------

	/**
	 * Sets the colour used to paint a selected/focused Property foreground.
	 *
	 * @param selectedPropertyForeground
	 */
	public void setSelectedPropertyForeground(Color selectedPropertyForeground)
	{
		this.selectedPropertyForeground = selectedPropertyForeground;
		repaint();
	}

	//--------------------------------------------------------------------------

	public final PropertyEditorFactory getEditorFactory()
	{
		return editorFactory;
	}

	//--------------------------------------------------------------------------

	public PropertyRendererFactory getRendererFactory()
	{
		return rendererFactory;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isCellEditable(int row, int column)
	{
		// names are not editable
		if(column == 0)
			return false;

		PropertySheetElement item = getSheetModel().getPropertySheetElement(row);
		return item.isProperty() && item.getProperty().isEditable();
	}

	//--------------------------------------------------------------------------

	/**
	 * Gets the CellEditor for the given row and column. It uses the editor
	 * registry to find a suitable editor for the property.
	 *
	 * @see javax.swing.JTable#getCellEditor(int, int)
	 */
	@Override
	public TableCellEditor getCellEditor(int row, int column)
	{
		if(column == 0)
			return null;

		PropertySheetElement item = getSheetModel().getPropertySheetElement(row);
		
		if(!item.isProperty())
			return null;

		TableCellEditor result = null;
		Property property = item.getProperty();
		PropertyEditor editor = getEditorFactory().createPropertyEditor(property);
		
		if(editor != null)
			result = new CellEditorAdapter(editor);

		return result;
	}

	//--------------------------------------------------------------------------
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column)
	{
		PropertySheetElement item = getSheetModel().getPropertySheetElement(row);

		switch(column)
		{
			case PropertySheetTableModel.NAME_COLUMN:
				// name column gets a custom renderer
				return nameRenderer;

			case PropertySheetTableModel.VALUE_COLUMN:
			{
				if(!item.isProperty())
					return nameRenderer;

				// property value column gets the renderer from the factory
				Property property = item.getProperty();
				TableCellRenderer renderer = getRendererFactory().createTableCellRenderer(property);
				
				if(renderer == null)
					renderer = getCellRenderer(property.getType());
				
				return renderer;
			}
			default:
				// when will this happen, given the above?
				return super.getCellRenderer(row, column);
		}
	}

	//--------------------------------------------------------------------------

	/**
	 * Helper method to lookup a cell renderer based on type.
	 *
	 * @param type the type for which a renderer should be found
	 * @return a renderer for the given object type
	 */
	private TableCellRenderer getCellRenderer(Class type)
	{
		// try to create one from the factory
		TableCellRenderer renderer = getRendererFactory().createTableCellRenderer(type);

		// if that fails, recursively try again with the superclass
		if(renderer == null && type != null)
			renderer = getCellRenderer(type.getSuperclass());

		// if that fails, just use the default Object renderer
		if(renderer == null)
			renderer = super.getDefaultRenderer(Object.class);

		return renderer;
	}

	//--------------------------------------------------------------------------

	public final PropertySheetTableModel getSheetModel()
	{
		return (PropertySheetTableModel) getModel();
	}

	//--------------------------------------------------------------------------

	/**
	 * Overriden <li>to prevent the cell focus rect to be painted <li>to disable
	 * ({@link Component#setEnabled(boolean)} the renderer if the Property is
	 * not editable
	 */
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
	{
		Object value = getValueAt(row, column);
		boolean isSelected = isCellSelected(row, column);
		
		Component component = renderer.getTableCellRendererComponent(this, value,isSelected, false, row, column);
		PropertySheetElement item = getSheetModel().getPropertySheetElement(row);
		
		if(item.isProperty())
			component.setEnabled(item.getProperty().isEditable());
		
		return component;
	}

	//--------------------------------------------------------------------------

	/**
	 * Overriden to register a listener on the model. This listener ensures
	 * editing is cancelled when editing row is being changed.
	 *
	 * @see javax.swing.JTable#setModel(javax.swing.table.TableModel)
	 * @throws IllegalArgumentException if dataModel is not a
	 * {@link PropertySheetTableModel}
	 */
	@Override
	public void setModel(TableModel newModel)
	{
		if(!(newModel instanceof PropertySheetTableModel))
			throw new IllegalArgumentException("dataModel must be of type " + PropertySheetTableModel.class.getName());

		if(cancelEditing == null)
			cancelEditing = new CancelEditing();

		TableModel oldModel = getModel();
		
		if(oldModel != null)
			oldModel.removeTableModelListener(cancelEditing);
		
		super.setModel(newModel);
		newModel.addTableModelListener(cancelEditing);

		// ensure the "value" column can not be resized
		getColumnModel().getColumn(1).setResizable(false);
	}

	//--------------------------------------------------------------------------

	public boolean getWantsExtraIndent()
	{
		return wantsExtraIndent;
	}

	//--------------------------------------------------------------------------

	/**
	 * By default, properties with children are painted with the same indent
	 * level as other properties and categories. When nested properties exist
	 * within the set of properties, the end-user might be confused by the
	 * category and property handles. Sets this property to true to add an extra
	 * indent level to properties.
	 */
	public void setWantsExtraIndent(boolean wantsExtraIndent)
	{
		this.wantsExtraIndent = wantsExtraIndent;
		repaint();
	}

	//--------------------------------------------------------------------------

	/**
	 * Ensures the table uses the full height of its parent
	 */
	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		return getPreferredSize().height < getParent().getHeight();
	}

	//--------------------------------------------------------------------------	
	
	public void commitEditing()
	{
		TableCellEditor editor = getCellEditor();
	
		if(editor != null)
			editor.stopCellEditing();
	}

	//--------------------------------------------------------------------------
	
	public void cancelEditing()
	{
		TableCellEditor editor = getCellEditor();
		if(editor != null)
		{
			editor.cancelCellEditing();
		}
	}
	
	//--------------------------------------------------------------------------

	/**
	 * Calculates the required left indent for a given item, given its type and
	 * its hierarchy level.
	 */
	public static int getIndent(PropertySheetTable table, PropertySheetElement item)
	{
		int indent;

		if(item.isProperty())
		{
			// it is a property, it has no parent or a category, and no child
			if((item.getParent() == null || !item.getParent().isProperty())
					&& !item.hasToggle())
			{
				indent = table.getWantsExtraIndent() ? HOTSPOT_SIZE : 0;
			}
			else
			{
				// it is a property with children
				if(item.hasToggle())
				{
					indent = item.getDepth() * HOTSPOT_SIZE;
				}
				else
				{
					indent = (item.getDepth() + 1) * HOTSPOT_SIZE;
				}
			}

			if(table.getWantsExtraIndent())
			{
				indent += HOTSPOT_SIZE;
			}

		}
		else
		{
			// category has no indent
			indent = 0;
		}
		return indent;
	}

	//--------------------------------------------------------------------------
}
