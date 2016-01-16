/****************************************/
/* ZoneObjectFieldSubProperty.java		*/
/* Created on: 09-Jun-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.serialization.ResourceType;

import java.lang.reflect.Field;
import java.util.logging.Level;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneObjectFieldSubProperty extends ZoneProperty
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private ZoneProperty parent;
	
	//--------------------------------------------------------------------------


	public ZoneObjectFieldSubProperty(String name, Class type, String description, ResourceType resourceType, boolean scriptLink, ZoneProperty parent, boolean editable, MinMaxInfo minMaxInfo)
	{
		super(name, name, type, description, parent.ownerClass, parent.documentType, null, resourceType, scriptLink, editable, minMaxInfo);
		this.parent = parent;
	}
	
	//--------------------------------------------------------------------------	
	
	@Override
	public Property getParentProperty()
	{
		return parent;
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
		Object obj = parent.getObject(object);	
		
		try
		{
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);			
			field.set(obj, getValue());			
		}
		catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, "Object: " + (obj == null ? "null" : obj.getClass().getSimpleName()) + " name: " + name + " parent: " + parent, ex);
		}
	}
	
	//--------------------------------------------------------------------------
		
	@Override
	protected Object getArray(Object object)
	{
		Object obj = parent.getObject(object);	
		Object array = null;
		
		try
		{
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);	

			array = field.get(obj);
		}
		catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			throw new RuntimeException("Object: " + object.getClass().getCanonicalName(), ex);
		}
		
		assert array.getClass().isArray() : "Object " + array + " is not an array!";
		
		return array;
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected Object getObject(Object object)
	{
		Object parentobject = parent.getObject(object);	
		Object obj = null;
		
		try
		{
			Field field = parentobject.getClass().getDeclaredField(name);
			field.setAccessible(true);	

			obj = field.get(parentobject);
		}
		catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			throw new RuntimeException("Parent: " + parentobject.getClass().getCanonicalName() + " Object: " + object.getClass().getCanonicalName(), ex);
		}
		
		assert isObject(obj) : "Object " + obj + " is not a valid object!";
		
		return obj;
	}
	
	//--------------------------------------------------------------------------
}
