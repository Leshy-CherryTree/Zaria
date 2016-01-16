/****************************************/
/* ReflectionTools.java					*/
/* Created on: 17-Mar-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.properties.PropertyTools;
import eu.cherrytree.zaria.serialization.Link;
import eu.cherrytree.zaria.serialization.LinkArray;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.logging.Level;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ReflectionTools
{
	//--------------------------------------------------------------------------
	
	public static <Def extends ZariaObjectDefinition> Def createNewDefinition(Class<Def> cls, String id)
	{
		try
		{
			Def obj = cls.getConstructor().newInstance();
			setDefinitionFieldValue(obj, "id", id);
			setDefinitionFieldValue(obj, "uuid", UUID.randomUUID());
			
			return obj;
		}
		catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
			
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public static <Def extends ZariaObjectDefinition> Def createNewDefinition(Class<Def> cls)
	{
		return createNewDefinition(cls, cls.getSimpleName());
	}
	
	//--------------------------------------------------------------------------
	
	public static Field getField(Class cls, String name)
	{
		for(Field field : cls.getDeclaredFields())
		{
			if(field.getName().equals(name))
			{
				field.setAccessible(true);
				return field;
			}				
		}
		
		if(cls.getSuperclass() != null)
			return getField(cls.getSuperclass(), name);
		
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public static void setDefinitionFieldValue(ZariaObjectDefinition definition, String fieldName, Object value) throws SecurityException, IllegalArgumentException, NoSuchFieldException
	{
		try
		{
			Field field = getField(definition.getClass(), fieldName);
			field.setAccessible(true);
			
			if(Link.class.isAssignableFrom(field.getType()))
			{
				assert UUID.class.isAssignableFrom(value.getClass());
				
				field.set(definition, PropertyTools.createNewLink((UUID)value));
			}
			else if(LinkArray.class.isAssignableFrom(field.getType()))
			{
				assert UUID.class.isAssignableFrom(value.getClass()) || UUID[].class.isAssignableFrom(value.getClass());
				
				if(UUID.class.isAssignableFrom(value.getClass()))
				{
					field.set(definition, PropertyTools.createNewLinkArray(new UUID[]{(UUID)value}));
				}
				else if(UUID[].class.isAssignableFrom(value.getClass()))
				{
					field.set(definition, PropertyTools.createNewLinkArray((UUID[])value));
				}
			}
			else
				field.set(definition, value);
		}
		catch (IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}
	
	//--------------------------------------------------------------------------
}
