/****************************************/
/* ZoneProperty.java					*/
/* Created on: 20-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.properties;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.serialization.Link;
import eu.cherrytree.zaria.serialization.LinkArray;

import eu.cherrytree.zaria.serialization.ResourceType;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.annotations.CustomResource;
import eu.cherrytree.zaria.serialization.annotations.FieldDescription;
import eu.cherrytree.zaria.serialization.annotations.Resource;
import eu.cherrytree.zaria.serialization.annotations.ScriptLink;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ZoneProperty extends Property
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	protected String name;
	protected String displayName;
	
	protected String description;
	protected Class ownerClass;
	
	protected Class type;	
	
	protected ZoneProperty[] subProperties;
	
	// TODO This should be changed into an enum;
	protected Class linkClass;
	protected ResourcePropertyInfo resourceInfo;
	protected boolean scriptLink;
	
	protected boolean editable;
	
	protected MinMaxInfo minMax;
	
	protected ZoneDocument.DocumentType documentType;
	
	//--------------------------------------------------------------------------

	public ZoneProperty(String name, String displayName, Class type, String description, Class ownerClass, ZoneDocument.DocumentType documentType, Class linkClass, ResourcePropertyInfo resourceInfo, boolean scriptLink, boolean editable, MinMaxInfo minMax)
	{
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.description = description;
		this.ownerClass = ownerClass;
		this.subProperties = null;
		this.linkClass = linkClass;
		this.resourceInfo = resourceInfo;
		this.editable = editable;
		this.minMax = minMax;
		this.documentType = documentType;
		this.scriptLink = scriptLink;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public String getName()
	{
		return name;
	}

	//--------------------------------------------------------------------------

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	//--------------------------------------------------------------------------

	@Override
	public Class getType()
	{
		return type;
	}
			
	//--------------------------------------------------------------------------

	@Override
	public String getShortDescription()
	{
		return description;
	}

	//--------------------------------------------------------------------------
	
	public boolean isLink()
	{
		return linkClass != null;
	}
	
	//--------------------------------------------------------------------------
	
	public String getLinkClassName()
	{
		return linkClass.getSimpleName();
	}
	
	//--------------------------------------------------------------------------
	
	public Class getLinkClass()
	{
		return linkClass;
	}
			
	//--------------------------------------------------------------------------
	
	public boolean isScriptLink()
	{
		return scriptLink;
	}
	
	//--------------------------------------------------------------------------
		
	@Override
	public boolean isEditable()
	{
		return editable;
	}

	//--------------------------------------------------------------------------

	@Override
	public String getCategory()
	{
		return ownerClass.getSimpleName();
	}
	
	//--------------------------------------------------------------------------

	public ResourcePropertyInfo getResourceInfo()
	{
		return resourceInfo;
	}
			
	//--------------------------------------------------------------------------
	
	public MinMaxInfo getMinMax()
	{
		return minMax;
	}
	
	//--------------------------------------------------------------------------
	
	public ZoneDocument.DocumentType getDocumentType()
	{
		return documentType;
	}
			
	//--------------------------------------------------------------------------

	@Override
	public void readFromObject(Object object)
	{				
		try
		{
			Field field = ownerClass.getDeclaredField(name);
			field.setAccessible(true);
			
			if(Link.class.isAssignableFrom(field.getType()))
			{
				Link link = (Link) field.get(object);
				
				if(link == null)
					initializeValue(null);
				else
					initializeValue(link.getUUID());
			}			
			else if(LinkArray.class.isAssignableFrom(field.getType()))
			{
				LinkArray link_array = (LinkArray) field.get(object);
				
				if(link_array == null)
					initializeValue(new UUID[0]);
				else
					initializeValue(link_array.getUUIDs());
			}			
			else
				initializeValue(field.get(object));
		}
		catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}

	//--------------------------------------------------------------------------

	@Override
	public void writeToObject(Object object)
	{
		try
		{
			Field field = ownerClass.getDeclaredField(name);
			field.setAccessible(true);	
			
			if(Link.class.isAssignableFrom(field.getType()))
			{
				field.set(object, PropertyTools.createNewLink((UUID) getValue()));
			}
			else if(LinkArray.class.isAssignableFrom(field.getType()))
			{
				field.set(object, PropertyTools.createNewLinkArray((UUID[]) getValue()));
			}
			else				
				field.set(object, getValue());			
		}
		catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, "Type: " + getClass().getCanonicalName() + " property: " + getName() + " object: " + object, ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	protected Object getArray(Object object)
	{
		try
		{
			Field field = ownerClass.getDeclaredField(name);
			field.setAccessible(true);	
			
			Object array = field.get(object);
			
			assert array.getClass().isArray() || LinkArray.class.isAssignableFrom(array.getClass()) : "Object " + array + " is not an array!";
			
			return array;
		}
		catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	protected Object getObject(Object object)
	{
		try
		{
			Field field = ownerClass.getDeclaredField(name);
			field.setAccessible(true);	
			
			Object obj = field.get(object);
			
			assert isObject(obj) : "Object " + obj + " is not a valid object!";
			
			return obj;
		}
		catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	//--------------------------------------------------------------------------
	
	protected boolean isObject(Object value)
	{								
		return	!value.getClass().isPrimitive() && 
				!value.getClass().isArray() &&
				!value.getClass().isEnum() &&
				!Boolean.class.isAssignableFrom(value.getClass()) &&
				!Byte.class.isAssignableFrom(value.getClass()) &&
				!Short.class.isAssignableFrom(value.getClass()) &&
				!Integer.class.isAssignableFrom(value.getClass()) &&
				!Long.class.isAssignableFrom(value.getClass()) &&
				!Float.class.isAssignableFrom(value.getClass()) &&
				!Double.class.isAssignableFrom(value.getClass()) &&
				!Character.class.isAssignableFrom(value.getClass()) &&
				!String.class.isAssignableFrom(value.getClass()) &&
				!UUID.class.isAssignableFrom(value.getClass()) &&
				!ZariaObjectDefinition.class.isAssignableFrom(value.getClass());
	} 
	
	//--------------------------------------------------------------------------
	
	private void setArraySubProperties(Object value)
	{
		int length = Array.getLength(value);
		
		subProperties = new ZoneProperty[length];
		
		for(int i = 0 ; i < length ; i++)
		{
			Object arrayElement = Array.get(value, i);
			
			Class cls;
			
			if(arrayElement == null)
				cls = value.getClass().getComponentType();
			else
				cls = arrayElement.getClass();
			
			subProperties[i] = new ZoneArrayElementSubProperty(name, i, cls, this, editable);
			subProperties[i].initializeValue(arrayElement);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void initializeObjectSubProperties(Object value) throws IllegalArgumentException, IllegalAccessException
	{
		ArrayList<Field> fields = new ArrayList<>();
		
		getAllFields(value.getClass(), fields);
		
		subProperties = new ZoneProperty[fields.size()];

		// TODO There is a lot of code duplication here from the PropertyTools. Merge this.
		
		for(int  i = 0 ; i < fields.size() ; i++)
		{
			Field field = fields.get(i);
			field.setAccessible(true);
			
			String descstr = field.getName();
				
			FieldDescription descr_anno = field.getAnnotation(FieldDescription.class);
				
			if(descr_anno != null)
				descstr = descr_anno.value();
			
			ResourcePropertyInfo res_info = null;

			Resource resource = field.getAnnotation(Resource.class);
			if (resource != null)
				res_info = new ResourcePropertyInfo(resource.value());

			CustomResource custom_resource = field.getAnnotation(CustomResource.class);
			if (custom_resource != null)
				res_info =  new ResourcePropertyInfo(custom_resource.name(), custom_resource.extensions());
			
			boolean script_link = field.getAnnotation(ScriptLink.class) != null;
			
			subProperties[i] = new ZoneObjectFieldSubProperty(field.getName(), field.getType(), descstr, res_info, script_link, this, editable, PropertyTools.getMinMax(field));			
			subProperties[i].initializeValue(field.get(value));
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void setObjectSubProperties(Object value) throws IllegalArgumentException, IllegalAccessException
	{
		ArrayList<Field> fields = new ArrayList<>();
		
		getAllFields(value.getClass(), fields);
		
		assert subProperties.length == fields.size();
		
		for(int  i = 0 ; i < fields.size() ; i++)
		{
			Field field = fields.get(i);
			field.setAccessible(true);
			
			subProperties[i].setValue(field.get(value));
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void getAllFields(Class cls, ArrayList<Field> fields)
	{
		int index = 0;
		
		for(Field field : cls.getDeclaredFields())
		{						
			if(!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
			{
				fields.add(index, field);
				index++;
			}
		}
				
		Class superClass = cls.getSuperclass();
		
		if(superClass != null)
			getAllFields(superClass, fields);
	}
	
	//--------------------------------------------------------------------------
	
	private boolean verifyType(Object obj)
	{
		if(Link.class.isAssignableFrom(type))
		{
			if(obj == null)
				return true;
			else
				return UUID.class.isAssignableFrom(obj.getClass());
		}
		else if(UUID.class.isAssignableFrom(type))
		{
			if(obj == null)
				return true;
		}
		
		Class cls = obj.getClass();
		
		if(type.isPrimitive() || cls.isPrimitive())
		{				
			 return	((type == Boolean.class || type == boolean.class) && (cls == Boolean.class || cls == boolean.class)) || 
					((type == Byte.class || type == byte.class) && (cls == Byte.class || cls == byte.class)) || 
					((type == Short.class || type == short.class) && (cls == Short.class || cls == short.class)) || 
					((type == Integer.class || type == int.class) && (cls == Integer.class || cls == int.class)) || 
					((type == Long.class || type == long.class) && (cls == Long.class || cls == long.class)) || 
					((type == Float.class || type == float.class) && (cls == Float.class || cls == float.class)) || 
					((type == Double.class || type == double.class) && (cls == Double.class || cls == double.class)) || 
					((type == Character.class || type == char.class) && (cls == Character.class || cls == char.class));					
		}
		else if(LinkArray.class.isAssignableFrom(type))
		{
			return UUID[].class.isAssignableFrom(cls);
		}
		else
		{
			return type == cls;
		}
	}
	
	//--------------------------------------------------------------------------
	
	private Object verifyInitialized(Object value)
	{
		try
		{
			if(value == null)
			{
				if(type == Link.class || type == LinkArray.class)
					value = null;
				else if(type.isArray())
					value = Array.newInstance(type.getComponentType(), 0);
				else
					value = type != UUID.class ? type.newInstance() : null;
			}
		}
		catch(InstantiationException | IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		return value;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void setValue(Object value)
	{				
		value = verifyInitialized(value);
		
		assert verifyType(value) : "Types do not match! Was expecting " + type + " got " + (value != null ? value.getClass() : "[null]");

		if(value != null)
		{
			if(value.getClass().isArray())
			{
				setArraySubProperties(value);
			}
			else if(isObject(value))
			{
				try
				{
					setObjectSubProperties(value);
				}
				catch(IllegalArgumentException | IllegalAccessException ex)
				{
					DebugConsole.logger.log(Level.SEVERE, null, ex);
				}
			}
		}
					
		super.setValue(value);
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected void initializeValue(Object value)
	{		
		value = verifyInitialized(value);
		
		assert verifyType(value) : "Types do not match! Was expecting " + type + " got " + (value != null ? value.getClass() : "[null]") + " for property " + getName();

		if(value != null)
		{
			if(value.getClass().isArray())
			{
				setArraySubProperties(value);
			}
			else if(isObject(value))
			{
				try
				{
					initializeObjectSubProperties(value);
				}
				catch(IllegalArgumentException | IllegalAccessException ex)
				{
					DebugConsole.logger.log(Level.SEVERE, null, ex);
				}
			}			
		}
		
		super.initializeValue(value);
	}

	//--------------------------------------------------------------------------	
	
	@Override
	public Property getParentProperty()
	{
		return null;
	}
	
	//--------------------------------------------------------------------------
	
	public ZoneProperty getTopProperty()
	{
		return getParentProperty() == null ? this : ((ZoneProperty)getParentProperty()).getTopProperty();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public Property[] getSubProperties()
	{
		return subProperties;
	}
	
	//--------------------------------------------------------------------------
	
	private static String getPropertyRecurisve(ZoneProperty property, String indent)
	{
		String ret = indent + "Name: " + property.name + " Type: " + property.type.getSimpleName() + " Value: " + property.getValue();
		
		if(property.subProperties != null)
		{
			for(ZoneProperty prop : property.subProperties)
				ret += "\n" + getPropertyRecurisve(prop, indent + "\t");	
		}
		
		return ret;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String toString()
	{
		if(getParentProperty() != null)
			return "Property: [" + name + "]\n" + getParentProperty().toString();
		
		return getPropertyRecurisve(this, "");
	}
	
	//--------------------------------------------------------------------------	
}
