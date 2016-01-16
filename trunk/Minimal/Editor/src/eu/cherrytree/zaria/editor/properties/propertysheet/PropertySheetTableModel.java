/********************************************/
/* PropertySheetTableModel.java				*/
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

public class PropertySheetTableModel extends AbstractTableModel implements PropertyChangeListener
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	public static final int NAME_COLUMN = 0;
	public static final int VALUE_COLUMN = 1;
	public static final int NUM_COLUMNS = 2;
	
	//--------------------------------------------------------------------------
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private List model;
	private List publishedModel;
	private List properties;
	
	private boolean restoreToggleStates;
	
	private Map toggleStates;

	//--------------------------------------------------------------------------

	public PropertySheetTableModel()
	{
		model = new ArrayList();
		publishedModel = new ArrayList();
		properties = new ArrayList();
		restoreToggleStates = false;
		toggleStates = new HashMap();
	}

	//--------------------------------------------------------------------------

	public void setProperties(Property[] newProperties)
	{
		// unregister the listeners from previous properties
		for(Iterator iter = properties.iterator() ; iter.hasNext() ;)
		{
			Property prop = (Property) iter.next();
			prop.removePropertyChangeListener(this);
		}

		// replace the current properties
		properties.clear();
		properties.addAll(Arrays.asList(newProperties));

		// add listeners
		for(Iterator iter = properties.iterator() ; iter.hasNext() ;)
		{
			Property prop = (Property) iter.next();
			prop.addPropertyChangeListener(this);
		}

		buildModel();
	}

	//--------------------------------------------------------------------------

	public Property[] getProperties()
	{
		return (Property[]) properties.toArray(new Property[properties.size()]);
	}

	//--------------------------------------------------------------------------

	public void addProperty(Property property)
	{
		properties.add(property);
		property.addPropertyChangeListener(this);
		buildModel();
	}

	//--------------------------------------------------------------------------

	public void addProperty(int index, Property property)
	{
		properties.add(index, property);
		property.addPropertyChangeListener(this);
		buildModel();
	}

	//--------------------------------------------------------------------------

	public void removeProperty(Property property)
	{
		properties.remove(property);
		property.removePropertyChangeListener(this);
		buildModel();
	}

	//--------------------------------------------------------------------------

	public int getPropertyCount()
	{
		return properties.size();
	}

	//--------------------------------------------------------------------------

	public Iterator propertyIterator()
	{
		return properties.iterator();
	}

	//--------------------------------------------------------------------------

	@Override
	public Class getColumnClass(int columnIndex)
	{
		return super.getColumnClass(columnIndex);
	}

	//--------------------------------------------------------------------------

	@Override
	public int getColumnCount()
	{
		return NUM_COLUMNS;
	}

	//--------------------------------------------------------------------------

	@Override
	public int getRowCount()
	{
		return publishedModel.size();
	}

	//--------------------------------------------------------------------------

	public Object getObject(int rowIndex)
	{
		return getPropertySheetElement(rowIndex);
	}

	//--------------------------------------------------------------------------

	/**
	 * Get the current property sheet element, of type {@link PropertySheetElement}, at the
	 * specified row.
	 */
	public PropertySheetElement getPropertySheetElement(int rowIndex)
	{
		return (PropertySheetElement) publishedModel.get(rowIndex);
	}

	//--------------------------------------------------------------------------

	/**
	 * Set whether or not this model will restore the toggle states when new
	 * properties are applied.
	 */
	public void setRestoreToggleStates(boolean value)
	{
		restoreToggleStates = value;
		if(!restoreToggleStates)
		{
			toggleStates.clear();
		}
	}

	//--------------------------------------------------------------------------

	/**
	 * Get whether this model is restoring toggle states
	 */
	public boolean isRestoreToggleStates()
	{
		return restoreToggleStates;
	}

	//--------------------------------------------------------------------------

	/**
	 * @return the category view toggle states.
	 */
	public Map getToggleStates()
	{
		// Call visibilityChanged to populate the toggleStates map
		visibilityChanged(restoreToggleStates);
		return toggleStates;
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
		// We are providing a toggleStates map - so by definition we must want to
		// store the toggle states
		setRestoreToggleStates(true);
		this.toggleStates.clear();
		this.toggleStates.putAll(toggleStates);
	}

	//--------------------------------------------------------------------------

	/**
	 * Retrieve the value at the specified row and column location. When the row
	 * contains a category or the column is {@link #NAME_COLUMN}, an
	 * {@link PropertySheetElement} object will be returned. If the row is a property and the
	 * column is {@link #VALUE_COLUMN}, the value of the property will be
	 * returned.
	 *
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object result = null;
		PropertySheetElement item = getPropertySheetElement(rowIndex);

		if(item.isProperty())
		{
			switch(columnIndex)
			{
				case NAME_COLUMN:
					result = item;
					break;

				case VALUE_COLUMN:
					result = item.getProperty().getValue();
					break;
			}
		}
		else
		{
			result = item;
		}
		
		return result;
	}

	//--------------------------------------------------------------------------

	/**
	 * Sets the value at the specified row and column. This will have no effect
	 * unless the row is a property and the column is {@link #VALUE_COLUMN}.
	 *
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex)
	{
		PropertySheetElement item = getPropertySheetElement(rowIndex);
		
		if(item.isProperty())
		{
			if(columnIndex == VALUE_COLUMN)
				item.getProperty().setValue(value);
		}
	}

	//--------------------------------------------------------------------------

	/**
	 * Add a {@link PropertyChangeListener} to the current model.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.addPropertyChangeListener(listener);
	}

	//--------------------------------------------------------------------------

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.removePropertyChangeListener(listener);
	}

	//--------------------------------------------------------------------------

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		// forward the event to registered listeners
		listeners.firePropertyChange(evt);
	}

	//--------------------------------------------------------------------------

	protected void visibilityChanged(final boolean restoreOldStates)
	{
		// Store the old visibility states
		if(restoreOldStates)
		{
			for(Iterator iter = publishedModel.iterator() ; iter.hasNext() ;)
			{
				final PropertySheetElement item = (PropertySheetElement) iter.next();
				toggleStates.put(item.getKey(), item.isVisible() ? Boolean.TRUE : Boolean.FALSE);
			}
		}
		
		publishedModel.clear();
		
		for(Iterator iter = model.iterator() ; iter.hasNext() ;)
		{
			PropertySheetElement item = (PropertySheetElement) iter.next();
			PropertySheetElement parent = item.getParent();
			
			if(restoreOldStates)
			{
				Boolean oldState = (Boolean) toggleStates.get(item.getKey());
				
				if(oldState != null)
					item.setVisible(oldState.booleanValue());
				
				if(parent != null)
				{
					oldState = (Boolean) toggleStates.get(parent.getKey());
					
					if(oldState != null)
						parent.setVisible(oldState.booleanValue());
				}
			}
			
			if(parent == null || parent.isVisible())
				publishedModel.add(item);
		}
	}

	//--------------------------------------------------------------------------

	private void buildModel()
	{
		model.clear();

		if(properties != null && properties.size() > 0)
		{
			List categories = getPropertyCategories(properties);

			for(Iterator iter = categories.iterator() ; iter.hasNext() ;)
			{
				String category = (String) iter.next();
				PropertySheetElement categoryItem = new PropertySheetElement(category, null, this);
				model.add(categoryItem);
				addPropertiesToModel(getPropertiesForCategory(properties, category), categoryItem);
			}
		}

		visibilityChanged(restoreToggleStates);
		fireTableDataChanged();
	}

	//--------------------------------------------------------------------------

	protected List getPropertyCategories(List localProperties)
	{
		List categories = new ArrayList();
		for(Iterator iter = localProperties.iterator() ; iter.hasNext() ;)
		{
			Property property = (Property) iter.next();
			if(!categories.contains(property.getCategory()))
				categories.add(property.getCategory());
		}
		return categories;
	}

	//--------------------------------------------------------------------------

	/**
	 * Add the specified properties to the model using the specified parent.
	 *
	 * @param localProperties the properties to add to the end of the model
	 * @param parent the {@link PropertySheetElement} parent of these properties, null if none
	 */
	private void addPropertiesToModel(List localProperties, PropertySheetElement parent)
	{
		for(Iterator iter = localProperties.iterator() ; iter.hasNext() ;)
		{
			Property property = (Property) iter.next();
			PropertySheetElement propertyItem = new PropertySheetElement(property, parent, this);
			model.add(propertyItem);

			// add any sub-properties
			Property[] subProperties = property.getSubProperties();
			if(subProperties != null && subProperties.length > 0)
				addPropertiesToModel(Arrays.asList(subProperties), propertyItem);
		}
	}

	//--------------------------------------------------------------------------

	/**
	 * Convenience method to get all the properties of one category.
	 */
	private List getPropertiesForCategory(List localProperties, String category)
	{
		List categoryProperties = new ArrayList();
		
		for(Iterator iter = localProperties.iterator() ; iter.hasNext() ;)
		{
			Property property = (Property) iter.next();
			if((category == property.getCategory()) || (category != null && category.equals(property.getCategory())))
				categoryProperties.add(property);
		}
		
		return categoryProperties;
	}

	//--------------------------------------------------------------------------
}
