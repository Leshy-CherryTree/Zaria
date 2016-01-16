/****************************************/
/* AbstractPropertyEditor.java          */
/* Created on: 15-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class AbstractPropertyEditor implements PropertyEditor
{
	//--------------------------------------------------------------------------
	
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	//--------------------------------------------------------------------------

	@Override
	public boolean isPaintable()
	{
		return false;
	}

	//--------------------------------------------------------------------------

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.addPropertyChangeListener(listener);
	}

	//--------------------------------------------------------------------------

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		listeners.removePropertyChangeListener(listener);
	}

	//--------------------------------------------------------------------------

	protected void firePropertyChange(Object oldValue, Object newValue)
	{
		listeners.firePropertyChange("value", oldValue, newValue);
	}

	//--------------------------------------------------------------------------

	@Override
	public String getAsText()
	{
		return null;
	}

	//--------------------------------------------------------------------------

	@Override
	public String getJavaInitializationString()
	{
		return null;
	}

	//--------------------------------------------------------------------------

	@Override
	public String[] getTags()
	{
		return null;
	}

	//--------------------------------------------------------------------------

	@Override
	public void setAsText(String text) throws IllegalArgumentException
	{
		// Intentionally empty
	}

	//--------------------------------------------------------------------------

	@Override
	public void paintValue(Graphics gfx, Rectangle box)
	{
		// Intentionally empty
	}

	//--------------------------------------------------------------------------
}
