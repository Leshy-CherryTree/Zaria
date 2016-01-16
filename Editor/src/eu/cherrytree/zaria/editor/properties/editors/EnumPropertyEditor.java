/****************************************/
/* EnumPropertyEditor.java              */
/* Created on: 21-Jul-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JComboBox;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class EnumPropertyEditor extends AbstractPropertyEditor implements ActionListener
{
	//--------------------------------------------------------------------------
	
	private Enum enumValue;
	
	private JComboBox combo;
						
	//--------------------------------------------------------------------------

	public EnumPropertyEditor(Class<Enum> enumClass) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method values = enumClass.getDeclaredMethod("values");
		
		Object array = values.invoke(null);
		
		Object[] objects = new Object[Array.getLength(array)];
		
		for(int i = 0 ; i < objects.length ; i++)
			objects[i] = Array.get(array, i);
		
		combo = new JComboBox(objects)
		{
			@Override
			public void setSelectedItem(Object anObject)
			{
				enumValue = (Enum) anObject;	
				super.setSelectedItem(anObject);
			}			
		};		
		
		combo.addActionListener(this);
		combo.setOpaque(true);
	}
	
	//--------------------------------------------------------------------------
		
	@Override
	public void setValue(Object value)
	{		
		enumValue = (Enum) value;
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getValue()
	{
		return enumValue;
	}

	//--------------------------------------------------------------------------

	@Override
	public Component getCustomEditor()
	{
		return combo;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean supportsCustomEditor()
	{
		return false;
	}	
	//--------------------------------------------------------------------------


	@Override
	public void actionPerformed(ActionEvent ae)
	{
		Enum item = (Enum) combo.getSelectedItem();		
		firePropertyChange(enumValue, item);				
		enumValue = item;	
	}
	
	//--------------------------------------------------------------------------
}
