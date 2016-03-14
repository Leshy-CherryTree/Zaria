/********************************************/
/* PropertySheetPanel.java					*/
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

import java.awt.Dimension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * An implementation of a PropertySheet which shows a table to
 * edit/view values, a description pane which updates when the
 * selection changes and buttons to toggle between a flat view and a
 * by-category view of the properties. A button in the toolbar allows
 * to sort the properties and categories by name.
 * <p>
 * Default sorting is by name (case-insensitive). Custom sorting can
 * be implemented through
 * {@link com.l2fprod.common.propertysheet.PropertySheetTableModel#setCategorySortingComparator(Comparator)}
 * and
 * {@link com.l2fprod.common.propertysheet.PropertySheetTableModel#setPropertySortingComparator(Comparator)}
 */
public final class PropertySheetPanel extends JPanel implements PropertyChangeListener
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	class SelectionListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			int row = table.getSelectedRow();
			Property prop = null;
			
			if (row >= 0 && table.getRowCount() > row)
				prop = model.getPropertySheetElement(row).getProperty();
			
			if (prop != null)
			{
				descriptionPanel.setText("<html>"
						+ "[" + (prop.getDisplayName() == null ? "" : prop.getDisplayName()) + "]"
						+ "<br>"
						+ (prop.getShortDescription() == null ? "" : prop.getShortDescription())
					+ "</font>"
				);
			}
			else
			{
				descriptionPanel.setText("<html>");
			}

			//position it at the top
			descriptionPanel.setCaretPosition(0);
		}
	}
	
	//--------------------------------------------------------------------------

	private PropertySheetTable table;
	private PropertySheetTableModel model;
	
	private ListSelectionListener selectionListener = new SelectionListener();
	
	private JScrollPane tableScroll;
	private JSplitPane split;
	private JEditorPane descriptionPanel;
	private JScrollPane descriptionScrollPane;
	
	private int lastDescriptionHeight;

	//--------------------------------------------------------------------------

	public PropertySheetPanel(PropertyRendererFactory rendererFactory, PropertyEditorFactory editorFactory)
	{
		this(new PropertySheetTable(rendererFactory, editorFactory));
	}

	//--------------------------------------------------------------------------
	
	public PropertySheetPanel(PropertySheetTable table)
	{
		buildUI();
		setTable(table);
	}

	//--------------------------------------------------------------------------

	/**
	 * Sets the table used by this panel.
	 *
	 * Note: listeners previously added with
	 * {@link PropertySheetPanel#addPropertySheetChangeListener(PropertyChangeListener)}
	 * must be re-added after this call if the table model is not the same as
	 * the previous table.
	 */
	public void setTable(PropertySheetTable table)
	{
		if(table == null)
		{
			throw new IllegalArgumentException("table must not be null");
		}

		// remove the property change listener from any previous model
		if(model != null)
			model.removePropertyChangeListener(this);

		// get the model from the table
		model = (PropertySheetTableModel) table.getModel();
		model.addPropertyChangeListener(this);

		// remove the listener from the old table
		if(this.table != null)
			this.table.getSelectionModel().removeListSelectionListener(
					selectionListener);

		// prepare the new table
		table.getSelectionModel().addListSelectionListener(selectionListener);
		tableScroll.getViewport().setView(table);

		// use the new table as our table
		this.table = table;
	}

	//--------------------------------------------------------------------------

	/**
	 * React to property changes by repainting.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		repaint();
	}

	//--------------------------------------------------------------------------

	public PropertySheetTable getTable()
	{
		return table;
	}

	//--------------------------------------------------------------------------

	public void setDescriptionVisible(boolean visible)
	{
		if(visible)
		{
			add("Center", split);
			split.setTopComponent(tableScroll);
			split.setBottomComponent(descriptionScrollPane);
			// restore the divider location
			split.setDividerLocation(split.getHeight() - lastDescriptionHeight);
		}
		else
		{
			// save the size of the description pane to restore it later
			lastDescriptionHeight = split.getHeight() - split.getDividerLocation();
			remove(split);
			add("Center", tableScroll);
		}

		PropertySheetPanel.this.revalidate();
	}

	//--------------------------------------------------------------------------

	public void setProperties(Property[] properties)
	{
		model.setProperties(properties);
	}

	//--------------------------------------------------------------------------

	public Property[] getProperties()
	{
		return model.getProperties();
	}

	//--------------------------------------------------------------------------

	public void addProperty(Property property)
	{
		model.addProperty(property);
	}

	//--------------------------------------------------------------------------

	public void addProperty(int index, Property property)
	{
		model.addProperty(index, property);
	}

	//--------------------------------------------------------------------------

	public void removeProperty(Property property)
	{
		model.removeProperty(property);
	}

	//--------------------------------------------------------------------------

	public int getPropertyCount()
	{
		return model.getPropertyCount();
	}

	//--------------------------------------------------------------------------

	public Iterator propertyIterator()
	{
		return model.propertyIterator();
	}

	//--------------------------------------------------------------------------

	/**
	 * Initialises the PropertySheet from the given object. If any, it cancels
	 * pending edit before proceeding with properties.
	 */
	public void readFromObject(Object data)
	{
		// cancel pending edits
		getTable().cancelEditing();

		Property[] properties = model.getProperties();
		for(int i = 0, c = properties.length ; i < c ; i++)
		{
			properties[i].readFromObject(data);
		}
		repaint();
	}

	//--------------------------------------------------------------------------

	/**
	 * Writes the PropertySheet to the given object. If any, it commits pending
	 * edit before proceeding with properties.
	 */
	public void writeToObject(Object data)
	{
		// ensure pending edits are committed
		getTable().commitEditing();

		Property[] properties = getProperties();
		
		for(int i = 0 ; i < properties.length ; i++)
			properties[i].writeToObject(data);
	}

	//--------------------------------------------------------------------------

	public void addPropertySheetChangeListener(PropertyChangeListener listener)
	{
		model.addPropertyChangeListener(listener);
	}

	//--------------------------------------------------------------------------

	public void removePropertySheetChangeListener(PropertyChangeListener listener)
	{
		model.removePropertyChangeListener(listener);
	}

	//--------------------------------------------------------------------------

	public PropertyEditorFactory getEditorFactory()
	{
		return table.getEditorFactory();
	}

	//--------------------------------------------------------------------------

	public PropertyRendererFactory getRendererFactory()
	{
		return table.getRendererFactory();
	}

	//--------------------------------------------------------------------------

	/**
	 * Set wether or not toggle states are restored when new properties are
	 * applied.
	 */
	public void setRestoreToggleStates(boolean value)
	{
		model.setRestoreToggleStates(value);
	}

	//--------------------------------------------------------------------------
	
	public boolean isRestoreToggleStates()
	{
		return model.isRestoreToggleStates();
	}

	//--------------------------------------------------------------------------
	
	public Map getToggleStates()
	{
		return model.getToggleStates();
	}

	//--------------------------------------------------------------------------

	/**
	 * Sets the toggle states for the category views. Note this <b>MUST</b> be
	 * called <b>BEFORE</b> setting any properties.
	 *
	 * @param toggleStates the toggle states as returned by getToggleStates
	 */
	public void setToggleStates(Map toggleStates)
	{
		model.setToggleStates(toggleStates);
	}

	//--------------------------------------------------------------------------

	private void buildUI()
	{
		UIUtils.setBorderLayout(this);
		UIUtils.setBorder(this);
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setBorder(null);
		split.setResizeWeight(1.0);
		split.setContinuousLayout(true);
		add("Center", split);

		tableScroll = new JScrollPane();
		tableScroll.setBorder(BorderFactory.createEmptyBorder());
		split.setTopComponent(tableScroll);

		descriptionPanel = new JEditorPane("text/html", "<html>");
		descriptionPanel.setBorder(BorderFactory.createEmptyBorder());
		descriptionPanel.setEditable(false);
		descriptionPanel.setBackground(UIManager.getColor("Panel.background"));
		descriptionPanel.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		UIUtils.htmlize(descriptionPanel);

		selectionListener = new SelectionListener();

		descriptionScrollPane = new JScrollPane(descriptionPanel);
		descriptionScrollPane.setBorder(UIUtils.addMargin(BorderFactory.createLineBorder(UIManager.getColor("controlDkShadow"))));
		descriptionScrollPane.getViewport().setBackground(descriptionPanel.getBackground());
		descriptionScrollPane.setMinimumSize(new Dimension(50, 50));
		split.setBottomComponent(descriptionScrollPane);

		// by default description is not visible, toolbar is visible.
		setDescriptionVisible(false);
	}

	//--------------------------------------------------------------------------
}