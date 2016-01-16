/****************************************/
/* ZoneArrayElementSubProperty.java		*/
/* Created on: 09-Jun-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties;

import eu.cherrytree.zaria.serialization.LinkArray;
import java.lang.reflect.Array;
import java.util.UUID;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneArrayElementSubProperty extends ZoneProperty
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private ZoneProperty parent;
	private int index;
	
	//--------------------------------------------------------------------------

	public ZoneArrayElementSubProperty(String name, int index, Class type, ZoneProperty parent, boolean editable)
	{
		super(name + "[" + index + "]", name + "[" + index + "]", type, parent.description, parent.ownerClass, parent.documentType, parent.linkClass, parent.resourceType, parent.scriptLink, editable, parent.minMax);
		
		this.index = index;
		this.parent = parent;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void readFromObject(Object object)
	{
		throw new UnsupportedOperationException("Sub Properties should not read directly from the object.");
	}
			
	//--------------------------------------------------------------------------
	
	@Override
	public void writeToObject(Object object)
	{
		Object array = parent.getArray(object);
	
		if(LinkArray.class.isAssignableFrom(array.getClass()))
		{
			LinkArray linkarray = (LinkArray) array;
			linkarray.getUUIDs()[index] = (UUID) getValue();			
		}
		else
		{				
			Array.set(array, index, getValue());
		}
	}
	
	//--------------------------------------------------------------------------
		
	@Override
	protected Object getArray(Object object)
	{
		Object parentarray = parent.getArray(object);	
		Object array = Array.get(parentarray, index);
		
		assert array.getClass().isArray() : "Object " + array + " is not an array!";
		
		return array;
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected Object getObject(Object object)
	{
		Object array = parent.getArray(object);
		Object obj = Array.get(array, index);
		
		assert isObject(obj) : "Object " + obj + " is not a valid object!";
	
		return obj;
	}
	
	//--------------------------------------------------------------------------
}
