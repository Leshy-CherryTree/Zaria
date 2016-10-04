/********************************************/
/* Property.java								*/
/*											*/
/* Initial version released by L2FProd.com	*/
/* under the Apache License, Version 2.0		*/
/*											*/
/* Adapted, modified and released			*/
/* by Cherry Tree Studio under EUPL v1.1		*/
/*											*/
/* Copyright Cherry Tree Studio 2013			*/
/********************************************/

package eu.cherrytree.zaria.editor.properties;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.io.Serializable;


public abstract class Property implements Serializable, Cloneable
{
	//--------------------------------------------------------------------------
	
	private Object value;
	
	// PropertyChangeListeners are not serialized.
	private transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	//--------------------------------------------------------------------------

	public Object getValue()
	{
		return value;
	}

	//--------------------------------------------------------------------------

	@Override
	public Object clone()
	{
		try
		{
			Property clone = (Property) super.clone();
			return clone;
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException(e);
		}
	}

	//--------------------------------------------------------------------------

	public void setValue(Object value)
	{
		Object oldValue = this.value;
		this.value = value;
		
		if(value != oldValue && (value == null || !value.equals(oldValue)))
			firePropertyChange(oldValue, getValue());
	}

	//--------------------------------------------------------------------------

	protected void initializeValue(Object value)
	{
		this.value = value;
	}

	//--------------------------------------------------------------------------

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.addPropertyChangeListener(listener);
		Property[] subProperties = getSubProperties();
		
		if(subProperties != null)
		{
			for(int i = 0 ; i < subProperties.length ; ++i)
				subProperties[i].addPropertyChangeListener(listener);
		}
	}

	//--------------------------------------------------------------------------

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.removePropertyChangeListener(listener);
		Property[] subProperties = getSubProperties();
		
		if(subProperties != null)
		{
			for(int i = 0 ; i < subProperties.length ; ++i)
				subProperties[i].removePropertyChangeListener(listener);
		}
	}

	//--------------------------------------------------------------------------

	protected void firePropertyChange(Object oldValue, Object newValue)
	{
		listeners.firePropertyChange("value", oldValue, newValue);
	}

	//--------------------------------------------------------------------------

	public Property getParentProperty()
	{
		return null;
	}

	//--------------------------------------------------------------------------

	public Property[] getSubProperties()
	{
		return null;
	}

	//--------------------------------------------------------------------------

	public abstract String getName();
	public abstract String getDisplayName();
	public abstract String getShortDescription();
	public abstract Class getType();
	public abstract boolean isEditable();
	public abstract String getCategory();
	public abstract void readFromObject(Object object);
	public abstract void writeToObject(Object object);

	//--------------------------------------------------------------------------
}
