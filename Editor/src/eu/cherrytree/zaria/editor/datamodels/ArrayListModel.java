/****************************************/
/* ArrayListModel.java					*/
/* Created on: 24-May-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.AbstractListModel;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ArrayListModel extends AbstractListModel
{
	//--------------------------------------------------------------------------
	
	private ArrayList<Object> array = new ArrayList<>();
	private Class arrayClass;
	
	//--------------------------------------------------------------------------

	public ArrayListModel(Object object)
	{
		assert object.getClass().isArray();
		
		int length = Array.getLength(object);
		
		for(int i = 0 ; i < length ; i++)
			array.add(Array.get(object, i));
		
		arrayClass = object.getClass().getComponentType();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public int getSize()
	{
		return array.size();
	}
	
	//--------------------------------------------------------------------------
	
	public void moveUp(int index)
	{
		if(index > 0 && index < array.size())
		{
			Object obj = array.remove(index);
			array.add(index-1, obj);
			
			fireContentsChanged(this, 0, getSize());
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void moveDown(int index)
	{
		if(index >= 0 && index < array.size()-1)
		{
			Object obj = array.remove(index);
			array.add(index+1, obj);
			
			fireContentsChanged(this, 0, getSize());
		}
	}
	//--------------------------------------------------------------------------
	
	private Object createNewInstance() throws InstantiationException, IllegalAccessException
	{
		if(arrayClass == UUID.class)
		{
			return null;
		}
		else if(arrayClass == int.class || arrayClass == Integer.class || arrayClass == short.class || arrayClass == Short.class ||
				arrayClass == long.class || arrayClass == Long.class || arrayClass == byte.class || arrayClass == Byte.class)
		{
			return 0;
		}
		else if(arrayClass == int.class || arrayClass == Integer.class || arrayClass == float.class || arrayClass == Float.class)
		{
			return 0.0f;
		}
		else if(arrayClass == char.class || arrayClass == Character.class)
		{
			return '0';
		}
		else if(arrayClass == boolean.class || arrayClass == Boolean.class)
		{
			return false;
		}
		else
		{
			return arrayClass.newInstance();
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void add(int index) throws InstantiationException, IllegalAccessException
	{
		if(index >= 0 && index < array.size())
			array.add(index, createNewInstance());
		else
			array.add(createNewInstance());
		
		fireContentsChanged(this, 0, getSize());
	}
	
	//--------------------------------------------------------------------------
	
	public void remove(int index)
	{
		if(index >= 0 && index < array.size())
		{
			array.remove(index);
			fireContentsChanged(this, 0, getSize());
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Object getElementAt(int index)
	{
		return array.get(index);
	}

	//--------------------------------------------------------------------------
	
	public Object getArray()
	{
		Object array_obj = Array.newInstance(arrayClass, array.size());
		
		for(int i = 0 ; i < array.size() ; i++)
			Array.set(array_obj, i, array.get(i));
		
		return array_obj;
	}
	
	//--------------------------------------------------------------------------
}
