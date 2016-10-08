/****************************************/
/* PropertyTools.java					*/
/* Created on: 17-Oct-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.editor.document.ZoneMetadata;
import eu.cherrytree.zaria.editor.document.annotations.VisibleForClass;
import eu.cherrytree.zaria.game.GameObject;
import eu.cherrytree.zaria.game.GameObjectDefinition;
import eu.cherrytree.zaria.serialization.Link;
import eu.cherrytree.zaria.serialization.LinkArray;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.annotations.CustomResource;
import eu.cherrytree.zaria.serialization.annotations.FieldDescription;
import eu.cherrytree.zaria.serialization.annotations.GreaterThan;
import eu.cherrytree.zaria.serialization.annotations.LessThan;
import eu.cherrytree.zaria.serialization.annotations.MaxFloat;
import eu.cherrytree.zaria.serialization.annotations.MinFloat;
import eu.cherrytree.zaria.serialization.annotations.WeakLink;
import eu.cherrytree.zaria.serialization.annotations.Resource;
import eu.cherrytree.zaria.serialization.annotations.ScriptLink;
import eu.cherrytree.zaria.serialization.annotations.Uneditable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import eu.cherrytree.zaria.serialization.annotations.Max;
import eu.cherrytree.zaria.serialization.annotations.Min;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class PropertyTools
{
	//--------------------------------------------------------------------------
	
	private static void getAllLinkFieldsRecursive(Class<? extends ZariaObjectDefinition> zoneClass, ZariaObjectDefinition definition, ArrayList<UUID> uuids) throws IllegalArgumentException, IllegalAccessException
	{
		for (Field field : zoneClass.getDeclaredFields())
		{
			field.setAccessible(true);
			
			if (field.getType().isAssignableFrom(Link.class))
			{
				UUID uuid = ((Link)field.get(definition)).getUUID();
				
				if (uuid != null)
					uuids.add(uuid);
			}
			else if (field.getType().isAssignableFrom(LinkArray.class))
			{
				LinkArray link_array = (LinkArray) field.get(definition);
				
				for (UUID uuid : link_array.getUUIDs())
				{
					if (uuid != null)
						uuids.add(uuid);				
				}
			}
			else if (field.getType().isAssignableFrom(UUID.class))
			{
				WeakLink weaklink = field.getAnnotation(WeakLink.class);
				
				if (weaklink != null)
				{
					UUID uuid = (UUID) field.get(definition);
					
					if (uuid != null)
						uuids.add(uuid);
				}
			}
		}
		
		Class superClass = zoneClass.getSuperclass();
		
		if (superClass != null && ZariaObjectDefinition.class.isAssignableFrom(superClass))
			getAllLinkFieldsRecursive(superClass, definition, uuids);
	}
	
	//--------------------------------------------------------------------------
	
	public static ArrayList<UUID> getAllLinks(ZariaObjectDefinition definition)
	{
		ArrayList<UUID> ret = new ArrayList<>();
		
		try
		{
			getAllLinkFieldsRecursive(definition.getClass(), definition, ret);
		}
		catch (IllegalArgumentException | IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		return ret;
	}
	
	//--------------------------------------------------------------------------
	
	private static void getPropertiesRecursive(Class<? extends ZariaObjectDefinition> zoneClass, ArrayList<ZoneProperty> properties, ZoneDocument document)
	{
		int index = 0;
		
		for (Field field : zoneClass.getDeclaredFields())
		{									
			if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
			{																
				FieldDescription description = field.getAnnotation(FieldDescription.class);
			
				if (description != null && !description.visible())
					continue;
				
				String name = field.getName();
				String displayName = name;
				String descstr = name;
				
				if (description != null)
					descstr = description.value();																
				
				Class link_class = null;
				
				WeakLink weakLink = field.getAnnotation(WeakLink.class);
				
				if (weakLink != null)
				{	
					link_class = weakLink.value();
					
					if (!weakLink.name().isEmpty())
						displayName = weakLink.name();
				}
				
				if (Link.class.isAssignableFrom(field.getType()) || LinkArray.class.isAssignableFrom(field.getType()))
					link_class = getSpecializationClass(field); 								
								
				ResourcePropertyInfo res_info = null;
				
				Resource resource = field.getAnnotation(Resource.class);
				if (resource != null)
					res_info = new ResourcePropertyInfo(resource.value());
				
				CustomResource custom_resource = field.getAnnotation(CustomResource.class);
				if (custom_resource != null)
					res_info =  new ResourcePropertyInfo(custom_resource.name(), custom_resource.extensions());
								 
				boolean editable = field.getAnnotation(Uneditable.class) == null;
				boolean script_link = field.getAnnotation(ScriptLink.class) != null;
				
				properties.add(index, new ZoneProperty(name, displayName, field.getType(), descstr, zoneClass, document.getDocumentType(), link_class, res_info, script_link, editable, getMinMax(field)));
				index++;
			}
		}
				
		Class superClass = zoneClass.getSuperclass();
		
		if (superClass != null && ZariaObjectDefinition.class.isAssignableFrom(superClass))
			getPropertiesRecursive(superClass, properties, document);
	}
	
	//--------------------------------------------------------------------------
	
	public static MinMaxInfo getMinMax(Field field)
	{		
		Min min = field.getAnnotation(Min.class);
		Max max = field.getAnnotation(Max.class);
		
		if (min != null && max != null)
			return new MinMaxInfo(Integer.class, min.value(), max.value(), false, false);
		
		MaxFloat max_val=  field.getAnnotation(MaxFloat.class);
		MinFloat min_val = field.getAnnotation(MinFloat.class);
		GreaterThan greater_val = field.getAnnotation(GreaterThan.class);
		LessThan less_val = field.getAnnotation(LessThan.class);
		
		if ((max_val != null || less_val != null) && (min_val != null || greater_val != null))
		{
			float f_min = greater_val != null ? greater_val.value() : min_val.value();
			float f_max = less_val != null ? less_val.value() : max_val.value();
			return new MinMaxInfo(Float.class, f_min, f_max, greater_val != null, less_val != null);
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public static Link createNewLink(UUID uuid)
	{		
		try
		{
			Link link = new Link();
			Field id_field = Link.class.getDeclaredField("uuid");
			id_field.setAccessible(true);
			id_field.set(link, uuid);
			
			return link;
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public static LinkArray createNewLinkArray(UUID [] uuids)
	{		
		try
		{
			LinkArray link_array = new LinkArray();
			Field id_field = LinkArray.class.getDeclaredField("uuids");
			id_field.setAccessible(true);
			id_field.set(link_array, uuids);
			
			return link_array;
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		return null;
	}	
	
	//--------------------------------------------------------------------------
	
	public static Class getSpecializationClass(Field field)
	{
		if (field.getGenericType() instanceof ParameterizedType)
		{
			ParameterizedType aType = (ParameterizedType) field.getGenericType();
			Type[] fieldArgTypes = aType.getActualTypeArguments();
			
			for (Type fieldArgType : fieldArgTypes)
				return (Class) fieldArgType;
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	private static void getMetadataProperties(Class<? extends ZariaObjectDefinition> zoneClass, ArrayList<ZoneProperty> properties, ZoneDocument document)
	{
		int index = 0;
		
		for (Field field : ZoneMetadata.class.getDeclaredFields())
		{	
			VisibleForClass visible = field.getAnnotation(VisibleForClass.class);
			
			if (visible != null && visible.value() != null && visible.value().isAssignableFrom(zoneClass))
			{
				String name = field.getName();
				String descstr = name;

				FieldDescription description = field.getAnnotation(FieldDescription.class);

				if (description != null)
					descstr = description.value();
				
				boolean editable = field.getAnnotation(Uneditable.class) == null;

				properties.add(index, new ZoneMetadataProperty(name, field.getType(), descstr, document.getDocumentType(), editable));
				index++;
			}
		}		
	}

	//--------------------------------------------------------------------------
	
	public static ZoneProperty[] getProperties(Class<? extends ZariaObjectDefinition> zoneClass, ZoneDocument document)
	{
		ArrayList<ZoneProperty> properties = new ArrayList<>();
		
		getMetadataProperties(zoneClass, properties, document);
		getPropertiesRecursive(zoneClass, properties, document);
										
		ZoneProperty[] ret = new ZoneProperty[properties.size()];
		properties.toArray(ret);		
		return ret;
	}
	
	//--------------------------------------------------------------------------
	
	public static Class<? extends GameObject> getGameObjectClass(Class<? extends GameObjectDefinition> definitionClass)
	{				
		if (definitionClass.getGenericSuperclass() instanceof ParameterizedType)
		{
			ParameterizedType type = (ParameterizedType) definitionClass.getGenericSuperclass();
			Type[] arg_types = type.getActualTypeArguments();
			
			for (Type fieldArgType : arg_types)
				return (Class) fieldArgType;
		}	
	
		return null;
	}
	
	//--------------------------------------------------------------------------		
}
