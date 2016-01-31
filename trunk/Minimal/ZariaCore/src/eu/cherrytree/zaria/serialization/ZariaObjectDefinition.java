/****************************************/
/* ZariaObjectDefinition.java			*/
/* Created on: 16-Mar-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import eu.cherrytree.zaria.base.ApplicationRuntimeError;
import eu.cherrytree.zaria.serialization.annotations.DefinitionCategory;
import eu.cherrytree.zaria.serialization.annotations.DefinitionColor;
import eu.cherrytree.zaria.serialization.annotations.FieldDescription;
import eu.cherrytree.zaria.serialization.annotations.GreaterThan;
import eu.cherrytree.zaria.serialization.annotations.LessThan;
import eu.cherrytree.zaria.serialization.annotations.MaxFloat;
import eu.cherrytree.zaria.serialization.annotations.MaxInt;
import eu.cherrytree.zaria.serialization.annotations.MinFloat;
import eu.cherrytree.zaria.serialization.annotations.MinInt;
import eu.cherrytree.zaria.serialization.annotations.ScriptLink;
import eu.cherrytree.zaria.serialization.annotations.WeakLink;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.UUID;


@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
@JsonAutoDetect(	fieldVisibility=JsonAutoDetect.Visibility.ANY, 
					getterVisibility=JsonAutoDetect.Visibility.NONE, 
					isGetterVisibility=JsonAutoDetect.Visibility.NONE, 
					setterVisibility=JsonAutoDetect.Visibility.NONE, 
					creatorVisibility=JsonAutoDetect.Visibility.NONE	)

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
@DefinitionCategory("Other")
@DefinitionColor(ColorName.Silver)
public abstract class ZariaObjectDefinition
{
	//--------------------------------------------------------------------------
	
	@FieldDescription("The id of this definition.")
    private String id;
	
	@FieldDescription( required = true, visible = false )
	private UUID uuid;
	
	private transient String file;
	
	@FieldDescription( visible = false )
	private String metadata;
	
	//--------------------------------------------------------------------------
	
	private transient ZariaObjectDefinitionLibrary objectLibrary;
	
	//--------------------------------------------------------------------------

    public final String getID()
    {
        return id;
    }
				
	//--------------------------------------------------------------------------
	
	public final UUID getUUID()
	{
		return uuid;
	}
	
	//--------------------------------------------------------------------------
	
	public final String getFile()
	{
		return file;
	}
	
	//--------------------------------------------------------------------------
	
	final void setFile(String file)
	{
		this.file = file;
	}
			
	//--------------------------------------------------------------------------
	
	public final ZariaObjectDefinitionLibrary getObjectLibrary()
	{
		return objectLibrary;
	}
			
	//--------------------------------------------------------------------------
	
	public final DefinitionValidation validate()
	{		
		DefinitionValidation val = new DefinitionValidation(getClass().getSimpleName(), id);
		
		try
		{						
			validateFields(getClass(), val);
		}
		catch (Exception ex)
		{
			val.addError("", "", "Exception during validation: " + ex);
			return val;
		}	
		
		if(id != null)
			val.addError(!id.matches("[a-zA-Z0-9_-]*"), "id", id, "Object Defintion ID can contain only Latin alphanumeric charcacters, dashes and underscores.");
		
		onValidate(val);
		
		return val;
	}
	
	//--------------------------------------------------------------------------
	
	private void validateFields(Class cls, DefinitionValidation validation) throws IllegalArgumentException, IllegalAccessException
	{
		for(Field field : cls.getDeclaredFields())
		{
			field.setAccessible(true);
			
			ScriptLink scriptLink = field.getAnnotation(ScriptLink.class);
			
			if(scriptLink != null)
			{
				validation.addError(	field.getType() != String.class, field.getName(), 
										"", "ScriptLink is not if type String but " + field.getType().getCanonicalName() + ".");
			}
			
			if(field.getType() == Link.class || field.getType() == LinkArray.class)
				validation.addError(field.get(this) == null, field.getName(), "null", "Links cannot be null.");
																					
			FieldDescription fieldDescription = field.getAnnotation(FieldDescription.class);
			
			if(fieldDescription != null)
			{								
				if(field.getType() == Link.class)
				{
					if(field.get(this) != null)
					{
						Link link = (Link) field.get(this);
						
						validation.addError(	fieldDescription.required() == true && link.getUUID() == null, field.getName(), 
												"null", "Link is marked as required but is empty.");
					}
				}
				else if(field.getType() == LinkArray.class)
				{
					if(field.get(this) != null)
					{
						LinkArray linkArray = (LinkArray) field.get(this);
						
						validation.addError(	fieldDescription.required() == true && linkArray.isEmpty(), field.getName(), 
												"null", "LinkArray is marked as required but is empty.");
					}
				}
				else if(scriptLink != null)
				{
					if(field.getType() == String.class)
					{
						String link_target = (String) field.get(this);

						validation.addError(	fieldDescription.required() == true && (link_target == null || link_target.isEmpty()), field.getName(), 
												"null", "ScriptLink is marked as required but is empty.");
					}						
				}
				else
				{															
					validation.addError(	fieldDescription.required() == true && field.get(this) == null, field.getName(), 
											"null", "Field is marked as required but is null.");
				}
			}
			
			if(field.getType() == LinkArray.class)
			{
				if(field.get(this) != null)
				{
					LinkArray linkArray = (LinkArray) field.get(this);
					
					boolean has_spaces = false;
					
					for(UUID uid : linkArray.getUUIDs())
					{
						if(uid == null)
						{
							has_spaces = true;
							break;
						}
					}

					validation.addError(has_spaces, field.getName(), linkArray.toString(), "LinkArray has empty spaces.");
				}
			}
			
			WeakLink weakLink = field.getAnnotation(WeakLink.class);
			
			if(weakLink != null)
				validation.addError(field.getType() != UUID.class && field.getType() != UUID[].class, field.getName(), field.get(this).toString(), "WeakLinks must be represented as UUIDs.");

			MaxFloat maxFloat = field.getAnnotation(MaxFloat.class);
			
			if(maxFloat != null)
			{
				validation.addError(	field.getType() != float.class && field.getType() != Float.class, field.getName(), 
										field.get(this) != null ? field.get(this).toString() : "null", 
										"MaxFloat annotation used, but type is " + field.getType().getName());
								
				if((field.getType() == float.class || field.getType() == Float.class) && field.get(this) != null)
				{
					float val = Float.parseFloat(field.get(this).toString());					
					validation.addError(val > maxFloat.value(), field.getName(), field.get(this).toString(), "Value is greater than " + maxFloat.value());
				}			
			}
			
			MinFloat minFloat = field.getAnnotation(MinFloat.class);
			
			if(minFloat != null)
			{
				validation.addError(	field.getType() != float.class && field.getType() != Float.class, field.getName(), 
										field.get(this) != null ? field.get(this).toString() : "null", 
										"MinFloat annotation used, but type is " + field.getType().getName());
								
				if((field.getType() == float.class || field.getType() == Float.class) && field.get(this) != null)
				{
					float val = Float.parseFloat(field.get(this).toString());					
					validation.addError(val < minFloat.value(), field.getName(), field.get(this).toString(), "Value is less than " + minFloat.value());
				}			
			}
			
			LessThan lessThan = field.getAnnotation(LessThan.class);
			
			if(lessThan != null)
			{
				validation.addError(	field.getType() != float.class && field.getType() != Float.class, field.getName(), 
										field.get(this) != null ? field.get(this).toString() : "null", 
										"LessThan annotation used, but type is " + field.getType().getName() + " (should be float).");
								
				if((field.getType() == float.class || field.getType() == Float.class) && field.get(this) != null)
				{
					float val = Float.parseFloat(field.get(this).toString());					
					validation.addError(val >= lessThan.value(), field.getName(), field.get(this).toString(), "Value is not less than " + lessThan.value());
				}			
			}
			
			GreaterThan greaterThan = field.getAnnotation(GreaterThan.class);
									
			if(greaterThan != null)
			{
				validation.addError(	field.getType() != float.class && field.getType() != Float.class, field.getName(), 
										field.get(this) != null ? field.get(this).toString() : "null", 
										"GreaterThan annotation used, but type is " + field.getType().getName() + " (should be float).");
								
				if((field.getType() == float.class || field.getType() == Float.class) && field.get(this) != null)
				{
					float val = Float.parseFloat(field.get(this).toString());					
					validation.addError(val <= greaterThan.value(), field.getName(), field.get(this).toString(), "Value is not greater than " + greaterThan.value());
				}			
			}
			
			MaxInt maxInt = field.getAnnotation(MaxInt.class);
			
			if(maxInt != null)
			{
				validation.addError(	field.getType() != int.class && field.getType() != Integer.class, field.getName(), 
										field.get(this) != null ? field.get(this).toString() : "null", 
										"MaxInt annotation used, but type is " + field.getType().getName());
								
				if((field.getType() == int.class || field.getType() == Integer.class) && field.get(this) != null)
				{
					float val = Integer.parseInt(field.get(this).toString());					
					validation.addError(val > maxInt.value(), field.getName(), field.get(this).toString(), "Value is greater than " + maxInt.value());
				}			
			}
			
			MinInt minInt = field.getAnnotation(MinInt.class);
			
			if(minInt != null)
			{
				validation.addError(	field.getType() != int.class && field.getType() != Integer.class, field.getName(), 
										field.get(this) != null ? field.get(this).toString() : "null", 
										"MaxInt annotation used, but type is " + field.getType().getName());
								
				if((field.getType() == int.class || field.getType() == Integer.class) && field.get(this) != null)
				{
					float val = Integer.parseInt(field.get(this).toString());					
					validation.addError(val < minInt.value(), field.getName(), field.get(this).toString(), "Value is less than " + minInt.value());
				}			
			}
			
			if(Enum.class.isAssignableFrom(field.getType()))
				validation.addError(field.get(this) == null, field.getName(), "null", "Enum fields cannot be null.");
		}
				
		Class super_class = cls.getSuperclass();
		
		if(super_class != null && super_class != ZariaObjectDefinition.class)
			validateFields(super_class, validation);
	}
	
	//--------------------------------------------------------------------------
	
	private <T> void getAllLinks(Class<T> type, Class<? extends ZariaObjectDefinition> cls, ArrayList<T> links) throws IllegalArgumentException, IllegalAccessException
	{
		for(Field field : cls.getDeclaredFields())
		{							
			if(Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers()))
				continue;						
			
			if(field.getType() == type)
			{				
				field.setAccessible(true);
				links.add((T) field.get(this));
			}
		}		
		
		Class super_class = cls.getSuperclass();
		
		if(super_class != null && super_class != ZariaObjectDefinition.class)
			getAllLinks(type, super_class, links);
	}
	
	//--------------------------------------------------------------------------
	
	public final void preLoad(ZariaObjectDefinitionLibrary library)
	{		
		objectLibrary = library;
				 
		try
		{
			ArrayList<Link> links = new ArrayList<>();
			getAllLinks(Link.class, this.getClass(), links);
			
			for(Link link : links)
			{
				if(link != null)
					link.load(library);
			}
			
			ArrayList<LinkArray> link_arrays = new ArrayList<>();
			getAllLinks(LinkArray.class, this.getClass(), link_arrays);
			
			for(LinkArray link_array : link_arrays)
			{
				if(link_array != null)
					link_array.load(library);	
			}
		}
		catch (ClassCastException | IllegalArgumentException | IllegalAccessException | CantResolveLinkException ex)
		{
			ApplicationRuntimeError error = new ApplicationRuntimeError(ex);
			error.addInfo("Definition type", this.getClass().getCanonicalName());
			error.addInfo("ID", id);			
			
			throw error;
		}
		
		onPreLoad();
	}
	
	//--------------------------------------------------------------------------
	
	public abstract void onPreLoad();
	public abstract void onValidate(DefinitionValidation validation);
	
	//--------------------------------------------------------------------------
}
